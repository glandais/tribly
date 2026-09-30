package fr.pedalons.dto.auth.response;

import org.jspecify.annotations.Nullable;

/**
 * What a refresh gives back, internally: the response body without any refresh token, and the new
 * refresh token when this refresh rotated it — null inside the grace of a rotation made by another
 * one (docs/LEDGER_*.md SEC-27). Not exposed via the API: the resource puts the token in the cookie
 * or in the body.
 */
public record RefreshResult(AuthResponse response, @Nullable String refreshToken) {}
