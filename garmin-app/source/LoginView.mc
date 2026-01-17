using Toybox.WatchUi;
using Toybox.Graphics;
using Toybox.System;

/**
 * View displayed when user needs to log in.
 * Shows instructions to open Garmin Connect on phone.
 */
class LoginView extends WatchUi.View {

    function initialize() {
        View.initialize();
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
        // Start OAuth flow
        _app.startOAuthFlow();
        return true;
    }

    function onBack() {
        // Exit app
        System.exit();
        return true;
    }
}
