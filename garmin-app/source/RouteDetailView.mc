using Toybox.WatchUi;
using Toybox.Graphics;

/**
 * Route detail view showing route information and download option.
 */
class RouteDetailView extends WatchUi.View {
    private var _route;
    private var _isDownloading = false;
    private var _downloadStatus;

    function initialize(route) {
        View.initialize();
        if (View has :setActionMenuIndicator) {
            setActionMenuIndicator({:enabled => false});
        }
        if (View has :setControlBar) {
            setControlBar({
                :leftButton => WatchUi.CONTROL_BAR_LEFT_BUTTON_BACK,
                :rightButton => WatchUi.CONTROL_BAR_RIGHT_BUTTON_ACCEPT,
                :title => WatchUi.loadResource(Rez.Strings.Download)
            });
        }
        _route = route;
        _downloadStatus = null;
    }

    function setDownloading(downloading) {
        _isDownloading = downloading;
        if (downloading) {
            _downloadStatus = WatchUi.loadResource(Rez.Strings.Downloading);
        }
        WatchUi.requestUpdate();
    }

    function setDownloadComplete(success) {
        _isDownloading = false;
        if (success) {
            _downloadStatus = WatchUi.loadResource(Rez.Strings.DownloadComplete);
        } else {
            _downloadStatus = WatchUi.loadResource(Rez.Strings.DownloadFailed);
        }
        WatchUi.requestUpdate();
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
        var padding = 10;
        var y = padding;

        // Route name/label
        var name = _route.get("name");
        var label = _route.get("label");
        var title = (label != null && label.length() > 0) ? label : name;

        dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
        dc.drawText(
            centerX,
            y,
            Graphics.FONT_SMALL,
            title != null ? title : WatchUi.loadResource(Rez.Strings.Unknown),
            Graphics.TEXT_JUSTIFY_CENTER
        );
        y += 30;

        // Route name (if different from label)
        if (label != null && label.length() > 0 && name != null) {
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                y,
                Graphics.FONT_TINY,
                name,
                Graphics.TEXT_JUSTIFY_CENTER
            );
            y += 20;
        }

        // Distance
        var distance = _route.get("distance");
        if (distance != null) {
            var distanceKm = (distance / 1000.0).format("%.1f");
            y += 15;
            dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                y,
                Graphics.FONT_TINY,
                WatchUi.loadResource(Rez.Strings.Distance) + ": " + distanceKm + " " + WatchUi.loadResource(Rez.Strings.UnitKm),
                Graphics.TEXT_JUSTIFY_CENTER
            );
        }

        // Elevation gain
        var elevationGain = _route.get("elevationGain");
        if (elevationGain != null) {
            y += 20;
            dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                y,
                Graphics.FONT_TINY,
                WatchUi.loadResource(Rez.Strings.Elevation) + ": " + elevationGain.format("%.0f") + " " + WatchUi.loadResource(Rez.Strings.UnitM),
                Graphics.TEXT_JUSTIFY_CENTER
            );
        }

        // Download button or status
        y = height - 60;

        if (_isDownloading) {
            dc.setColor(Graphics.COLOR_YELLOW, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                y,
                Graphics.FONT_SMALL,
                _downloadStatus != null ? _downloadStatus : "...",
                Graphics.TEXT_JUSTIFY_CENTER
            );
        } else if (_downloadStatus != null) {
            var color = _downloadStatus.equals(WatchUi.loadResource(Rez.Strings.DownloadComplete))
                ? Graphics.COLOR_GREEN
                : Graphics.COLOR_RED;
            dc.setColor(color, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                y,
                Graphics.FONT_SMALL,
                _downloadStatus,
                Graphics.TEXT_JUSTIFY_CENTER
            );
        } else {
            // Draw download button hint
            dc.setColor(Graphics.COLOR_BLUE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                y,
                Graphics.FONT_SMALL,
                WatchUi.loadResource(Rez.Strings.Download),
                Graphics.TEXT_JUSTIFY_CENTER
            );
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                y + 25,
                Graphics.FONT_TINY,
                WatchUi.loadResource(Rez.Strings.PressSelect),
                Graphics.TEXT_JUSTIFY_CENTER
            );
        }
    }
}
