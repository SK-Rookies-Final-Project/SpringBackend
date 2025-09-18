package kafka.producer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.ClusterAuthorizationException;
import org.apache.kafka.common.serialization.StringSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//인가 실패를 하지 못했을 때의 모든 경우를 토픽에 데이터 넣는 코드
//성공 못하거나 실패 했을 때
public class ProducerApplication {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    // 데이터 모델 클래스들
    @Data
    @Builder
    static class Certified2Time {
        String id;
        OffsetDateTime alertTimeKst;
        String alertType;
        String clientIp;
        String description;
        Long failureCount;
    }

    @Data
    @Builder
    static class CertifiedNotMove {
        String id;
        OffsetDateTime alertTimeKst;
        String alertType;
        String clientIp;
        String description;
        Long failureCount;
    }

    @Data
    @Builder
    static class ResourceLevelFalse {
        String id;
        String clientIp;
        OffsetDateTime eventTimeKst;
        Boolean granted;
        String methodName;
        String operation;
        String principal;
        OffsetDateTime processingTimeKst;
        String resourceName;
        String resourceType;
    }

    @Data
    @Builder
    static class SystemLevelFalse {
        String id;
        String clientIp;
        OffsetDateTime eventTimeKst;
        Boolean granted;
        String methodName;
        String operation;
        String principal;
        OffsetDateTime processingTimeKst;
        String resourceName;
        String resourceType;
    }

    // Kafka 토픽 이름들
    private static final String TOPIC_CERTIFIED_2TIME = "certified-2time";
    private static final String TOPIC_CERTIFIED_NOT_MOVE = "certified-notMove";
    private static final String TOPIC_RESOURCE_LEVEL_FALSE = "resource-level-false";
    private static final String TOPIC_SYSTEM_LEVEL_FALSE = "system-level-false";

    public static void main(String[] args) throws Exception {
        final String bootstrap = System.getenv().getOrDefault(
                "BOOTSTRAP",
                "192.168.0.121:29092,192.168.0.121:39092,192.168.0.121:49092"
        );

        System.out.println("Running Producer to send messages to security audit topics...");
        runProducerLoop(bootstrap);
    }

