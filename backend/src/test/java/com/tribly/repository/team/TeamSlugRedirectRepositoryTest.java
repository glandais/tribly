package com.tribly.repository.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamSlugRedirect;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamSlugRedirectRepositoryTest {

  @Inject TeamSlugRedirectRepository teamSlugRedirectRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Nested
  @DisplayName("findByOldSlug")
  class FindByOldSlug {

    @Test
    void shouldReturnRedirectWhenOldSlugExists() {
      dataService.createTeamSlugRedirect("old-team-slug", team);

      Optional<TeamSlugRedirect> result = teamSlugRedirectRepository.findByOldSlug("old-team-slug");

      assertTrue(result.isPresent());
      assertEquals("old-team-slug", result.get().getOldSlug());
      assertEquals(team.getId(), result.get().getTeam().getId());
    }

    @Test
    void shouldReturnEmptyWhenOldSlugNotExists() {
      Optional<TeamSlugRedirect> result =
          teamSlugRedirectRepository.findByOldSlug("non-existent-slug");

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindCorrectRedirectAmongMultiple() {
      Team team2 = dataService.createTeam(user, "Team 2", "team-2", Visibility.PUBLIC);
      dataService.createTeamSlugRedirect("old-slug-1", team);
      dataService.createTeamSlugRedirect("old-slug-2", team2);

      Optional<TeamSlugRedirect> result = teamSlugRedirectRepository.findByOldSlug("old-slug-2");

      assertTrue(result.isPresent());
      assertEquals("old-slug-2", result.get().getOldSlug());
      assertEquals(team2.getId(), result.get().getTeam().getId());
    }

    @Test
    void shouldBeCaseSensitive() {
      dataService.createTeamSlugRedirect("old-team-slug", team);

      Optional<TeamSlugRedirect> result = teamSlugRedirectRepository.findByOldSlug("Old-Team-Slug");

      assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnRedirectWithTeamAssociation() {
      TeamSlugRedirect redirect = dataService.createTeamSlugRedirect("previous-slug", team);

      Optional<TeamSlugRedirect> result = teamSlugRedirectRepository.findByOldSlug("previous-slug");

      assertTrue(result.isPresent());
      assertEquals(redirect.getId(), result.get().getId());
      assertNotNull(result.get().getTeam());
      assertEquals("test-team", result.get().getTeam().getSlug());
    }
  }

  @Test
  @Transactional
  void shouldDeleteRedirectByOldSlug() {
    dataService.createTeamSlugRedirect("slug-to-delete", team);

    teamSlugRedirectRepository.deleteByOldSlug("slug-to-delete");

    Optional<TeamSlugRedirect> result = teamSlugRedirectRepository.findByOldSlug("slug-to-delete");
    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void shouldNotThrowWhenDeletingNonExistentSlug() {
    assertDoesNotThrow(() -> teamSlugRedirectRepository.deleteByOldSlug("non-existent-slug"));
  }

  @Test
  @Transactional
  void shouldOnlyDeleteSpecifiedSlug() {
    dataService.createTeamSlugRedirect("slug-1", team);
    dataService.createTeamSlugRedirect("slug-2", team);
    dataService.createTeamSlugRedirect("slug-3", team);

    teamSlugRedirectRepository.deleteByOldSlug("slug-2");

    assertTrue(teamSlugRedirectRepository.findByOldSlug("slug-1").isPresent());
    assertTrue(teamSlugRedirectRepository.findByOldSlug("slug-2").isEmpty());
    assertTrue(teamSlugRedirectRepository.findByOldSlug("slug-3").isPresent());
  }

  @Test
  @Transactional
  void shouldBeCaseSensitiveWhenDeleting() {
    dataService.createTeamSlugRedirect("case-sensitive-slug", team);

    teamSlugRedirectRepository.deleteByOldSlug("Case-Sensitive-Slug");

    assertTrue(teamSlugRedirectRepository.findByOldSlug("case-sensitive-slug").isPresent());
  }
}
