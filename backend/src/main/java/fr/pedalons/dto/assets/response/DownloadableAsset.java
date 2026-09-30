package fr.pedalons.dto.assets.response;

import java.io.InputStream;

/** @param fileName the asset's stored name, what a download is saved as */
public record DownloadableAsset(InputStream content, String contentType, String fileName) {}
