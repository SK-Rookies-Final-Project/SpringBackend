package com.finalproject.springbackend.service;

import com.finalproject.springbackend.controller.KafkaSecurityAuditLogController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
public class AuthFailedConsumer {

    @KafkaListener(
            topics = "${KAFKA_TOPIC_AUTH_FAILED}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onAuthorized(String message) {
        System.out.println("access-failed: "+ message);

        KafkaSecurityAuditLogController.sseEmittersAuthFailed.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("access-failed")                 // 이벤트 이름
                        .data(message, MediaType.APPLICATION_JSON) // 메시지 그대로 전달
                );
            } catch (IOException e) {
                System.err.println("SSE 전송 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
