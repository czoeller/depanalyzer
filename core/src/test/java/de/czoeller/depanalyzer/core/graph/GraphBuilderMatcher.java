package de.czoeller.depanalyzer.core.graph;

import de.czoeller.depanalyzer.core.dependency.DependencyNode;
import org.codehaus.plexus.util.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;


public final class GraphBuilderMatcher extends TypeSafeDiagnosingMatcher<GraphBuilder<DependencyNode>> {

    private static final String[] EMPTY_ARRAY = new String[0];
    private static final String NODE_PATTERN = "\\s*\"[\\w\\p{Punct}]+\"(\\[.+])?\\s*";
    private static final String EDGE_PATTERN = ".+ -> .+";

    private final String[] expectedNodes;
    private final String[] expectedEdges;

    private List<String> nodes;
    private List<String> edges;


    private GraphBuilderMatcher(String[] expectedNodes, String[] expectedEdges) {
        this.expectedNodes = expectedNodes;
        this.expectedEdges = expectedEdges;
    }


    public static GraphBuilderMatcher hasNodesAndEdges(String[] nodes, String[] edges) {
        return new GraphBuilderMatcher(nodes, edges);
    }

    public static GraphBuilderMatcher hasNodes(String... nodes) {
        return new GraphBuilderMatcher(nodes, EMPTY_ARRAY);
    }

    public static GraphBuilderMatcher emptyGraph() {
        return new GraphBuilderMatcher(EMPTY_ARRAY, EMPTY_ARRAY);
    }


    @Override
    public void describeTo(Description description) {
        description.appendText("Graph containing");
        if (this.expectedNodes.length != 0) {
            description.appendText("\nNodes:");
            for (String node : this.expectedNodes) {
                description.appendText("\n  ").appendText(node);
            }
        } else {
            description.appendText(" No nodes");
        }

        description.appendText("\nand");

        if (this.expectedEdges.length != 0) {
            description.appendText("\nEdges:");
            for (String edge : this.expectedEdges) {
                description.appendText("\n  ").appendText(edge);
            }
        } else {
            description.appendText(" No edges");
        }
    }


    @Override
    protected boolean matchesSafely(GraphBuilder<DependencyNode> graphBuilder, Description mismatchDescription) {
        init(graphBuilder);

        mismatchDescription.appendText("was\nNodes:");
        for (String node : this.nodes) {
            mismatchDescription.appendText("\n").appendText(node);
        }
        mismatchDescription.appendText("\nEdges:");
        for (String edge : this.edges) {
            mismatchDescription.appendText("\n").appendText(edge);
        }

        return containsInAnyOrder(this.expectedNodes).matches(this.nodes)
                | containsInAnyOrder(this.expectedEdges).matches(this.edges);
    }

    private void init(GraphBuilder<DependencyNode> graphBuilder) {
        String graph = graphBuilder.toString();
        String[] lines = graph.split("\n");
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();

        for (String line : lines) {
            String trimmed = StringUtils.trim(line);

            if (trimmed.matches(EDGE_PATTERN)) {
                this.edges.add(trimmed);
            } else if (trimmed.matches(NODE_PATTERN)) {
                this.nodes.add(trimmed);
            }
        }
    }

}