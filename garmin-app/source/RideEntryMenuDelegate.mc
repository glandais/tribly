using Toybox.WatchUi;

/**
 * Input delegate for ride entries Menu2.
 * Navigates to RouteDetailView for the selected entry.
 */
class RideEntryMenuDelegate extends WatchUi.Menu2InputDelegate {
    private var _ride;
    private var _apiClient;

    function initialize(ride, apiClient) {
        Menu2InputDelegate.initialize();
        _ride = ride;
        _apiClient = apiClient;
    }

    function onSelect(item) {
        var index = item.getId();
        var entries = _ride.get("entries");

        if (index != null && entries != null && index < entries.size()) {
            var entry = entries[index];
            var routeDict = {
                "teamSlug" => _ride.get("teamSlug"),
                "routeSlug" => entry.get("routeSlug"),
                "routeName" => entry.get("routeName"),
                "rideName" => _ride.get("rideName"),
                "groupName" => entry.get("groupName"),
                "startDateTime" => _ride.get("startDateTime"),
                "distance" => entry.get("distance"),
                "elevationGain" => entry.get("elevationGain"),
                "startLat" => entry.get("startLat"),
                "startLon" => entry.get("startLon"),
            };

            var detailView = new RouteDetailView(routeDict);
            var detailDelegate = new RouteDetailDelegate(detailView, _apiClient, routeDict);
            WatchUi.pushView(detailView, detailDelegate, WatchUi.SLIDE_LEFT);
        }
    }

    function onBack() {
        WatchUi.popView(WatchUi.SLIDE_RIGHT);
    }
}
