import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

/// Main shell widget providing bottom navigation
class MainShell extends ConsumerStatefulWidget {
  final Widget child;
  final GoRouterState state;

  const MainShell({
    super.key,
    required this.child,
    required this.state,
  });

  @override
  ConsumerState<MainShell> createState() => _MainShellState();
}

class _MainShellState extends ConsumerState<MainShell> {
  int _currentIndex = 0;

  static const _tabs = [
    '/home',
    '/teams',
    '/calendar',
    '/profile',
  ];

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
    int newIndex = 0;

    if (location.startsWith('/teams')) {
      newIndex = 1;
    } else if (location.startsWith('/calendar')) {
      newIndex = 2;
    } else if (location.startsWith('/profile')) {
      newIndex = 3;
    }

    if (newIndex != _currentIndex) {
      setState(() => _currentIndex = newIndex);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: widget.child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          if (index != _currentIndex) {
            context.go(_tabs[index]);
          }
        },
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: 'Accueil',
          ),
          NavigationDestination(
            icon: Icon(Icons.group_outlined),
            selectedIcon: Icon(Icons.group),
            label: 'Équipes',
          ),
          NavigationDestination(
            icon: Icon(Icons.calendar_today_outlined),
            selectedIcon: Icon(Icons.calendar_today),
            label: 'Calendrier',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outlined),
            selectedIcon: Icon(Icons.person),
            label: 'Profil',
          ),
        ],
      ),
    );
  }
}
