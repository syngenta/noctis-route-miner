/*package com.syngenta.syntree.routes.filters;

import com.syngenta.syntree.routes.Route;
import com.syngenta.syntree.routes.TestUtils;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import static org.junit.jupiter.api.Assertions.*;

class PropertyExistsFilterTest {

    @Test
    void rTypeLabelMustBeProvided() {
        assertThrows(IllegalArgumentException.class, () -> new PropertyExistsFilter(null, "foo"));
    }

    @Test
    void variableNameMustBeProvided() {
        assertThrows(IllegalArgumentException.class, () -> new PropertyExistsFilter(Label.label("C"), null));
        assertThrows(IllegalArgumentException.class, () -> new PropertyExistsFilter(Label.label("C"), ""));
    }

    @Test
    void returnsTrueWhenPropertyExists() {

        var propertyName = "foo";
        var label = Label.label("R");
        var path = TestUtils.mockPathWithProperty(label, propertyName);
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new PropertyExistsFilter(label, propertyName);

        var evaluation = filter.evaluate(path, branchState);

        assertTrue(evaluation);
    }

    @Test
    void returnsFalseWhenPropertyDoesNotExists() {

        var propertyName = "foo";
        var label = Label.label("R");
        var path = TestUtils.mockPathWithProperty(label, "other");
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new PropertyExistsFilter(label, propertyName);

        var evaluation = filter.evaluate(path, branchState);

        assertFalse(evaluation);
    }

}
*/