package de.czoeller.depanalyzer.analyzer.dummy;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.LOCIssue;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DummyAnalyzerImpl extends BaseAnalyzer {

    /**
     * Required to obtain instance reflective.
     * TODO: remove reflective instantiation
     */
    public DummyAnalyzerImpl() {}

    public DummyAnalyzerImpl(AnalyzerContext context) {
        super(context);
    }

    @Override
    public Analyzers getType() {
        return Analyzers.DUMMY;
    }

    @Override
    public List<Issue> analyze(DependencyNode node) throws AnalyzerException {
        final File file = node.getArtifact().getFile();
        List<Issue> issues = Lists.newArrayList();
        final int size;
        try {
            size = Iterators.size(FileUtils.lineIterator(file));
            issues.add(new LOCIssue(Issue.Severity.LOW, "loc issue", size));
            return issues;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not analyze", e);
        }
    }
}
