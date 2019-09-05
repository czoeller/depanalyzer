package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
public class Dependency {

    private List<Dependency> children;
    private Artifact artifact;

    public Dependency() {
        this.children = new ArrayList<>();
    }

    public Dependency(Artifact artifact) {
        this();
        this.artifact = artifact;
    }

    public Stream<Dependency> flattened() {
        return Stream.concat(
                Stream.of(this),
                children.stream().flatMap(Dependency::flattened));
    }

}
