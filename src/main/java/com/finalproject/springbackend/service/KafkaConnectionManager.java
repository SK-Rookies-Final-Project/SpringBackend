package com.finalproject.springbackend.service;

import com.finalproject.springbackend.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConnectionManager {

    private final KafkaAuthenticationService kafkaAuthenticationService;

    // 사용자별 Kafka Consumer 저장소
    private final Map<String, Consumer<String, String>> userConsumers = new ConcurrentHashMap<>();

    public Consumer<String, String> getOrCreateConsumer(String username, UserInfo userInfo) {
        return userConsumers.computeIfAbsent(username, key -> {
            log.info("새로운 Kafka Consumer 생성: {} (실제 Kafka SCRAM 인증)", username);
            return kafkaAuthenticationService.createAuthenticatedConsumer(userInfo);
        });
    }

    public void closeConsumer(String username) {
        Consumer<String, String> consumer = userConsumers.remove(username);
        if (consumer != null) {
            log.info("Kafka Consumer 종료: {}", username);
            consumer.close();
        }
    }

    public void closeAllConsumers() {
        log.info("모든 Kafka Consumer 종료");
        userConsumers.forEach((username, consumer) -> {
            try {
                consumer.close();
            } catch (Exception e) {
                log.error("Consumer 종료 중 오류 발생: {}", username, e);
            }
        });
        userConsumers.clear();
    }

    public boolean hasConsumer(String username) {
        return userConsumers.containsKey(username);
    }

    public Map<String, Consumer<String, String>> getAllConsumers() {
        return new HashMap<>(userConsumers);
    }
}
