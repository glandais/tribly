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
    private var _formatUtils;

    function initialize(apiClient) {
        View.initialize();
        _apiClient = apiClient;
        _error = null;
        _formatUtils = new FormatUtils();
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
                    :leftButton => WatchUi.CONTROL_BAR_LEFT_BUTTON_BACK
                });
            }

            for (var i = 0; i < routes.size(); i++) {
                var route = routes[i];
                var titleAndSublabel = formatRouteMenuItem(route);

                // Use index as ID, store route in menu item
                menu.addItem(new WatchUi.MenuItem(titleAndSublabel[0], titleAndSublabel[1], i, {}));
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

    /**
     * Format route menu item based on route type.
     * @param route Route dictionary
     * @return Array [title, sublabel]
     *
     * Route types and formatting:
     * - Standalone route: Route name | Distance / Elevation
     * - Ride route: Ride name - Date | Ride time
     * - Group route: Group name - Date | Group time
     */
    private function formatRouteMenuItem(route) {
        var routeName = route.get("routeName");
        var rideName = route.get("rideName");
        var startDateTime = route.get("startDateTime");
        var groupName = route.get("groupName");
        var distance = route.get("distance");
        var elevationGain = route.get("elevationGain");

        var title;
        var sublabel;

        var withGroup = groupName != null && groupName.length() > 0;
        var withRide = rideName != null && rideName.length() > 0;

        if (withGroup && withRide) {
            title = groupName + " - " + rideName;
        } else if (withRide) {
            title = rideName;
        } else {
            // Standalone route: Route name | Distance / Elevation
            title = (routeName != null && routeName.length() > 0) ? routeName : WatchUi.loadResource(Rez.Strings.Unknown);
        }
        if (startDateTime != null && startDateTime.length() > 0) {
            sublabel = _formatUtils.formatDateTime(startDateTime);
        } else {
            sublabel = _formatUtils.formatDistanceElevation(distance, elevationGain);
        }

        return [title, sublabel];
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
