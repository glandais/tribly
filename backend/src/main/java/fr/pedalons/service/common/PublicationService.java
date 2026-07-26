package fr.pedalons.service.common;

import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.dto.common.CountResponse;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.publications.response.PublicationDto;
import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.publications.response.PublicationListSummaries;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.publications.response.UserParticipations;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.Status;
import fr.pedalons.repository.common.AllPublicationRepository;
import fr.pedalons.repository.common.PublicationQuery;
import fr.pedalons.repository.ride.RideSummaryRepository;
import fr.pedalons.repository.trip.TripSummaryRepository;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.comment.CommentCountLookup;
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

  @Inject RideSummaryRepository rideSummaryRepository;

  @Inject TripSummaryRepository tripSummaryRepository;

  @Inject ParticipationLookup participationLookup;

  @Inject CommentCountLookup commentCountLookup;

  /** Without the "me" filters — kept so existing callers do not have to pass two nulls. */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public PublicationListResponse listAll(
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable MinRole minRole,
      int page,
      int size) {
    return listAll(type, search, from, to, minRole, null, false, page, size);
  }

  /** Without the "me" filters — kept so existing callers do not have to pass two nulls. */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST)
  public PublicationListResponse listTeam(
      String teamSlug,
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    return listTeam(teamSlug, type, search, from, to, null, false, page, size);
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
    return list(
        baseQuery(page, size)
            .type(type)
            .teamIds(teamIds)
            .search(search)
            .from(from)
            .to(to)
            .minRole(minRole)
            .includeDeleted(includeDeleted)
            .platformAdmin(platformAdmin)
            .build());
  }

  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public PublicationListResponse listAll(
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable MinRole minRole,
      @Nullable Status status,
      boolean participating,
      int page,
      int size) {
    return listAll(
        type, search, from, to, minRole, status, participating, ListViewMode.FULL, page, size);
  }

  /**
   * @param view {@link ListViewMode#COMPACT} strips the markdown body and the asset inventory from every
   *     row; the rows are otherwise identical, and the query is exactly the same
   */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public PublicationListResponse listAll(
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable MinRole minRole,
      @Nullable Status status,
      boolean participating,
      @Nullable ListViewMode view,
      int page,
      int size) {
    return list(
        baseQuery(page, size)
            .type(type)
            .search(search)
            .from(from)
            .to(to)
            .minRole(minRole)
            .status(status)
            .participating(participating)
            .includeDeleted(false)
            .build(),
        view);
  }

  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST)
  public PublicationListResponse listTeam(
      String teamSlug,
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable Status status,
      boolean participating,
      int page,
      int size) {
    return listTeam(
        teamSlug, type, search, from, to, status, participating, ListViewMode.FULL, page, size);
  }

  /**
   * @param view {@link ListViewMode#COMPACT} strips the markdown body and the asset inventory from every
   *     row; the rows are otherwise identical, and the query is exactly the same
   */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST)
  public PublicationListResponse listTeam(
      String teamSlug,
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable Status status,
      boolean participating,
      @Nullable ListViewMode view,
      int page,
      int size) {
    Team team = teamService.getTeam(teamSlug);
    boolean includeDeleted = includeDeletedService.isTeamEntityIncludeDeleted(team);
    return list(
        baseQuery(page, size)
            .type(type)
            .teamIds(Set.of(team.getId()))
            .search(search)
            .from(from)
            .to(to)
            .status(status)
            .participating(participating)
            .includeDeleted(includeDeleted)
            .build(),
        view);
  }

  /** How many publications {@link #listAll} would list, without listing them. */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public CountResponse countAll(
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable MinRole minRole,
      @Nullable Status status,
      boolean participating) {
    return count(
        baseQuery(0, 0)
            .type(type)
            .search(search)
            .from(from)
            .to(to)
            .minRole(minRole)
            .status(status)
            .participating(participating)
            .includeDeleted(false)
            .build());
  }

  /** How many publications {@link #listTeam} would list, without listing them. */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST)
  public CountResponse countTeam(
      String teamSlug,
      @Nullable PublicationType type,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable Status status,
      boolean participating) {
    Team team = teamService.getTeam(teamSlug);
    boolean includeDeleted = includeDeletedService.isTeamEntityIncludeDeleted(team);
    return count(
        baseQuery(0, 0)
            .type(type)
            .teamIds(Set.of(team.getId()))
            .search(search)
            .from(from)
            .to(to)
            .status(status)
            .participating(participating)
            .includeDeleted(includeDeleted)
            .build());
  }

  /**
   * The rides and trips the current user is registered to, soonest first.
   *
   * <p>Goes through the ordinary publication query rather than straight at {@code
   * RideParticipation}: the registration is only half the answer, the other half is whether the user
   * may still see the publication at all (domain, team visibility, status, disabled modules). A
   * direct query on the participation table would return outings from another domain or from a team
   * the user has since left.
   */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public PublicationListResponse listMyParticipations(
      @Nullable Instant from, @Nullable Instant to, @Nullable Status status, int page, int size) {
    return listMyParticipations(from, to, status, ListViewMode.FULL, page, size);
  }

  /**
   * @param view {@link ListViewMode#COMPACT} strips the markdown body and the asset inventory from every
   *     row
   */
  @CheckAccess(entityType = EntityType.PUBLICATION, action = ActionType.LIST_ALL_TEAMS)
  public PublicationListResponse listMyParticipations(
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable Status status,
      @Nullable ListViewMode view,
      int page,
      int size) {
    return list(
        baseQuery(page, size)
            .from(from)
            .to(to)
            .status(status)
            .participating(true)
            .ascending(true)
            .includeDeleted(false)
            .build(),
        view);
  }

  private PublicationQuery.PublicationQueryBuilder baseQuery(int page, int size) {
    return PublicationQuery.builder()
        .domainId(pedalonsQueryContext.getDomainId())
        .pinnedTeamId(pedalonsQueryContext.getPinnedTeamIdNullable())
        .userId(pedalonsQueryContext.getUserIdNullable())
        .page(page)
        .size(size)
        .platformAdmin(isPlatformAdmin());
  }

  protected PublicationListResponse list(PublicationQuery query) {
    return list(query, ListViewMode.FULL);
  }

  protected PublicationListResponse list(PublicationQuery query, @Nullable ListViewMode view) {
    if (query.participating() && query.userId() == null) {
      // An anonymous visitor participates in nothing; no need to ask the database.
      return new PublicationListResponse(List.of(), 0, query.page(), query.size());
    }
    PedalonsPage<Publication> publications = allPublicationRepository.find(query);
    PublicationListSummaries summaries = loadSummaries(publications.items());
    UserParticipations participations = loadParticipations(publications.items());
    // Two more queries for the whole page, none per row — and nothing at all for a visitor who is
    // not a member of any of the teams on it.
    CommentCounts commentCounts = commentCountLookup.forEntities(publications.items());
    List<PublicationDto> dtos =
        publications.items().stream()
            .map(
                publication ->
                    PublicationDto.from(
                        publication, assetService, summaries, participations, commentCounts, view))
            .toList();
    return new PublicationListResponse(dtos, publications.total(), query.page(), query.size());
  }

  protected CountResponse count(PublicationQuery query) {
    if (query.participating() && query.userId() == null) {
      // Same short circuit as list(): an anonymous visitor participates in nothing.
      return new CountResponse(0);
    }
    return new CountResponse(allPublicationRepository.countMatching(query));
  }

  /**
   * Resolves the "me" fields ({@code registered}, {@code registeredGroupId}) for the whole page at
   * once — two queries, whatever the page size. Anonymous callers cost nothing.
   */
  private UserParticipations loadParticipations(List<Publication> items) {
    return participationLookup.forPublications(
        idsOfType(items, Ride.class), idsOfType(items, Trip.class));
  }

  /**
   * Loads the association aggregates for a whole page in bulk.
   *
   * <p>Without this, every ride row would walk {@code groups -> participations -> user} and every
   * trip row would load its {@code stages} and {@code participations} in full, just to produce a
   * handful of counts. Batch fetching keeps the query count flat while doing it, which is exactly
   * what makes the cost easy to miss: the queries stay constant, the rows hydrated do not.
   *
   * <p>Only the ids actually present on the page are queried, and a type absent from the page costs
   * nothing — the repositories short-circuit on an empty id list.
   */
  private PublicationListSummaries loadSummaries(List<Publication> items) {
    return new PublicationListSummaries(
        rideSummaryRepository.loadListSummaries(idsOfType(items, Ride.class)),
        tripSummaryRepository.loadListSummaries(idsOfType(items, Trip.class)));
  }

  private static List<Long> idsOfType(List<Publication> items, Class<? extends Publication> type) {
    return items.stream().filter(type::isInstance).map(Publication::getId).toList();
  }

  protected boolean isPlatformAdmin() {
    return pedalonsQueryContext.isPlatformAdmin();
  }
}
