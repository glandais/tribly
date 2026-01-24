using Toybox.WatchUi;
using Toybox.Graphics;

/**
 * Main route list view.
 * Displays available routes from user's Tribly teams.
 */
class TriblyView extends WatchUi.View {
    private var _apiClient;
    private var _routes;
    private var _selectedIndex = 0;
    private var _isLoading = true;
    private var _error;

    function initialize(apiClient) {
        View.initialize();
        _apiClient = apiClient;
        _routes = null;
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

        if (routes != null) {
            _routes = routes;
            _selectedIndex = 0;
            _error = null;
        } else {
            _routes = null;
            _error = WatchUi.loadResource(Rez.Strings.ConnectionError);
        }

        WatchUi.requestUpdate();
    }

    function getRoutes() {
        return _routes;
    }

    function getSelectedIndex() {
        return _selectedIndex;
    }

    function setSelectedIndex(index) {
        if (_routes != null && index >= 0 && index < _routes.size()) {
            _selectedIndex = index;
            WatchUi.requestUpdate();
        }
    }

    function getSelectedRoute() {
        if (_routes != null && _selectedIndex < _routes.size()) {
            return _routes[_selectedIndex];
        }
        return null;
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
                Graphics.TEXT_JUSTIFY_CENTER
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

        if (_routes == null || _routes.size() == 0) {
            // Show no routes message
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.drawText(
                centerX,
                centerY,
                Graphics.FONT_SMALL,
                WatchUi.loadResource(Rez.Strings.NoRoutes),
                Graphics.TEXT_JUSTIFY_CENTER
            );
            return;
        }

        // Draw route list
        drawRouteList(dc);
    }

    private function drawRouteList(dc) {
        var width = dc.getWidth();
        var height = dc.getHeight();
        var routes = _routes;
        var itemHeight = 70;
        var startY = 10;

        // Calculate visible range
        var visibleCount = (height - 20) / itemHeight;
        var startIndex = _selectedIndex - (visibleCount / 2);
        if (startIndex < 0) {
            startIndex = 0;
        }
        var endIndex = startIndex + visibleCount;
        if (endIndex > routes.size()) {
            endIndex = routes.size();
            startIndex = endIndex - visibleCount;
            if (startIndex < 0) {
                startIndex = 0;
            }
        }

        for (var i = startIndex; i < endIndex; i++) {
            var route = routes[i];
            var y = startY + ((i - startIndex) * itemHeight);
            var isSelected = (i == _selectedIndex);

            drawRouteItem(dc, route, y, isSelected);
        }

        // Draw scroll indicators
        if (startIndex > 0) {
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.fillPolygon([[width/2 - 10, 5], [width/2 + 10, 5], [width/2, 0]]);
        }
        if (endIndex < routes.size()) {
            dc.setColor(Graphics.COLOR_LT_GRAY, Graphics.COLOR_TRANSPARENT);
            dc.fillPolygon([[width/2 - 10, height - 5], [width/2 + 10, height - 5], [width/2, height]]);
        }
    }

    private function drawRouteItem(dc, route, y, isSelected) {
        var width = dc.getWidth();
        var padding = 10;

        // Draw selection highlight
        if (isSelected) {
            dc.setColor(Graphics.COLOR_BLUE, Graphics.COLOR_BLUE);
            dc.fillRectangle(0, y, width, 65);
        }

        // Get route data
        var name = route.get("name");
        var label = route.get("label");
        var distance = route.get("distance");
        var elevationGain = route.get("elevationGain");

        // Draw label if present, otherwise name
        var title = (label != null && label.length() > 0) ? label : name;
        dc.setColor(isSelected ? Graphics.COLOR_WHITE : Graphics.COLOR_WHITE, Graphics.COLOR_TRANSPARENT);
        dc.drawText(
            padding,
            y + 5,
            Graphics.FONT_SMALL,
            title != null ? title : WatchUi.loadResource(Rez.Strings.Unknown),
            Graphics.TEXT_JUSTIFY_LEFT
        );

        // Draw stats on second line
        var distanceKm = (distance != null) ? (distance / 1000.0).format("%.1f") + WatchUi.loadResource(Rez.Strings.UnitKm) : "";
        var elevation = (elevationGain != null) ? elevationGain.format("%.0f") + WatchUi.loadResource(Rez.Strings.UnitM) : "";
        var stats = distanceKm + " | " + elevation + " " + WatchUi.loadResource(Rez.Strings.UnitDPlus);

        dc.setColor(isSelected ? Graphics.COLOR_LT_GRAY : Graphics.COLOR_DK_GRAY, Graphics.COLOR_TRANSPARENT);
        dc.drawText(
            padding,
            y + 35,
            Graphics.FONT_TINY,
            stats,
            Graphics.TEXT_JUSTIFY_LEFT
        );
    }
}
