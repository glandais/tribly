package fr.pedalons.util;

import static fr.pedalons.common.TokenUtils.generateSecureToken;
import static fr.pedalons.common.TokenUtils.hashToken;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.calendar.CalendarToken;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.common.TeamEntitySlugRedirect;
import fr.pedalons.domain.gpx.GpxPreview;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.platform.DomainAlias;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.*;
import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.ridetemplate.RideTemplateGroup;
import fr.pedalons.domain.route.GpxTrack;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.TeamPage;
import fr.pedalons.domain.team.TeamSlugRedirect;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripParticipation;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.*;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.ad.AdRepository;
import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.repository.calendar.CalendarTokenRepository;
import fr.pedalons.repository.comment.CommentRepository;
import fr.pedalons.repository.common.TeamEntitySlugRedirectRepository;
import fr.pedalons.repository.place.PlaceRepository;
import fr.pedalons.repository.platform.DomainAliasRepository;
import fr.pedalons.repository.platform.DomainRepository;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.repository.ride.RideGroupRepository;
import fr.pedalons.repository.ride.RideParticipationRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.ridetemplate.RideTemplateGroupRepository;
import fr.pedalons.repository.ridetemplate.RideTemplateRepository;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.repository.team.TeamPageRepository;
import fr.pedalons.repository.team.TeamRepository;
import fr.pedalons.repository.team.TeamSlugRedirectRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.trip.TripParticipationRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.repository.trip.TripStageRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.common.SlugService;
import io.github.glandais.gpx.climb.Climbs;
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
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
  @Inject DomainRepository domainRepository;
  @Inject DomainAliasRepository domainAliasRepository;

  /** Standard fixture shared by all resource tests, created in a single transaction. */
  public record StandardFixture(
      Domain domain,
      User user1,
      User user2,
      User user3,
      User user4,
      User user5,
      Team team1,
      Team team2) {}

  /**
   * Creates the standard resource-test fixture (5 users, public team1, private team2) in ONE
   * transaction: the nested @Transactional calls join this one, avoiding ~13 commits per test.
   */
  @Transactional
  public StandardFixture createStandardFixture() {
    Domain domain = getOrCreateDefaultDomain();

    User user1 = createUser(domain, "user1@example.com", "Test User 1");
    User user2 = createUser(domain, "user2@example.com", "Test User 2");
    User user3 = createUser(domain, "user3@example.com", "Test User 3");
    User user4 = createUser(domain, "user4@example.com", "Test User 4");
    User user5 = createUser(domain, "user5@example.com", "Test User 5");

    Team team1 = createTeam(domain, user1, "Team 1", "team-1", Visibility.PUBLIC);
    team1.setJoinable(true);
    team1.setAddMemberAllowed(true);
    addUserToTeam(user2, team1, TeamRole.ORGANIZER);
    addUserToTeam(user3, team1, TeamRole.MEMBER);

    Team team2 = createTeam(domain, user1, "Team 2", "team-2", Visibility.TEAM);
    addUserToTeam(user2, team2, TeamRole.ORGANIZER);
    addUserToTeam(user3, team2, TeamRole.MEMBER);

    return new StandardFixture(domain, user1, user2, user3, user4, user5, team1, team2);
  }

  @Transactional
  public Domain getOrCreateDefaultDomain() {
    return domainRepository
        .findByDomain("localhost")
        .orElseGet(
            () -> {
              Domain domain = new Domain("localhost", "Pedalons", "http://localhost:5173");
              domainRepository.persistAndFlush(domain);
              return domain;
            });
  }

  @Transactional
  public Domain createDomain(String domainName, String name, String baseUrl) {
    Domain domain = new Domain(domainName, name, baseUrl);
    domainRepository.persistAndFlush(domain);
    return domain;
  }

  @Transactional
  public DomainAlias createDomainAlias(
      String hostname, Domain domain, Team pinnedTeam, String name, String baseUrl) {
    DomainAlias alias = new DomainAlias(hostname, domain, pinnedTeam, name, baseUrl);
    domainAliasRepository.persistAndFlush(alias);
    return alias;
  }

  @Transactional
  public void setDomainAliasActive(DomainAlias alias, boolean active) {
    DomainAlias managed = domainAliasRepository.findById(alias.getId());
    managed.setActive(active);
  }

  @Transactional
  public void softDeleteTeam(Team team) {
    Team managed = teamRepository.findById(team.getId());
    managed.setDeleted(true);
  }

  @Transactional
  public void deleteDomain(Domain domain) {
    domain.setDeleted(true);
    domainRepository.getEntityManager().merge(domain);
  }

  @Transactional
  public User createUser(Domain domain, String email, String displayName) {
    User user = new User(domain, email, displayName);
    userRepository.persistAndFlush(user);
    return user;
  }

  @Transactional
  public User createUser(String email, String displayName) {
    Domain domain = getOrCreateDefaultDomain();
    User user = new User(domain, email, displayName);
    userRepository.persistAndFlush(user);
    return user;
  }

  @Transactional
  public User createVerifiedUser(Domain domain, String email, String displayName) {
    User user = new User(domain, email, displayName);
    user.markEmailVerified();
    userRepository.persistAndFlush(user);
    return user;
  }

  @Transactional
  public User createVerifiedUser(String email, String displayName) {
    Domain domain = getOrCreateDefaultDomain();
    User user = new User(domain, email, displayName);
    user.markEmailVerified();
    userRepository.persistAndFlush(user);
    return user;
  }

  @Transactional
  public void markEmailVerified(User user) {
    user.markEmailVerified();
    userRepository.getEntityManager().merge(user);
  }

  public User findUserByEmail(String email) {
    Domain domain = getOrCreateDefaultDomain();
    return userRepository.findByEmailAndDomain(domain.getId(), email).orElseThrow();
  }

  public User findUserByEmail(Domain domain, String email) {
    return userRepository.findByEmailAndDomain(domain.getId(), email).orElseThrow();
  }

  @Transactional
  public void updateUser(User user) {
    userRepository.getEntityManager().merge(user);
  }

  @Transactional
  public void deleteUser(User user) {
    user.setDeleted(true);
    userRepository.getEntityManager().merge(user);
  }

  @Transactional
  public User createPlatformAdminUser(String email, String displayName) {
    Domain domain = getOrCreateDefaultDomain();
    User user = new User(domain, email, displayName);
    user.setPlatformRole(fr.pedalons.enums.PlatformRole.PLATFORM_ADMIN);
    userRepository.persistAndFlush(user);
    return user;
  }

  @Transactional
  public Team findTeamBySlug(Domain domain, String slug) {
    return teamRepository.findBySlugAndDomain(domain.getId(), slug).orElseThrow();
  }

  @Transactional
  public void setTeamJoinable(Team team, boolean joinable) {
    Team managed = teamRepository.findById(team.getId());
    managed.setJoinable(joinable);
  }

  @Transactional
  public void setTeamAddMemberAllowed(Team team, boolean allowed) {
    Team managed = teamRepository.findById(team.getId());
    managed.setAddMemberAllowed(allowed);
  }

  @Transactional
  public void setTeamVisibilityEditable(Team team, boolean editable) {
    Team managed = teamRepository.findById(team.getId());
    managed.setVisibilityEditable(editable);
  }

  @Transactional
  public void setTeamVisibility(Team team, Visibility visibility) {
    Team managed = teamRepository.findById(team.getId());
    managed.setVisibility(visibility);
  }

  @Transactional
  public Team createTeam(User user, String name, String slug, Visibility visibility) {
    Domain domain = user.getDomain();
    Team team = new Team(domain, user, name, slug, visibility);
    teamRepository.persistAndFlush(team);
    addUserToTeam(user, team, TeamRole.ADMIN);
    return team;
  }

  @Transactional
  public Team createTeam(
      Domain domain, User user, String name, String slug, Visibility visibility) {
    Team team = new Team(domain, user, name, slug, visibility);
    teamRepository.persistAndFlush(team);
    addUserToTeam(user, team, TeamRole.ADMIN);
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
    // findById instead of merge: the caller's instance may be stale (e.g. after setRideRoute)
    Ride managed = rideRepository.findById(ride.getId());
    managed.setDeleted(true);
  }

  @Transactional
  public void updateRideGroup(RideGroup group) {
    rideGroupRepository.getEntityManager().merge(group);
  }

  @Transactional
  public void deleteRideGroup(RideGroup group) {
    rideGroupRepository.delete(group);
  }

  @Transactional
  public void deleteParticipation(RideParticipation participation) {
    participationRepository.delete(participation);
  }

  @Transactional
  public void setRideRoute(Ride ride, Route route) {
    ride.setRoute(route);
    rideRepository.getEntityManager().merge(ride);
  }

  @Transactional
  public void setRideGroupRoute(RideGroup group, Route route) {
    group.setRoute(route);
    rideGroupRepository.getEntityManager().merge(group);
  }

  @Transactional
  public void setTripRoute(Trip trip, Route route) {
    trip.setRoute(route);
    tripRepository.getEntityManager().merge(trip);
  }

  @Transactional
  public void setTripStageRoute(TripStage stage, Route route) {
    stage.setRoute(route);
    tripStageRepository.getEntityManager().merge(stage);
  }

  @Transactional
  public Route createRoute(Team team, User createdBy, String name) {
    return createRoute(team, createdBy, name, Visibility.PUBLIC);
  }

  @Transactional
  public Route createRoute(Team team, User createdBy, String name, Visibility visibility) {
    List<GpxTrack.TrackPoint> trackPoints =
        List.of(
            new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0),
            new GpxTrack.TrackPoint(45.1, 6.1, 510.0, 10000.0));
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
    LineString<G2D> lineString = (LineString<G2D>) Wkt.fromWkt(geometry, WGS84);
    GpxTrack track =
        new GpxTrack(createdBy, name, lineString, trackPoints, new Climbs(), 10, 10, 10);
    Route route =
        new Route(createdBy, team, name, SlugService.slugify(name), visibility, SurfaceType.ROAD);
    route.addTrack(track);
    routeRepository.persistAndFlush(route);
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
    LineString<G2D> lineString = (LineString<G2D>) Wkt.fromWkt(geometry, WGS84);
    GpxTrack track =
        new GpxTrack(createdBy, name, lineString, trackPoints, new Climbs(), 10, 10, 10);

    Route route =
        new Route(createdBy, team, name, SlugService.slugify(name), visibility, SurfaceType.ROAD);
    route.addTrack(track);
    route.setStatus(status);
    if (dateTime != null) {
      route.setDateTime(dateTime);
    }
    if (markdown != null) {
      route.setMarkdown(markdown);
    }
    routeRepository.persistAndFlush(route);
    return route;
  }

  /**
   * Create a route with specific filter-testable properties.
   */
  @Transactional
  public Route createRouteWithProperties(
      Team team,
      User createdBy,
      String name,
      Visibility visibility,
      int distance,
      int elevationGain,
      SurfaceType surfaceType,
      WindDirection windDirection,
      double startLat,
      double startLon,
      double endLat,
      double endLon) {
    List<GpxTrack.TrackPoint> trackPoints =
        List.of(
            new GpxTrack.TrackPoint(startLat, startLon, 500.0, 0.0),
            new GpxTrack.TrackPoint(endLat, endLon, 510.0, distance));
    String geometry = String.format("LINESTRING(%f %f,%f %f)", startLon, startLat, endLon, endLat);
    LineString<G2D> lineString = (LineString<G2D>) Wkt.fromWkt(geometry, WGS84);
    GpxTrack track =
        new GpxTrack(
            createdBy, name, lineString, trackPoints, new Climbs(), distance, elevationGain, 0);

    Route route =
        new Route(createdBy, team, name, SlugService.slugify(name), visibility, surfaceType);
    route.addTrack(track);
    route.setDistance((float) distance);
    route.setElevationGain((float) elevationGain);
    route.setElevationLoss((float) 0);
    route.setHilliness((float) (distance > 0 ? (1000 * elevationGain) / distance : 0));
    route.setSurfaceType(surfaceType);
    route.setWindDirection(windDirection);
    route.setStart(
        org.geolatte.geom.builder.DSL.point(
            WGS84, org.geolatte.geom.builder.DSL.g(startLon, startLat)));
    route.setEnd(
        org.geolatte.geom.builder.DSL.point(
            WGS84, org.geolatte.geom.builder.DSL.g(endLon, endLat)));
    routeRepository.persistAndFlush(route);
    return route;
  }

  @Transactional
  public void updateRoute(Route route) {
    routeRepository.getEntityManager().merge(route);
  }

  @Transactional
  public GpxPreview createGpxPreview(Domain domain, User user, java.util.UUID publicId) {
    GpxPreview preview =
        new GpxPreview(
            publicId, domain, user, "preview", 1000f, 100f, -100f, 100f, List.of(), List.of());
    routeRepository.getEntityManager().persist(preview);
    routeRepository.getEntityManager().flush();
    return preview;
  }

  /**
   * Ages a GPX preview so the retention job picks it up. Native query because {@code createdAt} is
   * {@code @CreationTimestamp updatable = false}.
   */
  @Transactional
  public void backdateGpxPreview(GpxPreview preview, int days) {
    routeRepository
        .getEntityManager()
        .createNativeQuery(
            "UPDATE gpx_previews SET created_at = created_at - CAST(:days AS interval) WHERE id ="
                + " :id")
        .setParameter("days", days + " days")
        .setParameter("id", preview.getId())
        .executeUpdate();
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
    userTeamRepository.delete(userTeam);
  }

  @Transactional
  public Team updateTeam(Team team) {
    return teamRepository.getEntityManager().merge(team);
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
  public Place createPlaceWithAddress(Team team, User createdBy, String name, String address) {
    Place place = new Place(createdBy, team, name, true, true);
    place.setAddress(address);
    placeRepository.persistAndFlush(place);
    return place;
  }

  @Transactional
  public void deletePlace(Place place) {
    placeRepository.delete(place);
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

  @Transactional
  public Post getPost(Long id) {
    return postRepository.findById(id);
  }

  @Inject AssetRepository assetRepository;
  @Inject StorageService storageService;

  private static final String ASSETS_PREFIX = "assets";

  @Transactional
  public Asset createAsset(Team team, User createdBy, AssetType type, String fileName) {
    String contentType = guessContentType(fileName);
    long fileId = TSID.fast().toLong();
    Asset asset = new Asset(createdBy, team, type, fileId, fileName, contentType);
    assetRepository.persistAndFlush(asset);

    // Upload dummy content to S3 so the asset can be retrieved
    String key = getAssetKey(team, fileId);
    byte[] dummyContent = ("test content for " + fileName).getBytes(StandardCharsets.UTF_8);
    storageService.store(
        key, new ByteArrayInputStream(dummyContent), contentType, dummyContent.length);

    return asset;
  }

  private String getAssetKey(Team team, long fileId) {
    String teamId = TsidUtils.toString(team.getId());
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return ASSETS_PREFIX + "/" + teamId + "/" + subPath + "/" + idString;
  }

  private String guessContentType(String fileName) {
    String lower = fileName.toLowerCase();
    if (lower.endsWith(".png")) return "image/png";
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
    if (lower.endsWith(".gif")) return "image/gif";
    if (lower.endsWith(".gpx")) return "application/gpx+xml";
    if (lower.endsWith(".fit")) return "application/vnd.ant.fit";
    return MediaType.APPLICATION_OCTET_STREAM;
  }

  @Transactional
  public void deleteAsset(Asset asset) {
    assetRepository.delete(asset);
  }

  @Transactional
  public void updateAsset(Asset asset) {
    assetRepository.getEntityManager().merge(asset);
  }

  @Transactional
  public void backdateAssetUpdatedAt(Asset asset, java.time.Instant updatedAt) {
    assetRepository
        .getEntityManager()
        .createNativeQuery("UPDATE assets SET updated_at = ?1 WHERE id = ?2")
        .setParameter(1, updatedAt)
        .setParameter(2, asset.getId())
        .executeUpdate();
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
    TripStage stage = new TripStage(createdBy, trip, name, SlugService.slugify(name));
    tripStageRepository.persistAndFlush(stage);
    return stage;
  }

  @Transactional
  public TripStage createTripStage(User createdBy, Trip trip, String name, int sortOrder) {
    TripStage stage = new TripStage(createdBy, trip, name, SlugService.slugify(name));
    stage.setSortOrder(sortOrder);
    tripStageRepository.persistAndFlush(stage);
    return stage;
  }

  @Transactional
  public TripStage createTripStage(User createdBy, Trip trip, String name, Instant dateTime) {
    TripStage stage = new TripStage(createdBy, trip, name, SlugService.slugify(name));
    stage.setDateTime(dateTime);
    tripStageRepository.persistAndFlush(stage);
    return stage;
  }

  @Transactional
  public void deleteTripStage(TripStage stage) {
    // findById instead of merge: the caller's instance may be stale (e.g. after setTripStageRoute)
    TripStage managed = tripStageRepository.findById(stage.getId());
    managed.setDeleted(true);
  }

  @Transactional
  public TripParticipation createTripParticipation(Trip trip, User user) {
    TripParticipation participation = new TripParticipation(trip, user);
    tripParticipationRepository.persistAndFlush(participation);
    return participation;
  }

  @Transactional
  public void deleteTripParticipation(TripParticipation participation) {
    tripParticipationRepository.delete(participation);
  }

  @Inject RideTemplateRepository rideTemplateRepository;
  @Inject RideTemplateGroupRepository rideTemplateGroupRepository;

  @Transactional
  public RideTemplate createRideTemplate(Team team, User createdBy, String name, String slug) {
    return createRideTemplate(team, createdBy, name, slug, Visibility.PUBLIC, Status.PUBLISHED);
  }

  @Transactional
  public RideTemplate createRideTemplate(
      Team team, User createdBy, String name, String slug, Visibility visibility, Status status) {
    RideTemplate template = new RideTemplate(createdBy, team, name, slug, name, visibility, status);
    rideTemplateRepository.persistAndFlush(template);
    return template;
  }

  @Transactional
  public void deleteRideTemplate(RideTemplate template) {
    rideTemplateRepository.delete(template);
  }

  @Transactional
  public RideTemplateGroup createRideTemplateGroup(
      User createdBy, RideTemplate template, String name) {
    RideTemplateGroup group = new RideTemplateGroup(createdBy, template, name);
    rideTemplateGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public RideTemplateGroup createRideTemplateGroup(
      User createdBy,
      RideTemplate template,
      String name,
      Integer averageSpeed,
      Integer maxParticipants) {
    RideTemplateGroup group = new RideTemplateGroup(createdBy, template, name);
    group.setAverageSpeed(averageSpeed != null ? (float) averageSpeed : null);
    group.setMaxParticipants(maxParticipants);
    rideTemplateGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public void deleteRideTemplateGroup(RideTemplateGroup group) {
    rideTemplateGroupRepository.delete(group);
  }

  @Inject AdRepository adRepository;

  @Transactional
  public Ad createAd(Team team, User createdBy, String name, AdType adType) {
    return createAd(team, createdBy, name, Instant.now(), adType);
  }

  @Transactional
  public Ad createAd(Team team, User createdBy, String name, Instant dateTime, AdType adType) {
    Ad ad = new Ad(createdBy, team, dateTime, name, SlugService.slugify(name), adType);
    adRepository.persistAndFlush(ad);
    return ad;
  }

  @Transactional
  public void deleteAd(Ad ad) {
    ad.setDeleted(true);
    adRepository.getEntityManager().merge(ad);
  }

  @Inject CommentRepository commentRepository;

  @Transactional
  public Comment createComment(User createdBy, TeamEntity teamEntity, String content) {
    Comment comment = new Comment(createdBy, teamEntity, content);
    commentRepository.persistAndFlush(comment);
    return comment;
  }

  @Transactional
  public Comment createReply(
      User createdBy, TeamEntity teamEntity, Comment parent, String content) {
    Comment reply = new Comment(createdBy, teamEntity, parent, content);
    commentRepository.persistAndFlush(reply);
    return reply;
  }

  @Transactional
  public void deleteComment(Comment comment) {
    commentRepository.delete(comment);
  }

  @Inject TeamPageRepository teamPageRepository;

  @Transactional
  public TeamPage createAdditionalPage(Team team, User createdBy, String name, int order) {
    return createAdditionalPage(team, createdBy, name, order, Visibility.PUBLIC);
  }

  @Transactional
  public TeamPage createAdditionalPage(
      Team team, User createdBy, String name, int order, Visibility visibility) {
    TeamPage page =
        TeamPage.createAdditionalPage(
            createdBy, team, name, SlugService.slugify(name), visibility, order);
    teamPageRepository.persistAndFlush(page);
    return page;
  }

  @Transactional
  public void deleteTeamPage(TeamPage page) {
    page.setDeleted(true);
    teamPageRepository.getEntityManager().merge(page);
  }

  @Inject TeamSlugRedirectRepository teamSlugRedirectRepository;

  @Transactional
  public TeamSlugRedirect createTeamSlugRedirect(String oldSlug, Team team) {
    TeamSlugRedirect redirect = new TeamSlugRedirect(oldSlug, team);
    teamSlugRedirectRepository.persistAndFlush(redirect);
    return redirect;
  }

  @Inject TeamEntitySlugRedirectRepository teamEntitySlugRedirectRepository;

  @Transactional
  public TeamEntitySlugRedirect createTeamEntitySlugRedirect(
      String oldSlug, Team team, Integer entityType, Long entityId) {
    TeamEntitySlugRedirect redirect =
        new TeamEntitySlugRedirect(oldSlug, team, entityType, entityId);
    teamEntitySlugRedirectRepository.persistAndFlush(redirect);
    return redirect;
  }

  @Inject CalendarTokenRepository calendarTokenRepository;

  @Transactional
  public CalendarToken createCalendarToken(User user, String token) {
    CalendarToken calendarToken = new CalendarToken(user, token);
    calendarTokenRepository.persistAndFlush(calendarToken);
    return calendarToken;
  }

  @Transactional
  public void deleteCalendarToken(CalendarToken token) {
    calendarTokenRepository.delete(token);
  }

  // ===== Auth entities =====

  @Inject fr.pedalons.repository.auth.AuthSessionRepository authSessionRepository;
  @Inject fr.pedalons.repository.auth.AuthTokenRepository authTokenRepository;
  @Inject fr.pedalons.repository.auth.PasskeyRepository passkeyRepository;
  @Inject fr.pedalons.repository.auth.WebAuthnChallengeRepository webAuthnChallengeRepository;
  @Inject fr.pedalons.repository.gps.GpsServiceConnectionRepository gpsServiceConnectionRepository;
  @Inject fr.pedalons.repository.gps.DomainGpsCredentialRepository domainGpsCredentialRepository;

  @Transactional
  public fr.pedalons.domain.auth.AuthSession createAuthSession(
      User user, String refreshTokenHash, java.time.Instant expiresAt) {
    var session = new fr.pedalons.domain.auth.AuthSession(user, refreshTokenHash, expiresAt);
    authSessionRepository.persistAndFlush(session);
    return session;
  }

  /**
   * Creates a refresh token for a user and returns the raw token (not the hash). The token is
   * stored as a hash in the database.
   */
  @Transactional
  public String createRefreshTokenForUser(User user) {
    String rawToken = generateSecureToken();
    String tokenHash = hashToken(rawToken);
    var session =
        new fr.pedalons.domain.auth.AuthSession(
            user, tokenHash, java.time.Instant.now().plusSeconds(30 * 24 * 60 * 60));
    authSessionRepository.persistAndFlush(session);
    return rawToken;
  }

  @Transactional
  public fr.pedalons.domain.auth.AuthSession createExpiredAuthSession(
      User user, String refreshTokenHash) {
    var session =
        new fr.pedalons.domain.auth.AuthSession(
            user, refreshTokenHash, java.time.Instant.now().minusSeconds(3600));
    authSessionRepository.persistAndFlush(session);
    return session;
  }

  @Transactional
  public void revokeAuthSession(fr.pedalons.domain.auth.AuthSession session) {
    session.revoke();
    authSessionRepository.getEntityManager().merge(session);
  }

  @Transactional
  public fr.pedalons.domain.auth.AuthToken createAuthToken(
      String email,
      String tokenHash,
      fr.pedalons.enums.AuthTokenType tokenType,
      java.time.Instant expiresAt) {
    var token =
        new fr.pedalons.domain.auth.AuthToken(
            email, tokenHash, tokenType, expiresAt, getOrCreateDefaultDomain().getId());
    authTokenRepository.persistAndFlush(token);
    return token;
  }

  @Transactional
  public fr.pedalons.domain.auth.AuthToken createAuthToken(
      User user,
      String email,
      String tokenHash,
      fr.pedalons.enums.AuthTokenType tokenType,
      java.time.Instant expiresAt) {
    var token =
        new fr.pedalons.domain.auth.AuthToken(
            user, email, tokenHash, tokenType, expiresAt, getOrCreateDefaultDomain().getId());
    authTokenRepository.persistAndFlush(token);
    return token;
  }

  @Transactional
  public fr.pedalons.domain.auth.AuthToken createExpiredAuthToken(
      String email, String tokenHash, fr.pedalons.enums.AuthTokenType tokenType) {
    var token =
        new fr.pedalons.domain.auth.AuthToken(
            email,
            tokenHash,
            tokenType,
            java.time.Instant.now().minusSeconds(3600),
            getOrCreateDefaultDomain().getId());
    authTokenRepository.persistAndFlush(token);
    return token;
  }

  @Transactional
  public void markAuthTokenUsed(fr.pedalons.domain.auth.AuthToken token) {
    token.markUsed();
    authTokenRepository.getEntityManager().merge(token);
  }

  @Transactional
  public fr.pedalons.domain.auth.Passkey createPasskey(
      User user, byte[] credentialId, byte[] publicKey) {
    var passkey = new fr.pedalons.domain.auth.Passkey(user, credentialId, publicKey);
    passkeyRepository.persistAndFlush(passkey);
    return passkey;
  }

  @Transactional
  public void deletePasskey(fr.pedalons.domain.auth.Passkey passkey) {
    passkeyRepository.delete(passkey);
  }

  @Transactional
  public fr.pedalons.domain.auth.WebAuthnChallenge createWebAuthnChallenge(
      User user,
      String email,
      String challenge,
      fr.pedalons.enums.WebAuthnChallengeType challengeType,
      java.time.Instant expiresAt) {
    var webAuthnChallenge =
        new fr.pedalons.domain.auth.WebAuthnChallenge(
            user, email, challenge, challengeType, expiresAt);
    webAuthnChallengeRepository.persistAndFlush(webAuthnChallenge);
    return webAuthnChallenge;
  }

  @Transactional
  public fr.pedalons.domain.auth.WebAuthnChallenge createExpiredWebAuthnChallenge(
      User user,
      String email,
      String challenge,
      fr.pedalons.enums.WebAuthnChallengeType challengeType) {
    var webAuthnChallenge =
        new fr.pedalons.domain.auth.WebAuthnChallenge(
            user, email, challenge, challengeType, java.time.Instant.now().minusSeconds(3600));
    webAuthnChallengeRepository.persistAndFlush(webAuthnChallenge);
    return webAuthnChallenge;
  }

  // ===== GPS Service Connection entities =====

  @Transactional
  public fr.pedalons.domain.gps.GpsServiceConnection createGpsServiceConnection(
      User user, GpsServiceType serviceType) {
    var connection = new fr.pedalons.domain.gps.GpsServiceConnection(user, serviceType);
    connection.setAccessTokenEncrypted(new byte[] {1, 2, 3, 4});
    gpsServiceConnectionRepository.persistAndFlush(connection);
    return connection;
  }

  @Transactional
  public void deleteGpsServiceConnection(fr.pedalons.domain.gps.GpsServiceConnection connection) {
    gpsServiceConnectionRepository.delete(connection);
  }

  @Transactional
  public fr.pedalons.domain.gps.DomainGpsCredential createDomainGpsCredential(
      Domain domain, GpsServiceType serviceType, String clientId) {
    // Merge domain to ensure it's managed in current transaction
    Domain managedDomain = domainGpsCredentialRepository.getEntityManager().merge(domain);
    var credential =
        new fr.pedalons.domain.gps.DomainGpsCredential(managedDomain, serviceType, clientId);
    domainGpsCredentialRepository.persistAndFlush(credential);
    return credential;
  }

  @Transactional
  public fr.pedalons.domain.gps.DomainGpsCredential createDomainGpsCredential(
      Domain domain, GpsServiceType serviceType, String clientId, byte[] clientSecretEncrypted) {
    // Merge domain to ensure it's managed in current transaction
    Domain managedDomain = domainGpsCredentialRepository.getEntityManager().merge(domain);
    var credential =
        new fr.pedalons.domain.gps.DomainGpsCredential(managedDomain, serviceType, clientId);
    credential.setClientSecretEncrypted(clientSecretEncrypted);
    domainGpsCredentialRepository.persistAndFlush(credential);
    return credential;
  }

  @Transactional
  public void deactivateDomainGpsCredential(fr.pedalons.domain.gps.DomainGpsCredential credential) {
    credential.setActive(false);
    domainGpsCredentialRepository.getEntityManager().merge(credential);
  }

  @Inject fr.pedalons.repository.gps.GpsOAuthStateRepository gpsOAuthStateRepository;

  @Transactional
  public fr.pedalons.domain.gps.GpsOAuthState createGpsOAuthState(
      User user, String state, GpsServiceType serviceType, java.time.Instant expiresAt) {
    var oauthState =
        new fr.pedalons.domain.gps.GpsOAuthState(
            user,
            state,
            serviceType,
            expiresAt,
            null,
            "http://localhost/callback",
            getOrCreateDefaultDomain().getId());
    gpsOAuthStateRepository.persistAndFlush(oauthState);
    return oauthState;
  }

  @Transactional
  public fr.pedalons.domain.gps.GpsOAuthState createExpiredGpsOAuthState(
      User user, String state, GpsServiceType serviceType) {
    var oauthState =
        new fr.pedalons.domain.gps.GpsOAuthState(
            user,
            state,
            serviceType,
            java.time.Instant.now().minusSeconds(3600),
            null,
            "http://localhost/callback",
            getOrCreateDefaultDomain().getId());
    gpsOAuthStateRepository.persistAndFlush(oauthState);
    return oauthState;
  }

  // ===== GDPR data export =====

  @Inject fr.pedalons.repository.user.UserExportRepository userExportRepository;

  @Transactional
  public fr.pedalons.domain.user.UserExport createUserExport(Domain domain, User user) {
    var export =
        new fr.pedalons.domain.user.UserExport(
            domain, user, domain.getBaseUrl(), domain.getName(), "fr");
    userExportRepository.persistAndFlush(export);
    return export;
  }

  @Transactional
  public fr.pedalons.domain.user.UserExport createUserExport(
      Domain domain, User user, fr.pedalons.enums.UserExportStatus status) {
    var export = createUserExport(domain, user);
    var managed = userExportRepository.findById(export.getId());
    managed.setStatus(status);
    return managed;
  }

  /** Ages an export so the cooldown check or the stuck-job sweep picks it up. */
  @Transactional
  public void backdateUserExport(fr.pedalons.domain.user.UserExport export, int minutes) {
    userExportRepository
        .getEntityManager()
        .createNativeQuery(
            "UPDATE user_exports SET requested_at = requested_at - CAST(:offset AS interval),"
                + " started_at = started_at - CAST(:offset AS interval) WHERE id = :id")
        .setParameter("offset", minutes + " minutes")
        .setParameter("id", export.getId())
        .executeUpdate();
  }

  @Transactional
  public fr.pedalons.domain.user.UserExport reloadUserExport(Long id) {
    return userExportRepository.findById(id);
  }

  @Transactional
  public fr.pedalons.domain.user.UserExport findLatestUserExport(User user) {
    return userExportRepository.findLatestByUser(user.getId()).orElseThrow();
  }

  /** Plants a recognisable password hash, so redaction tests can assert it never leaves the DB. */
  @Transactional
  public void setUserPasswordHash(User user, String passwordHash) {
    User managed = userRepository.findById(user.getId());
    managed.setPasswordHash(passwordHash);
  }

  @Transactional
  public void updateUserExport(fr.pedalons.domain.user.UserExport export) {
    userExportRepository.getEntityManager().merge(export);
  }
}
