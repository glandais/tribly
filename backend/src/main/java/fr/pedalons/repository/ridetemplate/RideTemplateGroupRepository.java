package fr.pedalons.repository.ridetemplate;

import fr.pedalons.domain.ridetemplate.RideTemplateGroup;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RideTemplateGroupRepository implements BaseRepository<RideTemplateGroup> {

  public Optional<RideTemplateGroup> findByIdAndTemplate(Long groupId, Long templateId) {
    return find("id = ?1 AND template.id = ?2", groupId, templateId).firstResultOptional();
  }

  /** Ride template groups a user created, for the GDPR data export. */
  public List<RideTemplateGroup> findByCreator(Long domainId, Long userId) {
    return list(
        "createdBy.id = ?2 and template.team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
