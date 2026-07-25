import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../domain/route_filters.dart';
import '../../providers/route_list_provider.dart';
import 'route_filter_labels.dart';

/// Opens the filter sheet and returns the filters to apply, or null if the
/// user dismissed it.
///
/// The sheet edits a draft copy: nothing reaches the list until
/// "Voir N parcours" is tapped.
Future<RouteFilters?> showRouteFilterSheet(
  BuildContext context, {
  required String? teamSlug,
  required RouteFilters filters,
}) {
  return showModalBottomSheet<RouteFilters>(
    context: context,
    isScrollControlled: true,
    showDragHandle: true,
    useSafeArea: true,
    builder: (context) =>
        _RouteFilterSheet(teamSlug: teamSlug, initial: filters),
  );
}

/// Opens the sort picker on its own, for the sort chip above the list.
Future<({RouteSortBy by, SortDirection dir})?> showRouteSortSheet(
  BuildContext context, {
  required RouteSortBy sortBy,
  required SortDirection sortDir,
}) {
  return showModalBottomSheet<({RouteSortBy by, SortDirection dir})>(
    context: context,
    showDragHandle: true,
    useSafeArea: true,
    builder: (context) => _SortSheet(sortBy: sortBy, sortDir: sortDir),
  );
}

class _RouteFilterSheet extends ConsumerStatefulWidget {
  final String? teamSlug;
  final RouteFilters initial;

  const _RouteFilterSheet({required this.teamSlug, required this.initial});

  @override
  ConsumerState<_RouteFilterSheet> createState() => _RouteFilterSheetState();
}

class _RouteFilterSheetState extends ConsumerState<_RouteFilterSheet> {
  late RouteFilters _draft = widget.initial;

  /// Last count the server confirmed, kept on screen while the next one is in
  /// flight so the button label does not flicker on every slider move.
  int? _lastCount;

  void _update(RouteFilters next) => setState(() => _draft = next);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final countProvider = routeCountProvider((
      teamSlug: widget.teamSlug,
      filters: _draft,
    ));
    ref.listen(countProvider, (_, next) {
      final value = next.value;
      if (value != null) _lastCount = value;
    });
    final count = ref.watch(countProvider).value ?? _lastCount;

