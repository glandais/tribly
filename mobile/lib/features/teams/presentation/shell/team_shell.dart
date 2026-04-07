import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../pages/team_detail_page.dart';
import 'team_navigation_destination.dart';

/// Team shell widget providing bottom navigation for team sections.
///
/// Replaces the main app navigation when inside a team.
/// Shows dynamic tabs based on team configuration and membership.
class TeamShell extends ConsumerStatefulWidget {
  final Widget child;
  final GoRouterState state;

  const TeamShell({
    super.key,
    required this.child,
    required this.state,
  });

  @override
  ConsumerState<TeamShell> createState() => _TeamShellState();
}

class _TeamShellState extends ConsumerState<TeamShell> {
  String get _teamSlug => widget.state.pathParameters['teamSlug'] ?? '';

  @override
  Widget build(BuildContext context) {
    final teamAsync = ref.watch(teamDetailProvider(_teamSlug));

    return teamAsync.when(
      data: (team) => _TeamShellContent(
        team: team,
        state: widget.state,
        child: widget.child,
      ),
      loading: () => Scaffold(
        appBar: AppBar(),
        body: const Center(child: CircularProgressIndicator()),
      ),
      error: (error, stack) => Scaffold(
        appBar: AppBar(),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.error_outline,
                  size: 48, color: Theme.of(context).colorScheme.error),
              const SizedBox(height: 16),
              Text(getErrorMessage(error)),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () =>
                    ref.invalidate(teamDetailProvider(_teamSlug)),
                child: Text('common.retry'.tr()),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _TeamShellContent extends StatefulWidget {
  final TeamDetailDto team;
  final GoRouterState state;
  final Widget child;

  const _TeamShellContent({
    required this.team,
    required this.state,
    required this.child,
  });

  @override
  State<_TeamShellContent> createState() => _TeamShellContentState();
}

class _TeamShellContentState extends State<_TeamShellContent> {
  late List<AppDestination> _destinations;
  int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    _destinations = buildTeamDestinations(widget.team);
    _updateIndex();
  }

  @override
  void didUpdateWidget(covariant _TeamShellContent oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.team != widget.team) {
      _destinations = buildTeamDestinations(widget.team);
    }
    _updateIndex();
  }

  void _updateIndex() {
    final location = widget.state.matchedLocation;
    final newIndex = getTeamDestinationIndex(location, _destinations);

    if (newIndex != _currentIndex) {
      setState(() => _currentIndex = newIndex);
    }
  }

  void _onDestinationSelected(int index) {
    if (index != _currentIndex) {
      context.go(_destinations[index].path);
    }
  }

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final sizeClass = Breakpoints.getWindowSizeClass(width);

    return switch (sizeClass) {
      WindowSizeClass.compact => _buildCompactLayout(context),
      WindowSizeClass.medium => _buildRailLayout(context, extended: false),
      WindowSizeClass.expanded => _buildRailLayout(context, extended: true),
    };
  }

  Widget _buildCompactLayout(BuildContext context) {
    return Scaffold(
      body: widget.child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: _onDestinationSelected,
        destinations: _destinations
            .map((dest) => NavigationDestination(
                  icon: Icon(dest.icon),
                  selectedIcon: Icon(dest.selectedIcon),
                  label: dest.label.tr(),
                ))
            .toList(),
      ),
    );
  }

  Widget _buildRailLayout(BuildContext context, {required bool extended}) {
    final theme = Theme.of(context);

    return Scaffold(
      body: Row(
        children: [
          NavigationRail(
            selectedIndex: _currentIndex,
            onDestinationSelected: _onDestinationSelected,
            extended: extended,
            labelType: extended
                ? NavigationRailLabelType.none
                : NavigationRailLabelType.all,
            backgroundColor: theme.colorScheme.surface,
            leading: IconButton(
              icon: const Icon(Icons.arrow_back),
              onPressed: () => context.go('/teams'),
            ),
            destinations: _destinations
                .map((dest) => NavigationRailDestination(
                      icon: Icon(dest.icon),
                      selectedIcon: Icon(dest.selectedIcon),
                      label: Text(dest.label.tr()),
                    ))
                .toList(),
          ),
          const VerticalDivider(thickness: 1, width: 1),
          Expanded(child: widget.child),
        ],
      ),
    );
  }
}
