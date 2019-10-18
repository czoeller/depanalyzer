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