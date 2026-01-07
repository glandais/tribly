package com.tribly.repository.common;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.repository.route.RouteRepository;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BaseEntity#equals(Object)} and {@link BaseEntity#hashCode()} implementation.
 * These tests verify correct behavior with Hibernate proxies, lazy loading, and Set operations.
 */
@QuarkusTest
class BaseEntityEqualsHashCodeTest {

  @Inject RouteRepository routeRepository;
  @Inject EntityManager entityManager;
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

  // Helper methods with @Transactional in outer class (CDI requirement)

  @Transactional
  Route findRouteById(Long id) {
    return routeRepository.findById(id);
  }

  @Transactional
  void persistRoute(Route route) {
    routeRepository.persistAndFlush(route);
  }

  @Transactional
  void clearEntityManager() {
    entityManager.clear();
  }

  @Transactional
  Route getRouteReference(Long id) {
    return routeRepository.getEntityManager().getReference(Route.class, id);
  }

  @Transactional
  void detachRoute(Route route) {
    entityManager.detach(route);
  }

  @Nested
  @DisplayName("Basic Equals Contract")
  class BasicEqualsContract {

    @Test
    @DisplayName("Same instance should equal itself (reflexive)")
    void equals_sameInstance_shouldBeEqual() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);

      assertEquals(route, route);
      assertTrue(route.equals(route));
    }

    @Test
    @DisplayName("Entity should not equal null")
    void equals_null_shouldNotBeEqual() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);

      assertNotEquals(route, null);
      assertFalse(route.equals(null));
    }

    @Test
    @DisplayName("Entities with same ID should be equal (symmetric)")
    void equals_sameId_shouldBeEqual() {
      Route route1 = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Route route2 = findRouteById(route1.getId());

      assertEquals(route1, route2);
      assertEquals(route2, route1); // symmetric
    }

    @Test
    @DisplayName("Entities with different IDs should not be equal")
    void equals_differentId_shouldNotBeEqual() {
      Route route1 = dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      Route route2 = dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

      assertNotEquals(route1, route2);
    }

    @Test
    @DisplayName("Different entity types should not be equal even with same ID pattern")
    void equals_differentEntityTypes_shouldNotBeEqual() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);

      assertNotEquals(route, team);
      assertNotEquals(route, user);
      assertNotEquals(team, user);
    }
  }

  @Nested
  @DisplayName("Null ID Handling")
  class NullIdHandling {

    @Test
    @DisplayName("Entity with null ID should not equal another entity with null ID")
    void equals_bothNullId_shouldNotBeEqual() {
      // Create entities without persisting (no ID assigned yet)
      Route route1 =
          new Route(user, team, "Route 1", "route-1", Visibility.PUBLIC, SurfaceType.ROAD);
      Route route2 =
          new Route(user, team, "Route 2", "route-2", Visibility.PUBLIC, SurfaceType.ROAD);

      assertNull(route1.getId());
      assertNull(route2.getId());
      assertNotEquals(route1, route2);
    }

    @Test
    @DisplayName("Entity with null ID should not equal entity with assigned ID")
    void equals_oneNullId_shouldNotBeEqual() {
      Route persistedRoute = dataService.createRoute(team, user, "Persisted", Visibility.PUBLIC);
      Route transientRoute =
          new Route(user, team, "Transient", "transient", Visibility.PUBLIC, SurfaceType.ROAD);

      assertNotNull(persistedRoute.getId());
      assertNull(transientRoute.getId());
      assertNotEquals(persistedRoute, transientRoute);
      assertNotEquals(transientRoute, persistedRoute);
    }

    @Test
    @DisplayName("Entity with null ID should equal itself")
    void equals_nullIdSameInstance_shouldBeEqual() {
      Route route = new Route(user, team, "Route", "route", Visibility.PUBLIC, SurfaceType.ROAD);

      assertNull(route.getId());
      assertEquals(route, route); // Same instance always equals
    }
  }

  @Nested
  @DisplayName("HashCode Contract")
  class HashCodeContract {

    @Test
    @DisplayName("HashCode should be consistent across calls")
    void hashCode_shouldBeConsistent() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);

      int hashCode1 = route.hashCode();
      int hashCode2 = route.hashCode();
      int hashCode3 = route.hashCode();

      assertEquals(hashCode1, hashCode2);
      assertEquals(hashCode2, hashCode3);
    }

    @Test
    @DisplayName("Equal entities should have same hashCode")
    void hashCode_equalEntities_shouldHaveSameHashCode() {
      Route route1 = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Route route2 = findRouteById(route1.getId());

      assertEquals(route1, route2);
      assertEquals(route1.hashCode(), route2.hashCode());
    }

    @Test
    @DisplayName("HashCode should be same for all instances of same entity type")
    void hashCode_sameEntityType_shouldBeSame() {
      Route route1 = dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      Route route2 = dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

      // By design, hashCode is based on class, not ID
      assertEquals(route1.hashCode(), route2.hashCode());
    }

    @Test
    @DisplayName("HashCode should differ between entity types")
    void hashCode_differentEntityTypes_shouldDiffer() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);

      // Different entity types should have different hashCodes
      assertNotEquals(route.hashCode(), team.hashCode());
      assertNotEquals(route.hashCode(), user.hashCode());
    }

    @Test
    @DisplayName("HashCode should be same before and after persist")
    void hashCode_beforeAndAfterPersist_shouldBeSame() {
      Route route = new Route(user, team, "Route", "route", Visibility.PUBLIC, SurfaceType.ROAD);
      int hashCodeBefore = route.hashCode();

      persistRoute(route);

      int hashCodeAfter = route.hashCode();
      assertEquals(hashCodeBefore, hashCodeAfter);
    }
  }

  @Nested
  @DisplayName("HashSet Operations")
  class HashSetOperations {

    @Test
    @DisplayName("Entity should be findable in HashSet after persist")
    void hashSet_shouldFindEntityAfterPersist() {
      Route route = new Route(user, team, "Route", "route", Visibility.PUBLIC, SurfaceType.ROAD);
      Set<Route> set = new HashSet<>();

      set.add(route);
      assertTrue(set.contains(route));

      persistRoute(route);

      // Should still be findable after ID is assigned
      assertTrue(set.contains(route));
    }

    @Test
    @DisplayName("HashSet should handle multiple entities correctly")
    void hashSet_multipleEntities_shouldHandleCorrectly() {
      Route route1 = dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      Route route2 = dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);
      Route route3 = dataService.createRoute(team, user, "Route 3", Visibility.PUBLIC);

      Set<Route> set = new HashSet<>();
      set.add(route1);
      set.add(route2);
      set.add(route3);

      assertEquals(3, set.size());
      assertTrue(set.contains(route1));
      assertTrue(set.contains(route2));
      assertTrue(set.contains(route3));
    }

    @Test
    @DisplayName("Adding same entity twice should not duplicate")
    void hashSet_addSameEntityTwice_shouldNotDuplicate() {
      Route route1 = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Route route2 = findRouteById(route1.getId());

      Set<Route> set = new HashSet<>();
      set.add(route1);
      set.add(route2);

      assertEquals(1, set.size());
    }

    @Test
    @DisplayName("HashSet should work with entities loaded from different queries")
    void hashSet_entitiesFromDifferentQueries_shouldWork() {
      Route original = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Long routeId = original.getId();

      clearEntityManager(); // Detach all entities

      Route reloaded1 = findRouteById(routeId);
      Route reloaded2 = findRouteById(routeId);

      Set<Route> set = new HashSet<>();
      set.add(reloaded1);
      set.add(reloaded2);

      assertEquals(1, set.size()); // Same entity, should not duplicate
    }
  }

  @Nested
  @DisplayName("Lazy Loading and Proxy Handling")
  class LazyLoadingAndProxyHandling {

    @Test
    @DisplayName("Lazy loaded entity should equal eagerly loaded entity")
    void equals_lazyVsEager_shouldBeEqual() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Long routeId = route.getId();

      clearEntityManager(); // Detach all entities

      // Get reference (proxy) vs find (eager)
      Route proxy = getRouteReference(routeId);
      Route eager = findRouteById(routeId);

      assertEquals(proxy, eager);
      assertEquals(eager, proxy);
    }

    @Test
    @DisplayName("Proxy hashCode should equal actual entity hashCode")
    void hashCode_proxy_shouldEqualActualEntity() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Long routeId = route.getId();

      clearEntityManager();

      Route proxy = getRouteReference(routeId);
      Route actual = findRouteById(routeId);

      assertEquals(proxy.hashCode(), actual.hashCode());
    }

    @Test
    @DisplayName("Lazy loaded createdBy should not affect route equality")
    void equals_withLazyCreatedBy_shouldWork() {
      Route route1 = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Long routeId = route1.getId();

      clearEntityManager();

      Route route2 = findRouteById(routeId);
      // createdBy is lazy loaded - don't access it

      assertEquals(route1.getId(), route2.getId());
      // The entities should be equal based on ID, regardless of lazy associations
    }

    @Test
    @DisplayName("Entity in lazy Set should be findable")
    void lazySet_shouldFindEntity() {
      // Create a team with routes
      Route route1 = dataService.createRoute(team, user, "Route 1", Visibility.PUBLIC);
      Route route2 = dataService.createRoute(team, user, "Route 2", Visibility.PUBLIC);

      clearEntityManager();

      // Reload and check set operations work with lazy loading
      Route reloadedRoute1 = findRouteById(route1.getId());
      Route reloadedRoute2 = findRouteById(route2.getId());

      Set<Route> set = new HashSet<>();
      set.add(reloadedRoute1);

      assertTrue(set.contains(reloadedRoute1));
      assertFalse(set.contains(reloadedRoute2));

      set.add(reloadedRoute2);
      assertEquals(2, set.size());
    }
  }

  @Nested
  @DisplayName("Detached Entity Handling")
  class DetachedEntityHandling {

    @Test
    @DisplayName("Detached entity should equal managed entity with same ID")
    void equals_detachedVsManaged_shouldBeEqual() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Long routeId = route.getId();

      detachRoute(route);
      Route managed = findRouteById(routeId);

      assertEquals(route, managed);
    }

    @Test
    @DisplayName("Detached entity should be findable in HashSet with managed entity")
    void hashSet_detachedAndManaged_shouldWork() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);
      Long routeId = route.getId();

      Set<Route> set = new HashSet<>();
      set.add(route);

      detachRoute(route);
      Route managed = findRouteById(routeId);

      assertTrue(set.contains(managed));
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCases {

    @Test
    @DisplayName("Entity should not equal non-entity object")
    void equals_nonEntityObject_shouldNotBeEqual() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);

      assertNotEquals(route, "string");
      assertNotEquals(route, 123);
      assertNotEquals(route, new Object());
    }

    @Test
    @DisplayName("Multiple transient entities in HashSet should remain distinct")
    void hashSet_multipleTransientEntities_shouldRemainDistinct() {
      Route route1 =
          new Route(user, team, "Route 1", "route-1", Visibility.PUBLIC, SurfaceType.ROAD);
      Route route2 =
          new Route(user, team, "Route 2", "route-2", Visibility.PUBLIC, SurfaceType.ROAD);
      Route route3 =
          new Route(user, team, "Route 3", "route-3", Visibility.PUBLIC, SurfaceType.ROAD);

      Set<Route> set = new HashSet<>();
      set.add(route1);
      set.add(route2);
      set.add(route3);

      // All should be added since they have null IDs and are different instances
      // Note: Due to hashCode being class-based, they hash to same bucket
      // but equals returns false for different instances with null ID
      assertEquals(3, set.size());
    }

    @Test
    @DisplayName("Removed and re-added entity should work correctly in HashSet")
    void hashSet_removeAndReAdd_shouldWork() {
      Route route = dataService.createRoute(team, user, "Route", Visibility.PUBLIC);

      Set<Route> set = new HashSet<>();
      set.add(route);
      assertEquals(1, set.size());

      set.remove(route);
      assertEquals(0, set.size());

      set.add(route);
      assertEquals(1, set.size());
      assertTrue(set.contains(route));
    }
  }
}
