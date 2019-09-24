package de.czoeller.depanalyzer.analyzer.jdepend;

import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Issue;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDependAnalyzer implements Analyzer {

    private JDepend analyzer;

    public JDependAnalyzer() {
        this.analyzer = new JDepend();
    }

    @Override
    public Map<Artifact, List<Issue>> analyze(Artifact artifact) {
        Map<Artifact, List<Issue>> issues = new HashMap<>();
        try {
            analyzer.addDirectory(artifact.getArtifactPath());
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
