package com.tribly.api.assets;

import jakarta.ws.rs.Path;

@Path("/api/download/public/assets/{assetId}/{fileName}")
public class DownloadPublicAssetResource extends AbstractDownloadAssetResource {}
