using Toybox.Application.Storage;
using Toybox.Time;
using Toybox.System;

/**
 * Manages authentication tokens for Pédalons API.
 * Handles token storage, retrieval, and refresh.
 * Also manages device code flow state.
 */
class AuthManager {
    private const ACCESS_TOKEN_KEY = "access_token";
    private const REFRESH_TOKEN_KEY = "refresh_token";
    private const TOKEN_EXPIRY_KEY = "token_expiry";
    private const DEVICE_CODE_KEY = "device_code";
    private const USER_CODE_KEY = "user_code";
    private const CODE_EXPIRY_KEY = "code_expiry";

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

    // === Device Code Flow State ===

    /**
     * Save device code data for polling.
     */
    function saveDeviceCode(deviceCode, userCode, expiresIn) {
        Storage.setValue(DEVICE_CODE_KEY, deviceCode);
        Storage.setValue(USER_CODE_KEY, userCode);

        // Calculate expiry timestamp
        var expiry = Time.now().value() + expiresIn;
        Storage.setValue(CODE_EXPIRY_KEY, expiry);

        // System.println("Device code saved, expires at: " + expiry);
    }

    /**
     * Get the current device code.
     */
    function getDeviceCode() {
        return Storage.getValue(DEVICE_CODE_KEY);
    }

    /**
     * Get the current user code.
     */
    function getUserCode() {
        return Storage.getValue(USER_CODE_KEY);
    }

    /**
     * Check if the device code has expired.
     */
    function isDeviceCodeExpired() {
        var expiry = Storage.getValue(CODE_EXPIRY_KEY);
        if (expiry == null) {
            return true;
        }

        var now = Time.now().value();
        return now > expiry;
    }

    /**
     * Clear device code data.
     */
    function clearDeviceCode() {
        Storage.deleteValue(DEVICE_CODE_KEY);
        Storage.deleteValue(USER_CODE_KEY);
        Storage.deleteValue(CODE_EXPIRY_KEY);
        // System.println("Device code cleared");
    }
}
