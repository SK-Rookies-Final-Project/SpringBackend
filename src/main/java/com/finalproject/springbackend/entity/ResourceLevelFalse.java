package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"resource-level-false\"")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLevelFalse {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime eventTimeKST;

    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime processingTimeKST;

    private String principal;

    private String clientIp;

    private String methodName;

    private boolean granted;

    private String resourceType;

    private String resourceName;

    private String operation;



}
// 토픽명: resource-level-false
// {
//  "eventTimeKST": "2025-09-11 11:05:32.185 KST",
//  "processingTimeKST": "2025-09-11 11:05:32.774 KST",
//  "principal": "User:dr",
//  "clientIp": "15.164.187.115",
//  "methodName": "kafka.DescribeConfigs",
//  "granted": false,
//  "resourceType": "Topic",
//  "resourceName": "audit-topic",
//  "operation": "DescribeConfigs"
//}

