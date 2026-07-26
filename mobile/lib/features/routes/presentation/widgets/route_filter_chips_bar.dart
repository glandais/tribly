import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../domain/route_filters.dart';
import '../../domain/route_filter_labels.dart';
import 'route_filter_sheet.dart';

/// The filter state made visible above the list, one chip per constraint.
///
/// On mobile this replaces the web's stack of selects: what is applied is
/// readable at a glance and removable in one tap. Sort is a chip like the
/// others, in first position.
///
/// Elle n'existe **qu'en vue Liste** : en vue Carte, le compteur du
/// [PdlFilterButton] prend le relais (§3.1).
class RouteFilterChipsBar extends ConsumerWidget {
  final RouteFilters filters;
  final ValueChanged<RouteFilters> onChanged;

  const RouteFilterChipsBar({
    super.key,
    required this.filters,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final UnitSystem units = ref.watch(unitSystemProvider);
    final List<RouteFilterField> active = filters.activeFields;
    final List<RouteFilterField> inactive = RouteFilterField.values
        .where(
          (RouteFilterField f) =>
              f != RouteFilterField.search &&
              // La proximité ne s'arme pas depuis la feuille de filtres mais
              // depuis « Autour de moi » : proposer une chip inerte ici serait
              // une icône-action sans effet.
              f != RouteFilterField.proximity &&
              !active.contains(f),
        )
        .toList();

    // F-DE-3 : la rangée figeait sa hauteur à 40 px et la 4ᵉ chip se faisait
    // couper net par le bord droit, sans rien qui dise qu'il en restait.
    // `PdlChipRow` mesure sa hauteur — elle suit donc l'agrandissement
    // typographique — et fond les 28 derniers pixels, ce qui annonce le
    // débordement au lieu de le subir.
    return PdlChipRow(
      children: <Widget>[
        PdlChip(
          sortStyle: true,
          icon: filters.sortDir == SortDirection.asc
              ? PdlIcons.chevronUp
              : PdlIcons.chevronDown,
          label: RouteFilterLabels.routeSortByName(filters.sortBy),
          onTap: () => _pickSort(context),
        ),
        for (final RouteFilterField field in active)
          PdlChip(
            label: RouteFilterLabels.filterChip(filters, field, units) ?? '',
            selected: true,
            onTap: () => _openFilters(context),
            onRemoved: () => onChanged(filters.without(field)),
          ),
        for (final RouteFilterField field in inactive)
          PdlChip(
            label: RouteFilterLabels.filterFieldName(field),
            onTap: () => _openFilters(context),
          ),
      ],
    );
  }

  Future<void> _openFilters(BuildContext context) async {
    final RouteFilters? result = await showRouteFilterSheet(
      context,
      filters: filters,
    );
    if (result != null) onChanged(result);
  }

  Future<void> _pickSort(BuildContext context) async {
    final ({RouteSortBy by, SortDirection dir})? result =
        await showRouteSortSheet(
          context,
          sortBy: filters.sortBy,
          sortDir: filters.sortDir,
        );
    if (result != null) {
      onChanged(filters.copyWith(sortBy: result.by, sortDir: result.dir));
    }
  }
}
