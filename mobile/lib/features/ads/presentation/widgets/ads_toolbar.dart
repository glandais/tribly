import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../keys.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../tags/presentation/tag_filter.dart';
import '../../../tags/providers/team_tags_provider.dart';
import '../../domain/ad_filters.dart';
import 'ad_sort_sheet.dart';

/// La barre d'outils de la rubrique Annonces, épinglée sous l'en-tête.
///
/// Recherche débouncée, puis une rangée de chips dont **la chip de tri vient
/// en premier** : elle porte le seul réglage qui ne se devine pas de la liste
/// elle-même. Suivent les quatre chips de type, **exclusives** — un choix, pas
/// une combinaison, parce que `adType` est un paramètre unique au contrat.
///
/// La chip « Tags » ferme la rangée, et seulement quand l'équipe a des tags
/// d'annonce (ledger `MOB-39`) : elle combine, elle, plusieurs choix en OU.
class AdsToolbar extends ConsumerWidget {
  const AdsToolbar({super.key, required this.filters, required this.onChanged});

  final AdFilters filters;
  final ValueChanged<AdFilters> onChanged;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final List<TagWithUsageDto> vocabulary = teamTagsOrEmpty(ref, (
      teamSlug: filters.teamSlug,
      type: TagTarget.ad,
    ));

    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: PdlSpacing.section),
          child: PdlSearchField(
            key: keys.adsList.searchField,
            value: filters.search,
            hintText: 'ads.list.searchPlaceholder'.tr(),
            clearTooltip: 'common.clearSearch'.tr(),
            onChanged: (String? value) => onChanged(
              value == null
                  ? filters.copyWith(clearSearch: true)
                  : filters.copyWith(search: value),
            ),
          ),
        ),
        const SizedBox(height: PdlSpacing.chipGap),
        PdlChipRow(
          children: <Widget>[
            PdlChip(
              label: AdSortOption.of(filters).label,
              icon: PdlIcons.sort,
              sortStyle: true,
              onTap: () => _openSortSheet(context),
            ),
            PdlChip(
              key: keys.adsList.typeChip(null),
              label: 'ads.list.allTypes'.tr(),
              selected: filters.adType == null,
              onTap: () => onChanged(filters.copyWith(clearAdType: true)),
            ),
            for (final AdType type in AdType.$valuesDefined)
              PdlChip(
                key: keys.adsList.typeChip(type.json),
                label: 'ads.adType.${type.json}'.tr(),
                selected: filters.adType == type,
                // Retoucher la chip déjà choisie la lève : sans cela, revenir
                // à « Tous » demanderait de viser une autre chip que celle
                // qu'on a sous les yeux.
                onTap: () => onChanged(
                  filters.adType == type
                      ? filters.copyWith(clearAdType: true)
                      : filters.copyWith(adType: type),
                ),
              ),
            if (vocabulary.isNotEmpty)
              TagFilterChip(
                vocabulary: vocabulary,
                selected: filters.tagIds,
                onChanged: (List<String> ids) =>
                    onChanged(filters.copyWith(tagIds: ids)),
              ),
          ],
        ),
      ],
    );
  }

  Future<void> _openSortSheet(BuildContext context) async {
    final AdFilters? next = await showAdSortSheet(context, filters: filters);
    if (next != null) onChanged(next);
  }
}
