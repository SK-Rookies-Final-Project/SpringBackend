package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.dto.LogsRequestDTO;
import com.finalproject.springbackend.dto.LogsResponseDTO;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/kafka")
public class KafkaSecurityAuditLogController {

    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Value("${CLUSTER_ID}")
    private String clusterId;

    // 1. 실시간 스트리밍 API: 정제 전 데이터
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getStreamLogs(
            @RequestParam(required = false) String topics, // 필터링 할 토픽 목록(쉼표로 구분)
            @RequestParam(required = false) String users,  // 필터링 할 사용자 목록(쉼표로 구분)
            @RequestParam(required = false) String status  // 권한 상태 필터 (granted, denied)
    ) {

        String clientId=clusterId;

        SseEmitter emitter = new SseEmitter(0L);

        // 1. 새로운 클라이언트의 Emitter를 Map에 추가
        sseEmitters.put(clientId, emitter);

        // 2. 연결이 끊어지거나 타임아웃되면 Map에서 제거
        emitter.onCompletion(() -> sseEmitters.remove(clientId));
        emitter.onTimeout(() -> sseEmitters.remove(clientId));

        return emitter;
    }

//    // 2. 감사 로그 조회 API
//    @GetMapping("/logs")
//    public LogsResponseDTO getlogs(@ModelAttribute LogsRequestDTO logs){
//
//    }

    // 3. 개별 로그 조회 API
//    @GetMapping("/logs/{id}")
//    public String getLogById(@PathVariable String id){
//
//    }

    // 4. 사용자 목록 조회 API
//    @GetMapping("/users")
//    public String getUsers(){
//
//    }

    // 5. 토픽 목록 조회 API
//    @GetMapping("/topics")
//    public String getTopics(){
//
//    }

    // 6. 통계 조회 API
//    @GetMapping("/stats")
//    public String getStats(){
//
//    }

    /**추가한거*/
//    // 1. 로그인 성공
//    // "authorized-access"에서 consume
//    @GetMapping("/auth")
//    public String accessTrue(){
//
//    }

//    // 2. 로그인 비인가 권한 없는 행동 할 때
//    // "unauthorized-access"에서 consume
//    @GetMapping("/unauth")
//    public String accessFalse(){
//
//    }

//    // 3. 없는 계정으로 로그인 시도
//    // "access-failed"에서 consume
//    @GetMapping("/auth_failed")
//    public String authFailed(){
//
//    }





}
