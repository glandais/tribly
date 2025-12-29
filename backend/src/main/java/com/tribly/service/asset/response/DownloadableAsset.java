package com.tribly.service.asset.response;

import java.io.File;

public record DownloadableAsset(File file, String contentType) {}
