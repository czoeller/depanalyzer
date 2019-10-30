/*
 * Copyright (C) 2019 czoeller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.czoeller.depanalyzer.analyzer.dependencychecker;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.Analyzer;
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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DependencyCheckerAnalyzer extends BaseAnalyzer {

    private final Settings settings = new Settings();

    private Engine engine = null;

    private static DependencyCheckerAnalyzer INSTANCE;
    private static List<Dependency> analyzeResult = null;

    public DependencyCheckerAnalyzer() {
        init();
    }

    public DependencyCheckerAnalyzer(AnalyzerContext context) {
        super(context);
        init();
        INSTANCE = this;
    }

    private void init() {
        settings.setBoolean("ossindexAnalyzerEnabled", false);
        engine = new Engine(Engine.Mode.STANDALONE, settings);
    }

    @Override
    public Analyzer newInstance(AnalyzerContext context) {
        synchronized(DependencyCheckerAnalyzer.class) {
            return INSTANCE;
        }
    }

    @Override
    public List<Issue> analyze(DependencyNode node) throws AnalyzerException {
        List<Issue> issues = Lists.newArrayList();

        synchronized(DependencyCheckerAnalyzer.class) {
            if (analyzeResult == null) {
                String[] files = new String[] { "target/jar-analysis" };
                try {
                    analyzeResult = runScan("report", new String[]{"html"}, "Test Application", files, new String[]{}, 0, 11);
                } catch (ReportException | ExceptionCollection e) {
                    //throw new AnalyzerException("Could not analyze", e);
                }
            }
        }

        for (Dependency dependency : analyzeResult) {
            for (Vulnerability vulnerability : dependency.getVulnerabilities()) {
                if(dependency.getFileName().equals(node.getArtifact().getFile().getName())) {
                    issues.add(new CVEIssue(Issue.Severity.HIGH, vulnerability.getDescription()));
                }
            }
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
        try {
            for (String outputFormat : outputFormats) {
                engine.writeReports(applicationName, new File(reportDirectory), outputFormat, exCol);
            }
        } catch (ReportException ex) {
            if (exCol != null) {
                exCol.addException(ex);
                throw exCol;
            } else {
                throw ex;
            }
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
