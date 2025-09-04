package com.finalproject.springbackend.service;

import com.finalproject.springbackend.controller.KafkaSecurityAuditLogController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class AuditLogConsumer {

    // 방법 1: 직접 property placeholder 사용 (가장 간단)
    @KafkaListener(
            topics = "${KAFKA_TOPIC_AUDIT_LOG}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
//        System.out.println("Consumed message: " + message);

        // 연결되어 있는 모든 클라이언트에게 메세지 전송
        KafkaSecurityAuditLogController.sseEmitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("log-event")
                        .data(message, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                System.err.println("Error sending message to client: " + id);
                // 에러 발생한 emitter 제거
                KafkaSecurityAuditLogController.sseEmitters.remove(id);
            }
        });
    }

}