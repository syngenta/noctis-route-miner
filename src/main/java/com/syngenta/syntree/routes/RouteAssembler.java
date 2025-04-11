package com.syngenta.syntree.routes;

import org.neo4j.graphdb.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.String;

// Can the assembling procedure be optimised?

class RouteAssembler {
    public static Collection<NewRoute> assemble(Map<Node, List<RouteLink>> groupedRouteLinks, Node startNode) {
        // orRoutes contains as keys all OR nodes, as values list of routes
        // List of routes represents all the routes which have at the moment this OR node among it's leaf nodes
        Map<Node, List<NewRoute>> orRoutes = new HashMap<>();
        // When the route doesn't have OR nodes to be investigated, it's considered to be a final route
        Collection<NewRoute> finalRoutes = new HashSet<>();

        // Initialize orRoutes with keys from groupedRouteLinks except for the start node
        for (Node node : groupedRouteLinks.keySet()) {
            if (!node.getProperty("uid").equals(startNode.getProperty("uid"))) {
                orRoutes.put(node, new ArrayList<>());
            }
        }

        // PRINTING
        System.out.println("OR Routes INIT");
        orRoutes.forEach((node, links) -> {
            String nodeId = node.getProperty("uid").toString();
            System.out.println("Node ID: " + nodeId);
            links.forEach(r -> System.out.println("\t" + r.toString()));
        });

        // Set up orRoutes map
        List<RouteLink> currentLinks = groupedRouteLinks.get(startNode);
        Queue queue = new Queue(orRoutes);
        // Remove the leaf Nodes of the tree
        // this should happen at the stage of creating routeLinks
        for (RouteLink link : currentLinks) {
            for (Node leafNode : new HashSet<>(link.leafNodes)) {
                if (!queue.allNodes.contains(leafNode)) {
                    link.removeLeafNode(leafNode);
                }
            }
        }


        System.out.println("Current link INIT");
        for (RouteLink link : currentLinks) {
            System.out.println(link.toString());
        }

        // Initialization of the queue.order
        for (RouteLink link : currentLinks) {
            NewRoute newRoute = new NewRoute(link);
            System.out.println("NewRoute: " + newRoute);
            queue.addNodes(link.leafNodes);
            System.out.println("Queue order" );
            queue.order.stream()
            .map(node -> node.getProperty("uid").toString())
            .forEach(System.out::println);
            // in case the link doesn't have OR nodes among leaf nodes
            if (newRoute.leafNodes.isEmpty()) {
                finalRoutes.add(newRoute);
            } else {
                for (Node leafNode : link.leafNodes) {
                    // no need to remove the route since orRoutes is empty ATM
                    orRoutes.get(leafNode).add(newRoute);
                }
            }

        }

        // PRINTING
        System.out.println("OR Routes after SET UP");
        orRoutes.forEach((node, links) -> {
            String nodeId = node.getProperty("uid").toString();
            System.out.println("Node ID: " + nodeId);
            links.forEach(r -> System.out.println("\t" + r.toString()));
        });

        // Iterate through OR nodes to get the routes
        Node currentNode = queue.getNext();

        while (currentNode != null) {
            System.out.println("=====================================================================");
            System.out.println("START |iteration");
            System.out.println(currentNode.getProperty("uid").toString());

            // Getting links over which iteration will be happening
            currentLinks = groupedRouteLinks.get(currentNode);

            // Modify the leaf nodes of each RouteLink in currentLinks
            // this should not happen here
            // should be part of how route links are created in the first part of the algorithm
            for (RouteLink link : currentLinks) {
                for (Node leafNode : new HashSet<>(link.leafNodes)) {
                    if (!queue.allNodes.contains(leafNode)) {
                        link.removeLeafNode(leafNode);
                    }
                }
            }

            // PRINTING
            System.out.println("Current link |iteration");
            for (RouteLink link : currentLinks) {
                System.out.println(link.toString());
            }

            // PRINTING
            for (RouteLink ll : currentLinks) {
                System.out.println("Leaf Nodes for RouteLink:");
                for (Node leafNode : ll.leafNodes) {
                    System.out.println(leafNode.getProperty("uid").toString());
                }
                System.out.println();
            }

            //Getting routes which have curren Node as one of leafNodes
            List<NewRoute> currentRoutes = orRoutes.get(currentNode);
            // Making a copy over which the iteration will be happening
            //because the currentRoutes will be modified. Can this be done better?
            List<NewRoute> currentRoutesCopy = new ArrayList<>(currentRoutes);
            // PRINTING
            System.out.println("Current routes |iteration");
            currentRoutes.forEach(r -> System.out.println("\t" + r.toString()));

            //Iterating over route to which links will be connected
            for (NewRoute route : currentRoutesCopy) {
                System.out.println("1 |iteration");
                // This is not needed and the list of current nodes
                // can be wiped at the end of the iteration
                orRoutes.get(currentNode).remove(route);
                // Removing the currentNode from leafNodes so we don't follow it anymore
                route.removeLeafNode(currentNode);
                System.out.println(route.toString());
                // Introducing the flag firstLink to reduce the number of remove operations
                boolean firstLink = true;
                for (RouteLink link : currentLinks) {
                    System.out.println("2 |iteration");
                    System.out.println(link.toString());

                    NewRoute newRoute = route.addLink(link);
                    // this part will be removed
                    // it's here because the leafNodes of routeLink contain
                    // leafNdoes of the tree and ends of the loop
                    for (Node leafNode : link.leafNodes) {
                        // Check if the leaf node is present in the visitedNodes set
                        if (newRoute.visitedNodes.contains(leafNode)) {
                            // Add the leaf node to the temporary list for removal
                            newRoute.removeLeafNode(leafNode);
                        }
                    }
                    if (newRoute.leafNodes.isEmpty()) {
                        System.out.println("2.1 |iteration");
                        // if no OR nodes in leafNodes, then it's a final route
                        finalRoutes.add(newRoute);
                    } else {
                        if (firstLink) {
                            // current node is not in leaf nodes anymore
                            // it has been removed above
                            for (Node node : route.leafNodes) {
                                System.out.println("2.2 |iteration");
                                System.out.println(node.getProperty("uid").toString());
                                // if this is the firstLink old route has to be removed from the node
                                orRoutes.get(node).remove(route);
                                orRoutes.get(node).add(newRoute);
                            }
                            for (Node node : new HashSet<>(newRoute.leafNodes)) {
                                // does this take a lot of computational time?
                                // how to implement this if more efficiently
                                if (!route.leafNodes.contains(node)) {
                                    // PRINTING
                                    System.out.println("2.3 |iteration");
                                    System.out.println(node.getProperty("uid").toString());
                                    System.out.println("OR Routes");
                                    orRoutes.forEach((nn, ll) -> {
                                        String nodeId = nn.getProperty("uid").toString();
                                        System.out.println("Node ID: " + nodeId);
                                        ll.forEach(r -> System.out.println("\t" + r.toString()));
                                    });
                                    // Just adding the route, no removing is needed because this route
                                    // was never assigned to this node
                                    orRoutes.get(node).add(newRoute);
                                }
                            }
                        } else {
                            System.out.println("2.4 |iteration");
                            // because it's not the first link. the route has been removed already from the nodes
                            // to which it had been assigned
                            for (Node node : newRoute.leafNodes) {
                                System.out.println(node.getProperty("uid").toString());
                                orRoutes.get(node).add(newRoute);
                            }
                        }
                        System.out.println("2.5 |iteration");
                        // adding the nodes to the queue
                        queue.addNodes(link.leafNodes);
                        queue.order.stream()
                            .map(node -> node.getProperty("uid").toString())
                            .forEach(System.out::println);
                        System.out.println("Current routes |iteration");
                        currentRoutesCopy.forEach(r -> System.out.println("\t" + r.toString()));
                    }
                    System.out.println("2.6 new link |iteration");
                    firstLink = false;
                }
                System.out.println("2.7 new route |iteration");
            }
            System.out.println("3 |iteration");
            currentNode = queue.getNext();
            // PRINTING
            System.out.println("OR Routes END iteration");
            orRoutes.forEach((node, links) -> {
                String nodeId = node.getProperty("uid").toString();
                System.out.println("Node ID: " + nodeId);
                links.forEach(r -> System.out.println("\t" + r.toString()));
            });
        }

        return finalRoutes;
    }
}