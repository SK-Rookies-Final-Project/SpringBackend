package com.finalproject.springbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    
    // SSE Emitter 저장소들
    private final Map<String, SseEmitter> rawLogEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> authorizedEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> unauthorizedEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> authFailedEmitters = new ConcurrentHashMap<>();

    //우기님이 추가해주시면 바로 토픽명 추가 ㄱㄱ
    private final Map<String, SseEmitter> oneEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> twoEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> threeEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> fourEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> fiveEmitters = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> sixEmitters = new ConcurrentHashMap<>();


    // Raw 로그 스트림 생성
    public SseEmitter createRawLogStream() {
        return createSseConnection(rawLogEmitters);
    }
    
    // 인증된 접근 스트림 생성
    public SseEmitter createAuthorizedStream() {
        return createSseConnection(authorizedEmitters);
    }
    
    // 인증되지 않은 접근 스트림 생성
    public SseEmitter createUnauthorizedStream() {
        return createSseConnection(unauthorizedEmitters);
    }
    
    // 인증 실패 스트림 생성
    public SseEmitter createAuthFailedStream() {
        return createSseConnection(authFailedEmitters);
    }

    //우기님이 추가해주시면 바로 토픽명 추가 ㄱㄱ
    public SseEmitter createOneStream() { return createSseConnection(oneEmitters); }
    public SseEmitter createTwoStream() { return createSseConnection(twoEmitters); }
    public SseEmitter createThreeStream() { return createSseConnection(threeEmitters); }
    public SseEmitter createFourStream() { return createSseConnection(fourEmitters); }
    public SseEmitter createFiveStream() { return createSseConnection(fiveEmitters); }
    public SseEmitter createSixStream() { return createSseConnection(sixEmitters); }
    
    // 공통 SSE 연결 생성 로직
    private SseEmitter createSseConnection(Map<String, SseEmitter> emitterMap) {
        // clientId를 랜덤값으로 정하겠다
        String clientId = java.util.UUID.randomUUID().toString();

        // Long 타입의 최대값을 열어둘 시간으로 지정하고 데이터의 통로를 열어두겠다
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // clientId와 emitter를 연결하겠다
        emitterMap.put(clientId, emitter);
        
        // 연결 종료 시 정리
        emitter.onCompletion(() -> emitterMap.remove(clientId));
        emitter.onTimeout(() -> emitterMap.remove(clientId));
        emitter.onError((throwable) -> emitterMap.remove(clientId));
        
        return emitter;
    }
    
    // Getter 메소드들 - Consumer에서 접근하기 위함
    public Map<String, SseEmitter> getRawLogEmitters() {
        return rawLogEmitters;
    }
    
    public Map<String, SseEmitter> getAuthorizedEmitters() {
        return authorizedEmitters;
    }
    
    public Map<String, SseEmitter> getUnauthorizedEmitters() {
        return unauthorizedEmitters;
    }
    
    public Map<String, SseEmitter> getAuthFailedEmitters() {
        return authFailedEmitters;
    }

    //우기님이 추가해주시면 바로 토픽명 추가 ㄱㄱ
    public Map<String, SseEmitter> getOneEmitters() { return oneEmitters; }
    public Map<String, SseEmitter> getTwoEmitters() { return twoEmitters; }
    public Map<String, SseEmitter> getThreeEmitters() { return threeEmitters; }
    public Map<String, SseEmitter> getFourEmitters() { return fourEmitters; }
    public Map<String, SseEmitter> getFiveEmitters() { return fiveEmitters; }
    public Map<String, SseEmitter> getSixEmitters() { return sixEmitters; }

}