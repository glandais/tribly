import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../domain/ad_filters.dart';
import 'ad_sort_sheet.dart';

/// La barre d'outils de la rubrique Annonces, épinglée sous l'en-tête.
///
/// Recherche débouncée, puis une rangée de chips dont **la chip de tri vient
/// en premier** : elle porte le seul réglage qui ne se devine pas de la liste
/// elle-même. Suivent les quatre chips de type, **exclusives** — un choix, pas
/// une combinaison, parce que `adType` est un paramètre unique au contrat.
class AdsToolbar extends StatelessWidget {
  const AdsToolbar({super.key, required this.filters, required this.onChanged});

  final AdFilters filters;
  final ValueChanged<AdFilters> onChanged;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: PdlSpacing.section),
          child: PdlSearchField(
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
              label: 'ads.list.allTypes'.tr(),
              selected: filters.adType == null,
              onTap: () => onChanged(filters.copyWith(clearAdType: true)),
            ),
            for (final AdType type in AdType.$valuesDefined)
              PdlChip(
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
