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
package de.czoeller.depanalyzer.core.builder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;

/**
 * Builds a pom with via maven API.
 * Supplies Maven-Projects of the built modules in reactor order.
 */
@Slf4j
public class ProjectBuilder {

    private final SortReactorOrder sortReactorOrder;

    @Getter
    private MavenProject parentProject;
    @Getter
    private Supplier<Collection<MavenProject>> projectSupplier;

    public ProjectBuilder() {
        this.sortReactorOrder = new SortReactorOrder();
    }

    public void build(File parent) {

        final File java = findJava();

        log.info("Using java {} for maven build.", java);

        BuiltProject builtProject = EmbeddedMaven
                .forProject(parent)
                .setGoals("install")
                .setBatchMode(true)
                .setRecursive(true)
                .setJavaHome(java)
                .build();

        final Model model = builtProject.getModel();
        model.setPomFile(parent);

        MavenProject project = new MavenProject(model);
        List<MavenProject> subModuleCollector = new ArrayList<>();
        subModuleCollector.add(project);
        setHierarchy(builtProject, project, subModuleCollector);

        sortReactorOrder.sortModulesInReactorOrder(builtProject, subModuleCollector);
        project.setCollectedProjects(subModuleCollector);

        parentProject = project;
        projectSupplier = () -> parentProject.getCollectedProjects();
    }

    /**
     * Try to find and use java8 for maven build.
     * @return Java path to be used as jdk home.
     */
    private File findJava() {
        final String javaHome = System.getenv("JAVA_HOME");
        log.debug("java_home is set to {}", javaHome);
        if(javaHome != null && (javaHome.contains("1.8") || javaHome.contains("8.0"))) {
            log.debug("using java_home as is");
            return new File(javaHome);
        }
        if (javaHome != null && (javaHome.contains("11") || javaHome.contains("12") || javaHome.contains("13"))) {
            log.warn("Detected Java home at {} and it doesn't seem to be java8!", javaHome);
            log.warn("Trying to find and switch to java8 ...");
            final Optional<File> java8 = findJava8(javaHome);
            if(java8.isPresent()) {
                log.warn("Found java8.");
                return java8.get();
            } else {
                log.error("Could not find java8");
            }
        }
        log.error("Could not find proper java.");
        throw new RuntimeException("Could not find proper java.");
    }

    private Optional<File> findJava8(String javaHome) {
        final File javaHomeParent = new File(javaHome).getParentFile();
        final File[] files = javaHomeParent.listFiles((current, name) -> new File(current, name).isDirectory());
        log.debug("possible jdk: {}", files);
        return Arrays.stream(files)
                     .filter(f -> f.getName().contains("1.8"))
                     .findFirst();
    }

    private void setHierarchy(BuiltProject builtProject, MavenProject parentMavenProject, List<MavenProject> subModuleCollector) {

        for (BuiltProject module : builtProject.getModules()) {
            final Model subModel = module.getModel();
            final MavenProject subProject = new MavenProject(subModel);
            subProject.setPomFile(subModel.getPomFile());
            resolveArtifact(subModel, subProject);
            subProject.setParent(parentMavenProject);

            subModuleCollector.add(subProject);
            setHierarchy(module, subProject, subModuleCollector);
        }
    }

    private void resolveArtifact(Model model, MavenProject project) {

        final String identifier = String.format("%s:%s:%s", model.getGroupId(), model.getArtifactId(), model.getVersion());

        try {
            final MavenResolvedArtifact[] resolvedArtifacts = Maven.configureResolver()
                                                                   .useLegacyLocalRepo(true)
                                                                   .withMavenCentralRepo(false)
                                                                   .loadPomFromFile(model.getPomFile())
                                                                   .resolve(identifier)
                                                                   .withoutTransitivity()
                                                                   .asResolvedArtifact();

            final MavenResolvedArtifact artifact = resolvedArtifacts[0];

            final MavenCoordinate coordinate = artifact.getCoordinate();
            final DefaultArtifact defaultArtifact = new DefaultArtifact(coordinate.getGroupId(),
                    coordinate.getArtifactId(),
                    coordinate.getVersion(),
                    artifact.getScope().toString(),
                    coordinate.getType().toString(),
                    coordinate.getClassifier(),
                    new DefaultArtifactHandler());
            defaultArtifact.setFile(artifact.asFile());
            project.setArtifact(defaultArtifact);

        } catch (NoResolvedResultException e) {
            log.warn("Could not resolve artifacts for {}", identifier);

            project.setArtifact(new DefaultArtifact(model.getGroupId(), model.getArtifactId(), model.getVersion(), "compile", "jar", "jar", new DefaultArtifactHandler()));
        }

    }

}
