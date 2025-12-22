package com.tribly.service.ride;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.repository.RideRepository;
import com.tribly.enums.RideStatus;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import org.jboss.logging.Logger;

/**
 * Scheduled job that auto-publishes rides when their publishAt time has passed.
 */
@ApplicationScoped
public class RidePublishScheduler {

  private static final Logger LOG = Logger.getLogger(RidePublishScheduler.class);

  @Inject RideRepository rideRepository;

  /**
   * Runs every minute to check for rides that should be auto-published.
   * A ride is auto-published if:
   * - status is DRAFT
   * - publishAt is not null
   * - publishAt <= current time
   */
  @Scheduled(every = "1m")
  @Transactional
  void autoPublishRides() {
    List<Ride> rides = rideRepository.findRidesToAutoPublish(RideStatus.DRAFT, Instant.now());

    for (Ride ride : rides) {
      ride.setStatus(RideStatus.PUBLISHED);
      ride.setPublishAt(null); // Clear after publishing
      rideRepository.persist(ride);
      LOG.infov(
          "Auto-published ride {0} '{1}' for team {2}",
          ride.getId(), ride.getName(), ride.getTeam().getId());
    }

    if (!rides.isEmpty()) {
      LOG.infov("Auto-published {0} ride(s)", rides.size());
    }
  }
}
