import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:patrol/patrol.dart';

export 'package:pedalons/keys.dart';

abstract class Module {
  Module(this.$);

  final PatrolIntegrationTester $;

  /// The widget of [key], scrolled into view in the screen's vertical list.
  ///
  /// Patrol's own `scrollTo` drives the first `Scrollable` it meets, and most screens start with a
  /// horizontal one (a chip row, a carousel): the vertical list is named here instead.
  Future<PatrolFinder> scrolledTo(Key key) =>
      $(key).scrollTo(view: _verticalScrollable.first);

  /// Whether a widget of [key] is currently on screen — for the absence checks, made once the
  /// screen is known to be loaded.
  bool isShown(Key key) => $(key).exists;

  /// Waits until no widget of [key] is left in the tree.
  Future<void> waitUntilGone(
    Key key, {
    Duration timeout = const Duration(seconds: 10),
  }) async {
    final deadline = DateTime.now().add(timeout);
    while ($(key).exists) {
      if (DateTime.now().isAfter(deadline)) {
        throw TestFailure('$key still shown after $timeout');
      }
      await $.pump(const Duration(milliseconds: 200));
    }
  }

  /// Waits until one of [keys] is on screen, and returns it.
  Future<Key> waitUntilAnyIsShown(
    List<Key> keys, {
    Duration timeout = const Duration(seconds: 15),
  }) async {
    final deadline = DateTime.now().add(timeout);
    while (true) {
      for (final key in keys) {
        if ($(key).visible) return key;
      }
      if (DateTime.now().isAfter(deadline)) {
        throw TestFailure('none of $keys shown after $timeout');
      }
      await $.pump(const Duration(milliseconds: 200));
    }
  }

  /// Whether the widget of [key] shows [text] somewhere inside it.
  bool shows(Key key, String text) =>
      $(key).$(find.textContaining(text)).exists;

  static final Finder _verticalScrollable = find.byWidgetPredicate(
    (Widget widget) =>
        widget is Scrollable &&
        axisDirectionToAxis(widget.axisDirection) == Axis.vertical,
  );
}
