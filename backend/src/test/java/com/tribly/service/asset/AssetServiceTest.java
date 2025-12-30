package com.tribly.service.asset;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.post.Post;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.response.AssetDto;
import com.tribly.dto.common.response.AssetsDto;
import com.tribly.enums.AssetType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.response.AssetWithFile;
import com.tribly.service.asset.response.DownloadableAsset;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AssetServiceTest {

  @Inject AssetService assetService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Team team;
  private Team privateTeam;
  private User admin;
  private User organizer;
  private User member;
  private User nonMember;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    privateTeam = dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(admin, team, TeamRole.ADMIN);
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
    dataService.addUserToTeam(admin, privateTeam, TeamRole.ADMIN);
  }

  @AfterEach
  void cleanup() {
    // Clean up any created files
  }

  private InputStream getExampleGpxStream() {
    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("example.gpx");
    assertNotNull(resourceAsStream, "example.gpx not found in test resources");
    return resourceAsStream;
  }

  @Nested
  class CreateAsset {

    @Test
    void shouldCreateAssetForOrganizer() throws Exception {
      InputStream content =
          new ByteArrayInputStream("test content".getBytes(StandardCharsets.UTF_8));

      AssetDto result =
          assetService.createAsset("test-team", organizer.getId(), content, "test.txt");

      assertNotNull(result);
      assertNotNull(result.id());
      assertEquals("test.txt", result.fileName());
      assertTrue(result.url().contains("/api/download/"));
    }

    @Test
    void shouldThrowForNonOrganizer() {
      InputStream content = new ByteArrayInputStream("test".getBytes());

      assertThrows(
          BusinessException.class,
          () -> assetService.createAsset("test-team", member.getId(), content, "test.txt"));
    }

    @Test
    void shouldThrowForNonexistentTeam() {
      InputStream content = new ByteArrayInputStream("test".getBytes());

      assertThrows(
          BusinessException.class,
          () -> assetService.createAsset("nonexistent", admin.getId(), content, "test.txt"));
    }
  }

  @Nested
  class AddAssetStream {

    @Test
    void shouldCreateAssetWithContent() throws Exception {
      InputStream content =
          new ByteArrayInputStream("file content".getBytes(StandardCharsets.UTF_8));

      AssetWithFile result =
          assetService.addAssetStream(admin, team, AssetType.IMAGE, null, content, "image.png");

      assertNotNull(result);
      assertNotNull(result.asset());
      assertNotNull(result.file());
      assertEquals("image.png", result.asset().getFileName());
      assertTrue(result.file().exists());
    }

    @Test
    void shouldCreateAssetWithoutContent() throws Exception {
      AssetWithFile result =
          assetService.addAssetStream(admin, team, AssetType.IMAGE, null, null, "placeholder.png");

      assertNotNull(result);
      assertNotNull(result.asset());
      assertEquals("placeholder.png", result.asset().getFileName());
      // File is created but empty
      assertFalse(result.file().exists());
    }
  }

  @Nested
  class GetAssetFile {

    @Test
    void shouldReturnCorrectFilePath() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");

      File file = assetService.getAssetFile(asset);

      assertNotNull(file);
      String assetIdString = TsidUtils.toString(asset.getId());
      assertTrue(file.getPath().contains(assetIdString.substring(0, 4)));
      assertTrue(file.getName().equals(assetIdString));
    }
  }

  @Nested
  class GetDownloadableAsset {

    @Test
    void shouldReturnAssetForPublicTeam() throws Exception {
      InputStream content = getExampleGpxStream();
      AssetWithFile assetWithFile =
          assetService.addAssetStream(
              admin, team, AssetType.ROUTE_ORIGINAL_GPX, null, content, "route.gpx");
      String assetId = TsidUtils.toString(assetWithFile.asset().getId());

      DownloadableAsset result = assetService.getDownloadableAsset(assetId, null);

      assertNotNull(result);
      assertNotNull(result.file());
      assertEquals("application/gpx+xml", result.contentType());
    }

    @Test
    void shouldDetectContentTypeForPng() throws Exception {
      // Create a minimal PNG file (1x1 transparent pixel)
      byte[] pngBytes = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
      InputStream content = new ByteArrayInputStream(pngBytes);
      AssetWithFile assetWithFile =
          assetService.addAssetStream(admin, team, AssetType.IMAGE, null, content, "image.png");
      String assetId = TsidUtils.toString(assetWithFile.asset().getId());

      DownloadableAsset result = assetService.getDownloadableAsset(assetId, null);

      assertNotNull(result);
      assertEquals("image/png", result.contentType());
    }
  }

  @Nested
  class GetAssetById {

    @Test
    void shouldReturnAssetById() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");

      Asset result = assetService.getAsset(asset.getId());

      assertNotNull(result);
      assertEquals(asset.getId(), result.getId());
    }

    @Test
    void shouldThrowForNonexistentAsset() {
      assertThrows(BusinessException.class, () -> assetService.getAsset(999999L));
    }
  }

  @Nested
  class GetAssetWithSecurity {

    @Test
    void shouldReturnAssetFromPublicTeamWithoutTeamEntity() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");

      Asset result = assetService.getAsset(asset.getId(), null);

      assertNotNull(result);
    }

    @Test
    void shouldRequireOrganizerForPrivateTeamWithoutTeamEntity() {
      Asset asset = dataService.createAsset(privateTeam, admin, AssetType.IMAGE, "test.png");

      // Organizer of private team can access
      Asset result = assetService.getAsset(asset.getId(), admin.getId());
      assertNotNull(result);

      // Non-member cannot access
      assertThrows(
          BusinessException.class, () -> assetService.getAsset(asset.getId(), nonMember.getId()));
    }

    @Test
    void shouldCheckTeamEntityVisibility() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "post-image.png");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      // Public post asset accessible to anyone
      Asset result = assetService.getAsset(asset.getId(), null);
      assertNotNull(result);
    }

    @Test
    void shouldThrowForInaccessibleTeamEntity() {
      Post post = dataService.createPost(team, admin, "Team Post", Instant.now(), Visibility.TEAM);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "post-image.png");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      // Non-member cannot access team-visibility post asset
      assertThrows(
          BusinessException.class, () -> assetService.getAsset(asset.getId(), nonMember.getId()));
    }
  }

  @Nested
  class DeleteAsset {

    @Test
    void shouldDeleteExistingFile() throws Exception {
      InputStream content = new ByteArrayInputStream("to delete".getBytes());
      AssetWithFile assetWithFile =
          assetService.addAssetStream(admin, team, AssetType.IMAGE, null, content, "delete-me.txt");
      assertTrue(assetWithFile.file().exists());

      assetService.deleteAsset(assetWithFile.asset().getId());

      assertFalse(assetWithFile.file().exists());
    }

    @Test
    void shouldHandleNonExistentFile() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "no-file.txt");

      // Should not throw even if file doesn't exist
      assertDoesNotThrow(() -> assetService.deleteAsset(asset.getId()));
    }

    @Test
    void shouldThrowForNonexistentAsset() {
      assertThrows(BusinessException.class, () -> assetService.deleteAsset(999999L));
    }
  }

  @Nested
  class MapAsset {

    @Test
    void shouldMapAssetWithoutTeamEntity() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");

      AssetDto result = assetService.map(asset);

      assertNotNull(result);
      assertEquals(TsidUtils.toString(asset.getId()), result.id());
      assertEquals("test.png", result.fileName());
      assertTrue(result.url().contains("/public/")); // Team is public
    }

    @Test
    void shouldMapAssetWithTeamEntity() {
      Post post = dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.TEAM);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "post-image.png");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      AssetDto result = assetService.map(asset);

      assertNotNull(result);
      assertTrue(result.url().contains("/team/")); // Post is team-visibility
    }
  }

  @Nested
  class GetAssetsDto {

    @Test
    void shouldGroupAssetsByType() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset logo = dataService.createAsset(team, admin, AssetType.LOGO, "logo.png");
      logo.setTeamEntity(post);
      dataService.updateAsset(logo);
      post.getAssets().add(logo);
      Asset image1 = dataService.createAsset(team, admin, AssetType.IMAGE, "image1.png");
      image1.setTeamEntity(post);
      dataService.updateAsset(image1);
      post.getAssets().add(image1);
      Asset image2 = dataService.createAsset(team, admin, AssetType.IMAGE, "image2.png");
      image2.setTeamEntity(post);
      dataService.updateAsset(image2);
      post.getAssets().add(image2);

      AssetsDto result = assetService.getAssetsDto(post);

      assertNotNull(result);
      assertNotNull(result.logo());
      assertEquals(2, result.images().size());
      assertTrue(result.videos().isEmpty());
      assertTrue(result.attachments().isEmpty());
    }

    @Test
    void shouldReturnEmptyListsForNoAssets() {
      Post post =
          dataService.createPost(team, admin, "Empty Post", Instant.now(), Visibility.PUBLIC);

      AssetsDto result = assetService.getAssetsDto(post);

      assertNotNull(result);
      assertNull(result.logo());
      assertTrue(result.images().isEmpty());
    }
  }

  @Nested
  class UpdateAssets {

    @Test
    void shouldUpdateAssetsFromDto() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "new-image.png");
      String imageId = TsidUtils.toString(image.getId());

      AssetsDto assetsDto =
          new AssetsDto(
              null, // no logo
              List.of(new AssetDto(imageId, "new-image.png", null)),
              null,
              null,
              null,
              null,
              null,
              null);

      assetService.updateAssets(post, assetsDto);

      assertTrue(post.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())));
    }

    @Test
    void shouldPreserveSystemAssets() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset systemAsset =
          dataService.createAsset(team, admin, AssetType.ROUTE_ORIGINAL_GPX, "route.gpx");
      systemAsset.setTeamEntity(post);
      dataService.updateAsset(systemAsset);
      post.getAssets().add(systemAsset);

      // Update with null assets should preserve system assets
      assetService.updateAssets(post, null);

      assertTrue(post.getAssets().stream().anyMatch(a -> a.getType().isSystem()));
    }

    @Test
    void shouldHandleNullAssets() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);

      // Should not throw
      assertDoesNotThrow(() -> assetService.updateAssets(post, null));
    }

    @Test
    void shouldNotAddAssetFromDifferentTeam() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset foreignAsset =
          dataService.createAsset(privateTeam, admin, AssetType.IMAGE, "foreign.png");
      String foreignId = TsidUtils.toString(foreignAsset.getId());

      AssetsDto assetsDto =
          new AssetsDto(
              new AssetDto(foreignId, "foreign.png", null),
              null,
              null,
              null,
              null,
              null,
              null,
              null);

      assetService.updateAssets(post, assetsDto);

      // Foreign asset should not be added
      assertFalse(post.getAssets().stream().anyMatch(a -> a.getId().equals(foreignAsset.getId())));
    }

    @Test
    void shouldAddAssetAlreadyAssignedToSameEntity() {
      // Branch: same team + asset.teamEntity == same entity → add
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "existing-image.png");
      image.setTeamEntity(post);
      dataService.updateAsset(image);
      String imageId = TsidUtils.toString(image.getId());

      AssetsDto assetsDto =
          new AssetsDto(
              null,
              List.of(new AssetDto(imageId, "existing-image.png", null)),
              null,
              null,
              null,
              null,
              null,
              null);

      assetService.updateAssets(post, assetsDto);

      assertTrue(post.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())));
    }

    @Test
    void shouldNotAddAssetAssignedToDifferentEntity() {
      // Branch: same team + asset.teamEntity == different entity → don't add
      Post post1 = dataService.createPost(team, admin, "Post 1", Instant.now(), Visibility.PUBLIC);
      Post post2 = dataService.createPost(team, admin, "Post 2", Instant.now(), Visibility.PUBLIC);
      Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "other-post-image.png");
      image.setTeamEntity(post1);
      dataService.updateAsset(image);
      String imageId = TsidUtils.toString(image.getId());

      AssetsDto assetsDto =
          new AssetsDto(
              null,
              List.of(new AssetDto(imageId, "other-post-image.png", null)),
              null,
              null,
              null,
              null,
              null,
              null);

      assetService.updateAssets(post2, assetsDto);

      // Asset assigned to post1 should not be added to post2
      assertFalse(post2.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())));
    }
  }
}
