package de.czoeller.depanalyzer.core.input.resolver;

import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.internal.test.util.NodeBuilder;

public class AetherUtils {

    public static DependencyNode getDependencyNode(String group, String artifact, String version) {
        NodeBuilder builder = new NodeBuilder();
        builder.groupId(group);
        builder.artifactId(artifact);
        builder.version(version);
        return builder.build();
    }

    AetherUtils(){}
}
