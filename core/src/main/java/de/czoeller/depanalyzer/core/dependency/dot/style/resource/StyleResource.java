
package de.czoeller.depanalyzer.core.dependency.dot.style.resource;

import java.io.IOException;
import java.io.InputStream;

public interface StyleResource {

  boolean exists();

  InputStream openStream() throws IOException;
}
