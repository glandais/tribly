package fr.pedalons.common;

import java.net.URI;
import java.util.Locale;

/** Small helpers for the base URLs read from configuration. */
public final class UrlUtils {

  private UrlUtils() {}

  /** {@code url}, trimmed, without its trailing slashes — ready for a path to be appended. */
  public static String stripTrailingSlash(String url) {
    String u = url.trim();
    while (u.endsWith("/")) {
      u = u.substring(0, u.length() - 1);
    }
    return u;
  }

  /**
   * Refuses anything but an {@code https://} URL, with {@code http://} tolerated towards the local
   * machine only (a workstation running both applications).
   *
   * @param name what the URL is, for the message — typically its environment variable
   * @throws IllegalStateException when {@code url} is unparseable or not acceptable
   */
  public static void requireHttps(String url, String name) {
    URI uri;
    try {
      uri = URI.create(url);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(name + " is not a valid URL");
    }
    String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
    String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
    boolean local = host.equals("localhost") || host.equals("127.0.0.1") || host.equals("[::1]");
    if (!scheme.equals("https") && !(scheme.equals("http") && local)) {
      throw new IllegalStateException(name + " must be https:// (http:// only towards localhost)");
    }
  }
}
