import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';

/// Le vocabulaire de tags d'une équipe pour un type de contenu (ledger
/// `MOB-39`).
///
/// Lecture seule : l'app affiche et filtre, elle n'étiquette pas et
/// n'administre pas le vocabulaire (plan des tags, D20). Une liste vide est la
/// réponse attendue pour la plupart des équipes, et elle **cache** le filtre —
/// une puce qui n'ouvre qu'une feuille vide est une action sans effet.
///
/// Un échec se replie aussi sur « pas de filtre » côté écran : le filtre par
/// tag est un raffinement, pas une raison de mettre la liste en erreur.
final teamTagsProvider = FutureProvider.autoDispose
    .family<List<TagWithUsageDto>, ({String teamSlug, TagTarget type})>((
      Ref ref,
      ({String teamSlug, TagTarget type}) key,
    ) {
      return ref
          .watch(tagsClientProvider)
          .listTeamTags(teamSlug: key.teamSlug, type: key.type);
    });

/// Les tags de [key] une fois chargés, ou une liste vide tant qu'ils ne le
/// sont pas — ou s'ils ne le seront pas. Le raccourci des barres de filtres.
List<TagWithUsageDto> teamTagsOrEmpty(
  WidgetRef ref,
  ({String teamSlug, TagTarget type}) key,
) => ref.watch(teamTagsProvider(key)).value ?? const <TagWithUsageDto>[];
