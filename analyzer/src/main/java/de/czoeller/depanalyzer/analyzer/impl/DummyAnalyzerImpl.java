package de.czoeller.depanalyzer.analyzer.impl;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.LOCIssue;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyAnalyzerImpl implements Analyzer {

    @Override
    public Map<Artifact, List<Issue>> analyze(Artifact artifact) {
        final File file = new File(artifact.getArtifactPath());
        val map = new HashMap<Artifact, List<Issue>>();
        final int size;
        try {
            size = Iterators.size(FileUtils.lineIterator(file));
            map.put(artifact, Lists.newArrayList(new LOCIssue(size)));
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not analyze", e);
        }
    }
}
