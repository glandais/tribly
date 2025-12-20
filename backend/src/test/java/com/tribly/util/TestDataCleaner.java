package com.tribly.util;

import com.tribly.domain.ride.repository.RideGroupRepository;
import com.tribly.domain.ride.repository.RideParticipationRepository;
import com.tribly.domain.ride.repository.RideRepository;
import com.tribly.domain.route.repository.GpxTrackRepository;
import com.tribly.domain.route.repository.RouteClimbRepository;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.domain.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TestDataCleaner {

  @Inject RideParticipationRepository participationRepository;
  @Inject RideGroupRepository rideGroupRepository;
  @Inject RideRepository rideRepository;
  @Inject UserTeamRepository userTeamRepository;
  @Inject TeamRepository teamRepository;
  @Inject UserRepository userRepository;
  @Inject RouteRepository routeRepository;
  @Inject RouteClimbRepository routeClimbRepository;
  @Inject GpxTrackRepository gpxTrackRepository;

  @Transactional
  public void cleanAll() {
    participationRepository.deleteAll();
    rideGroupRepository.deleteAll();
    rideRepository.deleteAll();
    routeClimbRepository.deleteAll();
    gpxTrackRepository.deleteAll();
    routeRepository.deleteAll();
    userTeamRepository.deleteAll();
    teamRepository.deleteAll();
    userRepository.deleteAll();
  }
}
