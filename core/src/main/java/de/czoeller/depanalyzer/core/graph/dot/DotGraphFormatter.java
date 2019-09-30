
package de.czoeller.depanalyzer.core.graph.dot;


import de.czoeller.depanalyzer.core.graph.Edge;
import de.czoeller.depanalyzer.core.graph.GraphFormatter;
import de.czoeller.depanalyzer.core.graph.Node;

import java.util.Collection;

import static de.czoeller.depanalyzer.core.graph.dot.DotEscaper.escape;

public class DotGraphFormatter implements GraphFormatter {

  private final DotAttributeBuilder graphAttributeBuilder;
  private final DotAttributeBuilder nodeAttributeBuilder;
  private final DotAttributeBuilder edgeAttributeBuilder;

  public DotGraphFormatter() {
    this.graphAttributeBuilder = new DotAttributeBuilder();
    this.nodeAttributeBuilder = new DotAttributeBuilder().shape("box").fontName("Arial");
    this.edgeAttributeBuilder = new DotAttributeBuilder().fontName("Arial").fontSize(10);
  }

  public DotGraphFormatter(DotAttributeBuilder graphAttributeBuilder, DotAttributeBuilder nodeAttributeBuilder, DotAttributeBuilder edgeAttributeBuilder) {
    this.graphAttributeBuilder = graphAttributeBuilder;
    this.nodeAttributeBuilder = nodeAttributeBuilder;
    this.edgeAttributeBuilder = edgeAttributeBuilder;
  }

  @Override
  public String format(String graphName, Collection<Node<?>> nodes, Collection<Edge> edges) {
    StringBuilder sb = new StringBuilder("digraph ").append(escape(graphName)).append(" {");
    appendAttributes("graph", this.graphAttributeBuilder, sb);
    appendAttributes("node", this.nodeAttributeBuilder, sb);
    appendAttributes("edge", this.edgeAttributeBuilder, sb);

    sb.append("\n\n  // Node Definitions:");
    for (Node<?> node : nodes) {
      String nodeId = node.getNodeId();
      String nodeName = node.getNodeName();
      sb.append("\n  ")
          .append(escape(nodeId))
          .append(nodeName);
    }

    sb.append("\n\n  // Edge Definitions:");
    for (Edge edge : edges) {
      String edgeDefinition = escape(edge.getFromNodeId()) + " -> " + escape(edge.getToNodeId()) + edge.getName();
      sb.append("\n  ").append(edgeDefinition);
    }

    return sb.append("\n}").toString();
  }

  private void appendAttributes(String tagName, DotAttributeBuilder attributeBuilder, StringBuilder sb) {
    if (!attributeBuilder.isEmpty()) {
      sb.append("\n  ")
          .append(tagName)
          .append(" ")
          .append(attributeBuilder);
    }
  }
}
