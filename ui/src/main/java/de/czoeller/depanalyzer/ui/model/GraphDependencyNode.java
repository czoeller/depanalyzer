package de.czoeller.depanalyzer.ui.model;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import lombok.*;
import org.apache.maven.artifact.Artifact;

import java.util.List;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@Data
public class GraphDependencyNode implements HasHeat, HasArtifact {
    private static long idCounter = 0;
    private final DependencyNode dependencyNode;
    @Setter
    private Integer primaryLevel = 0;
    @Getter
    private final Long id;

    public GraphDependencyNode(DependencyNode dependencyNode) {
        this.dependencyNode = dependencyNode;
        this.id = idCounter++;
    }

    @Override
    public double getHeat() {
        return dependencyNode.getIssues().size();
    }

    @Override
    public Artifact getArtifact() {
        return dependencyNode.getArtifact();
    }

    public List<Issue> getIssues() {
        return dependencyNode.getIssues();
    }
}
