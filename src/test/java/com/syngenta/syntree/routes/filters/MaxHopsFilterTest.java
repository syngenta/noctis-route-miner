/*package com.syngenta.syntree.routes.filters;

import com.syngenta.syntree.routes.Route;
import com.syngenta.syntree.routes.TestUtils;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import static org.junit.jupiter.api.Assertions.*;

class MaxHopsFilterTest {

    @Test
    void rTypeLabelMustBeProvided() {

        assertThrows(IllegalArgumentException.class, () -> new MaxHopsFilter(null, 2));
    }

    @Test
    void correctLabelIncreasesCounter() {
        var label = Label.label("R");
        var path = TestUtils.mockPath(label);
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new MaxHopsFilter(label, 2);

        var evaluation = filter.evaluate(path, branchState);

        assertFalse(evaluation);
        assertEquals(0, filter.getMaxRTypeHopsAborts());
    }

    @Test
    void otherLabelsDoNotIncreaseCounter() {
        var rLabel = Label.label("R");
        var otherLabel = Label.label("other");
        var path = TestUtils.mockPath(otherLabel);
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new MaxHopsFilter(rLabel, 2);

        var evaluation = filter.evaluate(path, branchState);

        assertFalse(evaluation);
        assertEquals(0, filter.getMaxRTypeHopsAborts());
    }
}
*/