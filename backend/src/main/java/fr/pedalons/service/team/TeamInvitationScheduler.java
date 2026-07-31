package fr.pedalons.service.team;

import fr.pedalons.repository.team.TeamInvitationRepository;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Keeps the invitation table honest.
 *
 * <p>Two jobs, and the first is not cosmetic: the "one live invitation per (team, address)" index
 * keys on the status, so a lapsed invitation goes on occupying the slot until something marks it
 * {@code EXPIRED}. Without this sweep, an address invited once and never answered could never be
 * invited again.
 *
 * <p>Deliberately separate from {@code AuthCleanupScheduler}, which <em>deletes</em> tokens once
 * they are used or expired. Applying that rule here would erase every accepted and every revoked
 * invitation within a day, and who let whom into a team should outlive the invitation itself.
 */
@ApplicationScoped
public class TeamInvitationScheduler {

  @Inject TeamInvitationRepository invitationRepository;

  @ConfigProperty(name = "pedalons.teams.invitations.retention-days", defaultValue = "365")
  int retentionDays;

  @Scheduled(cron = "0 30 3 * * ?")
  @Transactional
  void sweepInvitations() {
    long expired = invitationRepository.expireOverdue();
    long purged = invitationRepository.deleteOldTerminal(retentionDays);
    if (expired > 0 || purged > 0) {
      Log.infof("Invitation sweep: %d marked expired, %d old rows purged", expired, purged);
    }
  }
}
