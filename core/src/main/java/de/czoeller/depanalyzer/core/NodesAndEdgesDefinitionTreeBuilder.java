package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.core.graph.Edge;
import de.czoeller.depanalyzer.core.graph.Node;
import de.czoeller.depanalyzer.metamodel.DependencyNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class NodesAndEdgesDefinitionTreeBuilder {

    private final Map<String, Node<DependencyNode>> nodeDefinitions;
    private final Set<Edge> edges;
    private DependencyNode rootNode;

    public NodesAndEdgesDefinitionTreeBuilder(Map<String, Node<DependencyNode>> nodeDefinitions, Set<Edge> edges) {
        this.nodeDefinitions = nodeDefinitions;
        this.edges = edges;
    }

    public DependencyNode build() {
        rootNode = nodeDefinitions.values().stream().map(n -> n.nodeObject).findFirst().orElseThrow(() -> new IllegalStateException("No root found"));
        //rootNode.flattened().sequential().forEach(n -> n.getChildren().clear());

        for (Map.Entry<String, Node<DependencyNode>> es : nodeDefinitions.entrySet()) {
            final DependencyNode nodeOrCreate = findNodeOrCreate(es.getValue().nodeObject);
            setChildren(nodeOrCreate, es.getKey());
        }

        return rootNode;
    }

    private void setChildren(DependencyNode node, String key) {
        node.getChildren().clear();
        node.getChildren().addAll(getTargetNodesForEdge(key));
    }

    private List<DependencyNode> getTargetNodesForEdge(String key) {
        return edges.stream().filter(k -> k.getFromNodeId().equals(key)).map(e -> nodeDefinitions.get(e.getToNodeId()).nodeObject).collect(Collectors.toList());
    }

    private DependencyNode findNodeOrCreate(DependencyNode node) {
        return rootNode.flattened()
                    .filter(Objects::nonNull)
                    .filter(n -> n.getArtifact().toString().equals(node.getArtifact().toString()))
                    .findFirst()
                    .orElseGet(() -> {
                        final String key = node.getArtifact().getGroupId() + ":" + node.getArtifact().getArtifactId() + ":" + node.getArtifact().getType() + ":" + node.getArtifact().getScope();
                        if(!nodeDefinitions.containsKey(key)) {
                            throw new IllegalStateException("No node definition for " + key);
                        }
                        final Node<DependencyNode> dependencyNodeNode = nodeDefinitions.get(key);
                        return dependencyNodeNode.nodeObject;
                    });
    }

}
