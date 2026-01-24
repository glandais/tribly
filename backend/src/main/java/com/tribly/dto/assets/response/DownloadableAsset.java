package com.tribly.dto.assets.response;

import java.io.InputStream;

public record DownloadableAsset(InputStream content, String contentType) {}
