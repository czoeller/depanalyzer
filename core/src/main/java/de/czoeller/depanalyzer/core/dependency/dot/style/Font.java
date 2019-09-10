
package de.czoeller.depanalyzer.core.dependency.dot.style;

import de.czoeller.depanalyzer.core.graph.dot.DotAttributeBuilder;
import org.apache.commons.lang3.StringUtils;

class Font {

  String color;
  Integer size;
  String name;

  DotAttributeBuilder setAttributes(DotAttributeBuilder builder) {
    return builder
        .fontColor(this.color)
        .fontSize(this.size)
        .fontName(this.name);
  }

  void merge(Font other) {
    this.color = StringUtils.defaultIfBlank(other.color, this.color);
    this.size = other.size != null ? other.size : this.size;
    this.name = StringUtils.defaultIfBlank(other.name, this.name);
  }
}
