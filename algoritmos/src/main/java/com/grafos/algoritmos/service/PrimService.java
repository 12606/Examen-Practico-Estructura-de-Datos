package com.grafos.algoritmos.service;

import com.grafos.algoritmos.dto.AlgorithmResponse;
import com.grafos.algoritmos.dto.EdgeDto;
import com.grafos.algoritmos.dto.GraphRequest;
import com.grafos.algoritmos.exception.InvalidGraphException;
import com.grafos.algoritmos.graph.Graph;
import com.grafos.algoritmos.graph.GraphBuilder;
import com.grafos.algoritmos.graph.GraphUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PrimService {

    private final GraphBuilder graphBuilder;

    public AlgorithmResponse execute(GraphRequest request) {
        long startTime = System.nanoTime();

        Graph graph = graphBuilder.build(request);

        GraphUtils.validateUndirectedWeightedGraph(graph);
        GraphUtils.validateNonNegativeWeights(request.getEdges());

        if (graph.getNodes().isEmpty()) {
            throw new InvalidGraphException("El grafo no contiene nodos.");
        }

        String start = request.getStart();

        if (start == null || start.isBlank()) {
            start = graph.getNodes().keySet().iterator().next();
        }

        GraphUtils.validateStart(graph, start);

        Set<String> visited = new HashSet<>();
        List<String> visitedOrder = new ArrayList<>();
        List<EdgeDto> resultEdges = new ArrayList<>();

        PriorityQueue<EdgeDto> priorityQueue = new PriorityQueue<>(
                Comparator.comparingDouble(edge -> edge.getWeight() != null ? edge.getWeight() : 1.0)
        );

        visited.add(start);
        visitedOrder.add(start);
        priorityQueue.addAll(graph.getNeighbors(start));

        double totalCost = 0.0;
        int steps = 0;

        while (!priorityQueue.isEmpty() && visited.size() < graph.getNodes().size()) {
            EdgeDto edge = priorityQueue.poll();
            steps++;

            String nextNode = edge.getTo();

            if (visited.contains(nextNode)) {
                continue;
            }

            visited.add(nextNode);
            visitedOrder.add(nextNode);

            double weight = edge.getWeight() != null ? edge.getWeight() : 1.0;
            resultEdges.add(new EdgeDto(edge.getFrom(), edge.getTo(), weight));
            totalCost += weight;

            for (EdgeDto neighborEdge : graph.getNeighbors(nextNode)) {
                if (!visited.contains(neighborEdge.getTo())) {
                    priorityQueue.add(neighborEdge);
                }
            }
        }
        boolean completed = visited.size() == graph.getNodes().size();
        long endTime = System.nanoTime();
        return AlgorithmResponse.builder()
                .algorithm("Prim")
                .visitedOrder(visitedOrder)
                .resultEdges(resultEdges)
                .totalCost(totalCost)
                .steps(steps)
                .found(completed)
                .executionTimeMs((endTime - startTime) / 1_000_000)
                .message(completed
                        ? "Árbol de expansión mínimo generado con Prim."
                        : "El grafo no es conexo; no se pudo generar un MST completo.")
                .build();
    }
}