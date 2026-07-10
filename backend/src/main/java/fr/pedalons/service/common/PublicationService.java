package fr.pedalons.service.common;

import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.publications.response.PublicationDto;
import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.repository.common.AllPublicationRepository;
import fr.pedalons.repository.common.PublicationQuery;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.TeamService;
import fr.pedalons.service.team.request.MinRole;
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

  @Inject PedalonsQueryContext pedalonsQueryContext;

  @Inject TeamService teamService;

  @Inject IncludeDeletedService includeDeletedService;

  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public PublicationListResponse listAll(
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable MinRole minRole,
      int page,
      int size) {
    return list(type, null, search, from, to, minRole, page, size, false, isPlatformAdmin());
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
    Team team = teamService.getTeam(teamSlug);
    Long teamId = team.getId();
    boolean includeDeleted = includeDeletedService.isTeamEntityIncludeDeleted(team);
    return list(
        type,
        Set.of(teamId),
        search,
        from,
        to,
        null,
        page,
        size,
        includeDeleted,
        isPlatformAdmin());
  }

  protected PublicationListResponse list(
      @Nullable PublicationType type,
      @Nullable Set<Long> teamIds,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable MinRole minRole,
      int page,
      int size,
      boolean includeDeleted,
      boolean platformAdmin) {
    PedalonsPage<Publication> publications =
        allPublicationRepository.find(
            PublicationQuery.builder()
                .domainId(pedalonsQueryContext.getDomainId())
                .userId(pedalonsQueryContext.getUserIdNullable())
                .type(type)
                .teamIds(teamIds)
                .search(search)
                .from(from)
                .to(to)
                .minRole(minRole)
                .page(page)
                .size(size)
                .includeDeleted(includeDeleted)
                .platformAdmin(platformAdmin)
                .build());
    List<PublicationDto> dtos =
        publications.items().stream()
            .map(publication -> PublicationDto.from(publication, assetService))
            .toList();
    return new PublicationListResponse(dtos, publications.total(), page, size);
  }

  protected boolean isPlatformAdmin() {
    return pedalonsQueryContext.isPlatformAdmin();
  }
}
