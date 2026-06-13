package com.grafos.algoritmos.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.grafos.algoritmos.dto.AlgorithmResponse;
import com.grafos.algoritmos.dto.GraphRequest;
import com.grafos.algoritmos.dto.NodeDto;
import com.grafos.algoritmos.graph.Graph;
import com.grafos.algoritmos.graph.GraphBuilder;
import com.grafos.algoritmos.graph.GraphUtils;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AStarService {

    private final GraphBuilder graphBuilder;

    public AlgorithmResponse execute(GraphRequest request) {
        long startTime = System.nanoTime();

        Graph graph = graphBuilder.build(request);

        String start = request.getStart();
        String goal = request.getGoal();

        GraphUtils.validateStart(graph, start);
        GraphUtils.validateGoal(graph, goal);
        GraphUtils.validateNonNegativeWeights(request.getEdges());

        String heuristicType = request.getHeuristic() != null
                ? request.getHeuristic().trim().toUpperCase()
                : "NONE";

        PriorityQueue<NodeState> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(NodeState::fScore)
        );

        Map<String, Double> gScore = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> closedSet = new HashSet<>();
        List<String> visitedOrder = new ArrayList<>();

        for (String nodeId : graph.getNodes().keySet()) {
            gScore.put(nodeId, Double.POSITIVE_INFINITY);
        }

        gScore.put(start, 0.0);

        openSet.add(new NodeState(
                start,
                0.0,
                heuristic(graph, start, goal, heuristicType)
        ));

        int steps = 0;
        boolean found = false;

        while (!openSet.isEmpty()) {
            NodeState currentState = openSet.poll();
            String current = currentState.nodeId();

            if (closedSet.contains(current)) {
                continue;
            }

            closedSet.add(current);
            visitedOrder.add(current);
            steps++;

            if (current.equals(goal)) {
                found = true;
                break;
            }

            for (var edge : graph.getNeighbors(current)) {
                String neighbor = edge.getTo();
                double weight = edge.getWeight() != null ? edge.getWeight() : 1.0;

                double tentativeGScore = gScore.get(current) + weight;

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    parent.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);

                    double hScore = heuristic(graph, neighbor, goal, heuristicType);
                    openSet.add(new NodeState(neighbor, tentativeGScore, hScore));
                }
            }
        }
        List<String> path = found
                ? GraphUtils.reconstructPath(parent, start, goal)
                : List.of();
        double totalCost = found ? gScore.get(goal) : 0.0;
        long endTime = System.nanoTime();
        return AlgorithmResponse.builder()
                .algorithm("A*")
                .visitedOrder(visitedOrder)
                .path(path)
                .totalCost(totalCost)
                .steps(steps)
                .found(found)
                .executionTimeMs((endTime - startTime) / 1_000_000)
                .message(found ? "Camino óptimo encontrado con A*." : "No se encontró camino con A*.")
                .build();
    }
    private double heuristic(
            Graph graph,
            String current,
            String goal,
            String heuristicType
    ) {
        NodeDto currentNode = graph.getNode(current);
        NodeDto goalNode = graph.getNode(goal);

        if (currentNode == null || goalNode == null) {
            return 0.0;
        }

        if (currentNode.getX() == null || currentNode.getY() == null
                || goalNode.getX() == null || goalNode.getY() == null) {
            return 0.0;
        }
        double dx = Math.abs(currentNode.getX() - goalNode.getX());
        double dy = Math.abs(currentNode.getY() - goalNode.getY());
        return switch (heuristicType) {
            case "MANHATTAN" -> dx + dy;
            case "EUCLIDEAN" -> Math.sqrt(dx * dx + dy * dy);
            default -> 0.0;
        };
    }
    @AllArgsConstructor
    private static class NodeState {
        private String nodeId;
        private double gScore;
        private double hScore;
        public String nodeId() {
            return nodeId;
        }
        public double fScore() {
            return gScore + hScore;
        }
    }
}