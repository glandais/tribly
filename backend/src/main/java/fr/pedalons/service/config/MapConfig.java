package fr.pedalons.service.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.List;
import java.util.Optional;

/**
 * Cartography settings handed to the clients by {@code GET /api/config}.
 *
 * <p>Application configuration rather than columns on {@code Domain}: the basemaps and the public
 * tile host are properties of the deployment, not of the tenant — every domain of an instance
 * renders the same VersaTiles styles today, hard-coded in both clients. Putting them in a table
 * would buy per-tenant basemaps nobody has asked for, at the price of a migration and an admin
 * screen. {@code defaultCenter} is the one value that genuinely differs per site, and it is derived
 * from the team the site roots on before falling back to what is configured here — see {@code
 * ConfigService}.
 */
@ConfigMapping(prefix = "pedalons.map")
public interface MapConfig {

  /**
   * The public base URL of the tile host, for a client that builds its own style or sprite URLs.
   *
   * <p>Not to be confused with {@code tileserver.url}, which is the <em>internal</em> renderer the
   * thumbnail service talks to and which must never be handed to a client.
   */
  @WithDefault("https://tiles.versatiles.org")
  String tileServerBaseUrl();

  /** The basemaps, in the order the switcher should list them. */
  List<Style> styles();

  /** Where a map opens when nothing else says where to look. */
  DefaultCenter defaultCenter();

  interface Style {
    String id();

    String label();

    String url();

    /** The dark counterpart of {@link #url()}, when the style has one. */
    Optional<String> darkVariant();
  }

  interface DefaultCenter {
    @WithDefault("46.6")
    double lat();

    @WithDefault("2.3")
    double lon();

    @WithDefault("5")
    double zoom();
  }
}
