import 'dart:async';

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

/// Debounce applied to the search field before a new query is issued.
const Duration kSearchDebounce = Duration(milliseconds: 350);

/// Search field plus the filter button and its active-filter badge.
///
/// Search is the one control that applies as you type — debounced, so a fast
/// typist triggers one request instead of ten. Everything else waits for the
/// sheet to be confirmed.
class RouteSearchBar extends StatefulWidget {
  final String? search;
  final ValueChanged<String?> onSearchChanged;
  final VoidCallback onOpenFilters;
  final int activeFilterCount;

  const RouteSearchBar({
    super.key,
    required this.search,
    required this.onSearchChanged,
    required this.onOpenFilters,
    required this.activeFilterCount,
  });

  @override
  State<RouteSearchBar> createState() => _RouteSearchBarState();
}

class _RouteSearchBarState extends State<RouteSearchBar> {
  late final TextEditingController _controller = TextEditingController(
    text: widget.search ?? '',
  );
  Timer? _debounce;

  @override
  void didUpdateWidget(RouteSearchBar oldWidget) {
    super.didUpdateWidget(oldWidget);
    // The search can also be cleared from outside (chip removal, reset).
    final incoming = widget.search ?? '';
    if (incoming != (oldWidget.search ?? '') && incoming != _controller.text) {
      _controller.text = incoming;
    }
  }

  @override
  void dispose() {
    _debounce?.cancel();
    _controller.dispose();
    super.dispose();
  }

  void _onChanged(String value) {
    // Rebuild so the clear button appears/disappears with the text.
    setState(() {});
    _debounce?.cancel();
    _debounce = Timer(kSearchDebounce, () {
      final trimmed = value.trim();
      widget.onSearchChanged(trimmed.isEmpty ? null : trimmed);
    });
  }

  void _clear() {
    _debounce?.cancel();
    _controller.clear();
    widget.onSearchChanged(null);
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: _controller,
            onChanged: _onChanged,
            textInputAction: TextInputAction.search,
            decoration: InputDecoration(
              hintText: 'routes.filters.searchPlaceholder'.tr(),
              prefixIcon: const Icon(Icons.search, size: 20),
              suffixIcon: _controller.text.isEmpty
                  ? null
                  : IconButton(
                      icon: const Icon(Icons.close, size: 18),
                      onPressed: _clear,
                      tooltip: 'common.cancel'.tr(),
                    ),
              isDense: true,
              filled: true,
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide.none,
              ),
            ),
          ),
        ),
        const SizedBox(width: 10),
        Badge.count(
          count: widget.activeFilterCount,
          isLabelVisible: widget.activeFilterCount > 0,
          child: IconButton.filledTonal(
            icon: const Icon(Icons.tune),
            onPressed: widget.onOpenFilters,
            tooltip: 'routes.filters.title'.tr(),
            style: IconButton.styleFrom(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              backgroundColor: theme.colorScheme.surfaceContainerHighest,
            ),
          ),
        ),
      ],
    );
  }
}
