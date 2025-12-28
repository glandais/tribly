package com.tribly.service.asset.response;

import com.tribly.domain.asset.Asset;
import java.io.File;

public record AssetFile(Asset asset, File file) {}
