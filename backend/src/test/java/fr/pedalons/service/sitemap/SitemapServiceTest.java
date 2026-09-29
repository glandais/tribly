package fr.pedalons.service.sitemap;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.platform.DomainAlias;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.sitemap.SitemapEntryDto;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SitemapServiceTest extends AbstractBaseTest {

  @Inject SitemapService sitemapService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  /** What a public team with no content yields: its home and its "about" page. */
  private static final Set<String> PUBLIC_TEAM =
      Set.of("TEAM public-team", "TEAM_ABOUT public-team");

  private Domain domain;
  private User owner;
  private Team publicTeam;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    owner = dataService.createUser("owner@example.com", "Owner");
    publicTeam = dataService.createTeam(owner, "Public Team", "public-team", Visibility.PUBLIC);
  }

  /** Each entry as "TYPE team/trip/slug", so an assertion reads like the list it checks. */
  private Set<String> entries() {
    return sitemapService.getSitemap().entries().stream()
        .map(SitemapServiceTest::describe)
        .collect(Collectors.toSet());
  }

  private static String describe(SitemapEntryDto e) {
    return e.type()
        + " "
        + e.teamSlug()
        + (e.tripSlug() != null ? "/" + e.tripSlug() : "")
        + (e.slug() != null ? "/" + e.slug() : "");
  }

  @Test
  void listsPublicTeamAndItsPublicContent() {
    Instant now = Instant.now();
    dataService.createRide(publicTeam, owner, "Sortie", "sortie", now);
    dataService.createPost(publicTeam, owner, "Article", now);
    dataService.createAdditionalPage(publicTeam, owner, "Charte", 0);
    Trip trip = dataService.createTrip(publicTeam, owner, "Voyage", now);
    dataService.createTripStage(owner, trip, "Etape");

    Set<String> entries = entries();

    assertTrue(entries.containsAll(PUBLIC_TEAM), entries::toString);
    assertTrue(entries.contains("RIDE public-team/sortie"), entries::toString);
    assertTrue(entries.contains("POST public-team/article"), entries::toString);
    assertTrue(entries.contains("TEAM_PAGE public-team/charte"), entries::toString);
    assertTrue(entries.contains("TRIP public-team/voyage"), entries::toString);
    assertTrue(entries.contains("TRIP_STAGE public-team/voyage/etape"), entries::toString);
  }

  @Test
  void leavesOutRoutesEvenPublicOnes() {
    dataService.createRoute(publicTeam, owner, "Boucle", Visibility.PUBLIC);

    assertEquals(PUBLIC_TEAM, entries());
  }

  @Test
  void leavesOutAdsEvenOnAPublicTeam() {
    dataService.createAd(publicTeam, owner, "Velo a vendre", AdType.SALE);

    assertTrue(
        sitemapService.getSitemap().entries().stream()
            .noneMatch(e -> e.slug() != null && e.slug().startsWith("velo")));
  }

  @Test
  void leavesOutWhatAnAnonymousListingHides() {
    Instant now = Instant.now();
    dataService.createRide(publicTeam, owner, "Membres", "membres", now, Visibility.TEAM);
    dataService.createRide(publicTeam, owner, "Lien", "lien", now, Visibility.PUBLIC_UNLISTED);
    dataService.createRide(publicTeam, owner, "Brouillon", "brouillon", now, Status.DRAFT);
    Post hidden = dataService.createPost(publicTeam, owner, "Signale", now);
    dataService.hideForModeration(hidden);

    assertEquals(PUBLIC_TEAM, entries());
  }

  @Test
  void leavesOutTeamsThatAreNotPublic() {
    Team unlisted =
        dataService.createTeam(owner, "Unlisted", "unlisted", Visibility.PUBLIC_UNLISTED);
    Team members = dataService.createTeam(owner, "Members", "members", Visibility.TEAM);
    dataService.createPost(unlisted, owner, "Article du lien", Instant.now());
    dataService.createPost(members, owner, "Article des membres", Instant.now());

    assertEquals(PUBLIC_TEAM, entries());
  }

  @Test
  void leavesOutStagesOfAHiddenTrip() {
    Trip trip =
        dataService.createTrip(publicTeam, owner, "Voyage prive", Instant.now(), Visibility.TEAM);
    dataService.createTripStage(owner, trip, "Etape privee");

    assertEquals(PUBLIC_TEAM, entries());
  }

  @Test
  void onAPinnedHost_listsTheOneTeam() {
    Team other = dataService.createTeam(owner, "Other", "other", Visibility.PUBLIC);
    dataService.createPost(other, owner, "Ailleurs", Instant.now());
    dataService.createPost(publicTeam, owner, "Ici", Instant.now());
    DomainAlias alias =
        dataService.createDomainAlias(
            "club.localhost", domain, publicTeam, "Club", "http://club.localhost");
    domainResolver.setAliasForTest(alias);

    assertEquals(
        Set.of("TEAM public-team", "TEAM_ABOUT public-team", "POST public-team/ici"), entries());
  }

  @Test
  void onAPinnedHost_anUnlistedTeamStaysOut() {
    // The pinned listing lets an unlisted team's public content through; every page of that team
    // is noindex all the same (ledger WEB-4), so the sitemap must not announce it.
    Team unlisted =
        dataService.createTeam(owner, "Unlisted", "unlisted", Visibility.PUBLIC_UNLISTED);
    dataService.createPost(unlisted, owner, "Article", Instant.now());
    DomainAlias alias =
        dataService.createDomainAlias(
            "unlisted.localhost", domain, unlisted, "Unlisted", "http://unlisted.localhost");
    domainResolver.setAliasForTest(alias);

    assertEquals(List.of(), sitemapService.getSitemap().entries());
  }
}
