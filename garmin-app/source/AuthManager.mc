using Toybox.Application.Storage;
using Toybox.Time;
using Toybox.System;

/**
 * Manages authentication tokens for Tribly API.
 * Handles token storage, retrieval, and refresh.
 */
class AuthManager {
    private const ACCESS_TOKEN_KEY = "access_token";
    private const REFRESH_TOKEN_KEY = "refresh_token";
    private const TOKEN_EXPIRY_KEY = "token_expiry";

    function initialize() {
    }

    /**
     * Check if we have a valid (non-expired) access token.
     */
    function hasValidToken() {
        var accessToken = getAccessToken();
        if (accessToken == null || accessToken.length() == 0) {
            return false;
        }

        var expiry = getTokenExpiry();
        if (expiry == null) {
            return false;
        }

        // Check if token is expired (with 5 minute buffer)
        var now = Time.now().value();
        return expiry > (now + 300);
    }

    /**
     * Get the current access token.
     */
    function getAccessToken() {
        return Storage.getValue(ACCESS_TOKEN_KEY);
    }

    /**
     * Get the refresh token.
     */
    function getRefreshToken() {
        return Storage.getValue(REFRESH_TOKEN_KEY);
    }

    /**
     * Get the token expiry timestamp.
     */
    function getTokenExpiry() {
        return Storage.getValue(TOKEN_EXPIRY_KEY);
    }

    /**
     * Save tokens from API response.
     */
    function saveTokens(accessToken, refreshToken, expiresIn) {
        Storage.setValue(ACCESS_TOKEN_KEY, accessToken);

        if (refreshToken != null) {
            Storage.setValue(REFRESH_TOKEN_KEY, refreshToken);
        }

        // Calculate expiry timestamp
        var expiry = Time.now().value() + expiresIn;
        Storage.setValue(TOKEN_EXPIRY_KEY, expiry);

        // System.println("Tokens saved, expires at: " + expiry);
    }

    /**
     * Clear all stored tokens (logout).
     */
    function clearTokens() {
        Storage.deleteValue(ACCESS_TOKEN_KEY);
        Storage.deleteValue(REFRESH_TOKEN_KEY);
        Storage.deleteValue(TOKEN_EXPIRY_KEY);
        // System.println("Tokens cleared");
    }

    /**
     * Check if we need to refresh the token.
     * Returns true if token expires in less than 10 minutes.
     */
    function needsRefresh() {
        var expiry = getTokenExpiry();
        if (expiry == null) {
            return true;
        }

        var now = Time.now().value();
        // Refresh if less than 10 minutes remaining
        return expiry < (now + 600);
    }
}
