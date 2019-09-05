package de.czoeller.depanalyzer.metamodel;

import lombok.Data;

@Data
public class Artifact {

    private final String artifactPath;
    private Version version;

    public Artifact(String artifactPath) {
        this.artifactPath = artifactPath;
    }
}
