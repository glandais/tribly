package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.TeamWebhook;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TeamWebhookRepository implements PanacheRepository<TeamWebhook> {

  public Optional<TeamWebhook> findByTeam(Long teamId) {
    return find("team.id", teamId).firstResultOptional();
  }
}