    return SafeArea(
      top: false,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(20, 0, 20, 16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    'routes.filters.title'.tr(),
                    style: theme.textTheme.titleLarge,
                  ),
                ),
                if (_draft.activeFields.isNotEmpty)
                  TextButton(
                    onPressed: () => _update(_draft.filtersCleared),
                    child: Text('routes.filters.reset'.tr()),
                  ),
              ],
            ),
            const SizedBox(height: 8),
            Flexible(
              child: SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    _RangeSection(
                      label: 'routes.filters.distance'.tr(),
                      summary: RouteFilterLabels.range(
                        _draft.minDistance,
                        _draft.maxDistance,
                        RouteFilterLabels.distance,
                      ),
                      min: _draft.minDistance,
                      max: _draft.maxDistance,
                      upperBound: kRouteDistanceMaxMeters,
                      step: kRouteDistanceStepMeters,
                      onChanged: (min, max) => _update(
                        _draft.copyWith(minDistance: min, maxDistance: max),
                      ),
                    ),
                    const SizedBox(height: 20),
                    _RangeSection(
                      label: 'routes.filters.elevationGain'.tr(),
                      summary: RouteFilterLabels.range(
                        _draft.minElevationGain,
                        _draft.maxElevationGain,
                        RouteFilterLabels.elevation,
                      ),
                      min: _draft.minElevationGain,
                      max: _draft.maxElevationGain,
                      upperBound: kRouteElevationMaxMeters,
                      step: kRouteElevationStepMeters,
                      onChanged: (min, max) => _update(
                        _draft.copyWith(
                          minElevationGain: min,
                          maxElevationGain: max,
                        ),
                      ),
                    ),
                    const SizedBox(height: 20),
                    _ChoiceSection<SurfaceType>(
                      label: 'routes.filters.surfaceType'.tr(),
                      values: SurfaceType.$valuesDefined,
                      selected: _draft.surfaceType,
                      labelOf: RouteFilterLabels.surfaceType,
                      onSelected: (value) =>
                          _update(_draft.copyWith(surfaceType: value)),
                    ),
                    const SizedBox(height: 20),
                    _ChoiceSection<Hilliness>(
                      label: 'routes.filters.hilliness'.tr(),
                      values: Hilliness.$valuesDefined,
                      selected: _draft.hilliness,
                      labelOf: RouteFilterLabels.hilliness,
                      onSelected: (value) =>
                          _update(_draft.copyWith(hilliness: value)),
                    ),
                    const SizedBox(height: 12),
                    // Wind and sort are one level down: the two filters that
                    // actually decide the list stay above the fold.
                    _NavigationRow(
                      label: 'routes.filters.windDirection'.tr(),
                      value: _draft.windDirection == null
                          ? 'routes.filters.anyFeminine'.tr()
                          : RouteFilterLabels.windDirection(
                              _draft.windDirection!,
                            ),
                      onTap: _pickWindDirection,
                    ),
                    _NavigationRow(
                      label: 'routes.filters.sort'.tr(),
                      value: _sortSummary(),
                      onTap: _pickSort,
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            FilledButton(
              onPressed: () => Navigator.of(context).pop(_draft),
              style: FilledButton.styleFrom(
                minimumSize: const Size.fromHeight(52),
              ),
              child: Text(
                count == null
                    ? 'routes.filters.applyUnknown'.tr()
                    : 'routes.filters.apply'.plural(count),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _sortSummary() {
    final arrow = _draft.sortDir == SortDirection.asc ? '↑' : '↓';
    return '${RouteFilterLabels.sortBy(_draft.sortBy)} $arrow';
  }

  Future<void> _pickWindDirection() async {
    final result = await showModalBottomSheet<_Optional<WindDirection>>(
      context: context,
      showDragHandle: true,
      useSafeArea: true,
      builder: (context) => _OptionSheet<WindDirection>(
        title: 'routes.filters.windDirection'.tr(),
        anyLabel: 'routes.filters.anyFeminine'.tr(),
        values: WindDirection.$valuesDefined,
        selected: _draft.windDirection,
        labelOf: RouteFilterLabels.windDirection,
      ),
    );
    if (result != null) _update(_draft.copyWith(windDirection: result.value));
  }

  Future<void> _pickSort() async {
    final result = await showRouteSortSheet(
      context,
      sortBy: _draft.sortBy,
      sortDir: _draft.sortDir,
    );
    if (result != null) {
      _update(_draft.copyWith(sortBy: result.by, sortDir: result.dir));
    }
  }
}

/// A min/max range over a slider, where either end sitting on its bound means
/// "unbounded" and is reported back as null.
class _RangeSection extends StatelessWidget {
  final String label;
  final String summary;
  final double? min;
  final double? max;
  final double upperBound;
  final double step;
  final void Function(double? min, double? max) onChanged;

  const _RangeSection({
    required this.label,
    required this.summary,
    required this.min,
    required this.max,
    required this.upperBound,
    required this.step,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final values = RangeValues(
      (min ?? 0).clamp(0, upperBound),
      (max ?? upperBound).clamp(0, upperBound),
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          children: [
            Expanded(child: Text(label, style: theme.textTheme.titleSmall)),
            Text(
              summary,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.outline,
              ),
            ),
          ],
        ),
        RangeSlider(
          values: values,
          min: 0,
          max: upperBound,
          divisions: (upperBound / step).round(),
          onChanged: (next) => onChanged(
            next.start <= 0 ? null : next.start,
            next.end >= upperBound ? null : next.end,
          ),
        ),
      ],
    );
  }
}

/// A row of choice chips with a leading "all" chip that clears the field.
class _ChoiceSection<T> extends StatelessWidget {
  final String label;
  final List<T> values;
  final T? selected;
  final String Function(T) labelOf;
  final ValueChanged<T?> onSelected;

  const _ChoiceSection({
    required this.label,
    required this.values,
    required this.selected,
    required this.labelOf,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: Theme.of(context).textTheme.titleSmall),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            ChoiceChip(
              label: Text('routes.filters.any'.tr()),
              selected: selected == null,
              onSelected: (_) => onSelected(null),
            ),
            for (final value in values)
              ChoiceChip(
                label: Text(labelOf(value)),
                selected: selected == value,
                onSelected: (isSelected) =>
                    onSelected(isSelected ? value : null),
              ),
          ],
        ),
      ],
    );
  }
}

