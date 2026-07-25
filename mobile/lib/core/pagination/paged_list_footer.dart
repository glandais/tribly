import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import 'paged_list_state.dart';

/// Foot of an infinitely scrolled list.
///
/// Renders whichever of the three end-of-list states applies: next page
/// loading, next page failed, or nothing left to load. Deliberately the only
/// place a next-page problem is shown — the items above it stay untouched.
class PagedListFooter extends StatelessWidget {
  final PagedListState<Object?> state;
  final VoidCallback onRetry;

  /// Optional placeholder shown above the spinner while the next page loads,
  /// so the list grows into the incoming content instead of jumping.
  final Widget? skeleton;

  const PagedListFooter({
    super.key,
    required this.state,
    required this.onRetry,
    this.skeleton,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    if (state.nextError != null) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 16),
        child: Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Text(
                  'pagination.nextPageError'.tr(),
                  textAlign: TextAlign.center,
                  style: theme.textTheme.bodyMedium,
                ),
                const SizedBox(height: 12),
                OutlinedButton.icon(
                  onPressed: onRetry,
                  icon: const Icon(Icons.refresh, size: 18),
                  label: Text('common.retry'.tr()),
                ),
              ],
            ),
          ),
        ),
      );
    }

    if (state.isLoadingNext) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 12),
        child: Column(
          children: [
            if (skeleton != null) ...[skeleton!, const SizedBox(height: 12)],
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2),
                ),
                const SizedBox(width: 10),
                Text(
                  'pagination.loadingMore'.tr(),
                  style: theme.textTheme.bodySmall?.copyWith(
                    color: theme.colorScheme.outline,
                  ),
                ),
              ],
            ),
          ],
        ),
      );
    }

    if (!state.hasMore && state.items.isNotEmpty) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 20),
        child: Center(
          child: Text(
            'pagination.endOfList'.tr(),
            style: theme.textTheme.bodySmall?.copyWith(
              color: theme.colorScheme.outline,
            ),
          ),
        ),
      );
    }

    return const SizedBox(height: 8);
  }
}
