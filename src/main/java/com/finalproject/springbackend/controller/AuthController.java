package com.finalproject.springbackend.controller;

import com.finalproject.springbackend.dto.LoginRequestDTO;
import com.finalproject.springbackend.dto.LoginResponseDTO;
import com.finalproject.springbackend.service.AuthService;
import com.finalproject.springbackend.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PermissionService permissionService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("로그인 시도: {}", loginRequest.getUsername());
        
        LoginResponseDTO response = authService.authenticate(loginRequest);
        
        if (response.isSuccess()) {
            log.info("로그인 성공: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } else {
            log.warn("로그인 실패: {}", loginRequest.getUsername());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);
            
            if (isValid) {
                String username = authService.getUsernameFromToken(token);
                return ResponseEntity.ok().body("{\"valid\": true, \"username\": \"" + username + "\"}");
            }
        }
        
        return ResponseEntity.badRequest().body("{\"valid\": false}");
    }

    @GetMapping("/permissions")
    public ResponseEntity<?> getUserPermissions(Authentication authentication) {
        String username = authentication.getName();
        List<String> permissions = permissionService.getPermissionCodes(username);
        
        return ResponseEntity.ok(Map.of(
            "username", username,
            "permissions", permissions
        ));
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        var userInfo = authService.getUserInfo(username);
        List<String> permissions = permissionService.getPermissionCodes(username);
        
        return ResponseEntity.ok(Map.of(
            "username", username,
            "region", userInfo.getRegion(),
            "allowedTopics", userInfo.getAllowedTopics(),
            "permissions", permissions
        ));
    }

    /**
     * Kafka SCRAM 인증 테스트용 엔드포인트 (개발/테스트 목적)
     * 실제 운영 환경에서는 제거해야 합니다.
     * 집가고싶어요
     */
    @PostMapping("/test-kafka-auth")
    public ResponseEntity<?> testKafkaAuth(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Kafka 인증 테스트: {}", loginRequest.getUsername());
        
        try {
            boolean authResult = authService.testKafkaAuthentication(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            return ResponseEntity.ok(Map.of(
                "username", loginRequest.getUsername(),
                "kafkaAuthSuccess", authResult,
                "message", authResult ? "Kafka 인증 성공" : "Kafka 인증 실패"
            ));
        } catch (Exception e) {
            log.error("Kafka 인증 테스트 중 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "username", loginRequest.getUsername(),
                "kafkaAuthSuccess", false,
                "error", e.getMessage()
            ));
        }
    }
}
