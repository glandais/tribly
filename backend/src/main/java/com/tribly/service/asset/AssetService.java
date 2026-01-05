package com.tribly.service.asset;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.asset.repository.AssetRepository;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.repository.AllTeamEntityRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.common.response.AssetDimensionsDto;
import com.tribly.dto.common.response.AssetDto;
import com.tribly.dto.common.response.AssetsDto;
import com.tribly.enums.AssetType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.infrastructure.imgproxy.ImgProxyService;
import com.tribly.service.asset.response.AssetWithFile;
import com.tribly.service.asset.response.DownloadableAsset;
import com.tribly.service.security.TeamSecurityService;
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

  @Inject UserRepository userRepository;

  @Inject AllTeamEntityRepository allTeamEntityRepository;

  @Inject TeamSecurityService securityService;

  @Inject ImgProxyService imgProxyService;

  @ConfigProperty(name = "storage.path")
  String storagePath = "/tmp";

  private final Tika tika = new Tika();

  public AssetDto createAsset(
      String teamSlug, Long userId, InputStream inputStream, String fileName) throws IOException {
    // Security check: reuse ride permissions (admins & organizers can create routes)
    UserTeam userTeam = securityService.requireOrganizer(userId, teamSlug);

    User creator =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    AssetWithFile assetFile =
        addAssetStream(creator, userTeam.getTeam(), AssetType.IMAGE, null, inputStream, fileName);
    return map(assetFile.asset());
  }

  public AssetWithFile addAsset(
      User creator, TeamEntity teamEntity, AssetType type, String fileName) throws IOException {
    return addAssetStream(creator, teamEntity.getTeam(), type, teamEntity, null, fileName);
  }

  @Transactional
  public AssetWithFile addAssetStream(
      User creator,
      Team team,
      AssetType type,
      @Nullable TeamEntity teamEntity,
      @Nullable InputStream content,
      String fileName)
      throws IOException {
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

  public File getAssetFile(Team team, long fileId) {
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    Path assetDirectory = Path.of(storagePath, TsidUtils.toString(team.getId()), subPath);
    return new File(assetDirectory.toFile(), idString);
  }

  public String getRelativeAssetFile(Team team, long fileId) {
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return TsidUtils.toString(team.getId()) + "/" + subPath + "/" + idString;
  }

  public DownloadableAsset getDownloadableAsset(String assetId, @Nullable Long userId) {
    Asset asset = getAsset(TsidUtils.toLong(assetId), userId);
    File file = getAssetFile(asset);
    return new DownloadableAsset(file, asset.getContentType());
  }

  public Response getImage(String assetId, @Nullable Long userId, int size, String accept) {
    Asset asset = getAsset(TsidUtils.toLong(assetId), userId);
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

  public Asset getAsset(Long id) {
    return assetRepository
        .findByIdOptional(id)
        .orElseThrow(() -> BusinessException.notFound("Asset", id));
  }

  public Asset getAsset(Long id, @Nullable Long userId) {
    Asset asset =
        assetRepository
            .findByIdOptional(id)
            .orElseThrow(() -> BusinessException.notFound("Asset", id));
    if (asset.getTeamEntity() == null) {
      if (asset.getTeam().getVisibility() == Visibility.TEAM) {
        securityService.requireOrganizer(userId, asset.getTeam().getSlug());
      }
    } else {
      TriblyPage<TeamEntity> page =
          allTeamEntityRepository.find(
              TeamEntityQueryBasic.builder()
                  .userId(userId)
                  .id(asset.getTeamEntity().getId())
                  .size(1)
                  .build());
      if (page.total() == 0) {
        throw BusinessException.forbidden("");
      }
    }
    return asset;
  }

  public void deleteAsset(Long id) {
    // FIXME auto delete files on cascade ?
    Asset asset =
        assetRepository
            .findByIdOptional(id)
            .orElseThrow(() -> BusinessException.notFound("Asset", id));
    File file = getAssetFile(asset);
    if (file.exists()) {
      file.delete();
    }
    // FIXME delete if parent directory is empty
  }

  public AssetDto map(Asset asset) {
    Visibility visibility;
    if (asset.getTeamEntity() == null) {
      visibility = asset.getTeam().getVisibility();
    } else {
      visibility = asset.getTeamEntity().getVisibility();
    }
    AssetDimensionsDto assetDimensionsDto;
    String imageUrl;
    if (asset.getContentType().startsWith("image/")) {
      imageUrl =
          "/api/download/"
              + visibility.name().toLowerCase()
              + "/images/"
              + TsidUtils.toString(asset.getId())
              + "/{size}";
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

  public AssetsDto getAssetsDto(TeamEntity teamEntity) {
    Map<AssetType, List<Asset>> assets =
        teamEntity.getAssets().stream().collect(Collectors.groupingBy(Asset::getType));
    return new AssetsDto(
        getAssetDto(assets, AssetType.LOGO),
        getAssetDtoList(assets, AssetType.IMAGE),
        getAssetDtoList(assets, AssetType.VIDEO),
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

    if (assets == null) {
      return;
    }
    order = addAssetToEntity(order, teamEntity, AssetType.LOGO, assets.logo());
    order = addAssetsToEntity(order, teamEntity, AssetType.IMAGE, assets.images());
    order = addAssetsToEntity(order, teamEntity, AssetType.VIDEO, assets.videos());
    addAssetsToEntity(order, teamEntity, AssetType.ATTACHMENT, assets.attachments());
  }

  private int addAssetsToEntity(
      int order, TeamEntity teamEntity, AssetType assetType, List<AssetDto> assetDtos) {
    if (assetDtos == null) {
      return order;
    }
    for (AssetDto assetDto : assetDtos) {
      addAssetToEntity(order, teamEntity, assetType, assetDto);
      order++;
    }
    return order;
  }

  private int addAssetToEntity(
      int order, TeamEntity teamEntity, AssetType assetType, @Nullable AssetDto assetDto) {
    if (assetDto == null) {
      return order;
    }
    Long assetId = TsidUtils.toLong(assetDto.id());
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
