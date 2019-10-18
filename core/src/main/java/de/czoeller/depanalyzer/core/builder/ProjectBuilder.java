package de.czoeller.depanalyzer.core.builder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
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
        System.exit(1);
        return null;
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

    private void resolveArtifact(Model model1, MavenProject project1) {
        final MavenResolvedArtifact[] artifact = Maven.configureResolver()
                                                      .useLegacyLocalRepo(true)
                                                      .withMavenCentralRepo(false)
                                                      .loadPomFromFile(model1.getPomFile())
                                                      .resolve(model1.getGroupId() + ":" + model1.getArtifactId() + ":" + model1.getVersion())
                                                      .withoutTransitivity().asResolvedArtifact();
        final MavenResolvedArtifact a1 = artifact[0];
        final DefaultArtifact defaultArtifact = new DefaultArtifact(a1.getCoordinate()
                                                                      .getGroupId(), a1.getCoordinate()
                                                                                       .getArtifactId(), a1.getCoordinate()
                                                                                                           .getVersion(), a1.getScope()
                                                                                                                            .toString(), a1.getCoordinate()
                                                                                                                                           .getType()
                                                                                                                                           .toString(), a1.getCoordinate()
                                                                                                                                                          .getClassifier(), new DefaultArtifactHandler());
        defaultArtifact.setFile(a1.asFile());
        project1.setArtifact(defaultArtifact);
    }

}
