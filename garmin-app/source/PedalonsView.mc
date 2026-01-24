using Toybox.WatchUi;
using Toybox.Graphics;

/**
 * Main route list view.
 * Shows loading/error states, then switches to Menu2 when routes are loaded.
 */
class PedalonsView extends WatchUi.View {
    private var _apiClient;
    private var _isLoading = true;
    private var _error;

    function initialize(apiClient) {
        View.initialize();
        _apiClient = apiClient;
        _error = null;
    }

    function onShow() {
        // Load routes when view is shown
        loadRoutes();
    }

    function loadRoutes() {
        _isLoading = true;
        _error = null;
        WatchUi.requestUpdate();

        _apiClient.fetchRoutes(method(:onRoutesLoaded));
    }

    function onRoutesLoaded(routes) {
        _isLoading = false;

        if (routes != null && routes.size() > 0) {
            // Create Menu2 with routes
            var menu = new WatchUi.Menu2({
                :title => WatchUi.loadResource(Rez.Strings.RoutesTitle)
            });

            if (menu has :setActionMenuIndicator) {
                menu.setActionMenuIndicator({:enabled => false});
            }
            if (menu has :setControlBar) {
                menu.setControlBar({
                    :leftButton => WatchUi.CONTROL_BAR_LEFT_BUTTON_BACK,
                    :rightButton => WatchUi.CONTROL_BAR_RIGHT_BUTTON_ACCEPT
                });
            }

            for (var i = 0; i < routes.size(); i++) {
                var route = routes[i];
                var name = route.get("name");
                var label = route.get("label");
                var distance = route.get("distance");
                var elevationGain = route.get("elevationGain");

                // Use label if present, otherwise name
                var title = (label != null && label.length() > 0) ? label : name;
                if (title == null) {
                    title = WatchUi.loadResource(Rez.Strings.Unknown);
                }

                // Format stats as sublabel
                var distanceKm = (distance != null) ? (distance / 1000.0).format("%.1f") + WatchUi.loadResource(Rez.Strings.UnitKm) : "";
                var elevation = (elevationGain != null) ? elevationGain.format("%.0f") + WatchUi.loadResource(Rez.Strings.UnitM) + " " + WatchUi.loadResource(Rez.Strings.UnitDPlus) : "";
                var sublabel = distanceKm + " | " + elevation;

                // Use index as ID, store route in menu item
                menu.addItem(new WatchUi.MenuItem(title, sublabel, i, {}));
            }

            // Switch to menu view
            WatchUi.switchToView(menu, new RouteMenuDelegate(routes, _apiClient), WatchUi.SLIDE_IMMEDIATE);
        } else if (routes != null && routes.size() == 0) {
            _error = WatchUi.loadResource(Rez.Strings.NoRoutes);
            WatchUi.requestUpdate();
        } else {
            _error = WatchUi.loadResource(Rez.Strings.ConnectionError);
            WatchUi.requestUpdate();
        }
    }

    function getApiClient() {
        return _apiClient;
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

        if (_isLoading) {
            // Show loading message
            dc.setColor(Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY,
                Graphics.FONT_SMALL,
                WatchUi.loadResource(Rez.Strings.LoadingRoutes),
                Graphics.TEXT_JUSTIFY_CENTER | Graphics.TEXT_JUSTIFY_VCENTER
            );
            return;
        }

        if (_error != null) {
            // Show error
            dc.setColor(Graphics.COLOR_RED, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY,
                Graphics.FONT_SMALL,
                _error,
                Graphics.TEXT_JUSTIFY_CENTER | Graphics.TEXT_JUSTIFY_VCENTER
            );
            return;
        }
    }
}
