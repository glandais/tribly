package fr.pedalons.dto.assets.response;

import java.io.InputStream;

public record DownloadableAsset(InputStream content, String contentType) {}
