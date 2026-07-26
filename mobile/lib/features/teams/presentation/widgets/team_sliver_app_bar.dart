import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../../core/widgets/authenticated_image.dart';

/// Bandeau d'équipe rétractable.
///
/// Il ne reste qu'un seul appelant, `AdsPage`, et il disparaîtra avec la
/// refonte de cet écran au lot 5 : l'équipe porte désormais son propre en-tête
/// dans `TeamHomePage`.
///
/// F-DE-1 : la hauteur déployée passe de 160 à 120 px. Le titre est ici posé
/// sur un dégradé de marque et non sur une photo — le contraste est donc
/// maîtrisé, il n'y a pas de voile à poser —, mais 160 px de bandeau sous
/// l'en-tête de section rendaient la première annonce invisible à l'ouverture.
class TeamSliverAppBar extends StatelessWidget {
  final TeamDetailDto team;

  const TeamSliverAppBar({super.key, required this.team});

  @override
  Widget build(BuildContext context) {
    return SliverAppBar(
      expandedHeight: 120,
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
