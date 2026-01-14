package com.tribly.dto.auth.response;

/**
 * Internal result containing both the auth response (for the client) and the refresh token (for the
 * cookie). This is not exposed via the API.
 */
public record AuthResult(AuthResponse response, String refreshToken) {}
