package com.syngenta.syntree.routes;

import com.syngenta.syntree.routes.filters.EvaluationFilter;
import com.syngenta.syntree.routes.filters.MaxHopsFilter;
import com.syngenta.syntree.routes.filters.PropertyExistsFilter;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.internal.helpers.collection.Pair;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RouteConfig {

    public static final String MAX_R_HOPS = "maxNumberReactions";
    public static final String PROPERTY_NAME = "nodeStopProperty";

    private final Label compoundLabel;
    private final Label reactionLabel;
    // Not all filters are harmonized with new state RouteLink
    private final Collection<EvaluationFilter> filters;
    // this is the type of the relation r in (:compoundLabel)-[r]-(:reactionLabel)
    private final RelationshipType relationshipType;

    // this is the direction of the relation r in (:compoundLabel)-[r]-(:reactionLabel) as seen from compoundLabel
    private final Direction direction;

    public RouteConfig(String compoundLabel, String reactionLabel,
                       Pair<RelationshipType, Direction> relTypeCFromR, Map<String, Object> config) {

        this.compoundLabel = Label.label(compoundLabel);
        this.reactionLabel = Label.label(reactionLabel);

        direction = relTypeCFromR.other();
        relationshipType = relTypeCFromR.first();

        filters = new ArrayList<>();



        var maxR = config.get(MAX_R_HOPS);
        // This part should be returned back
        // It is commented because Filters take Route as an input in evaluate
        // Should be harmonized

        if (maxR != null) {
            if (maxR instanceof Long ) {
                filters.add(new MaxHopsFilter(this.reactionLabel, this.compoundLabel,(Long)maxR));
            } else {
                throw new IllegalArgumentException(MAX_R_HOPS + " must be of type long");
            }
        }

        if (config.containsKey(PROPERTY_NAME)) {
            var propertyName = config.get(PROPERTY_NAME);
            if(propertyName instanceof String) {
                filters.add(new PropertyExistsFilter(this.compoundLabel, (String)propertyName));
            } else {
                throw new IllegalArgumentException(PROPERTY_NAME + " must be of type String");
            }
        }



    }


    public Collection<EvaluationFilter> getFilters() {
        return Collections.unmodifiableCollection(filters);
    }

    public Label getCompoundLabel() {
        return compoundLabel;
    }

    public Label getReactionLabel() {
        return reactionLabel;
    }

    public Direction getOutboundDirection() {
        return direction;
    }

    public RelationshipType getOutboundRelationshipType() {
        return relationshipType;
    }
}
