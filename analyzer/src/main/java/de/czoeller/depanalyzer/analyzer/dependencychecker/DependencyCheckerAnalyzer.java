package de.czoeller.depanalyzer.analyzer.dependencychecker;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.metamodel.CVEIssue;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.data.nvdcve.DatabaseException;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Vulnerability;
import org.owasp.dependencycheck.exception.ExceptionCollection;
import org.owasp.dependencycheck.exception.ReportException;
import org.owasp.dependencycheck.utils.Settings;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DependencyCheckerAnalyzer extends BaseAnalyzer {

    private final Settings settings = new Settings();

    private Engine engine = null;

    public DependencyCheckerAnalyzer() {
        init();
    }

    public DependencyCheckerAnalyzer(AnalyzerContext context) {
        super(context);
        init();
    }

    private void init() {
        settings.setBoolean("ossindexAnalyzerEnabled", false);
        engine = new Engine(Engine.Mode.STANDALONE, settings);
    }

    @Override
    public List<Issue> analyze(DependencyNode node) throws AnalyzerException {
        List<Issue> issues = Lists.newArrayList();
        String[] files = new String[] { node.getArtifact().getFile().getAbsolutePath() };
        try {
            final List<Dependency> dependencies = runScan("report", new String[]{"html"}, "Test Application", files, new String[]{}, 0, 11);

            for (Dependency dependency : dependencies) {
                for (Vulnerability vulnerability : dependency.getVulnerabilities()) {
                    issues.add(new CVEIssue(Issue.Severity.HIGH, vulnerability.getDescription()));
                }
            }
        } catch (ReportException | ExceptionCollection e) {
            //throw new AnalyzerException("Could not analyze", e);
        }

        return issues;
    }

    private List<Dependency> runScan(String reportDirectory, String[] outputFormats, String applicationName, String[] files,
                                     String[] excludes, int symLinkDepth, float cvssFailScore) throws DatabaseException, ExceptionCollection, ReportException {

        final List<Dependency> dependencies = new LinkedList<>();
        engine.scan(files);

        ExceptionCollection exCol = null;
        try {
            engine.analyzeDependencies();
            dependencies.addAll(Arrays.asList(engine.getDependencies()));
        } catch (ExceptionCollection ex) {
            if (ex.isFatal()) {
                throw ex;
            }
            exCol = ex;
        }
        if (exCol != null && !exCol.getExceptions().isEmpty()) {
            throw exCol;
        }
        return Collections.unmodifiableList(dependencies);
    }

    @Override
    public Analyzers getType() {
        return Analyzers.CVE;
    }
}
