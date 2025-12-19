package com.tribly.util;

import com.tribly.domain.common.Visibility;
import com.tribly.domain.ride.*;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.TeamRole;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@ApplicationScoped
public class TestDataService {

  @Inject UserRepository userRepository;
  @Inject TeamRepository teamRepository;
  @Inject UserTeamRepository userTeamRepository;
  @Inject RideRepository rideRepository;
  @Inject RideGroupRepository rideGroupRepository;
  @Inject RideParticipationRepository participationRepository;
  @Inject RouteRepository routeRepository;

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
    user.softDelete();
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
  public Ride createRide(Team team, User createdBy, String title, String slug, LocalDate date) {
    Ride ride = new Ride(team, createdBy, title, slug, date);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public Ride createRideWithStatus(
      Team team, User createdBy, String title, String slug, LocalDate date, RideStatus status) {
    Ride ride = new Ride(team, createdBy, title, slug, date);
    ride.setStatus(status);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public Ride createRideWithVisibility(
      Team team, User createdBy, String title, String slug, LocalDate date, Visibility visibility) {
    Ride ride = new Ride(team, createdBy, title, slug, date);
    ride.setVisibility(visibility);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public Ride createRideWithPublishAt(
      Team team,
      User createdBy,
      String title,
      String slug,
      LocalDate date,
      RideStatus status,
      java.time.Instant publishAt) {
    Ride ride = new Ride(team, createdBy, title, slug, date);
    ride.setStatus(status);
    ride.setPublishAt(publishAt);
    rideRepository.persistAndFlush(ride);
    return ride;
  }

  @Transactional
  public RideGroup createRideGroup(Ride ride, String name) {
    RideGroup group = new RideGroup(ride, name);
    rideGroupRepository.persistAndFlush(group);
    return group;
  }

  @Transactional
  public RideGroup createRideGroupWithOrder(Ride ride, String name, int sortOrder) {
    RideGroup group = new RideGroup(ride, name);
    group.setSortOrder(sortOrder);
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
  public RideParticipation createParticipationWithStatus(
      RideGroup group, User user, ParticipationStatus status) {
    RideParticipation participation = new RideParticipation(group, user);
    participation.setStatus(status);
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
    Route route = new Route(team, createdBy, name);
    routeRepository.persistAndFlush(route);
    return route;
  }

  @Transactional
  public Route createRouteWithVisibility(
      Team team, User createdBy, String name, Visibility visibility) {
    Route route = new Route(team, createdBy, name);
    route.setVisibility(visibility);
    routeRepository.persistAndFlush(route);
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
}
