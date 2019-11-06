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
package de.czoeller.depanalyzer.analyzer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ExampleArtifacts {

    private static final Path MYSQL_PATH = Paths.get("..", "target", "example-artifacts", "mysql-connector-java-5.1.27.jar");
    public static final Artifact TEST_ARTIFACT_MYSQL;

    static {
        Artifact artifact = new DefaultArtifact("mysql","mysql-connector-java", "5.1.27", "compile", "jar", "", new DefaultArtifactHandler());
        artifact.setFile(MYSQL_PATH.toFile());
        TEST_ARTIFACT_MYSQL = artifact;
    }

    private ExampleArtifacts() {}
}
