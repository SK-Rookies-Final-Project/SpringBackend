package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="\"system-level-false\"")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLevelFalse {

    @Id @Column(columnDefinition="text")
    private String id;

    @Column(name="event-time-kst", columnDefinition = "timestamptz", nullable = false)
    private OffsetDateTime eventTimeKST;

    @Column(name="process-time-kst",columnDefinition = "timestamptz", nullable = false)
    private OffsetDateTime processTimeKST;

    @Column(name="principal", columnDefinition="text")
    private String principal;

    @Column(name="client_ip", columnDefinition="text")
    private String clientIp;

    @Column(name="method_name", columnDefinition="text")
    private String methodName;

    @Column(name="granted", columnDefinition="boolean")
    private boolean granted;

    @Column(name="resource-type", columnDefinition="text")
    private String resourceType;

    @Column(name="resource-name", columnDefinition="text")
    private String resourceName;

    @Column(name="operation", columnDefinition="text")
    private String operation;
}
