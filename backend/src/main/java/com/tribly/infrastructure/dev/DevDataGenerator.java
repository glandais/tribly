package com.tribly.infrastructure.dev;

import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import com.tribly.common.GeoPoint;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.ads.request.AdRequest;
import com.tribly.dto.comments.request.CommentRequest;
import com.tribly.dto.comments.response.CommentDto;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.pages.request.TeamPageRequest;
import com.tribly.dto.places.request.PlaceRequest;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.ridetemplates.request.RideTemplateGroupRequest;
import com.tribly.dto.ridetemplates.request.RideTemplateRequest;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.dto.routes.response.RouteDto;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.trips.request.StageRequest;
import com.tribly.dto.trips.request.TripRequest;
import com.tribly.dto.trips.response.TripDto;
import com.tribly.enums.*;
import com.tribly.repository.platform.DomainRepository;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.ad.AdService;
import com.tribly.service.comment.CommentService;
import com.tribly.service.page.TeamPageService;
import com.tribly.service.place.PlaceService;
import com.tribly.service.post.PostService;
import com.tribly.service.ride.RideService;
import com.tribly.service.ridetemplate.RideTemplateService;
import com.tribly.service.route.RouteService;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.team.TeamMembershipService;
import com.tribly.service.team.TeamService;
import com.tribly.service.trip.TripService;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.geolatte.geom.builder.DSL;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
@IfBuildProfile("dev")
public class DevDataGenerator {

  private static final Logger LOG = Logger.getLogger(DevDataGenerator.class);

  // Paris area coordinates
  private static final double BASE_LAT = 48.8566;
  private static final double BASE_LON = 2.3522;

  private static final int TEAM_COUNT = 20;
  // Entity counts per team
  private static final int ROUTES_PER_TEAM = 6;
  private static final int RIDES_PER_TEAM = 10;
  private static final int POSTS_PER_TEAM = 10;
  private static final int TRIPS_PER_TEAM = 3;
  private static final int ADS_PER_TEAM = 5;
  private static final int COMMENTS_PER_TEAM_PER_TYPE = 10;

  @Inject TriblyQueryContext triblyContext;
  @Inject UserRepository userRepository;
  @Inject DomainRepository domainRepository;
  @Inject RouteService routeService;
  @Inject TeamService teamService;
  @Inject TeamMembershipService teamMembershipService;
  @Inject RideService rideService;
  @Inject PlaceService placeService;
  @Inject PostService postService;
  @Inject TripService tripService;
  @Inject RideTemplateService rideTemplateService;
  @Inject AdService adService;
  @Inject CommentService commentService;
  @Inject TeamPageService teamPageService;

  private List<User> users;
  private final Random r = new Random();

  public void generate() {
    LOG.info("Starting dev data generation...");
    users = createUsers();

    for (int i = 0; i < TEAM_COUNT; i++) {
      createTeam();
    }
  }

  @Transactional
  protected List<User> createUsers() {
    // Get or create default domain
    Domain domain = getOrCreateDefaultDomain();

    List<User> createdUsers = new ArrayList<>();
    String[][] userData = {
      {"admin@example.com", "Admin User"},
      {"user1@example.com", "User One"},
      {"user2@example.com", "User Two"},
      {"user3@example.com", "User Three"},
      {"user4@example.com", "User Four"},
      {"user5@example.com", "User Five"},
      {"user6@example.com", "User Six"}
    };

    for (String[] data : userData) {
      User user = new User(domain, data[0], data[1]);
      userRepository.persistAndFlush(user);
      createdUsers.add(user);
    }
    LOG.infof("Created %d users", createdUsers.size());
    return createdUsers;
  }

  private Domain getOrCreateDefaultDomain() {
    return domainRepository
        .findByDomain("localhost")
        .orElseGet(
            () -> {
              Domain domain = new Domain("localhost", "Tribly", "http://localhost:5173");
              domainRepository.persistAndFlush(domain);
              return domain;
            });
  }

  String[] teamPrefix = {"Cycling Club ", "Velo Team ", "Bike Club ", "Peloton ", "Riders "};

