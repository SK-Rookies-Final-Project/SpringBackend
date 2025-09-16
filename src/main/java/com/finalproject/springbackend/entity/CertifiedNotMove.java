package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="\"certified-not-move\"")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertifiedNotMove {
    @Id @Column(columnDefinition="text")
    private String id;

    @Column(name="client_ip", columnDefinition="text")
    private String clientIp;

    @Column(name="alert_time_kst",columnDefinition="timestamptz", nullable = false)
    private OffsetDateTime alertTimeKST;

    @Column(name="alert_type", columnDefinition = "text")
    private String alertType;

    @Column(name="description", columnDefinition = "text")
    private String description;

    @Column(name="failure_count", columnDefinition = "BIGINT")
    private Long failureCount;

}

//{
//  "clientIp": "45.156.129.153",
//  "alertTime": "2025-09-11 17:55:04.335 KST",
//  "failureCount": 1,
//  "message": "IP 45.156.129.153 has been inactive for 5 minutes after 1 failed attempt(s)."
//}