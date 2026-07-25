import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../auth/services/passkey_service.dart';
import '../../../teams/presentation/widgets/publication_card.dart';

/// Provider for home publications feed
final _homePublicationsProvider =
    FutureProvider.family<
      PublicationListResponse,
      ({int page, PublicationType? type})
    >((ref, params) async {
      final client = ref.watch(publicationsClientProvider);
      return client.listAllPublications(
        page: params.page,
        size: 20,
        type: params.type,
      );
    });

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<HomePage> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage> {
  PublicationType? _selectedType;

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authProvider);
    final theme = Theme.of(context);
    final params = (page: 0, type: _selectedType);
    final publicationsAsync = ref.watch(_homePublicationsProvider(params));

    final userName =
        authState.user?.displayName.split(' ').firstOrNull ??
        'home.defaultGreeting'.tr();

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          ref.invalidate(_homePublicationsProvider(params));
        },
        child: CustomScrollView(
          slivers: [
            // App bar with greeting
            SliverAppBar(
              expandedHeight: 120,
              pinned: true,
              flexibleSpace: FlexibleSpaceBar(
                title: Text(
                  'home.greeting'.tr(namedArgs: {'name': userName}),
                  style: const TextStyle(fontSize: 18),
                ),
                background: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: [
                        theme.colorScheme.primary,
                        theme.colorScheme.primaryContainer,
                      ],
                    ),
                  ),
                ),
              ),
            ),

            // Passkey prompt if not configured
            if (!authState.hasPasskeys)
              SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.all(16),
                  child: Card(
                    color: theme.colorScheme.primaryContainer,
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          Icon(
                            Icons.fingerprint,
                            color: theme.colorScheme.onPrimaryContainer,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'auth.passkey.simplifiedLogin'.tr(),
                                  style: theme.textTheme.titleSmall?.copyWith(
                                    color: theme.colorScheme.onPrimaryContainer,
                                  ),
                                ),
                                Text(
                                  'auth.passkey.enablePrompt'.tr(),
                                  style: theme.textTheme.bodySmall?.copyWith(
                                    color: theme.colorScheme.onPrimaryContainer,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          TextButton(
                            onPressed: () => _registerPasskey(context, ref),
                            child: Text('auth.passkey.enable'.tr()),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),

            // Type filter chips
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                child: SingleChildScrollView(
                  scrollDirection: Axis.horizontal,
                  child: Row(
                    children: [
                      _FilterChip(
                        label: 'home.feed.all'.tr(),
                        selected: _selectedType == null,
                        onSelected: () => setState(() => _selectedType = null),
                      ),
                      const SizedBox(width: 8),
                      _FilterChip(
                        label: 'rides.title'.tr(),
                        icon: Icons.directions_bike,
                        selected: _selectedType == PublicationType.ride,
                        onSelected: () => setState(
                          () => _selectedType = PublicationType.ride,
                        ),
                      ),
                      const SizedBox(width: 8),
                      _FilterChip(
                        label: 'posts.title'.tr(),
                        icon: Icons.article,
                        selected: _selectedType == PublicationType.post,
                        onSelected: () => setState(
                          () => _selectedType = PublicationType.post,
                        ),
                      ),
                      const SizedBox(width: 8),
                      _FilterChip(
                        label: 'trips.title'.tr(),
                        icon: Icons.hiking,
                        selected: _selectedType == PublicationType.trip,
                        onSelected: () => setState(
                          () => _selectedType = PublicationType.trip,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),

            // Publications feed
            publicationsAsync.when(
              data: (response) {
                final publications = response.publications;
                if (publications.isEmpty) {
                  return SliverFillRemaining(
                    child: Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          AnimatedEmptyState(
                            child: Icon(
                              Icons.dynamic_feed,
                              size: 64,
                              color: theme.colorScheme.outline,
                            ),
                          ),
                          const SizedBox(height: 16),
                          Text(
                            'home.feed.empty'.tr(),
                            style: theme.textTheme.titleMedium,
                          ),
                        ],
                      ),
                    ),
                  );
                }

                return SliverPadding(
                  padding: const EdgeInsets.all(16),
                  sliver: SliverList.separated(
                    itemCount: publications.length,
                    separatorBuilder: (context, index) =>
                        const SizedBox(height: 8),
                    itemBuilder: (context, index) {
                      return ContentWidthConstraint(
                        child: PublicationCard(
                          publication: publications[index],
                        ),
                      );
                    },
                  ),
                );
              },
              loading: () => SliverPadding(
                padding: const EdgeInsets.all(16),
                sliver: SliverToBoxAdapter(
                  child: ContentWidthConstraint(
                    child: const ShimmerCardList(itemCount: 5),
                  ),
                ),
              ),
              error: (error, _) => SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.all(16),
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          Icon(
                            Icons.error_outline,
                            color: theme.colorScheme.error,
                          ),
                          const SizedBox(width: 12),
                          Expanded(child: Text(getErrorMessage(error))),
                          TextButton(
                            onPressed: () => ref.invalidate(
                              _homePublicationsProvider(params),
                            ),
                            child: Text('common.retry'.tr()),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),

            const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
          ],
        ),
      ),
    );
  }

  Future<void> _registerPasskey(BuildContext context, WidgetRef ref) async {
    try {
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.register(deviceName: 'Mobile');
      ref.read(authProvider.notifier).setHasPasskeys(true);
      if (context.mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('auth.passkey.success'.tr())));
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text(getErrorMessage(e))));
      }
    }
  }
}

class _FilterChip extends StatelessWidget {
  final String label;
  final IconData? icon;
  final bool selected;
  final VoidCallback onSelected;

  const _FilterChip({
    required this.label,
    this.icon,
    required this.selected,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    return FilterChip(
      label: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (icon != null) ...[Icon(icon, size: 16), const SizedBox(width: 4)],
          Text(label),
        ],
      ),
      selected: selected,
      onSelected: (_) => onSelected(),
    );
  }
}
