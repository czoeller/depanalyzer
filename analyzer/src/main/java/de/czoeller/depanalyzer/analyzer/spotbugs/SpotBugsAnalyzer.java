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
package de.czoeller.depanalyzer.analyzer.spotbugs;

import com.google.common.collect.Lists;
import de.czoeller.depanalyzer.analyzer.Analyzer;
import de.czoeller.depanalyzer.analyzer.AnalyzerContext;
import de.czoeller.depanalyzer.analyzer.AnalyzerException;
import de.czoeller.depanalyzer.analyzer.BaseAnalyzer;
import de.czoeller.depanalyzer.metamodel.Analyzers;
import de.czoeller.depanalyzer.metamodel.DependencyNode;
import de.czoeller.depanalyzer.metamodel.Issue;
import de.czoeller.depanalyzer.metamodel.Issue.Severity;
import de.czoeller.depanalyzer.metamodel.SpotBugsIssue;
import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.config.UserPreferences;
import edu.umd.cs.findbugs.plugins.DuplicatePluginIdException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class SpotBugsAnalyzer extends BaseAnalyzer {

    private FindBugs2 findBugs;

    /**
     * Required to obtain instance reflective.
     * TODO: remove reflective instantiation
     */
    public SpotBugsAnalyzer() {
        init();
    }

    public SpotBugsAnalyzer(AnalyzerContext context) {
        super(context);
        init();
    }

    private void init() {
        this.findBugs = new FindBugs2();
    }

    @Override
    public Analyzer newInstance(AnalyzerContext context) {
        return new SpotBugsAnalyzer(context);
    }

    @Override
    public Analyzers getType() {
        return Analyzers.SPOTBUGS;
    }

    @Override
    public  List<Issue> analyze(DependencyNode node) throws AnalyzerException {
        List<Issue> issues = Lists.newArrayList();

        if(!shouldAnalyzeNode(node)) {
            log.trace("Skipping node '{}' because it's not in context.", node.getIdentifier());
            return issues;
        }

        log.debug("Analyzing node {}", node.getIdentifier());

        Collection<Plugin> customPlugins = loadPlugins();
        disableUpdateChecksOnEveryPlugin();
        Project project = new Project();
        project.addFile(node.getArtifact().getFile().getAbsolutePath());

        Iterator<File> it = FileUtils.iterateFiles(new File("target/jar-analysis"), null, false);
        while(it.hasNext()) {
            File next = it.next();
            if(!(next.getName().contains("spotbugs") || next.getName().contains("findbugs") || next.getName().contains(node.getArtifact().getArtifactId()))) {
                project.addAuxClasspathEntry(next.getAbsolutePath());
            }
        }

        BugCollection bugs = new SortedBugCollection();
        BugReporter bugReporter = new MyReporter(bugs);
        findBugs.setProject(project);
        //findBugs.setProgressCallback(new TextUIProgressCallback(System.out));
        findBugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
        findBugs.setBugReporter(bugReporter);
        final UserPreferences defaultUserPreferences = UserPreferences.createDefaultUserPreferences();
        defaultUserPreferences.setEffort(UserPreferences.EFFORT_MAX);
        findBugs.setUserPreferences(defaultUserPreferences);

        try {
            findBugs.execute();
            for (BugInstance bug : bugs) {
                issues.add(new SpotBugsIssue(Severity.map(bug.getPriority()), bug.getMessage()));
            }
            if(!issues.isEmpty()) {
                log.info("found bugs: {}", bugs);
            }
        } catch (IOException e) {
            log.debug("IO exception while analyzing", e);
        } catch (InterruptedException e) {
            log.debug("Analyzer interrupted", e);
        } finally {
            resetCustomPluginList(customPlugins);
        }

        return issues;
    }

    private Collection<Plugin> loadPlugins() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Path findSecBugsPath = Paths.get("target","spotbugs-plugins", "findsecbugs-plugin-1.9.0.jar");
        File findSecBugsFile = findSecBugsPath.toFile();

        if(!findSecBugsFile.exists()) {
            log.error("Could not find find-secbugs plugin. This would lead to incomplete analyze results.");
            throw new RuntimeException("Could not find find-secbugs plugin. This would lead to incomplete analyze results.");
        }

        List<String> pluginJarPathList = Lists.newArrayList();
        pluginJarPathList.add(findSecBugsFile.getAbsolutePath());

        List<Plugin> customPluginList = Lists.newArrayList();
        for (String path : pluginJarPathList) {
            try {
                Plugin plugin = Plugin.addCustomPlugin(new File(path).toURI(), contextClassLoader);
                if (plugin != null) {
                    customPluginList.add(plugin);
                    log.info("Loading findbugs plugin: " + path);
                }
            } catch (PluginException e) {
                log.warn("Failed to load plugin for the custom detector: " + path);
                log.debug("Cause of failure", e);
            } catch (DuplicatePluginIdException e) {
                log.debug("Plugin already loaded: exception ignored: " + e.getMessage());
            }
        }

        return customPluginList;
    }

    /**
     * Disable the update check for every plugin. See http://findbugs.sourceforge.net/updateChecking.html
     */
    private static void disableUpdateChecksOnEveryPlugin() {
        for (Plugin plugin : Plugin.getAllPlugins()) {
            plugin.setMyGlobalOption("noUpdateChecks", "true");
        }
    }

    private static void resetCustomPluginList(Collection<Plugin> customPlugins) {
        if (customPlugins != null) {
            for (Plugin plugin : customPlugins) {
                Plugin.removeCustomPlugin(plugin);
            }
        }
    }

    private boolean shouldAnalyzeNode(DependencyNode node) {
        return node.getArtifact().getGroupId().contains(getContext().getTargetGroupId());
    }
}
