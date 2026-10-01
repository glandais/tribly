package fr.pedalons.common;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * A WGS84 position in degrees. The bounds are validated wherever a request carries one through
 * {@code @Valid} (planner points, router): an out-of-range coordinate would otherwise reach the GPX
 * pipeline, whose resampling allocates by distance (docs/LEDGER_*.md SEC-6).
 */
public record GeoPoint(
    @Schema(required = true) @DecimalMin("-180") @DecimalMax("180") double lng,
    @Schema(required = true) @DecimalMin("-90") @DecimalMax("90") double lat) {}
