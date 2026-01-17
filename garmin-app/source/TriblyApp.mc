using Toybox.Application;
using Toybox.Communications;
using Toybox.Lang;
using Toybox.System;
using Toybox.WatchUi;

/**
 * Main Tribly application for Garmin Connect IQ.
 * Allows users to browse and download routes from their Tribly teams.
 */
class TriblyApp extends Application.AppBase {
    private var _authManager;
    private var _apiClient;

    function initialize() {
        AppBase.initialize();
        _authManager = new AuthManager();
        _apiClient = new ApiClient(_authManager);
    }

    function onStart(state) {
        // Register for OAuth messages from phone
        Communications.registerForOAuthMessages(method(:onOAuthMessage));
    }

    function onStop(state) {
    }

    function getInitialView() {
        // Check if we have a valid token
        if (_authManager.hasValidToken()) {
            return showRouteList();
        } else {
            return showLoginView();
        }
    }

    /**
     * Show the login view prompting user to authenticate.
     */
    function showLoginView() {
        var view = new LoginView();
        var delegate = new LoginDelegate(self);
        return [view, delegate];
    }

    /**
     * Show the route list view.
     */
    function showRouteList() {
        var view = new TriblyView(_apiClient);
        var delegate = new TriblyDelegate(view, _apiClient);
        return [view, delegate];
    }

    /**
     * Initiate OAuth flow by opening browser on phone.
     */
    function startOAuthFlow() {
        var params = {
            "client_id" => "garmin-connect-iq",
            "response_type" => "code",
            "redirect_uri" => "connectiq://oauth"
        };

        Communications.makeOAuthRequest(
            ApiClient.API_BASE_URL + "/garmin/oauth/authorize",
            params,
            "connectiq://oauth",
            Communications.OAUTH_RESULT_TYPE_URL,
            {"code" => "code"}
        );
    }

    /**
     * Handle OAuth callback from phone browser.
     */
    function onOAuthMessage(message as Communications.OAuthMessage) as Void {
        var data = message.data;
        if (data != null && data instanceof Lang.Dictionary) {
            var dict = data as Lang.Dictionary;
            if (dict.hasKey("code")) {
                var code = dict.get("code");
                exchangeCodeForTokens(code);
                return;
            }
        }
        // OAuth failed, show error
        WatchUi.switchToView(
            new ErrorView(WatchUi.loadResource(Rez.Strings.ConnectionError)),
            new ErrorDelegate(self),
            WatchUi.SLIDE_IMMEDIATE
        );
    }

    /**
     * Exchange authorization code for access and refresh tokens.
     */
    function exchangeCodeForTokens(code) {
        _apiClient.exchangeCode(code, method(:onTokenExchange));
    }

    /**
     * Callback when token exchange completes.
     */
    function onTokenExchange(success) {
        if (success) {
            // Token saved, show route list
            var viewPair = showRouteList();
            WatchUi.switchToView(viewPair[0], viewPair[1], WatchUi.SLIDE_IMMEDIATE);
        } else {
            // Token exchange failed
            WatchUi.switchToView(
                new ErrorView(WatchUi.loadResource(Rez.Strings.ConnectionError)),
                new ErrorDelegate(self),
                WatchUi.SLIDE_IMMEDIATE
            );
        }
    }

    /**
     * Get the auth manager instance.
     */
    function getAuthManager() {
        return _authManager;
    }

    /**
     * Get the API client instance.
     */
    function getApiClient() {
        return _apiClient;
    }
}
