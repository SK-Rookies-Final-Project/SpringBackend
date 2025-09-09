package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.annotation.RequirePermission;
import com.finalproject.springbackend.dto.Permission;
import com.finalproject.springbackend.service.SseService;
import com.finalproject.springbackend.service.UserBasedKafkaConsumer;
import com.finalproject.springbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaSecurityAuditLogController {

    private final SseService sseService;
    private final UserBasedKafkaConsumer userBasedKafkaConsumer;
    private final AuthService authService;
    
    @Value("${CLUSTER_ID}")
    private String clusterId;

    // 1. 실시간 스트리밍 API: 정제 전 데이터 (권한 기반)
    @RequirePermission({Permission.STREAM_RAW_LOGS})
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getStreamLogs(
            @RequestParam(required = false) String topics,
            @RequestParam(required = false) String users,
            @RequestParam(required = false) String status,
            Authentication authentication
    ) {
        String username = authentication.getName();
        log.info("사용자 {}가 stream API 호출", username);
        
        SseEmitter emitter = userBasedKafkaConsumer.createUserStream(username, "stream");
        if (emitter == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(emitter);
    }

    // 2. 인증된 접근 로그 스트리밍 (권한 기반)
    @RequirePermission({Permission.STREAM_AUTH_LOGS})
    @GetMapping(value = "/auth", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getAuthorizedAccess(Authentication authentication) {
        String username = authentication.getName();
        log.info("사용자 {}가 auth API 호출", username);
        
        SseEmitter emitter = userBasedKafkaConsumer.createUserStream(username, "auth");
        if (emitter == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(emitter);
    }

    // 3. 인증되지 않은 접근 로그 스트리밍 (권한 기반)
    @RequirePermission({Permission.STREAM_UNAUTH_LOGS})
    @GetMapping(value = "/unauth", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getUnauthorizedAccess(Authentication authentication) {
        String username = authentication.getName();
        log.info("사용자 {}가 unauth API 호출", username);
        
        SseEmitter emitter = userBasedKafkaConsumer.createUserStream(username, "unauth");
        if (emitter == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(emitter);
    }

    // 4. 인증 실패 로그 스트리밍 (권한 기반)
    @RequirePermission({Permission.STREAM_AUTH_FAILED_LOGS})
    @GetMapping(value = "/auth_failed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> getAuthFailed(Authentication authentication) {
        String username = authentication.getName();
        log.info("사용자 {}가 auth_failed API 호출", username);
        
        SseEmitter emitter = userBasedKafkaConsumer.createUserStream(username, "auth_failed");
        if (emitter == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(emitter);
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        String username = authentication.getName();
        log.info("사용자 {} 로그아웃", username);
        
        userBasedKafkaConsumer.stopUserStreams(username);
        
        return ResponseEntity.ok().body("{\"message\": \"로그아웃 성공\"}");
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