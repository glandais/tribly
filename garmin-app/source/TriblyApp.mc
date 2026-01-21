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
        System.println("startOAuthFlow called");

        var redirectUri = ApiClient.BASE_URL + "/garmin/success";
        var params = {
            "client_id" => "garmin-connect-iq",
            "response_type" => "code",
            "redirect_uri" => redirectUri
        };

        var oauthUrl =  ApiClient.BASE_URL + "/garmin/login" +
            "?client_id=" + params["client_id"] +
            "&response_type=" + params["response_type"] +
            "&redirect_uri=" + params["redirect_uri"];
        System.println("OAuth URL: " + oauthUrl);

        Communications.makeOAuthRequest(
            ApiClient.BASE_URL + "/garmin/login",
            params,
            redirectUri,
            Communications.OAUTH_RESULT_TYPE_URL,
            {"code" => "code"}
        );
    }

    /**
     * Handle OAuth callback from phone browser.
     */
    function onOAuthMessage(message as Communications.OAuthMessage) as Void {
        System.println("onOAuthMessage called");
        System.println("responseCode: " + message.responseCode);
        var data = message.data;
        System.println("data: " + data);
        if (data != null && data instanceof Lang.Dictionary) {
            var dict = data as Lang.Dictionary;
            System.println("dict keys: " + dict.keys());
            if (dict.hasKey("code")) {
                var code = dict.get("code");
                System.println("Got auth code: " + code);
                exchangeCodeForTokens(code);
                return;
            }
        }
        // OAuth failed, show error
        System.println("OAuth failed - no code in response");
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
