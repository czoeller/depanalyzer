package de.czoeller.depanalyzer.core.dependency;

import de.czoeller.depanalyzer.core.graph.GraphBuilder;
import org.eclipse.aether.graph.DependencyVisitor;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * A node visitor that creates edges between the visited nodes using a {@link GraphBuilder}.
 */
class GraphBuildingVisitor implements DependencyVisitor {

    private final GraphBuilder<DependencyNode> graphBuilder;
    private final Deque<DependencyNode> nodeStack;

    /**
     * Max depth of the graph. Nodes deeper than this depth will be cut off from the graph.
     */
    private int cutOffDepth = 0;

    GraphBuildingVisitor(GraphBuilder<DependencyNode> graphBuilder) {
        this.graphBuilder = graphBuilder;
        this.nodeStack = new ArrayDeque<>();
    }

    @Override
    public boolean visitEnter(org.eclipse.aether.graph.DependencyNode node) {
        DependencyNode node1 = new DependencyNode(node);
        if (isExcluded(node1)) {
            return true;
        }

        if(nodeStack.isEmpty()) {
            this.graphBuilder.setRootNode(node1);
        }
        this.nodeStack.push(node1);
        this.cutOffDepth = this.nodeStack.size();

        return true;
    }

    @Override
    public boolean visitLeave(org.eclipse.aether.graph.DependencyNode node) {
        DependencyNode dependencyNode = new DependencyNode(node);
        if (isExcluded(dependencyNode)) {
            return true;
        }

        this.nodeStack.pop();

        DependencyNode currentParent = this.nodeStack.peek();
        if (this.nodeStack.size() < this.cutOffDepth) {
            this.cutOffDepth = this.nodeStack.size();

            if (currentParent != null) {
                mergeWithExisting(dependencyNode);
                if ("test".equals(dependencyNode.getArtifact().getScope())) {
                    this.graphBuilder.addPermanentEdge(currentParent, dependencyNode);
                } else {
                    this.graphBuilder.addEdge(currentParent, dependencyNode);
                }
            }
        }

        return true;
    }


    private boolean isExcluded(DependencyNode node) {
        /*Artifact artifact = node.getArtifact();

        return !this.globalFilter.include(artifact)
                || !this.transitiveFilter.include(artifact)
                || !this.includedResolutions.contains(node.getResolution());
                */
        return false;
    }

    private void mergeWithExisting(DependencyNode node) {
        DependencyNode effectiveNode = this.graphBuilder.getEffectiveNode(node);
        node.merge(effectiveNode);
    }
}