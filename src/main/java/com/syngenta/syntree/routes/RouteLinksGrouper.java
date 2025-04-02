package com.syngenta.syntree.routes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.graphdb.*;

// can this be directly how route links are collected in synTreeEvaluator?
public class RouteLinksGrouper {
    public static Map<Node, List<RouteLink>> groupRouteLinks(Collection<RouteLink> routeLinks) {
        Map<Node, List<RouteLink>> groupedRouteLinks = new HashMap<>();

        for (RouteLink routeLink : routeLinks) {
            Set<Node> rootNodes = routeLink.getRootNodes();
            Node rootNode = rootNodes.iterator().next();
            List<RouteLink> linksForRootNode = groupedRouteLinks.computeIfAbsent(rootNode, k -> new ArrayList<>());
            linksForRootNode.add(routeLink);
        }

        return groupedRouteLinks;
    }
}