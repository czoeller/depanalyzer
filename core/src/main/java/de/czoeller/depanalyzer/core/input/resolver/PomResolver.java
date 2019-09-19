package de.czoeller.depanalyzer.core.input.resolver;

import java.io.File;

public interface PomResolver {
    PomResolverResult resolvePom(File pomFile);
}
