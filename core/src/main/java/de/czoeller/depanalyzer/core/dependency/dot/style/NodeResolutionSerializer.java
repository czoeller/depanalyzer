
package de.czoeller.depanalyzer.core.dependency.dot.style;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.czoeller.depanalyzer.core.dependency.NodeResolution;

import java.io.IOException;

class NodeResolutionSerializer extends JsonSerializer<NodeResolution> {

  @Override
  public void serialize(NodeResolution value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeFieldName(value.name().toLowerCase().replace('_', '-'));
  }

}
