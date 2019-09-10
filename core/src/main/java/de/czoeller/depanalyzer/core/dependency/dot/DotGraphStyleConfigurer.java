
package de.czoeller.depanalyzer.core.dependency.dot;


import de.czoeller.depanalyzer.core.dependency.AbstractGraphStyleConfigurer;
import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import de.czoeller.depanalyzer.core.dependency.dot.style.StyleConfiguration;
import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import de.czoeller.depanalyzer.core.graph.dot.DotGraphFormatter;

public class DotGraphStyleConfigurer extends AbstractGraphStyleConfigurer {

  private final StyleConfiguration styleConfiguration;

  public DotGraphStyleConfigurer(StyleConfiguration styleConfiguration) {
    this.styleConfiguration = styleConfiguration;
  }

  @Override
  public GraphBuilder<DependencyNode> configure(GraphBuilder<DependencyNode> graphBuilder) {
    DotDependencyNodeNameRenderer nodeNameRenderer = new DotDependencyNodeNameRenderer(this.showGroupId, this.showArtifactId, this.showTypes, this.showClassifiers, this.showVersionsOnNodes, this.showOptional, this.styleConfiguration);
    DotDependencyEdgeRenderer edgeRenderer = new DotDependencyEdgeRenderer(this.showVersionOnEdges, this.styleConfiguration);

    return graphBuilder
        .graphFormatter(new DotGraphFormatter(this.styleConfiguration.graphAttributes(), this.styleConfiguration.defaultNodeAttributes(), this.styleConfiguration.defaultEdgeAttributes()))
        .useNodeNameRenderer(nodeNameRenderer)
        .useEdgeRenderer(edgeRenderer);
  }
}
