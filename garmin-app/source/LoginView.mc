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
        // Clear screen
        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_BLACK);
        dc.clear();

        var width = dc.getWidth();
        var height = dc.getHeight();
        var centerX = width / 2;
        var centerY = height / 2;

        if (_userCode != null) {
            // Display user code and instructions
            // Instructions
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY - 70,
                Graphics.FONT_TINY,
                WatchUi.loadResource(Rez.Strings.GoTo),
                Graphics.TEXT_JUSTIFY_CENTER
            );

            // URL
            dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY - 45,
                Graphics.FONT_SMALL,
                "pedalons.fr/device",
                Graphics.TEXT_JUSTIFY_CENTER
            );

            // User code (large)
            dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY,
                Graphics.FONT_NUMBER_HOT,
                _userCode,
                Graphics.TEXT_JUSTIFY_CENTER
            );

            // Status
            dc.setColor(Graphics.COLOR_BLUE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY + 55,
                Graphics.FONT_TINY,
                _statusText,
                Graphics.TEXT_JUSTIFY_CENTER
            );
        } else {
            // Initial state - prompt to start login
            // Draw title
            dc.setColor(Graphics.COLOR_BLUE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY - 60,
                Graphics.FONT_MEDIUM,
                WatchUi.loadResource(Rez.Strings.LoginRequired),
                Graphics.TEXT_JUSTIFY_CENTER
            );

            // Draw instructions
            dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY,
                Graphics.FONT_SMALL,
                WatchUi.loadResource(Rez.Strings.LoginInstructions),
                Graphics.TEXT_JUSTIFY_CENTER
            );

            // Draw "Press to login" hint
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY + 60,
                Graphics.FONT_TINY,
                "Press SELECT to login",
                Graphics.TEXT_JUSTIFY_CENTER
            );
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
