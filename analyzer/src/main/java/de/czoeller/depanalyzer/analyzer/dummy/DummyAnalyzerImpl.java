package de.czoeller.depanalyzer.analyzer.dummy;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.LOCIssue;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DummyAnalyzerImpl implements Analyzer {

    @Override
    public List<Issue> analyze(DependencyNode node) throws AnalyzerException {
        final File file = node.getArtifact().getFile();
        List<Issue> list = Lists.newArrayList();
        final int size;
        try {
            size = Iterators.size(FileUtils.lineIterator(file));
            list.add(new LOCIssue(size));
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not analyze", e);
        }
    }
}
