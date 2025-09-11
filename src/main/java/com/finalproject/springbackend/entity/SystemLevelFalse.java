package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="\"system-level-false\"")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLevelFalse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="payload", columnDefinition = "text")
    private String payload;
}
