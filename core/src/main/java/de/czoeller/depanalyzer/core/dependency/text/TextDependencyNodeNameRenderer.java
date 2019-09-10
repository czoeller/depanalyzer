package de.czoeller.depanalyzer.core.dependency.text;

import com.google.common.base.Joiner;
import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import de.czoeller.depanalyzer.core.graph.NodeRenderer;
import org.apache.maven.artifact.Artifact;

public class TextDependencyNodeNameRenderer implements NodeRenderer<DependencyNode> {

    private static final Joiner SLASH_JOINER = Joiner.on("/").skipNulls();
    private static final Joiner COLON_JOINER = Joiner.on(":").skipNulls();

    private final boolean showGroupId;
    private final boolean showArtifactId;
    private final boolean showTypes;
    private final boolean showClassifiers;
    private final boolean showVersion;
    private final boolean showOptional;

    public TextDependencyNodeNameRenderer(boolean showGroupId, boolean showArtifactId, boolean showTypes, boolean showClassifiers, boolean showVersionsOnNodes, boolean showOptional) {
        this.showGroupId = showGroupId;
        this.showArtifactId = showArtifactId;
        this.showTypes = showTypes;
        this.showClassifiers = showClassifiers;
        this.showVersion = showVersionsOnNodes;
        this.showOptional = showOptional;
    }

    @Override
    public String render(DependencyNode node) {
        Artifact artifact = node.getArtifact();

        String artifactString = COLON_JOINER.join(
                this.showGroupId ? artifact.getGroupId() : null,
                this.showArtifactId ? artifact.getArtifactId() : null,
                this.showVersion ? node.getEffectiveVersion() : null,
                this.showTypes ? SLASH_JOINER.join(node.getTypes()) : null,
                this.showClassifiers ? SLASH_JOINER.join(node.getClassifiers()) : null,
                SLASH_JOINER.join(node.getScopes()));

        if (this.showOptional && artifact.isOptional()) {
            return artifactString + " (optional)";
        }

        return artifactString;
    }

}