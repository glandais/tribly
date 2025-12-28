package com.tribly.service.asset;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.asset.repository.AssetRepository;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.enums.AssetType;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.asset.response.AssetFile;
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AssetService {

  @Inject AssetRepository assetRepository;

  @ConfigProperty(name = "gpx.storage.path")
  String storagePath = "/tmp";

  public AssetFile addAsset(Team team, AssetType type, String fileName) throws IOException {
    return addAssetStream(team, type, null, null, fileName);
  }

  public AssetFile addAsset(TeamEntity teamEntity, AssetType type, String fileName)
      throws IOException {
    return addAssetStream(teamEntity.getTeam(), type, teamEntity, null, fileName);
  }

  public AssetFile addAssetFile(
      Team team, AssetType type, @Nullable TeamEntity teamEntity, File file, String fileName)
      throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      return addAssetStream(team, type, teamEntity, fis, fileName);
    }
  }

  public AssetFile addAssetStream(
      Team team,
      AssetType type,
      @Nullable TeamEntity teamEntity,
      @Nullable InputStream content,
      String fileName)
      throws IOException {
    Long id = TSID.Factory.getTsid().toLong();
    File file = getAssetFile(team, id);

    Files.createDirectories(file.getParentFile().toPath());

    Asset asset = new Asset(team, type, fileName);
    asset.setId(id);
    asset.setTeamEntity(teamEntity);

    if (content != null) {
      try (FileOutputStream fos = new FileOutputStream(file)) {
        IOUtils.copy(content, fos);
      }
    }

    assetRepository.persistAndFlush(asset);
    return new AssetFile(asset, file);
  }

  protected File getAssetFile(Asset asset) {
    return getAssetFile(asset.getTeam(), asset.getId());
  }

  protected File getAssetFile(Team team, Long id) {
    String idString = TsidUtils.toString(id);
    String subPath = idString.substring(0, 5);
    Path assetDirectory = Path.of(storagePath, TsidUtils.toString(team.getId()), subPath);
    return new File(assetDirectory.toFile(), idString);
  }

  public AssetFile getAsset(Long id) {
    Asset asset =
        assetRepository
            .findByIdOptional(id)
            .orElseThrow(() -> BusinessException.notFound("Asset", id));
    return new AssetFile(asset, getAssetFile(asset));
  }

  public AssetFile getAsset(Long id, @Nullable Long userId) {
    // FIXME check access to asset
    return getAsset(id);
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
}
