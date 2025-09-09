package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaSecurityAuditLogController {

    private final SseService sseService;
    
    @Value("${CLUSTER_ID}")
    private String clusterId;

    // 1. 실시간 스트리밍 API: 정제 전 데이터
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getStreamLogs(
            @RequestParam(required = false) String topics,
            @RequestParam(required = false) String users,
            @RequestParam(required = false) String status
    ) {
        return sseService.createRawLogStream();
    }

    // 2. 인증된 접근 로그 스트리밍
    @GetMapping(value = "/auth", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getAuthorizedAccess() {
        return sseService.createAuthorizedStream();
    }

    // 3. 인증되지 않은 접근 로그 스트리밍
    @GetMapping(value = "/unauth", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getUnauthorizedAccess() {
        return sseService.createUnauthorizedStream();
    }

    // 4. 인증 실패 로그 스트리밍
    @GetMapping(value = "/auth_failed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getAuthFailed() {
        return sseService.createAuthFailedStream();
    }
    
    //우기님이 추가해주시면 uri 변경 및 메서드명 변경하기
    @GetMapping(value = "/one", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getOne(){
        return sseService.createOneStream();
    }
    @GetMapping(value = "/two", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getTwo(){
        return sseService.createTwoStream();
    }
    @GetMapping(value = "/three", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getThree(){
        return sseService.createThreeStream();
    }
    @GetMapping(value = "/four", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getFour(){
        return sseService.createFourStream();
    }
    @GetMapping(value = "/five", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getFive(){
        return sseService.createFiveStream();
    }
    @GetMapping(value = "/six", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getSix(){
        return sseService.createSixStream();
    }

}