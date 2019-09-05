package de.czoeller.depanalyzer.input;

import de.czoeller.depanalyzer.metamodel.Dependency;
import de.czoeller.depanalyzer.metamodel.Artifact;

public interface InputParser {
    Dependency buildDependencyGraph(Artifact root);
}
