package com.finalproject.springbackend.service;

import com.finalproject.springbackend.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Service
public class KafkaAuthenticationService {

    @Value("${OHIO_KAFKA_BOOTSTRAP_SERVERS}")
    private String ohioBootstrapServers;

    @Value("${CONSUMER_GROUP_ID}")
    private String consumerGroupId;

    @Value("${KAFKA_SECURITY_PROTOCOL:SASL_PLAINTEXT}")
    private String securityProtocol;

    @Value("${KAFKA_SASL_MECHANISM:SCRAM-SHA-512}")
    private String saslMechanism;

    @Value("${app.topic.candidates:}")
    private String topicCandidatesCsv;

    private static final int PROBE_MAX_ATTEMPTS = 3;
    private static final long PROBE_INITIAL_BACKOFF_MS = 250L;
    private static final Duration PROBE_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(2);

    public Set<String> listAccessibleTopics(String username, String password) {
        Consumer<String, String> consumer = null;
        try {
            Properties props = createKafkaProperties(username, password);
            log.debug("Kafka security.protocol={}, sasl.mechanism={}", securityProtocol, saslMechanism);
            consumer = new KafkaConsumer<>(props);

            var candidates = parseCandidates();
            if (candidates.isEmpty()) {
                log.debug("No candidates configured, using listTopics path");
                try {
                    var topics = consumer.listTopics(Duration.ofSeconds(15));
                    return topics.keySet().stream()
                            .filter(t -> t != null && !t.startsWith("__"))
                            .collect(Collectors.toSet());
                } catch (Exception e) {
                    log.warn("listTopics failed: {}", e.getMessage());
                    return Set.of();
                }
            } else {
                log.debug("Using consume-probe for {} candidates", candidates.size());
                return candidates.stream()
                        .map(this::stripQuotes)
                        .filter(t -> t != null && !t.isBlank())
                        .filter(t -> probeConsumeWithRetry(username, password, t))
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            log.error("SCRAM auth/topic discovery failed: {}", e.getMessage());
            throw e;
        } finally {
            if (consumer != null) {
                try { consumer.close(); } catch (Exception ignore) { }
            }
        }
    }

    public String[] getCandidateTopics() {
        return parseCandidates().stream()
                .map(this::stripQuotes)
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);
    }

    private boolean probeConsumeWithRetry(String username, String password, String topic) {
        long backoff = PROBE_INITIAL_BACKOFF_MS;
        for (int attempt = 1; attempt <= PROBE_MAX_ATTEMPTS; attempt++) {
            if (canConsumeTopic(username, password, topic)) {
                log.debug("consume-probe success: topic='{}' attempt {}", topic, attempt);
                return true;
            }
            log.debug("consume-probe retry: topic='{}' attempt {} backoff={}ms", topic, attempt, backoff);
            try { Thread.sleep(backoff); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            backoff *= 2;
        }
        log.debug("consume-probe failed: topic='{}'", topic);
        return false;
    }

    private boolean canConsumeTopic(String username, String password, String topic) {
        Consumer<String, String> c = null;
        try {
            Properties props = createKafkaProperties(username, password);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId + "-probe-" + username + "-" + Math.abs(topic.hashCode()));
            c = new KafkaConsumer<>(props);
            c.subscribe(Collections.singletonList(topic));
            ConsumerRecords<String, String> records = c.poll(POLL_TIMEOUT);
            return true;
        } catch (AuthorizationException | UnknownTopicOrPartitionException auth) {
            log.debug("consume-probe auth/topic error: topic='{}' msg='{}'", topic, auth.getMessage());
            return false;
        } catch (Exception e) {
            log.debug("consume-probe exception: topic='{}' msg='{}'", topic, e.getMessage());
            return false;
        } finally {
            if (c != null) {
                try { c.unsubscribe(); } catch (Exception ignore) {}
                try { c.close(); } catch (Exception ignore) {}
            }
        }
    }

    private boolean hasAccessToTopic(Consumer<String, String> consumer, String topic) {
        try {
            List<PartitionInfo> partitions = consumer.partitionsFor(topic, PROBE_TIMEOUT);
            return partitions != null && !partitions.isEmpty();
        } catch (Exception e) {
            log.debug("partitionsFor exception: topic='{}' msg='{}'", topic, e.getMessage());
            return false;
        }
    }

    private java.util.List<String> parseCandidates() {
        if (topicCandidatesCsv == null) return java.util.List.of();
        var trimmed = topicCandidatesCsv.trim();
        if (trimmed.isEmpty()) return java.util.List.of();
        return Arrays.stream(trimmed.split(","))
                .map(String::trim)
                .map(this::stripQuotes)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    private String stripQuotes(String s) {
        if (s == null || s.length() < 2) return s == null ? "" : s;
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    public boolean authenticateWithKafka(String username, String password) {
        try {
            Set<String> topics = listAccessibleTopics(username, password);
            return !topics.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public Consumer<String, String> createAuthenticatedConsumer(UserInfo userInfo) {
        Properties props = createKafkaProperties(userInfo.getUsername(), userInfo.getPassword());
        return new KafkaConsumer<>(props);
    }

    private Properties createKafkaProperties(String username, String password) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ohioBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId + "-" + username);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 45000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 20000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 30000);
        props.put("security.protocol", securityProtocol);
        props.put("sasl.mechanism", saslMechanism);
        String jaasConfig = String.format(
            "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";",
            username, password
        );
        props.put("sasl.jaas.config", jaasConfig);
        return props;
    }
}
