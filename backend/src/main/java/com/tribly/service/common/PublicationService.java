package com.tribly.service.common;

import com.tribly.domain.common.Publication;
import com.tribly.domain.common.repository.AllPublicationRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationListResponse;
import com.tribly.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PublicationService
    extends TeamEntityService<Publication, BasicQuery, TeamEntityQueryBasic> {

  @Inject AllPublicationRepository allPublicationRepository;

  @Override
  protected AllPublicationRepository getRepository() {
    return allPublicationRepository;
  }

  @Override
  protected TeamEntityQueryBasic getQuery(
      BasicQuery query, Set<Long> memberTeamIds, Set<Long> organizerTeamIds) {
    return query.getTeamEntityQueryBasic(memberTeamIds, organizerTeamIds);
  }

  public PublicationListResponse list(
      @Nullable Set<String> teamSlugs,
      @Nullable Long userId,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable Status status,
      int page,
      int size) {
    TriblyPage<Publication> publications =
        list(new BasicQuery(null, teamSlugs, userId, status, null, from, to, page, size));
    List<PublicationDto> dtos = publications.items().stream().map(PublicationDto::from).toList();
    return new PublicationListResponse(dtos, publications.total(), page, size);
  }
}
