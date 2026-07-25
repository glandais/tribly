import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../../core/widgets/authenticated_image.dart';

/// Reusable collapsible SliverAppBar for team pages.
///
/// Shows team name, logo, and a back button. Collapses on scroll
/// to a pinned title bar.
class TeamSliverAppBar extends StatelessWidget {
  final TeamDetailDto team;

  const TeamSliverAppBar({super.key, required this.team});

  @override
  Widget build(BuildContext context) {
    return SliverAppBar(
      expandedHeight: 160,
      pinned: true,
      leading: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () => context.go(Paths.teams()),
      ),
      flexibleSpace: FlexibleSpaceBar(
        title: Text(team.name, style: const TextStyle(fontSize: 16)),
        background: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [
                Theme.of(context).colorScheme.primary,
                Theme.of(context).colorScheme.primaryContainer,
              ],
            ),
          ),
          child: Center(
            child: Padding(
              padding: const EdgeInsets.only(bottom: 24),
              child: Hero(
                tag: 'team-logo-${team.slug}',
                child: AuthenticatedCircleAvatar(
                  imageUrl: team.about.assets.logo?.url,
                  fallbackText: team.name.safeFirstUpper(),
                  radius: 36,
                  fontSize: 28,
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
