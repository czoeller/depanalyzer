package de.czoeller.depanalyzer.ui.model;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = {"edge"})
public class GraphDependencyEdge {
    private String edge;
    private int level = 0;
    private DependencyNode source;
    private DependencyNode target;
    private boolean primary;

    /** Creates a new instance of ArtifactGraphEdge */
    public GraphDependencyEdge(DependencyNode source, DependencyNode target) {
        edge = source.getArtifact().getArtifactId() + "--" + target.getArtifact().getArtifactId(); //NOI18N
        this.target = target;
        this.source = source;
    }

    @Override
    public String toString() {
        return edge;
    }

    public void setLevel(int lvl) {
        level = lvl;
    }

    public int getLevel() {
        return level;
    }

    public void setPrimaryPath(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary;
    }

    public DependencyNode getSource() {
        return source;
    }

    public DependencyNode getTarget() {
        return target;
    }
}
