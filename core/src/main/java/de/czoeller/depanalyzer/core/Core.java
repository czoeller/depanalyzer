package de.czoeller.depanalyzer.core;

import de.czoeller.depanalyzer.metamodel.Artifact;
import de.czoeller.depanalyzer.metamodel.Version;

public class Core {
    public static final Artifact TEST_ARTIFACT_SPRING;

    static {
        //String TARGET_LIB_PATH = System.getProperty("user.dir") + "/assets/mysql-connector-java-5.1.27.jar";
        String TARGET_LIB_PATH = "C:\\Users\\noex_\\.m2\\repository\\org\\springframework\\spring-web\\5.1.3.RELEASE\\spring-web-5.1.3.RELEASE.jar";
        Artifact artifact = new Artifact(TARGET_LIB_PATH);
        Version version = new Version("org.springframework","spring-web", ":5.1.3.RELEASE");
        artifact.setVersion(version);
        TEST_ARTIFACT_SPRING = artifact;
    }

}
