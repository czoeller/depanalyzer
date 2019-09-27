package de.czoeller.depanalyzer.core.dependency.text;

import de.czoeller.depanalyzer.core.dependency.AbstractGraphStyleConfigurer;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.core.dependency.GraphStyleConfigurer;
import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.core.graph.text.TextGraphFormatter;

public class TextGraphStyleConfigurer extends AbstractGraphStyleConfigurer {

    boolean repeatTransitiveDependencies;

    @Override
    public GraphStyleConfigurer repeatTransitiveDependencies(boolean repeatTransitiveDependencies) {
        this.repeatTransitiveDependencies = repeatTransitiveDependencies;
        return this;
    }

    @Override
    public GraphBuilder<DependencyNode> configure(GraphBuilder<DependencyNode> graphBuilder) {
        return graphBuilder
                .useNodeNameRenderer(new TextDependencyNodeNameRenderer(this.showGroupId, this.showArtifactId, this.showTypes, this.showClassifiers, this.showVersionsOnNodes, this.showOptional))
                .useEdgeRenderer(new TextDependencyEdgeRenderer(this.showVersionOnEdges))
                .graphFormatter(new TextGraphFormatter(this.repeatTransitiveDependencies));
    }
}
