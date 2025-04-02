package com.syngenta.syntree.routes;

import com.syngenta.syntree.routes.filters.EvaluationFilter;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.PathEvaluator;
import org.neo4j.internal.helpers.collection.Iterables;
import org.neo4j.logging.Log;


import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashSet;
import java.util.Set;


public class SynTreeEvaluator implements PathEvaluator<RouteLink> {

    private final Log log;
    private final Collection<EvaluationFilter> filters;
    private final Set<RouteLink> routeLinks;
    private final RouteConfig routeConfig;

    public SynTreeEvaluator(Log log, RouteConfig routeConfig) {

        this.log = log;
        this.routeConfig = routeConfig;
        this.filters = routeConfig.getFilters();

        routeLinks = new HashSet<>();
    }

    @Override
    public Evaluation evaluate(Path path, BranchState<RouteLink> branchState) {

        var endNode = path.endNode();
        var state = branchState.getState();
        var lastRelationship = path.lastRelationship();


        if (endNode.hasLabel(routeConfig.getReactionLabel())) {
            if (lastRelationship != null) {
                // getOtherNde here points to a compoundLabel
                int degree = lastRelationship.getOtherNode(endNode).getDegree(
                        routeConfig.getOutboundRelationshipType(), routeConfig.getOutboundDirection());
                if (degree > 1) {
                    routeLinks.add(state);
                    var newLink = new RouteLink();
                    newLink.add(lastRelationship);
                    routeLinks.add(newLink);
                    ///System.out.println(Iterables.stream(path.nodes()).map(n -> n.getProperty("gid").toString()).collect(Collectors.joining("-")));
                    //System.out.println(newLink.toString());
                    branchState.setState(newLink);
                    ///System.out.println("REACTION or after");
                    //log.debug("new route %s added", newState.toString());
                } else {
                    state.add(lastRelationship);
                    ///System.out.println("REACTION and");
                }
            }
        } else if (endNode.hasLabel(routeConfig.getCompoundLabel())) {
            if (lastRelationship != null) {
                //System.out.println("COMPOUND before");
                state.add(lastRelationship);
                //System.out.println("COMPOUND after");
                //System.out.println("END 1");
            } else {
                routeLinks.add(state);
            }
            // nothing
        } else {
            throw new IllegalStateException("dunno know how to handle label " + endNode.getLabels());
        }


        // This part is inplace because the comparison of routeLinks to each other doesn't work correctly
        // And set routeLinks has duplicates
        // Should be removed
        routeLinks.removeIf(routeLink -> {
            Set<String> gids = routeLink.getRelationships().stream()
                                        .map(relationship -> Long.toString(relationship.getId()))
                                        .collect(Collectors.toSet());

            return routeLinks.stream()
                             .anyMatch(other -> !other.equals(routeLink) &&
                                                gids.equals(other.getRelationships().stream()
                                                                .map(relationship -> Long.toString(relationship.getId()))
                                                                .collect(Collectors.toSet())));
        });

        routeLinks.removeIf(routeLink -> routeLink.getRelationships().isEmpty());


        return getEvaluation(path, branchState);
    }


    public Collection<RouteLink> getRouteLinks() {
        return routeLinks;
    }

    // package visibility for testing

    Evaluation getEvaluation(Path path, BranchState<RouteLink> branchState) {

        for (EvaluationFilter filter : filters) {
            if (filter.evaluate(path, branchState)) {
                return Evaluation.EXCLUDE_AND_PRUNE;
            }
        }



        return Evaluation.EXCLUDE_AND_CONTINUE;
    }


    @Override
    public Evaluation evaluate(Path path) {
        throw new IllegalStateException("evaluation without branch state not supported");
    }
}