  String[] teamSuffix = {
    "Paris", "Lyon", "Bordeaux", "Marseille", "Toulouse",
  };

  protected void createTeam() {
    int userIndex = r.nextInt(users.size());
    User admin = users.get(userIndex);
    triblyContext.setUserForTest(admin);

    String name =
        teamPrefix[r.nextInt(teamPrefix.length)] + teamSuffix[r.nextInt(teamSuffix.length)];
    Visibility visibility = Visibility.values()[r.nextInt(Visibility.values().length)];

    TeamRequest request =
        new TeamRequest(
            name,
            MediaDto.builder().markdown("# " + name + "\n\nWelcome to our cycling team!").build(),
            visibility,
            true,
            true,
            null);

    var dto = teamService.createTeam(request);
    Team team = teamService.getTeam(dto.slug());
    List<User> users = createUserTeams(team, admin, userIndex);
    List<User> organizers = List.of(admin, users.get(1));

    Map<Visibility, List<String>> routes = createRoutes(team, organizers);
    List<String> places = createPlaces(team, organizers);
    List<String> rides = createRides(team, organizers, routes, places);
    List<String> posts = createPosts(team, organizers);
    List<String> trips = createTrips(team, organizers, routes, places);
    createRideTemplates(team, organizers);
    createAds(team, users);
    createComments(team, users, posts, rides, routes, trips);
    createTeamPages(team, admin);
  }

  private Visibility randomVisibility(int i) {
    return Visibility.values()[i % 2];
  }

  private Visibility randomVisibility(Team team, int i) {
    return team.getVisibility() == Visibility.TEAM ? Visibility.TEAM : randomVisibility(i);
  }

  private List<User> createUserTeams(Team team, User owner, int userIndex) {
    triblyContext.setUserForTest(owner);

    List<User> thisTeamUsers = new ArrayList<>();
    thisTeamUsers.add(team.getCreatedBy());

    for (int i = 1; i <= 4; i++) {
      User user = users.get((userIndex + i) % users.size());
      TeamRole role = (i == 1) ? TeamRole.ORGANIZER : TeamRole.MEMBER;
      teamMembershipService.addMember(team.getSlug(), user.getId(), role);
      thisTeamUsers.add(user);
    }
    return thisTeamUsers;
  }

  private Map<Visibility, List<String>> createRoutes(Team team, List<User> organizers) {
    Map<Visibility, List<String>> routes = new EnumMap<>(Visibility.class);
    routes.put(Visibility.PUBLIC, new ArrayList<>());
    routes.put(Visibility.TEAM, new ArrayList<>());
    for (int routeIndex = 0; routeIndex < ROUTES_PER_TEAM; routeIndex++) {
      User creator = organizers.get(r.nextInt(organizers.size()));
      triblyContext.setUserForTest(creator);
      String name = getRouteName();
      Visibility visibility = randomVisibility(team, routeIndex);

      // Generate circular route with radius 10-30 km
      double radiusKm = 10 + (routeIndex * 4); // 10, 14, 18, 22, 26, 30 km
      List<GeoPoint> points = generateCircularRoute(radiusKm, 20);

      MediaDto mediaDto =
          MediaDto.builder()
              .markdown("A beautiful " + name.toLowerCase() + " through the countryside.")
              .build();
      RouteRequest request = new RouteRequest(name, mediaDto, SurfaceType.ROAD, visibility, points);

      RouteDto routeDto = routeService.createRoute(team.getSlug(), request, null);
      routes.get(visibility).add(routeDto.slug());
    }
    return routes;
  }

  String[] routePrefix = {"Scenic ", "Hill ", "River ", "Forest ", "Valleyr ", "Coastal "};

  String[] routeSuffix = {
    "Loop", "Challenge", "Path", "Trail", "Ride",
  };

  private String getRouteName() {
    return routePrefix[r.nextInt(routePrefix.length)] + routeSuffix[r.nextInt(routeSuffix.length)];
  }

