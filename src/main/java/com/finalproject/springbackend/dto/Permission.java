package com.finalproject.springbackend.dto;

public enum Permission {
    // 스트리밍 API 권한
    STREAM_RAW_LOGS("stream:raw_logs", "정제 전 데이터 스트리밍"),
    STREAM_AUTH_LOGS("stream:auth_logs", "인증된 접근 로그 스트리밍"),
    STREAM_UNAUTH_LOGS("stream:unauth_logs", "인증되지 않은 접근 로그 스트리밍"),
    STREAM_AUTH_FAILED_LOGS("stream:auth_failed_logs", "인증 실패 로그 스트리밍"),
    
    // 관리자 권한
    ADMIN_ALL("admin:all", "모든 권한"),
    
    // 지역별 권한
    SEOUL_REGION("region:seoul", "Seoul 지역 접근"),
    OHIO_REGION("region:ohio", "Ohio 지역 접근");

    private final String code;
    private final String description;

    Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