    private static void runAdminTask(String bootstrap, String topic) {
        Properties props = new Properties(); // AdminClient 설정을 담을 Properties 객체 생성
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap); // 브로커 주소
        props.put("security.protocol", "SASL_PLAINTEXT"); // SASL_PLAINTEXT 보안 프로토콜 사용
        props.put("sasl.mechanism", "SCRAM-SHA-512"); // SCRAM-SHA-512 인증 방식
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"dr\" password=\"dr-secret\";");
        // AdminClient 인증용 사용자(dr) 계정 정보

        try (Admin adminClient = Admin.create(props)) { // AdminClient 생성 (try-with-resources로 자동 close)
            ConfigResource topicResource = new ConfigResource(ConfigResource.Type.TOPIC, topic); // 토픽 리소스 지정
            ConfigEntry retentionEntry = new ConfigEntry("retention.ms", "60000"); // retention.ms=60000 (1분) 설정
            Config config = new Config(Collections.singleton(retentionEntry)); // 단일 설정으로 Config 객체 생성

            Map<ConfigResource, Config> configs = new HashMap<>(); // 리소스-설정 매핑
            configs.put(topicResource, config);

            AlterConfigsResult result = adminClient.alterConfigs(configs); // 설정 변경 요청

            result.all().get(); // 비동기 결과 완료 대기 (권한 없으면 예외 발생)

            System.out.printf("[SUCCESS] Successfully altered config for topic %s%n", topic); // 성공 로그
        } catch (Exception e) {
            // 예외 원인 추출
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof ClusterAuthorizationException || cause instanceof AuthorizationException) {
                // 권한 없음 예외 → DENIED 출력
                System.out.printf("[DENIED] User 'dr' is not authorized to alter configs for topic '%s'. Exception: %s%n",
                        topic, cause.getMessage());
            } else {
                // 그 외 예외 → ERROR 출력
                System.err.printf("[ERROR] An unexpected error occurred: %s%n", cause.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void runProducerLoop(String bootstrap) throws InterruptedException {
        final long intervalMs = Long.parseLong(System.getenv().getOrDefault("INTERVAL_MS", "1000"));
        System.out.printf("BOOTSTRAP=%s%n", bootstrap);

        try (KafkaProducer<String, String> producer = newProducer(bootstrap)) {
            while (true) {
                // 각 토픽에 대한 메시지 생성 및 전송
                sendCertified2TimeMessage(producer);
                sendCertifiedNotMoveMessage(producer);
                sendResourceLevelFalseMessage(producer);
                sendSystemLevelFalseMessage(producer);

                Thread.sleep(intervalMs);
            }
        }
    }

    private static void sendCertified2TimeMessage(KafkaProducer<String, String> producer) {
        ObjectNode data = objectMapper.createObjectNode()
            .put("id", UUID.randomUUID().toString())
            .put("alert_time_kst", OffsetDateTime.now(ZoneOffset.of("+09:00")).toString())
            .put("alert_type", "LOGIN_FAILURE")
            .put("client_ip", generateRandomIp())
            .put("description", "2회 연속 인증 실패")
            .put("failure_count", 2);

        sendMessage(producer, TOPIC_CERTIFIED_2TIME, data);
    }

    private static void sendCertifiedNotMoveMessage(KafkaProducer<String, String> producer) {
        ObjectNode data = objectMapper.createObjectNode()
            .put("id", UUID.randomUUID().toString())
            .put("alert_time_kst", OffsetDateTime.now(ZoneOffset.of("+09:00")).toString())
            .put("alert_type", "LOCATION_CHANGE")
            .put("client_ip", generateRandomIp())
            .put("description", "비정상적인 위치에서의 접근 시도")
            .put("failure_count", 1);

        sendMessage(producer, TOPIC_CERTIFIED_NOT_MOVE, data);
    }

    private static void sendResourceLevelFalseMessage(KafkaProducer<String, String> producer) {
        ObjectNode data = objectMapper.createObjectNode()
            .put("id", UUID.randomUUID().toString())
            .put("client_ip", generateRandomIp())
            .put("event_time_kst", OffsetDateTime.now(ZoneOffset.of("+09:00")).toString())
            .put("granted", false)
            .put("method_name", "getResource")
            .put("operation", "READ")
            .put("principal", "user123")
            .put("processing_time_kst", OffsetDateTime.now(ZoneOffset.of("+09:00")).toString())
            .put("resource_name", "/api/secure/data")
            .put("resource_type", "REST_API");

        sendMessage(producer, TOPIC_RESOURCE_LEVEL_FALSE, data);
    }

    private static void sendSystemLevelFalseMessage(KafkaProducer<String, String> producer) {
        ObjectNode data = objectMapper.createObjectNode()
            .put("id", UUID.randomUUID().toString())
            .put("client_ip", generateRandomIp())
            .put("event_time_kst", OffsetDateTime.now(ZoneOffset.of("+09:00")).toString())
            .put("granted", false)
            .put("method_name", "systemOperation")
            .put("operation", "ADMIN")
            .put("principal", "user123")
            .put("processing_time_kst", OffsetDateTime.now(ZoneOffset.of("+09:00")).toString())
            .put("resource_name", "system/config")
            .put("resource_type", "SYSTEM");

        sendMessage(producer, TOPIC_SYSTEM_LEVEL_FALSE, data);
    }

    private static void sendMessage(KafkaProducer<String, String> producer, String topic, ObjectNode data) {
        String key = UUID.randomUUID().toString();
        String value = data.toString();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

        try {
            RecordMetadata md = producer.send(record).get();
            System.out.printf("[OK    ] -> %s p=%d o=%d%n", topic, md.partition(), md.offset());
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof AuthorizationException) {
                System.out.printf("[DENIED] write to %s (no ACL)%n", topic);
            } else if (cause instanceof AuthenticationException ||
                    (cause.getMessage() != null && cause.getMessage().contains("Authentication failed"))) {
                System.out.printf("[AUTH-FAIL] authentication failed%n");
            } else {
                System.out.printf("[ERROR ] send failed: %s%n", cause.toString());
            }
        }
    }

    private static String generateRandomIp() {
        return String.format("%d.%d.%d.%d",
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256),
                ThreadLocalRandom.current().nextInt(256));
    }

    private static KafkaProducer<String, String> newProducer(String bootstrap) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        props.put("allow.auto.create.topics", false);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30_000);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "SCRAM-SHA-512");
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                        "username=\"admin\" password=\"admin-secret\";");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "security-audit-producer");
        return new KafkaProducer<>(props);
    }

}
