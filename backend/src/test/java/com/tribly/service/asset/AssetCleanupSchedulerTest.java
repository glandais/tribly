package com.tribly.service.asset;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.AssetType;
import com.tribly.enums.Visibility;
import com.tribly.repository.asset.AssetRepository;
import com.tribly.service.security.DomainResolver;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AssetCleanupSchedulerTest {

  @Inject AssetCleanupScheduler assetCleanupScheduler;
  @Inject AssetRepository assetRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Team team;
  private User admin;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    Domain domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
  }

  @Test
  void shouldDeleteOrphanedAssetsOlderThanOneDay() {
    Asset orphaned = dataService.createAsset(team, admin, AssetType.IMAGE, "orphan.png");
    dataService.backdateAssetUpdatedAt(orphaned, Instant.now().minus(2, ChronoUnit.DAYS));

    assetCleanupScheduler.cleanupOrphanedAssets();

    assertFalse(
        assetRepository.findByIdOptional(orphaned.getId()).isPresent(),
        "Orphaned asset older than 1 day should be deleted");
  }

  @Test
  void shouldNotDeleteOrphanedAssetsNewerThanOneDay() {
    Asset recent = dataService.createAsset(team, admin, AssetType.IMAGE, "recent.png");
    // updatedAt is now (just created) — should NOT be deleted

    assetCleanupScheduler.cleanupOrphanedAssets();

    assertTrue(
        assetRepository.findByIdOptional(recent.getId()).isPresent(),
        "Recently uploaded orphaned asset should be kept");
  }

  @Test
  void shouldNotDeleteAssetsAttachedToEntity() {
    Post post =
        dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
    Asset attached = dataService.createAsset(team, admin, AssetType.IMAGE, "attached.png");
    attached.setTeamEntity(post);
    dataService.updateAsset(attached);
    dataService.backdateAssetUpdatedAt(attached, Instant.now().minus(2, ChronoUnit.DAYS));

    assetCleanupScheduler.cleanupOrphanedAssets();

    assertTrue(
        assetRepository.findByIdOptional(attached.getId()).isPresent(),
        "Asset attached to entity should not be deleted regardless of age");
  }

  @Test
  void shouldHandleNoOrphanedAssets() {
    assertDoesNotThrow(() -> assetCleanupScheduler.cleanupOrphanedAssets());
  }

  @Test
  void shouldOnlyDeleteOldOrphansAndKeepEverythingElse() {
    // Old orphan → delete
    Asset oldOrphan = dataService.createAsset(team, admin, AssetType.IMAGE, "old-orphan.png");
    dataService.backdateAssetUpdatedAt(oldOrphan, Instant.now().minus(3, ChronoUnit.DAYS));

    // Recent orphan → keep
    Asset recentOrphan = dataService.createAsset(team, admin, AssetType.IMAGE, "recent-orphan.png");

    // Old attached → keep
    Post post =
        dataService.createPost(team, admin, "Post", Instant.now(), Visibility.PUBLIC);
    Asset oldAttached = dataService.createAsset(team, admin, AssetType.IMAGE, "old-attached.png");
    oldAttached.setTeamEntity(post);
    dataService.updateAsset(oldAttached);
    dataService.backdateAssetUpdatedAt(oldAttached, Instant.now().minus(3, ChronoUnit.DAYS));

    assetCleanupScheduler.cleanupOrphanedAssets();

    assertFalse(assetRepository.findByIdOptional(oldOrphan.getId()).isPresent(),
        "Old orphan should be deleted");
    assertTrue(assetRepository.findByIdOptional(recentOrphan.getId()).isPresent(),
        "Recent orphan should be kept");
    assertTrue(assetRepository.findByIdOptional(oldAttached.getId()).isPresent(),
        "Old attached asset should be kept");
  }
}
