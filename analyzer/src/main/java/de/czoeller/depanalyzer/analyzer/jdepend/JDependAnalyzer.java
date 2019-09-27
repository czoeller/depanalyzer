package de.czoeller.depanalyzer.analyzer.jdepend;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class JDependAnalyzer implements Analyzer {

    private JDepend analyzer;

    public JDependAnalyzer() {
        this.analyzer = new JDepend();
    }

    @Override
    public List<Issue> analyze(DependencyNode node)  {
        List<Issue> issues = Lists.newArrayList();
        try {
            analyzer.addDirectory(node.getArtifact().getFile().getAbsolutePath());
            final Collection<JavaPackage> packageList = analyzer.analyze();

            for (JavaPackage javaPackage : packageList) {
                if (javaPackage.getName().contains("apache")) {
                    System.out.println(javaPackage);
                    System.out.println(javaPackage.instability());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new AnalyzerException("Failed to analyze artifact", e);
        }

        return issues;
    }

}
