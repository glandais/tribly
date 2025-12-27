package com.tribly.domain.common.repository;

import com.tribly.domain.common.Publication;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AllPublicationRepository
    implements TeamEntityRepository<Publication, TeamEntityQueryBasic> {
  @Override
  public String getTypeName() {
    return "Publication";
  }
}