  private List<GeoPoint> generateCircularRoute(double radiusKm, int numPoints) {
    List<GeoPoint> points = new ArrayList<>();
    // Convert km to degrees (approximate: 1 degree latitude ≈ 111 km)
    double radiusLat = radiusKm / 111.0;
    // Longitude degrees vary with latitude (at Paris ~48.85°, 1 degree ≈ 73 km)
    double radiusLon = radiusKm / 73.0;

    for (int i = 0; i <= numPoints; i++) {
      double angle = 2 * Math.PI * i / numPoints;
      double lat = BASE_LAT + radiusLat * Math.sin(angle);
      double lon = BASE_LON + radiusLon * Math.cos(angle);
      points.add(new GeoPoint(lon, lat));
    }
    return points;
  }

  private List<String> createPlaces(Team team, List<User> organizers) {
    List<String> places = new ArrayList<>();

    String[] placeNames = {"Team HQ", "Coffee Shop", "Park Entrance", "Train Station"};
    for (int i = 0; i < placeNames.length; i++) {
      User creator = organizers.get(r.nextInt(organizers.size()));
      triblyContext.setUserForTest(creator);

      double lat = BASE_LAT + i * 0.001;
      double lon = BASE_LON + i * 0.001;
      Point<G2D> coordinates = DSL.point(WGS84, DSL.g(lon, lat));

      PlaceRequest request =
          new PlaceRequest(placeNames[i], null, null, i < 2, i >= 2, coordinates);

      PlaceDetailDto place = placeService.createPlace(team.getSlug(), request);
      places.add(place.id());
    }
    return places;
  }

  private List<String> createRides(
      Team team, List<User> organizers, Map<Visibility, List<String>> routes, List<String> places) {
    List<String> rides = new ArrayList<>();
    Instant now = Instant.now();

    String teamSlug = team.getSlug();

    for (int rideIndex = 0; rideIndex < RIDES_PER_TEAM; rideIndex++) {
      User creator = organizers.get(r.nextInt(organizers.size()));
      triblyContext.setUserForTest(creator);

      // Spread rides from -3 months to +2 months
      int daysOffset = -90 + (rideIndex * 15);
      Instant rideDate = now.plus(daysOffset, ChronoUnit.DAYS);

      String title = getRideTitle(rideIndex);
      Status status = rideIndex < 8 ? Status.PUBLISHED : Status.DRAFT;
      Visibility visibility = randomVisibility(team, rideIndex);

      List<GroupRequest> groups =
          List.of(
              GroupRequest.builder()
                  .name("Fast Group")
                  .averageSpeed(30.0f)
                  .maxParticipants(15)
                  .routeSlug(r.nextBoolean() ? getRandomRoute(routes, visibility) : null)
                  .build(),
              GroupRequest.builder()
                  .name("Leisure Group")
                  .averageSpeed(25.0f)
                  .maxParticipants(20)
                  .routeSlug(r.nextBoolean() ? getRandomRoute(routes, visibility) : null)
                  .build());

      RideRequest request =
          new RideRequest(
              title,
              MediaDto.builder()
                  .markdown("Join us for " + title + "!\n\nMeeting point: Team headquarters.")
                  .build(),
              rideDate,
              status,
              visibility,
              getRandomRoute(routes, visibility),
              r.nextBoolean() ? getRandomString(places) : null,
              r.nextBoolean() ? getRandomString(places) : null,
              null,
              groups);

      var dto = rideService.createRide(teamSlug, request);
      if (status == Status.PUBLISHED) {
        rides.add(dto.getSlug());
      }
    }
    return rides;
  }

  private @Nullable String getRandomRoute(
      Map<Visibility, List<String>> routes, Visibility visibility) {
    if (visibility == Visibility.PUBLIC || r.nextBoolean()) {
      return getRandomString(routes.get(Visibility.PUBLIC));
    } else {
      return getRandomString(routes.get(Visibility.TEAM));
    }
  }

  private String getRandomString(List<String> strings) {
    if (strings.isEmpty()) {
      return null;
    }
    return strings.get(r.nextInt(strings.size()));
  }

