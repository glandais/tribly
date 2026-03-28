package fr.pedalons.repository.place;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.enums.Visibility;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PlaceRepository}.
 */
@QuarkusTest
class PlaceRepositoryTest extends AbstractBaseTest {

  @Inject PlaceRepository placeRepository;
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
  @DisplayName("findByIdAndTeam")
  class FindByIdAndTeam {

    @Test
    @DisplayName("Should return place when id and team match")
    void findByIdAndTeam_shouldReturnPlaceWhenMatches() {
      Place place = dataService.createPlace(team, user, "Meeting Point");

      Optional<Place> result = placeRepository.findByIdAndTeam(place.getId(), team.getId());

      assertTrue(result.isPresent());
      assertEquals(place.getId(), result.get().getId());
      assertEquals("Meeting Point", result.get().getName());
    }

    @Test
    @DisplayName("Should return empty when id doesn't exist")
    void findByIdAndTeam_shouldReturnEmptyWhenIdNotExists() {
      dataService.createPlace(team, user, "Meeting Point");

      Optional<Place> result = placeRepository.findByIdAndTeam(999999L, team.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when team doesn't match")
    void findByIdAndTeam_shouldReturnEmptyWhenTeamNotMatches() {
      Place place = dataService.createPlace(team, user, "Meeting Point");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);

      Optional<Place> result = placeRepository.findByIdAndTeam(place.getId(), otherTeam.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when place is deleted")
    void findByIdAndTeam_shouldReturnEmptyWhenDeleted() {
      Place place = dataService.createPlace(team, user, "Meeting Point");
      dataService.deletePlace(place);

      Optional<Place> result = placeRepository.findByIdAndTeam(place.getId(), team.getId());

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should distinguish between places in different teams")
    void findByIdAndTeam_shouldDistinguishBetweenTeams() {
      Place place1 = dataService.createPlace(team, user, "Place in Team 1");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      Place place2 = dataService.createPlace(otherTeam, user, "Place in Team 2");

      Optional<Place> result1 = placeRepository.findByIdAndTeam(place1.getId(), team.getId());
      Optional<Place> result2 = placeRepository.findByIdAndTeam(place2.getId(), otherTeam.getId());
      Optional<Place> wrongTeam =
          placeRepository.findByIdAndTeam(place1.getId(), otherTeam.getId());

      assertTrue(result1.isPresent());
      assertTrue(result2.isPresent());
      assertTrue(wrongTeam.isEmpty());
      assertEquals("Place in Team 1", result1.get().getName());
      assertEquals("Place in Team 2", result2.get().getName());
    }
  }

  @Nested
  @DisplayName("findByTeam")
  class FindByTeam {

    @Test
    @DisplayName("Should return all places for team")
    void findByTeam_shouldReturnAllPlacesForTeam() {
      dataService.createPlace(team, user, "Place 1");
      dataService.createPlace(team, user, "Place 2");
      dataService.createPlace(team, user, "Place 3");

      PedalonsPage<Place> result = placeRepository.findByTeam(team.getId(), 0, 10);

      assertEquals(3, result.items().size());
      assertEquals(3, result.total());
    }

    @Test
    @DisplayName("Should return empty for team with no places")
    void findByTeam_shouldReturnEmptyForTeamWithNoPlaces() {
      Team emptyTeam = dataService.createTeam(user, "Empty Team", "empty-team", Visibility.PUBLIC);

      PedalonsPage<Place> result = placeRepository.findByTeam(emptyTeam.getId(), 0, 10);

      assertEquals(0, result.items().size());
      assertEquals(0, result.total());
    }

    @Test
    @DisplayName("Should not return places from other teams")
    void findByTeam_shouldNotReturnPlacesFromOtherTeams() {
      dataService.createPlace(team, user, "Team Place");
      Team otherTeam = dataService.createTeam(user, "Other Team", "other-team", Visibility.PUBLIC);
      dataService.createPlace(otherTeam, user, "Other Team Place");

      PedalonsPage<Place> result = placeRepository.findByTeam(team.getId(), 0, 10);

      assertEquals(1, result.items().size());
      assertEquals("Team Place", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should not return deleted places")
    void findByTeam_shouldNotReturnDeletedPlaces() {
      dataService.createPlace(team, user, "Active Place");
      Place deletedPlace = dataService.createPlace(team, user, "Deleted Place");
      dataService.deletePlace(deletedPlace);

      PedalonsPage<Place> result = placeRepository.findByTeam(team.getId(), 0, 10);

      assertEquals(1, result.items().size());
      assertEquals("Active Place", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("Should support pagination with size limit")
    void findByTeam_shouldSupportPaginationSize() {
      for (int i = 1; i <= 10; i++) {
        dataService.createPlace(team, user, "Place " + i);
      }

      PedalonsPage<Place> result = placeRepository.findByTeam(team.getId(), 0, 3);

      assertEquals(3, result.items().size());
      assertEquals(10, result.total());
    }

    @Test
    @DisplayName("Should support pagination with page offset")
    void findByTeam_shouldSupportPaginationOffset() {
      for (int i = 1; i <= 10; i++) {
        dataService.createPlace(team, user, "Place " + i);
      }

      PedalonsPage<Place> page0 = placeRepository.findByTeam(team.getId(), 0, 3);
      PedalonsPage<Place> page1 = placeRepository.findByTeam(team.getId(), 1, 3);
      PedalonsPage<Place> page2 = placeRepository.findByTeam(team.getId(), 2, 3);

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
      dataService.createPlace(team, user, "Place 1");
      dataService.createPlace(team, user, "Place 2");

      PedalonsPage<Place> result = placeRepository.findByTeam(team.getId(), 10, 5);

      assertEquals(0, result.items().size());
      assertEquals(2, result.total());
    }

    @Test
    @DisplayName("Should return all places with different types")
    void findByTeam_shouldReturnPlacesWithDifferentTypes() {
      dataService.createPlace(team, user, "Start Only", true, false);
      dataService.createPlace(team, user, "End Only", false, true);
      dataService.createPlace(team, user, "Both", true, true);
      dataService.createPlace(team, user, "Neither", false, false);

      PedalonsPage<Place> result = placeRepository.findByTeam(team.getId(), 0, 10);

      assertEquals(4, result.items().size());
    }
  }

  @Nested
  @DisplayName("Place Entity Properties")
  class PlaceEntityProperties {

    @Test
    @DisplayName("Should create place with default startPlace and endPlace true")
    void createPlace_shouldHaveDefaultProperties() {
      Place place = dataService.createPlace(team, user, "Default Place");

      assertTrue(place.isStartPlace());
      assertTrue(place.isEndPlace());
    }

    @Test
    @DisplayName("Should create place with custom startPlace and endPlace")
    void createPlace_shouldAllowCustomProperties() {
      Place startOnly = dataService.createPlace(team, user, "Start Only", true, false);
      Place endOnly = dataService.createPlace(team, user, "End Only", false, true);
      Place neither = dataService.createPlace(team, user, "Neither", false, false);

      assertTrue(startOnly.isStartPlace());
      assertFalse(startOnly.isEndPlace());

      assertFalse(endOnly.isStartPlace());
      assertTrue(endOnly.isEndPlace());

      assertFalse(neither.isStartPlace());
      assertFalse(neither.isEndPlace());
    }

    @Test
    @DisplayName("Should preserve place team association")
    void createPlace_shouldPreserveTeamAssociation() {
      Place place = dataService.createPlace(team, user, "Place");

      Optional<Place> found = placeRepository.findByIdAndTeam(place.getId(), team.getId());

      assertTrue(found.isPresent());
      assertEquals(team.getId(), found.get().getTeam().getId());
    }
  }
}
