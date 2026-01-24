using Toybox.WatchUi;
using Toybox.System;

/**
 * Input delegate for the route list Menu2.
 * Handles item selection and navigation.
 */
class RouteMenuDelegate extends WatchUi.Menu2InputDelegate {
    private var _routes;
    private var _apiClient;

    function initialize(routes, apiClient) {
        Menu2InputDelegate.initialize();
        _routes = routes;
        _apiClient = apiClient;
    }

    /**
     * Handle item selection - open route detail view.
     */
    function onSelect(item) {
        var index = item.getId();
        if (index != null && index < _routes.size()) {
            var route = _routes[index];
            var detailView = new RouteDetailView(route);
            var detailDelegate = new RouteDetailDelegate(detailView, _apiClient, route);
            WatchUi.pushView(detailView, detailDelegate, WatchUi.SLIDE_LEFT);
        }
    }

    /**
     * Handle back button - exit app.
     */
    function onBack() {
        System.exit();
    }
}
