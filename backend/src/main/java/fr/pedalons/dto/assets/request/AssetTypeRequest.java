package fr.pedalons.dto.assets.request;

import fr.pedalons.enums.AssetType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Public-facing subset of {@link AssetType} that clients are allowed to request when uploading
 * assets through the REST API.
 *
 * <p><strong>Security:</strong> only non-system asset types belong here. System types
 * (ROUTE_ORIGINAL_GPX, ROUTE_FILTERED_GPX, ROUTE_FIT, ROUTE/RIDE/TRIP_THUMBNAIL_*, etc.) are
 * produced by the backend's GPX/route processing pipeline and must never be settable from a client
 * request — they would let a caller bypass server-generated provenance, overwrite generated route
 * artifacts, or smuggle a file under a system AssetType whose validation/category mapping has
 * different semantics than what the user actually intended. Adding any system AssetType to this
 * enum would be a privilege-escalation vulnerability. {@link
 * fr.pedalons.infrastructure.filetype.FileTypeCategory#forAssetType} and the AssetType.isSystem()
 * flag are the authoritative source on what counts as system-only.
 */
@RequiredArgsConstructor
@Getter
public enum AssetTypeRequest {
  LOGO(AssetType.LOGO),
  IMAGE(AssetType.IMAGE),
  ATTACHMENT(AssetType.ATTACHMENT);

  final AssetType assetType;
}
