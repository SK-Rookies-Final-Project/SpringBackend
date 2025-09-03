package com.finalproject.springbackend.service;

import com.finalproject.springbackend.controller.KafkaSecurityAuditLogController;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;

@Service
public class AuditLogConsumer {

    @Value("${KAFKA_TOPIC_UNAUTHORIZED}")
    private String topics;

    public String[] getTopics(){
        String raw = topics.trim();
        //앞뒤 따옴표 제거
        if((raw.startsWith("'") && raw.endsWith("'")) ||
                (raw.startsWith("\"")) && raw.endsWith("\"")){
            raw = raw.substring(1, raw.length() -1);
        }
        //여러 개 받을 수 있어서 배열로 반환
        return Arrays.stream(raw.split(","))
                // 공백 제거
                .map(String::trim)  //String::trim==s -> s.trim()
                //빈 문자열 제거
                .filter(s -> !s.isEmpty())
                //배열 생성
                .toArray(String[]::new); //String[]::new == size -> new String[size]
    }

    @KafkaListener(
            topics = "#{__listener.getTopics()}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        System.out.println("Consumed message: " + message);

        // 연결되어 있는 모든 클라이언트에게 메세지 전송
        KafkaSecurityAuditLogController.sseEmitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("log-event")//로그 이벤트 명("log-event")이 찍힘
                        .data(message, MediaType.APPLICATION_JSON)); // Kafka에서 받은 메시지를 그대로 전달
            } catch (IOException e) {
                System.err.println("Error sending message to client: " + id);
            }
        });
    }
}