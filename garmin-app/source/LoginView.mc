using Toybox.WatchUi;
using Toybox.Graphics;
using Toybox.Lang;
using Toybox.System;

/**
 * View displayed when user needs to log in.
 * Shows the Device Code Flow: a QR code of the verification URL with the code in it, then the
 * site and the user code, which the web page asks to compare before pairing (ledger SEC-2).
 */
class LoginView extends WatchUi.View {
    // Quiet zone around the QR code, in modules (the standard asks for 4)
    private const QUIET_ZONE = 4;
    // Beyond this a QR code fills a 1050 screen without scanning any better
    private const MAX_MODULE_PX = 8;

    private var _userCode;
    private var _host;
    private var _qrModules;
    private var _statusText;
    private var _isPolling;

    function initialize() {
        View.initialize();
        _userCode = null;
        _host = null;
        _qrModules = null;
        _statusText = "";
        _isPolling = false;
    }

    /**
     * Set the code to display, with the URLs the backend returned for this domain.
     */
    function setDeviceCode(code, verificationUri, verificationUriComplete) {
        _userCode = code;
        _host = hostOf(verificationUri);
        _qrModules =
            verificationUriComplete != null ? QrCode.encode(verificationUriComplete) : null;
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
            var label = WatchUi.loadResource(Rez.Strings.GoTo);
            var textTop = centerY - 70;
            var qrTop = 6;
            var textHeight =
                2 * dc.getFontHeight(Graphics.FONT_TINY) +
                dc.getFontHeight(Graphics.FONT_SMALL) +
                dc.getFontHeight(Graphics.FONT_LARGE) +
                22;
            var modulePx = _qrModules != null ? moduleSize(dc, textHeight + 2 * qrTop) : 0;
            if (modulePx > 0) {
                var box = drawQrCode(dc, centerX, qrTop, modulePx);
                label = WatchUi.loadResource(Rez.Strings.ScanOrGoTo);
                textTop = qrTop + box + 6;
            }

            var layout = new VerticalLayout(textTop, 4);
            layout.draw(dc, centerX, Graphics.FONT_TINY, label, null, Graphics.COLOR_LT_GRAY);
            if (_host != null) {
                layout.draw(dc, centerX, Graphics.FONT_SMALL, _host, null, Graphics.COLOR_WHITE);
            }
            layout.skip(10);
            layout.draw(dc, centerX, Graphics.FONT_LARGE, _userCode, null, Graphics.COLOR_WHITE);
            layout.draw(dc, centerX, Graphics.FONT_TINY, _statusText, null, Graphics.COLOR_BLUE);
        } else {
            var layout = new VerticalLayout(centerY - 30, 60);
            layout.draw(
                dc,
                centerX,
                Graphics.FONT_MEDIUM,
                WatchUi.loadResource(Rez.Strings.LoginRequired),
                null,
                Graphics.COLOR_BLUE
            );
            layout.draw(
                dc,
                centerX,
                Graphics.FONT_TINY,
                WatchUi.loadResource(Rez.Strings.PressSelectToLogin),
                null,
                Graphics.COLOR_LT_GRAY
            );
        }
    }

    /**
     * Largest whole module size that fits the screen above the text, 0 below 2 px (too small to
     * scan: the view then falls back to the site and the code alone).
     */
    private function moduleSize(dc, reservedHeight) {
        var modules = QrCode.sideOf(_qrModules) + 2 * QUIET_ZONE;
        var room = dc.getHeight() - reservedHeight;
        if (dc.getWidth() < room) {
            room = dc.getWidth();
        }
        var size = room / modules;
        if (size > MAX_MODULE_PX) {
            size = MAX_MODULE_PX;
        }
        return size >= 2 ? size : 0;
    }

    /**
     * Draw the QR code on a white square (its quiet zone), dark runs of a row as one rectangle.
     * Returns the side of the square in pixels.
     */
    private function drawQrCode(dc, centerX, top, modulePx) {
        var modules = _qrModules as Lang.ByteArray;
        var side = QrCode.sideOf(modules);
        var box = (side + 2 * QUIET_ZONE) * modulePx;
        var left = centerX - box / 2;
        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_WHITE);
        dc.fillRectangle(left, top, box, box);

        dc.setColor(Graphics.COLOR_BLACK, Graphics.COLOR_WHITE);
        var origin = QUIET_ZONE * modulePx;
        for (var y = 0; y < side; y++) {
            var x = 0;
            while (x < side) {
                if (modules[y * side + x] == 1) {
                    var start = x;
                    while (x < side && modules[y * side + x] == 1) {
                        x++;
                    }
                    dc.fillRectangle(
                        left + origin + start * modulePx,
                        top + origin + y * modulePx,
                        (x - start) * modulePx,
                        modulePx
                    );
                } else {
                    x++;
                }
            }
        }
        return box;
    }

    /**
     * "https://www.pedalons.fr/garmin" -> "www.pedalons.fr/garmin": what the rider would type.
     */
    private function hostOf(uri) {
        if (uri == null) {
            return null;
        }
        var scheme = uri.find("://");
        return scheme != null ? uri.substring(scheme + 3, uri.length()) : uri;
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
    }
}
