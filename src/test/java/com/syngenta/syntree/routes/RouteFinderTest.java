package com.syngenta.syntree.routes;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.extension.Neo4jExtension;
import org.neo4j.internal.helpers.collection.Iterators;
import routes.ChemPathDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RouteFinderTest {

    @RegisterExtension
    static Neo4jExtension neo4j = Neo4jExtension.builder()
            .withDisabledServer()
            .withProcedure(RouteFinder.class)
            .withFixture(db -> {
                try {

                    InputStream importFile = RouteFinderTest.class.getClassLoader()
                            .getResourceAsStream("sampleData2graphs.cyper");

                    String allCmds = IOUtils.toString(importFile, Charset.defaultCharset());
                    for (String cmd : allCmds.split(";")) {
                        cmd = cmd.trim();
                        if (cmd.startsWith("//")) {
                            continue;
                        }
                        if (!cmd.isEmpty()) {
                            db.executeTransactionally(cmd);
                        }
                    }
                    return null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .build();

    private final ChemPathDescriptor converter = new ChemPathDescriptor();

    private static final String GRAPH1_CALL = "CALL noctis.RouteMiner(start, 'C', 'R', '<INP', '<OUT', {}) YIELD relationships RETURN relationships";
    private static final String GRAPH2_CALL = "CALL noctis.RouteMiner(start, 'MMM', 'CCC', '<REACTANT', '<PRODUCT', {}) YIELD relationships RETURN relationships";


    @Test
    void graph4FromC1(GraphDatabaseService db) {
        assertRoutes(db, "MATCH (start:MMM {uid:1}) " + GRAPH2_CALL ,
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-109","109<-11","4<-106","106<-8","8<-111","111<-13"},
            new String[]{"1<-102","102<-3","102<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-109","109<-11"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-108","108<-10","4<-105","105<-7","105<-6","7<-103","103<-3","103<-2","3<-107","107<-9"},
            new String[]{"1<-101","101<-5","101<-2","2<-107","107<-9","5<-113","113<-14","9<-109","109<-11"},
            new String[]{"1<-102","102<-3","102<-2","3<-107","107<-9","2<-107","107<-9","9<-108","108<-10"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-107","107<-9","9<-109","109<-11","4<-106","106<-8","8<-110","110<-12"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-107","107<-9","9<-108","108<-10","4<-106","106<-8","8<-110","110<-12"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-107","107<-9","9<-108","108<-10","4<-106","106<-8","8<-111","111<-13"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-109","109<-11","4<-105","105<-7","105<-6","7<-103","103<-3","103<-2","3<-107","107<-9"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-108","108<-10","4<-106","106<-8","8<-111","111<-13"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-109","109<-11","4<-105","105<-7","105<-6","7<-103","103<-3","103<-2","3<-107","107<-9"},
            new String[]{"1<-102","102<-3","102<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-108","108<-10"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-109","109<-11","4<-106","106<-8","8<-110","110<-12"},
            new String[]{"1<-101","101<-5","101<-2","2<-104","104<-5","5<-113","113<-14"},
            new String[]{"1<-101","101<-5","101<-2","2<-107","107<-9","5<-112","112<-2","9<-109","109<-11"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-108","108<-10","4<-105","105<-7","105<-6","7<-103","103<-3","103<-2","3<-107","107<-9"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-107","107<-9","9<-108","108<-10","4<-105","105<-7","105<-6","7<-103","103<-3","103<-2","3<-107","107<-9"},
            new String[]{"1<-101","101<-5","101<-2","2<-107","107<-9","5<-113","113<-14","9<-108","108<-10"},
            new String[]{"1<-102","102<-3","102<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-108","108<-10"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-107","107<-9","9<-109","109<-11","4<-105","105<-7","105<-6","7<-103","103<-3","103<-2","3<-107","107<-9"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-108","108<-10","4<-106","106<-8","8<-110","110<-12"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-107","107<-9","9<-109","109<-11","4<-106","106<-8","8<-111","111<-13"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-108","108<-10","4<-106","106<-8","8<-110","110<-12"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-108","108<-10","4<-106","106<-8","8<-111","111<-13"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-109","109<-11","4<-106","106<-8","8<-110","110<-12"},
            new String[]{"1<-101","101<-5","101<-2","2<-104","104<-5","5<-112","112<-2"},
            new String[]{"1<-102","102<-3","102<-2","3<-107","107<-9","2<-107","107<-9","9<-109","109<-11"},
            new String[]{"1<-103","103<-4","103<-3","103<-2","3<-107","107<-9","2<-104","104<-5","5<-112","112<-2","9<-109","109<-11","4<-106","106<-8","8<-111","111<-13"},
            new String[]{"1<-102","102<-3","102<-2","3<-107","107<-9","2<-104","104<-5","5<-113","113<-14","9<-109","109<-11"},
            new String[]{"1<-101","101<-5","101<-2","2<-107","107<-9","5<-112","112<-2","9<-108","108<-10"}
        );
    }


    @Test
    void graph1FromC1(GraphDatabaseService db) {
        assertRoutes(db, "MATCH (start:C {uid:1}) " + GRAPH1_CALL ,
                new String[]{"1<-101", "101<-2", "2<-104", "104<-5"},
                new String[]{"1<-102", "102<-3", "102<-2", "2<-104", "104<-5"},

                new String[]{"1<-103", "103<-2", "2<-104", "104<-5", "103<-3", "103<-4", "4<-106", "106<-8"},
                new String[]{"1<-103", "103<-2", "2<-104", "104<-5", "103<-3", "103<-4", "4<-105", "105<-7", "105<-6"}
        );
    }

    @Test
    void graph1FromC1WithStopPropertyAtC4(GraphDatabaseService db) {
        assertRoutes(db, "MATCH (start:C {uid:1}) "
                        + "CALL noctis.RouteMiner(start, 'C', 'R', '<INP', '<OUT', {nodeStopProperty:'stop'}) " +
                        "YIELD relationships RETURN relationships" ,
                new String[]{"1<-101", "101<-2", "2<-104", "104<-5"},
                new String[]{"1<-102", "102<-3", "102<-2", "2<-104", "104<-5"},

                new String[]{"1<-103", "103<-2", "2<-104", "104<-5", "103<-3", "103<-4" }
        );
    }


    @Test
    void graph1FromC1WithMaxR(GraphDatabaseService db) {
        assertRoutes(db, "MATCH (start:C {uid:1}) "
                        + "CALL noctis.RouteMiner(start, 'C', 'R', '<INP', '<OUT', {maxNumberReactions:1}) " +
                        "YIELD relationships RETURN relationships" ,
                new String[]{"1<-101", "101<-2"},
                new String[]{"1<-102", "102<-3", "102<-2"},
                new String[]{"1<-103", "103<-2",  "103<-3", "103<-4"}
        );
    }


    private void assertRoutes(GraphDatabaseService db, String cypher, String[]... routes) {
        try (Transaction tx = db.beginTx()) {

            List<List<String>> rels = tx.execute(cypher).columnAs("relationships")
                    .stream()
                    .map(o -> ((List<Relationship>) o).stream()
                            .map(converter::relationshipRepresentation).collect(Collectors.toList()))
                    .collect(Collectors.toList());

            assertEquals(routes.length, rels.size());
            ListAssert<List<String>> listListAssert = assertThat(rels);

            for (String[] route : routes) {
                listListAssert.anySatisfy(strings -> assertThat(strings).containsExactlyInAnyOrder(route));
            }
            tx.commit();
        }
    }

}
