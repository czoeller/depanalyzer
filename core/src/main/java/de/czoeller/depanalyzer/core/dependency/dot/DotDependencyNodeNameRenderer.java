
package de.czoeller.depanalyzer.core.dependency.dot;

import com.google.common.base.Joiner;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.core.dependency.dot.style.StyleConfiguration;
import de.czoeller.depanalyzer.core.dependency.dot.style.StyleKey;
import de.czoeller.depanalyzer.core.graph.NodeRenderer;
import org.apache.maven.artifact.Artifact;

import java.util.LinkedHashSet;
import java.util.Set;


public class DotDependencyNodeNameRenderer implements NodeRenderer<DependencyNode> {

  private static final Joiner SLASH_JOINER = Joiner.on("/").skipNulls();

  private final boolean showGroupId;
  private final boolean showArtifactId;
  private final boolean showTypes;
  private final boolean showClassifiers;
  private final boolean showVersion;
  private final boolean showOptional;
  private final StyleConfiguration styleConfiguration;

  public DotDependencyNodeNameRenderer(boolean showGroupId, boolean showArtifactId, boolean showTypes, boolean showClassifiers, boolean showVersion, boolean showOptional, StyleConfiguration styleConfiguration) {
    this.showGroupId = showGroupId;
    this.showArtifactId = showArtifactId;
    this.showTypes = showTypes;
    this.showClassifiers = showClassifiers;
    this.showVersion = showVersion;
    this.showOptional = showOptional;
    this.styleConfiguration = styleConfiguration;
  }


  @Override
  public String render(DependencyNode node) {
    Artifact artifact = node.getArtifact();
    String scopes = createScopeString(node.getScopes());
    String types = createTypeString(node.getTypes());
    String classifiers = createClassifierString(node.getClassifiers());

    String effectiveScope = node.getEffectiveScope();
    StyleKey styleKey = StyleKey.create(artifact.getGroupId(), artifact.getArtifactId(), effectiveScope, artifact.getType(), node.getEffectiveVersion(), classifiers, artifact.isOptional());

    return this.styleConfiguration.nodeAttributes(
        styleKey,
        this.showGroupId ? artifact.getGroupId() : null,
        this.showArtifactId ? artifact.getArtifactId() : null,
        this.showVersion ? node.getEffectiveVersion() : null,
        this.showOptional && artifact.isOptional(),
        this.showTypes ? types : null,
        this.showClassifiers ? classifiers : null,
        scopes
    ).toString();
  }

  private static String createScopeString(Set<String> scopes) {
    if (scopes.size() > 1 || !scopes.contains("compile")) {
      return "(" + SLASH_JOINER.join(scopes) + ")";
    }

    return "";
  }

  private static String createTypeString(Set<String> types) {
    if (types.size() > 1 || !types.contains("jar")) {
      Set<String> typesToDisplay = new LinkedHashSet<>(types.size());
      for (String type : types) {
        typesToDisplay.add("." + type);
      }

      return SLASH_JOINER.join(typesToDisplay);
    }

    return "";
  }

  private static String createClassifierString(Set<String> classifiers) {
    return SLASH_JOINER.join(classifiers);
  }

}
