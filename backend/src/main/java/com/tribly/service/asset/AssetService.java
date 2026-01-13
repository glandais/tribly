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
import com.tribly.enums.*;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.infrastructure.imgproxy.ImgProxyService;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AssetService {

  @Inject AssetRepository assetRepository;

  @Inject ImgProxyService imgProxyService;

  @Inject TriblyQueryContext triblyContext;

  @Inject TeamService teamService;

  @ConfigProperty(name = "storage.path")
  String storagePath = "/tmp";

  private final Tika tika = new Tika();

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.CREATE)
  public AssetDto createAsset(String teamSlug, InputStream inputStream, String fileName)
      throws IOException {
    Team team = teamService.getTeam(teamSlug);
    // every member can create asset (ads, ...)
    AssetWithFile assetFile = addAssetStream(team, AssetType.IMAGE, null, inputStream, fileName);
    return map(assetFile.asset());
  }

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

    File file = getAssetFile(team, fileId);
    Files.createDirectories(file.getParentFile().toPath());
    if (content != null) {
      try (FileOutputStream fos = new FileOutputStream(file)) {
        IOUtils.copy(content, fos);
      }

      Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rw-r--r--");
      Files.setPosixFilePermissions(file.toPath(), ownerWritable);
    }
    String contentType = getContentType(file, fileName);

    Asset asset = new Asset(creator, team, type, fileId, fileName, contentType);
    asset.setTeamEntity(teamEntity);

    if (content != null && contentType.startsWith("image/")) {
      try (InputStream is =
          imgProxyService.getPhotoContent(
              "image/jpeg", getRelativeAssetFile(team, fileId), 4096, 4096)) {
        BufferedImage read = ImageIO.read(is);
        asset.setWidth(read.getWidth());
        asset.setHeight(read.getHeight());
      } catch (Exception e) {
        // imgproxy may fail for invalid images - continue without dimensions
      }
    }

    assetRepository.persistAndFlush(asset);

    return new AssetWithFile(asset, file);
  }

  public File getAssetFile(Asset asset) {
    return getAssetFile(asset.getTeam(), asset.getFileId());
  }

  private File getAssetFile(Team team, long fileId) {
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    Path assetDirectory = Path.of(storagePath, TsidUtils.toString(team.getId()), subPath);
    return new File(assetDirectory.toFile(), idString);
  }

  private String getRelativeAssetFile(Team team, long fileId) {
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return TsidUtils.toString(team.getId()) + "/" + subPath + "/" + idString;
  }

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.READ)
  public DownloadableAsset getDownloadableAsset(String teamSlug, Long assetId) {
    Asset asset = getAsset(assetId);
    File file = getAssetFile(asset);
    return new DownloadableAsset(file, asset.getContentType());
  }

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.READ)
  public Response getImage(String teamSlug, Long assetId, int size, String accept) {
    Asset asset = getAsset(assetId);
    String relativeAssetFile = getRelativeAssetFile(asset.getTeam(), asset.getFileId());
    return Response.fromResponse(imgProxyService.getPhoto(accept, relativeAssetFile, size, size))
        .build();
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

  @CheckAccess(entityType = EntityType.ASSET, action = ActionType.DELETE)
  protected void deleteAsset(String teamSlug, Long id) {
    // FIXME auto delete files on cascade ?
    Asset asset =
        assetRepository
            .findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException(EntityType.ASSET, id));
    File file = getAssetFile(asset);
    if (file.exists()) {
      file.delete();
    }
    // FIXME delete if parent directory is empty
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
    return new AssetsDto(
        getAssetDto(assets, AssetType.LOGO),
        getAssetDtoList(assets, AssetType.IMAGE),
        getAssetDtoList(assets, AssetType.ATTACHMENT),
        getAssetDto(assets, AssetType.ROUTE_ORIGINAL_GPX),
        getAssetDto(assets, AssetType.ROUTE_FILTERED_GPX),
        getAssetDto(assets, AssetType.ROUTE_FIT),
        getAssetDto(assets, AssetType.ROUTE_THUMBNAIL));
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
   * @param assets
   */
  public void updateAssets(TeamEntity teamEntity, AssetsDto assets) {
    Set<Asset> unmodifiable =
        teamEntity.getAssets().stream()
            .filter(s -> s.getType().isSystem())
            .collect(Collectors.toSet());
    teamEntity.getAssets().clear();
    int order = 0;
    for (Asset asset : unmodifiable) {
      asset.setSortOrder(order++);
      teamEntity.getAssets().add(asset);
    }

    order = addAssetToEntity(order, teamEntity, AssetType.LOGO, assets.logo());
    order = addAssetsToEntity(order, teamEntity, AssetType.IMAGE, assets.images());
    addAssetsToEntity(order, teamEntity, AssetType.ATTACHMENT, assets.attachments());
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
