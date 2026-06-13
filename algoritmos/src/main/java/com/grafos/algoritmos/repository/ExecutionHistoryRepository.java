package com.grafos.algoritmos.repository;

import com.grafos.algoritmos.entity.ExecutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistory, Long> {

    List<ExecutionHistory> findTop10ByOrderByCreatedAtDesc();
}