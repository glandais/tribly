import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/pdl.dart';

import 'module.dart';

/// The « Localisation » section of an ad: a blurred sector on a still map, never a pin.
final class AdLocation extends Module {
  AdLocation(super.$);

  /// Waits for the page of the ad named [name] — another ad's page may still be sliding away
  /// under it.
  Future<void> waitUntilAdIsShown(String name) async {
    final deadline = DateTime.now().add(const Duration(seconds: 15));
    while (!shows(keys.ad.title, name) ||
        $(keys.ad.title).evaluate().length != 1) {
      if (DateTime.now().isAfter(deadline)) {
        throw TestFailure('the ad « $name » is not shown');
      }
      await $.pump(const Duration(milliseconds: 200));
    }
  }

  /// Scrolls down to « Annonceur », the section every ad ends with: the page is then built down
  /// to it, and a location section above it would be in the tree.
  Future<void> scrollToSeller() async {
    await scrolledIntoView(keys.ad.seller);
  }

  Future<void> waitUntilMapIsShown() async {
    await (await scrolledTo(keys.ad.locationCaption)).waitUntilVisible();
    await $(keys.ad.locationMap).waitUntilVisible();
  }

  bool get showsSection => isShown(keys.ad.location);

  bool get showsMap => isShown(keys.ad.locationMap);

  bool get showsSector => isShown(keys.ad.locationSector);

  bool captionSays(String text) => shows(keys.ad.locationCaption, text);

  bool descriptionSays(String text) => shows(keys.ad.locationDescription, text);

  PdlMap get _map => $(keys.ad.locationMap).evaluate().single.widget as PdlMap;

  /// Whether the map draws any point of its own — a waypoint, a start or an end marker. The
  /// sector is an overlay of the page, not a map feature, so a map with a pin says true.
  bool get mapHasAPin =>
      _map.waypoints.isNotEmpty ||
      _map.start != null ||
      _map.end != null ||
      _map.kmMarkers.isNotEmpty ||
      _map.overlays.isNotEmpty;

  /// Where the map is centred, `[lon, lat]`.
  List<double> get mapCentre {
    final PdlMapPoint centre = _map.initialCenter!;
    return [centre.lon, centre.lat];
  }
}
