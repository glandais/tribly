package com.tribly.api.assets;

import jakarta.ws.rs.Path;

@Path("/api/download/team/assets/{assetId}/{fileName}")
public class DownloadTeamAssetResource extends AbstractDownloadAssetResource {}
