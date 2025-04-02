package routes;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Paths;

public class ChemPathDescriptor implements Paths.PathDescriptor<Path> {

    @Override
    public String nodeRepresentation(Path path, Node node) {
        return node.getProperty("uid").toString();
    }

    @Override
    public String relationshipRepresentation(Path path, Node from, Relationship relationship) {
        return relationship.getEndNode().getProperty("uid") + "<-" + relationship.getStartNode().getProperty("uid");
        //relationship.getType().name() + "-";
    }

    public String relationshipRepresentation(Relationship r) {
        return relationshipRepresentation(null, null, r);
    }
}
