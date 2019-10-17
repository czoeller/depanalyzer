package de.czoeller.depanalyzer.analyzer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.netbeans.api.annotations.common.StaticResource;

import java.io.File;

public abstract class ExampleArtifacts {

    @StaticResource(searchClasspath = true, relative = true)
    private static final String r = "../assets/mysql-connector-java-5.1.27.jar";
    public static final Artifact TEST_ARTIFACT_MYSQL;

    static {
        Artifact artifact = new DefaultArtifact("mysql","mysql-connector-java", "5.1.27", "compile", "jar", "", new DefaultArtifactHandler());
        artifact.setFile(new File(r));
        TEST_ARTIFACT_MYSQL = artifact;
    }

    private ExampleArtifacts() {}
}
