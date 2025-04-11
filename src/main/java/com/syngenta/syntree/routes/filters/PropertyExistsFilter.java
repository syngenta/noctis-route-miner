package com.syngenta.syntree.routes.filters;

import com.syngenta.syntree.routes.RouteLink;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.BranchState;

/**
 * Terminates pathfinding for the current path and includes the path when a node with the configured property is encountered.
 */

public class PropertyExistsFilter implements EvaluationFilter {

    private final Label cTypeLabel;
    private final String propertyName;

    public PropertyExistsFilter(Label cTypeLabel, String propertyName) {

        if (cTypeLabel == null) {
            throw new IllegalArgumentException("you must provide a C type label");
        }
        if (propertyName == null || propertyName.isEmpty()) {
            throw new IllegalArgumentException("you must provide a property name");
        }
        this.cTypeLabel = cTypeLabel;
        this.propertyName = propertyName;
    }

    @Override
    public boolean evaluate(Path path, BranchState<RouteLink> state) {

        return path.endNode().hasLabel(cTypeLabel) && path.endNode().hasProperty(propertyName);
    }

    @Override
    public String toString() {
        return String.format("PropertyExistsFilter, stopping and including at nodes with Label %s and property %s",
                cTypeLabel, propertyName);
    }
}
