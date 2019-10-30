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
