package fr.pedalons.infrastructure.gps;

import org.jspecify.annotations.Nullable;

/**
 * OAuth token response from a GPS service.
 */
public record TokenResponse(
    String accessToken,
    @Nullable String refreshToken,
    @Nullable Long expiresIn,
    @Nullable String userId) {}
