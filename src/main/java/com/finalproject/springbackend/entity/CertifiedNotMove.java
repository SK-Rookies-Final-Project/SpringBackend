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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientIp;

    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime alertTime;

    private int failureCount;

    @Column(columnDefinition = "text")
    private String message;

}

//{
//  "clientIp": "45.156.129.153",
//  "alertTime": "2025-09-11 17:55:04.335 KST",
//  "failureCount": 1,
//  "message": "IP 45.156.129.153 has been inactive for 5 minutes after 1 failed attempt(s)."
//}