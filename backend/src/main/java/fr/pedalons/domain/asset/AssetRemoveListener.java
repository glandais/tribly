package fr.pedalons.domain.asset;

import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.service.asset.AssetService;
import jakarta.inject.Inject;
import jakarta.persistence.PreRemove;
import org.jboss.logging.Logger;

public class AssetRemoveListener {

  private static final Logger LOG = Logger.getLogger(AssetRemoveListener.class);

  @Inject StorageService storageService;

  @Inject AssetService assetService;

  @PreRemove
  void onPreRemove(Asset asset) {
    String key = assetService.getAssetKey(asset.getTeam(), asset.getFileId());
    try {
      storageService.delete(key);
    } catch (Exception e) {
      LOG.warnv("Failed to delete asset {0} from storage: {1}", asset.getId(), e.getMessage());
    }
  }
}
