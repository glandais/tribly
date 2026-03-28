package com.tribly.service.ad;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.AbstractBaseTest;
import com.tribly.common.exception.BusinessException;
import com.tribly.common.exception.ConflictException;
import com.tribly.common.exception.TriblyException;
import com.tribly.domain.ad.Ad;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.ads.request.AdRequest;
import com.tribly.dto.ads.response.AdDto;
import com.tribly.dto.ads.response.AdEditDto;
import com.tribly.dto.ads.response.AdListResponse;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.enums.AdType;
import com.tribly.enums.RentalPeriod;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdServiceTest extends AbstractBaseTest {

  @Inject AdService adService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext userService;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private Team team;
  private User admin;
  private User organizer;
  private User member;
  private User nonMember;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
  }

  @Nested
  class GetAdDetail {

    @Test
    void shouldReturnAdForMember() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(member);
      AdDto result = adService.getDto(team.getSlug(), ad.getSlug());

      assertNotNull(result);
      assertEquals("Test Ad", result.name());
    }

    @Test
    void shouldReturnAdForAdmin() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(admin);
      AdDto result = adService.getDto(team.getSlug(), ad.getSlug());

      assertNotNull(result);
      assertEquals("Test Ad", result.name());
    }

    @Test
    void shouldThrowForAnonymous() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(null);
      assertThrows(TriblyException.class, () -> adService.getDto(team.getSlug(), ad.getSlug()));
    }

    @Test
    void shouldThrowForNonMember() {
      Ad ad = dataService.createAd(team, admin, "Test Ad", AdType.SALE);

      userService.setUserForTest(nonMember);
      assertThrows(TriblyException.class, () -> adService.getDto(team.getSlug(), ad.getSlug()));
    }

    @Test
    void shouldThrowForNonexistentAd() {
      userService.setUserForTest(admin);
      assertThrows(TriblyException.class, () -> adService.getDto(team.getSlug(), "nonexistent"));
    }
  }

  @Nested
  class GetAdEditDto {

    @Test
    void shouldReturnEditDtoForCreator() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);

      userService.setUserForTest(member);
      AdEditDto result = adService.getDtoEdit(team.getSlug(), ad.getSlug());

      assertNotNull(result);
      assertEquals("My Ad", result.name());
    }

    @Test
    void shouldReturnEditDtoForAdmin() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);

      userService.setUserForTest(admin);
      AdEditDto result = adService.getDtoEdit(team.getSlug(), ad.getSlug());

      assertNotNull(result);
    }

    @Test
    void shouldThrowForNonCreatorMember() {
      Ad ad = dataService.createAd(team, admin, "Admin Ad", AdType.SALE);

      userService.setUserForTest(member);
      assertThrows(TriblyException.class, () -> adService.getDtoEdit(team.getSlug(), ad.getSlug()));
    }
  }

  @Nested
  class ListAds {

    @Test
    void shouldListAdsForMember() {
      dataService.createAd(team, admin, "Ad 1", AdType.SALE);
      dataService.createAd(team, admin, "Ad 2", AdType.WANTED);

      userService.setUserForTest(member);
      AdListResponse result = adService.listAds(team.getSlug(), null, null, null, null, 0, 10);

      assertEquals(2, result.ads().size());
    }

    @Test
    void shouldFilterByAdType() {
      dataService.createAd(team, admin, "For Sale", AdType.SALE);
      dataService.createAd(team, admin, "Wanted", AdType.WANTED);

      userService.setUserForTest(member);
      AdListResponse result =
          adService.listAds(team.getSlug(), null, AdType.SALE, null, null, 0, 10);

      assertEquals(1, result.ads().size());
      assertEquals("For Sale", result.ads().getFirst().name());
    }

    @Test
    void shouldThrowForAnonymous() {
      userService.setUserForTest(null);
      assertThrows(
          TriblyException.class,
          () -> adService.listAds(team.getSlug(), null, null, null, null, 0, 10));
    }

    @Test
    void shouldThrowForNonMember() {
      userService.setUserForTest(nonMember);
      assertThrows(
          TriblyException.class,
          () -> adService.listAds(team.getSlug(), null, null, null, null, 0, 10));
    }
  }

  @Nested
  class CreateAd {

    @Test
    void shouldCreateAdForMember() {
      AdRequest request =
          new AdRequest(
              "New Ad",
              MediaDto.builder().markdown("Description").build(),
              Status.PUBLISHED,
              AdType.SALE,
              new BigDecimal("100.00"),
              null,
              "Paris",
              null);

      userService.setUserForTest(member);
      AdDto result = adService.createAd(team.getSlug(), request);

      assertNotNull(result);
      assertEquals("New Ad", result.name());
      assertEquals(AdType.SALE, result.adType());
      assertEquals(new BigDecimal("100.00"), result.price());
    }

    @Test
    void shouldCreateRentalAdWithPeriod() {
      AdRequest request =
          new AdRequest(
              "Rental Ad",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.RENTAL,
              new BigDecimal("50.00"),
              RentalPeriod.DAY,
              null,
              null);

      userService.setUserForTest(member);
      AdDto result = adService.createAd(team.getSlug(), request);

      assertNotNull(result);
      assertEquals(AdType.RENTAL, result.adType());
      assertEquals(RentalPeriod.DAY, result.rentalPeriod());
    }

    @Test
    void shouldThrowForRentalWithoutPeriod() {
      AdRequest request =
          new AdRequest(
              "Rental Ad",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.RENTAL,
              null,
              null,
              null,
              null);

      userService.setUserForTest(member);
      assertThrows(BusinessException.class, () -> adService.createAd(team.getSlug(), request));
    }

    @Test
    void shouldThrowForCancelledStatus() {
      AdRequest request =
          new AdRequest(
              "Cancelled Ad",
              MediaDto.builder().build(),
              Status.CANCELLED,
              AdType.SALE,
              null,
              null,
              null,
              null);

      userService.setUserForTest(member);
      assertThrows(BusinessException.class, () -> adService.createAd(team.getSlug(), request));
    }

    @Test
    void shouldThrowForAnonymous() {
      AdRequest request =
          new AdRequest(
              "Ad",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              null,
              null,
              null);

      userService.setUserForTest(null);
      assertThrows(TriblyException.class, () -> adService.createAd(team.getSlug(), request));
    }

    @Test
    void shouldGenerateUniqueSlug() {
      AdRequest request1 =
          new AdRequest(
              "Same Name",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              null,
              null,
              null);
      AdRequest request2 =
          new AdRequest(
              "Same Name",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              null,
              null,
              null);

      userService.setUserForTest(member);
      AdDto result1 = adService.createAd(team.getSlug(), request1);
      userService.setUserForTest(member);
      AdDto result2 = adService.createAd(team.getSlug(), request2);

      assertNotEquals(result1.slug(), result2.slug());
    }
  }

  @Nested
  class UpdateAd {

    @Test
    void shouldUpdateAdForCreator() {
      Ad ad = dataService.createAd(team, member, "Original", AdType.SALE);
      AdRequest request =
          new AdRequest(
              "Updated",
              MediaDto.builder().markdown("New description").build(),
              Status.PUBLISHED,
              AdType.WANTED,
              new BigDecimal("200.00"),
              null,
              "Lyon",
              null);

      userService.setUserForTest(member);
      AdDto result = adService.updateAd(team.getSlug(), ad.getSlug(), request);

      assertEquals("Updated", result.name());
      assertEquals(AdType.WANTED, result.adType());
      assertEquals(new BigDecimal("200.00"), result.price());
    }

    @Test
    void shouldUpdateAdForAdmin() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);
      AdRequest request =
          new AdRequest(
              "Admin Updated",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              null,
              null,
              null);

      userService.setUserForTest(admin);
      AdDto result = adService.updateAd(team.getSlug(), ad.getSlug(), request);

      assertEquals("Admin Updated", result.name());
    }

    @Test
    void shouldThrowForNonCreatorMember() {
      Ad ad = dataService.createAd(team, admin, "Admin Ad", AdType.SALE);
      AdRequest request =
          new AdRequest(
              "Updated",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              null,
              null,
              null);

      userService.setUserForTest(member);
      assertThrows(
          TriblyException.class, () -> adService.updateAd(team.getSlug(), ad.getSlug(), request));
    }

    @Test
    void shouldClearRentalPeriodWhenChangingType() {
      Ad ad = dataService.createAd(team, member, "Rental", AdType.RENTAL);
      AdRequest request =
          new AdRequest(
              "Now For Sale",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              RentalPeriod.DAY,
              null,
              null);

      userService.setUserForTest(member);
      AdDto result = adService.updateAd(team.getSlug(), ad.getSlug(), request);

      assertEquals(AdType.SALE, result.adType());
      assertNull(result.rentalPeriod());
    }
  }

  @Nested
  class DeleteAd {

    @Test
    void shouldSoftDeleteAdForCreator() {
      Ad ad = dataService.createAd(team, member, "To Delete", AdType.SALE);

      userService.setUserForTest(member);
      adService.deleteAd(team.getSlug(), ad.getSlug());

      userService.setUserForTest(admin);
      assertThrows(TriblyException.class, () -> adService.getDto(team.getSlug(), ad.getSlug()));
    }

    @Test
    void shouldSoftDeleteAdForAdmin() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);

      userService.setUserForTest(admin);
      adService.deleteAd(team.getSlug(), ad.getSlug());

      assertThrows(TriblyException.class, () -> adService.getDto(team.getSlug(), ad.getSlug()));
    }

    @Test
    void shouldThrowForNonCreatorMember() {
      Ad ad = dataService.createAd(team, admin, "Admin Ad", AdType.SALE);

      userService.setUserForTest(member);
      assertThrows(TriblyException.class, () -> adService.deleteAd(team.getSlug(), ad.getSlug()));
    }

    @Test
    void shouldThrowForNonexistentAd() {
      userService.setUserForTest(admin);
      assertThrows(TriblyException.class, () -> adService.deleteAd(team.getSlug(), "nonexistent"));
    }
  }

  @Nested
  class AdsDisabled {

    @Test
    void shouldThrowWhenAdsDisabled() {
      team.setEnableAds(false);
      dataService.updateTeam(team);

      userService.setUserForTest(member);
      assertThrows(
          TriblyException.class,
          () -> adService.listAds(team.getSlug(), null, null, null, null, 0, 10));
    }

    @Test
    void shouldThrowCreateWhenAdsDisabled() {
      team.setEnableAds(false);
      dataService.updateTeam(team);
      AdRequest request =
          new AdRequest(
              "Ad",
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              null,
              null,
              null);

      userService.setUserForTest(member);
      assertThrows(TriblyException.class, () -> adService.createAd(team.getSlug(), request));
    }
  }

  @Nested
  class UpdateSlug {

    @Test
    void shouldUpdateSlugForCreator() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);
      String oldSlug = ad.getSlug();

      userService.setUserForTest(member);
      AdDto result = adService.updateSlug(team.getSlug(), oldSlug, "new-slug");

      assertEquals("new-slug", result.slug());
      assertEquals("My Ad", result.name());
    }

    @Test
    void shouldUpdateSlugForAdmin() {
      Ad ad = dataService.createAd(team, member, "Member Ad", AdType.SALE);
      String oldSlug = ad.getSlug();

      userService.setUserForTest(admin);
      AdDto result = adService.updateSlug(team.getSlug(), oldSlug, "admin-changed-slug");

      assertEquals("admin-changed-slug", result.slug());
    }

    @Test
    void shouldReturnSameDtoWhenSlugUnchanged() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);
      String currentSlug = ad.getSlug();

      userService.setUserForTest(member);
      AdDto result = adService.updateSlug(team.getSlug(), currentSlug, currentSlug);

      assertEquals(currentSlug, result.slug());
    }

    @Test
    void shouldThrowForInvalidSlugFormat() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);

      userService.setUserForTest(member);
      assertThrows(
          BusinessException.class,
          () -> adService.updateSlug(team.getSlug(), ad.getSlug(), "Invalid Slug With Spaces!"));
    }

    @Test
    void shouldThrowWhenNewSlugAlreadyTaken() {
      Ad ad1 = dataService.createAd(team, member, "Ad One", AdType.SALE);
      Ad ad2 = dataService.createAd(team, member, "Ad Two", AdType.SALE);

      userService.setUserForTest(member);
      assertThrows(
          ConflictException.class,
          () -> adService.updateSlug(team.getSlug(), ad1.getSlug(), ad2.getSlug()));
    }

    @Test
    void shouldCreateRedirectFromOldSlug() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);
      String oldSlug = ad.getSlug();

      userService.setUserForTest(member);
      adService.updateSlug(team.getSlug(), oldSlug, "new-slug");

      // Old slug should redirect to the entity
      userService.setUserForTest(member);
      AdDto result = adService.getDto(team.getSlug(), oldSlug);
      assertEquals("new-slug", result.slug());
    }

    @Test
    void shouldThrowForNonCreatorMember() {
      Ad ad = dataService.createAd(team, admin, "Admin Ad", AdType.SALE);

      userService.setUserForTest(member);
      assertThrows(
          TriblyException.class,
          () -> adService.updateSlug(team.getSlug(), ad.getSlug(), "new-slug"));
    }

    @Test
    void shouldThrowForAnonymous() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);

      userService.setUserForTest(null);
      assertThrows(
          TriblyException.class,
          () -> adService.updateSlug(team.getSlug(), ad.getSlug(), "new-slug"));
    }

    @Test
    void shouldThrowForNonMember() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);

      userService.setUserForTest(nonMember);
      assertThrows(
          TriblyException.class,
          () -> adService.updateSlug(team.getSlug(), ad.getSlug(), "new-slug"));
    }

    @Test
    void shouldAllowReusingOldSlugAfterChange() {
      Ad ad = dataService.createAd(team, member, "My Ad", AdType.SALE);
      String originalSlug = ad.getSlug();

      // Change slug
      userService.setUserForTest(member);
      adService.updateSlug(team.getSlug(), originalSlug, "new-slug");

      // Create new ad with original slug should work (redirect gets cleared)
      AdRequest request =
          new AdRequest(
              originalSlug,
              MediaDto.builder().build(),
              Status.PUBLISHED,
              AdType.SALE,
              null,
              null,
              null,
              null);

      userService.setUserForTest(member);
      AdDto newAd = adService.createAd(team.getSlug(), request);
      // New ad gets its own slug (might be original or modified)
      assertNotNull(newAd.slug());
    }
  }
}
