
package de.czoeller.depanalyzer.core.dependency;

/**
 * Utility class to abbreviate version strings.
 */
public final class VersionAbbreviator {

  private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";

  private VersionAbbreviator() {
    throw new AssertionError("Not instantiable");
  }

  public static String abbreviateVersion(String version) {
    if (version.endsWith(SNAPSHOT_SUFFIX)) {
      return version.substring(0, version.length() - SNAPSHOT_SUFFIX.length()) + "-S.";
    }

    return version;
  }
}
