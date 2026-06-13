package com.grafos.algoritmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "execution_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String algorithm;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String requestJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String responseJson;

    private Double totalCost;

    private Integer steps;

    private Long executionTimeMs;

    private LocalDateTime createdAt;
}