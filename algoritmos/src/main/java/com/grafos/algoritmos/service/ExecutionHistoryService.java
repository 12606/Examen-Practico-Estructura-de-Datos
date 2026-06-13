package com.grafos.algoritmos.service;

import com.grafos.algoritmos.entity.ExecutionHistory;
import com.grafos.algoritmos.repository.ExecutionHistoryRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecutionHistoryService {

    private final ExecutionHistoryRepository executionHistoryRepository;
    private final JsonMapper jsonMapper;

    public ExecutionHistoryService(
            ExecutionHistoryRepository executionHistoryRepository,
            JsonMapper jsonMapper
    ) {
        this.executionHistoryRepository = executionHistoryRepository;
        this.jsonMapper = jsonMapper;
    }

    public ExecutionHistory saveExecution(
            String algorithm,
            Object request,
            Object response,
            long executionTimeMs,
            int steps,
            double totalCost
    ) {
        try {
            ExecutionHistory history = new ExecutionHistory();

            history.setAlgorithm(algorithm);
            history.setRequestJson(jsonMapper.writeValueAsString(request));
            history.setResponseJson(jsonMapper.writeValueAsString(response));
            history.setExecutionTimeMs(executionTimeMs);
            history.setSteps(steps);
            history.setTotalCost(totalCost);
            history.setCreatedAt(LocalDateTime.now());

            return executionHistoryRepository.save(history);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el historial de ejecución", e);
        }
    }

    public List<ExecutionHistory> findLatest() {
        return executionHistoryRepository.findTop20ByOrderByCreatedAtDesc();
    }
}