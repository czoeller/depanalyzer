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

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.netbeans.api.annotations.common.StaticResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReactorOrderParserTest {

    @Nested
    @DisplayName("Parse outputs")
    class ParseOutputs {

        private ReactorOrderParser reactorOrderParser;
        @StaticResource(searchClasspath = true)
        private static final String multiModule = "Example-MultiModule.txt";
        @StaticResource(searchClasspath = true)
        private static final String noMultiModule = "Example-NoMultiModule.txt";

        @BeforeEach
        void setUp() {
            this.reactorOrderParser = new ReactorOrderParser();
        }

        @DisplayName("Multi module project")
        @Test
        void multiModuleProject() throws IOException {
            // Arrange
            String mavenLog = getMavenLogFromFile(multiModule);
            // Act
            final List<String> modules = reactorOrderParser.parseModulesInReactorOrder(mavenLog);
            // Assert
            assertThat(modules).containsSequence("depanalyzer", "metamodel", "analyzer", "core", "ui");
        }

        @DisplayName("No multi module project")
        @Test
        void noMultiModuleProject() throws IOException {
            // Arrange
            String mavenLog = getMavenLogFromFile(noMultiModule);
            // Act
            final List<String> modules = reactorOrderParser.parseModulesInReactorOrder(mavenLog);
            // Assert
            assertThat(modules).isEmpty();
        }

        private String getMavenLogFromFile(String exampleLog) throws IOException {
            return FileUtils.readFileToString(new File(this.getClass().getResource(exampleLog).getFile()));
        }

    }
}