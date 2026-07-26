import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../api/generated/export.dart';
import '../../config/paths.dart';
import '../pdl/pdl_team_line.dart';

/// Ligne d'appartenance d'une publication : logo, nom d'équipe, chevron.
///
/// Ce n'est plus qu'une **façade** : le rendu appartient à [PdlTeamLine]
/// (B3). Ce qui reste ici est ce que `core/pdl` ne peut pas connaître — le DTO
/// et l'itinéraire de navigation. C'est la frontière du module : tout widget
/// qui prend un DTO vit hors de `core/pdl`, sans préfixe `Pdl`.
class TeamBanner extends StatelessWidget {
  final TeamPublicationDto team;

  const TeamBanner({super.key, required this.team});

  @override
  Widget build(BuildContext context) {
    return PdlTeamLine(
      label: team.name,
      onTap: () => context.push(Paths.team(team.slug)),
    );
  }
}
