package fr.pedalons.service.asset.response;

import fr.pedalons.domain.asset.Asset;
import java.io.File;

public record AssetWithFile(Asset asset, File file) {}
