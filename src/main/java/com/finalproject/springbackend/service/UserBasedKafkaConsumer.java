package com.finalproject.springbackend.service;

import com.finalproject.springbackend.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBasedKafkaConsumer {

    private final KafkaConnectionManager kafkaConnectionManager;
    private final AuthService authService;
    private final SseService sseService;

    // 사용자별 활성 스트림 저장소
    private final Map<String, Map<String, SseEmitter>> userStreams = new ConcurrentHashMap<>();

    public SseEmitter createUserStream(String username, String streamType) {
        UserInfo userInfo = authService.getUserInfo(username);
        if (userInfo == null) {
            log.error("사용자 정보를 찾을 수 없습니다: {}", username);
            return null;
        }

        // 사용자별 스트림 맵 생성
        userStreams.computeIfAbsent(username, k -> new ConcurrentHashMap<>());

        // SSE Emitter 생성
        String clientId = username + "-" + streamType + "-" + System.currentTimeMillis();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // 스트림 저장
        userStreams.get(username).put(clientId, emitter);

        // 연결 종료 시 정리
        emitter.onCompletion(() -> {
            userStreams.get(username).remove(clientId);
            if (userStreams.get(username).isEmpty()) {
                userStreams.remove(username);
            }
        });
        emitter.onTimeout(() -> {
            userStreams.get(username).remove(clientId);
            if (userStreams.get(username).isEmpty()) {
                userStreams.remove(username);
            }
        });
        emitter.onError((throwable) -> {
            userStreams.get(username).remove(clientId);
            if (userStreams.get(username).isEmpty()) {
                userStreams.remove(username);
            }
        });

        // 비동기로 Kafka Consumer 시작
        startKafkaConsumer(username, userInfo, streamType);

        return emitter;
    }

    private void startKafkaConsumer(String username, UserInfo userInfo, String streamType) {
        CompletableFuture.runAsync(() -> {
            try {
                Consumer<String, String> consumer = kafkaConnectionManager.getOrCreateConsumer(username, userInfo);
                
                // 사용자 권한에 따른 토픽 필터링
                List<String> allowedTopics = Arrays.asList(userInfo.getAllowedTopics());
                List<String> topicsToSubscribe = filterTopicsByStreamType(allowedTopics, streamType);
                
                if (topicsToSubscribe.isEmpty()) {
                    log.warn("사용자 {}가 {} 스트림에 접근할 권한이 없습니다", username, streamType);
                    return;
                }

                consumer.subscribe(topicsToSubscribe);
                log.info("사용자 {}가 토픽 {} 구독 시작", username, topicsToSubscribe);

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    
                    for (ConsumerRecord<String, String> record : records) {
                        String message = record.value();
                        String topic = record.topic();
                        
                        // 사용자의 해당 스트림에 메시지 전송
                        sendMessageToUserStream(username, streamType, message, topic);
                    }
                }
            } catch (Exception e) {
                log.error("Kafka Consumer 실행 중 오류 발생: {}", username, e);
            }
        });
    }

    private List<String> filterTopicsByStreamType(List<String> allowedTopics, String streamType) {
        switch (streamType) {
            case "stream":
                return allowedTopics.stream()
                    .filter(topic -> topic.contains("audit-log") || topic.contains("confluent"))
                    .toList();
            case "auth":
                return allowedTopics.stream()
                    .filter(topic -> topic.contains("authorized-access"))
                    .toList();
            case "unauth":
                return allowedTopics.stream()
                    .filter(topic -> topic.contains("unauthorized-access"))
                    .toList();
            case "auth_failed":
                return allowedTopics.stream()
                    .filter(topic -> topic.contains("access-failed"))
                    .toList();
            default:
                return allowedTopics;
        }
    }

    private void sendMessageToUserStream(String username, String streamType, String message, String topic) {
        Map<String, SseEmitter> userStreamMap = userStreams.get(username);
        if (userStreamMap == null) {
            return;
        }

        userStreamMap.forEach((clientId, emitter) -> {
            if (clientId.contains(streamType)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(streamType)
                            .data(message));
                } catch (IOException e) {
                    log.error("SSE 전송 중 오류 발생: {}", clientId, e);
                    userStreamMap.remove(clientId);
                }
            }
        });
    }

    public void stopUserStreams(String username) {
        Map<String, SseEmitter> userStreamMap = userStreams.remove(username);
        if (userStreamMap != null) {
            userStreamMap.forEach((clientId, emitter) -> {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.error("스트림 종료 중 오류 발생: {}", clientId, e);
                }
            });
        }
        kafkaConnectionManager.closeConsumer(username);
    }

    public boolean hasActiveStreams(String username) {
        return userStreams.containsKey(username);
    }
}
