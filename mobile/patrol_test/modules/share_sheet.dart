import 'package:flutter_test/flutter_test.dart';
import 'package:patrol/patrol.dart';

import 'module.dart';

/// The system share sheet (`SharePlus`), which the app opens on a file it downloaded — a GPX or
/// FIT export. It is native: read through Patrol's native UI tree, iOS only for now.
final class ShareSheet extends Module {
  ShareSheet(super.$);

  /// Waits until the share sheet is up — its activity list, whatever the device's language —
  /// and returns whether it shows [text] anywhere (label, title, value or identifier): the
  /// caption under the file, « GPX • 17 ko » for instance.
  Future<bool> waitUntilShownAndCheck(
    String text, {
    Duration timeout = const Duration(seconds: 20),
  }) async {
    final deadline = DateTime.now().add(timeout);
    while (true) {
      final roots = (await $.platform.ios.getNativeViews(null)).roots;
      if (roots.any((view) => _contains(view, _activityList))) {
        return roots.any((view) => _contains(view, text));
      }
      if (DateTime.now().isAfter(deadline)) {
        final dump = StringBuffer();
        for (final view in roots) {
          _dump(view, 0, dump);
        }
        throw TestFailure('no share sheet after $timeout; native tree:\n$dump');
      }
      await Future<void>.delayed(const Duration(milliseconds: 500));
    }
  }

  /// The identifier of the sheet's list of activities (UIKit's, not the app's).
  static const _activityList = 'ActivityListView';

  static bool _contains(IOSNativeView view, String text) =>
      view.label.contains(text) ||
      view.title.contains(text) ||
      (view.value?.contains(text) ?? false) ||
      view.identifier.contains(text) ||
      view.children.any((child) => _contains(child, text));

  static void _dump(IOSNativeView view, int depth, StringBuffer out) {
    out.writeln(
      '${'  ' * depth}${view.elementType} id=${view.identifier} '
      'label=${view.label} title=${view.title} value=${view.value}',
    );
    for (final child in view.children) {
      _dump(child, depth + 1, out);
    }
  }
}
