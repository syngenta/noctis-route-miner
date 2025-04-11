package com.syngenta.syntree.routes.filters;

import com.syngenta.syntree.routes.RouteLink;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.stream.StreamSupport;

/**
 * Counts the number of times a Node of the given label has been on the path.
 * Uses the {@link Route} to pass that information down the path.
 * The {@code MaxHopsFilter.evaluation()} returns {code EXCLUDE_AND_CONTINUE} unless {@code maxRTypeHops}
 * have been reached, in which case it returns {@code EXCLUDE_AND_PRUNE}.
 * The number of times {@code maxRTypeHops} has been reached is stored and can be retrieved
 * via {@code MaxHopsFilter:getMaxRTypeHopsAborts}
 */
public class MaxHopsFilter implements EvaluationFilter {

    private final Label rTypeLabel;
    private final Label cTypeLabel;
    private final long maxRTypeHops;
    private long maxRTypeHopsAborts = 0;

    public MaxHopsFilter(Label rTypeLabel, Label cTypeLabel, long maxRTypeHops) {
        if (rTypeLabel == null) {
            throw new IllegalArgumentException("you must provide a R type label");
        }

        this.rTypeLabel = rTypeLabel;
        this.cTypeLabel = cTypeLabel;
        this.maxRTypeHops = maxRTypeHops;
    }

    @Override
    public boolean evaluate(Path path, BranchState<RouteLink> branchState) {
        if (path.endNode().hasLabel(cTypeLabel)) {
            var state = branchState.getState();

            if (countRTypeInPath(path) >= maxRTypeHops) {
                maxRTypeHopsAborts++;
                state.setWasCanceled(true);
                return true;
            }
        }
        return false;
    }

    public long getMaxRTypeHopsAborts() {
        return maxRTypeHopsAborts;
    }

    @Override
    public String toString() {
        return String.format("MaxHopsFilter, stopping and excluding at nodes with Label %s and %d number of hops",
                rTypeLabel, maxRTypeHops);
    }


    private long countRTypeInPath(Path path) {

        return StreamSupport.stream(path.nodes().spliterator(),false)
                .filter(n -> n.hasLabel(rTypeLabel))
                .count();
    }

}
