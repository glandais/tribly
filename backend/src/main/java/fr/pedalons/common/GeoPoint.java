package fr.pedalons.common;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record GeoPoint(@Schema(required = true) double lng, @Schema(required = true) double lat) {}
