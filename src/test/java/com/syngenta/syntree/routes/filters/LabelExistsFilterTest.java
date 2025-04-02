/*package com.syngenta.syntree.routes.filters;

import com.syngenta.syntree.routes.Route;
import com.syngenta.syntree.routes.TestUtils;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import static org.junit.jupiter.api.Assertions.*;

class LabelExistsFilterTest {

    @Test
    void rTypeLabelMustBeProvided() {
        assertThrows(IllegalArgumentException.class, () -> new LabelExistsFilter(null, Label.label("foo")));
    }

    @Test
    void TerminationLabelMustBeProvided() {
        assertThrows(IllegalArgumentException.class, () -> new LabelExistsFilter(Label.label("C"), null));
    }

    @Test
    void returnsTrueWhenLabelExists() {

        var terminationLabel = Label.label("foo");
        var label = Label.label("R");
        var path = TestUtils.mockPathWithLabel(label, terminationLabel);
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new LabelExistsFilter(label, terminationLabel);

        var evaluation = filter.evaluate(path, branchState);

        assertTrue(evaluation);
    }

    @Test
    void returnsFalseWhenLabelDoesNotExists() {

        var label = Label.label("R");
        var path = TestUtils.mockPathWithProperty(label, "dummyProp");
        var state = new Route();
        var branchState = TestUtils.mockState(state);
        var filter = new LabelExistsFilter(label, Label.label("other"));

        var evaluation = filter.evaluate(path, branchState);

        assertFalse(evaluation);
    }

}
*/