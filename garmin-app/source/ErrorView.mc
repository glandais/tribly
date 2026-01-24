using Toybox.WatchUi;
using Toybox.Graphics;
using Toybox.System;

/**
 * View displayed when an error occurs.
 */
class ErrorView extends WatchUi.View {
    private var _message;

    function initialize(message) {
        View.initialize();
        _message = message;
    }

    function onLayout(dc) {
    }

    function onUpdate(dc) {
        // Clear screen
        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_BLACK);
        dc.clear();

        var width = dc.getWidth();
        var height = dc.getHeight();
        var centerX = width / 2;
        var centerY = height / 2;

        // Draw error message
        dc.setColor(Graphics.COLOR_RED, Graphics.COLOR_TRANSPARENT);
        dc.drawText(
            centerX,
            centerY - 20,
            Graphics.FONT_SMALL,
            _message,
            Graphics.TEXT_JUSTIFY_CENTER
        );

        // Draw retry hint
        dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
        dc.drawText(
            centerX,
            centerY + 20,
            Graphics.FONT_TINY,
            WatchUi.loadResource(Rez.Strings.Retry) + WatchUi.loadResource(Rez.Strings.SelectHint),
            Graphics.TEXT_JUSTIFY_CENTER
        );
    }
}

/**
 * Input delegate for error view.
 */
class ErrorDelegate extends WatchUi.BehaviorDelegate {
    private var _app;

    function initialize(app) {
        BehaviorDelegate.initialize();
        _app = app;
    }

    function onSelect() {
        // Retry - go back to login or route list
        if (_app.getAuthManager().hasValidToken()) {
            var viewPair = _app.showRouteList();
            WatchUi.switchToView(viewPair[0], viewPair[1], WatchUi.SLIDE_IMMEDIATE);
        } else {
            var viewPair = _app.showLoginView();
            WatchUi.switchToView(viewPair[0], viewPair[1], WatchUi.SLIDE_IMMEDIATE);
        }
        return true;
    }

    function onBack() {
        // Exit app
        System.exit();
        return true;
    }
}