class _NavigationRow extends StatelessWidget {
  final String label;
  final String value;
  final VoidCallback onTap;

  const _NavigationRow({
    required this.label,
    required this.value,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return ListTile(
      contentPadding: EdgeInsets.zero,
      shape: Border(top: BorderSide(color: theme.dividerColor)),
      title: Text(label, style: theme.textTheme.titleSmall),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            value,
            style: theme.textTheme.bodyMedium?.copyWith(
              color: theme.colorScheme.outline,
            ),
          ),
          const Icon(Icons.chevron_right, size: 20),
        ],
      ),
      onTap: onTap,
    );
  }
}

/// Wrapper so a sub-sheet can return "no value" without being mistaken for a
/// dismissal.
class _Optional<T> {
  final T? value;
  const _Optional(this.value);
}

/// One selectable line in a sub-sheet.
class _OptionRow extends StatelessWidget {
  final String label;
  final bool isSelected;
  final VoidCallback onTap;

  const _OptionRow({
    required this.label,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return ListTile(
      title: Text(
        label,
        style: isSelected
            ? theme.textTheme.bodyLarge?.copyWith(
                color: theme.colorScheme.primary,
                fontWeight: FontWeight.w600,
              )
            : theme.textTheme.bodyLarge,
      ),
      trailing: isSelected
          ? Icon(Icons.check, color: theme.colorScheme.primary)
          : null,
      onTap: onTap,
    );
  }
}

class _OptionSheet<T> extends StatelessWidget {
  final String title;
  final String anyLabel;
  final List<T> values;
  final T? selected;
  final String Function(T) labelOf;

  const _OptionSheet({
    required this.title,
    required this.anyLabel,
    required this.values,
    required this.selected,
    required this.labelOf,
  });

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 0, 20, 8),
              child: Text(title, style: Theme.of(context).textTheme.titleLarge),
            ),
            _OptionRow(
              label: anyLabel,
              isSelected: selected == null,
              onTap: () => Navigator.of(context).pop(_Optional<T>(null)),
            ),
            for (final value in values)
              _OptionRow(
                label: labelOf(value),
                isSelected: selected == value,
                onTap: () => Navigator.of(context).pop(_Optional<T>(value)),
              ),
          ],
        ),
      ),
    );
  }
}

class _SortSheet extends StatefulWidget {
  final RouteSortBy sortBy;
  final SortDirection sortDir;

  const _SortSheet({required this.sortBy, required this.sortDir});

  @override
  State<_SortSheet> createState() => _SortSheetState();
}

class _SortSheetState extends State<_SortSheet> {
  late RouteSortBy _by = widget.sortBy;
  late SortDirection _dir = widget.sortDir;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 0, 20, 8),
              child: Text(
                'routes.filters.sort'.tr(),
                style: Theme.of(context).textTheme.titleLarge,
              ),
            ),
            for (final value in RouteSortBy.$valuesDefined)
              _OptionRow(
                label: RouteFilterLabels.sortBy(value),
                isSelected: _by == value,
                onTap: () => setState(() => _by = value),
              ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 8, 20, 16),
              child: SegmentedButton<SortDirection>(
                segments: [
                  ButtonSegment(
                    value: SortDirection.asc,
                    icon: const Icon(Icons.arrow_upward, size: 16),
                    label: Text('routes.sort.asc'.tr()),
                  ),
                  ButtonSegment(
                    value: SortDirection.desc,
                    icon: const Icon(Icons.arrow_downward, size: 16),
                    label: Text('routes.sort.desc'.tr()),
                  ),
                ],
                selected: {_dir},
                onSelectionChanged: (next) => setState(() => _dir = next.first),
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 0, 20, 16),
              child: FilledButton(
                onPressed: () =>
                    Navigator.of(context).pop((by: _by, dir: _dir)),
                style: FilledButton.styleFrom(
                  minimumSize: const Size.fromHeight(48),
                ),
                child: Text('common.continue'.tr()),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
