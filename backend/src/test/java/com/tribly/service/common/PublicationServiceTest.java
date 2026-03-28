package com.tribly.service.common;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.publications.response.PublicationListResponse;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PublicationServiceTest extends AbstractBaseTest {

  @Inject PublicationService publicationService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext queryContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private Team privateTeam;
  private User admin;
  private User member;
  private User nonMember;
  private final AtomicInteger slugCounter = new AtomicInteger(0);

  private String nextSlug() {
    return "slug-" + slugCounter.incrementAndGet();
  }

  @BeforeEach
  void setUp() {
    slugCounter.set(0);
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    privateTeam = dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Nested
  class ListPublications {

    @Test
    void shouldReturnPublishedPublicationsForAnonymous() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Public Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Public Post", now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Team Ride", nextSlug(), now, Visibility.TEAM);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
      assertTrue(
          result.publications().stream().allMatch(p -> p.getVisibility() == Visibility.PUBLIC));
    }

    @Test
    void shouldReturnAllPublicationsForMember() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Public Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Team Post", now, Visibility.TEAM);

      queryContext.setUserForTest(member);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }

    @Test
    void shouldFilterByTeamSlugs() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Team1 Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(privateTeam, admin, "Team2 Ride", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Team1 Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldFilterByDateRange() {
      Instant now = Instant.now();
      Instant yesterday = now.minus(1, ChronoUnit.DAYS);
      Instant tomorrow = now.plus(1, ChronoUnit.DAYS);
      Instant lastWeek = now.minus(7, ChronoUnit.DAYS);

      dataService.createRide(team, admin, "Recent Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Old Ride", nextSlug(), lastWeek, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, yesterday, tomorrow, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Recent Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldFilterBySearch() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Mountain Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Beach Ride", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), "Mountain", null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Mountain Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldSupportPagination() {
      Instant now = Instant.now();
      for (int i = 1; i <= 5; i++) {
        dataService.createRide(
            team, admin, "Ride " + i, nextSlug(), now.plus(i, ChronoUnit.HOURS), Visibility.PUBLIC);
      }

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 3);

      assertEquals(3, result.publications().size());
      assertEquals(5, result.total());
      assertEquals(0, result.page());
      assertEquals(3, result.size());
    }

    @Test
    void shouldReturnSecondPage() {
      Instant now = Instant.now();
      for (int i = 1; i <= 5; i++) {
        dataService.createRide(
            team, admin, "Ride " + i, nextSlug(), now.plus(i, ChronoUnit.HOURS), Visibility.PUBLIC);
      }

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 1, 3);

      assertEquals(2, result.publications().size());
      assertEquals(5, result.total());
      assertEquals(1, result.page());
    }

    @Test
    void shouldExcludeDraftPublications() {
      Instant now = Instant.now();
      dataService.createRide(
          team, admin, "Published Ride", nextSlug(), now, Visibility.PUBLIC, Status.PUBLISHED);
      dataService.createRide(
          team, admin, "Draft Ride", nextSlug(), now, Visibility.PUBLIC, Status.DRAFT);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Published Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldExcludeDeletedPublications() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Active Ride", nextSlug(), now, Visibility.PUBLIC);
      Ride deletedRide =
          dataService.createRide(team, admin, "Deleted Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.deleteRide(deletedRide);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Active Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldIncludeRidesAndPosts() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Test Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Test Post", now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
      assertTrue(result.publications().stream().anyMatch(p -> p.getName().equals("Test Ride")));
      assertTrue(result.publications().stream().anyMatch(p -> p.getName().equals("Test Post")));
    }

    @Test
    void shouldReturnEmptyListWhenNoPublications() {
      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(null, Set.of(team.getId()), null, null, null, 0, 10);

      assertTrue(result.publications().isEmpty());
      assertEquals(0, result.total());
    }

    @Test
    void shouldListFromMultipleTeams() {
      Instant now = Instant.now();
      Team team2 = dataService.createTeam(admin, "Team 2", "team-2", Visibility.PUBLIC);
      dataService.createRide(team, admin, "Ride Team 1", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team2, admin, "Ride Team 2", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.list(
              null, Set.of(team.getId(), team2.getId()), null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }

    @Test
    void shouldListAllTeamsWhenTeamSlugsNull() {
      Instant now = Instant.now();
      Team team2 = dataService.createTeam(admin, "Team 2", "team-2", Visibility.PUBLIC);
      dataService.createRide(team, admin, "Ride Team 1", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team2, admin, "Ride Team 2", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result = publicationService.list(null, null, null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }
  }

  @Nested
  class ListAll {

    @Test
    void shouldListAllPublicationsForAnonymous() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Ride 1", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Post 1", now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result = publicationService.listAll(null, null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }

    @Test
    void shouldListAllPublicationsForMember() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Public Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Team Ride", nextSlug(), now, Visibility.TEAM);

      queryContext.setUserForTest(member);
      PublicationListResponse result = publicationService.listAll(null, null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }

    @Test
    void shouldFilterByTypeRide() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Test Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Test Post", now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listAll(PublicationType.RIDE, null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Test Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldFilterByTypePost() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Test Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Test Post", now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listAll(PublicationType.POST, null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Test Post", result.publications().getFirst().getName());
    }

    @Test
    void shouldFilterBySearch() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Mountain Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Beach Ride", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listAll(null, "Mountain", null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Mountain Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldFilterByDateRange() {
      Instant now = Instant.now();
      Instant yesterday = now.minus(1, ChronoUnit.DAYS);
      Instant tomorrow = now.plus(1, ChronoUnit.DAYS);
      Instant lastWeek = now.minus(7, ChronoUnit.DAYS);

      dataService.createRide(team, admin, "Recent Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Old Ride", nextSlug(), lastWeek, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listAll(null, null, yesterday, tomorrow, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Recent Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldSupportPagination() {
      Instant now = Instant.now();
      for (int i = 1; i <= 5; i++) {
        dataService.createRide(
            team, admin, "Ride " + i, nextSlug(), now.plus(i, ChronoUnit.HOURS), Visibility.PUBLIC);
      }

      queryContext.setUserForTest(null);
      PublicationListResponse result = publicationService.listAll(null, null, null, null, 0, 3);

      assertEquals(3, result.publications().size());
      assertEquals(5, result.total());
    }

    @Test
    void shouldListFromMultipleTeams() {
      Instant now = Instant.now();
      Team team2 = dataService.createTeam(admin, "Team 2", "team-2", Visibility.PUBLIC);
      dataService.createRide(team, admin, "Ride Team 1", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team2, admin, "Ride Team 2", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result = publicationService.listAll(null, null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }
  }

  @Nested
  class ListTeam {

    @Test
    void shouldListTeamPublicationsForAnonymousPublicTeam() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Public Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Public Post", now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listTeam(team.getSlug(), null, null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }

    @Test
    void shouldListTeamPublicationsForMember() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Public Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Team Ride", nextSlug(), now, Visibility.TEAM);

      queryContext.setUserForTest(member);
      PublicationListResponse result =
          publicationService.listTeam(team.getSlug(), null, null, null, null, 0, 10);

      assertEquals(2, result.publications().size());
    }

    @Test
    void shouldHideTeamVisibilityFromAnonymous() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Public Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Team Ride", nextSlug(), now, Visibility.TEAM);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listTeam(team.getSlug(), null, null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Public Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldDenyAccessToPrivateTeamForNonMember() {
      dataService.addUserToTeam(member, privateTeam, TeamRole.MEMBER);

      Instant now = Instant.now();
      dataService.createRide(
          privateTeam, admin, "Private Ride", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(nonMember);
      assertThrows(
          ForbiddenException.class,
          () -> publicationService.listTeam(privateTeam.getSlug(), null, null, null, null, 0, 10));
    }

    @Test
    void shouldAllowAccessToPrivateTeamForMember() {
      dataService.addUserToTeam(member, privateTeam, TeamRole.MEMBER);

      Instant now = Instant.now();
      dataService.createRide(
          privateTeam, admin, "Private Ride", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(member);
      PublicationListResponse result =
          publicationService.listTeam(privateTeam.getSlug(), null, null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
    }

    @Test
    void shouldFilterByType() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Test Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createPost(team, admin, "Test Post", now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listTeam(
              team.getSlug(), PublicationType.RIDE, null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Test Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldFilterBySearch() {
      Instant now = Instant.now();
      dataService.createRide(team, admin, "Mountain Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Beach Ride", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listTeam(team.getSlug(), null, "Mountain", null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Mountain Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldFilterByDateRange() {
      Instant now = Instant.now();
      Instant yesterday = now.minus(1, ChronoUnit.DAYS);
      Instant tomorrow = now.plus(1, ChronoUnit.DAYS);
      Instant lastWeek = now.minus(7, ChronoUnit.DAYS);

      dataService.createRide(team, admin, "Recent Ride", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team, admin, "Old Ride", nextSlug(), lastWeek, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listTeam(team.getSlug(), null, null, yesterday, tomorrow, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Recent Ride", result.publications().getFirst().getName());
    }

    @Test
    void shouldSupportPagination() {
      Instant now = Instant.now();
      for (int i = 1; i <= 5; i++) {
        dataService.createRide(
            team, admin, "Ride " + i, nextSlug(), now.plus(i, ChronoUnit.HOURS), Visibility.PUBLIC);
      }

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listTeam(team.getSlug(), null, null, null, null, 0, 3);

      assertEquals(3, result.publications().size());
      assertEquals(5, result.total());
    }

    @Test
    void shouldOnlyListFromSpecifiedTeam() {
      Instant now = Instant.now();
      Team team2 = dataService.createTeam(admin, "Team 2", "team-2", Visibility.PUBLIC);
      dataService.createRide(team, admin, "Ride Team 1", nextSlug(), now, Visibility.PUBLIC);
      dataService.createRide(team2, admin, "Ride Team 2", nextSlug(), now, Visibility.PUBLIC);

      queryContext.setUserForTest(null);
      PublicationListResponse result =
          publicationService.listTeam(team.getSlug(), null, null, null, null, 0, 10);

      assertEquals(1, result.publications().size());
      assertEquals("Ride Team 1", result.publications().getFirst().getName());
    }
  }
}
