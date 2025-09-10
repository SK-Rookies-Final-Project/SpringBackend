package com.finalproject.springbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "test_table1")
@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Date date;
}
