package com.syngenta.syntree.routes.filters;

import com.syngenta.syntree.routes.RouteLink;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.BranchState;

/**
 * Filters to be used by the Evaluator to decide if to accept the node and expand from here.
 */
public interface EvaluationFilter {

    /**
     * Evaluate the current node (represented by path.endNode() ) and signal if mode should be considered and expand from.
     * The evaluator must loop over all configured filters and stop when the first one returns {@code false}
     * @param path Path to the current node in the traversal.
     * @param state current branchState
     * @return {@code true} if evaluation of the path should take place and expansion from here is allowed.
     * {@code false} if traversal must stop at this path.
     */
    boolean evaluate(Path path, BranchState<RouteLink> state);

}
