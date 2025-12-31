package com.tribly.util;

import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.asset.repository.AssetRepository;
import com.tribly.domain.place.Place;
import com.tribly.domain.place.repository.PlaceRepository;
import com.tribly.domain.post.Post;
import com.tribly.domain.post.repository.PostRepository;
import com.tribly.domain.ride.*;
import com.tribly.domain.ride.repository.RideGroupRepository;
import com.tribly.domain.ride.repository.RideParticipationRepository;
import com.tribly.domain.ride.repository.RideRepository;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.RouteClimb;
import com.tribly.domain.route.repository.GpxTrackRepository;
import com.tribly.domain.route.repository.RouteClimbRepository;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripParticipation;
import com.tribly.domain.trip.TripStage;
import com.tribly.domain.trip.repository.TripParticipationRepository;
import com.tribly.domain.trip.repository.TripRepository;
import com.tribly.domain.trip.repository.TripStageRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.enums.*;
import com.tribly.service.common.SlugService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.geolatte.geom.G2D;
import org.geolatte.geom.LineString;
import org.geolatte.geom.codec.Wkt;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TestDataService {

  @Inject UserRepository userRepository;
  @Inject TeamRepository teamRepository;
  @Inject UserTeamRepository userTeamRepository;
  @Inject RideRepository rideRepository;
  @Inject RideGroupRepository rideGroupRepository;
  @Inject RideParticipationRepository participationRepository;
  @Inject RouteRepository routeRepository;
  @Inject RouteClimbRepository routeClimbRepository;
  @Inject GpxTrackRepository gpxTrackRepository;

  @Transactional
  public User createUser(String email, String displayName) {
    User user = new User(email, displayName);
    userRepository.persistAndFlush(user);
    return user;
  }

  @Transactional
  public void deleteUser(User user) {
    user.setDeleted(true);
    userRepository.getEntityManager().merge(user);
  }

  @Transactional
  public Team createTeam(User user, String name, String slug, Visibility visibility) {
    Team team = new Team(user, name, slug, visibility);
    teamRepository.persistAndFlush(team);
    return team;
  }

  @Transactional
  public UserTeam addUserToTeam(User user, Team team, TeamRole role) {
    UserTeam userTeam = new UserTeam(user, user, team, role);
    userTeamRepository.persistAndFlush(userTeam);
    return userTeam;
  }

  @Transactional
  public Ride createRide(Team team, User createdBy, String title, String slug, Instant dateTime) {
    return createRide(
        team, createdBy, title, slug, dateTime, Visibility.PUBLIC, Status.PUBLISHED, null);
  }

  @Transactional
  public Ride createRide(
      Team team, User createdBy, String title, String slug, Instant dateTime, Status status) {
    return createRide(team, createdBy, title, slug, dateTime, Visibility.PUBLIC, status, null);
  }

  @Transactional
  public Ride createRide(
      Team team,
      User createdBy,
      String title,
      String slug,
      Instant dateTime,
      Visibility visibility) {
    return createRide(team, createdBy, title, slug, dateTime, visibility, Status.PUBLISHED, null);
  }

  @Transactional
  public Ride createRide(
      Team team,
      User createdBy,
      String title,
      String slug,
      Instant date,
      Visibility visibility,
      Status status) {
    return createRide(team, createdBy, title, slug, date, visibility, status, null);
  }

  @Transactional
  public Ride createRide(
      Team team,
      User createdBy,
      String title,
      String slug,
      Instant date,
      Visibility visibility,
      Status status,
      Instant publishAt) {
    Ride ride = new Ride(createdBy, team, date, title, slug, visibility);
    ride.setStatus(status);
    ride.setPublishAt(publishAt);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public RideGroup createRideGroup(User createdBy, Ride ride, String name) {
    RideGroup group = new RideGroup(createdBy, ride, name);
    rideGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public RideGroup createRideGroup(User createdBy, Ride ride, String name, int sortOrder) {
    RideGroup group = new RideGroup(createdBy, ride, name);
    group.setSortOrder(sortOrder);
    rideGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public RideGroup createRideGroupWithMaxParticipants(
      User createdBy, Ride ride, String name, int maxParticipants) {
    RideGroup group = new RideGroup(createdBy, ride, name);
    group.setRide(ride);
    group.setName(name);
    group.setMaxParticipants(maxParticipants);
    rideGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public RideParticipation createParticipation(RideGroup group, User user) {
    RideParticipation participation = new RideParticipation(group, user);
    participationRepository.persistAndFlush(participation);
    return participation;
  }

  @Transactional
  public void deleteRide(Ride ride) {
    ride.setDeleted(true);
    rideRepository.getEntityManager().merge(ride);
  }

  @Transactional
  public void deleteRideGroup(RideGroup group) {
    group.setDeleted(true);
    rideGroupRepository.getEntityManager().merge(group);
  }

  @Transactional
  public void deleteParticipation(RideParticipation participation) {
    participation.setDeleted(true);
    participationRepository.getEntityManager().merge(participation);
  }

  @Transactional
  public Route createRoute(Team team, User createdBy, String name) {
    return createRoute(team, createdBy, name, Visibility.PUBLIC);
  }

  @Transactional
  public Route createRoute(Team team, User createdBy, String name, Visibility visibility) {
    List<GpxTrack.TrackPoint> trackPoints = List.of(new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0));
    String geometry = "LINESTRING(6 45,6.1 45.1)";
    return createRoute(team, createdBy, name, visibility, geometry, trackPoints);
  }

  @Transactional
  public Route createRoute(
      Team team,
      User createdBy,
      String name,
      Visibility visibility,
      String geometry,
      List<GpxTrack.TrackPoint> trackPoints) {
    Route route = new Route(createdBy, team, name, SlugService.slugify(name), visibility);
    routeRepository.persistAndFlush(route);
    LineString<G2D> lineString = (LineString<G2D>) Wkt.fromWkt(geometry, WGS84);
    GpxTrack track = new GpxTrack(createdBy, route, lineString, trackPoints, Instant.now());
    gpxTrackRepository.persistAndFlush(track);
    return route;
  }

  @Transactional
  public Route createRoute(
      Team team,
      User createdBy,
      String name,
      Visibility visibility,
      Status status,
      @Nullable Instant dateTime,
      @Nullable String markdown) {
    List<GpxTrack.TrackPoint> trackPoints = List.of(new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0));
    String geometry = "LINESTRING(6 45,6.1 45.1)";
    Route route = new Route(createdBy, team, name, SlugService.slugify(name), visibility);
    route.setStatus(status);
    if (dateTime != null) {
      route.setDateTime(dateTime);
    }
    if (markdown != null) {
      route.setMarkdown(markdown);
    }
    routeRepository.persistAndFlush(route);
    LineString<G2D> lineString = (LineString<G2D>) Wkt.fromWkt(geometry, WGS84);
    GpxTrack track = new GpxTrack(createdBy, route, lineString, trackPoints, Instant.now());
    gpxTrackRepository.persistAndFlush(track);
    return route;
  }

  @Transactional
  public void updateRoute(Route route) {
    routeRepository.getEntityManager().merge(route);
  }

  @Transactional
  public void deleteRoute(Route route) {
    route.setDeleted(true);
    routeRepository.getEntityManager().merge(route);
  }

  @Transactional
  public void deleteTeam(Team team) {
    team.setDeleted(true);
    teamRepository.getEntityManager().merge(team);
  }

  @Transactional
  public void deleteUserTeam(UserTeam userTeam) {
    userTeam.setDeleted(true);
    userTeamRepository.getEntityManager().merge(userTeam);
  }

  @Transactional
  public Team updateTeam(Team team) {
    return teamRepository.getEntityManager().merge(team);
  }

  @Transactional
  public RouteClimb createRouteClimb(
      User createdBy,
      Route route,
      Integer startDistance,
      Integer endDistance,
      Integer elevationGain,
      BigDecimal averageGradient,
      BigDecimal maxGradient) {
    return createRouteClimb(
        createdBy,
        route,
        null,
        startDistance,
        endDistance,
        elevationGain,
        averageGradient,
        maxGradient,
        null);
  }

  @Transactional
  public RouteClimb createRouteClimb(
      User createdBy,
      Route route,
      @Nullable String name,
      Integer startDistance,
      Integer endDistance,
      Integer elevationGain,
      BigDecimal averageGradient,
      BigDecimal maxGradient,
      @Nullable ClimbCategory category) {
    RouteClimb climb =
        new RouteClimb(
            createdBy,
            route,
            startDistance,
            endDistance,
            elevationGain,
            averageGradient,
            maxGradient);
    climb.setName(name);
    climb.setCategory(category);
    routeClimbRepository.persistAndFlush(climb);
    return climb;
  }

  @Transactional
  public void deleteRouteClimb(RouteClimb climb) {
    climb.setDeleted(true);
    routeClimbRepository.getEntityManager().merge(climb);
  }

  @Inject PlaceRepository placeRepository;

  @Transactional
  public Place createPlace(Team team, User createdBy, String name) {
    return createPlace(team, createdBy, name, true, true);
  }

  @Transactional
  public Place createPlace(
      Team team, User createdBy, String name, boolean startPlace, boolean endPlace) {
    Place place = new Place(createdBy, team, name, startPlace, endPlace);
    placeRepository.persistAndFlush(place);
    return place;
  }

  @Transactional
  public void deletePlace(Place place) {
    place.setDeleted(true);
    placeRepository.getEntityManager().merge(place);
  }

  @Inject PostRepository postRepository;

  @Transactional
  public Post createPost(Team team, User createdBy, String name, Instant dateTime) {
    return createPost(team, createdBy, name, dateTime, Visibility.PUBLIC, Status.PUBLISHED, null);
  }

  @Transactional
  public Post createPost(
      Team team, User createdBy, String name, Instant dateTime, Visibility visibility) {
    return createPost(team, createdBy, name, dateTime, visibility, Status.PUBLISHED, null);
  }

  @Transactional
  public Post createPost(
      Team team,
      User createdBy,
      String name,
      Instant dateTime,
      Visibility visibility,
      Status status) {
    return createPost(team, createdBy, name, dateTime, visibility, status, null);
  }

  @Transactional
  public Post createPost(
      Team team,
      User createdBy,
      String name,
      Instant dateTime,
      Visibility visibility,
      Status status,
      @Nullable Instant publishAt) {
    Post post = new Post(createdBy, team, dateTime, name, SlugService.slugify(name), visibility);
    post.setStatus(status);
    post.setPublishAt(publishAt);
    postRepository.persistAndFlush(post);
    return post;
  }

  @Transactional
  public void deletePost(Post post) {
    post.setDeleted(true);
    postRepository.getEntityManager().merge(post);
  }

  @Transactional
  public void updatePost(Post post) {
    postRepository.getEntityManager().merge(post);
  }

  @Inject AssetRepository assetRepository;

  @Transactional
  public Asset createAsset(Team team, User createdBy, AssetType type, String fileName) {
    Asset asset = new Asset(createdBy, team, type, fileName);
    assetRepository.persistAndFlush(asset);
    return asset;
  }

  @Transactional
  public void deleteAsset(Asset asset) {
    asset.setDeleted(true);
    assetRepository.getEntityManager().merge(asset);
  }

  @Transactional
  public void updateAsset(Asset asset) {
    assetRepository.getEntityManager().merge(asset);
  }

  @Inject TripRepository tripRepository;
  @Inject TripStageRepository tripStageRepository;
  @Inject TripParticipationRepository tripParticipationRepository;

  @Transactional
  public Trip createTrip(Team team, User createdBy, String name, Instant dateTime) {
    return createTrip(team, createdBy, name, dateTime, Visibility.PUBLIC, Status.PUBLISHED, null);
  }

  @Transactional
  public Trip createTrip(
      Team team, User createdBy, String name, Instant dateTime, Visibility visibility) {
    return createTrip(team, createdBy, name, dateTime, visibility, Status.PUBLISHED, null);
  }

  @Transactional
  public Trip createTrip(
      Team team,
      User createdBy,
      String name,
      Instant dateTime,
      Visibility visibility,
      Status status,
      @Nullable Instant publishAt) {
    Trip trip = new Trip(createdBy, team, dateTime, name, SlugService.slugify(name), visibility);
    trip.setStatus(status);
    trip.setPublishAt(publishAt);
    tripRepository.persistAndFlush(trip);
    return trip;
  }

  @Transactional
  public void deleteTrip(Trip trip) {
    trip.setDeleted(true);
    tripRepository.getEntityManager().merge(trip);
  }

  @Transactional
  public TripStage createTripStage(User createdBy, Trip trip, String name) {
    TripStage stage = new TripStage(createdBy, trip, name);
    stage.setSlug(SlugService.slugify(name));
    tripStageRepository.persistAndFlush(stage);
    return stage;
  }

  @Transactional
  public TripStage createTripStage(User createdBy, Trip trip, String name, int sortOrder) {
    TripStage stage = new TripStage(createdBy, trip, name);
    stage.setSlug(SlugService.slugify(name));
    stage.setSortOrder(sortOrder);
    tripStageRepository.persistAndFlush(stage);
    return stage;
  }

  @Transactional
  public void deleteTripStage(TripStage stage) {
    stage.setDeleted(true);
    tripStageRepository.getEntityManager().merge(stage);
  }

  @Transactional
  public TripParticipation createTripParticipation(Trip trip, User user) {
    TripParticipation participation = new TripParticipation(trip, user);
    tripParticipationRepository.persistAndFlush(participation);
    return participation;
  }

  @Transactional
  public void deleteTripParticipation(TripParticipation participation) {
    participation.setDeleted(true);
    tripParticipationRepository.getEntityManager().merge(participation);
  }
}
