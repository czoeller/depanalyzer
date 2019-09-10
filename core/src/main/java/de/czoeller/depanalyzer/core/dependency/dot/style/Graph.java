
package de.czoeller.depanalyzer.core.dependency.dot.style;

import de.czoeller.depanalyzer.core.graph.dot.DotAttributeBuilder;
import org.apache.commons.lang3.StringUtils;

public class Graph {

  private String rankdir;

  DotAttributeBuilder createAttributes() {
    return new DotAttributeBuilder().rankdir(this.rankdir);
  }

  void merge(Graph other) {
    this.rankdir = StringUtils.defaultIfBlank(other.rankdir, this.rankdir);
  }
}
