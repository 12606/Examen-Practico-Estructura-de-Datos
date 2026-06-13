package com.grafos.algoritmos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grafos.algoritmos.dto.AlgorithmResponse;
import com.grafos.algoritmos.dto.GraphRequest;
import com.grafos.algoritmos.entity.ExecutionHistory;
import com.grafos.algoritmos.repository.ExecutionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecutionHistoryService {

    private final ExecutionHistoryRepository repository;
    private final ObjectMapper objectMapper;

    public void save(GraphRequest request, AlgorithmResponse response) {
        try {
            ExecutionHistory history = new ExecutionHistory();

            history.setAlgorithm(response.getAlgorithm());
            history.setRequestJson(objectMapper.writeValueAsString(request));
            history.setResponseJson(objectMapper.writeValueAsString(response));
            history.setTotalCost(response.getTotalCost());
            history.setSteps(response.getSteps());
            history.setExecutionTimeMs(response.getExecutionTimeMs());
            history.setCreatedAt(LocalDateTime.now());

            repository.save(history);
        } catch (Exception ex) {
            /*
             * Se evita interrumpir la ejecución del algoritmo si falla
             * el registro del historial.
             */
            System.err.println("No se pudo guardar el historial: " + ex.getMessage());
        }
    }

    public List<ExecutionHistory> findLatest() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }
}