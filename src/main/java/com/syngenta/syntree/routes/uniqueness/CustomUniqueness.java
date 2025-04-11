package com.syngenta.syntree.routes.uniqueness;

import org.neo4j.graphdb.traversal.TraversalBranch;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.graphdb.traversal.UniquenessFactory;
import org.neo4j.graphdb.traversal.UniquenessFilter;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;

import java.util.HashSet;
import java.util.Set;

// Implemented uniqueness of relationships within a path
// to avoid getting stuck in cycles
// Desired behavior: if the start node of the relationship is already in the path, this is the last relationship
// to be added to the path and this node shouldn't be explored further. the node is treated as an end node
// for this link.
// Current behaviour, it continues the traversal after this node following other possible relationships
// which are not yet part of the path
public enum CustomUniqueness implements UniquenessFactory {
    NONE(Uniqueness.NONE),
    RELATIONSHIPS_IN_PATH {
        @Override
        public UniquenessFilter create(Object optionalParameter) {
            return new RelationshipInPathUniquenessFilter();
        }

        @Override
        public boolean eagerStartBranches() {
            return true;
        }
    };

    private final Uniqueness existingUniqueness;

    CustomUniqueness(Uniqueness existingUniqueness) {
        this.existingUniqueness = existingUniqueness;
    }

    CustomUniqueness() {
        this.existingUniqueness = null;
    }

    @Override
    public UniquenessFilter create(Object object) {
        if (existingUniqueness != null) {
            return existingUniqueness.create(object);
        }
        throw new UnsupportedOperationException("Custom implementation is required");
    }

    @Override
    public boolean eagerStartBranches() {
        if (existingUniqueness != null) {
            return existingUniqueness.eagerStartBranches();
        }
        return false;
    }

    private static class RelationshipInPathUniquenessFilter implements UniquenessFilter {
        @Override
        public boolean checkFirst(TraversalBranch branch) {
            return true;
        }

        @Override
        public boolean check(TraversalBranch branch) {
            Set<Relationship>  seenRelationships = new HashSet<>();
            for (Relationship relationship : branch.relationships()) {
                if (seenRelationships.contains(relationship)) {
                    return false;
                }
                seenRelationships.add(relationship);
            }
            return true;
        }

    }
}