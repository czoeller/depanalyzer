package de.czoeller.depanalyzer.core;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;

import java.io.File;

public class Core {
    public static final Artifact TEST_ARTIFACT_SPRING;

    static {
        //String TARGET_LIB_PATH = System.getProperty("user.dir") + "/assets/mysql-connector-java-5.1.27.jar";
        String TARGET_LIB_PATH = "C:\\Users\\noex_\\.m2\\repository\\org\\springframework\\spring-web\\5.1.3.RELEASE\\spring-web-5.1.3.RELEASE.jar";
        Artifact artifact = new DefaultArtifact("org.springframework","spring-web", ":5.1.3.RELEASE", "compile", "jar", "", new DefaultArtifactHandler());
        artifact.setFile(new File(TARGET_LIB_PATH));
        TEST_ARTIFACT_SPRING = artifact;
    }

}
