using Toybox.WatchUi;
using Toybox.System;

/**
 * Input delegate for the home Menu2.
 * Handles selection of "Rides" or "Routes" top-level entries.
 */
class HomeMenuDelegate extends WatchUi.Menu2InputDelegate {
    private var _rides;
    private var _routes;
    private var _apiClient;
    private var _formatUtils;

    function initialize(rides, routes, apiClient, formatUtils) {
        Menu2InputDelegate.initialize();
        _rides = rides;
        _routes = routes;
        _apiClient = apiClient;
        _formatUtils = formatUtils;
    }

    function onSelect(item) {
        var id = item.getId();

        if (id.equals("rides")) {
            if (_rides.size() == 0) {
                return; // Nothing to show
            }

            // Build ride list menu
            var menu = new WatchUi.Menu2({
                :title => WatchUi.loadResource(Rez.Strings.Rides),
            });
            if (menu has :setControlBar) {
                menu.setControlBar({
                    :leftButton => WatchUi.CONTROL_BAR_LEFT_BUTTON_BACK,
                });
            }

            for (var i = 0; i < _rides.size(); i++) {
                var ride = _rides[i];
                var rideName = ride.get("rideName");
                var startDateTime = ride.get("startDateTime");
                var sublabel = "";
                if (startDateTime != null && startDateTime.length() > 0) {
                    sublabel = _formatUtils.formatDateTime(startDateTime);
                }
                var entries = ride.get("entries");
                var entryCount = entries != null ? entries.size() : 0;
                if (sublabel.length() > 0) {
                    sublabel = sublabel + " (" + entryCount + ")";
                } else {
                    sublabel = entryCount.toString();
                }
                menu.addItem(new WatchUi.MenuItem(rideName, sublabel, i, {}));
            }

            WatchUi.pushView(
                menu,
                new RideListMenuDelegate(_rides, _apiClient, _formatUtils),
                WatchUi.SLIDE_LEFT
            );
        } else if (id.equals("routes")) {
            if (_routes.size() == 0) {
                return; // Nothing to show
            }

            // Build standalone route list menu
            var menu = new WatchUi.Menu2({
                :title => WatchUi.loadResource(Rez.Strings.Routes),
            });
            if (menu has :setControlBar) {
                menu.setControlBar({
                    :leftButton => WatchUi.CONTROL_BAR_LEFT_BUTTON_BACK,
                });
            }

            for (var i = 0; i < _routes.size(); i++) {
                var route = _routes[i];
                var routeName = route.get("routeName");
                var distance = route.get("distance");
                var elevationGain = route.get("elevationGain");
                var sublabel = _formatUtils.formatDistanceElevation(distance, elevationGain);
                menu.addItem(new WatchUi.MenuItem(routeName, sublabel, i, {}));
            }

            WatchUi.pushView(
                menu,
                new StandaloneRouteMenuDelegate(_routes, _apiClient),
                WatchUi.SLIDE_LEFT
            );
        }
    }

    function onBack() {
        System.exit();
    }
}
