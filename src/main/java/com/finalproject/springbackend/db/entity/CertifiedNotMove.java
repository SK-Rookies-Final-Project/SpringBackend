package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="`certified-notMove`")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertifiedNotMove {

    /**
     * certified-notMove:
     * 인증 실패 후 일정 시간 동안 추가 활동이 없을 경우 발생하는 이벤트를 저장하는 엔티티 테이블
     */

    @Id @Column(columnDefinition="text")
    private String id;                      //기본키

    @Column(name="client_ip", columnDefinition="text")
    private String clientIp;                //비인가 접근을 시도한 클라이언트 ip

    @Column(name="alert_time_kst",columnDefinition="timestamptz", nullable = false)
    private OffsetDateTime alertTimeKST;    //비인가 접근 시도한 시간

    @Column(name="alert_type", columnDefinition = "text")
    private String alertType;               //비인가 접근 유형

    @Column(name="description", columnDefinition = "text")
    private String description;             //비인가 접근에 대한 설명

    @Column(name="failure_count", columnDefinition = "BIGINT")
    private Long failureCount;              //각 유형 별 비인가 접근 횟수

}

//{
//  "clientIp": "45.156.129.153",
//  "alertTime": "2025-09-11 17:55:04.335 KST",
//  "failureCount": 1,
//  "message": "IP 45.156.129.153 has been inactive for 5 minutes after 1 failed attempt(s)."
//}