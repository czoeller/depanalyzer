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
package de.czoeller.depanalyzer.core.input.resolver;

import de.czoeller.depanalyzer.metamodel.DependencyNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

class PomResolverTest {

    public final File resources;
    private PomResolver pomResolver;

    PomResolverTest() throws URISyntaxException {
        Path resourceDirectory = Paths.get("src","test","projects");
        resources = resourceDirectory.toFile();
    }

    @BeforeEach
    void setUp() {
        this.pomResolver = new PomResolverImpl();
    }

    @Test
    void resolvePom_Single_Dependency_Tree() {
        File basedir = new File(resources, "single-dependency");

        final PomResolverResult pomResolverResult = pomResolver.resolvePom(new File(basedir, "pom.xml"));

        final DependencyNode rootNode = pomResolverResult.getRootNode();

        assertThat(rootNode.getChildren()).size().isEqualTo(1);
        assertThat(rootNode.getChildren().get(0).getArtifact().getArtifactId()).contains("guava");
    }

    @Test
    void resolvePom_Single_Dependency_Dot() {
        File basedir = new File(resources, "single-dependency");

        pomResolver.resolvePom(new File(basedir, "pom.xml"));

        final File dotFile = new File(basedir, "target/single-dependency.dot");
        final File expected = new File(basedir, "expectations/graph_without-types.dot");

        assertThat(dotFile).exists().isFile();
        assertThat(contentOf(dotFile)).isEqualTo(contentOf(expected));
    }

    @Test
    void resolvePom_MM_Tree() {
        File basedir = new File(resources, "reduced-edges-test");

        final PomResolverResult pomResolverResult = pomResolver.resolvePom(new File(basedir, "pom.xml"));

        final DependencyNode rootNode = pomResolverResult.getRootNode();

        assertThat(rootNode.getChildren()).size().isEqualTo(5);
    }

    @Test
    void resolvePom_MM_Dot() {
        File basedir = new File(resources, "reduced-edges-test");

        final PomResolverResult pomResolverResult = pomResolver.resolvePom(new File(basedir, "pom.xml"));

        final File dotFile = new File(basedir, "target/condense-test.dot");
        final File expected = new File(basedir, "expectations/aggregated-reduced-edges.dot");

        assertThat(dotFile).exists().isFile();
        assertThat(contentOf(dotFile)).isEqualTo(contentOf(expected));
    }
}