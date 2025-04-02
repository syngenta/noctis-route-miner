package com.syngenta.syntree.routes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.graphdb.*;

public class NewRoute {
    public List<RouteLink> routeLinks;
    public Set<Node> leafNodes;
    public Set<Node> visitedNodes;

    public NewRoute() {
        this.routeLinks = new ArrayList<>();
        this.leafNodes = new HashSet<>();
        // visitedNodes attribute is used to determined which leafNodes are part of a loop
        // should be removed as these nodes should not be put as leafNodes at the stage of
        // routeLinks population
        this.visitedNodes = new HashSet<>();
    }

    public NewRoute(RouteLink routeLink) {
        this();
        if (routeLink != null) {
            this.routeLinks.add(routeLink);
            this.leafNodes.addAll(routeLink.getLeafNodes());
            // Should be redone for a single root node instead of nodes
            this.visitedNodes.addAll(routeLink.getRootNodes());
        }
    }

    public NewRoute addLink(RouteLink link) {
        NewRoute newRoute = new NewRoute();
        newRoute.routeLinks.addAll(this.routeLinks);
        newRoute.routeLinks.add(link);
        newRoute.visitedNodes.addAll(this.visitedNodes);
        newRoute.visitedNodes.addAll(link.getRootNodes());
        newRoute.leafNodes.addAll(updateLeafNodes(link));
        return newRoute;
    }

    public void removeLeafNode(Node node) {
        this.leafNodes.remove(node);
    }

    public List<Relationship> getRelationships() {
        List<Relationship> relationships = new ArrayList<>();
        for (RouteLink routeLink : routeLinks) {
            relationships.addAll(routeLink.getRelationships());
        }
        return relationships;
    }

    private Set<Node> updateLeafNodes(RouteLink link) {
        Set<Node> oldNodes = new HashSet<>(this.leafNodes);
        Set<Node> newNodes = new HashSet<>(link.getLeafNodes());
        oldNodes.addAll(newNodes);
        return oldNodes;
    }

    public NewRoute copy() {
        NewRoute newRoute = new NewRoute();
        newRoute.routeLinks.addAll(this.routeLinks);
        newRoute.leafNodes.addAll(this.leafNodes);
        newRoute.visitedNodes.addAll(this.visitedNodes);
        return newRoute;
    }

    @Override
    public String toString() {
        return routeLinks.stream()
                .map(RouteLink::toString)
                .collect(Collectors.joining(","));
    }
}