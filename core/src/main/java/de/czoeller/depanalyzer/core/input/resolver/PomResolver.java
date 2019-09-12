package de.czoeller.depanalyzer.core.input.resolver;

import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import lombok.Data;

import java.io.File;

public interface PomResolver {
    DependencyNode resolvePom(File pomFile);
    PomResolverResult resolvePomExperimental(File pomFile);
}
