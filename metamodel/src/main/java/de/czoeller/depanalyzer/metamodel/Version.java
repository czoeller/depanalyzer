package de.czoeller.depanalyzer.metamodel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Version {
    private String groupId;
    private String artifactId;
    private String version;

    @Override
    public String toString() {
        return String.format("%s:%s:%s", getGroupId(), getArtifactId(), getVersion());
    }

}
