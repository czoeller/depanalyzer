package de.czoeller.depanalyzer.core.dependency.text;

import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import de.czoeller.depanalyzer.core.graph.EdgeRenderer;

public class TextDependencyEdgeRenderer implements EdgeRenderer<DependencyNode> {

    private final boolean showVersions;

    public TextDependencyEdgeRenderer(boolean showVersionOnEdges) {
        this.showVersions = showVersionOnEdges;
    }

    @Override
    public String render(DependencyNode from, DependencyNode to) {
        switch (to.getResolution()) {
            case OMITTED_FOR_CONFLICT:
                String message = "omitted for conflict";
                if (this.showVersions) {
                    message += ": " + to.getArtifact().getVersion();
                }

                return message;

            case OMITTED_FOR_DUPLICATE:
                return "omitted for duplicate";

            default:
                return "";
        }
    }
}