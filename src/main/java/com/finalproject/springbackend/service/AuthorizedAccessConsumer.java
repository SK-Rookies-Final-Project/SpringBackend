// AuthorizedAccessConsumer.java
package com.finalproject.springbackend.service;

import com.finalproject.springbackend.controller.KafkaSecurityAuditLogController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Service
public class AuthorizedAccessConsumer {

    @KafkaListener(
            topics = "${KAFKA_TOPIC_AUTHORIZED_ACCESS}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onAuthorized(String message) {
//        System.out.println("authorized-access: "+ message);

        KafkaSecurityAuditLogController.sseEmittersLoginAccess.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("authorized-access")                 // 이벤트 이름
                        .data(message, MediaType.APPLICATION_JSON) // 메시지 그대로 전달
                );
            } catch (IOException e) {
                System.err.println();
            }
        });
    }
}
