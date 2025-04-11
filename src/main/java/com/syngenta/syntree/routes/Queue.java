package com.syngenta.syntree.routes;

import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


import java.util.ArrayList;

import org.neo4j.graphdb.*;

// Can Queue be replaces with native to neo4j lib class?


class Queue {
    // order -- defines in which order the OR nodes are traversed
    // would be good to sort the nodes by the distance from the root
    // this will decrease the computational cost for some cases
    // or this can be done at the level of curren routes
    List<Node> order;
    // should be deprecated
    Set<Node> visited;
    // allNodes -- a collection of all OR nodes
    Set<Node> allNodes;

    Queue(Map<Node, List<NewRoute>> orRoutes) {
        order = new ArrayList<>();
        visited = new HashSet<>();
        allNodes = orRoutes.keySet();
    }

    Node getNext() {
        if (order.isEmpty()) {
            return null;
        }
        Node nextNode = order.get(0);
        order.remove(0);
        visited.add(nextNode);
        return nextNode;
    }

    void addNodes(Set<Node> nodes) {
        for (Node node : nodes) {
            if (!order.contains(node) && allNodes.contains(node)) {
                order.add(node);
            }
        }
    }

}