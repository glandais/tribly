/// Window size breakpoints for responsive layouts.
///
/// Based on Material 3 window size classes:
/// - Compact: width < 600 (phones)
/// - Medium: 600 <= width < 840 (small tablets, portrait)
/// - Expanded: width >= 840 (large tablets, desktops)
enum WindowSizeClass { compact, medium, expanded }

/// Breakpoint utilities for adaptive layouts.
class Breakpoints {
  Breakpoints._();

  /// Maximum width for compact window size class (phones).
  static const double compactMaxWidth = 600;

  /// Maximum width for medium window size class (small tablets).
  static const double mediumMaxWidth = 840;

  /// Returns the window size class for the given width.
  static WindowSizeClass getWindowSizeClass(double width) {
    if (width < compactMaxWidth) return WindowSizeClass.compact;
    if (width < mediumMaxWidth) return WindowSizeClass.medium;
    return WindowSizeClass.expanded;
  }

  /// Returns the number of grid columns for the given size class.
  static int gridColumns(WindowSizeClass sizeClass) => switch (sizeClass) {
        WindowSizeClass.compact => 1,
        WindowSizeClass.medium => 2,
        WindowSizeClass.expanded => 3,
      };

  /// Returns the maximum content width for the given size class.
  /// Returns null for compact (full width).
  static double? contentMaxWidth(WindowSizeClass sizeClass) =>
      switch (sizeClass) {
        WindowSizeClass.compact => null,
        WindowSizeClass.medium => 720,
        WindowSizeClass.expanded => 960,
      };

  /// Returns whether navigation rail should be extended (show labels).
  static bool shouldExtendRail(WindowSizeClass sizeClass) =>
      sizeClass == WindowSizeClass.expanded;
}
