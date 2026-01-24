using Toybox.WatchUi;
using Toybox.System;

/**
 * Input delegate for the main route list view.
 */
class TriblyDelegate extends WatchUi.BehaviorDelegate {
    private var _view;
    private var _apiClient;

    function initialize(view, apiClient) {
        BehaviorDelegate.initialize();
        _view = view;
        _apiClient = apiClient;
    }

    function onNextPage() {
        // Scroll down
        var routes = _view.getRoutes();
        if (routes != null) {
            var newIndex = _view.getSelectedIndex() + 1;
            if (newIndex < routes.size()) {
                _view.setSelectedIndex(newIndex);
            }
        }
        return true;
    }

    function onPreviousPage() {
        // Scroll up
        var newIndex = _view.getSelectedIndex() - 1;
        if (newIndex >= 0) {
            _view.setSelectedIndex(newIndex);
        }
        return true;
    }

    function onSelect() {
        // Open route detail view
        var route = _view.getSelectedRoute();
        if (route != null) {
            var detailView = new RouteDetailView(route);
            var detailDelegate = new RouteDetailDelegate(detailView, _apiClient, route);
            WatchUi.pushView(detailView, detailDelegate, WatchUi.SLIDE_LEFT);
        }
        return true;
    }

    function onMenu() {
        // Show menu with refresh option
        var menu = new WatchUi.Menu2({:title => WatchUi.loadResource(Rez.Strings.MenuTitle)});
        menu.addItem(new WatchUi.MenuItem(
            WatchUi.loadResource(Rez.Strings.RefreshRoutes),
            null,
            "refresh",
            {}
        ));
        WatchUi.pushView(menu, new MenuDelegate(_view), WatchUi.SLIDE_LEFT);
        return true;
    }

    function onBack() {
        // Exit app
        System.exit();
    }
}

/**
 * Menu delegate for the options menu.
 */
class MenuDelegate extends WatchUi.Menu2InputDelegate {
    private var _view;

    function initialize(view) {
        Menu2InputDelegate.initialize();
        _view = view;
    }

    function onSelect(item) {
        var id = item.getId();
        if (id.equals("refresh")) {
            WatchUi.popView(WatchUi.SLIDE_RIGHT);
            _view.loadRoutes();
        }
    }
}
