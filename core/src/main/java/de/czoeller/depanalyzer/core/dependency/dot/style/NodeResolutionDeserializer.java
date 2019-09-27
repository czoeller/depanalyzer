
package de.czoeller.depanalyzer.core.dependency.dot.style;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.czoeller.depanalyzer.metamodel.NodeResolution;

import java.io.IOException;


class NodeResolutionDeserializer extends JsonDeserializer<NodeResolution> {

  @Override
  public NodeResolution deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String name = p.getText().replace('-', '_').toUpperCase();
    return NodeResolution.valueOf(name);
  }

}
