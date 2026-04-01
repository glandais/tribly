package fr.pedalons.service.place;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.places.request.PlaceRequest;
import fr.pedalons.dto.places.response.PlaceDetailDto;
import fr.pedalons.dto.places.response.PlaceListResponse;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PlaceServiceTest extends AbstractBaseTest {

  @Inject PlaceService placeService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext queryContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User admin;
  private User organizer;
  private User member;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Nested
  class ListPlaces {

    @Test
    void shouldListPlacesForOrganizer() {
      dataService.createPlace(team, admin, "Place 1");
      dataService.createPlace(team, admin, "Place 2");

      queryContext.setUserForTest(organizer);
      PlaceListResponse result = placeService.listPlaces(team.getSlug(), 0, 10, null, false, false);

      assertEquals(2, result.places().size());
      assertEquals(2, result.total());
    }

    @Test
    void shouldSupportPagination() {
      for (int i = 1; i <= 5; i++) {
        dataService.createPlace(team, admin, "Place " + i);
      }

      queryContext.setUserForTest(organizer);
      PlaceListResponse result = placeService.listPlaces(team.getSlug(), 0, 3, null, false, false);

      assertEquals(3, result.places().size());
      assertEquals(5, result.total());
    }

    @Test
    void shouldThrowForMember() {
      queryContext.setUserForTest(member);
      assertThrows(
          PedalonsException.class,
          () -> placeService.listPlaces(team.getSlug(), 0, 10, null, false, false));
    }

    @Test
    void shouldExcludeDeletedPlaces() {
      dataService.createPlace(team, admin, "Active Place");
      Place deletedPlace = dataService.createPlace(team, admin, "Deleted Place");
      dataService.deletePlace(deletedPlace);

      queryContext.setUserForTest(organizer);
      PlaceListResponse result = placeService.listPlaces(team.getSlug(), 0, 10, null, false, false);

      assertEquals(1, result.places().size());
      assertEquals("Active Place", result.places().getFirst().name());
    }

    @Test
    void shouldFilterBySearch() {
      dataService.createPlace(team, admin, "Gare de Lyon");
      dataService.createPlace(team, admin, "Place de la République");

      queryContext.setUserForTest(organizer);
      PlaceListResponse result =
          placeService.listPlaces(team.getSlug(), 0, 10, "gare", false, false);

      assertEquals(1, result.places().size());
      assertEquals("Gare de Lyon", result.places().getFirst().name());
    }

    @Test
    void shouldFilterByStartPlace() {
      dataService.createPlace(team, admin, "Start Only", true, false);
      dataService.createPlace(team, admin, "End Only", false, true);
      dataService.createPlace(team, admin, "Both", true, true);

      queryContext.setUserForTest(organizer);
      PlaceListResponse result = placeService.listPlaces(team.getSlug(), 0, 10, null, true, false);

      assertEquals(2, result.places().size());
      assertTrue(result.places().stream().allMatch(PlaceDetailDto::startPlace));
    }
  }

  @Nested
  class GetPlace {

    @Test
    void shouldReturnPlaceForOrganizer() {
      Place place = dataService.createPlace(team, admin, "Test Place");
      String placeId = TsidUtils.toString(place.getId());

      queryContext.setUserForTest(organizer);
      PlaceDetailDto result = placeService.getPlace(team.getSlug(), placeId);

      assertNotNull(result);
      assertEquals("Test Place", result.name());
    }

    @Test
    void shouldThrowForNonexistentPlace() {
      queryContext.setUserForTest(organizer);
      assertThrows(
          PedalonsException.class,
          () -> placeService.getPlace(team.getSlug(), TsidUtils.toString(9999L)));
    }

    @Test
    void shouldThrowForMember() {
      Place place = dataService.createPlace(team, admin, "Test Place");
      String placeId = TsidUtils.toString(place.getId());

      queryContext.setUserForTest(member);
      assertThrows(PedalonsException.class, () -> placeService.getPlace(team.getSlug(), placeId));
    }
  }

  @Nested
  class CreatePlace {

    @Test
    void shouldCreatePlaceForOrganizer() {
      PlaceRequest request =
          new PlaceRequest("New Place", "123 Main St", "http://example.com", true, false, null);

      queryContext.setUserForTest(organizer);
      PlaceDetailDto result = placeService.createPlace(team.getSlug(), request);

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

      queryContext.setUserForTest(organizer);
      PlaceDetailDto result = placeService.createPlace(team.getSlug(), request);

      assertNotNull(result);
      assertNotNull(result.geometry());
    }

    @Test
    void shouldCreatePlaceWithoutCoordinates() {
      PlaceRequest request = new PlaceRequest("No Geo Place", null, null, true, true, null);

      queryContext.setUserForTest(organizer);
      PlaceDetailDto result = placeService.createPlace(team.getSlug(), request);

      assertNotNull(result);
      assertNull(result.geometry());
    }

    @Test
    void shouldThrowForMember() {
      PlaceRequest request = new PlaceRequest("Place", null, null, true, true, null);

      queryContext.setUserForTest(member);
      assertThrows(
          PedalonsException.class, () -> placeService.createPlace(team.getSlug(), request));
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

      queryContext.setUserForTest(organizer);
      PlaceDetailDto result = placeService.updatePlace(team.getSlug(), placeId, request);

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
      queryContext.setUserForTest(organizer);
      placeService.updatePlace(team.getSlug(), placeId, withGeo);

      // Then clear them
      PlaceRequest noGeo = new PlaceRequest("No Geo", null, null, true, true, null);
      queryContext.setUserForTest(organizer);
      PlaceDetailDto result = placeService.updatePlace(team.getSlug(), placeId, noGeo);

      assertNull(result.geometry());
    }

    @Test
    void shouldThrowForNonexistentPlace() {
      PlaceRequest request = new PlaceRequest("Place", null, null, true, true, null);

      queryContext.setUserForTest(organizer);
      assertThrows(
          PedalonsException.class,
          () -> placeService.updatePlace(team.getSlug(), TsidUtils.toString(9999L), request));
    }

    @Test
    void shouldThrowForMember() {
      Place place = dataService.createPlace(team, admin, "Test");
      String placeId = TsidUtils.toString(place.getId());
      PlaceRequest request = new PlaceRequest("Updated", null, null, true, true, null);

      queryContext.setUserForTest(member);
      assertThrows(
          PedalonsException.class,
          () -> placeService.updatePlace(team.getSlug(), placeId, request));
    }
  }

  @Nested
  class DeletePlace {

    @Test
    void shouldSoftDeletePlace() {
      Place place = dataService.createPlace(team, admin, "To Delete");
      String placeId = TsidUtils.toString(place.getId());

      queryContext.setUserForTest(organizer);
      placeService.deletePlace(team.getSlug(), placeId);

      queryContext.setUserForTest(organizer);
      assertThrows(PedalonsException.class, () -> placeService.getPlace(team.getSlug(), placeId));
    }

    @Test
    void shouldThrowForNonexistentPlace() {
      queryContext.setUserForTest(organizer);
      assertThrows(
          PedalonsException.class,
          () -> placeService.deletePlace(team.getSlug(), TsidUtils.toString(9999L)));
    }

    @Test
    void shouldThrowForMember() {
      Place place = dataService.createPlace(team, admin, "Test");
      String placeId = TsidUtils.toString(place.getId());

      queryContext.setUserForTest(member);
      assertThrows(
          PedalonsException.class, () -> placeService.deletePlace(team.getSlug(), placeId));
    }
  }
}
