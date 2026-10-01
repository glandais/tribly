package fr.pedalons.dto.router.request;

import fr.pedalons.common.GeoPoint;
import jakarta.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record RouterRequest(
    @Schema(required = true) @Valid GeoPoint from,
    @Schema(required = true) @Valid GeoPoint to,
    @Schema(required = true) RouterProfile profile) {}
