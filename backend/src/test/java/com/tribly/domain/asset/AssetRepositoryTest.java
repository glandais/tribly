package com.tribly.domain.asset;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.asset.repository.AssetRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.AssetType;
import com.tribly.enums.Visibility;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AssetRepository}.
 */
@QuarkusTest
class AssetRepositoryTest {

  @Inject AssetRepository assetRepository;
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
  @DisplayName("Basic CRUD Operations")
  class BasicCrud {

    @Test
    @DisplayName("Should persist and find asset by id")
    void persist_shouldPersistAndFindById() {
      Asset asset = dataService.createAsset(team, user, AssetType.IMAGE, "photo.jpg");

      Asset found = assetRepository.findById(asset.getId());

      assertNotNull(found);
      assertEquals(asset.getId(), found.getId());
      assertEquals("photo.jpg", found.getFileName());
      assertEquals(AssetType.IMAGE, found.getType());
    }

    @Test
    @DisplayName("Should return null for non-existent id")
    void findById_nonExistent_shouldReturnNull() {
      Asset found = assetRepository.findById(999999L);

      assertNull(found);
    }

    @Test
    @DisplayName("Should list all assets")
    void listAll_shouldReturnAllAssets() {
      dataService.createAsset(team, user, AssetType.IMAGE, "photo1.jpg");
      dataService.createAsset(team, user, AssetType.IMAGE, "photo2.jpg");
      dataService.createAsset(team, user, AssetType.ATTACHMENT, "file.pdf");

      List<Asset> assets = assetRepository.listAll();

      assertEquals(3, assets.size());
    }

    @Test
    @DisplayName("Should count assets")
    void count_shouldReturnCount() {
      dataService.createAsset(team, user, AssetType.IMAGE, "photo1.jpg");
      dataService.createAsset(team, user, AssetType.IMAGE, "photo2.jpg");

      long count = assetRepository.count();

      assertEquals(2, count);
    }
  }

  @Nested
  @DisplayName("Asset Properties")
  class AssetProperties {

    @Test
    @DisplayName("Should preserve team association")
    void createAsset_shouldPreserveTeam() {
      Asset asset = dataService.createAsset(team, user, AssetType.IMAGE, "photo.jpg");

      Asset found = assetRepository.findById(asset.getId());

      assertEquals(team.getId(), found.getTeam().getId());
    }

    @Test
    @DisplayName("Should preserve asset type")
    void createAsset_shouldPreserveType() {
      Asset image = dataService.createAsset(team, user, AssetType.IMAGE, "photo.jpg");
      Asset attachment = dataService.createAsset(team, user, AssetType.ATTACHMENT, "doc.pdf");
      Asset logo = dataService.createAsset(team, user, AssetType.LOGO, "logo.png");

      assertEquals(AssetType.IMAGE, assetRepository.findById(image.getId()).getType());
      assertEquals(AssetType.ATTACHMENT, assetRepository.findById(attachment.getId()).getType());
      assertEquals(AssetType.LOGO, assetRepository.findById(logo.getId()).getType());
    }

    @Test
    @DisplayName("Should have default sortOrder of 0")
    void createAsset_shouldHaveDefaultSortOrder() {
      Asset asset = dataService.createAsset(team, user, AssetType.IMAGE, "photo.jpg");

      assertEquals(0, asset.getSortOrder());
    }

    @Test
    @DisplayName("Should have null teamEntity by default")
    void createAsset_shouldHaveNullTeamEntity() {
      Asset asset = dataService.createAsset(team, user, AssetType.IMAGE, "photo.jpg");

      assertNull(asset.getTeamEntity());
    }
  }

  @Nested
  @DisplayName("Soft Delete")
  class SoftDelete {

    @Test
    @DisplayName("Should soft delete asset")
    void deleteAsset_shouldSetDeletedTrue() {
      Asset asset = dataService.createAsset(team, user, AssetType.IMAGE, "photo.jpg");

      dataService.deleteAsset(asset);

      Asset found = assetRepository.findById(asset.getId());
      assertNotNull(found);
      assertTrue(found.isDeleted());
    }

    @Test
    @DisplayName("Soft deleted asset should still be findable by id")
    void deletedAsset_shouldStillBeFindableById() {
      Asset asset = dataService.createAsset(team, user, AssetType.IMAGE, "photo.jpg");
      dataService.deleteAsset(asset);

      Asset found = assetRepository.findById(asset.getId());

      assertNotNull(found);
      assertEquals(asset.getId(), found.getId());
    }
  }

  @Nested
  @DisplayName("Multiple Teams")
  class MultipleTeams {

    @Test
    @DisplayName("Assets from different teams should be independent")
    void assets_fromDifferentTeams_shouldBeIndependent() {
      Team team2 = dataService.createTeam(user, "Team 2", "team-2", Visibility.PUBLIC);

      dataService.createAsset(team, user, AssetType.IMAGE, "team1-photo.jpg");
      dataService.createAsset(team, user, AssetType.IMAGE, "team1-photo2.jpg");
      dataService.createAsset(team2, user, AssetType.IMAGE, "team2-photo.jpg");

      List<Asset> allAssets = assetRepository.listAll();

      assertEquals(3, allAssets.size());
    }
  }
}
