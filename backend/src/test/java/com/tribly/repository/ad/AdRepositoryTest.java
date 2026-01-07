package com.tribly.repository.ad;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.ad.Ad;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.AdType;
import com.tribly.enums.Visibility;
import com.tribly.repository.common.TriblyPage;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdRepositoryTest {

  @Inject AdRepository adRepository;
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
  @DisplayName("Ad Type Filter")
  class AdTypeFilter {

    @Test
    void find_shouldReturnAllAdsWithoutAdTypeFilter() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);
      dataService.createAd(team, user, "Rental Ad", AdType.RENTAL);
      dataService.createAd(team, user, "Wanted Ad", AdType.WANTED);

      AdQuery query = AdQuery.builder().userId(user.getId()).build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals(3, result.total());
    }

    @Test
    void find_shouldFilterBySaleAdType() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);
      dataService.createAd(team, user, "Rental Ad", AdType.RENTAL);
      dataService.createAd(team, user, "Wanted Ad", AdType.WANTED);

      AdQuery query = AdQuery.builder().userId(user.getId()).adType(AdType.SALE).build();
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

      AdQuery query = AdQuery.builder().userId(user.getId()).adType(AdType.RENTAL).build();
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

      AdQuery query = AdQuery.builder().userId(user.getId()).adType(AdType.WANTED).build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(1, result.items().size());
      assertEquals("Wanted Ad", result.items().getFirst().getName());
      assertEquals(AdType.WANTED, result.items().getFirst().getAdType());
    }

    @Test
    void find_shouldReturnEmptyWhenNoAdsMatchAdType() {
      dataService.createAd(team, user, "Sale Ad", AdType.SALE);

      AdQuery query = AdQuery.builder().adType(AdType.RENTAL).build();
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

      AdQuery query = AdQuery.builder().userId(user.getId()).adType(AdType.SALE).build();
      TriblyPage<Ad> result = adRepository.find(query);

      assertEquals(3, result.items().size());
      assertEquals(3, result.total());
      assertTrue(result.items().stream().allMatch(ad -> ad.getAdType() == AdType.SALE));
    }
  }
}
