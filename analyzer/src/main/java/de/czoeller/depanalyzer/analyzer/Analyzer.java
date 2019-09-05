package de.czoeller.depanalyzer.analyzer;

import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Issue;

import java.util.List;
import java.util.Map;

public interface Analyzer {
    Map<Artifact, List<Issue>> analyze(Artifact artifact);
}
