package com.syngenta.syntree.routes;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.internal.helpers.collection.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Set;
import java.util.HashSet;

public class RouteLink {

    public final List<Relationship> relationships = new ArrayList<>();
    public Set<Node> leafNodes = new HashSet<>();
    // this should become rootNode. A link cannot have more than one root
    public final Set<Node> rootNodes = new HashSet<>();
    private boolean wasCanceled = false;

    public RouteLink() {
        this(null);
    }

    public RouteLink(List<Relationship> relationships) {
        if (relationships != null && !relationships.isEmpty()) {
            this.relationships.addAll(relationships);
            updateLeafNodes();
        }
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void add(Relationship rel) {
        relationships.add(rel);
        updateLeafNodes();
    }

    public void removeLeafNode(Node node) {
        this.leafNodes.remove(node);
    }

    @Override
    public String toString() {
        return relationships.stream()
                .map(r -> r.getEndNode().getProperty("uid") + "<-" + r.getStartNode().getProperty("uid"))
                .collect(Collectors.joining(","));
    }
    public Set<Node> getLeafNodes() {
        return leafNodes;
    }

    public Set<Node> getRootNodes() {
        return rootNodes;
    }

    public boolean wasCanceled() {
        if (wasCanceled) {
            return true;
        }
        return false;
    }

    public void setWasCanceled(boolean wasCanceled) {
        this.wasCanceled = wasCanceled;
    }

    // This part should be optimized. Can this be done at the level of routelink collection in synTreeEvaluator?
    private void updateLeafNodes() {
        leafNodes.clear();
        Set<Node> allNodes = relationships.stream()
                .flatMap(r -> Stream.of(r.getStartNode(), r.getEndNode()))
                .collect(Collectors.toSet());

        for (Node node : allNodes) {
            long inDegree = relationships.stream()
                    .flatMap(r -> Stream.of(r.getStartNode()))
                    .filter(n -> n.equals(node))
                    .count();
            long outDegree = relationships.stream()
                    .flatMap(r -> Stream.of(r.getEndNode()))
                    .filter(n -> n.equals(node))
                    .count();
            if (inDegree == 1 && outDegree == 0) {
                leafNodes.add(node);
            }
            if (outDegree == 1 && inDegree == 0) {
                rootNodes.add(node);
            }
        }
    }
}
