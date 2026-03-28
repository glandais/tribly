package com.tribly.repository.ad;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.domain.ad.Ad;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.TriblyPage;
import com.tribly.enums.AdType;
import com.tribly.enums.EntityType;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdRepositoryTest extends AbstractBaseTest {

  @Inject AdRepository adRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    user = dataService.createUser("test@example.com", "Test User");
    team = dataService.createTeam(user, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Nested
  @DisplayName("Query Builder Methods")
  class QueryBuilderMethods {

    @Test
    void getQuerySlug_shouldBuildCorrectQuery() {
      AdQuery query =
          adRepository.getQuerySlug(domain.getId(), team.getId(), user.getId(), "test-slug");

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    void getQuerySlug_shouldWorkWithNullUserId() {
      AdQuery query = adRepository.getQuerySlug(domain.getId(), team.getId(), null, "test-slug");

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals("test-slug", query.slug());
    }

    @Test
    void getQueryId_shouldBuildCorrectQuery() {
      AdQuery query = adRepository.getQueryId(domain.getId(), team.getId(), user.getId(), 12345L);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertEquals(user.getId(), query.userId());
      assertEquals(12345L, query.id());
    }

    @Test
    void getQueryId_shouldWorkWithNullUserId() {
      AdQuery query = adRepository.getQueryId(domain.getId(), team.getId(), null, 12345L);

      assertEquals(domain.getId(), query.domainId());
      assertTrue(query.teamIds().contains(team.getId()));
      assertNull(query.userId());
      assertEquals(12345L, query.id());
    }
  }

  @Nested
  @DisplayName("Entity Type Methods")
  class EntityTypeMethods {

    @Test
    void getEntityType_shouldReturnAd() {
      assertEquals(com.tribly.enums.TeamEntityType.AD, adRepository.getEntityType());
    }

    @Test
    void getAllEntityType_shouldReturnAd() {
      assertEquals(EntityType.AD, adRepository.getAllEntityType());
    }
  }

  @Nested
  @DisplayName("Ad Type Filter")
  class AdTypeFilter {

    @Test
    void find_shouldReturnAllAdsWithoutAdTypeFilter() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);
      dataService.createAd(team, user, "Rental Ad", AdType.RENTAL);
      dataService.createAd(team, user, "Wanted Ad", AdType.WANTED);

      AdQuery query = AdQuery.builder().domainId(domain.getId()).userId(user.getId()).build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals(3, result.total());
    }

    @Test
    void find_shouldFilterBySaleAdType() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);
      dataService.createAd(team, user, "Rental Ad", AdType.RENTAL);
      dataService.createAd(team, user, "Wanted Ad", AdType.WANTED);

      AdQuery query =
          AdQuery.builder()
              .domainId(domain.getId())
              .userId(user.getId())
              .adType(AdType.SALE)
              .build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Sale Ad", result.items().getFirst().getName());
      assertEquals(AdType.SALE, result.items().getFirst().getAdType());
    }

    @Test
    void find_shouldFilterByRentalAdType() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);
      dataService.createAd(team, user, "Rental Ad", AdType.RENTAL);
      dataService.createAd(team, user, "Wanted Ad", AdType.WANTED);

      AdQuery query =
          AdQuery.builder()
              .domainId(domain.getId())
              .userId(user.getId())
              .adType(AdType.RENTAL)
              .build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Rental Ad", result.items().getFirst().getName());
      assertEquals(AdType.RENTAL, result.items().getFirst().getAdType());
    }

    @Test
    void find_shouldFilterByWantedAdType() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);
      dataService.createAd(team, user, "Rental Ad", AdType.RENTAL);
      dataService.createAd(team, user, "Wanted Ad", AdType.WANTED);

      AdQuery query =
          AdQuery.builder()
              .domainId(domain.getId())
              .userId(user.getId())
              .adType(AdType.WANTED)
              .build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Wanted Ad", result.items().getFirst().getName());
      assertEquals(AdType.WANTED, result.items().getFirst().getAdType());
    }

    @Test
    void find_shouldReturnEmptyWhenNoAdsMatchAdType() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);

      AdQuery query = AdQuery.builder().domainId(domain.getId()).adType(AdType.RENTAL).build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(0, result.items().size());
      assertEquals(0, result.total());
    }

    @Test
    void find_shouldReturnMultipleAdsOfSameType() {
      dataService.createAd(team, user, "Sale Ad 1", AdType.SALE);
      dataService.createAd(team, user, "Sale Ad 2", AdType.SALE);
      dataService.createAd(team, user, "Sale Ad 3", AdType.SALE);
      dataService.createAd(team, user, "Rental Ad", AdType.RENTAL);

      AdQuery query =
          AdQuery.builder()
              .domainId(domain.getId())
              .userId(user.getId())
              .adType(AdType.SALE)
              .build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals(3, result.total());
      assertTrue(result.items().stream().allMatch(ad -> ad.getAdType() == AdType.SALE));
    }
  }
}
