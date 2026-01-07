package com.tribly.dto.assets.response;

import java.io.File;

public record DownloadableAsset(File file, String contentType) {}
