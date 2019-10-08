package de.czoeller.depanalyzer.analyzer.jdepend;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.MetricIssue;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JDependAnalyzer extends BaseAnalyzer {

    private JDepend jdepend;

    /**
     * Required to obtain instance reflective.
     * TODO: remove reflective instantiation
     */
    public JDependAnalyzer() {
        init();
    }

    public JDependAnalyzer(AnalyzerContext context) {
        super(context);
        init();
    }

    private void init() {
        this.jdepend = new JDepend();
    }

    @Override
    public List<Issue> analyze(DependencyNode node)  {
        List<Issue> issues = Lists.newArrayList();
        try {
            jdepend.addDirectory(node.getArtifact().getFile().getAbsolutePath());
            final Collection<JavaPackage> packageList = jdepend.analyze();

            final List<JavaPackage> filteredPackages = packageList.stream()
                                                                  .filter(this::shouldAnalyzePackage).collect(Collectors.toList());

            for (JavaPackage javaPackage : filteredPackages) {
                log.trace("Analyzing package '{}'", javaPackage);
                if (javaPackage.getName().contains(getContext().getTargetGroupId())) {
                    log.trace("Analyzing package internals of '{}'", javaPackage);
                    final float instability = javaPackage.instability();
                    if(instability >= 0.75) {
                        log.info("Found instability issue in package '{}'", javaPackage);
                        issues.add(new MetricIssue(instability));
                    }
                } else {
                    log.trace("Skip analyzing package '{}'", javaPackage);
                }
            }
        } catch (IOException e) {
            throw new AnalyzerException("Failed to analyze artifact", e);
        }

        return issues;
    }

    private boolean shouldAnalyzePackage(JavaPackage javaPackage) {
        return !javaPackage.getName().startsWith("java.") && javaPackage.getName()
                                                              .contains(getContext().getTargetGroupId());
    }
}
