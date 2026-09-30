package fr.pedalons.service.sitemap;

import fr.pedalons.dto.sitemap.SitemapDto;
import fr.pedalons.dto.sitemap.SitemapEntryDto;
import fr.pedalons.dto.sitemap.SitemapEntryType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.team.TeamPageQuery;
import fr.pedalons.repository.team.TeamPageRepository;
import fr.pedalons.repository.team.TeamRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * The pages of a site that a search engine may index — what {@code /sitemap.xml} lists.
 *
 * <p>Public teams, and their public content as an anonymous visitor lists it: each type goes
 * through its repository's listing query ({@code findIndexable}), so the visibility, status,
 * moderation and module rules are the listing's own. Three things are deliberately left out: ads,
 * which only members read; routes, whose pages are a map and a GPX file rather than text to
 * index; and the full-screen map pages, which the client does not derive from these entries. Stages carry no visibility of their own: they are listed under a trip that is.
 */
@ApplicationScoped
public class SitemapService {

  /** The sitemap protocol's cap on the URLs of one file. */
  static final int MAX_ENTRIES = 50_000;

  /** Bound on the trip ids of one {@code IN} list. */
  private static final int STAGE_BATCH = 1_000;

  /** Team slug, slug and last modification: the projection of every slug-addressed type. */
  private static final String ROW = "te.team.slug, te.slug, te.updatedAt";

  @Inject PedalonsQueryContext pedalonsQueryContext;
  @Inject TeamRepository teamRepository;
  @Inject TeamPageRepository teamPageRepository;
  @Inject RideRepository rideRepository;
  @Inject PostRepository postRepository;
  @Inject TripRepository tripRepository;

  @Public
  public SitemapDto getSitemap() {
    Long domainId = pedalonsQueryContext.getDomainId();
    Long pinnedTeamId = pedalonsQueryContext.getPinnedTeamIdNullable();
    List<SitemapEntryDto> entries = new ArrayList<>();

    addTeams(entries, domainId, pinnedTeamId);

    TeamPageQuery pageQuery =
        TeamPageQuery.builder().domainId(domainId).pinnedTeamId(pinnedTeamId).build();
    for (Object[] row :
        teamPageRepository.findIndexable(
            pageQuery, "te.team.slug, te.slug, te.updatedAt, te.aboutPage", MAX_ENTRIES)) {
      boolean about = (Boolean) row[3];
      entries.add(
          new SitemapEntryDto(
              about ? SitemapEntryType.TEAM_ABOUT : SitemapEntryType.TEAM_PAGE,
              (String) row[0],
              null,
              about ? null : (String) row[1],
              (Instant) row[2]));
    }

    TeamEntityQueryBasic basic =
        TeamEntityQueryBasic.builder().domainId(domainId).pinnedTeamId(pinnedTeamId).build();
    addRows(entries, SitemapEntryType.RIDE, rideRepository.findIndexable(basic, ROW, MAX_ENTRIES));
    addRows(entries, SitemapEntryType.POST, postRepository.findIndexable(basic, ROW, MAX_ENTRIES));

    List<Object[]> trips = tripRepository.findIndexable(basic, ROW + ", te.id", MAX_ENTRIES);
    addRows(entries, SitemapEntryType.TRIP, trips);
    addStages(entries, trips.stream().map(row -> (Long) row[3]).toList());

    // Each query is bounded on its own; the file as a whole is cut here, teams and pages first.
    return new SitemapDto(
        entries.size() > MAX_ENTRIES ? List.copyOf(entries.subList(0, MAX_ENTRIES)) : entries);
  }

  private void addTeams(List<SitemapEntryDto> entries, Long domainId, @Nullable Long pinnedTeamId) {
    String hql =
        "select t.slug, t.updatedAt from Team t where t.domain.id = :domainId"
            + " and t.deleted = false and t.visibility = :visibility"
            + (pinnedTeamId != null ? " and t.id = :pinnedTeamId" : "")
            + " order by t.updatedAt desc";
    TypedQuery<Object[]> query =
        teamRepository
            .getEntityManager()
            .createQuery(hql, Object[].class)
            .setParameter("domainId", domainId)
            .setParameter("visibility", Visibility.PUBLIC);
    if (pinnedTeamId != null) {
      query.setParameter("pinnedTeamId", pinnedTeamId);
    }
    for (Object[] row : query.setMaxResults(MAX_ENTRIES).getResultList()) {
      entries.add(
          new SitemapEntryDto(
              SitemapEntryType.TEAM, (String) row[0], null, null, (Instant) row[1]));
    }
  }

  private void addStages(List<SitemapEntryDto> entries, List<Long> tripIds) {
    for (int from = 0; from < tripIds.size(); from += STAGE_BATCH) {
      List<Long> batch = tripIds.subList(from, Math.min(from + STAGE_BATCH, tripIds.size()));
      List<Object[]> rows =
          tripRepository
              .getEntityManager()
              .createQuery(
                  "select s.team.slug, s.trip.slug, s.slug, s.updatedAt from TripStage s"
                      + " where s.trip.id in (:tripIds) and s.deleted = false"
                      + " order by s.updatedAt desc",
                  Object[].class)
              .setParameter("tripIds", batch)
              .setMaxResults(MAX_ENTRIES)
              .getResultList();
      for (Object[] row : rows) {
        entries.add(
            new SitemapEntryDto(
                SitemapEntryType.TRIP_STAGE,
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (Instant) row[3]));
      }
    }
  }

  private static void addRows(
      List<SitemapEntryDto> entries, SitemapEntryType type, List<Object[]> rows) {
    for (Object[] row : rows) {
      entries.add(
          new SitemapEntryDto(type, (String) row[0], null, (String) row[1], (Instant) row[2]));
    }
  }
}
