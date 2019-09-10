
package de.czoeller.depanalyzer.core.dependency.dot.style;

import de.czoeller.depanalyzer.core.graph.dot.DotAttributeBuilder;

class Polygon extends AbstractNode {

  private int sides;

  Polygon() {
    super("polygon");
  }

  @Override
  DotAttributeBuilder createAttributes() {
    return super.createAttributes().addAttribute("sides", this.sides > 0 ? Integer.toString(this.sides) : null);
  }

}
