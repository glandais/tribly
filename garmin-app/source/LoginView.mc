using Toybox.WatchUi;
using Toybox.Graphics;
using Toybox.System;

/**
 * View displayed when user needs to log in.
 * Shows the user code and verification URL for Device Code Flow.
 */
class LoginView extends WatchUi.View {
    private var _userCode;
    private var _statusText;
    private var _isPolling;

    function initialize() {
        View.initialize();
        _userCode = null;
        _statusText = "";
        _isPolling = false;
    }

    /**
     * Set the user code to display.
     */
    function setUserCode(code) {
        _userCode = code;
        _isPolling = true;
        _statusText = WatchUi.loadResource(Rez.Strings.Waiting);
        WatchUi.requestUpdate();
    }

    /**
     * Set the status text.
     */
    function setStatus(text) {
        _statusText = text;
        WatchUi.requestUpdate();
    }

    /**
     * Get whether we're polling.
     */
    function isPolling() {
        return _isPolling;
    }

    function onLayout(dc) {
        // No layout XML, we draw directly
    }

    function onUpdate(dc) {
        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_BLACK);
        dc.clear();

        var centerX = dc.getWidth() / 2;
        var centerY = dc.getHeight() / 2;

        if (_userCode != null) {
            var layout = new VerticalLayout(centerY - 70, 4);
            layout.draw(dc, centerX, Graphics.FONT_TINY, WatchUi.loadResource(Rez.Strings.GoTo), null, Graphics.COLOR_LT_GRAY);
            layout.draw(dc, centerX, Graphics.FONT_SMALL, WatchUi.loadResource(Rez.Strings.VerificationUrl), null, Graphics.COLOR_WHITE);
            layout.skip(10);
            layout.draw(dc, centerX, Graphics.FONT_NUMBER_HOT, _userCode, null, Graphics.COLOR_WHITE);
            layout.draw(dc, centerX, Graphics.FONT_TINY, _statusText, null, Graphics.COLOR_BLUE);
        } else {
            var layout = new VerticalLayout(centerY - 30, 60);
            layout.draw(dc, centerX, Graphics.FONT_MEDIUM, WatchUi.loadResource(Rez.Strings.LoginRequired), null, Graphics.COLOR_BLUE);
            layout.draw(dc, centerX, Graphics.FONT_TINY, WatchUi.loadResource(Rez.Strings.PressSelectToLogin), null, Graphics.COLOR_LT_GRAY);
        }
    }
}

/**
 * Input delegate for LoginView.
 */
class LoginDelegate extends WatchUi.BehaviorDelegate {
    private var _app;

    function initialize(app) {
        BehaviorDelegate.initialize();
        _app = app;
    }

    function onSelect() {
        // Start Device Code Flow
        _app.startDeviceCodeFlow();
        return true;
    }

    function onBack() {
        // Exit app
        System.exit();
        return true;
    }
}
