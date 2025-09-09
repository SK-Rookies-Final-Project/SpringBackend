package com.finalproject.springbackend.service;

import com.finalproject.springbackend.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Properties;

@Slf4j
@Service
public class KafkaAuthenticationService {

    @Value("${OHIO_KAFKA_BOOTSTRAP_SERVERS}")
    private String ohioBootstrapServers;

    @Value("${CONSUMER_GROUP_ID}")
    private String consumerGroupId;

    /**
     * 실제 Kafka 클러스터에 SCRAM 인증을 시도합니다.
     * 이 메서드는 로그인 시도하는 사용자의 실제 계정 정보로 Kafka 브로커에 인증을 시도합니다.
     * @param username Kafka 사용자명 (실제 SCRAM 계정)
     * @param password Kafka 비밀번호 (실제 SCRAM 비밀번호)
     * @return 인증 성공 여부
     */
    public boolean authenticateWithKafka(String username, String password) {
        Consumer<String, String> consumer = null;
        try {
            log.info("실제 Kafka SCRAM 인증 시도: {} (서버: {})", username, ohioBootstrapServers);
            
            // 실제 사용자 계정으로 Kafka 속성 생성
            Properties props = createKafkaProperties(username, password);
            log.info("Kafka SCRAM 설정: username={}, security.protocol={}, sasl.mechanism={}", 
                username, props.getProperty("security.protocol"), props.getProperty("sasl.mechanism"));
            
            // 실제 사용자 계정으로 Consumer 생성
            consumer = new KafkaConsumer<>(props);
            
            // 1. 실제 Kafka 연결 테스트 - 토픽 리스트 조회로 인증 확인
            log.info("토픽 리스트 조회로 SCRAM 인증 확인 중...");
            var topics = consumer.listTopics(Duration.ofMillis(15000));
            log.info("사용자 {}가 접근 가능한 토픽 수: {}", username, topics.size());
            
            // 토픽 목록 로깅
            if (log.isInfoEnabled()) {
                topics.forEach((topicName, partitionInfo) -> 
                    log.info("접근 가능한 토픽: {} (파티션 수: {})", topicName, partitionInfo.size()));
            }
            
            // 2. 추가 검증: 실제 토픽에 구독 시도
            log.info("실제 토픽 구독 테스트 중...");
            try {
                // 시스템 토픽 대신 일반 토픽 사용 (권한이 있는 경우)
                consumer.subscribe(java.util.Arrays.asList("__consumer_offsets"));
                var records = consumer.poll(Duration.ofMillis(3000));
                log.info("폴링 결과: {} 개의 레코드", records.count());
            } catch (Exception pollException) {
                log.warn("토픽 구독 테스트 중 오류 (인증은 성공할 수 있음): {}", pollException.getMessage());
            }
            
            log.info("✅ Kafka SCRAM 인증 성공: {} (토픽 수: {})", username, topics.size());
            return true;
            
        } catch (Exception e) {
            log.error("❌ Kafka SCRAM 인증 실패: {} - {}", username, e.getMessage());
            log.error("인증 실패 상세 정보:", e);
            
            // 특정 오류 타입에 대한 추가 정보 제공
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Authentication failed") || e.getMessage().contains("SASL authentication failed")) {
                    log.error("🔐 인증 실패: 사용자명 '{}' 또는 비밀번호가 Kafka 브로커의 SCRAM 계정과 일치하지 않습니다.", username);
                } else if (e.getMessage().contains("Connection refused")) {
                    log.error("🌐 연결 실패: Kafka 서버 '{}'에 연결할 수 없습니다.", ohioBootstrapServers);
                } else if (e.getMessage().contains("SASL")) {
                    log.error("🔧 SASL 인증 오류: SCRAM 설정을 확인해주세요.");
                } else if (e.getMessage().contains("TimeoutException")) {
                    log.error("⏰ 타임아웃: Kafka 서버 응답 시간이 초과되었습니다.");
                }
            }
            
            return false;
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
                } catch (Exception e) {
                    log.warn("Consumer 종료 중 오류: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 사용자 정보로 Kafka Consumer를 생성합니다.
     * @param userInfo 사용자 정보
     * @return Kafka Consumer
     */
    public Consumer<String, String> createAuthenticatedConsumer(UserInfo userInfo) {
        Properties props = createKafkaProperties(userInfo.getUsername(), userInfo.getPassword());
        return new KafkaConsumer<>(props);
    }

    /**
     * 실제 사용자 계정으로 Kafka Consumer 속성을 생성합니다.
     * 이 메서드는 로그인 시도하는 사용자의 실제 SCRAM 계정 정보를 사용합니다.
     * @param username 실제 SCRAM 사용자명
     * @param password 실제 SCRAM 비밀번호
     * @return Kafka Consumer 속성
     */
    private Properties createKafkaProperties(String username, String password) {
        Properties props = new Properties();
        
        // 기본 설정
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ohioBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId + "-" + username);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        
        // 연결 및 인증 타임아웃 설정 (더 관대하게)
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 15000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        
        // 재시도 설정
        props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 10000);

        // SASL SCRAM SHA-512 설정 - 실제 사용자 계정 사용
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        
        // 실제 로그인 시도하는 사용자의 SCRAM 계정 정보로 JAAS 설정
        String jaasConfig = String.format(
            "org.apache.kafka.common.security.scram.ScramLoginModule required " +
            "username=\"%s\" " +
            "password=\"%s\";",
            username,
            password
        );
        props.put("sasl.jaas.config", jaasConfig);
        
        log.debug("생성된 JAAS 설정: {}", jaasConfig.replace(password, "***"));

        return props;
    }
}
