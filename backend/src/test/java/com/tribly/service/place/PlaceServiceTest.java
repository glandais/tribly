package com.tribly.service.place;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.place.Place;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.places.request.PlaceRequest;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.places.response.PlaceListResponse;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PlaceServiceTest {

  @Inject PlaceService placeService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private User admin;
  private User organizer;
  private User member;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    dataService.addUserToTeam(admin, team, TeamRole.ADMIN);
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Nested
  class ListPlaces {

    @Test
    void shouldListPlacesForOrganizer() {
      dataService.createPlace(team, admin, "Place 1");
      dataService.createPlace(team, admin, "Place 2");

      PlaceListResponse result = placeService.listPlaces(team, 0, 10, organizer);

      assertEquals(2, result.places().size());
      assertEquals(2, result.total());
    }

    @Test
    void shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createPlace(team, admin, "Place " + i);
      }

      PlaceListResponse result = placeService.listPlaces(team, 0, 3, organizer);

      assertEquals(3, result.places().size());
      assertEquals(5, result.total());
    }

    @Test
    void shouldThrowForMember() {
      assertThrows(BusinessException.class, () -> placeService.listPlaces(team, 0, 10, member));
    }

    @Test
    void shouldExcludeDeletedPlaces() {
      dataService.createPlace(team, admin, "Active Place");
      Place deletedPlace = dataService.createPlace(team, admin, "Deleted Place");
      dataService.deletePlace(deletedPlace);

      PlaceListResponse result = placeService.listPlaces(team, 0, 10, organizer);

      assertEquals(1, result.places().size());
      assertEquals("Active Place", result.places().getFirst().name());
    }
  }

  @Nested
  class GetPlace {

    @Test
    void shouldReturnPlaceForOrganizer() {
      Place place = dataService.createPlace(team, admin, "Test Place");
      String placeId = TsidUtils.toString(place.getId());

      PlaceDetailDto result = placeService.getPlace(team, placeId, organizer);

      assertNotNull(result);
      assertEquals("Test Place", result.name());
    }

    @Test
    void shouldThrowForNonexistentPlace() {
      assertThrows(
          BusinessException.class,
          () -> placeService.getPlace(team, TsidUtils.toString(9999L), organizer));
    }

    @Test
    void shouldThrowForMember() {
      Place place = dataService.createPlace(team, admin, "Test Place");
      String placeId = TsidUtils.toString(place.getId());

      assertThrows(BusinessException.class, () -> placeService.getPlace(team, placeId, member));
    }
  }

  @Nested
  class CreatePlace {

    @Test
    void shouldCreatePlaceForOrganizer() {
      PlaceRequest request =
          new PlaceRequest("New Place", "123 Main St", "http://example.com", true, false, null);

      PlaceDetailDto result = placeService.createPlace(team, request, organizer);

      assertNotNull(result);
      assertEquals("New Place", result.name());
      assertEquals("123 Main St", result.address());
      assertEquals("http://example.com", result.link());
      assertTrue(result.startPlace());
      assertFalse(result.endPlace());
    }

    @Test
    void shouldCreatePlaceWithCoordinates() {
      Point<G2D> point = point(WGS84, g(2.3522, 48.8566));
      PlaceRequest request = new PlaceRequest("Geo Place", null, null, false, true, point);

      PlaceDetailDto result = placeService.createPlace(team, request, organizer);

      assertNotNull(result);
      assertNotNull(result.geometry());
    }

    @Test
    void shouldCreatePlaceWithoutCoordinates() {
      PlaceRequest request = new PlaceRequest("No Geo Place", null, null, true, true, null);

      PlaceDetailDto result = placeService.createPlace(team, request, organizer);

      assertNotNull(result);
      assertNull(result.geometry());
    }

    @Test
    void shouldThrowForMember() {
      PlaceRequest request = new PlaceRequest("Place", null, null, true, true, null);

      assertThrows(BusinessException.class, () -> placeService.createPlace(team, request, member));
    }
  }

  @Nested
  class UpdatePlace {

    @Test
    void shouldUpdateAllFields() {
      Place place = dataService.createPlace(team, admin, "Original", true, true);
      String placeId = TsidUtils.toString(place.getId());
      Point<G2D> point = point(WGS84, g(2.3522, 48.8566));
      PlaceRequest request =
          new PlaceRequest("Updated", "New Address", "http://new.com", false, true, point);

      PlaceDetailDto result = placeService.updatePlace(team, placeId, request, organizer);

      assertEquals("Updated", result.name());
      assertEquals("New Address", result.address());
      assertEquals("http://new.com", result.link());
      assertFalse(result.startPlace());
      assertTrue(result.endPlace());
      assertNotNull(result.geometry());
    }

    @Test
    void shouldClearGeometry() {
      Place place = dataService.createPlace(team, admin, "With Geo", true, true);
      String placeId = TsidUtils.toString(place.getId());
      // First set coordinates
      Point<G2D> point = point(WGS84, g(2.3522, 48.8566));
      PlaceRequest withGeo = new PlaceRequest("With Geo", null, null, true, true, point);
      placeService.updatePlace(team, placeId, withGeo, organizer);

      // Then clear them
      PlaceRequest noGeo = new PlaceRequest("No Geo", null, null, true, true, null);
      PlaceDetailDto result = placeService.updatePlace(team, placeId, noGeo, organizer);

      assertNull(result.geometry());
    }

    @Test
    void shouldThrowForNonexistentPlace() {
      PlaceRequest request = new PlaceRequest("Place", null, null, true, true, null);

      assertThrows(
          BusinessException.class,
          () -> placeService.updatePlace(team, TsidUtils.toString(9999L), request, organizer));
    }

    @Test
    void shouldThrowForMember() {
      Place place = dataService.createPlace(team, admin, "Test");
      String placeId = TsidUtils.toString(place.getId());
      PlaceRequest request = new PlaceRequest("Updated", null, null, true, true, null);

      assertThrows(
          BusinessException.class, () -> placeService.updatePlace(team, placeId, request, member));
    }
  }

  @Nested
  class DeletePlace {

    @Test
    void shouldSoftDeletePlace() {
      Place place = dataService.createPlace(team, admin, "To Delete");
      String placeId = TsidUtils.toString(place.getId());

      placeService.deletePlace(team, placeId, organizer);

      assertThrows(BusinessException.class, () -> placeService.getPlace(team, placeId, organizer));
    }

    @Test
    void shouldThrowForNonexistentPlace() {
      assertThrows(
          BusinessException.class,
          () -> placeService.deletePlace(team, TsidUtils.toString(9999L), organizer));
    }

    @Test
    void shouldThrowForMember() {
      Place place = dataService.createPlace(team, admin, "Test");
      String placeId = TsidUtils.toString(place.getId());

      assertThrows(BusinessException.class, () -> placeService.deletePlace(team, placeId, member));
    }
  }
}
