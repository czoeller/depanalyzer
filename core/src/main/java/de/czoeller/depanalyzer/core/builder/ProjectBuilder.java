package de.czoeller.depanalyzer.core.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
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
import java.io.StringReader;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class ProjectBuilder {

    @Getter
    private MavenProject parentProject;
    @Getter
    private Supplier<Collection<MavenProject>> projectSupplier;

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

        modulesReactorOrder(builtProject, subModuleCollector);
        project.setCollectedProjects(subModuleCollector);

        parentProject = project;
        projectSupplier = () -> parentProject.getCollectedProjects();
    }

    /**
     * Try to find and use java8 for maven build.
     * @return
     */
    private File findJava() {
        final String javaHome = System.getenv("java_home");
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

    private void modulesReactorOrder(BuiltProject builtProject, List<MavenProject> subModuleList) {
        log.debug("Try to bring modules to reactor order");
        Scanner scanner = new Scanner(new StringReader(builtProject.getMavenLog()));
        List<String> modules = Lists.newArrayList();
        boolean nowModules = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // ] ends the debug level output
            final String msg = line.split("]")[1].trim();
            if(line.contains("Reactor Build Order:")) {
                nowModules = true;
            } else if(!msg.isEmpty() && nowModules) {
                // first line after Reactor Build Order: is empty followed by the modules in order
                modules.add(msg);
            } else if(msg.isEmpty() && !modules.isEmpty()) {
                // done
                break;
            }
            // process the line
        }
        if(modules.isEmpty()) {
            log.debug("Project seems to be no multi module project.");
        } else {
            log.debug("order parsed from reactor: {}", modules);
            log.debug("order before sort: {}", subModuleList.stream().map(MavenProject::getName).collect(Collectors.toList()));
            subModuleList.sort(Ordering.explicit(modules).onResultOf(MavenProject::getName));
            final List<String> modulesAfterSort = subModuleList.stream().map(MavenProject::getName).collect(Collectors.toList());
            log.debug("order after sort: {}", modulesAfterSort);

            if(!modulesAfterSort.equals(modules)) {
                log.error("The reactor order could not be reconstructed! This would lead to wrong handling in the reachability map of the graph building component.");
                System.exit(1);
            }
        }
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
