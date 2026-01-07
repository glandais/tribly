package com.tribly.service.common;

import com.tribly.domain.common.Publication;
import com.tribly.dto.publications.response.PublicationDto;
import com.tribly.dto.publications.response.PublicationListResponse;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.repository.common.AllPublicationRepository;
import com.tribly.repository.common.PublicationQuery;
import com.tribly.repository.common.TriblyPage;
import com.tribly.service.asset.AssetService;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import com.tribly.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PublicationService {

  @Inject AllPublicationRepository allPublicationRepository;

  @Inject AssetService assetService;

  @Inject TriblyQueryContext triblyQueryContext;

  @Inject TeamService teamService;

  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public PublicationListResponse listAll(
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    return list(type, null, search, from, to, page, size);
  }

  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST)
  public PublicationListResponse listTeam(
      String teamSlug,
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    Long teamId = teamService.getTeam(teamSlug).getId();
    return list(type, Set.of(teamId), search, from, to, page, size);
  }

  protected PublicationListResponse list(
      @Nullable PublicationType type,
      @Nullable Set<Long> teamIds,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Publication> publications =
        allPublicationRepository.find(
            PublicationQuery.builder()
                .userId(triblyQueryContext.getUserIdNullable())
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
