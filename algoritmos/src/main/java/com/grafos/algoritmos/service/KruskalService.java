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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KruskalService {

    private final GraphBuilder graphBuilder;

    public AlgorithmResponse execute(GraphRequest request) {
        long startTime = System.nanoTime();

        Graph graph = graphBuilder.build(request);

        GraphUtils.validateUndirectedWeightedGraph(graph);
        GraphUtils.validateNonNegativeWeights(request.getEdges());

        if (request.getEdges() == null || request.getEdges().isEmpty()) {
            throw new InvalidGraphException("Kruskal requiere aristas ponderadas.");
        }

        List<EdgeDto> sortedEdges = new ArrayList<>(request.getEdges());

        sortedEdges.sort(Comparator.comparingDouble(
                edge -> edge.getWeight() != null ? edge.getWeight() : 1.0
        ));

        UnionFind unionFind = new UnionFind(graph.getNodes().keySet());

        List<EdgeDto> resultEdges = new ArrayList<>();
        double totalCost = 0.0;
        int steps = 0;

        for (EdgeDto edge : sortedEdges) {
            steps++;

            String from = edge.getFrom();
            String to = edge.getTo();
            double weight = edge.getWeight() != null ? edge.getWeight() : 1.0;

            if (unionFind.union(from, to)) {
                resultEdges.add(new EdgeDto(from, to, weight));
                totalCost += weight;
            }

            if (resultEdges.size() == graph.getNodes().size() - 1) {
                break;
            }
        }

        boolean completed = resultEdges.size() == graph.getNodes().size() - 1;

        long endTime = System.nanoTime();

        return AlgorithmResponse.builder()
                .algorithm("Kruskal")
                .resultEdges(resultEdges)
                .totalCost(totalCost)
                .steps(steps)
                .found(completed)
                .executionTimeMs((endTime - startTime) / 1_000_000)
                .message(completed
                        ? "Árbol de expansión mínimo generado con Kruskal."
                        : "El grafo no es conexo; se generó un bosque de expansión mínimo.")
                .build();
    }
}