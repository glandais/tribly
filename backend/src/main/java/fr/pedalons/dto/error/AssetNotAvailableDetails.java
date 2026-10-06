package fr.pedalons.dto.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * An asset a content cites that it cannot take: unknown, of another team, or already attached to
 * another content. Only the id is named, never which content or team holds it (docs/LEDGER_*.md
 * API-68).
 */
@RequiredArgsConstructor
@Getter
public class AssetNotAvailableDetails implements ErrorDetails {
  @Schema(description = "Type", required = true)
  final ErrorCode type = ErrorCode.ASSET_NOT_AVAILABLE;

  @Schema(description = "Id of the asset, as the request cited it", required = true)
  final String assetId;
}
