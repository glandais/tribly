package fr.pedalons.domain.asset;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.infrastructure.storage.StorageService;
import jakarta.inject.Inject;
import jakarta.persistence.PreRemove;
import org.jboss.logging.Logger;

public class AssetRemoveListener {

  private static final Logger LOG = Logger.getLogger(AssetRemoveListener.class);
  private static final String ASSETS_PREFIX = "assets";

  @Inject StorageService storageService;

  @PreRemove
  void onPreRemove(Asset asset) {
    String teamId = TsidUtils.toString(asset.getTeam().getId());
    String fileIdString = TsidUtils.toString(asset.getFileId());
    String subPath = fileIdString.substring(0, 4);
    String key = ASSETS_PREFIX + "/" + teamId + "/" + subPath + "/" + fileIdString;
    try {
      storageService.delete(key);
    } catch (Exception e) {
      LOG.warnv("Failed to delete asset {0} from storage: {1}", asset.getId(), e.getMessage());
    }
  }
}
