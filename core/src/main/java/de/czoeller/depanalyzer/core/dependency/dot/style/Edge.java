
package de.czoeller.depanalyzer.core.dependency.dot.style;

import de.czoeller.depanalyzer.core.graph.dot.DotAttributeBuilder;
import org.apache.commons.lang3.StringUtils;

class Edge {

  private String style;
  private String color;
  private final Font font = new Font();

  DotAttributeBuilder createAttributes() {
    DotAttributeBuilder builder = new DotAttributeBuilder()
        .style(this.style)
        .color(this.color);

    return getFont().setAttributes(builder);
  }

  private Font getFont() {
    return this.font;
  }

  void merge(Edge other) {
    this.style = StringUtils.defaultIfBlank(other.style, this.style);
    this.color = StringUtils.defaultIfBlank(other.color, this.color);
    this.font.merge(other.font);
  }
}
