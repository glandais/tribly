package com.tribly.domain.ridetemplate.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.ridetemplate.RideTemplateGroup;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RideTemplateGroupRepository implements BaseRepository<RideTemplateGroup> {

  public Optional<RideTemplateGroup> findByIdAndTemplate(Long groupId, Long templateId) {
    return find("id = ?1 AND template.id = ?2 AND deleted = false", groupId, templateId)
        .firstResultOptional();
  }
}