  private String getRideTitle(int index) {
    String[] titles = {
      "Morning Ride",
      "Sunday Long Ride",
      "Hill Training",
      "Recovery Spin",
      "Sprint Practice",
      "Endurance Ride",
      "Coffee Ride",
      "Weekend Explorer",
      "Tempo Training",
      "Social Ride"
    };
    return titles[index % titles.length];
  }

  private List<String> createPosts(Team team, List<User> organizers) {
    List<String> posts = new ArrayList<>();
    Instant now = Instant.now();

    String teamSlug = team.getSlug();

    for (int postIndex = 0; postIndex < POSTS_PER_TEAM; postIndex++) {
      User creator = organizers.get(r.nextInt(organizers.size()));
      triblyContext.setUserForTest(creator);

      int daysOffset = -180 + (postIndex * 18);
      Instant postDate = now.plus(daysOffset, ChronoUnit.DAYS);

      String title = getPostTitle(postIndex);
      Status status = r.nextBoolean() ? Status.PUBLISHED : Status.DRAFT;
      Visibility visibility = randomVisibility(team, postIndex);

      PostRequest request =
          new PostRequest(
              title,
              MediaDto.builder()
                  .markdown("# " + title + "\n\nThis is a post about " + title.toLowerCase() + ".")
                  .build(),
              postDate,
              status,
              visibility,
              null);

      var dto = postService.createPost(teamSlug, request);
      if (status == Status.PUBLISHED) {
        posts.add(dto.getSlug());
      }
    }
    return posts;
  }

  private String getPostTitle(int index) {
    String[] titles = {
      "Season Kickoff",
      "Training Tips",
      "Race Report",
      "Equipment Review",
      "Member Spotlight",
      "Route of the Month",
      "Safety Reminder",
      "Upcoming Events",
      "Photo Gallery",
      "Club News"
    };
    return titles[index % titles.length];
  }

  private List<String> createTrips(
      Team team, List<User> organizers, Map<Visibility, List<String>> routes, List<String> places) {
    List<String> trips = new ArrayList<>();
    Instant now = Instant.now();

    String teamSlug = team.getSlug();

    for (int tripIndex = 0; tripIndex < TRIPS_PER_TEAM; tripIndex++) {
      User creator = organizers.get(r.nextInt(organizers.size()));
      triblyContext.setUserForTest(creator);
      int daysOffset = 30 + (tripIndex * 30);
      Instant tripDate = now.plus(daysOffset, ChronoUnit.DAYS);

      String name = getTripName(tripIndex);
      Visibility visibility = randomVisibility(team, tripIndex);

      List<StageRequest> stages = new ArrayList<>();
      for (int s = 0; s < 3; s++) {
        String stageName = "Stage " + (s + 1) + ": " + getStageDestination(s);
        stages.add(
            StageRequest.builder()
                .name(stageName)
                .dateTime(tripDate.plus(s, ChronoUnit.DAYS))
                .media(
                    MediaDto.builder()
                        .markdown("Stage " + (s + 1) + " covers approximately 80km.")
                        .build())
                .routeSlug(getRandomRoute(routes, visibility))
                .startPlaceId(getRandomString(places))
                .endPlaceId(getRandomString(places))
                .build());
      }

      Status status = Status.PUBLISHED;
      TripRequest request =
          new TripRequest(
              name,
              MediaDto.builder()
                  .markdown("Join us for the " + name + "! A multi-day cycling adventure.")
                  .build(),
              tripDate,
              status,
              visibility,
              getRandomRoute(routes, visibility),
              null,
              stages);

      TripDto trip = tripService.createTrip(teamSlug, request);
      trips.add(trip.getSlug());
    }
    return trips;
  }

  private String getTripName(int index) {
    String[] names = {"Alpine Adventure", "Coastal Tour", "Mountain Challenge"};
    return names[index % names.length];
  }

  private String getStageDestination(int index) {
    String[] destinations = {"Mountain Pass", "Lake View", "Valley Finish"};
    return destinations[index % destinations.length];
  }

