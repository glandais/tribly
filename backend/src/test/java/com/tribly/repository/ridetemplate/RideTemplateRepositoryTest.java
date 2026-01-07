package com.tribly.repository.ridetemplate;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ridetemplate.RideTemplate;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.repository.common.TriblyPage;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RideTemplateRepositoryTest {

  @Inject RideTemplateRepository templateRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;
  private Team team;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Nested
  @DisplayName("findByTeamAndSlug")
  class FindByTeamAndSlug {

    @Test
    @DisplayName("Should return template when team id and slug match")
    void findByTeamAndSlug_shouldReturnTemplateWhenMatches() {
      RideTemplate template =
          dataService.createRideTemplate(team, user, "Morning Ride", "morning-ride");

      Optional<RideTemplate> result =
          templateRepository.findByTeamAndSlug(team.getId(), "morning-ride");

      assertTrue(result.isPresent());
      assertEquals(template.getId(), result.get().getId());
      assertEquals("Morning Ride", result.get().getName());
      assertEquals("morning-ride", result.get().getSlug());
    }

    @Test
    @DisplayName("Should return empty when slug doesn't exist")
    void findByTeamAndSlug_shouldReturnEmptyWhenSlugNotExists() {
      dataService.createRideTemplate(team, user, "Morning Ride", "morning-ride");

      Optional<RideTemplate> result =
          templateRepository.findByTeamAndSlug(team.getId(), "nonexistent-slug");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when team doesn't match")
    void findByTeamAndSlug_shouldReturnEmptyWhenTeamNotMatches() {
      RideTemplate template =
          dataService.createRideTemplate(team, user, "Morning Ride", "morning-ride");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      Optional<RideTemplate> result =
          templateRepository.findByTeamAndSlug(otherTeam.getId(), "morning-ride");

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when template is deleted")
    void findByTeamAndSlug_shouldReturnEmptyWhenDeleted() {
      RideTemplate template =
          dataService.createRideTemplate(team, user, "Morning Ride", "morning-ride");
      dataService.deleteRideTemplate(template);

      Optional<RideTemplate> result =
          templateRepository.findByTeamAndSlug(team.getId(), "morning-ride");

      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("existsByTeamAndSlug")
  class ExistsByTeamAndSlug {

    @Test
    @DisplayName("Should return true when template exists")
    void existsByTeamAndSlug_shouldReturnTrueWhenExists() {
      dataService.createRideTemplate(team, user, "Test Template", "test-template");

      boolean exists = templateRepository.existsByTeamAndSlug(team.getId(), "test-template");

      assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when template doesn't exist")
    void existsByTeamAndSlug_shouldReturnFalseWhenNotExists() {
      boolean exists = templateRepository.existsByTeamAndSlug(team.getId(), "nonexistent-slug");

      assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true even for deleted templates (for slug uniqueness)")
    void existsByTeamAndSlug_shouldReturnTrueForDeletedTemplates() {
      RideTemplate template =
          dataService.createRideTemplate(team, user, "Deleted Template", "deleted-template");
      dataService.deleteRideTemplate(template);

      boolean exists = templateRepository.existsByTeamAndSlug(team.getId(), "deleted-template");

      assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false for different team")
    void existsByTeamAndSlug_shouldReturnFalseForDifferentTeam() {
      dataService.createRideTemplate(team, user, "Test Template", "test-template");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      boolean exists = templateRepository.existsByTeamAndSlug(otherTeam.getId(), "test-template");

      assertFalse(exists);
    }
  }

  @Nested
  @DisplayName("findByTeam")
  class FindByTeam {

    @Test
    @DisplayName("Should return all templates for team")
    void findByTeam_shouldReturnAllTemplatesForTeam() {
      dataService.createRideTemplate(team, user, "Template 1", "template-1");
      dataService.createRideTemplate(team, user, "Template 2", "template-2");
      dataService.createRideTemplate(team, user, "Template 3", "template-3");

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), null, 0, 10);

      assertEquals(3, result.items().size());
      assertEquals(3, result.total());
    }

    @Test
    @DisplayName("Should return empty for team with no templates")
    void findByTeam_shouldReturnEmptyForTeamWithNoTemplates() {
      Team emptyTeam = dataService.createTeam(user, "Empty Team", "empty-team", Visibility.PUBLIC);

      TriblyPage<RideTemplate> result =
          templateRepository.findByTeam(emptyTeam.getId(), null, 0, 10);

      assertEquals(0, result.items().size());
      assertEquals(0, result.total());
    }

    @Test
    @DisplayName("Should not return templates from other teams")
    void findByTeam_shouldNotReturnTemplatesFromOtherTeams() {
      dataService.createRideTemplate(team, user, "Team Template", "team-template");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createRideTemplate(otherTeam, user, "Other Team Template", "other-team-template");

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), null, 0, 10);

      assertEquals(1, result.items().size());
      assertEquals("Team Template", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return deleted templates")
    void findByTeam_shouldNotReturnDeletedTemplates() {
      dataService.createRideTemplate(team, user, "Active Template", "active-template");
      RideTemplate deletedTemplate =
          dataService.createRideTemplate(team, user, "Deleted Template", "deleted-template");
      dataService.deleteRideTemplate(deletedTemplate);

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), null, 0, 10);

      assertEquals(1, result.items().size());
      assertEquals("Active Template", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should support pagination with size limit")
    void findByTeam_shouldSupportPaginationSize() {
      for (int i = 1; i <= 10; i++) {
        dataService.createRideTemplate(team, user, "Template " + i, "template-" + i);
      }

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), null, 0, 3);

      assertEquals(3, result.items().size());
      assertEquals(10, result.total());
    }

    @Test
    @DisplayName("Should support pagination with page offset")
    void findByTeam_shouldSupportPaginationOffset() {
      for (int i = 1; i <= 10; i++) {
        dataService.createRideTemplate(team, user, "Template " + i, "template-" + i);
      }

      TriblyPage<RideTemplate> page0 = templateRepository.findByTeam(team.getId(), null, 0, 3);
      TriblyPage<RideTemplate> page1 = templateRepository.findByTeam(team.getId(), null, 1, 3);
      TriblyPage<RideTemplate> page2 = templateRepository.findByTeam(team.getId(), null, 2, 3);

      assertEquals(3, page0.items().size());
      assertEquals(3, page1.items().size());
      assertEquals(3, page2.items().size());
      assertEquals(10, page0.total());
      assertEquals(10, page1.total());
      assertEquals(10, page2.total());
    }

    @Test
    @DisplayName("Should return empty page when beyond results")
    void findByTeam_shouldReturnEmptyPageWhenBeyondResults() {
      dataService.createRideTemplate(team, user, "Template 1", "template-1");
      dataService.createRideTemplate(team, user, "Template 2", "template-2");

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), null, 10, 5);

      assertEquals(0, result.items().size());
      assertEquals(2, result.total());
    }

    @Test
    @DisplayName("Should filter by search term")
    void findByTeam_shouldFilterBySearchTerm() {
      dataService.createRideTemplate(team, user, "Morning Ride", "morning-ride");
      dataService.createRideTemplate(team, user, "Evening Ride", "evening-ride");
      dataService.createRideTemplate(team, user, "Weekend Trip", "weekend-trip");

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), "ride", 0, 10);

      assertEquals(2, result.items().size());
      assertEquals(2, result.total());
    }

    @Test
    @DisplayName("Should filter by search term case insensitive")
    void findByTeam_shouldFilterBySearchTermCaseInsensitive() {
      dataService.createRideTemplate(team, user, "Morning RIDE", "morning-ride");
      dataService.createRideTemplate(team, user, "Evening ride", "evening-ride");

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), "RIDE", 0, 10);

      assertEquals(2, result.items().size());
    }

    @Test
    @DisplayName("Should order results by name ascending")
    void findByTeam_shouldOrderByNameAscending() {
      dataService.createRideTemplate(team, user, "Zebra Ride", "zebra-ride");
      dataService.createRideTemplate(team, user, "Alpha Ride", "alpha-ride");
      dataService.createRideTemplate(team, user, "Middle Ride", "middle-ride");

      TriblyPage<RideTemplate> result = templateRepository.findByTeam(team.getId(), null, 0, 10);

      assertEquals("Alpha Ride", result.items().get(0).getName());
      assertEquals("Middle Ride", result.items().get(1).getName());
      assertEquals("Zebra Ride", result.items().get(2).getName());
    }
  }
}
