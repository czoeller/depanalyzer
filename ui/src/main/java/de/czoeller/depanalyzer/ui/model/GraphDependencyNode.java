package de.czoeller.depanalyzer.ui.model;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import lombok.*;
import org.apache.maven.artifact.Artifact;

import java.util.Random;

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
        return new Random().nextDouble() * 100;
    }

    @Override
    public Artifact getArtifact() {
        return dependencyNode.getArtifact();
    }
}
