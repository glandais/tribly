package fr.pedalons.service.asset;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.assets.response.DownloadableAsset;
import fr.pedalons.dto.common.asset.AssetDimensionsDto;
import fr.pedalons.dto.common.asset.AssetDto;
import fr.pedalons.dto.common.asset.AssetsDto;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.enums.*;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.infrastructure.filetype.DetectedFileType;
import fr.pedalons.infrastructure.filetype.FileTypeDetector;
import fr.pedalons.infrastructure.imgproxy.ImgProxyService;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.service.asset.response.AssetWithFile;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.TeamService;
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
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AssetService {

  private static final Logger LOG = Logger.getLogger(AssetService.class);

  private static final String ASSETS_PREFIX = "assets";

  /** Matches ::asset{...id="ASSET_ID"...} directives in markdown */
  private static final Pattern ASSET_DIRECTIVE_PATTERN =
      Pattern.compile("::asset\\{[^}]*id=\"([^\"]+)\"[^}]*\\}");

  @Inject AssetRepository assetRepository;

  @Inject ImgProxyService imgProxyService;

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject TeamService teamService;

  @Inject StorageService storageService;

  @Inject FileTypeDetector fileTypeDetector;

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.CREATE)
  public AssetDto createAsset(
      String teamSlug, AssetType assetType, InputStream inputStream, String fileName)
      throws IOException {
    Team team = teamService.getTeam(teamSlug);
    // every member can create asset (ads, ...)
    AssetWithFile assetFile = addAssetStream(team, assetType, null, inputStream, fileName);
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
    User creator = pedalonsContext.getUser();
    long fileId = TSID.Factory.getTsid().toLong();

    // Create temp file for content type detection and possible external writing
    File tempFile = createTempFile(fileId);
    String contentType;

    if (content != null) {
      // Copy content to temp file for content type detection
      Files.copy(content, tempFile.toPath());
      contentType = detectAndValidate(tempFile, fileName, type);

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

      deleteTempFile(tempFile);
    } else {
      // No content - create temp file for external writing. Magika validation cannot run yet
      // because there is no payload; the caller is expected to populate the temp file and then
      // invoke uploadAssetFile(...) which re-runs detectAndValidate against the written bytes.
      contentType = getContentTypeFromFileName(fileName);
      LOG.debugf(
          "Skipping Magika validation: no upload payload yet for fileName=%s assetType=%s"
              + " (caller must invoke uploadAssetFile after writing the temp file)",
          fileName, type);
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
        LOG.warnf(
            e,
            "Failed to extract image dimensions for asset fileId=%s",
            TsidUtils.toString(fileId));
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

    String contentType = detectAndValidate(tempFile, asset.getFileName(), asset.getType());
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

    deleteTempFile(tempFile);
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

  public File createTempFile(long fileId) throws IOException {
    Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "pedalons-assets");
    Files.createDirectories(tempDir);
    return new File(tempDir.toFile(), TsidUtils.toString(fileId));
  }

  private File getTempFile(long fileId) {
    String idString = TsidUtils.toString(fileId);
    return Path.of(System.getProperty("java.io.tmpdir"), "pedalons-assets", idString).toFile();
  }

  public String getAssetKey(Team team, long fileId) {
    String teamId = TsidUtils.toString(team.getId());
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return ASSETS_PREFIX + "/" + teamId + "/" + subPath + "/" + idString;
  }

  /**
   * Uploads a pre-computed temp file to S3. Used by GPX processing pipeline where the file
   * content is prepared before the DB transaction to minimize connection hold time.
   *
   * @return the detected content type
   */
  public String uploadTempFileToS3(Team team, AssetType type, long fileId, String fileName)
      throws IOException {
    File tempFile = getTempFile(fileId);
    String contentType = detectAndValidate(tempFile, fileName, type);
    String key = getAssetKey(team, fileId);
    Map<String, String> metadata =
        Map.of(
            "file-id", TsidUtils.toString(fileId),
            "team-id", TsidUtils.toString(team.getId()),
            "file-name", fileName);
    try (InputStream fis = new FileInputStream(tempFile)) {
      storageService.store(key, fis, contentType, tempFile.length(), metadata);
    }
    deleteTempFile(tempFile);
    return contentType;
  }

  private static void deleteTempFile(File tempFile) {
    if (!tempFile.delete() && tempFile.exists()) {
      LOG.warnf(
          "Failed to delete temp asset file %s — may leak on disk", tempFile.getAbsolutePath());
    }
  }

  /**
   * Persists an asset record for a file already uploaded to S3. Used by GPX processing pipeline
   * after {@link #uploadTempFileToS3}.
   */
  @Transactional
  public Asset persistAsset(
      Team team,
      TeamEntity owner,
      AssetType type,
      long fileId,
      String fileName,
      String contentType) {
    User creator = pedalonsContext.getUser();
    Asset asset = new Asset(creator, team, type, fileId, fileName, contentType);
    asset.setTeamEntity(owner);
    owner.getAssets().add(asset);
    assetRepository.persist(asset);
    return asset;
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

  private String detectAndValidate(File file, String fileName, AssetType type) {
    DetectedFileType detected = fileTypeDetector.detectAndValidate(file, fileName, type);
    return detected.mimeType();
  }

  private String getContentTypeFromFileName(String fileName) {
    String guessed = URLConnection.guessContentTypeFromName(fileName);
    return guessed != null ? guessed : MediaType.APPLICATION_OCTET_STREAM;
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
