package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="`certified-2time`")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certified2Time {

    @Id @Column(columnDefinition = "text")
    private String id;

    @Column(name="client_ip", columnDefinition="text")
    private String clientIp;

    @Column(name="alert_time_kst", columnDefinition="timestamptz", nullable = false)
    private OffsetDateTime alertTimeKST;

    @Column(name="alert_type", columnDefinition = "text", nullable = false)
    private String alertType;

    @Column(name="description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name="failure_count", columnDefinition = "BIGINT", nullable = false)
    private Long failureCount;


}
//{
//  "clientIp": "15.164.187.115",
//  "alertTime": "2025-09-11 18:04:58.891 KST",
//  "failureCount": 5,
//  "message": "Login failures detected from IP 15.164.187.115, 5 times within 5 minutes."
//}