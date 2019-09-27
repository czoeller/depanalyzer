
package de.czoeller.depanalyzer.core.dependency.dot;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.NodeResolution;
import de.czoeller.depanalyzer.core.dependency.dot.style.StyleConfiguration;
import de.czoeller.depanalyzer.core.graph.EdgeRenderer;
import de.czoeller.depanalyzer.core.graph.dot.DotAttributeBuilder;

import static de.czoeller.depanalyzer.core.dependency.VersionAbbreviator.abbreviateVersion;


public class DotDependencyEdgeRenderer implements EdgeRenderer<DependencyNode> {

  private final boolean renderVersions;
  private final StyleConfiguration styleConfiguration;

  public DotDependencyEdgeRenderer(boolean renderVersions, StyleConfiguration styleConfiguration) {
    this.renderVersions = renderVersions;
    this.styleConfiguration = styleConfiguration;
  }

  @Override
  public String render(DependencyNode from, DependencyNode to) {
    NodeResolution fromResolution = from.getResolution();
    NodeResolution toResolution = to.getResolution();

    DotAttributeBuilder builder = this.styleConfiguration.edgeAttributes(fromResolution, toResolution, to.getEffectiveScope(), from.getArtifact(), to.getArtifact());
    if (toResolution == NodeResolution.OMITTED_FOR_CONFLICT && this.renderVersions) {
      builder.label(abbreviateVersion(to.getArtifact().getVersion()));
    }

    return builder.toString();
  }

}
