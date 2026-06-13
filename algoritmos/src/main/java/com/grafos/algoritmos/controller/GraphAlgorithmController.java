package com.grafos.algoritmos.controller;

import com.grafos.algoritmos.dto.AlgorithmResponse;
import com.grafos.algoritmos.dto.GraphRequest;
import com.grafos.algoritmos.entity.ExecutionHistory;
import com.grafos.algoritmos.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GraphAlgorithmController {

    private final DfsService dfsService;
    private final BfsService bfsService;
    private final DepthLimitedService depthLimitedService;
    private final IterativeDeepeningService iterativeDeepeningService;
    private final AStarService aStarService;
    private final KruskalService kruskalService;
    private final PrimService primService;
    private final ExecutionHistoryService executionHistoryService;

    @PostMapping("/dfs")
    public AlgorithmResponse dfs(@RequestBody GraphRequest request) {
        AlgorithmResponse response = dfsService.execute(request);
        executionHistoryService.save(request, response);
        return response;
    }

    @PostMapping("/bfs")
    public AlgorithmResponse bfs(@RequestBody GraphRequest request) {
        AlgorithmResponse response = bfsService.execute(request);
        executionHistoryService.save(request, response);
        return response;
    }

    @PostMapping("/depth-limited")
    public AlgorithmResponse depthLimited(@RequestBody GraphRequest request) {
        AlgorithmResponse response = depthLimitedService.execute(request);
        executionHistoryService.save(request, response);
        return response;
    }

    @PostMapping("/iterative-deepening")
    public AlgorithmResponse iterativeDeepening(@RequestBody GraphRequest request) {
        AlgorithmResponse response = iterativeDeepeningService.execute(request);
        executionHistoryService.save(request, response);
        return response;
    }

    @PostMapping("/a-star")
    public AlgorithmResponse aStar(@RequestBody GraphRequest request) {
        AlgorithmResponse response = aStarService.execute(request);
        executionHistoryService.save(request, response);
        return response;
    }

    @PostMapping("/kruskal")
    public AlgorithmResponse kruskal(@RequestBody GraphRequest request) {
        AlgorithmResponse response = kruskalService.execute(request);
        executionHistoryService.save(request, response);
        return response;
    }

    @PostMapping("/prim")
    public AlgorithmResponse prim(@RequestBody GraphRequest request) {
        AlgorithmResponse response = primService.execute(request);
        executionHistoryService.save(request, response);
        return response;
    }

    @GetMapping("/history")
    public List<ExecutionHistory> history() {
        return executionHistoryService.findLatest();
    }
}