package fr.pedalons.service.asset;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.assets.response.DownloadableAsset;
import fr.pedalons.dto.common.asset.AssetDto;
import fr.pedalons.dto.common.asset.AssetsDto;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.enums.AssetType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.service.asset.response.AssetWithFile;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AssetServiceTest extends AbstractBaseTest {

  @Inject AssetService assetService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext queryContext;
  @Inject DomainResolver domainResolver;
  @Inject StorageService storageService;
  @Inject AssetRepository assetRepository;
  @Inject PostRepository postRepository;

  private Domain domain;
  private Team team;
  private Team privateTeam;
  private User admin;
  private User organizer;
  private User member;
  private User member2;
  private User nonMember;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    admin = dataService.createUser("admin@example.com", "Admin");
    team = dataService.createTeam(admin, "Test Team", "test-team", Visibility.PUBLIC);
    privateTeam = dataService.createTeam(admin, "Private Team", "private-team", Visibility.TEAM);
    organizer = dataService.createUser("organizer@example.com", "Organizer");
    member = dataService.createUser("member@example.com", "Member");
    member2 = dataService.createUser("member2@example.com", "Member2");
    nonMember = dataService.createUser("nonmember@example.com", "NonMember");
    dataService.addUserToTeam(organizer, team, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member, team, TeamRole.MEMBER);
    dataService.addUserToTeam(organizer, privateTeam, TeamRole.ORGANIZER);
    dataService.addUserToTeam(member2, privateTeam, TeamRole.MEMBER);
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

  private InputStream getTestImageStream() {
    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("image.png");
    assertNotNull(resourceAsStream, "image.png not found in test resources");
    return resourceAsStream;
  }

  private String getAssetKey(Team t, long fileId) {
    String teamId = TsidUtils.toString(t.getId());
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return "assets/" + teamId + "/" + subPath + "/" + idString;
  }

  @Nested
  class CreateAsset {

    @Test
    void shouldCreateAssetForOrganizer() throws Exception {
      InputStream content =
          new ByteArrayInputStream("test content".getBytes(StandardCharsets.UTF_8));

      queryContext.setUserForTest(organizer);
      AssetDto result = assetService.createAsset(team.getSlug(), content, "test.txt");

      assertNotNull(result);
      assertNotNull(result.id());
      assertEquals("test.txt", result.fileName());
      assertTrue(result.url().contains("/api/download/"));
    }

    @Test
    void shouldCreateAssetForNonOrganizer() throws IOException {
      InputStream content = new ByteArrayInputStream("test".getBytes());

      queryContext.setUserForTest(member);
      AssetDto result = assetService.createAsset(team.getSlug(), content, "test.txt");

      assertNotNull(result);
      assertNotNull(result.id());
      assertEquals("test.txt", result.fileName());
      assertTrue(result.url().contains("/api/download/"));
    }
  }

  @Nested
  class AddAssetStream {

    @Test
    void shouldCreateAssetWithContent() throws Exception {
      InputStream content =
          new ByteArrayInputStream("file content".getBytes(StandardCharsets.UTF_8));

      queryContext.setUserForTest(member);
      AssetWithFile result =
          assetService.addAssetStream(team, AssetType.IMAGE, null, content, "image.png");

      assertNotNull(result);
      assertNotNull(result.asset());
      assertNotNull(result.file());
      assertEquals("image.png", result.asset().getFileName());
      // Temp file is deleted after S3 upload when content is provided
      assertFalse(result.file().exists());
    }

    @Test
    void shouldCreateAssetWithoutContent() throws Exception {
      queryContext.setUserForTest(member);
      AssetWithFile result =
          assetService.addAssetStream(team, AssetType.IMAGE, null, null, "placeholder.png");

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
      String fileIdString = TsidUtils.toString(asset.getFileId());
      assertTrue(file.getPath().contains(fileIdString.substring(0, 4)));
      assertEquals(fileIdString, file.getName());
    }
  }

  @Nested
  class GetDownloadableAsset {

    @Test
    void shouldReturnAssetForPublicTeam() throws Exception {
      // Create a public post to attach the asset to
      Post publicPost =
          dataService.createPost(team, admin, "Public Post", Instant.now(), Visibility.PUBLIC);
      InputStream content = getExampleGpxStream();
      queryContext.setUserForTest(member);
      AssetWithFile assetWithFile =
          assetService.addAssetStream(
              team, AssetType.ROUTE_ORIGINAL_GPX, publicPost, content, "route.gpx");
      Long assetId = assetWithFile.asset().getId();

      queryContext.setUserForTest(null);
      DownloadableAsset result = assetService.getDownloadableAsset(team.getSlug(), assetId);

      assertNotNull(result);
      assertNotNull(result.content());
      assertEquals("application/gpx+xml", result.contentType());
    }

    @Test
    void shouldDetectContentTypeForPng() throws Exception {
      // Create a minimal PNG file (1x1 transparent pixel)
      byte[] pngBytes = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
      InputStream content = new ByteArrayInputStream(pngBytes);
      queryContext.setUserForTest(organizer);
      AssetWithFile assetWithFile =
          assetService.addAssetStream(team, AssetType.IMAGE, null, content, "image.png");
      Long assetId = assetWithFile.asset().getId();

      queryContext.setUserForTest(organizer);
      DownloadableAsset result = assetService.getDownloadableAsset(team.getSlug(), assetId);

      assertNotNull(result);
      assertEquals("image/png", result.contentType());
    }
  }

  @Nested
  class GetAssetWithSecurity {

    @Test
    void shouldReturnAssetFromPublicTeamWithoutTeamEntity() {
      Asset asset = dataService.createAsset(team, member, AssetType.IMAGE, "test.png");

      queryContext.setUserForTest(member);
      DownloadableAsset result = assetService.getDownloadableAsset(team.getSlug(), asset.getId());

      assertNotNull(result);
    }

    @Test
    void shouldRequireOwnerForPrivateTeamWithoutTeamEntity() {
      Asset asset = dataService.createAsset(privateTeam, member2, AssetType.IMAGE, "test.png");

      // Organizer of private team can access
      queryContext.setUserForTest(member2);
      DownloadableAsset result =
          assetService.getDownloadableAsset(privateTeam.getSlug(), asset.getId());
      assertNotNull(result);

      // Non-member cannot access
      queryContext.setUserForTest(nonMember);
      assertThrows(
          PedalonsException.class,
          () -> assetService.getDownloadableAsset(privateTeam.getSlug(), asset.getId()));
    }

    @Test
    void shouldCheckTeamEntityVisibility() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "post-image.png");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      // Public post asset accessible to anyone
      queryContext.setUserForTest(null);
      DownloadableAsset result = assetService.getDownloadableAsset(team.getSlug(), asset.getId());
      assertNotNull(result);
    }

    @Test
    void shouldThrowForInaccessibleTeamEntity() {
      Post post = dataService.createPost(team, admin, "Team Post", Instant.now(), Visibility.TEAM);
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "post-image.png");
      asset.setTeamEntity(post);
      dataService.updateAsset(asset);

      // Non-member cannot access team-visibility post asset
      queryContext.setUserForTest(nonMember);
      assertThrows(
          PedalonsException.class,
          () -> assetService.getDownloadableAsset(team.getSlug(), asset.getId()));
    }
  }

  @Nested
  class DeleteAsset {

    @Test
    void shouldDeleteFromS3() throws Exception {
      InputStream content = new ByteArrayInputStream("to delete".getBytes());
      queryContext.setUserForTest(organizer);
      AssetWithFile assetWithFile =
          assetService.addAssetStream(team, AssetType.IMAGE, null, content, "delete-me.txt");
      // Temp file is deleted after S3 upload
      assertFalse(assetWithFile.file().exists());

      queryContext.setUserForTest(organizer);
      // Should not throw - deletes from S3
      assertDoesNotThrow(
          () -> assetService.deleteAsset(team.getSlug(), assetWithFile.asset().getId()));
    }

    @Test
    void shouldHandleNonExistentFile() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "no-file.txt");

      // Should not throw even if file doesn't exist
      queryContext.setUserForTest(admin);
      assertDoesNotThrow(() -> assetService.deleteAsset(team.getSlug(), asset.getId()));
    }

    @Test
    void shouldThrowForNonexistentAsset() {
      queryContext.setUserForTest(organizer);
      assertThrows(
          PedalonsException.class, () -> assetService.deleteAsset(team.getSlug(), 999999L));
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
      assertTrue(result.url().contains("/team/")); // No teamEntity -> authent
      assertTrue(result.imageUrl().contains("/team/")); // No teamEntity -> authent
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

    @Test
    void shouldIncludeImageUrlForImageAssets() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");

      AssetDto result = assetService.map(asset);

      assertNotNull(result);
      assertNotNull(result.imageUrl(), "imageUrl should be set for image assets");
      assertTrue(result.imageUrl().contains("/images/"));
      assertTrue(result.imageUrl().contains("/{size}"));
    }

    @Test
    void shouldNotIncludeImageUrlForNonImageAssets() throws Exception {
      InputStream content = getExampleGpxStream();
      queryContext.setUserForTest(member);
      AssetWithFile assetWithFile =
          assetService.addAssetStream(
              team, AssetType.ROUTE_ORIGINAL_GPX, null, content, "route.gpx");

      AssetDto result = assetService.map(assetWithFile.asset());

      assertNotNull(result);
      assertNull(result.imageUrl(), "imageUrl should be null for non-image assets");
    }

    @Test
    void shouldIncludeDimensionsWhenAvailable() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");
      asset.setWidth(800);
      asset.setHeight(600);
      dataService.updateAsset(asset);

      AssetDto result = assetService.map(asset);

      assertNotNull(result);
      assertNotNull(
          result.imageDimensions(), "dimensions should be set when width/height available");
      assertEquals(800, result.imageDimensions().width());
      assertEquals(600, result.imageDimensions().height());
    }

    @Test
    void shouldNotIncludeDimensionsWhenNotAvailable() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");
      // No width/height set

      AssetDto result = assetService.map(asset);

      assertNotNull(result);
      assertNull(result.imageDimensions(), "dimensions should be null when not available");
    }

    @Test
    void shouldIncludeContentType() {
      Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "test.png");

      AssetDto result = assetService.map(asset);

      assertNotNull(result);
      assertNotNull(result.contentType());
    }
  }

  @Nested
  class ImageDimensionExtraction {

    @Test
    void shouldExtractDimensionsFromValidImage() throws Exception {
      InputStream imageContent = getTestImageStream();

      queryContext.setUserForTest(member);
      AssetWithFile result =
          assetService.addAssetStream(team, AssetType.IMAGE, null, imageContent, "photo.png");

      assertNotNull(result);
      assertNotNull(result.asset().getWidth(), "Width should be extracted from valid image");
      assertNotNull(result.asset().getHeight(), "Height should be extracted from valid image");
      assertTrue(result.asset().getWidth() > 0);
      assertTrue(result.asset().getHeight() > 0);
    }

    @Test
    void shouldHandleInvalidImageGracefully() throws Exception {
      // Invalid image content (not a real image)
      InputStream invalidContent =
          new ByteArrayInputStream("not an image".getBytes(StandardCharsets.UTF_8));

      queryContext.setUserForTest(member);
      AssetWithFile result =
          assetService.addAssetStream(team, AssetType.IMAGE, null, invalidContent, "fake.png");

      assertNotNull(result);
      // Should not fail, just have no dimensions
      assertNull(result.asset().getWidth());
      assertNull(result.asset().getHeight());
    }

    @Test
    void shouldNotExtractDimensionsForNonImageFiles() throws Exception {
      InputStream content = getExampleGpxStream();

      queryContext.setUserForTest(member);
      AssetWithFile result =
          assetService.addAssetStream(
              team, AssetType.ROUTE_ORIGINAL_GPX, null, content, "route.gpx");

      assertNotNull(result);
      assertNull(result.asset().getWidth());
      assertNull(result.asset().getHeight());
    }
  }

  @Nested
  class GetImage {

    @Test
    void shouldReturnResizedImage() throws Exception {
      // Upload a valid image first
      InputStream imageContent = getTestImageStream();

      queryContext.setUserForTest(organizer);
      AssetWithFile assetWithFile =
          assetService.addAssetStream(team, AssetType.IMAGE, null, imageContent, "photo.png");
      Long assetId = assetWithFile.asset().getId();

      // Request resized image
      queryContext.setUserForTest(organizer);
      jakarta.ws.rs.core.Response response =
          assetService.getImage(team.getSlug(), assetId, 200, "image/jpeg");

      assertNotNull(response);
      assertEquals(200, response.getStatus());
    }

    @Test
    void shouldThrowForNonexistentAsset() {
      queryContext.setUserForTest(null);
      assertThrows(
          PedalonsException.class,
          () -> assetService.getImage(team.getSlug(), 999999999L, 200, "image/jpeg"));
    }

    @Test
    void shouldRespectSecurityForPrivateTeamAsset() throws Exception {
      // Upload image to private team
      InputStream imageContent = getTestImageStream();
      queryContext.setUserForTest(admin);
      AssetWithFile assetWithFile =
          assetService.addAssetStream(
              privateTeam, AssetType.IMAGE, null, imageContent, "private-photo.png");
      Long assetId = assetWithFile.asset().getId();

      // Non-member should not be able to access
      queryContext.setUserForTest(nonMember);
      assertThrows(
          PedalonsException.class,
          () -> assetService.getImage(privateTeam.getSlug(), assetId, 200, "image/jpeg"));
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
          AssetsDto.builder().images(List.of(AssetDto.builder().id(imageId).build())).build();
      String markdown = "Some text\n::asset{id=\"" + imageId + "\" size=\"medium\"}\nMore text";
      MediaDto mediaDto = new MediaDto(markdown, assetsDto);

      assetService.updateAssets(post, mediaDto);

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
      MediaDto mediaDto = new MediaDto("markdown", AssetsDto.builder().build());
      assetService.updateAssets(post, mediaDto);

      assertTrue(post.getAssets().stream().anyMatch(a -> a.getType().isSystem()));
    }

    @Test
    void shouldHandleNullAssets() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      MediaDto mediaDto = new MediaDto("markdown", AssetsDto.builder().build());

      // Should not throw
      assertDoesNotThrow(() -> assetService.updateAssets(post, mediaDto));
    }

    @Test
    void shouldNotAddAssetFromDifferentTeam() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset foreignAsset =
          dataService.createAsset(privateTeam, admin, AssetType.IMAGE, "foreign.png");
      String foreignId = TsidUtils.toString(foreignAsset.getId());

      AssetsDto assetsDto =
          AssetsDto.builder().logo(AssetDto.builder().id(foreignId).build()).build();
      MediaDto mediaDto = new MediaDto("markdown", assetsDto);

      assetService.updateAssets(post, mediaDto);

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
          AssetsDto.builder().images(List.of(AssetDto.builder().id(imageId).build())).build();
      String markdown = "::asset{id=\"" + imageId + "\"}";
      MediaDto mediaDto = new MediaDto(markdown, assetsDto);

      assetService.updateAssets(post, mediaDto);

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
          AssetsDto.builder().images(List.of(AssetDto.builder().id(imageId).build())).build();
      String markdown = "::asset{id=\"" + imageId + "\"}";
      MediaDto mediaDto = new MediaDto(markdown, assetsDto);

      assetService.updateAssets(post2, mediaDto);

      // Asset assigned to post1 should not be added to post2
      assertFalse(post2.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())));
    }

    @Test
    void shouldRemoveImagesNotReferencedInMarkdown() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset usedImage = dataService.createAsset(team, admin, AssetType.IMAGE, "used.png");
      Asset unusedImage = dataService.createAsset(team, admin, AssetType.IMAGE, "unused.png");
      String usedId = TsidUtils.toString(usedImage.getId());
      String unusedId = TsidUtils.toString(unusedImage.getId());

      // Both images in DTO, but only one referenced in markdown
      AssetsDto assetsDto =
          AssetsDto.builder()
              .images(
                  List.of(
                      AssetDto.builder().id(usedId).build(),
                      AssetDto.builder().id(unusedId).build()))
              .build();
      String markdown = "Some text ::asset{id=\"" + usedId + "\"} more text";
      MediaDto mediaDto = new MediaDto(markdown, assetsDto);

      assetService.updateAssets(post, mediaDto);

      assertTrue(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(usedImage.getId())),
          "Image referenced in markdown should be kept");
      assertFalse(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(unusedImage.getId())),
          "Image not referenced in markdown should be removed");
    }

    @Test
    void shouldRemoveOldAssetFromCollection() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);

      Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "to-remove.png");
      image.setTeamEntity(post);
      dataService.updateAsset(image);
      post.getAssets().add(image);

      // Update without the image → removed from collection
      MediaDto mediaDto = new MediaDto("no images here", AssetsDto.builder().build());

      assetService.updateAssets(post, mediaDto);

      assertFalse(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())),
          "Image should be removed from entity");
    }

    @Test
    void shouldKeepReferencedImageInCollection() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "kept.png");
      String imageId = TsidUtils.toString(image.getId());

      image.setTeamEntity(post);
      dataService.updateAsset(image);
      post.getAssets().add(image);

      AssetsDto assetsDto =
          AssetsDto.builder().images(List.of(AssetDto.builder().id(imageId).build())).build();
      String markdown = "::asset{id=\"" + imageId + "\"}";
      MediaDto mediaDto = new MediaDto(markdown, assetsDto);

      assetService.updateAssets(post, mediaDto);

      assertTrue(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())),
          "Image should still be on entity");
    }

    @Test
    void shouldRemoveImageInDtoButNotInMarkdown() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "orphaned.png");
      String imageId = TsidUtils.toString(image.getId());

      image.setTeamEntity(post);
      dataService.updateAsset(image);
      post.getAssets().add(image);

      // Image in DTO but NOT in markdown
      AssetsDto assetsDto =
          AssetsDto.builder().images(List.of(AssetDto.builder().id(imageId).build())).build();
      MediaDto mediaDto = new MediaDto("text without any asset directives", assetsDto);

      assetService.updateAssets(post, mediaDto);

      assertFalse(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())),
          "Image not in markdown should be removed from entity");
    }

    @Test
    void shouldHandleEmptyMarkdown() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "img.png");
      String imageId = TsidUtils.toString(image.getId());

      AssetsDto assetsDto =
          AssetsDto.builder().images(List.of(AssetDto.builder().id(imageId).build())).build();
      MediaDto mediaDto = new MediaDto("", assetsDto);

      assetService.updateAssets(post, mediaDto);

      assertFalse(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())),
          "No images should be kept when markdown is empty");
    }

    @Test
    void shouldHandleMultipleAssetDirectivesInMarkdown() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset img1 = dataService.createAsset(team, admin, AssetType.IMAGE, "img1.png");
      Asset img2 = dataService.createAsset(team, admin, AssetType.IMAGE, "img2.png");
      Asset img3 = dataService.createAsset(team, admin, AssetType.IMAGE, "img3.png");
      String id1 = TsidUtils.toString(img1.getId());
      String id2 = TsidUtils.toString(img2.getId());
      String id3 = TsidUtils.toString(img3.getId());

      AssetsDto assetsDto =
          AssetsDto.builder()
              .images(
                  List.of(
                      AssetDto.builder().id(id1).build(),
                      AssetDto.builder().id(id2).build(),
                      AssetDto.builder().id(id3).build()))
              .build();
      String markdown =
          "Intro\n::asset{id=\""
              + id1
              + "\" size=\"full\"}\nMiddle\n::asset{id=\""
              + id3
              + "\" alt=\"photo\"}\nEnd";
      MediaDto mediaDto = new MediaDto(markdown, assetsDto);

      assetService.updateAssets(post, mediaDto);

      assertTrue(post.getAssets().stream().anyMatch(a -> a.getId().equals(img1.getId())));
      assertFalse(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(img2.getId())),
          "img2 not in markdown should be removed");
      assertTrue(post.getAssets().stream().anyMatch(a -> a.getId().equals(img3.getId())));
    }

    @Test
    void shouldPreserveLogoAndAttachmentsRegardlessOfMarkdown() {
      Post post =
          dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
      Asset logo = dataService.createAsset(team, admin, AssetType.IMAGE, "logo.png");
      Asset attachment = dataService.createAsset(team, admin, AssetType.IMAGE, "doc.pdf");
      String logoId = TsidUtils.toString(logo.getId());
      String attachmentId = TsidUtils.toString(attachment.getId());

      // Logo and attachments are not filtered by markdown references
      AssetsDto assetsDto =
          AssetsDto.builder()
              .logo(AssetDto.builder().id(logoId).build())
              .attachments(List.of(AssetDto.builder().id(attachmentId).build()))
              .build();
      MediaDto mediaDto = new MediaDto("no directives here", assetsDto);

      assetService.updateAssets(post, mediaDto);

      assertTrue(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(logo.getId())),
          "Logo should be kept regardless of markdown");
      assertTrue(
          post.getAssets().stream().anyMatch(a -> a.getId().equals(attachment.getId())),
          "Attachment should be kept regardless of markdown");
    }
  }

  @Test
  @Transactional
  void shouldDeleteS3WhenAssetIsDeleted() {
    Asset asset = dataService.createAsset(team, admin, AssetType.IMAGE, "file.png");
    String s3Key = getAssetKey(team, asset.getFileId());
    assertTrue(storageService.exists(s3Key), "S3 file should exist before delete");

    Asset managed = assetRepository.findById(asset.getId());
    assetRepository.delete(managed);
    assetRepository.flush();

    assertFalse(storageService.exists(s3Key), "S3 file should be deleted by @PreRemove listener");
  }

  @Test
  @Transactional
  void shouldDeleteS3WhenOrphanRemoved() {
    Post post = dataService.createPost(team, admin, "Test Post", Instant.now(), Visibility.PUBLIC);
    Asset image = dataService.createAsset(team, admin, AssetType.IMAGE, "orphan.png");
    String s3Key = getAssetKey(team, image.getFileId());

    post.getAssets().add(image);
    dataService.updatePost(post);

    postRepository.flush();
    assetRepository.flush();

    // Re-fetch managed post to have a proper managed collection
    Post managed = dataService.getPost(post.getId());
    assertTrue(
        managed.getAssets().stream().anyMatch(a -> a.getId().equals(image.getId())),
        "Image should be in collection before removal");

    // Remove from collection → orphanRemoval triggers @PreRemove
    managed.getAssets().removeIf(a -> a.getId().equals(image.getId()));
    dataService.updatePost(managed);

    postRepository.flush();
    assetRepository.flush();

    assertFalse(
        storageService.exists(s3Key), "S3 file should be deleted by orphanRemoval + @PreRemove");
  }
}
