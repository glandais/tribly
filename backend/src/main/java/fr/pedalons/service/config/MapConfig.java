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
 * renders the same basemaps, which both clients used to hard-code. Putting them in a table
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

  /**
   * One basemap.
   *
   * <p>Two kinds, and exactly one of the two per entry — {@code MapStyleService} rejects a style
   * declaring both or neither at startup:
   *
   * <ul>
   *   <li>a <b>hosted vector style</b>, {@link #url()} pointing at someone else's {@code style.json}
   *       (VersaTiles, IGN);
   *   <li>a <b>raster basemap</b>, {@link #raster()} naming XYZ tile templates. Those providers
   *       serve tiles, not style documents, so the server generates the MapLibre wrapper itself and
   *       hands the clients a URL on {@code /api/map/styles/{id}.json}. That wrapper used to be
   *       hand-written in the web client (its {@code rasterStyle()} helper) and simply absent from
   *       the mobile one, which is why the app only ever had vector fonds.
   * </ul>
   */
  interface Style {
    String id();

    String label();

    /**
     * The switcher section this basemap belongs to — {@code vector}, {@code satellite} or {@code
     * raster}. Free-form on purpose: the clients group by equal values and localise the heading,
     * they don't validate against an enum they'd have to be redeployed to extend.
     */
    @WithDefault("vector")
    String group();

    /** The hosted style document, for a vector basemap. Mutually exclusive with {@link #raster()}. */
    Optional<String> url();

    /**
     * The dark counterpart of {@link #url()}, when the style has one.
     *
     * <p>Only meaningful for a hosted style: a raster basemap is an aerial or a printed map, it has
     * no dark rendering, and a client asked to render it at night keeps it as it is.
     */
    Optional<String> darkVariant();

    /** The tile source, for a raster basemap. Mutually exclusive with {@link #url()}. */
    Optional<Raster> raster();
  }

  /** An XYZ raster source the server wraps into a one-layer MapLibre style. */
  interface Raster {
    /**
     * The tile templates. MapLibre has no {@code {s}} subdomain token, so a provider spread over
     * {@code a./b./c.} is declared as one entry per subdomain.
     */
    List<String> tiles();

    String attribution();

    /**
     * The deepest zoom the provider actually renders. MapLibre overzooms past it rather than showing
     * nothing, which keeps route lines usable above the source's native detail.
     */
    @WithDefault("19")
    int maxZoom();

    /**
     * The shallowest zoom matrix the provider serves, when it doesn't start at 0 — without it a
     * world-zoom map fires a wall of 404s (IGN SCAN 25 starts at z6).
     */
    Optional<Integer> minZoom();
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
