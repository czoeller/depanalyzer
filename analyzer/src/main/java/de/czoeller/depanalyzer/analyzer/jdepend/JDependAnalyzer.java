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
package de.czoeller.depanalyzer.analyzer.jdepend;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.MetricIssue;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
public class JDependAnalyzer extends BaseAnalyzer {

    public static final double DISTANCE_THRESHOLD = 0.3;

    private JDepend jdepend;
    private static Collection<JavaPackage> analyzeResult = null;
    private static Map<String, List<String>> jarPackagesMap = new HashMap<>();

    public JDependAnalyzer(AnalyzerContext context) {
        super(context);
        init();
    }

    private void init() {
        this.jdepend = new JDepend();
    }

    @Override
    public Analyzers getType() {
        return Analyzers.METRICS;
    }

    @Override
    public List<Issue> analyze(DependencyNode node)  {
        final List<Issue> issues = Lists.newArrayList();

        if (analyzeResult == null) {
            try {
                Files.list(Paths.get("target", "jar-analysis"))
                     .forEach(f -> {
                         try {
                             jdepend.addDirectory(f.toFile().getAbsolutePath());
                             jarPackagesMap.put(f.toFile().getName(), getPackagesInJar(f.toFile().getAbsolutePath()));
                         } catch (IOException e) {
                             throw new AnalyzerException("Could not analyze", e);
                         }
                     });
            } catch (IOException e) {
                throw new AnalyzerException("Could not analyze", e);
            }
            analyzeResult = jdepend.analyze();
        }

        final List<JavaPackage> filteredPackages = analyzeResult.stream().filter(this::shouldAnalyzePackage).collect(Collectors.toList());

        for (JavaPackage javaPackage : filteredPackages) {
            log.trace("Analyzing package '{}'", javaPackage);

            if (javaPackage.getName().contains(getContext().getTargetGroupId())) {
                if (originatesFromPackage(node, javaPackage)) {
                    log.trace("Analyzing package internals of '{}'", javaPackage);
                    final float distance = javaPackage.distance();
                    if (distance >= DISTANCE_THRESHOLD) {
                        log.info("Found distance issue in package '{}'", javaPackage);
                        issues.add(new MetricIssue(getSeverity(distance), String.format("distance of pkg '%s': %.2f (A: %.2f I: %.2f)", javaPackage.getName(), distance, javaPackage.abstractness(), javaPackage.instability()), distance));
                    }
                } else {
                    log.trace("Skip analyzing package '{}' because to issue originates from a different package and not the current jar ('{}')", javaPackage, node.getArtifact().getFile().getName());
                }
            } else {
                log.trace("Skip analyzing package '{}'", javaPackage);
            }
        }

        return issues;
    }

    /**
     * JDepend analyzes the specified jars at once. It provides {@link JavaPackage} as interface but doesn't know where this package lives.
     * So the mapping Jar <-> Package is not there. To change this we process all JARs and store a list of its packages.
     * When JDepend gives us a {@link JavaPackage} we can lookup the package name on our maps to finally find the Jar.
     * If the jar of the {@link DependencyNode} that is currently queried and our guess of the jar where the package lives match then we made the Jar <-> Package relation.
     * @param node
     * @param javaPackage
     * @return
     */
    private boolean originatesFromPackage(DependencyNode node, JavaPackage javaPackage) {
        final String jarNameOfCurrentRequestedNode = node.getArtifact().getFile().getName();

        final Map<String, Integer> similarityPerJarMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : jarPackagesMap.entrySet()) {

            final Map<String, Integer> similarityPerPackagesMap = new HashMap<>();
            for (String pkg : entry.getValue()) {
                if( pkg.startsWith(javaPackage.getName()) ) {
                    similarityPerPackagesMap.put(pkg, javaPackage.getName().length());
                }
            }

            if(!similarityPerPackagesMap.isEmpty()) {
                // Get best match for Jar <- Package
                final Integer value = Collections.max(similarityPerPackagesMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
                similarityPerJarMap.put(entry.getKey(), value);
            }
        }

        final String jarNameThePackageLivesIn = Collections.max(similarityPerJarMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

        final boolean isEqual = jarNameThePackageLivesIn.equals(jarNameOfCurrentRequestedNode);

        return isEqual;
    }

    public static List<String> getPackagesInJar(String jarPath) {
        List<String> packages = Lists.newArrayList();
        try {
            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements()) {
                final JarEntry jarEntry = entryEnumeration.nextElement();
                if(jarEntry.isDirectory()) {
                    packages.add(jarEntry.getName().replaceAll("/", "."));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packages;
    }

    private Issue.Severity getSeverity(float distance) {
       Issue.Severity severity = Issue.Severity.LOW;
       if(distance >= 0.90) {
           severity = Issue.Severity.HIGH;
       } else if(distance >= 0.75) {
           severity = Issue.Severity.MEDIUM;
       }

        return severity;
    }

    private boolean shouldAnalyzePackage(JavaPackage javaPackage) {
        return !javaPackage.getName().startsWith("java.");
    }

}
