package com.tribly.service.common;

import com.tribly.domain.common.Publication;
import com.tribly.domain.common.repository.AllPublicationRepository;
import com.tribly.domain.common.repository.PublicationQuery;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationListResponse;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.enums.ActionType;
import com.tribly.infrastructure.exception.ForbiddenException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PublicationService
    extends TeamEntityService<Publication, AllPublicationRepository, PublicationDto> {

  @Inject AllPublicationRepository allPublicationRepository;

  @Override
  protected AllPublicationRepository getRepository() {
    return allPublicationRepository;
  }

  @Override
  protected PublicationDto toDto(Publication entity) {
    return PublicationDto.from(entity, assetService);
  }

  @Override
  protected boolean hasRights(
      ActionType action, Team team, @Nullable User user, @Nullable Publication entity) {
    return false;
  }

  @Override
  @Nullable
  protected Publication getBySlug(Team team, String entitySlug, @Nullable User user) {
    // not redirectable at this level
    throw new ForbiddenException();
  }

  public PublicationListResponse list(
      @Nullable PublicationType type,
      @Nullable Set<Long> teamIds,
      @Nullable User user,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Publication> publications =
        allPublicationRepository.find(
            PublicationQuery.builder()
                .userId(user == null ? null : user.getId())
                .type(type)
                .teamIds(teamIds)
                .search(search)
                .from(from)
                .to(to)
                .page(page)
                .size(size)
                .build());
    List<PublicationDto> dtos =
        publications.items().stream()
            .map(publication -> PublicationDto.from(publication, assetService))
            .toList();
    return new PublicationListResponse(dtos, publications.total(), page, size);
  }
}
