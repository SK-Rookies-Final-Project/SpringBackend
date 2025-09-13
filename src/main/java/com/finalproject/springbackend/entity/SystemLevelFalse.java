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
