package com.tribly.service.thumbnail;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.Route;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripStage;
import com.tribly.enums.AssetType;
import com.tribly.service.asset.AssetService;
import com.tribly.service.asset.response.AssetWithFile;
import io.github.glandais.gpx.data.GPX;
import io.github.glandais.gpx.data.GPXPath;
import io.github.glandais.gpx.data.GPXPathType;
import io.github.glandais.gpx.data.Point;
import io.github.glandais.gpx.map.TileMapProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.awt.Color;
import java.io.File;
import java.time.Instant;
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

    // Build combined GPX with one path per route
    List<GPXPath> paths = new ArrayList<>();
    for (Route route : routes) {
      for (GpxTrack track : route.getTracks()) {
        GPXPath gpxPath = new GPXPath(track.getName(), GPXPathType.TRACK);
        for (GpxTrack.TrackPoint tp : track.getTrackPoints()) {
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
    }

    if (paths.isEmpty()) {
      return;
    }

    GPX gpx = new GPX("thumbnail", paths, List.of());

    generateThumbnail(entity, gpx, "colorful", lightType, "thumbnail-light.png");
    generateThumbnail(entity, gpx, "eclipse", darkType, "thumbnail-dark.png");
  }

  private void generateThumbnail(
      TeamEntity entity, GPX gpx, String style, AssetType assetType, String fileName) {
    try {
      AssetWithFile assetFile = assetService.addAsset(entity, assetType, fileName);
      entity.getAssets().add(assetFile.asset());
      File file = assetFile.file();
      String tileUrl = tileserverUrl + "/styles/" + style + "/256/{z}/{x}/{y}.png";
      tileMapProducer.createTileMap(file, gpx, tileUrl, 0.1, 512, 512, ROUTE_COLORS);
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
