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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KafkaAuthenticationService kafkaAuthenticationService;

    @Value("${jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Value("${APP_DEFAULT_REGION:ohio}")
    private String defaultRegion;

    // 세션 기간 동안 사용자 비밀번호와 토픽을 보관 (메모리)
    private final ConcurrentHashMap<String, String> usernameToPassword = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String[]> usernameToAllowedTopics = new ConcurrentHashMap<>();

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        log.info("🔐 로그인 시도: {} (실제 Kafka SCRAM 계정으로 인증)", username);

        // SCRAM 인증 + 접근 가능 토픽 조회(후보 기반 프로빙 포함)
        Set<String> accessibleTopics;
        try {
            accessibleTopics = kafkaAuthenticationService.listAccessibleTopics(username, password);
        } catch (Exception e) {
            log.error("❌ Kafka SCRAM 인증 실패: {} - {}", username, e.getMessage());
            return LoginResponseDTO.builder()
                    .success(false)
                    .message("Kafka SCRAM 인증에 실패했습니다. 계정 정보를 확인해주세요")
                    .build();
        }

        // 내부 토픽(__*) 제외
        String[] allowedTopics = accessibleTopics.stream()
                .filter(t -> t != null && !t.startsWith("__"))
                .sorted()
                .toArray(String[]::new);

        usernameToPassword.put(username, password);
        usernameToAllowedTopics.put(username, allowedTopics);

        String region = defaultRegion;
        String token = generateToken(username, region);

        return LoginResponseDTO.builder()
                .success(true)
                .token(token)
                .username(username)
                .region(region)
                .message("로그인 성공 (Kafka SCRAM 인증 완료)")
                .allowedTopics(allowedTopics)
                .build();
    }

    public UserInfo getUserInfo(String username) {
        return UserInfo.builder()
                .username(username)
                .password(usernameToPassword.getOrDefault(username, ""))
                .region(defaultRegion)
                .allowedTopics(usernameToAllowedTopics.getOrDefault(username, new String[0]))
                .build();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
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

    public boolean testKafkaAuthentication(String username, String password) {
        return kafkaAuthenticationService.authenticateWithKafka(username, password);
    }
}
