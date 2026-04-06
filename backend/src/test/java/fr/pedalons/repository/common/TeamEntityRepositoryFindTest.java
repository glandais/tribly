package fr.pedalons.repository.common;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.repository.route.RouteQuery;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for {@link TeamEntityRepository#find}
 * method. Tests visibility rules, filtering, pagination, and search functionality using Route
 * entities as the test subject.
 */
@QuarkusTest
class TeamEntityRepositoryFindTest extends AbstractBaseTest {

  @Inject RouteRepository routeRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private User publicTeamOwner;
  private User privateTeamOwner;
  private User regularUser;
  private Team publicTeam;
  private Team privateTeam;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    publicTeamOwner = dataService.createUser("public-owner@example.com", "Public Team Owner");
    privateTeamOwner = dataService.createUser("private-owner@example.com", "Private Team Owner");
    regularUser = dataService.createUser("regular@example.com", "Regular User");
    publicTeam =
        dataService.createTeam(publicTeamOwner, "Public Team", "public-team", Visibility.PUBLIC);
    privateTeam =
        dataService.createTeam(privateTeamOwner, "Private Team", "private-team", Visibility.TEAM);
  }

  @Nested
  @DisplayName("Anonymous User Visibility")
  class AnonymousUserVisibility {

    @Test
    @DisplayName("Should return only PUBLIC entities from PUBLIC teams")
    void find_anonymousUser_shouldReturnOnlyPublicEntities() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Public Route", Visibility.PUBLIC);
      dataService.createRoute(publicTeam, publicTeamOwner, "Team Route", Visibility.TEAM);
      dataService.createRoute(
          privateTeam, privateTeamOwner, "Private Team Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Public Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return TEAM visibility entities")
    void find_anonymousUser_shouldNotReturnTeamVisibilityEntities() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Team Only Route", Visibility.TEAM);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should not return entities from TEAM visibility teams")
    void find_anonymousUser_shouldNotReturnEntitiesFromPrivateTeams() {
      dataService.createRoute(
          privateTeam, privateTeamOwner, "Route in Private Team", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should not return DRAFT status entities")
    void find_anonymousUser_shouldNotReturnDraftEntities() {
      dataService.createRoute(
          publicTeam, publicTeamOwner, "Draft Route", Visibility.PUBLIC, Status.DRAFT, null, null);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should return CANCELLED status entities")
    void find_anonymousUser_shouldReturnCancelledEntities() {
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Cancelled Route",
          Visibility.PUBLIC,
          Status.CANCELLED,
          null,
          null);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Cancelled Route", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Authenticated User - Team Member Visibility")
  class AuthenticatedMemberVisibility {

    @Test
    @DisplayName("Should return PUBLIC entities from PUBLIC teams")
    void find_authenticatedMember_shouldReturnPublicEntities() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Public Route", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Public Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should return TEAM visibility entities when user is a member")
    void find_authenticatedMember_shouldReturnTeamVisibilityEntitiesWhenMember() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.MEMBER);
      dataService.createRoute(publicTeam, publicTeamOwner, "Team Only Route", Visibility.TEAM);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Team Only Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return TEAM visibility entities when user is not a member")
    void find_authenticatedNonMember_shouldNotReturnTeamVisibilityEntities() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Team Only Route", Visibility.TEAM);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should return entities from TEAM visibility teams when user is a member")
    void find_authenticatedMember_shouldReturnEntitiesFromPrivateTeamsWhenMember() {
      dataService.addUserToTeam(regularUser, privateTeam, TeamRole.MEMBER);
      dataService.createRoute(
          privateTeam, privateTeamOwner, "Route in Private Team", Visibility.TEAM);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Route in Private Team", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return DRAFT entities when user is a regular member")
    void find_authenticatedMember_shouldNotReturnDraftEntities() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.MEMBER);
      dataService.createRoute(
          publicTeam, publicTeamOwner, "Draft Route", Visibility.PUBLIC, Status.DRAFT, null, null);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should not return entities when membership is deleted")
    void find_authenticatedMember_shouldNotReturnEntitiesWhenMembershipDeleted() {
      UserTeam membership = dataService.addUserToTeam(regularUser, publicTeam, TeamRole.MEMBER);
      dataService.createRoute(publicTeam, publicTeamOwner, "Team Only Route", Visibility.TEAM);
      dataService.deleteUserTeam(membership);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }
  }

  @Nested
  @DisplayName("Authenticated User - Organizer/Admin Visibility")
  class AuthenticatedOrganizerAdminVisibility {

    @Test
    @DisplayName("ORGANIZER should see DRAFT entities from their team")
    void find_organizer_shouldSeeDraftEntities() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.ORGANIZER);
      dataService.createRoute(
          publicTeam, publicTeamOwner, "Draft Route", Visibility.PUBLIC, Status.DRAFT, null, null);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Draft Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("ADMIN should see DRAFT entities from their team")
    void find_admin_shouldSeeDraftEntities() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.ADMIN);
      dataService.createRoute(
          publicTeam, publicTeamOwner, "Draft Route", Visibility.PUBLIC, Status.DRAFT, null, null);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Draft Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("ORGANIZER should not see DRAFT entities from other teams")
    void find_organizer_shouldNotSeeDraftEntitiesFromOtherTeams() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.ORGANIZER);
      Team anotherTeam =
          dataService.createTeam(
              privateTeamOwner, "Another Team", "another-team", Visibility.PUBLIC);
      dataService.createRoute(
          anotherTeam,
          privateTeamOwner,
          "Draft Route",
          Visibility.PUBLIC,
          Status.DRAFT,
          null,
          null);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertTrue(result.items().stream().noneMatch(r -> "Draft Route".equals(r.getName())));
    }

    @Test
    @DisplayName("ADMIN should see all entity statuses from their team")
    void find_admin_shouldSeeAllEntityStatuses() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.ADMIN);
      dataService.createRoute(
          publicTeam, publicTeamOwner, "Draft Route", Visibility.PUBLIC, Status.DRAFT, null, null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Published Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          null,
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Cancelled Route",
          Visibility.PUBLIC,
          Status.CANCELLED,
          null,
          null);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
    }
  }

  @Nested
  @DisplayName("Deleted Entities Filtering")
  class DeletedEntitiesFiltering {

    @Test
    @DisplayName("Should never return deleted entities for anonymous users")
    void find_anonymousUser_shouldNeverReturnDeletedEntities() {
      Route route =
          dataService.createRoute(publicTeam, publicTeamOwner, "Deleted Route", Visibility.PUBLIC);
      dataService.deleteRoute(route);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should never return deleted entities for authenticated users")
    void find_authenticatedUser_shouldNeverReturnDeletedEntities() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.ADMIN);
      Route route =
          dataService.createRoute(publicTeam, publicTeamOwner, "Deleted Route", Visibility.PUBLIC);
      dataService.deleteRoute(route);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should not return entities from deleted teams")
    void find_shouldNotReturnEntitiesFromDeletedTeams() {
      dataService.createRoute(
          publicTeam, publicTeamOwner, "Route in Deleted Team", Visibility.PUBLIC);
      dataService.deleteTeam(publicTeam);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }
  }

  @Nested
  @DisplayName("Team Slug Filtering")
  class TeamSlugFiltering {

    @Test
    @DisplayName("Should filter by single team slug")
    void find_shouldFilterBySingleTeamSlug() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Public Team Route", Visibility.PUBLIC);
      Team anotherTeam =
          dataService.createTeam(
              publicTeamOwner, "Another Team", "another-team", Visibility.PUBLIC);
      dataService.createRoute(
          anotherTeam, publicTeamOwner, "Another Team Route", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).teamIds(Set.of(publicTeam.getId())).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Public Team Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should filter by multiple team slugs")
    void find_shouldFilterByMultipleTeamSlugs() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Public Team Route", Visibility.PUBLIC);
      Team anotherTeam =
          dataService.createTeam(
              publicTeamOwner, "Another Team", "another-team", Visibility.PUBLIC);
      dataService.createRoute(
          anotherTeam, publicTeamOwner, "Another Team Route", Visibility.PUBLIC);
      Team thirdTeam =
          dataService.createTeam(publicTeamOwner, "Third Team", "third-team", Visibility.PUBLIC);
      dataService.createRoute(thirdTeam, publicTeamOwner, "Third Team Route", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .teamIds(Set.of(publicTeam.getId(), anotherTeam.getId()))
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(2, result.items().size());
      assertTrue(result.items().stream().anyMatch(r -> "Public Team Route".equals(r.getName())));
      assertTrue(result.items().stream().anyMatch(r -> "Another Team Route".equals(r.getName())));
    }

    @Test
    @DisplayName("Should return empty when team slug doesn't match")
    void find_shouldReturnEmptyWhenTeamSlugDoesNotMatch() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).teamIds(Set.of(-1L)).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should ignore empty team slugs set")
    void find_shouldIgnoreEmptyTeamSlugsSet() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).teamIds(Set.of()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
    }
  }

  @Nested
  @DisplayName("Entity Slug Filtering")
  class EntitySlugFiltering {

    @Test
    @DisplayName("Should filter by entity slug")
    void find_shouldFilterBySlug() {
      Route route1 =
          dataService.createRoute(publicTeam, publicTeamOwner, "Route One", Visibility.PUBLIC);
      dataService.createRoute(publicTeam, publicTeamOwner, "Route Two", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).slug(route1.getSlug()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Route One", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should return empty when slug doesn't exist")
    void find_shouldReturnEmptyWhenSlugDoesNotExist() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Route", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).slug("nonexistent-slug").build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }
  }

  @Nested
  @DisplayName("Entity ID Filtering")
  class EntityIdFiltering {

    @Test
    @DisplayName("Should filter by entity ID")
    void find_shouldFilterById() {
      Route route1 =
          dataService.createRoute(publicTeam, publicTeamOwner, "Route One", Visibility.PUBLIC);
      dataService.createRoute(publicTeam, publicTeamOwner, "Route Two", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).id(route1.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals(route1.getId(), result.items().getFirst().getId());
    }

    @Test
    @DisplayName("Should return empty when ID doesn't exist")
    void find_shouldReturnEmptyWhenIdDoesNotExist() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).id(999999L).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }
  }

  @Nested
  @DisplayName("Search Filtering")
  class SearchFiltering {

    @Test
    @DisplayName("Should search by name")
    void find_shouldSearchByName() {
      dataService.createRoute(
          publicTeam, publicTeamOwner, "Mountain Climb Route", Visibility.PUBLIC);
      dataService.createRoute(publicTeam, publicTeamOwner, "Flat Sprint Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).search("Mountain").build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Mountain Climb Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should search by markdown content")
    void find_shouldSearchByMarkdown() {
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Route One",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          null,
          "This route has challenging climbs");
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Route Two",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          null,
          "This route is flat and fast");

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).search("challenging").build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Route One", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should search case-insensitively")
    void find_shouldSearchCaseInsensitively() {
      dataService.createRoute(publicTeam, publicTeamOwner, "MOUNTAIN Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).search("mountain").build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
    }

    @Test
    @DisplayName("Should return empty when search term doesn't match")
    void find_shouldReturnEmptyWhenSearchDoesNotMatch() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Coastal Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).search("mountain").build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
    }

    @Test
    @DisplayName("Should ignore null search")
    void find_shouldIgnoreNullSearch() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).search(null).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
    }

    @Test
    @DisplayName("Should ignore empty search")
    void find_shouldIgnoreEmptySearch() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Route", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).search("").build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
    }
  }

  @Nested
  @DisplayName("Date Range Filtering")
  class DateRangeFiltering {

    @Test
    @DisplayName("Should filter by from date")
    void find_shouldFilterByFromDate() {
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Old Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "New Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 6, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .from(LocalDate.of(2025, 3, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("New Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should filter by to date")
    void find_shouldFilterByToDate() {
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Old Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "New Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 6, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .to(LocalDate.of(2025, 3, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Old Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should filter by date range")
    void find_shouldFilterByDateRange() {
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "January Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 1, 15).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "March Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 3, 15).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "June Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 6, 15).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .from(LocalDate.of(2025, 2, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .to(LocalDate.of(2025, 5, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("March Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should include boundary dates")
    void find_shouldIncludeBoundaryDates() {
      Instant exactDate = LocalDate.of(2025, 3, 15).atStartOfDay().toInstant(ZoneOffset.UTC);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Exact Date Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          exactDate,
          null);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).from(exactDate).to(exactDate).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
    }
  }

  @Nested
  @DisplayName("Pagination")
  class Pagination {

    @BeforeEach
    void setUpPaginationData() {
      for (int i = 1; i <= 10; i++) {
        dataService.createRoute(
            publicTeam,
            publicTeamOwner,
            "Route " + i,
            Visibility.PUBLIC,
            Status.PUBLISHED,
            LocalDate.of(2025, 1, i).atStartOfDay().toInstant(ZoneOffset.UTC),
            null);
      }
    }

    @Test
    @DisplayName("Should return limited results by size")
    void find_shouldReturnLimitedResultsBySize() {
      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).size(3).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals(10, result.total());
    }

    @Test
    @DisplayName("Should return correct page")
    void find_shouldReturnCorrectPage() {
      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).page(1).size(3).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals(10, result.total());
    }

    @Test
    @DisplayName("Should return empty page when beyond results")
    void find_shouldReturnEmptyPageWhenBeyondResults() {
      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).page(10).size(5).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(0, result.items().size());
      assertEquals(10, result.total());
    }

    @Test
    @DisplayName("Should return all results when size is larger than total")
    void find_shouldReturnAllResultsWhenSizeIsLarger() {
      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).size(100).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(10, result.items().size());
      assertEquals(10, result.total());
    }

    @Test
    @DisplayName("Should use default pagination when not specified")
    void find_shouldUseDefaultPaginationWhenNotSpecified() {
      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertTrue(result.items().size() <= 10);
    }
  }

  @Nested
  @DisplayName("Combined Filters")
  class CombinedFilters {

    @Test
    @DisplayName("Should combine team slug and search filters")
    void find_shouldCombineTeamSlugAndSearch() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Mountain Route", Visibility.PUBLIC);
      dataService.createRoute(publicTeam, publicTeamOwner, "Coastal Route", Visibility.PUBLIC);
      Team anotherTeam =
          dataService.createTeam(
              publicTeamOwner, "Another Team", "another-team", Visibility.PUBLIC);
      dataService.createRoute(anotherTeam, publicTeamOwner, "Mountain Trail", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .teamIds(Set.of(publicTeam.getId()))
              .search("Mountain")
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Mountain Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should combine visibility and date filters for authenticated user")
    void find_shouldCombineVisibilityAndDateFiltersForAuthenticatedUser() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.MEMBER);

      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Old Public",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "New Team",
          Visibility.TEAM,
          Status.PUBLISHED,
          LocalDate.of(2025, 6, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Old Team",
          Visibility.TEAM,
          Status.PUBLISHED,
          LocalDate.of(2025, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .userId(regularUser.getId())
              .from(LocalDate.of(2025, 3, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("New Team", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should combine all filters")
    void find_shouldCombineAllFilters() {
      dataService.addUserToTeam(regularUser, publicTeam, TeamRole.MEMBER);

      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "Mountain Adventure",
          Visibility.TEAM,
          Status.PUBLISHED,
          LocalDate.of(2025, 6, 15).atStartOfDay().toInstant(ZoneOffset.UTC),
          "A challenging mountain climb");

      // Decoys
      dataService.createRoute(publicTeam, publicTeamOwner, "Mountain Public", Visibility.PUBLIC);
      dataService.createRoute(publicTeam, publicTeamOwner, "Coastal Adventure", Visibility.TEAM);

      RouteQuery query =
          RouteQuery.builder()
              .domainId(domain.getId())
              .userId(regularUser.getId())
              .teamIds(Set.of(publicTeam.getId()))
              .search("Mountain")
              .from(LocalDate.of(2025, 6, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .to(LocalDate.of(2025, 7, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
              .build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Mountain Adventure", result.items().getFirst().getName());
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCases {

    @Test
    @DisplayName("Should return public entities for non-existent user ID")
    void find_shouldReturnPublicEntitiesForNonExistentUserId() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Public Route", Visibility.PUBLIC);
      dataService.createRoute(publicTeam, publicTeamOwner, "Team Route", Visibility.TEAM);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).userId(999999L).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Public Route", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should handle special characters in search")
    void find_shouldHandleSpecialCharactersInSearch() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Route (Test)", Visibility.PUBLIC);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).search("(Test)").build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(1, result.items().size());
    }

    @Test
    @DisplayName("Should return correct total count with filters")
    void find_shouldReturnCorrectTotalCountWithFilters() {
      for (int i = 1; i <= 5; i++) {
        dataService.createRoute(publicTeam, publicTeamOwner, "Mountain " + i, Visibility.PUBLIC);
      }
      for (int i = 1; i <= 5; i++) {
        dataService.createRoute(publicTeam, publicTeamOwner, "Coastal " + i, Visibility.PUBLIC);
      }

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).search("Mountain").size(2).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(5, result.total());
    }

    @Test
    @DisplayName("Should handle concurrent visibility scenarios")
    void find_shouldHandleConcurrentVisibilityScenarios() {
      Team team1 = dataService.createTeam(publicTeamOwner, "Team 1", "team-1", Visibility.PUBLIC);
      Team team2 = dataService.createTeam(publicTeamOwner, "Team 2", "team-2", Visibility.PUBLIC);
      dataService.addUserToTeam(regularUser, team1, TeamRole.MEMBER);

      dataService.createRoute(team1, publicTeamOwner, "Team 1 - Team Route", Visibility.TEAM);
      dataService.createRoute(team2, publicTeamOwner, "Team 2 - Team Route", Visibility.TEAM);
      dataService.createRoute(team1, publicTeamOwner, "Team 1 - Public Route", Visibility.PUBLIC);
      dataService.createRoute(team2, publicTeamOwner, "Team 2 - Public Route", Visibility.PUBLIC);

      RouteQuery query =
          RouteQuery.builder().domainId(domain.getId()).userId(regularUser.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertTrue(result.items().stream().anyMatch(r -> "Team 1 - Team Route".equals(r.getName())));
      assertTrue(
          result.items().stream().anyMatch(r -> "Team 1 - Public Route".equals(r.getName())));
      assertTrue(
          result.items().stream().anyMatch(r -> "Team 2 - Public Route".equals(r.getName())));
      assertFalse(result.items().stream().anyMatch(r -> "Team 2 - Team Route".equals(r.getName())));
    }
  }

  @Nested
  @DisplayName("Ordering")
  class Ordering {

    @Test
    @DisplayName("Should order by dateTime descending")
    void find_shouldOrderByDateTimeDescending() {
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "January Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "March Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 3, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);
      dataService.createRoute(
          publicTeam,
          publicTeamOwner,
          "February Route",
          Visibility.PUBLIC,
          Status.PUBLISHED,
          LocalDate.of(2025, 2, 1).atStartOfDay().toInstant(ZoneOffset.UTC),
          null);

      RouteQuery query = RouteQuery.builder().domainId(domain.getId()).build();
      PedalonsPage<Route> result = routeRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals("March Route", result.items().get(0).getName());
      assertEquals("February Route", result.items().get(1).getName());
      assertEquals("January Route", result.items().get(2).getName());
    }
  }

  @Nested
  @DisplayName("findByTeamAndSlug")
  class FindByTeamAndSlug {

    @Test
    @DisplayName("Should return entity when team and slug match")
    void findByTeamAndSlug_shouldReturnEntityWhenMatches() {
      Route route =
          dataService.createRoute(publicTeam, publicTeamOwner, "Test Route", Visibility.PUBLIC);

      var result =
          routeRepository.findByTeamAndSlug(
              domain.getId(), publicTeam.getId(), publicTeamOwner.getId(), route.getSlug(), false);

      assertTrue(result.isPresent());
      assertEquals(route.getId(), result.get().getId());
      assertEquals("Test Route", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty when team doesn't match")
    void findByTeamAndSlug_shouldReturnEmptyWhenTeamDoesNotMatch() {
      Route route =
          dataService.createRoute(publicTeam, publicTeamOwner, "Test Route", Visibility.PUBLIC);

      var result =
          routeRepository.findByTeamAndSlug(
              domain.getId(), privateTeam.getId(), publicTeamOwner.getId(), route.getSlug(), false);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when slug doesn't match")
    void findByTeamAndSlug_shouldReturnEmptyWhenSlugDoesNotMatch() {
      dataService.createRoute(publicTeam, publicTeamOwner, "Test Route", Visibility.PUBLIC);

      var result =
          routeRepository.findByTeamAndSlug(
              domain.getId(),
              publicTeam.getId(),
              publicTeamOwner.getId(),
              "nonexistent-slug",
              false);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when entity is deleted")
    void findByTeamAndSlug_shouldReturnEmptyWhenDeleted() {
      Route route =
          dataService.createRoute(publicTeam, publicTeamOwner, "Deleted Route", Visibility.PUBLIC);
      dataService.deleteRoute(route);

      var result =
          routeRepository.findByTeamAndSlug(
              domain.getId(), publicTeam.getId(), publicTeamOwner.getId(), route.getSlug(), false);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return entity regardless of visibility")
    void findByTeamAndSlug_shouldReturnEntityRegardlessOfVisibility() {
      Route teamRoute =
          dataService.createRoute(publicTeam, publicTeamOwner, "Team Route", Visibility.TEAM);

      var result =
          routeRepository.findByTeamAndSlug(
              domain.getId(),
              publicTeam.getId(),
              publicTeamOwner.getId(),
              teamRoute.getSlug(),
              false);

      assertTrue(result.isPresent());
      assertEquals("Team Route", result.get().getName());
    }

    @Test
    @DisplayName("Should return entity regardless of status")
    void findByTeamAndSlug_shouldReturnEntityRegardlessOfStatus() {
      Route draftRoute =
          dataService.createRoute(
              publicTeam,
              publicTeamOwner,
              "Draft Route",
              Visibility.PUBLIC,
              Status.DRAFT,
              null,
              null);

      var result =
          routeRepository.findByTeamAndSlug(
              domain.getId(),
              publicTeam.getId(),
              publicTeamOwner.getId(),
              draftRoute.getSlug(),
              false);

      assertTrue(result.isPresent());
      assertEquals("Draft Route", result.get().getName());
      assertEquals(Status.DRAFT, result.get().getStatus());
    }

    @Test
    @DisplayName("Should NOT return entity from deleted team")
    void findByTeamAndSlug_shouldNotReturnEntityFromDeletedTeam() {
      Route route =
          dataService.createRoute(publicTeam, publicTeamOwner, "Route", Visibility.PUBLIC);
      dataService.deleteTeam(publicTeam);

      var result =
          routeRepository.findByTeamAndSlug(
              domain.getId(), publicTeam.getId(), publicTeamOwner.getId(), route.getSlug(), false);

      // Note: findByTeamAndSlug does NOT filter by team.deleted
      assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should distinguish between routes with same slug in different teams")
    void findByTeamAndSlug_shouldDistinguishBetweenTeams() {
      Route route1 =
          dataService.createRoute(publicTeam, publicTeamOwner, "Same Name", Visibility.PUBLIC);
      Team anotherTeam =
          dataService.createTeam(
              publicTeamOwner, "Another Team", "another-team", Visibility.PUBLIC);
      Route route2 =
          dataService.createRoute(anotherTeam, publicTeamOwner, "Same Name", Visibility.PUBLIC);

      var result1 =
          routeRepository.findByTeamAndSlug(
              domain.getId(), publicTeam.getId(), publicTeamOwner.getId(), route1.getSlug(), false);
      var result2 =
          routeRepository.findByTeamAndSlug(
              domain.getId(),
              anotherTeam.getId(),
              publicTeamOwner.getId(),
              route2.getSlug(),
              false);

      assertTrue(result1.isPresent());
      assertTrue(result2.isPresent());
      assertEquals(publicTeam.getId(), result1.get().getTeam().getId());
      assertEquals(anotherTeam.getId(), result2.get().getTeam().getId());
    }
  }
}
