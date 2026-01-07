package com.tribly.repository.team;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamPage;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import com.tribly.repository.common.TriblyPage;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TeamPageRepositoryTest {

  @Inject TeamPageRepository teamPageRepository;
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
  @DisplayName("includeAbout parameter")
  class IncludeAboutParameter {

    @Test
    void find_shouldIncludeAboutPageByDefault() {
      dataService.createAdditionalPage(team, user, "Page 1", 0);

      TeamPageQuery query = TeamPageQuery.builder().teamIds(Set.of(team.getId())).build();
      TriblyPage<TeamPage> result = teamPageRepository.find(query);

      assertEquals(2, result.items().size());
      assertEquals(2, result.total());
    }

    @Test
    void find_shouldIncludeAboutPageWhenIncludeAboutIsTrue() {
      dataService.createAdditionalPage(team, user, "Page 1", 0);

      TeamPageQuery query =
          TeamPageQuery.builder().teamIds(Set.of(team.getId())).includeAbout(true).build();
      TriblyPage<TeamPage> result = teamPageRepository.find(query);

      assertEquals(2, result.items().size());
      assertTrue(result.items().stream().anyMatch(TeamPage::isAboutPage));
    }

    @Test
    void find_shouldExcludeAboutPageWhenIncludeAboutIsFalse() {

      dataService.createAdditionalPage(team, user, "Page 1", 0);
      dataService.createAdditionalPage(team, user, "Page 2", 1);

      TeamPageQuery query =
          TeamPageQuery.builder().teamIds(Set.of(team.getId())).includeAbout(false).build();
      TriblyPage<TeamPage> result = teamPageRepository.find(query);

      assertEquals(2, result.items().size());
      assertTrue(result.items().stream().noneMatch(TeamPage::isAboutPage));
    }

    @Test
    void find_shouldReturnOnlyAboutPageWhenNoAdditionalPages() {

      TeamPageQuery query = TeamPageQuery.builder().teamIds(Set.of(team.getId())).build();
      TriblyPage<TeamPage> result = teamPageRepository.find(query);

      assertEquals(1, result.items().size());
      assertTrue(result.items().getFirst().isAboutPage());
    }

    @Test
    void find_shouldReturnEmptyWhenExcludingAboutAndNoAdditionalPages() {

      TeamPageQuery query =
          TeamPageQuery.builder().teamIds(Set.of(team.getId())).includeAbout(false).build();
      TriblyPage<TeamPage> result = teamPageRepository.find(query);

      assertEquals(0, result.items().size());
    }
  }

  @Nested
  @DisplayName("countAdditionalPages")
  class CountAdditionalPages {

    @Test
    void shouldReturnCorrectCountOfAdditionalPages() {

      dataService.createAdditionalPage(team, user, "Page 1", 0);
      dataService.createAdditionalPage(team, user, "Page 2", 1);
      dataService.createAdditionalPage(team, user, "Page 3", 2);

      long count = teamPageRepository.countAdditionalPages(team.getId());

      assertEquals(3, count);
    }

    @Test
    void shouldReturnZeroWhenOnlyAboutPageExists() {

      long count = teamPageRepository.countAdditionalPages(team.getId());

      assertEquals(0, count);
    }

    @Test
    void shouldReturnZeroWhenNoPages() {
      long count = teamPageRepository.countAdditionalPages(team.getId());

      assertEquals(0, count);
    }

    @Test
    void shouldExcludeDeletedAdditionalPages() {

      dataService.createAdditionalPage(team, user, "Visible Page", 0);
      TeamPage deletedPage = dataService.createAdditionalPage(team, user, "Deleted Page", 1);
      dataService.deleteTeamPage(deletedPage);

      long count = teamPageRepository.countAdditionalPages(team.getId());

      assertEquals(1, count);
    }

    @Test
    void shouldNotCountPagesFromOtherTeams() {
      dataService.createAdditionalPage(team, user, "Team 1 Page", 0);
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createAdditionalPage(otherTeam, user, "Team 2 Page", 0);

      long count = teamPageRepository.countAdditionalPages(team.getId());

      assertEquals(1, count);
    }
  }

  @Nested
  @DisplayName("getNextPageOrder")
  class GetNextPageOrder {

    @Test
    void shouldReturnZeroWhenNoAdditionalPages() {

      int nextOrder = teamPageRepository.getNextPageOrder(team.getId());

      assertEquals(0, nextOrder);
    }

    @Test
    void shouldReturnZeroWhenNoPages() {
      int nextOrder = teamPageRepository.getNextPageOrder(team.getId());

      assertEquals(0, nextOrder);
    }

    @Test
    void shouldReturnNextOrderAfterExistingPages() {
      dataService.createAdditionalPage(team, user, "Page 1", 0);
      dataService.createAdditionalPage(team, user, "Page 2", 1);
      dataService.createAdditionalPage(team, user, "Page 3", 2);

      int nextOrder = teamPageRepository.getNextPageOrder(team.getId());

      assertEquals(3, nextOrder);
    }

    @Test
    void shouldHandleNonSequentialPageOrders() {
      dataService.createAdditionalPage(team, user, "Page A", 0);
      dataService.createAdditionalPage(team, user, "Page B", 5);
      dataService.createAdditionalPage(team, user, "Page C", 10);

      int nextOrder = teamPageRepository.getNextPageOrder(team.getId());

      assertEquals(11, nextOrder);
    }

    @Test
    void shouldIgnoreDeletedPagesForNextOrder() {

      dataService.createAdditionalPage(team, user, "Page 1", 0);
      TeamPage deletedPage = dataService.createAdditionalPage(team, user, "Deleted Page", 5);
      dataService.deleteTeamPage(deletedPage);

      int nextOrder = teamPageRepository.getNextPageOrder(team.getId());

      assertEquals(1, nextOrder);
    }

    @Test
    void shouldNotConsiderPagesFromOtherTeams() {
      dataService.createAdditionalPage(team, user, "Team 1 Page", 0);
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createAdditionalPage(otherTeam, user, "Team 2 Page", 10);

      int nextOrder = teamPageRepository.getNextPageOrder(team.getId());

      assertEquals(1, nextOrder);
    }

    @Test
    void shouldIgnoreAboutPageOrder() {

      int nextOrder = teamPageRepository.getNextPageOrder(team.getId());

      assertEquals(0, nextOrder);
    }
  }
}
