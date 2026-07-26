import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../domain/route_filters.dart';
import 'route_filter_sheet.dart';
import '../../domain/route_filter_labels.dart';

/// The filter state made visible above the list, one chip per constraint.
///
/// On mobile this replaces the web's stack of selects: what is applied is
/// readable at a glance and removable in one tap. Sort is a chip like the
/// others, in first position.
class RouteFilterChipsBar extends ConsumerWidget {
  final RouteFilters filters;
  final ValueChanged<RouteFilters> onChanged;

  /// Opens the full filter sheet — used by the chips for fields that are not
  /// set yet.
  final VoidCallback onOpenFilters;

  const RouteFilterChipsBar({
    super.key,
    required this.filters,
    required this.onChanged,
    required this.onOpenFilters,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final units = ref.watch(unitSystemProvider);
    final active = filters.activeFields;
    final inactive = RouteFilterField.values
        .where((f) => f != RouteFilterField.search && !active.contains(f))
        .toList();

    // F-DE-3 : la rangée figeait sa hauteur à 40 px et la 4ᵉ chip se faisait
    // couper net par le bord droit, sans rien qui dise qu'il en restait.
    // `PdlChipRow` mesure sa hauteur — elle suit donc l'agrandissement
    // typographique — et fond les 28 derniers pixels, ce qui annonce le
    // débordement au lieu de le subir.
    return PdlChipRow(
      children: [
        PdlChip(
          sortStyle: true,
          icon: filters.sortDir == SortDirection.asc
              ? Icons.arrow_upward
              : Icons.arrow_downward,
          label: RouteFilterLabels.routeSortByName(filters.sortBy),
          onTap: () => _pickSort(context),
        ),
        for (final field in active)
          PdlChip(
            label: RouteFilterLabels.filterChip(filters, field, units) ?? '',
            selected: true,
            onTap: onOpenFilters,
            onRemoved: () => onChanged(filters.without(field)),
          ),
        for (final field in inactive)
          PdlChip(
            label: RouteFilterLabels.filterFieldName(field),
            onTap: onOpenFilters,
          ),
      ],
    );
  }

  Future<void> _pickSort(BuildContext context) async {
    final result = await showRouteSortSheet(
      context,
      sortBy: filters.sortBy,
      sortDir: filters.sortDir,
    );
    if (result != null) {
      onChanged(filters.copyWith(sortBy: result.by, sortDir: result.dir));
    }
  }
}
