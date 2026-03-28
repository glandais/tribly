package fr.pedalons.service.common;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.common.TeamEntitySlugRedirect;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.TeamSlugRedirect;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SlugServiceTest extends AbstractBaseTest {

  @Inject SlugService slugService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  @Nested
  class Slugify {

    @Test
    void shouldConvertSimpleName() {
      String result = SlugService.slugify("Hello World");

      assertEquals("hello-world", result);
    }

    @Test
    void shouldHandleSpecialCharacters() {
      String result = SlugService.slugify("Côte d'Azur & Beyond!");

      assertEquals("cote-d-azur-beyond", result);
    }

    @Test
    void shouldConvertUnderscoresToHyphens() {
      String result = SlugService.slugify("hello_world_test");

      assertEquals("hello-world-test", result);
    }

    @Test
    void shouldHandleAccentedCharacters() {
      String result = SlugService.slugify("Café élégant");

      assertEquals("cafe-elegant", result);
    }

    @Test
    void shouldHandleNumbers() {
      String result = SlugService.slugify("Route 66");

      assertEquals("route-66", result);
    }

    @Test
    void shouldHandleEmptyString() {
      String result = SlugService.slugify("");

      assertEquals("", result);
    }

    @Test
    void shouldTrimWhitespace() {
      String result = SlugService.slugify("  Trimmed  ");

      assertEquals("trimmed", result);
    }

    @Test
    void shouldHandleMultipleSpaces() {
      String result = SlugService.slugify("Multiple   Spaces   Here");

      assertEquals("multiple-spaces-here", result);
    }
  }

  @Nested
  class GenerateSlug {

    @Test
    void shouldReturnBaseSlugWhenNotExists() {
      String result = slugService.generateSlug("Test Name", slug -> false);

      assertEquals("test-name", result);
    }

    @Test
    void shouldAppendNumberWhenSlugExists() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("test-name");

      String result = slugService.generateSlug("Test Name", existingSlugs::contains);

      assertEquals("test-name-1", result);
    }

    @Test
    void shouldIncrementNumberUntilUnique() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("test-name");
      existingSlugs.add("test-name-1");
      existingSlugs.add("test-name-2");

      String result = slugService.generateSlug("Test Name", existingSlugs::contains);

      assertEquals("test-name-3", result);
    }

    @Test
    void shouldHandleManyCollisions() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("route");
      for (int i = 1; i <= 100; i++) {
        existingSlugs.add("route-" + i);
      }

      String result = slugService.generateSlug("Route", existingSlugs::contains);

      assertEquals("route-101", result);
    }

    @Test
    void shouldWorkWithSpecialCharactersInName() {
      Set<String> existingSlugs = new HashSet<>();
      existingSlugs.add("cafe-paris");

      String result = slugService.generateSlug("Café Paris", existingSlugs::contains);

      assertEquals("cafe-paris-1", result);
    }
  }

  @Nested
  class IsValidSlug {

    @Test
    void shouldAcceptSimpleSlug() {
      assertTrue(slugService.isValidSlug("hello"));
    }

    @Test
    void shouldAcceptSlugWithHyphens() {
      assertTrue(slugService.isValidSlug("hello-world"));
    }

    @Test
    void shouldAcceptSlugWithNumbers() {
      assertTrue(slugService.isValidSlug("route-66"));
    }

    @Test
    void shouldAcceptSlugWithMultipleHyphens() {
      assertTrue(slugService.isValidSlug("my-long-slug-name"));
    }

    @Test
    void shouldAcceptSingleCharacter() {
      assertTrue(slugService.isValidSlug("a"));
    }

    @Test
    void shouldAcceptPureNumbers() {
      assertTrue(slugService.isValidSlug("123"));
    }

    @Test
    void shouldRejectEmptyString() {
      assertFalse(slugService.isValidSlug(""));
    }

    @Test
    void shouldRejectSlugWithLeadingHyphen() {
      assertFalse(slugService.isValidSlug("-hello"));
    }

    @Test
    void shouldRejectSlugWithTrailingHyphen() {
      assertFalse(slugService.isValidSlug("hello-"));
    }

    @Test
    void shouldRejectSlugWithConsecutiveHyphens() {
      assertFalse(slugService.isValidSlug("hello--world"));
    }

    @Test
    void shouldRejectSlugWithUppercase() {
      assertFalse(slugService.isValidSlug("Hello"));
    }

    @Test
    void shouldRejectSlugWithSpaces() {
      assertFalse(slugService.isValidSlug("hello world"));
    }

    @Test
    void shouldRejectSlugWithUnderscores() {
      assertFalse(slugService.isValidSlug("hello_world"));
    }

    @Test
    void shouldRejectSlugWithSpecialCharacters() {
      assertFalse(slugService.isValidSlug("hello@world"));
      assertFalse(slugService.isValidSlug("hello!world"));
      assertFalse(slugService.isValidSlug("hello#world"));
    }

    @Test
    void shouldRejectSlugExceedingMaxLength() {
      String longSlug = "a".repeat(201);
      assertFalse(slugService.isValidSlug(longSlug));
    }

    @Test
    void shouldAcceptSlugAtMaxLength() {
      String maxSlug = "a".repeat(200);
      assertTrue(slugService.isValidSlug(maxSlug));
    }
  }

  @Nested
  class CreateTeamRedirect {

    private Domain domain;
    private Team team;
    private User admin;

    @BeforeEach
    void setUp() {
      dataCleaner.cleanAll();
      domain = dataService.getOrCreateDefaultDomain();
      domainResolver.setDomainForTest(domain);
      admin = dataService.createUser("admin@example.com", "Admin");
      team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    }

    @Test
    void shouldCreateTeamRedirect() {
      String oldSlug = "old-team-slug";

      slugService.createTeamRedirect(team, oldSlug);

      Optional<TeamSlugRedirect> redirect =
          slugService.resolveTeamRedirect(domain.getId(), oldSlug);
      assertTrue(redirect.isPresent());
      assertEquals(team.getId(), redirect.get().getTeam().getId());
      assertEquals(oldSlug, redirect.get().getOldSlug());
    }

    @Test
    void shouldResolveTeamRedirectByOldSlug() {
      String oldSlug = "previous-slug";
      slugService.createTeamRedirect(team, oldSlug);

      Optional<TeamSlugRedirect> resolved =
          slugService.resolveTeamRedirect(domain.getId(), oldSlug);

      assertTrue(resolved.isPresent());
      assertEquals(team.getId(), resolved.get().getTeam().getId());
    }

    @Test
    void shouldReturnEmptyForNonexistentRedirect() {
      Optional<TeamSlugRedirect> resolved =
          slugService.resolveTeamRedirect(domain.getId(), "nonexistent-slug");

      assertFalse(resolved.isPresent());
    }

    @Test
    void shouldClearTeamRedirect() {
      String oldSlug = "to-be-cleared";
      slugService.createTeamRedirect(team, oldSlug);
      assertTrue(slugService.resolveTeamRedirect(domain.getId(), oldSlug).isPresent());

      slugService.clearTeamRedirect(domain.getId(), oldSlug);

      assertFalse(slugService.resolveTeamRedirect(domain.getId(), oldSlug).isPresent());
    }

    @Test
    void shouldSupportMultipleRedirectsForSameTeam() {
      String oldSlug1 = "old-slug-1";
      String oldSlug2 = "old-slug-2";

      slugService.createTeamRedirect(team, oldSlug1);
      slugService.createTeamRedirect(team, oldSlug2);

      assertTrue(slugService.resolveTeamRedirect(domain.getId(), oldSlug1).isPresent());
      assertTrue(slugService.resolveTeamRedirect(domain.getId(), oldSlug2).isPresent());
    }
  }

  @Nested
  class CreateEntityRedirect {

    private Domain domain;
    private Team team;
    private User admin;

    @BeforeEach
    void setUp() {
      dataCleaner.cleanAll();
      domain = dataService.getOrCreateDefaultDomain();
      domainResolver.setDomainForTest(domain);
      admin = dataService.createUser("admin@example.com", "Admin");
      team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    }

    @Test
    void shouldCreateEntityRedirect() {
      Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
      String oldSlug = "old-ride-slug";

      slugService.createEntityRedirect(ride, oldSlug);

      Optional<TeamEntitySlugRedirect> redirect =
          slugService.resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug);
      assertTrue(redirect.isPresent());
      assertEquals(ride.getId(), redirect.get().getEntityId());
      assertEquals(oldSlug, redirect.get().getOldSlug());
    }

    @Test
    void shouldResolveEntityRedirectByOldSlug() {
      Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
      String oldSlug = "previous-ride-slug";
      slugService.createEntityRedirect(ride, oldSlug);

      Optional<TeamEntitySlugRedirect> resolved =
          slugService.resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug);

      assertTrue(resolved.isPresent());
      assertEquals(ride.getId(), resolved.get().getEntityId());
    }

    @Test
    void shouldReturnEmptyForNonexistentEntityRedirect() {
      Optional<TeamEntitySlugRedirect> resolved =
          slugService.resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, "nonexistent-slug");

      assertFalse(resolved.isPresent());
    }

    @Test
    void shouldClearEntityRedirect() {
      Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
      String oldSlug = "to-be-cleared";
      slugService.createEntityRedirect(ride, oldSlug);
      assertTrue(
          slugService
              .resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug)
              .isPresent());

      slugService.clearEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug);

      assertFalse(
          slugService
              .resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug)
              .isPresent());
    }

    @Test
    void shouldSupportMultipleRedirectsForSameEntity() {
      Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
      String oldSlug1 = "old-slug-1";
      String oldSlug2 = "old-slug-2";

      slugService.createEntityRedirect(ride, oldSlug1);
      slugService.createEntityRedirect(ride, oldSlug2);

      assertTrue(
          slugService
              .resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug1)
              .isPresent());
      assertTrue(
          slugService
              .resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug2)
              .isPresent());
    }

    @Test
    void shouldIsolateRedirectsByEntityType() {
      Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
      String oldSlug = "shared-slug";

      slugService.createEntityRedirect(ride, oldSlug);

      // Should find redirect for RIDE type
      assertTrue(
          slugService
              .resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug)
              .isPresent());
      // Should NOT find redirect for different entity type
      assertFalse(
          slugService
              .resolveEntityRedirect(team.getId(), TeamEntityType.POST, oldSlug)
              .isPresent());
    }

    @Test
    void shouldIsolateRedirectsByTeam() {
      Team team2 = dataService.createTeam(admin, "Team 2", "team-2", Visibility.PUBLIC);
      Ride ride = dataService.createRide(team, admin, "Test Ride", "test-ride", Instant.now());
      String oldSlug = "ride-slug";

      slugService.createEntityRedirect(ride, oldSlug);

      // Should find redirect for original team
      assertTrue(
          slugService
              .resolveEntityRedirect(team.getId(), TeamEntityType.RIDE, oldSlug)
              .isPresent());
      // Should NOT find redirect for different team
      assertFalse(
          slugService
              .resolveEntityRedirect(team2.getId(), TeamEntityType.RIDE, oldSlug)
              .isPresent());
    }
  }
}
