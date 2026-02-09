package com.tribly.service.asset;

import com.tribly.common.TsidUtils;
import com.tribly.domain.asset.Asset;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.assets.response.DownloadableAsset;
import com.tribly.dto.common.asset.AssetDimensionsDto;
import com.tribly.dto.common.asset.AssetDto;
import com.tribly.dto.common.asset.AssetsDto;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.enums.*;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.infrastructure.imgproxy.ImgProxyService;
import com.tribly.infrastructure.storage.StorageService;
import com.tribly.repository.asset.AssetRepository;
import com.tribly.service.asset.response.AssetWithFile;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import com.tribly.service.team.TeamService;
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AssetService {

  private static final String ASSETS_PREFIX = "assets";

  /** Matches ::asset{...id="ASSET_ID"...} directives in markdown */
  private static final Pattern ASSET_DIRECTIVE_PATTERN =
      Pattern.compile("::asset\\{[^}]*id=\"([^\"]+)\"[^}]*\\}");

  @Inject AssetRepository assetRepository;

  @Inject ImgProxyService imgProxyService;

  @Inject TriblyQueryContext triblyContext;

  @Inject TeamService teamService;

  @Inject StorageService storageService;

  private final Tika tika = new Tika();

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.CREATE)
  public AssetDto createAsset(String teamSlug, InputStream inputStream, String fileName)
      throws IOException {
    Team team = teamService.getTeam(teamSlug);
    // every member can create asset (ads, ...)
    AssetWithFile assetFile = addAssetStream(team, AssetType.IMAGE, null, inputStream, fileName);
    return map(assetFile.asset());
  }

  /**
   * Creates an asset with a temp file for external libraries to write to. After writing, call
   * {@link #uploadAssetFile(Asset)} to upload the file to S3.
   */
  public AssetWithFile addAsset(TeamEntity teamEntity, AssetType type, String fileName)
      throws IOException {
    return addAssetStream(teamEntity.getTeam(), type, teamEntity, null, fileName);
  }

  @Transactional
  protected AssetWithFile addAssetStream(
      Team team,
      AssetType type,
      @Nullable TeamEntity teamEntity,
      @Nullable InputStream content,
      String fileName)
      throws IOException {
    User creator = triblyContext.getUser();
    long fileId = TSID.Factory.getTsid().toLong();

    // Create temp file for content type detection and possible external writing
    File tempFile = createTempFile(fileId);
    String contentType;

    if (content != null) {
      // Copy content to temp file for content type detection
      Files.copy(content, tempFile.toPath());
      contentType = getContentType(tempFile, fileName);

      // Upload to S3 with metadata
      String key = getAssetKey(team, fileId);
      Map<String, String> metadata =
          Map.of(
              "file-id", TsidUtils.toString(fileId),
              "team-id", TsidUtils.toString(team.getId()),
              "file-name", fileName);
      try (InputStream fis = new FileInputStream(tempFile)) {
        storageService.store(key, fis, contentType, tempFile.length(), metadata);
      }

      // Clean up temp file after upload
      tempFile.delete();
    } else {
      // No content - create temp file for external writing
      contentType = getContentTypeFromFileName(fileName);
    }

    Asset asset = new Asset(creator, team, type, fileId, fileName, contentType);
    asset.setTeamEntity(teamEntity);

    if (content != null && contentType.startsWith("image/")) {
      try (InputStream is =
          imgProxyService.getPhotoContent("image/jpeg", getAssetKey(team, fileId), 4096, 4096)) {
        BufferedImage read = ImageIO.read(is);
        asset.setWidth(read.getWidth());
        asset.setHeight(read.getHeight());
      } catch (Exception e) {
        // imgproxy may fail for invalid images - continue without dimensions
      }
    }

    assetRepository.persistAndFlush(asset);

    return new AssetWithFile(asset, tempFile);
  }

  /**
   * Uploads an asset file from temp location to S3. Call this after external libraries have
   * written to the temp file returned by {@link #addAsset(TeamEntity, AssetType, String)}.
   */
  public void uploadAssetFile(Asset asset) throws IOException {
    File tempFile = getTempFile(asset.getFileId());
    if (!tempFile.exists()) {
      throw new IOException("Temp file not found for asset: " + asset.getId());
    }

    String contentType = getContentType(tempFile, asset.getFileName());
    asset.setContentType(contentType);

    String key = getAssetKey(asset.getTeam(), asset.getFileId());
    Map<String, String> metadata =
        Map.of(
            "asset-id", TsidUtils.toString(asset.getId()),
            "file-id", TsidUtils.toString(asset.getFileId()),
            "team-id", TsidUtils.toString(asset.getTeam().getId()),
            "file-name", asset.getFileName());
    try (InputStream fis = new FileInputStream(tempFile)) {
      storageService.store(key, fis, contentType, tempFile.length(), metadata);
    }

    // Clean up temp file
    tempFile.delete();
  }

  /**
   * Gets the temp file for an asset (for external libraries to write to).
   */
  public File getAssetFile(Asset asset) {
    return getTempFile(asset.getFileId());
  }

  /**
   * Retrieves asset content as an InputStream from S3.
   */
  public InputStream getAssetContent(Asset asset) {
    String key = getAssetKey(asset.getTeam(), asset.getFileId());
    return storageService.retrieve(key);
  }

  private File createTempFile(long fileId) throws IOException {
    Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "tribly-assets");
    Files.createDirectories(tempDir);
    return new File(tempDir.toFile(), TsidUtils.toString(fileId));
  }

  private File getTempFile(long fileId) {
    String idString = TsidUtils.toString(fileId);
    return Path.of(System.getProperty("java.io.tmpdir"), "tribly-assets", idString).toFile();
  }

  private String getAssetKey(Team team, long fileId) {
    String teamId = TsidUtils.toString(team.getId());
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return ASSETS_PREFIX + "/" + teamId + "/" + subPath + "/" + idString;
  }

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.READ)
  public DownloadableAsset getDownloadableAsset(String teamSlug, Long assetId) {
    Asset asset = getAsset(assetId);
    InputStream content = getAssetContent(asset);
    return new DownloadableAsset(content, asset.getContentType());
  }

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.READ)
  public Response getImage(String teamSlug, Long assetId, int size, String accept) {
    Asset asset = getAsset(assetId);
    String key = getAssetKey(asset.getTeam(), asset.getFileId());
    return Response.fromResponse(imgProxyService.getPhoto(accept, key, size, size)).build();
  }

  private String getContentType(File file, String fileName) {
    Metadata metadata = new Metadata();
    metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);
    String contentTypeOverride = getContentTypeOverride(fileName);
    if (contentTypeOverride != null) {
      metadata.set(TikaCoreProperties.CONTENT_TYPE_USER_OVERRIDE, contentTypeOverride);
    }
    if (!file.exists()) {
      try {
        return tika.detect(null, metadata);
      } catch (IOException e) {
        return MediaType.APPLICATION_OCTET_STREAM;
      }
    } else {
      try (TikaInputStream fis = TikaInputStream.get(file.toPath(), metadata)) {
        return tika.detect(fis, metadata);
      } catch (IOException e) {
        return MediaType.APPLICATION_OCTET_STREAM;
      }
    }
  }

  private String getContentTypeFromFileName(String fileName) {
    String guessed = URLConnection.guessContentTypeFromName(fileName);
    return guessed != null ? guessed : MediaType.APPLICATION_OCTET_STREAM;
  }

  @Nullable
  private String getContentTypeOverride(String fileName) {
    String fileNameLowerCase = fileName.toLowerCase();
    if (fileNameLowerCase.endsWith(".png")) {
      return "image/png";
    }
    if (fileNameLowerCase.endsWith(".gif")) {
      return "image/gif";
    }
    if (fileNameLowerCase.endsWith(".jpg")) {
      return "image/jpeg";
    }
    if (fileNameLowerCase.endsWith(".gpx")) {
      return "application/gpx+xml";
    }
    if (fileNameLowerCase.endsWith(".fit")) {
      return "application/vnd.ant.fit";
    }
    return null;
  }

  private Asset getAsset(Long id) {
    return assetRepository
        .findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException(EntityType.ASSET, id));
  }

  @Transactional
  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.DELETE)
  protected void deleteAsset(String teamSlug, Long id) {
    Asset asset =
        assetRepository
            .findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException(EntityType.ASSET, id));
    assetRepository.delete(asset);
  }

  protected AssetDto map(Asset asset) {
    Visibility visibility;
    if (asset.getTeamEntity() == null) {
      visibility = Visibility.TEAM;
    } else {
      visibility = asset.getTeamEntity().getVisibility();
    }
    String teamSlug = asset.getTeam().getSlug();
    AssetDimensionsDto assetDimensionsDto;
    String imageUrl;
    if (asset.getContentType().startsWith("image/")) {
      imageUrl = getImageUrl(asset);
    } else {
      imageUrl = null;
    }
    if (asset.getWidth() != null && asset.getHeight() != null) {
      assetDimensionsDto = new AssetDimensionsDto(asset.getWidth(), asset.getHeight());
    } else {
      assetDimensionsDto = null;
    }
    String url =
        "/api/download/"
            + visibility.name().toLowerCase()
            + "/assets/"
            + teamSlug
            + "/"
            + TsidUtils.toString(asset.getId())
            + "/"
            + asset.getFileName();
    return new AssetDto(
        TsidUtils.toString(asset.getId()),
        asset.getFileName(),
        asset.getContentType(),
        url,
        imageUrl,
        assetDimensionsDto);
  }

  public String getImageUrl(Asset asset) {
    String teamSlug = asset.getTeam().getSlug();
    Visibility visibility;
    if (asset.getTeamEntity() == null) {
      visibility = Visibility.TEAM;
    } else {
      visibility = asset.getTeamEntity().getVisibility();
    }
    return "/api/download/"
        + visibility.name().toLowerCase()
        + "/images/"
        + teamSlug
        + "/"
        + TsidUtils.toString(asset.getId())
        + "/{size}";
  }

  /**
   * Called to create DTO in MediaDto.from
   * @param teamEntity
   * @return
   */
  public AssetsDto getAssetsDto(TeamEntity teamEntity) {
    Map<AssetType, List<Asset>> assets =
        teamEntity.getAssets().stream().collect(Collectors.groupingBy(Asset::getType));
    AssetDto thumbnailLight =
        coalesce(
            getAssetDto(assets, AssetType.RIDE_THUMBNAIL_LIGHT),
            getAssetDto(assets, AssetType.TRIP_THUMBNAIL_LIGHT),
            getAssetDto(assets, AssetType.ROUTE_THUMBNAIL_LIGHT));
    AssetDto thumbnailDark =
        coalesce(
            getAssetDto(assets, AssetType.RIDE_THUMBNAIL_DARK),
            getAssetDto(assets, AssetType.TRIP_THUMBNAIL_DARK),
            getAssetDto(assets, AssetType.ROUTE_THUMBNAIL_DARK));
    return new AssetsDto(
        getAssetDto(assets, AssetType.LOGO),
        getAssetDtoList(assets, AssetType.IMAGE),
        getAssetDtoList(assets, AssetType.ATTACHMENT),
        getAssetDto(assets, AssetType.ROUTE_ORIGINAL_GPX),
        getAssetDto(assets, AssetType.ROUTE_FILTERED_GPX),
        getAssetDto(assets, AssetType.ROUTE_FIT),
        thumbnailLight,
        thumbnailDark);
  }

  @SafeVarargs
  private static <T> @Nullable T coalesce(@Nullable T... values) {
    for (T value : values) {
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private @Nullable AssetDto getAssetDto(Map<AssetType, List<Asset>> assets, AssetType assetType) {
    return Optional.ofNullable(assets.get(assetType))
        .filter(l -> !l.isEmpty())
        .map(List::getFirst)
        .map(this::map)
        .orElse(null);
  }

  private List<AssetDto> getAssetDtoList(Map<AssetType, List<Asset>> assets, AssetType assetType) {
    List<Asset> assetsForType = assets.get(assetType);
    if (assetsForType == null) {
      return List.of();
    }
    return assetsForType.stream()
        .sorted(Comparator.comparing(Asset::getSortOrder))
        .map(this::map)
        .toList();
  }

  /**
   * Called in service having MediaDto
   * @param teamEntity
   * @param mediaRequest
   */
  public void updateAssets(TeamEntity teamEntity, MediaDto mediaRequest) {
    teamEntity.setMarkdown(mediaRequest.markdown());
    AssetsDto assets = mediaRequest.assets();

    // Parse markdown to find which image assets are actually referenced
    Set<String> referencedAssetIds = extractAssetIdsFromMarkdown(mediaRequest.markdown());

    // Only keep images that are referenced in the markdown
    List<AssetDto> usedImages =
        assets.images().stream().filter(img -> referencedAssetIds.contains(img.id())).toList();

    Set<Asset> unmodifiable =
        teamEntity.getAssets().stream()
            .filter(s -> s.getType().isSystem())
            .collect(Collectors.toSet());
    // orphanRemoval deletes removed assets from DB; AssetRemoveListener handles S3 cleanup
    teamEntity.getAssets().clear();
    int order = 0;
    for (Asset asset : unmodifiable) {
      asset.setSortOrder(order++);
      teamEntity.getAssets().add(asset);
    }

    order = addAssetToEntity(order, teamEntity, AssetType.LOGO, assets.logo());
    order = addAssetsToEntity(order, teamEntity, AssetType.IMAGE, usedImages);
    addAssetsToEntity(order, teamEntity, AssetType.ATTACHMENT, assets.attachments());
  }

  private Set<String> extractAssetIdsFromMarkdown(String markdown) {
    Set<String> assetIds = new HashSet<>();
    if (markdown.isEmpty()) {
      return assetIds;
    }
    Matcher matcher = ASSET_DIRECTIVE_PATTERN.matcher(markdown);
    while (matcher.find()) {
      assetIds.add(matcher.group(1));
    }
    return assetIds;
  }

  private int addAssetsToEntity(
      int order, TeamEntity teamEntity, AssetType assetType, List<AssetDto> assetRequests) {
    for (AssetDto assetDto : assetRequests) {
      addAssetToEntity(order, teamEntity, assetType, assetDto);
      order++;
    }
    return order;
  }

  private int addAssetToEntity(
      int order, TeamEntity teamEntity, AssetType assetType, @Nullable AssetDto assetRequest) {
    if (assetRequest == null) {
      return order;
    }
    Long assetId = TsidUtils.toLong(assetRequest.id());
    Asset asset = getAsset(assetId);
    if (asset.getTeam().getId().equals(teamEntity.getTeam().getId())
        && (asset.getTeamEntity() == null
            || asset.getTeamEntity().getId().equals(teamEntity.getId()))) {
      asset.setTeamEntity(teamEntity);
      asset.setType(assetType);
      assetRepository.persist(asset);
      teamEntity.getAssets().add(asset);
    }
    return order + 1;
  }
}
