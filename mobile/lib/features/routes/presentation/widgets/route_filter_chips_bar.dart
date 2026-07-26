import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/utils/formatters.dart';
import '../../domain/route_filters.dart';
import 'route_filter_sheet.dart';

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
    final theme = Theme.of(context);
    final units = ref.watch(unitSystemProvider);
    final active = filters.activeFields;
    final inactive = RouteFilterField.values
        .where((f) => f != RouteFilterField.search && !active.contains(f))
        .toList();

    return SizedBox(
      height: 40,
      child: ListView(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        children: [
          ActionChip(
            avatar: Icon(
              filters.sortDir == SortDirection.asc
                  ? Icons.arrow_upward
                  : Icons.arrow_downward,
              size: 16,
            ),
            label: Text(AppFormatters.routeSortByName(filters.sortBy)),
            onPressed: () => _pickSort(context),
          ),
          for (final field in active) ...[
            const SizedBox(width: 8),
            InputChip(
              label: Text(
                AppFormatters.filterChip(filters, field, units) ?? '',
              ),
              selected: true,
              showCheckmark: false,
              onDeleted: () => onChanged(filters.without(field)),
              onPressed: onOpenFilters,
            ),
          ],
          for (final field in inactive) ...[
            const SizedBox(width: 8),
            ActionChip(
              label: Text(AppFormatters.filterFieldName(field)),
              labelStyle: theme.textTheme.labelLarge?.copyWith(
                color: theme.colorScheme.outline,
              ),
              backgroundColor: Colors.transparent,
              side: BorderSide(color: theme.colorScheme.outlineVariant),
              onPressed: onOpenFilters,
            ),
          ],
        ],
      ),
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
