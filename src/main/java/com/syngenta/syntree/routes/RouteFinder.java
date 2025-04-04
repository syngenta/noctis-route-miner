package com.syngenta.syntree.routes;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.InitialBranchState;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.internal.helpers.collection.Pair;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import com.syngenta.syntree.routes.uniqueness.CustomUniqueness;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.neo4j.graphdb.Direction.*;

public class RouteFinder {

    @Context
    public Transaction tx;

    @Context
    public Log log;

    @Procedure(name="noctis.route.miner", mode = Mode.READ)
    public Stream<RelationshipsNewRouteResult> find(@Name("startNode") Node startNode,
                                                           @Name("compound label") String compoundLabel,
                                                           @Name("reaction label") String reactionLabel,
                                                           @Name("reaction input relationship") String relTypeInbound,
                                                           @Name("reaction output relationship") String relTypeOutbound,
                                                           @Name("configuration options") Map <String, Object> configMap
                                                           ) {

        if (startNode == null) {
            throw new IllegalArgumentException("please provide a start node");
        }

        if (compoundLabel == null || compoundLabel.isEmpty()) {
            throw new IllegalArgumentException("please provide a compound label");
        }

        if (reactionLabel == null || reactionLabel.isEmpty()) {
            throw new IllegalArgumentException("please provide a reaction label");
        }

        Pair<RelationshipType, Direction> inbound = parseTypeAndDirection(relTypeInbound);
        Pair<RelationshipType, Direction> outbound = parseTypeAndDirection(relTypeOutbound);


        var config = new RouteConfig(compoundLabel, reactionLabel, outbound, configMap);

        var evaluator = new SynTreeEvaluator(log, config);

        var rootRouteLink = new RouteLink();

        var traversalDescription = tx.traversalDescription()
                .breadthFirst()
                .uniqueness(CustomUniqueness.RELATIONSHIPS_IN_PATH)
                .expand(
                        PathExpanders.forTypesAndDirections(
                                inbound.first(), inbound.other(),
                                outbound.first(), outbound.other())
                        , new InitialBranchState.State<>(rootRouteLink, null)
                )
                .evaluator(evaluator);

        runTraversal(startNode, traversalDescription);


        var routeLinks = evaluator.getRouteLinks();

        if (routeLinks.isEmpty() && (!rootRouteLink.getRelationships().isEmpty())) {
            routeLinks.add(rootRouteLink);
        }

        Map<Node, List<RouteLink>> groupedRouteLinks = RouteLinksGrouper.groupRouteLinks(routeLinks);

        System.out.println("===========================================================================================");
        System.out.println("printing out different route links here");
        groupedRouteLinks.forEach((node, links) -> {
            String nodeId = node.getProperty("uid").toString();
            System.out.println("Node ID: " + nodeId);
            links.forEach(r -> System.out.println("\t" + r.toString()));
        });

        // second part of the algorithm
        var theRoutes = RouteAssembler.assemble(groupedRouteLinks, startNode);

        for (NewRoute route : theRoutes) {
            System.out.println(route.toString());
        }

        return theRoutes.stream()
                .map(RelationshipsNewRouteResult::new);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void runTraversal(Node startNode, TraversalDescription traversalDescription) {
        for (Path ignored : traversalDescription.traverse(startNode)) {
            // need to iterate here to force the traversal API, but, we don't collect the paths as the
            // result will be in the evaluator.routes(), see below
        }
    }

    private Pair<RelationshipType, Direction> parseTypeAndDirection(String directionalType) {
        String type = directionalType;
        Direction direction = BOTH;
        if (directionalType.startsWith("<")) {
            type = directionalType.substring(1);
            direction = INCOMING;
        } else if (directionalType.endsWith(">")) {
            type = directionalType.substring(0, directionalType.length() - 1);
            direction = OUTGOING;
        }
        return Pair.of(RelationshipType.withName(type), direction);
    }



    public static class RelationshipsNewRouteResult {
        public final List<Relationship> relationships;

        public RelationshipsNewRouteResult(NewRoute route) {
            this.relationships = route.getRelationships();
        }
    }
}
