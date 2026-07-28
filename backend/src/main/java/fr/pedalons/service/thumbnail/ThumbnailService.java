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
import io.github.glandais.elevation.CoordinatesElevation;
import io.github.glandais.elevation.LatLonElevation;
import io.github.glandais.engine.path.Path;
import io.github.glandais.engine.path.PathJvm;
import io.github.glandais.map.TileMapProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

  @Inject TileMapProducer tileMapProducer;

  @Inject AssetService assetService;

  @ConfigProperty(name = "tileserver.url")
  private String tileserverUrl;

  public void generateRideThumbnails(Ride ride) {
    List<Route> routes = collectRideRoutes(ride);
    generateThumbnails(ride, routes, AssetType.RIDE_THUMBNAIL_LIGHT, AssetType.RIDE_THUMBNAIL_DARK);
  }

  public void generateTripThumbnails(Trip trip) {
    List<Route> routes = collectTripRoutes(trip);
    generateThumbnails(trip, routes, AssetType.TRIP_THUMBNAIL_LIGHT, AssetType.TRIP_THUMBNAIL_DARK);
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
    if (routes.isEmpty()) {
      return;
    }

    // Remove old thumbnails
    removeExistingThumbnails(entity, lightType, darkType);

    // Build combined GPX with one path per route track
    List<ThumbnailTrack> tracks = new ArrayList<>();
    for (Route route : routes) {
      for (GpxTrack track : route.getTracks()) {
        tracks.add(new ThumbnailTrack(track.getName(), track.getTrackPoints()));
      }
    }

    List<Path> paths = buildPaths(tracks);
    if (paths.isEmpty()) {
      return;
    }

    generateThumbnail(entity, paths, "colorful", lightType, "thumbnail-light.png");
    generateThumbnail(entity, paths, "eclipse", darkType, "thumbnail-dark.png");
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
    List<Path> paths = buildPaths(tracks);
    if (paths.isEmpty()) {
      return false;
    }
    try {
      String tileUrl = tileserverUrl + "/styles/colorful/256/{z}/{x}/{y}.png";
      tileMapProducer.createTileMap(
          output, paths, tileUrl, 0.1, null, 512, 512, null, ROUTE_COLORS);
      return true;
    } catch (Exception e) {
      LOG.warnv("Preview thumbnail generation failed: {0}", e.getMessage());
      return false;
    }
  }

  /**
   * Rebuilds renderable geometry from stored track points. {@code public} so {@code
   * GpxProcessingService} draws its route thumbnails from exactly the same geometry the rest of the
   * app renders, instead of maintaining a second conversion.
   */
  public static List<Path> buildPaths(List<ThumbnailTrack> tracks) {
    List<Path> paths = new ArrayList<>();
    for (ThumbnailTrack track : tracks) {
      List<CoordinatesElevation> coordinates = new ArrayList<>(track.points().size());
      for (GpxTrack.TrackPoint tp : track.points()) {
        coordinates.add(new LatLonElevation(tp.lat(), tp.lng(), tp.ele()));
      }
      if (coordinates.isEmpty()) {
        continue;
      }
      paths.add(PathJvm.fromCoordinates(coordinates));
    }
    return paths;
  }

  private void generateThumbnail(
      TeamEntity entity, List<Path> paths, String style, AssetType assetType, String fileName) {
    try {
      AssetWithFile assetFile = assetService.addAsset(entity, assetType, fileName);
      entity.getAssets().add(assetFile.asset());
      File file = assetFile.file();
      String tileUrl = tileserverUrl + "/styles/" + style + "/256/{z}/{x}/{y}.png";
      // Exactly one framing mode may be non-null (maxSize | width+height | zoom): here
      // width+height. A second one would raise IllegalArgumentException, swallowed by the catch
      // below since a thumbnail is best-effort.
      tileMapProducer.createTileMap(file, paths, tileUrl, 0.1, null, 512, 512, null, ROUTE_COLORS);
      assetService.uploadAssetFile(assetFile.asset());
      LOG.infov(
          "Generated {0} thumbnail for {1} {2}",
          style, entity.getClass().getSimpleName(), entity.getId());
    } catch (Exception e) {
      LOG.warnv(
          "Thumbnail generation ({0}) failed for {1} {2}: {3}",
          style, entity.getClass().getSimpleName(), entity.getId(), e);
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
