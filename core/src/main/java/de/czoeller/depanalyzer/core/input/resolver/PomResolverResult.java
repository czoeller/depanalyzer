package de.czoeller.depanalyzer.core.input.resolver;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.core.graph.Edge;
import de.czoeller.depanalyzer.core.graph.Node;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class PomResolverResult {
    private DependencyNode rootNode;
    private final Map<String, Node<DependencyNode>> nodeDefinitions;
    private final Set<Edge> edges;

}
