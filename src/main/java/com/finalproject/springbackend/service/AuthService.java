package com.finalproject.springbackend.service;

import com.finalproject.springbackend.dto.LoginRequestDTO;
import com.finalproject.springbackend.dto.LoginResponseDTO;
import com.finalproject.springbackend.dto.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KafkaAuthenticationService kafkaAuthenticationService;

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    // 하드코딩된 사용자 정보 제거 - 이제 Kafka SCRAM 인증만 사용

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        log.info("🔐 로그인 시도: {} (실제 Kafka SCRAM 계정으로 인증)", username);

        // 1. 실제 Kafka 브로커의 SCRAM 인증 확인
        log.info("📡 Kafka 브로커 SCRAM 인증 확인 중: {}", username);
        boolean kafkaAuthSuccess = kafkaAuthenticationService.authenticateWithKafka(username, password);
        
        if (!kafkaAuthSuccess) {
            log.error("❌ Kafka SCRAM 인증 실패: {} - 계정이 Kafka 브로커에 존재하지 않거나 비밀번호가 틀렸습니다.", username);
            return LoginResponseDTO.builder()
                    .success(false)
                    .message("Kafka SCRAM 인증에 실패했습니다. 계정 정보를 확인해주세요")
                    .build();
        }

        // 2. JWT 토큰 생성 (지역 정보는 기본값 사용)
        String region = determineRegionFromUsername(username);
        String token = generateToken(username, region);
        log.info("✅ 로그인 성공: {} (Kafka SCRAM 인증 완료, 지역: {})", username, region);
        
        return LoginResponseDTO.builder()
                .success(true)
                .token(token)
                .username(username)
                .region(region)
                .message("로그인 성공 (Kafka SCRAM 인증 완료)")
                .build();
    }

    /**
     * 사용자명으로부터 지역을 결정합니다.
     * 실제 환경에서는 사용자 정보를 DB에서 조회하거나 다른 방식으로 결정해야 합니다.
     */
    private String determineRegionFromUsername(String username) {
        // 사용자명 패턴으로 지역 결정 (예: admin, ca는 seoul, 나머지는 ohio)
        if ("admin".equals(username) || "ca".equals(username)) {
            return "seoul";
        } else {
            return "ohio";
        }
    }

    /**
     * 사용자명으로부터 허용된 토픽을 결정합니다.
     * 실제 환경에서는 사용자 권한을 DB에서 조회해야 합니다.
     */
    public String[] getAllowedTopicsForUser(String username) {
        // 사용자명 패턴으로 권한 결정
        switch (username) {
            case "admin":
                return new String[]{"authorized-access", "access-failed", "confluent-audit-log-events", "unauthorized-access"};
            case "ca":
                return new String[]{"authorized-access", "access-failed"};
            case "ro":  // Read Only 사용자
                return new String[]{"confluent-audit-log-events"};
            case "dw":  // Data Warehouse 사용자
                return new String[]{"confluent-audit-log-events", "unauthorized-access"};
            case "dr":  // Data Reader 사용자
                return new String[]{"confluent-audit-log-events"};
            case "sd":
                return new String[]{"confluent-audit-log-events", "unauthorized-access"};
            case "cd":
                return new String[]{"confluent-audit-log-events"};
            case "urd":
                return new String[]{"unauthorized-access"};
            default:
                // 기본적으로 모든 토픽 허용 (실제 환경에서는 제한적이어야 함)
                return new String[]{"authorized-access", "access-failed", "confluent-audit-log-events", "unauthorized-access"};
        }
    }

    public UserInfo getUserInfo(String username) {
        // 하드코딩된 사용자 정보 대신 동적으로 생성
        return UserInfo.builder()
                .username(username)
                .password("") // 비밀번호는 저장하지 않음
                .region(determineRegionFromUsername(username))
                .allowedTopics(getAllowedTopicsForUser(username))
                .build();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("토큰에서 사용자명 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    private String generateToken(String username, String region) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(username)
                .claim("region", region)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Kafka 인증 테스트를 위한 메서드
     */
    public boolean testKafkaAuthentication(String username, String password) {
        return kafkaAuthenticationService.authenticateWithKafka(username, password);
    }
}
