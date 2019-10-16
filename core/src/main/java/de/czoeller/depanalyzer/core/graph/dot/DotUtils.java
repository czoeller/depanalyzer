package de.czoeller.depanalyzer.core.graph.dot;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class DotUtils {

    private static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile("\r?\n");

    private DotUtils() {}

    public static void createDotGraphImage(Path graphFilePath, String dotFormattedOutput) throws IOException {
        if(System.getenv("CI") != null) {
            Graphviz.useEngine(new GraphvizCmdLineEngine());
            Graphviz.fromString(dotFormattedOutput)
                    .render(Format.PNG)
                    .toFile(graphFilePath.toFile());
        }
    }

    public static void writeGraphFile(String graph, Path graphFilePath) throws IOException {
        Path parent = graphFilePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (Writer writer = Files.newBufferedWriter(graphFilePath, StandardCharsets.UTF_8)) {
            writer.write(graph);
        }
    }

    private static String createDotImageFileName(Path graphFilePath) {
        String graphFileName = graphFilePath.getFileName().toString();
        graphFileName = graphFileName.substring(0, graphFileName.lastIndexOf(".")) + "." + "png";
        return graphFileName;
    }

}
