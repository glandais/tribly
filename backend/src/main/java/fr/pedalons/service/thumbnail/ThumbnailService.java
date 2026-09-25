package fr.pedalons.service.thumbnail;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.enums.AssetType;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.asset.response.AssetWithFile;
import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.GPXPathType;
import io.github.glandais.gpx.data.Point;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ThumbnailService {

  private static final Logger LOG = Logger.getLogger(ThumbnailService.class);

  private static final List<Color> ROUTE_COLORS =
      List.of(
          Color.decode("#566B13"),
          Color.decode("#1d32a8"),
          Color.decode("#732C7B"),
          Color.decode("#bdbd22"),
          Color.decode("#c90808"),
          Color.decode("#b81491"),
          Color.decode("#628de3"),
          Color.decode("#6dcc5c"),
          Color.decode("#c694d4"),
          Color.decode("#e3a209"));

  /** A route's own thumbnail draws every track in red, as {@code GpxProcessingService} does. */
  static final List<Color> ROUTE_TRACK_COLORS = List.of(Color.RED);

  @Inject MapThumbnailRenderer renderer;

  @Inject AssetService assetService;

  public void generateRideThumbnails(Ride ride) {
    List<Route> routes = collectRideRoutes(ride);
    generateThumbnails(ride, routes, AssetType.RIDE_THUMBNAIL_LIGHT, AssetType.RIDE_THUMBNAIL_DARK);
  }

  public void generateTripThumbnails(Trip trip) {
    List<Route> routes = collectTripRoutes(trip);
    generateThumbnails(trip, routes, AssetType.TRIP_THUMBNAIL_LIGHT, AssetType.TRIP_THUMBNAIL_DARK);
  }

  /**
   * Redraws a route's thumbnails from the track geometry already in the database — the GPX import
   * draws them from the parsed file ({@code GpxProcessingService}), this is for fixing them later.
   */
  public void generateRouteThumbnails(Route route) {
    generateThumbnails(
        route,
        List.of(route),
        AssetType.ROUTE_THUMBNAIL_LIGHT,
        AssetType.ROUTE_THUMBNAIL_DARK,
        ROUTE_TRACK_COLORS);
  }

  private List<Route> collectRideRoutes(Ride ride) {
    LinkedHashSet<Long> seenIds = new LinkedHashSet<>();
    List<Route> routes = new ArrayList<>();

    if (ride.getRoute() != null) {
      seenIds.add(ride.getRoute().getId());
      routes.add(ride.getRoute());
    }

    ride.getGroups().stream()
        .sorted(Comparator.comparing(RideGroup::getSortOrder))
        .map(RideGroup::getRoute)
        .filter(r -> r != null && seenIds.add(r.getId()))
        .forEach(routes::add);

    return routes;
  }

  private List<Route> collectTripRoutes(Trip trip) {
    LinkedHashSet<Long> seenIds = new LinkedHashSet<>();
    List<Route> routes = new ArrayList<>();

    if (trip.getRoute() != null) {
      seenIds.add(trip.getRoute().getId());
      routes.add(trip.getRoute());
    }

    trip.getStages().stream()
        .filter(s -> !s.isDeleted())
        .sorted(Comparator.comparing(TripStage::getSortOrder))
        .map(TripStage::getRoute)
        .filter(r -> r != null && seenIds.add(r.getId()))
        .forEach(routes::add);

    return routes;
  }

  private void generateThumbnails(
      TeamEntity entity, List<Route> routes, AssetType lightType, AssetType darkType) {
    generateThumbnails(entity, routes, lightType, darkType, ROUTE_COLORS);
  }

  private void generateThumbnails(
      TeamEntity entity,
      List<Route> routes,
      AssetType lightType,
      AssetType darkType,
      List<Color> colors) {
    if (routes.isEmpty()) {
      return;
    }

    // Remove old thumbnails: one that no longer matches the routes is worse than none, which the
    // clients replace with their own placeholder
    removeExistingThumbnails(entity, lightType, darkType);

    // Build combined GPX with one path per route track
    List<ThumbnailTrack> tracks = new ArrayList<>();
    for (Route route : routes) {
      for (GpxTrack track : route.getTracks()) {
        tracks.add(new ThumbnailTrack(track.getName(), track.getTrackPoints()));
      }
    }

    List<GPXPath> paths = buildPaths(tracks);
    if (paths.isEmpty()) {
      return;
    }

    GPX gpx = new GPX("thumbnail", paths, List.of());

    generateThumbnail(
        entity, gpx, MapThumbnailRenderer.LIGHT_STYLE, colors, lightType, "thumbnail-light.png");
    generateThumbnail(
        entity, gpx, MapThumbnailRenderer.DARK_STYLE, colors, darkType, "thumbnail-dark.png");
  }

  /** Track geometry reduced to what the map renderer needs — decoupled from {@code Asset}/team. */
  public record ThumbnailTrack(String name, List<GpxTrack.TrackPoint> points) {}

  /**
   * Renders track geometry to a light-style map PNG at {@code output}, the same way ride/route
   * thumbnails are drawn, but to a plain file instead of an {@code Asset}. GPX previews have no team
   * and therefore no asset to hang a thumbnail on, so they render straight to storage. Returns
   * {@code false} (leaving no usable file) when there is nothing to draw or rendering fails —
   * callers treat a thumbnail as best-effort.
   */
  public boolean renderLightThumbnail(File output, List<ThumbnailTrack> tracks) {
    List<GPXPath> paths = buildPaths(tracks);
    if (paths.isEmpty()) {
      return false;
    }
    GPX gpx = new GPX("thumbnail", paths, List.of());
    try {
      renderer.render(output, gpx, MapThumbnailRenderer.LIGHT_STYLE, ROUTE_COLORS);
      return true;
    } catch (Exception e) {
      LOG.warnv("Preview thumbnail generation failed: {0}", e.getMessage());
      return false;
    }
  }

  static List<GPXPath> buildPaths(List<ThumbnailTrack> tracks) {
    List<GPXPath> paths = new ArrayList<>();
    for (ThumbnailTrack track : tracks) {
      GPXPath gpxPath = new GPXPath(track.name(), GPXPathType.TRACK);
      for (GpxTrack.TrackPoint tp : track.points()) {
        Point p = new Point();
        p.setLon(Math.toRadians(tp.lng()));
        p.setLat(Math.toRadians(tp.lat()));
        p.setEle(tp.ele());
        p.setInstant(null, Instant.EPOCH);
        gpxPath.addPoint(p);
      }
      gpxPath.computeArrays();
      paths.add(gpxPath);
    }
    return paths;
  }

  /**
   * Draws first, and only then creates the asset: a failed render leaves the entity without this
   * thumbnail rather than with an asset whose file is missing or wrong.
   */
  private void generateThumbnail(
      TeamEntity entity,
      GPX gpx,
      String style,
      List<Color> colors,
      AssetType assetType,
      String fileName) {
    File rendered = null;
    try {
      rendered = File.createTempFile("thumbnail-", ".png");
      renderer.render(rendered, gpx, style, colors);
      AssetWithFile assetFile = assetService.addAsset(entity, assetType, fileName);
      entity.getAssets().add(assetFile.asset());
      try {
        Files.move(
            rendered.toPath(), assetFile.file().toPath(), StandardCopyOption.REPLACE_EXISTING);
        assetService.uploadAssetFile(assetFile.asset());
      } catch (Exception e) {
        entity.getAssets().remove(assetFile.asset());
        throw e;
      }
      LOG.infov(
          "Generated {0} thumbnail for {1} {2}",
          style, entity.getClass().getSimpleName(), entity.getId());
    } catch (Exception e) {
      LOG.warnv(
          "Thumbnail generation ({0}) failed for {1} {2}, left without one: {3}",
          style, entity.getClass().getSimpleName(), entity.getId(), e.getMessage());
    } finally {
      if (rendered != null) {
        rendered.delete();
      }
    }
  }

  private void removeExistingThumbnails(
      TeamEntity entity, AssetType lightType, AssetType darkType) {
    entity
        .getAssets()
        .removeIf(
            asset -> {
              AssetType type = asset.getType();
              return type == lightType || type == darkType;
            });
  }
}