  private void createRideTemplates(Team team, List<User> organizers) {

    String teamSlug = team.getSlug();

    for (int templateIndex = 0; templateIndex < 2; templateIndex++) {
      User creator = organizers.get(r.nextInt(organizers.size()));
      triblyContext.setUserForTest(creator);

      String name = getTemplateName(templateIndex);
      Visibility visibility = randomVisibility(team, templateIndex);

      List<RideTemplateGroupRequest> groups =
          List.of(
              RideTemplateGroupRequest.builder()
                  .name("Performance Group")
                  .averageSpeed(32.0f)
                  .maxParticipants(12)
                  .build(),
              RideTemplateGroupRequest.builder()
                  .name("Casual Group")
                  .averageSpeed(24.0f)
                  .maxParticipants(25)
                  .build());

      RideTemplateRequest request =
          new RideTemplateRequest(
              name, "Template for " + name, visibility, Status.PUBLISHED, groups);

      rideTemplateService.createTemplate(teamSlug, request);
    }
  }

  private String getTemplateName(int index) {
    String[] names = {"Weekly Training Ride", "Monthly Endurance Ride"};
    return names[index % names.length];
  }

  private void createAds(Team team, List<User> users) {

    String teamSlug = team.getSlug();

    AdType[] adTypes = {AdType.SALE, AdType.WANTED, AdType.RENTAL};
    String[] adNames = {"Bike", "Wheels"};

    for (int i = 0; i < ADS_PER_TEAM; i++) {
      User creator = users.get(r.nextInt(users.size()));
      triblyContext.setUserForTest(creator);

      String name = adNames[i % adNames.length] + " " + (i + 1);

      AdType adType = adTypes[i % adTypes.length];
      RentalPeriod rentalPeriod =
          adType == AdType.RENTAL
              ? RentalPeriod.values()[r.nextInt(RentalPeriod.values().length)]
              : null;
      AdRequest request =
          new AdRequest(
              name,
              MediaDto.builder()
                  .markdown(
                      "Contact me for more details about this "
                          + adNames[i % adNames.length].toLowerCase()
                          + ".")
                  .build(),
              Status.PUBLISHED,
              adType,
              BigDecimal.valueOf(r.nextInt(100, 1000)),
              rentalPeriod,
              teamSuffix[r.nextInt(teamSuffix.length)],
              null);

      adService.createAd(teamSlug, request);
    }
  }

  private void createComments(
      Team team,
      List<User> users,
      List<String> posts,
      List<String> rides,
      Map<Visibility, List<String>> routes,
      List<String> trips) {
    createComments(team, users, EntityType.POST, posts);
    createComments(team, users, EntityType.RIDE, rides);
    createComments(team, users, EntityType.ROUTE, routes.get(Visibility.PUBLIC));
    createComments(team, users, EntityType.ROUTE, routes.get(Visibility.TEAM));
    createComments(team, users, EntityType.TRIP, trips);
  }

  private void createComments(
      Team team, List<User> users, EntityType entityType, List<String> slugs) {
    if (slugs.isEmpty()) {
      return;
    }
    for (int i = 0; i < COMMENTS_PER_TEAM_PER_TYPE; i++) {
      User creator = users.get(r.nextInt(users.size()));
      triblyContext.setUserForTest(creator);

      CommentRequest request = new CommentRequest("That's something !", null);
      String slug = getRandomString(slugs);
      CommentDto comment = commentService.createComment(team.getSlug(), slug, entityType, request);
      String parentId = comment.id();

      if (r.nextBoolean()) {
        creator = users.get(r.nextInt(users.size()));
        triblyContext.setUserForTest(creator);
        request = new CommentRequest("Yes it is !", parentId);
        commentService.createComment(team.getSlug(), slug, entityType, request);
      }
    }
  }

  private void createTeamPages(Team team, User admin) {
    triblyContext.setUserForTest(admin);

    TeamPageRequest request =
        new TeamPageRequest(
            "Club Rules",
            MediaDto.builder()
                .markdown(
                    "# Club Rules for "
                        + team.getName()
                        + "\n\n1. Be respectful\n2. Ride safe\n3. Have fun!")
                .build(),
            Visibility.TEAM);

    teamPageService.createPage(team.getSlug(), request);
  }
}
