package com.tribly.util;

import com.tribly.domain.ride.RideGroupRepository;
import com.tribly.domain.ride.RideParticipationRepository;
import com.tribly.domain.ride.RideRepository;
import com.tribly.domain.route.RouteRepository;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.team.UserTeamRepository;
import com.tribly.domain.user.UserRepository;
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

  @Transactional
  public void cleanAll() {
    participationRepository.deleteAll();
    rideGroupRepository.deleteAll();
    rideRepository.deleteAll();
    routeRepository.deleteAll();
    userTeamRepository.deleteAll();
    teamRepository.deleteAll();
    userRepository.deleteAll();
  }
}
