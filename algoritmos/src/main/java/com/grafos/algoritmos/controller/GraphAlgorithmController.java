package com.grafos.algoritmos.controller;

import com.grafos.algoritmos.dto.AlgorithmResponse;
import com.grafos.algoritmos.dto.GraphRequest;
import com.grafos.algoritmos.entity.ExecutionHistory;
import com.grafos.algoritmos.service.AStarService;
import com.grafos.algoritmos.service.BfsService;
import com.grafos.algoritmos.service.DepthLimitedService;
import com.grafos.algoritmos.service.DfsService;
import com.grafos.algoritmos.service.ExecutionHistoryService;
import com.grafos.algoritmos.service.IterativeDeepeningService;
import com.grafos.algoritmos.service.KruskalService;
import com.grafos.algoritmos.service.PrimService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class GraphAlgorithmController {

    private final DfsService dfsService;
    private final BfsService bfsService;
    private final DepthLimitedService depthLimitedService;
    private final IterativeDeepeningService iterativeDeepeningService;
    private final AStarService aStarService;
    private final KruskalService kruskalService;
    private final PrimService primService;
    private final ExecutionHistoryService executionHistoryService;

    public GraphAlgorithmController(
            DfsService dfsService,
            BfsService bfsService,
            DepthLimitedService depthLimitedService,
            IterativeDeepeningService iterativeDeepeningService,
            AStarService aStarService,
            KruskalService kruskalService,
            PrimService primService,
            ExecutionHistoryService executionHistoryService
    ) {
        this.dfsService = dfsService;
        this.bfsService = bfsService;
        this.depthLimitedService = depthLimitedService;
        this.iterativeDeepeningService = iterativeDeepeningService;
        this.aStarService = aStarService;
        this.kruskalService = kruskalService;
        this.primService = primService;
        this.executionHistoryService = executionHistoryService;
    }

    @PostMapping("/dfs")
    public AlgorithmResponse dfs(@RequestBody GraphRequest request) {
        return executeAndSave("DFS", request, () -> dfsService.execute(request));
    }

    @PostMapping("/bfs")
    public AlgorithmResponse bfs(@RequestBody GraphRequest request) {
        return executeAndSave("BFS", request, () -> bfsService.execute(request));
    }

    @PostMapping("/depth-limited")
    public AlgorithmResponse depthLimited(@RequestBody GraphRequest request) {
        return executeAndSave("DEPTH_LIMITED", request, () -> depthLimitedService.execute(request));
    }

    @PostMapping("/iterative-deepening")
    public AlgorithmResponse iterativeDeepening(@RequestBody GraphRequest request) {
        return executeAndSave("ITERATIVE_DEEPENING", request, () -> iterativeDeepeningService.execute(request));
    }

    @PostMapping("/a-star")
    public AlgorithmResponse aStar(@RequestBody GraphRequest request) {
        return executeAndSave("A_STAR", request, () -> aStarService.execute(request));
    }

    @PostMapping("/kruskal")
    public AlgorithmResponse kruskal(@RequestBody GraphRequest request) {
        return executeAndSave("KRUSKAL", request, () -> kruskalService.execute(request));
    }

    @PostMapping("/prim")
    public AlgorithmResponse prim(@RequestBody GraphRequest request) {
        return executeAndSave("PRIM", request, () -> primService.execute(request));
    }

    @GetMapping("/history")
    public List<ExecutionHistory> history() {
        return executionHistoryService.findLatest();
    }

    private AlgorithmResponse executeAndSave(
            String algorithm,
            GraphRequest request,
            Supplier<AlgorithmResponse> executor
    ) {
        long startTime = System.nanoTime();

        AlgorithmResponse response = executor.get();

        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;

        executionHistoryService.saveExecution(
                algorithm,
                request,
                response,
                executionTimeMs,
                0,
                0.0
        );

        return response;
    }
}