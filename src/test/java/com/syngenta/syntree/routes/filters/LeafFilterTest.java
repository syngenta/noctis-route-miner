/*package com.syngenta.syntree.routes.filters;

import com.syngenta.syntree.routes.Route;
import com.syngenta.syntree.routes.TestUtils;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

import static com.syngenta.syntree.routes.TestUtils.mockPathWithDegree;
import static org.junit.jupiter.api.Assertions.*;

class LeafFilterTest {

    @Test
    void cTypeLabelMustBeProvided() {
        assertThrows(IllegalArgumentException.class, () -> new LeafFilter(null, Direction.BOTH, RelationshipType.withName("foo")));
    }

    @Test
    void directionMustBeProvided() {
        assertThrows(IllegalArgumentException.class, () -> new LeafFilter(Label.label("foo"),null, RelationshipType.withName("foo")));
    }

    @Test
    void typeMustBeProvided() {
        assertThrows(IllegalArgumentException.class, () -> new LeafFilter(Label.label("foo"), Direction.BOTH, null));
    }

    @Test
    void OnlyOtherRelationsReturnsTrue() {
        var cLabel = Label.label("C");
        var direction = Direction.INCOMING;
        var type = RelationshipType.withName("IN");
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new LeafFilter(cLabel, direction, type);
        var path = mockPathWithDegree(cLabel, type, direction, 0);

        var evaluation = filter.evaluate(path, branchState);

        assertTrue(evaluation);
    }

    @Test
    void ExistingRelationshipReturnsFalse() {
        var cLabel = Label.label("C");
        var direction = Direction.INCOMING;
        var type = RelationshipType.withName("IN");
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new LeafFilter(cLabel, direction, type);
        var path = mockPathWithDegree(cLabel, type, direction, 2);

        var evaluation = filter.evaluate(path, branchState);

        assertFalse(evaluation);
    }
}
*/