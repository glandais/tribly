import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import 'breakpoints.dart';
import 'navigation_destination.dart';

/// Adaptive scaffold that switches between bottom navigation and navigation rail
/// based on window width.
///
/// - Compact (< 600): Bottom NavigationBar
/// - Medium (600-840): NavigationRail with icons and labels
/// - Expanded (>= 840): Extended NavigationRail
class AdaptiveScaffold extends StatelessWidget {
  final Widget child;
  final int selectedIndex;
  final ValueChanged<int> onDestinationSelected;

  const AdaptiveScaffold({
    super.key,
    required this.child,
    required this.selectedIndex,
    required this.onDestinationSelected,
  });

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final sizeClass = Breakpoints.getWindowSizeClass(width);

    return switch (sizeClass) {
      WindowSizeClass.compact => _buildCompactLayout(context),
      WindowSizeClass.medium => _buildMediumLayout(context, extended: false),
      WindowSizeClass.expanded => _buildMediumLayout(context, extended: true),
    };
  }

  /// Compact layout with bottom navigation bar.
  Widget _buildCompactLayout(BuildContext context) {
    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: selectedIndex,
        onDestinationSelected: onDestinationSelected,
        destinations: kAppDestinations
            .map(
              (dest) => NavigationDestination(
                icon: Icon(dest.icon),
                selectedIcon: Icon(dest.selectedIcon),
                label: dest.label.tr(),
              ),
            )
            .toList(),
      ),
    );
  }

  /// Medium/Expanded layout with navigation rail.
  Widget _buildMediumLayout(BuildContext context, {required bool extended}) {
    final theme = Theme.of(context);

    return Scaffold(
      body: Row(
        children: [
          NavigationRail(
            selectedIndex: selectedIndex,
            onDestinationSelected: onDestinationSelected,
            extended: extended,
            labelType:
                extended ? NavigationRailLabelType.none : NavigationRailLabelType.all,
            backgroundColor: theme.colorScheme.surface,
            destinations: kAppDestinations
                .map(
                  (dest) => NavigationRailDestination(
                    icon: Icon(dest.icon),
                    selectedIcon: Icon(dest.selectedIcon),
                    label: Text(dest.label.tr()),
                  ),
                )
                .toList(),
          ),
          const VerticalDivider(thickness: 1, width: 1),
          Expanded(child: child),
        ],
      ),
    );
  }
}
