//package kafka.producer;
//
//import org.apache.kafka.clients.admin.*;
//import org.apache.kafka.clients.producer.*;
//import org.apache.kafka.common.config.ConfigResource;
//import org.apache.kafka.common.errors.AuthenticationException;
//import org.apache.kafka.common.errors.AuthorizationException;
//import org.apache.kafka.common.errors.ClusterAuthorizationException;
//import org.apache.kafka.common.serialization.StringSerializer;
//
//import lombok.AllArgsConstructor;
//
//import java.util.*;
//import java.util.concurrent.ThreadLocalRandom;
//
////인가 실패를 하지 못했을 때의 모든 경우를 토픽에 데이터 넣는 코드
////성공 못하거나 실패 했을 때
//public class ProducerApplication {
//
//    //User 클래스
//    @AllArgsConstructor
//    static class User {
//        final String name;
//        final String password;
//        final boolean expectWriteAllowed;
//
//
//    }
//
//    //User 객체 하나하나 만들어서 List에 저장
//    static final List<User> USERS = List.of(
//            new User("admin", "admin-secret", true),
//            new User("dw", "dw-secret", true),
//            new User("ro", "ro-secret", false),
//            new User("ua", "ua-secret", false),
//            new User("dm", "dm-secret", false),
//            new User("dr", "dr-secret", false)
//    );
//
//    public static void main(String[] args) throws Exception {
//           // 환경 변수에서 BOOTSTRAP 값 가져오기 (없으면 기본값 브로커 3개 주소 사용)
//           final String bootstrap = System.getenv().getOrDefault(
//                   "BOOTSTRAP",
//                   "15.164.187.115:9092,43.200.197.192:9092,13.209.235.184:9092"
//           );
//           // 환경 변수에서 TOPIC 값 가져오기 (없으면 audit-topic 사용)
//           final String topic = System.getenv().getOrDefault("TOPIC", "audit-topic");
//
//           // ./java -jar springbackend-0.0.1-SNAPSHOT-plain.jar alter-topic
//           // 실행 인자에 "alter-topic"이 있으면 관리자(Admin) 모드 실행
//           if (args.length > 0 && "alter-topic".equalsIgnoreCase(args[0])) {
//               System.out.println("Running in Admin Task mode: Attempting to alter topic config...");
//               runAdminTask(bootstrap, topic); // Admin 작업 수행
//           } else {
//               // 인자가 없으면 Producer 모드 실행
//               System.out.println("Running in Producer mode: Sending messages...");
//               runProducerLoop(bootstrap, topic); // 메시지 전송 루프 실행
//           }
//       }
//
//       private static void runAdminTask(String bootstrap, String topic) {
//           Properties props = new Properties(); // AdminClient 설정을 담을 Properties 객체 생성
//           props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap); // 브로커 주소
//           props.put("security.protocol", "SASL_PLAINTEXT"); // SASL_PLAINTEXT 보안 프로토콜 사용
//           props.put("sasl.mechanism", "SCRAM-SHA-512"); // SCRAM-SHA-512 인증 방식
//           props.put("sasl.jaas.config",
//                   "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"dr\" password=\"dr-secret\";");
//           // AdminClient 인증용 사용자(dr) 계정 정보
//
//           try (Admin adminClient = Admin.create(props)) { // AdminClient 생성 (try-with-resources로 자동 close)
//               ConfigResource topicResource = new ConfigResource(ConfigResource.Type.TOPIC, topic); // 토픽 리소스 지정
//               ConfigEntry retentionEntry = new ConfigEntry("retention.ms", "60000"); // retention.ms=60000 (1분) 설정
//               Config config = new Config(Collections.singleton(retentionEntry)); // 단일 설정으로 Config 객체 생성
//
//               Map<ConfigResource, Config> configs = new HashMap<>(); // 리소스-설정 매핑
//               configs.put(topicResource, config);
//
//               AlterConfigsResult result = adminClient.alterConfigs(configs); // 설정 변경 요청
//
//               result.all().get(); // 비동기 결과 완료 대기 (권한 없으면 예외 발생)
//
//               System.out.printf("[SUCCESS] Successfully altered config for topic %s%n", topic); // 성공 로그
//           } catch (Exception e) {
//               // 예외 원인 추출
//               Throwable cause = e.getCause() != null ? e.getCause() : e;
//               if (cause instanceof ClusterAuthorizationException || cause instanceof AuthorizationException) {
//                   // 권한 없음 예외 → DENIED 출력
//                   System.out.printf("[DENIED] User 'dr' is not authorized to alter configs for topic '%s'. Exception: %s%n",
//                           topic, cause.getMessage());
//               } else {
//                   // 그 외 예외 → ERROR 출력
//                   System.err.printf("[ERROR] An unexpected error occurred: %s%n", cause.getMessage());
//                   e.printStackTrace();
//               }
//           }
//       }
//
//       private static void runProducerLoop(String bootstrap, String topic) throws InterruptedException {
//           // 메시지 전송 간격 (기본 1000ms = 1초)
//           final long intervalMs = Long.parseLong(System.getenv().getOrDefault("INTERVAL_MS", "1000"));
//           long loop = 0; // 반복 횟수 카운터
//           System.out.printf("BOOTSTRAP=%s, TOPIC=%s%n", bootstrap, topic);
//
//           Map<String, KafkaProducer<String, String>> producers = new HashMap<>(); // 사용자별 Producer 캐시
//
//           try {
//               while (true) { // 무한 루프 시작
//                   User u = pickRandom(USERS); // 사용자 무작위 선택
//                   KafkaProducer<String, String> p =
//                           producers.computeIfAbsent(u.name, k -> newProducer(bootstrap, u));
//                   // 선택한 사용자로 Producer 없으면 새로 생성
//
//                   String key = "user-" + u.name; // 메시지 키 = user-username
//                   String value = sampleJson(loop, u.name); // 메시지 값 = JSON 문자열
//                   ProducerRecord<String, String> rec = new ProducerRecord<>(topic, key, value); // ProducerRecord 생성
//
//                   try {
//                       RecordMetadata md = p.send(rec).get(); // 메시지 전송 후 결과 동기 대기
//                       System.out.printf("[OK    ] user=%s -> %s p=%d o=%d%n",
//                               u.name, topic, md.partition(), md.offset()); // 성공 로그
//                   } catch (Exception sendEx) {
//                       // 예외 원인 추출
//                       Throwable cause = sendEx.getCause() != null ? sendEx.getCause() : sendEx;
//
//                       if (cause instanceof AuthorizationException) {
//                           // 권한 없음
//                           System.out.printf("[DENIED] user=%s write to %s  (no ACL)%n", u.name, topic);
//                       } else if (cause instanceof AuthenticationException ||
//                               (cause.getMessage() != null && cause.getMessage().contains("Authentication failed"))) {
//                           // 인증 실패
//                           System.out.printf("[AUTH-FAIL] user=%s authentication failed%n", u.name);
//                       } else {
//                           // 기타 예외
//                           System.out.printf("[ERROR ] user=%s send failed: %s%n", u.name, cause.toString());
//                       }
//                   }
//                   loop++; // 루프 카운트 증가
//                   Thread.sleep(intervalMs); // intervalMs 밀리초 동안 대기
//               }
//           } finally {
//               producers.values().forEach(KafkaProducer::close); // 종료 시 모든 Producer 닫기
//           }
//       }
//
//       // 리스트에서 랜덤 요소 하나 선택
//       static <T> T pickRandom(List<T> list) {
//           return list.get(ThreadLocalRandom.current().nextInt(list.size()));
//       }
//
//       // 새로운 KafkaProducer 생성
//       static KafkaProducer<String, String> newProducer(String bootstrap, User u) {
//           Properties props = new Properties(); // Producer 설정 객체
//           props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap); // 브로커 주소
//           props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // 키 직렬화
//           props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // 값 직렬화
//           props.put(ProducerConfig.ACKS_CONFIG, "all"); // 모든 복제본 ack 요구
//           props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false); // 멱등성 비활성화
//           props.put("allow.auto.create.topics", false); // 자동 토픽 생성 금지
//           props.put(ProducerConfig.LINGER_MS_CONFIG, 5); // 배치 지연시간 5ms
//           props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384); // 배치 크기 16KB
//           props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // snappy 압축 사용
//           props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30_000); // 요청 타임아웃 30초
//           props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000); // 전달 타임아웃 120초
//           props.put("security.protocol", "SASL_PLAINTEXT"); // 보안 프로토콜
//           props.put("sasl.mechanism", "SCRAM-SHA-512"); // SCRAM-SHA-512 인증 방식
//           String password = u.password; // 비밀번호 가져오기
//           props.put("sasl.jaas.config",
//                   "org.apache.kafka.common.security.scram.ScramLoginModule required " +
//                           "username=\"" + u.name + "\" password=\"" + password + "\";"); // 사용자별 JAAS config
//           props.put(ProducerConfig.CLIENT_ID_CONFIG, "demo-audit-producer-" + u.name); // 클라이언트 ID 지정
//           return new KafkaProducer<>(props); // Producer 생성 후 반환
//       }
//
//       // 전송할 JSON 문자열 생성
//       static String sampleJson(long loop, String who) {
//           long now = System.currentTimeMillis(); // 현재 시간(ms)
//           return "{"
//                   + "\"user\":\"" + who + "\"," // 사용자 이름
//                   + "\"ingestTime\":" + now + "," // 전송 시간
//                   + "\"loop\":" + loop + "," // 루프 카운터
//                   + "\"payload\":\"demo\"" // 페이로드 (demo)
//                   + "}";
//       }
//   }