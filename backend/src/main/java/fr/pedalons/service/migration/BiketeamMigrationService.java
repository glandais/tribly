package fr.pedalons.service.migration;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.migration.BiketeamMigrationMap;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.ridetemplate.RideTemplateGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripParticipation;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.asset.AssetsDto;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.places.request.PlaceRequest;
import fr.pedalons.dto.places.response.PlaceDetailDto;
import fr.pedalons.dto.posts.request.PostRequest;
import fr.pedalons.dto.posts.response.PostDto;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.dto.rides.response.RideDto;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.dto.routes.response.RouteDto;
import fr.pedalons.dto.trips.request.StageRequest;
import fr.pedalons.dto.trips.request.TripRequest;
import fr.pedalons.dto.trips.response.TripDto;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.enums.WindDirection;
import fr.pedalons.repository.comment.CommentRepository;
import fr.pedalons.repository.migration.BiketeamMigrationMapRepository;
import fr.pedalons.repository.place.PlaceRepository;
import fr.pedalons.repository.platform.DomainRepository;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.repository.ride.RideGroupRepository;
import fr.pedalons.repository.ride.RideParticipationRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.ridetemplate.RideTemplateGroupRepository;
import fr.pedalons.repository.ridetemplate.RideTemplateRepository;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.repository.team.TeamRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.trip.TripParticipationRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.asset.response.AssetWithFile;
import fr.pedalons.service.place.PlaceService;
import fr.pedalons.service.post.PostService;
import fr.pedalons.service.ride.RideService;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.trip.TripService;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ManagedContext;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Orchestrates the biketeam → tribly migration. Reads from the named "biketeam" datasource
 * (restored biketeam dump) via {@link BiketeamReader}, writes via existing tribly services, and
 * tracks source-row → target-id mappings in {@link BiketeamMigrationMap} for replayability.
 *
 * <p>The runner activates a request-scoped CDI context manually so {@link
 * PedalonsQueryContext#setUserForTest} and {@link DomainResolver#setDomainForTest} apply the same
 * way they do in tests, which makes {@code @CheckAccess} on the called services pass without HTTP.
 */
@ApplicationScoped
public class BiketeamMigrationService {

  private static final Logger LOG = Logger.getLogger(BiketeamMigrationService.class);
  private static final ZoneId PARIS = ZoneId.of("Europe/Paris");

  // Mapping table entity types
  private static final String T_USER = "USER";
  private static final String T_TEAM = "TEAM";
  private static final String T_USER_TEAM = "USER_TEAM";
  private static final String T_PLACE = "PLACE";
  private static final String T_ROUTE = "ROUTE";
  private static final String T_RIDE = "RIDE";
  private static final String T_RIDE_GROUP = "RIDE_GROUP";
  private static final String T_RIDE_PARTICIPATION = "RIDE_PARTICIPATION";
  private static final String T_RIDE_TEMPLATE = "RIDE_TEMPLATE";
  private static final String T_RIDE_TEMPLATE_GROUP = "RIDE_TEMPLATE_GROUP";
  private static final String T_TRIP = "TRIP";
  private static final String T_TRIP_STAGE = "TRIP_STAGE";
  private static final String T_TRIP_PARTICIPATION = "TRIP_PARTICIPATION";
  private static final String T_POST = "POST";
  private static final String T_COMMENT = "COMMENT";
  private static final String T_ASSET = "ASSET";

  @Inject BiketeamMigrationConfig config;
  @Inject BiketeamReader reader;
  @Inject BiketeamMigrationMapRepository mapRepo;

  @Inject DomainResolver domainResolver;
  @Inject PedalonsQueryContext pedalonsContext;

  @Inject DomainRepository domainRepository;
  @Inject TeamRepository teamRepository;
  @Inject UserRepository userRepository;
  @Inject UserTeamRepository userTeamRepository;
  @Inject PlaceRepository placeRepository;
  @Inject RouteRepository routeRepository;
  @Inject RideRepository rideRepository;
  @Inject TripRepository tripRepository;
  @Inject PostRepository postRepository;
  @Inject CommentRepository commentRepository;
  @Inject RideGroupRepository rideGroupRepository;
  @Inject RideParticipationRepository rideParticipationRepository;
  @Inject TripParticipationRepository tripParticipationRepository;
  @Inject RideTemplateRepository rideTemplateRepository;
  @Inject RideTemplateGroupRepository rideTemplateGroupRepository;

  @Inject PlaceService placeService;
  @Inject RouteService routeService;
  @Inject RideService rideService;
  @Inject TripService tripService;
  @Inject PostService postService;
  @Inject AssetService assetService;

  /** Entry point — request scope is activated manually so service-side {@code @CheckAccess} works. */
  public void run() throws Exception {
    reader.verifyConnectivity();

    ManagedContext requestContext = Arc.container().requestContext();
    boolean activated = false;
    if (!requestContext.isActive()) {
      requestContext.activate();
      activated = true;
    }
    try {
      runWithinRequest();
    } finally {
      if (activated) {
        requestContext.terminate();
      }
    }
  }

  private void runWithinRequest() {
    Domain domain = resolveDomain();

    User admin = bootstrapMigrationAdmin(domain);
    domainResolver.setDomainForTest(domain);
    pedalonsContext.setUserForTest(admin);

    Team team = ensureTargetTeam(domain, admin);
    ensureMembership(team, admin, TeamRole.ADMIN);

    String sourceTeam = config.getSourceTeamId();
    LOG.infof("Migrating data scoped to biketeam team_id='%s'", sourceTeam);

    Map<String, Long> userIds = migrateUsers(domain, sourceTeam);
    migrateUserTeams(team, sourceTeam, userIds);

    Map<String, Long> placeIds = migratePlaces(team, sourceTeam);
    Map<String, Long> routeIds = migrateMaps(team, sourceTeam);
    migrateRideTemplates(team, admin, sourceTeam);

    Map<String, Long> postIds = migratePublications(team, sourceTeam);
    Map<String, Long> rideIds = migrateRides(team, sourceTeam, routeIds, placeIds, userIds);
    Map<String, Long> tripIds = migrateTrips(team, sourceTeam, routeIds, userIds);

    migrateMessages(team, admin, sourceTeam, rideIds, tripIds, postIds, userIds);
  }

  // ─── Domain / team / admin ────────────────────────────────────────────────

  @Transactional
  protected Domain resolveDomain() {
    String domainName = config.getTargetDomain();
    return domainRepository
        .findByDomain(domainName)
        .orElseGet(
            () -> {
              LOG.infof(
                  "Target domain '%s' not found — creating it (name='%s', baseUrl='%s')",
                  domainName, config.getTargetDomainName(), config.getTargetDomainBaseUrl());
              Domain d =
                  new Domain(
                      domainName, config.getTargetDomainName(), config.getTargetDomainBaseUrl());
              domainRepository.persistAndFlush(d);
              return d;
            });
  }

  @Transactional
  protected User bootstrapMigrationAdmin(Domain domain) {
    if (config.getAdminEmail().isBlank()) {
      throw new IllegalStateException(
          "pedalons.migration.biketeam.admin-email is blank; cannot bootstrap migration user");
    }
    String email = config.getAdminEmail().toLowerCase(Locale.ROOT);
    User admin =
        userRepository
            .findByEmailAndDomain(domain.getId(), email)
            .orElseGet(
                () -> {
                  User u = new User(domain, email, email);
                  u.setEmailVerified(true);
                  u.setEmailVerifiedAt(Instant.now());
                  userRepository.persist(u);
                  return u;
                });
    LOG.infof("Using configured migration admin '%s'", email);
    return admin;
  }

  @Transactional
  protected Team ensureTargetTeam(Domain domain, User admin) {
    String slug = config.getTargetTeamSlug();
    Optional<Team> existing = teamRepository.findBySlugAndDomain(domain.getId(), slug);
    if (existing.isPresent()) {
      Team t = existing.get();
      mapRepo.upsert(T_TEAM, config.getSourceTeamId(), t.getId());
      return t;
    }
    BiketeamReader.BtTeam src = reader.findTeam(config.getSourceTeamId());
    String displayName = src != null ? src.name() : "N-Peloton";
    Team team = new Team(domain, admin, displayName, slug, Visibility.PUBLIC);
    team.setEnableRoutes(true);
    team.setEnableRides(true);
    team.setEnableTrips(true);
    team.setEnablePosts(true);
    team.setEnableAds(false);
    team.setVisibilityEditable(true);
    team.setJoinable(true);
    team.setAddMemberAllowed(true);
    teamRepository.persistAndFlush(team);
    mapRepo.upsert(T_TEAM, config.getSourceTeamId(), team.getId());
    return team;
  }

  @Transactional
  protected void ensureMembership(Team team, User user, TeamRole role) {
    Optional<UserTeam> existing = userTeamRepository.findByUserAndTeam(user.getId(), team.getId());
    if (existing.isPresent()) {
      UserTeam ut = existing.get();
      if (ut.getRole() != role) {
        ut.setRole(role);
        userTeamRepository.persist(ut);
      }
      return;
    }
    UserTeam ut = new UserTeam(user, user, team, role);
    userTeamRepository.persist(ut);
  }

  // ─── Users ────────────────────────────────────────────────────────────────

  /** Returns biketeam user_id → tribly user_id for users in n-peloton scope. */
  protected Map<String, Long> migrateUsers(Domain domain, String sourceTeam) {
    Set<String> referenced = new HashSet<>();
    reader.findUserRoles(sourceTeam).forEach(r -> referenced.add(r.userId()));
    List<String> rideIds =
        reader.findRides(sourceTeam).stream().map(BiketeamReader.BtRide::id).toList();
    List<String> rideGroupIds =
        reader.findRideGroups(rideIds).stream().map(BiketeamReader.BtRideGroup::id).toList();
    reader.findRideGroupParticipants(rideGroupIds).forEach(p -> referenced.add(p.userId()));
    List<String> tripIds =
        reader.findTrips(sourceTeam).stream().map(BiketeamReader.BtTrip::id).toList();
    reader.findTripParticipants(tripIds).forEach(p -> referenced.add(p.userId()));
    reader.findMessages(sourceTeam).forEach(m -> referenced.add(m.userId()));

    if (referenced.isEmpty()) {
      return Map.of();
    }
    Map<String, Long> idMap = new HashMap<>();
    for (BiketeamReader.BtUser bt : reader.findUsersByIds(new ArrayList<>(referenced))) {
      if (bt.email() == null || bt.email().isBlank()) {
        continue;
      }
      try {
        QuarkusTransaction.requiringNew()
            .run(
                () -> {
                  User user = upsertUserByEmail(domain, bt);
                  mapRepo.upsert(T_USER, bt.id(), user.getId());
                  idMap.put(bt.id(), user.getId());
                });
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate user biketeam.id=%s email=%s", bt.id(), bt.email());
      }
    }
    return idMap;
  }

  private User upsertUserByEmail(Domain domain, BiketeamReader.BtUser bt) {
    String email = bt.email().toLowerCase(Locale.ROOT);
    return userRepository
        .findByEmailAndDomain(domain.getId(), email)
        .map(u -> updateUser(u, bt))
        .orElseGet(() -> createUser(domain, bt));
  }

  private User createUser(Domain domain, BiketeamReader.BtUser bt) {
    String email = bt.email().toLowerCase(Locale.ROOT);
    User user = new User(domain, email, displayNameFor(bt));
    user.setEmailVerified(true);
    user.setEmailVerifiedAt(Instant.now());
    userRepository.persist(user);
    return user;
  }

  private User updateUser(User user, BiketeamReader.BtUser bt) {
    user.setDisplayName(displayNameFor(bt));
    if (!user.isEmailVerified()) {
      user.setEmailVerified(true);
      user.setEmailVerifiedAt(Instant.now());
    }
    if (bt.deletion() && !user.isDeleted()) {
      user.setDeleted(true);
    }
    userRepository.persist(user);
    return user;
  }

  private String displayNameFor(BiketeamReader.BtUser bt) {
    String first = bt.firstName() == null ? "" : bt.firstName().trim();
    String last = bt.lastName() == null ? "" : bt.lastName().trim();
    String dn = (first + " " + last).trim();
    return dn.isEmpty() ? bt.email() : dn;
  }

  // ─── User-team memberships ────────────────────────────────────────────────

  protected void migrateUserTeams(Team team, String sourceTeam, Map<String, Long> userIdsByBtId) {
    for (BiketeamReader.BtUserRole role : reader.findUserRoles(sourceTeam)) {
      Long triblyUserId = userIdsByBtId.get(role.userId());
      if (triblyUserId == null) {
        continue;
      }
      try {
        QuarkusTransaction.requiringNew()
            .run(
                () -> {
                  User user = userRepository.findActiveById(triblyUserId).orElse(null);
                  if (user == null) {
                    return;
                  }
                  ensureMembership(team, user, mapRole(role.role()));
                  userTeamRepository
                      .findByUserAndTeam(user.getId(), team.getId())
                      .ifPresent(
                          ut ->
                              mapRepo.upsert(
                                  T_USER_TEAM, role.teamId() + ":" + role.userId(), ut.getId()));
                });
      } catch (Exception e) {
        LOG.warnf(
            e,
            "Failed to migrate user_role biketeam team=%s user=%s",
            role.teamId(),
            role.userId());
      }
    }
  }

  // ─── Places ───────────────────────────────────────────────────────────────

  protected Map<String, Long> migratePlaces(Team team, String sourceTeam) {
    Map<String, Long> ids = new HashMap<>();
    for (BiketeamReader.BtPlace bt : reader.findPlaces(sourceTeam)) {
      try {
        QuarkusTransaction.requiringNew()
            .run(
                () -> {
                  PlaceRequest req =
                      new PlaceRequest(
                          bt.name(),
                          truncate(bt.address(), 200),
                          null,
                          bt.startPlace(),
                          bt.endPlace(),
                          toPoint(bt.pointLat(), bt.pointLng()));
                  Long mapped = mapRepo.findTriblyId(T_PLACE, bt.id());
                  Place place = null;
                  if (mapped != null) {
                    place = placeRepository.findByIdAndTeam(mapped, team.getId()).orElse(null);
                  }
                  if (place != null) {
                    place.setName(req.name());
                    place.setAddress(req.address());
                    place.setLink(req.link());
                    place.setStartPlace(req.startPlace());
                    place.setEndPlace(req.endPlace());
                    place.setGeometry(req.geometry());
                    placeRepository.persist(place);
                  } else {
                    PlaceDetailDto created = placeService.createPlace(team.getSlug(), req);
                    place =
                        placeRepository
                            .findByIdAndTeam(TsidUtils.toLong(created.id()), team.getId())
                            .orElseThrow();
                  }
                  mapRepo.upsert(T_PLACE, bt.id(), place.getId());
                  ids.put(bt.id(), place.getId());
                });
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate place biketeam.id=%s", bt.id());
      }
    }
    return ids;
  }

  // ─── Routes (Maps + GPX) ─────────────────────────────────────────────────

  protected Map<String, Long> migrateMaps(Team team, String sourceTeam) {
    Map<String, Long> ids = new HashMap<>();
    for (BiketeamReader.BtMap bt : reader.findMaps(sourceTeam)) {
      try {
        QuarkusTransaction.requiringNew()
            .timeout(600)
            .run(() -> migrateOneMap(team, sourceTeam, bt, ids));
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate route biketeam.id=%s", bt.id());
      }
    }
    return ids;
  }

  private void migrateOneMap(
      Team team, String sourceTeam, BiketeamReader.BtMap bt, Map<String, Long> ids) {
    Long mapped = mapRepo.findTriblyId(T_ROUTE, bt.id());
    if (bt.deletion()) {
      if (mapped != null) {
        routeRepository.findByIdOptional(mapped).ifPresent(r -> r.setDeleted(true));
      }
      return;
    }
    Path gpx = locateGpx(sourceTeam, bt.id());
    // Biketeam dropped its per-map visibility flag — all non-deleted routes are public.
    RouteRequest req =
        new RouteRequest(bt.name(), emptyMedia(), mapSurface(bt.type()), Visibility.PUBLIC, null);
    Route route = null;
    if (mapped != null) {
      route = routeRepository.findByIdOptional(mapped).orElse(null);
    }
    if (route != null) {
      routeService.updateRoute(team.getSlug(), route.getSlug(), req, gpx);
    } else {
      RouteDto created = routeService.createRoute(team.getSlug(), req, gpx);
      route = routeRepository.findByIdOptional(TsidUtils.toLong(created.id())).orElseThrow();
    }
    route.setStatus(Status.PUBLISHED);
    WindDirection wd = mapWindDirection(bt.windDirection());
    if (wd != null) {
      route.setWindDirection(wd);
    }
    if (bt.postedAt() != null) {
      route.setDateTime(bt.postedAt().atStartOfDay(PARIS).toInstant());
    }
    routeRepository.persist(route);
    mapRepo.upsert(T_ROUTE, bt.id(), route.getId());
    ids.put(bt.id(), route.getId());
  }

  private @Nullable Path locateGpx(String sourceTeam, String mapId) {
    if (config.getDataDir() == null || config.getDataDir().isBlank()) {
      return null;
    }
    Path candidate = Path.of(config.getDataDir(), "gpx", sourceTeam, mapId + ".gpx");
    return Files.isRegularFile(candidate) ? candidate : null;
  }

  // ─── Ride templates ───────────────────────────────────────────────────────

  protected void migrateRideTemplates(Team team, User admin, String sourceTeam) {
    List<BiketeamReader.BtRideTemplate> templates = reader.findRideTemplates(sourceTeam);
    if (templates.isEmpty()) {
      return;
    }
    Map<String, List<BiketeamReader.BtRideGroupTemplate>> groupsByTemplate = new HashMap<>();
    reader
        .findRideGroupTemplates(templates.stream().map(BiketeamReader.BtRideTemplate::id).toList())
        .forEach(
            g ->
                groupsByTemplate
                    .computeIfAbsent(g.rideTemplateId(), k -> new ArrayList<>())
                    .add(g));

    for (BiketeamReader.BtRideTemplate tpl : templates) {
      try {
        QuarkusTransaction.requiringNew()
            .run(() -> migrateOneRideTemplate(team, admin, tpl, groupsByTemplate));
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate ride template biketeam.id=%s", tpl.id());
      }
    }
  }

  private void migrateOneRideTemplate(
      Team team,
      User admin,
      BiketeamReader.BtRideTemplate tpl,
      Map<String, List<BiketeamReader.BtRideGroupTemplate>> groupsByTemplate) {
    Long mapped = mapRepo.findTriblyId(T_RIDE_TEMPLATE, tpl.id());
    RideTemplate target =
        mapped != null ? rideTemplateRepository.findByIdOptional(mapped).orElse(null) : null;
    if (target == null) {
      String slug = uniqueRideTemplateSlug(team.getId(), tpl.name());
      target =
          new RideTemplate(
              admin,
              team,
              tpl.name(),
              slug,
              biketeamToMarkdown(tpl.description()),
              Visibility.PUBLIC,
              Status.PUBLISHED);
      rideTemplateRepository.persistAndFlush(target);
    } else {
      target.setName(tpl.name());
      target.setMarkdown(biketeamToMarkdown(tpl.description()));
      rideTemplateRepository.persist(target);
    }
    mapRepo.upsert(T_RIDE_TEMPLATE, tpl.id(), target.getId());

    target.getGroups().clear();
    rideTemplateRepository.flush();
    int sortOrder = 0;
    for (BiketeamReader.BtRideGroupTemplate g :
        groupsByTemplate.getOrDefault(tpl.id(), List.of())) {
      RideTemplateGroup grp = new RideTemplateGroup(admin, target, g.name());
      grp.setTime(g.meetingTime());
      grp.setAverageSpeed(toFloat(g.averageSpeed()));
      grp.setSortOrder(sortOrder++);
      target.addGroup(grp);
      rideTemplateGroupRepository.persist(grp);
      mapRepo.upsert(T_RIDE_TEMPLATE_GROUP, g.id(), grp.getId());
    }
  }

  private String uniqueRideTemplateSlug(Long teamId, String name) {
    String base = slugify(name);
    String s = base;
    int n = 2;
    while (rideTemplateRepository.existsByTeamAndSlug(teamId, s)) {
      s = base + "-" + n++;
    }
    return s;
  }

  // ─── Posts ────────────────────────────────────────────────────────────────

  protected Map<String, Long> migratePublications(Team team, String sourceTeam) {
    Map<String, Long> ids = new HashMap<>();
    for (BiketeamReader.BtPublication bt : reader.findPublications(sourceTeam)) {
      if (bt.deletion()) {
        continue;
      }
      try {
        QuarkusTransaction.requiringNew()
            .timeout(120)
            .run(() -> migrateOnePublication(team, sourceTeam, bt, ids));
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate publication biketeam.id=%s", bt.id());
      }
    }
    return ids;
  }

  private void migrateOnePublication(
      Team team, String sourceTeam, BiketeamReader.BtPublication bt, Map<String, Long> ids) {
    Long mapped = mapRepo.findTriblyId(T_POST, bt.id());
    Post existing = mapped != null ? postRepository.findByIdOptional(mapped).orElse(null) : null;
    PostRequest req =
        new PostRequest(
            bt.title(),
            mediaWithExisting(biketeamToMarkdown(bt.content()), existing),
            bt.publishedAt() != null ? bt.publishedAt() : Instant.now(),
            mapStatus(bt.publishedStatus()),
            Visibility.PUBLIC,
            null);
    Post post;
    if (existing != null) {
      postService.updatePost(team.getSlug(), existing.getSlug(), req);
      post = existing;
    } else {
      PostDto created = postService.createPost(team.getSlug(), req);
      post = postRepository.findByIdOptional(TsidUtils.toLong(created.getId())).orElseThrow();
    }
    mapRepo.upsert(T_POST, bt.id(), post.getId());
    ids.put(bt.id(), post.getId());
    attachImage(post, sourceTeam, "pub-images", bt.id());
  }

  // ─── Rides ────────────────────────────────────────────────────────────────

  protected Map<String, Long> migrateRides(
      Team team,
      String sourceTeam,
      Map<String, Long> routeIds,
      Map<String, Long> placeIds,
      Map<String, Long> userIds) {
    Map<String, Long> ids = new HashMap<>();
    List<BiketeamReader.BtRide> rides = reader.findRides(sourceTeam);
    if (rides.isEmpty()) {
      return ids;
    }
    Map<String, List<BiketeamReader.BtRideGroup>> groupsByRide = new HashMap<>();
    reader
        .findRideGroups(rides.stream().map(BiketeamReader.BtRide::id).toList())
        .forEach(g -> groupsByRide.computeIfAbsent(g.rideId(), k -> new ArrayList<>()).add(g));

    Map<String, List<BiketeamReader.BtRideGroupParticipant>> partsByGroup = new HashMap<>();
    List<String> allGroupIds =
        groupsByRide.values().stream()
            .flatMap(List::stream)
            .map(BiketeamReader.BtRideGroup::id)
            .toList();
    reader
        .findRideGroupParticipants(allGroupIds)
        .forEach(p -> partsByGroup.computeIfAbsent(p.rideGroupId(), k -> new ArrayList<>()).add(p));

    for (BiketeamReader.BtRide bt : rides) {
      if (bt.deletion()) {
        continue;
      }
      try {
        QuarkusTransaction.requiringNew()
            .timeout(120)
            .run(
                () ->
                    migrateOneRide(
                        team,
                        sourceTeam,
                        bt,
                        groupsByRide,
                        partsByGroup,
                        routeIds,
                        placeIds,
                        userIds,
                        ids));
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate ride biketeam.id=%s", bt.id());
      }
    }
    return ids;
  }

  private void migrateOneRide(
      Team team,
      String sourceTeam,
      BiketeamReader.BtRide bt,
      Map<String, List<BiketeamReader.BtRideGroup>> groupsByRide,
      Map<String, List<BiketeamReader.BtRideGroupParticipant>> partsByGroup,
      Map<String, Long> routeIds,
      Map<String, Long> placeIds,
      Map<String, Long> userIds,
      Map<String, Long> ids) {
    List<BiketeamReader.BtRideGroup> groups = groupsByRide.getOrDefault(bt.id(), List.of());
    Instant dateTime = atParis(bt.date(), earliestMeetingTime(groups));
    List<GroupRequest> groupRequests =
        groups.stream()
            .map(
                g ->
                    GroupRequest.builder()
                        .name(g.name())
                        .time(g.meetingTime())
                        .averageSpeed(toFloat(g.averageSpeed()))
                        .routeSlug(routeSlugFromBiketeamId(routeIds, g.mapId()))
                        .build())
            .toList();
    Long mapped = mapRepo.findTriblyId(T_RIDE, bt.id());
    Ride existing = mapped != null ? rideRepository.findByIdOptional(mapped).orElse(null) : null;
    RideRequest req =
        new RideRequest(
            bt.title(),
            mediaWithExisting(biketeamToMarkdown(bt.description()), existing),
            dateTime,
            mapStatus(bt.publishedStatus()),
            Visibility.PUBLIC,
            null,
            placeIdString(placeIds, bt.startPlaceId()),
            placeIdString(placeIds, bt.endPlaceId()),
            null,
            groupRequests);

    Ride ride;
    if (existing != null) {
      rideService.updateRide(team.getSlug(), existing.getSlug(), req);
      ride = rideRepository.findByIdOptional(mapped).orElseThrow();
    } else {
      RideDto created = rideService.createRide(team.getSlug(), req);
      ride = rideRepository.findByIdOptional(TsidUtils.toLong(created.getId())).orElseThrow();
    }
    ids.put(bt.id(), ride.getId());
    mapRepo.upsert(T_RIDE, bt.id(), ride.getId());

    List<RideGroup> tribGroups = new ArrayList<>(ride.getGroups());
    tribGroups.sort(Comparator.comparingInt(RideGroup::getSortOrder));
    for (int i = 0; i < Math.min(tribGroups.size(), groups.size()); i++) {
      mapRepo.upsert(T_RIDE_GROUP, groups.get(i).id(), tribGroups.get(i).getId());
    }

    for (BiketeamReader.BtRideGroup g : groups) {
      Long triblyGroupId = mapRepo.findTriblyId(T_RIDE_GROUP, g.id());
      if (triblyGroupId == null) continue;
      RideGroup tribGroup = rideGroupRepository.findByIdOptional(triblyGroupId).orElse(null);
      if (tribGroup == null) continue;
      for (BiketeamReader.BtRideGroupParticipant p : partsByGroup.getOrDefault(g.id(), List.of())) {
        Long triblyUserId = userIds.get(p.userId());
        if (triblyUserId == null) continue;
        User u = userRepository.findActiveById(triblyUserId).orElse(null);
        if (u == null) continue;
        String mappingKey = g.id() + ":" + p.userId();
        if (mapRepo.findTriblyId(T_RIDE_PARTICIPATION, mappingKey) != null) continue;
        if (rideParticipationRepository
            .findByUserAndGroup(u.getId(), tribGroup.getId())
            .isPresent()) continue;
        RideParticipation rp = new RideParticipation(tribGroup, u);
        rideParticipationRepository.persist(rp);
        mapRepo.upsert(T_RIDE_PARTICIPATION, mappingKey, rp.getId());
      }
    }

    attachImage(ride, sourceTeam, "ride-images", bt.id());
  }

  // ─── Trips ────────────────────────────────────────────────────────────────

  protected Map<String, Long> migrateTrips(
      Team team, String sourceTeam, Map<String, Long> routeIds, Map<String, Long> userIds) {
    Map<String, Long> ids = new HashMap<>();
    List<BiketeamReader.BtTrip> trips = reader.findTrips(sourceTeam);
    if (trips.isEmpty()) {
      return ids;
    }
    Map<String, List<BiketeamReader.BtTripStage>> stagesByTrip = new HashMap<>();
    reader
        .findTripStages(trips.stream().map(BiketeamReader.BtTrip::id).toList())
        .forEach(s -> stagesByTrip.computeIfAbsent(s.tripId(), k -> new ArrayList<>()).add(s));

    Map<String, List<BiketeamReader.BtTripParticipant>> partsByTrip = new HashMap<>();
    reader
        .findTripParticipants(trips.stream().map(BiketeamReader.BtTrip::id).toList())
        .forEach(p -> partsByTrip.computeIfAbsent(p.tripId(), k -> new ArrayList<>()).add(p));

    for (BiketeamReader.BtTrip bt : trips) {
      if (bt.deletion()) {
        continue;
      }
      try {
        QuarkusTransaction.requiringNew()
            .timeout(120)
            .run(
                () ->
                    migrateOneTrip(
                        team, sourceTeam, bt, stagesByTrip, partsByTrip, routeIds, userIds, ids));
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate trip biketeam.id=%s", bt.id());
      }
    }
    return ids;
  }

  private void migrateOneTrip(
      Team team,
      String sourceTeam,
      BiketeamReader.BtTrip bt,
      Map<String, List<BiketeamReader.BtTripStage>> stagesByTrip,
      Map<String, List<BiketeamReader.BtTripParticipant>> partsByTrip,
      Map<String, Long> routeIds,
      Map<String, Long> userIds,
      Map<String, Long> ids) {
    Instant dateTime = atParis(bt.startDate(), bt.meetingTime());
    List<BiketeamReader.BtTripStage> stages = stagesByTrip.getOrDefault(bt.id(), List.of());
    List<StageRequest> stageRequests = new ArrayList<>();
    for (BiketeamReader.BtTripStage s : stages) {
      stageRequests.add(
          StageRequest.builder()
              .name(s.name())
              .dateTime(atParis(s.date(), null))
              .routeSlug(routeSlugFromBiketeamId(routeIds, s.mapId()))
              .startPlaceId(null)
              .endPlaceId(null)
              .media(emptyMedia())
              .build());
    }
    Long mapped = mapRepo.findTriblyId(T_TRIP, bt.id());
    Trip existing = mapped != null ? tripRepository.findByIdOptional(mapped).orElse(null) : null;
    TripRequest req =
        new TripRequest(
            bt.title(),
            mediaWithExisting(biketeamToMarkdown(bt.description()), existing),
            dateTime,
            mapStatus(bt.publishedStatus()),
            Visibility.PUBLIC,
            null,
            null,
            stageRequests);

    Trip trip;
    if (existing != null) {
      tripService.updateTrip(team.getSlug(), existing.getSlug(), req);
      trip = tripRepository.findByIdOptional(mapped).orElseThrow();
    } else {
      TripDto created = tripService.createTrip(team.getSlug(), req);
      trip = tripRepository.findByIdOptional(TsidUtils.toLong(created.getId())).orElseThrow();
    }
    ids.put(bt.id(), trip.getId());
    mapRepo.upsert(T_TRIP, bt.id(), trip.getId());

    for (BiketeamReader.BtTripParticipant p : partsByTrip.getOrDefault(bt.id(), List.of())) {
      Long triblyUserId = userIds.get(p.userId());
      if (triblyUserId == null) continue;
      User u = userRepository.findActiveById(triblyUserId).orElse(null);
      if (u == null) continue;
      if (tripParticipationRepository.findByUserAndTrip(u.getId(), trip.getId()).isPresent())
        continue;
      TripParticipation tp = new TripParticipation(trip, u);
      tripParticipationRepository.persist(tp);
      mapRepo.upsert(T_TRIP_PARTICIPATION, bt.id() + ":" + p.userId(), tp.getId());
    }

    List<TripStage> tribStages = new ArrayList<>(trip.getStages());
    tribStages.sort(Comparator.comparingInt(TripStage::getSortOrder));
    for (int i = 0; i < Math.min(tribStages.size(), stages.size()); i++) {
      mapRepo.upsert(T_TRIP_STAGE, stages.get(i).id(), tribStages.get(i).getId());
    }

    attachImage(trip, sourceTeam, "trip-images", bt.id());
  }

  // ─── Comments (Messages) ──────────────────────────────────────────────────

  protected void migrateMessages(
      Team team,
      User admin,
      String sourceTeam,
      Map<String, Long> rideIds,
      Map<String, Long> tripIds,
      Map<String, Long> postIds,
      Map<String, Long> userIds) {
    List<BiketeamReader.BtMessage> messages = new ArrayList<>(reader.findMessages(sourceTeam));
    messages.sort(Comparator.comparing(BiketeamReader.BtMessage::publishedAt));

    for (BiketeamReader.BtMessage m : messages) {
      try {
        QuarkusTransaction.requiringNew()
            .run(() -> migrateOneMessage(m, rideIds, tripIds, postIds, userIds));
      } catch (Exception e) {
        LOG.warnf(e, "Failed to migrate message biketeam.id=%s", m.id());
      }
    }
  }

  private void migrateOneMessage(
      BiketeamReader.BtMessage m,
      Map<String, Long> rideIds,
      Map<String, Long> tripIds,
      Map<String, Long> postIds,
      Map<String, Long> userIds) {
    if (mapRepo.findTriblyId(T_COMMENT, m.id()) != null) {
      return;
    }
    Long triblyUserId = userIds.get(m.userId());
    if (triblyUserId == null) return;
    User actor = userRepository.findActiveById(triblyUserId).orElse(null);
    if (actor == null) return;
    TeamEntity target = resolveCommentTarget(m, rideIds, tripIds, postIds);
    if (target == null) return;
    Comment parent = null;
    if (m.replyToId() != null) {
      Long parentId = mapRepo.findTriblyId(T_COMMENT, m.replyToId());
      if (parentId != null) {
        parent = commentRepository.findById(parentId);
      }
    }
    Comment comment =
        parent != null
            ? new Comment(actor, target, parent, m.content())
            : new Comment(actor, target, m.content());
    commentRepository.persistAndFlush(comment);
    mapRepo.upsert(T_COMMENT, m.id(), comment.getId());
  }

  private @Nullable TeamEntity resolveCommentTarget(
      BiketeamReader.BtMessage m,
      Map<String, Long> rideIds,
      Map<String, Long> tripIds,
      Map<String, Long> postIds) {
    String type = m.type() == null ? "" : m.type().toUpperCase(Locale.ROOT);
    return switch (type) {
      case "RIDE" -> {
        Long id = rideIds.get(m.targetId());
        yield id != null ? rideRepository.findByIdOptional(id).orElse(null) : null;
      }
      case "TRIP" -> {
        Long id = tripIds.get(m.targetId());
        yield id != null ? tripRepository.findByIdOptional(id).orElse(null) : null;
      }
      // TEAM-scoped messages have no equivalent target in tribly — dropped
      default -> null;
    };
  }

  // ─── Asset attachment helpers ─────────────────────────────────────────────

  /**
   * Attach the first matching image (any extension) found at
   * {gpx-dir}/{subdir}/{team}/{entityId}.* — pub-images / ride-images / trip-images.
   *
   * <p>After upload we (a) read pixel dimensions from the local file and (b) append a
   * {@code ::asset{id="..."}} directive to the entity's markdown. Without the directive, tribly's
   * MediaEditor wouldn't render the image and {@code AssetService.updateAssets} would purge it on
   * the first edit-save (it only keeps images referenced from markdown).
   */
  private void attachImage(
      TeamEntity entity, String sourceTeam, String subdir, String biketeamEntityId) {
    if (config.getDataDir() == null || config.getDataDir().isBlank()) {
      return;
    }
    Path dir = Path.of(config.getDataDir(), subdir, sourceTeam);
    if (!Files.isDirectory(dir)) {
      return;
    }
    Path image;
    try (Stream<Path> stream = Files.list(dir)) {
      image =
          stream
              .filter(
                  p -> {
                    String n = p.getFileName().toString();
                    int dot = n.lastIndexOf('.');
                    String base = dot == -1 ? n : n.substring(0, dot);
                    return base.equals(biketeamEntityId);
                  })
              .findFirst()
              .orElse(null);
    } catch (IOException e) {
      LOG.warnf(e, "Failed to list %s", dir);
      return;
    }
    if (image == null) {
      return;
    }
    String key = subdir + ":" + biketeamEntityId;
    Long alreadyMapped = mapRepo.findTriblyId(T_ASSET, key);
    if (alreadyMapped != null) {
      // Idempotent replay: ensure the directive is present even if the asset was migrated
      // previously.
      ensureAssetDirective(entity, alreadyMapped);
      return;
    }
    try (InputStream in = Files.newInputStream(image)) {
      AssetWithFile awf =
          assetService.addAsset(entity, AssetType.IMAGE, image.getFileName().toString());
      Files.copy(in, awf.file().toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      assetService.uploadAssetFile(awf.asset());
      readImageDimensions(image, awf.asset());
      ensureAssetDirective(entity, awf.asset().getId());
      mapRepo.upsert(T_ASSET, key, awf.asset().getId());
    } catch (IOException e) {
      LOG.warnf(e, "Failed to upload image %s for entity %s", image, biketeamEntityId);
    }
  }

  private void readImageDimensions(Path image, fr.pedalons.domain.asset.Asset asset) {
    try (InputStream in = Files.newInputStream(image)) {
      java.awt.image.BufferedImage bi = javax.imageio.ImageIO.read(in);
      if (bi != null) {
        asset.setWidth(bi.getWidth());
        asset.setHeight(bi.getHeight());
      }
    } catch (IOException e) {
      LOG.debugf("Could not read dimensions for %s: %s", image, e.getMessage());
    }
  }

  /** Append {@code ::asset{id="<tsid>"}} to the entity markdown if not already present. */
  private void ensureAssetDirective(TeamEntity entity, long assetId) {
    String tsid = TsidUtils.toString(assetId);
    String directive = "::asset{id=\"" + tsid + "\"}";
    String md = entity.getMarkdown() == null ? "" : entity.getMarkdown();
    if (md.contains(directive)) {
      return;
    }
    String separator = md.isEmpty() ? "" : "\n\n";
    entity.setMarkdown(md + separator + directive);
  }

  // ─── Mapping helpers ──────────────────────────────────────────────────────

  private static @Nullable Point<G2D> toPoint(@Nullable Double lat, @Nullable Double lng) {
    if (lat == null || lng == null) {
      return null;
    }
    return point(WGS84, g(lng, lat));
  }

  private static SurfaceType mapSurface(@Nullable String biketeamType) {
    if (biketeamType == null) {
      return SurfaceType.ROAD;
    }
    return switch (biketeamType.toUpperCase(Locale.ROOT)) {
      case "GRAVEL" -> SurfaceType.GRAVEL;
      case "MTB" -> SurfaceType.MTB;
      default -> SurfaceType.ROAD;
    };
  }

  private static @Nullable WindDirection mapWindDirection(@Nullable String biketeamWd) {
    if (biketeamWd == null) {
      return null;
    }
    return switch (biketeamWd.toUpperCase(Locale.ROOT)) {
      case "NORTH" -> WindDirection.NORTH;
      case "NORTHEAST" -> WindDirection.NORTH_EAST;
      case "EAST" -> WindDirection.EAST;
      case "SOUTHEAST" -> WindDirection.SOUTH_EAST;
      case "SOUTH" -> WindDirection.SOUTH;
      case "SOUTHWEST" -> WindDirection.SOUTH_WEST;
      case "WEST" -> WindDirection.WEST;
      case "NORTHWEST" -> WindDirection.NORTH_WEST;
      default -> null;
    };
  }

  private static Status mapStatus(@Nullable String biketeamStatus) {
    if (biketeamStatus == null) {
      return Status.DRAFT;
    }
    return switch (biketeamStatus.toUpperCase(Locale.ROOT)) {
      case "PUBLISHED" -> Status.PUBLISHED;
      default -> Status.DRAFT;
    };
  }

  private static TeamRole mapRole(@Nullable String biketeamRole) {
    if (biketeamRole == null) {
      return TeamRole.MEMBER;
    }
    return "ADMIN".equalsIgnoreCase(biketeamRole) ? TeamRole.ADMIN : TeamRole.MEMBER;
  }

  private static Instant atParis(@Nullable LocalDate date, @Nullable LocalTime time) {
    LocalDate d = date != null ? date : LocalDate.now(PARIS);
    LocalTime t = time != null ? time : LocalTime.MIDNIGHT;
    return d.atTime(t).atZone(PARIS).toInstant();
  }

  private static @Nullable LocalTime earliestMeetingTime(List<BiketeamReader.BtRideGroup> groups) {
    return groups.stream()
        .map(BiketeamReader.BtRideGroup::meetingTime)
        .filter(Objects::nonNull)
        .min(Comparator.naturalOrder())
        .orElse(null);
  }

  private static @Nullable Float toFloat(@Nullable Double d) {
    return d == null ? null : d.floatValue();
  }

  private @Nullable String routeSlugFromBiketeamId(
      Map<String, Long> routeIds, @Nullable String biketeamMapId) {
    if (biketeamMapId == null) return null;
    Long triblyId = routeIds.get(biketeamMapId);
    if (triblyId == null) {
      triblyId = mapRepo.findTriblyId(T_ROUTE, biketeamMapId);
    }
    if (triblyId == null) return null;
    return routeRepository.findByIdOptional(triblyId).map(Route::getSlug).orElse(null);
  }

  private static @Nullable String placeIdString(Map<String, Long> placeIds, @Nullable String btId) {
    if (btId == null) return null;
    Long t = placeIds.get(btId);
    return t != null ? TsidUtils.toString(t) : null;
  }

  private MediaDto emptyMedia() {
    return MediaDto.builder().build();
  }

  private MediaDto mediaWithMarkdown(@Nullable String md) {
    return new MediaDto(md == null ? "" : md, AssetsDto.builder().build());
  }

  /**
   * Build the MediaDto for an update call. Preserves the existing entity's IMAGE assets across the
   * replay by ensuring (a) every existing IMAGE asset has a {@code ::asset{id="..."}} directive in
   * the request markdown, and (b) every existing asset is included in {@code assets.images}. Both
   * conditions are required for {@code AssetService.updateAssets} not to purge them.
   */
  private MediaDto mediaWithExisting(
      @Nullable String sourceMarkdown, @Nullable TeamEntity existing) {
    String src = sourceMarkdown == null ? "" : sourceMarkdown;
    if (existing == null) {
      return new MediaDto(src, AssetsDto.builder().build());
    }
    String mergedMarkdown = mergeAssetDirectives(src, existing.getMarkdown());
    AssetsDto existingAssets = assetService.getAssetsDto(existing);
    // Backfill: directives for image assets that pre-exist but aren't yet referenced in markdown.
    StringBuilder mdBuilder = new StringBuilder(mergedMarkdown);
    for (fr.pedalons.dto.common.asset.AssetDto img : existingAssets.images()) {
      String directive = "::asset{id=\"" + img.id() + "\"}";
      if (mdBuilder.indexOf(directive) < 0) {
        if (mdBuilder.length() > 0) {
          mdBuilder.append("\n\n");
        }
        mdBuilder.append(directive);
      }
    }
    return new MediaDto(mdBuilder.toString(), existingAssets);
  }

  private static final java.util.regex.Pattern ASSET_DIRECTIVE_RE =
      java.util.regex.Pattern.compile("::asset\\{[^}]*\\}");

  private static String mergeAssetDirectives(String src, @Nullable String existingMd) {
    if (existingMd == null || existingMd.isEmpty()) {
      return src;
    }
    StringBuilder out = new StringBuilder(src);
    java.util.regex.Matcher m = ASSET_DIRECTIVE_RE.matcher(existingMd);
    while (m.find()) {
      String directive = m.group();
      if (out.indexOf(directive) < 0) {
        if (out.length() > 0) {
          out.append("\n\n");
        }
        out.append(directive);
      }
    }
    return out.toString();
  }

  private static String slugify(String name) {
    String s =
        name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+)|(-+$)", "");
    return s.isEmpty() ? "item" : s;
  }

  private static @Nullable String truncate(@Nullable String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max);
  }

  /**
   * Convert biketeam plain-text descriptions to Markdown that renders the same way. Biketeam
   * treated every {@code \n} as a visible line break; in Markdown, a lone {@code \n} is collapsed.
   * We convert each isolated {@code \n} to {@code "  \n"} (two trailing spaces + LF = hard break)
   * and leave existing paragraph breaks ({@code \n\n}) untouched.
   */
  private static String biketeamToMarkdown(@Nullable String s) {
    if (s == null || s.isEmpty()) return "";
    String normalized = s.replace("\r\n", "\n").replace("\r", "\n");
    return normalized.replaceAll("(?<!\\n)\\n(?!\\n)", "  \n");
  }
}
