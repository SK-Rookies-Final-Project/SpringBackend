package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="\"certified-2-time\"")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certified2Time {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientIp;

    @Column(columnDefinition="timestamptz")
    private OffsetDateTime alertTime;

    private int failureCount;

    @Column(columnDefinition = "text")
    private String message;
    
}
//{
//  "clientIp": "15.164.187.115",
//  "alertTime": "2025-09-11 18:04:58.891 KST",
//  "failureCount": 5,
//  "message": "Login failures detected from IP 15.164.187.115, 5 times within 5 minutes."
//}