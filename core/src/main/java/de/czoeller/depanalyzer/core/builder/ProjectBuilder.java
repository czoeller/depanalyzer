package de.czoeller.depanalyzer.core.builder;

import lombok.Getter;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ProjectBuilder {

    @Getter
    private MavenProject parentProject;
    @Getter
    private Supplier<Collection<MavenProject>> projectSupplier;

    public void build(File parent) {

        BuiltProject builtProject = EmbeddedMaven
                .forProject(parent)
                .setGoals("compile")
                .setRecursive(true)
                .setJavaHome(new File("C:\\Program Files\\Java\\jdk1.8.0_211"))
                .build();
        final Model model = builtProject.getModel();
        model.setPomFile(parent);

        MavenProject project = new MavenProject(model);
        List<MavenProject> subModuleCollector = new ArrayList<>();
        for (BuiltProject module : builtProject.getModules()) {
            final Model model1 = module.getModel();
            final MavenProject project1 = new MavenProject(model1);
            project1.setPomFile(model1.getPomFile());
            resolveArtifact(model1, project1);
            subModuleCollector.add(project1);
            project1.setParent(project);
        }
        project.setCollectedProjects(subModuleCollector);
        parentProject = project;
        projectSupplier = () -> {
            return parentProject.getCollectedProjects();
        };
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
