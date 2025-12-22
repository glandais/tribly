package com.tribly.util;

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
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.enums.*;
import com.tribly.service.common.SlugService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

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
  public User createUserWithLocale(
      String email, String displayName, String locale, String timezone) {
    User user = new User(email, displayName);
    user.setLocale(locale);
    user.setTimezone(timezone);
    userRepository.persistAndFlush(user);
    return user;
  }

  @Transactional
  public void deleteUser(User user) {
    user.setDeleted(true);
    userRepository.getEntityManager().merge(user);
  }

  @Transactional
  public Team createTeam(String name, String slug) {
    Team team = new Team(name, slug);
    teamRepository.persistAndFlush(team);
    return team;
  }

  @Transactional
  public Team createTeamWithVisibility(String name, String slug, Visibility visibility) {
    Team team = new Team(name, slug);
    team.setVisibility(visibility);
    teamRepository.persistAndFlush(team);
    return team;
  }

  @Transactional
  public UserTeam addUserToTeam(User user, Team team, TeamRole role) {
    UserTeam userTeam = new UserTeam(user, team, role);
    userTeamRepository.persistAndFlush(userTeam);
    return userTeam;
  }

  @Transactional
  public Ride createRide(
      Team team, User createdBy, String title, String slug, LocalDateTime dateTime) {
    Ride ride = new Ride(team, createdBy, title, slug, dateTime);
    ride.setStatus(Status.PUBLISHED);
    ride.setVisibility(Visibility.PUBLIC);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public Ride createRideWithStatus(
      Team team, User createdBy, String title, String slug, LocalDateTime dateTime, Status status) {
    Ride ride = new Ride(team, createdBy, title, slug, dateTime);
    ride.setStatus(status);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public Ride createRideWithVisibility(
      Team team,
      User createdBy,
      String title,
      String slug,
      LocalDateTime dateTime,
      Visibility visibility) {
    Ride ride = new Ride(team, createdBy, title, slug, dateTime);
    ride.setVisibility(visibility);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public Ride createRideWithVisibilityAndStatus(
      Team team,
      User createdBy,
      String title,
      String slug,
      LocalDateTime date,
      Visibility visibility,
      Status status) {
    Ride ride = new Ride(team, createdBy, title, slug, date);
    ride.setVisibility(visibility);
    ride.setStatus(status);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public Ride createRideWithPublishAt(
      Team team,
      User createdBy,
      String title,
      String slug,
      LocalDateTime dateTime,
      Status status,
      java.time.Instant publishAt) {
    Ride ride = new Ride(team, createdBy, title, slug, dateTime);
    ride.setStatus(status);
    ride.setPublishAt(publishAt);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public RideGroup createRideGroup(Ride ride, String name) {
    RideGroup group = new RideGroup();
    group.setRide(ride);
    group.setName(name);
    rideGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public RideGroup createRideGroupWithOrder(Ride ride, String name, int sortOrder) {
    RideGroup group = new RideGroup();
    group.setRide(ride);
    group.setName(name);
    group.setSortOrder(sortOrder);
    rideGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public RideGroup createRideGroupWithMaxParticipants(Ride ride, String name, int maxParticipants) {
    RideGroup group = new RideGroup();
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
    Route route = new Route(team, createdBy, name, SlugService.slugify(name));
    routeRepository.persistAndFlush(route);
    return route;
  }

  @Transactional
  public Route createRouteWithVisibility(
      Team team, User createdBy, String name, Visibility visibility) {
    Route route = new Route(team, createdBy, name, SlugService.slugify(name));
    route.setVisibility(visibility);
    routeRepository.persistAndFlush(route);
    List<GpxTrack.TrackPoint> trackPoints = List.of(new GpxTrack.TrackPoint(45.0, 6.0, 500.0, 0.0));
    String geometry = "LINESTRING(6 45,6.1 45.1)";
    createGpxTrack(route, geometry, trackPoints);
    return route;
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
  public void updateTeam(Team team) {
    teamRepository.getEntityManager().merge(team);
  }

  @Transactional
  public RouteClimb createRouteClimb(
      Route route,
      Integer startDistance,
      Integer endDistance,
      Integer elevationGain,
      BigDecimal averageGradient,
      BigDecimal maxGradient) {
    RouteClimb climb = new RouteClimb();
    climb.setRoute(route);
    climb.setStartDistance(startDistance);
    climb.setEndDistance(endDistance);
    climb.setElevationGain(elevationGain);
    climb.setAverageGradient(averageGradient);
    climb.setMaxGradient(maxGradient);
    routeClimbRepository.persistAndFlush(climb);
    return climb;
  }

  @Transactional
  public RouteClimb createRouteClimbWithCategory(
      Route route,
      String name,
      Integer startDistance,
      Integer endDistance,
      Integer elevationGain,
      BigDecimal averageGradient,
      BigDecimal maxGradient,
      ClimbCategory category) {
    RouteClimb climb = new RouteClimb();
    climb.setRoute(route);
    climb.setName(name);
    climb.setStartDistance(startDistance);
    climb.setEndDistance(endDistance);
    climb.setElevationGain(elevationGain);
    climb.setAverageGradient(averageGradient);
    climb.setMaxGradient(maxGradient);
    climb.setCategory(category);
    routeClimbRepository.persistAndFlush(climb);
    return climb;
  }

  @Transactional
  public void deleteRouteClimb(RouteClimb climb) {
    climb.setDeleted(true);
    routeClimbRepository.getEntityManager().merge(climb);
  }

  @Transactional
  public GpxTrack createGpxTrack(
      Route route, String geometry, List<GpxTrack.TrackPoint> trackPoints) {
    GpxTrack track = new GpxTrack();
    track.setRoute(route);
    track.setGeometry(geometry);
    track.setTrackPoints(trackPoints);
    track.setProcessedAt(Instant.now());
    gpxTrackRepository.persistAndFlush(track);
    return track;
  }

  @Transactional
  public GpxTrack createGpxTrackWithName(
      Route route,
      String name,
      String geometry,
      List<GpxTrack.TrackPoint> trackPoints,
      String originalFileName) {
    GpxTrack track = new GpxTrack();
    track.setRoute(route);
    track.setName(name);
    track.setGeometry(geometry);
    track.setTrackPoints(trackPoints);
    track.setOriginalFileName(originalFileName);
    track.setProcessedAt(Instant.now());
    gpxTrackRepository.persistAndFlush(track);
    return track;
  }

  @Transactional
  public void deleteGpxTrack(GpxTrack track) {
    track.setDeleted(true);
    gpxTrackRepository.getEntityManager().merge(track);
  }
}
