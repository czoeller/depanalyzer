package de.czoeller.depanalyzer.ui.model;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.maven.artifact.Artifact;

import java.util.Random;

@Data
@EqualsAndHashCode(of = "id")
public class GraphDependencyNode implements HasHeat, HasArtifact {
    private static long idCounter = 0;
    private final DependencyNode dependencyNode;
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
