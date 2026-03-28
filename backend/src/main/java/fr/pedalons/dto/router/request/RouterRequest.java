package fr.pedalons.dto.router.request;

import fr.pedalons.common.GeoPoint;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record RouterRequest(
    @Schema(required = true) GeoPoint from,
    @Schema(required = true) GeoPoint to,
    @Schema(required = true) RouterProfile profile) {}
