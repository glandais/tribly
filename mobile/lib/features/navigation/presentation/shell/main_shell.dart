import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../core/adaptive/adaptive.dart';

/// Main shell widget providing adaptive navigation.
///
/// - Compact screens: Bottom NavigationBar
/// - Medium screens: Side NavigationRail
/// - Expanded screens: Extended NavigationRail
class MainShell extends ConsumerStatefulWidget {
  final Widget child;
  final GoRouterState state;

  const MainShell({super.key, required this.child, required this.state});

  @override
  ConsumerState<MainShell> createState() => _MainShellState();
}

class _MainShellState extends ConsumerState<MainShell> {
  int _currentIndex = 0;

  @override
  void didUpdateWidget(covariant MainShell oldWidget) {
    super.didUpdateWidget(oldWidget);
    _updateIndex();
  }

  @override
  void initState() {
    super.initState();
    _updateIndex();
  }

  void _updateIndex() {
    final location = widget.state.matchedLocation;
    final newIndex = getDestinationIndex(location);

    if (newIndex != _currentIndex) {
      setState(() => _currentIndex = newIndex);
    }
  }

  void _onDestinationSelected(int index) {
    if (index != _currentIndex) {
      context.go(kAppDestinations[index].currentPath);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AdaptiveScaffold(
      selectedIndex: _currentIndex,
      onDestinationSelected: _onDestinationSelected,
      child: widget.child,
    );
  }
}
