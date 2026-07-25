import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../../core/widgets/team_banner.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../data/trip_repository.dart';

final tripDetailProvider =
    FutureProvider.family<TripDto, ({String teamSlug, String tripSlug})>((
      ref,
      params,
    ) async {
      final repository = ref.watch(tripRepositoryProvider);
      return repository.getTrip(params.teamSlug, params.tripSlug);
    });

class TripDetailPage extends ConsumerStatefulWidget {
  final String teamSlug;
  final String tripSlug;

  const TripDetailPage({
    super.key,
    required this.teamSlug,
    required this.tripSlug,
  });

  @override
  ConsumerState<TripDetailPage> createState() => _TripDetailPageState();
}

class _TripDetailPageState extends ConsumerState<TripDetailPage> {
  bool _isJoining = false;

  @override
  Widget build(BuildContext context) {
    final params = (teamSlug: widget.teamSlug, tripSlug: widget.tripSlug);
    final tripAsync = ref.watch(tripDetailProvider(params));

    return tripAsync.when(
      data: (trip) => _TripDetailContent(
        trip: trip,
        isJoining: _isJoining,
        onJoin: () => _joinTrip(trip),
        onLeave: () => _leaveTrip(trip),
      ),
      loading: () => Scaffold(
        appBar: AppBar(),
        body: const Center(child: CircularProgressIndicator()),
      ),
      error: (error, stack) => Scaffold(
        appBar: AppBar(),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.error_outline,
                size: 48,
                color: Theme.of(context).colorScheme.error,
              ),
              const SizedBox(height: 16),
              Text(getErrorMessage(error)),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(tripDetailProvider(params)),
                child: Text('common.retry'.tr()),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _joinTrip(TripDto trip) async {
    setState(() => _isJoining = true);
    try {
      final repository = ref.read(tripRepositoryProvider);
      await repository.joinTrip(trip.team.slug, trip.slug);
      ref.invalidate(
        tripDetailProvider((
          teamSlug: widget.teamSlug,
          tripSlug: widget.tripSlug,
        )),
      );
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('trips.joinSuccess'.tr())));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text(getErrorMessage(e))));
      }
    } finally {
      setState(() => _isJoining = false);
    }
  }

  Future<void> _leaveTrip(TripDto trip) async {
    setState(() => _isJoining = true);
    try {
      final repository = ref.read(tripRepositoryProvider);
      await repository.leaveTrip(trip.team.slug, trip.slug);
      ref.invalidate(
        tripDetailProvider((
          teamSlug: widget.teamSlug,
          tripSlug: widget.tripSlug,
        )),
      );
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('trips.leaveSuccess'.tr())));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text(getErrorMessage(e))));
      }
    } finally {
      setState(() => _isJoining = false);
    }
  }
}

class _TripDetailContent extends ConsumerWidget {
  final TripDto trip;
  final bool isJoining;
  final VoidCallback onJoin;
  final VoidCallback onLeave;

  const _TripDetailContent({
    required this.trip,
    required this.isJoining,
    required this.onJoin,
    required this.onLeave,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final thumbnailUrl = Theme.of(context).brightness == Brightness.dark
        ? trip.thumbnailDarkUrl
        : trip.thumbnailLightUrl;
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: thumbnailUrl != null ? 200 : null,
            pinned: true,
            flexibleSpace: thumbnailUrl != null
                ? FlexibleSpaceBar(
                    title: Text(
                      trip.name,
                      style: const TextStyle(fontSize: 16),
                    ),
                    background: AuthenticatedImage(
                      imageUrl: thumbnailUrl,
                      fit: BoxFit.cover,
                      errorWidget: const SizedBox.shrink(),
                    ),
                  )
                : null,
            title: thumbnailUrl == null ? Text(trip.name) : null,
          ),

          // Team
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
              child: TeamBanner(team: trip.team),
            ),
          ),

          // Date
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      Icon(
                        Icons.calendar_today,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                      const SizedBox(width: 16),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            AppFormatters.formatFullDate(
                              DateTime.parse(trip.dateTime),
                            ),
                            style: Theme.of(context).textTheme.titleMedium
                                ?.copyWith(fontWeight: FontWeight.bold),
                          ),
                          Text(
                            AppFormatters.formatTime(
                              DateTime.parse(trip.dateTime),
                            ),
                            style: Theme.of(context).textTheme.bodyLarge,
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),

          // Participants
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Row(
                children: [
                  Icon(
                    Icons.people,
                    color: Theme.of(context).colorScheme.outline,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'trips.participants'.tr(
                      namedArgs: {'count': trip.participantCount.toString()},
                    ),
                    style: Theme.of(context).textTheme.titleSmall,
                  ),
                  const Spacer(),
                  ...trip.participants
                      .take(5)
                      .map(
                        (p) => Padding(
                          padding: const EdgeInsets.only(left: 4),
                          child: AuthenticatedCircleAvatar(
                            imageUrl: p.avatarUrl,
                            fallbackText: p.displayName.safeFirstUpper(),
                            radius: 14,
                            fontSize: 10,
                          ),
                        ),
                      ),
                ],
              ),
            ),
          ),

          // Stages
          if (trip.stages.isNotEmpty) ...[
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.fromLTRB(16, 24, 16, 8),
                child: Text(
                  'trips.stages'.tr(),
                  style: Theme.of(context).textTheme.titleMedium,
                ),
              ),
            ),
            SliverList(
              delegate: SliverChildBuilderDelegate((context, index) {
                final stage = trip.stages[index];
                return ContentWidthConstraint(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 4,
                  ),
                  child: Card(
                    child: ListTile(
                      leading: CircleAvatar(
                        backgroundColor: Theme.of(context).colorScheme.primary,
                        foregroundColor: Theme.of(
                          context,
                        ).colorScheme.onPrimary,
                        radius: 16,
                        child: Text(
                          '${index + 1}',
                          style: const TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      title: Text(stage.name),
                      subtitle: Text(
                        AppFormatters.formatFullDate(
                          DateTime.parse(stage.dateTime),
                        ),
                      ),
                      trailing: const Icon(Icons.chevron_right),
                      onTap: () => context.push(
                        Paths.stage(trip.team.slug, trip.slug, stage.slug),
                      ),
                    ),
                  ),
                );
              }, childCount: trip.stages.length),
            ),
          ],

          if (trip.stages.isEmpty)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Text(
                  'trips.stages.empty'.tr(),
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Theme.of(context).colorScheme.outline,
                  ),
                ),
              ),
            ),

          // Route
          if (trip.routeSlug != null)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 4,
                ),
                child: Card(
                  child: ListTile(
                    leading: Icon(
                      Icons.route,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                    title: Text('routes.route'.tr()),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () => context.push(
                      Paths.route(trip.team.slug, trip.routeSlug!),
                    ),
                  ),
                ),
              ),
            ),

          // Description
          if (trip.media.markdown.isNotEmpty)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: MarkdownContent(
                      data: trip.media.markdown,
                      images: trip.media.assets.images,
                    ),
                  ),
                ),
              ),
            ),

          const SliverPadding(padding: EdgeInsets.only(bottom: 100)),
        ],
      ),
      bottomNavigationBar: _buildBottomBar(ref),
    );
  }

  bool _isCurrentUserParticipant(WidgetRef ref) {
    final currentUser = ref.watch(authProvider).user;
    if (currentUser == null) return false;
    return trip.participants.any((p) => p.id == currentUser.id);
  }

  Widget _buildBottomBar(WidgetRef ref) {
    final isParticipant = _isCurrentUserParticipant(ref);

    return SafeArea(
      child: ContentWidthConstraint(
        padding: const EdgeInsets.all(16),
        child: isParticipant
            ? OutlinedButton.icon(
                onPressed: isJoining ? null : onLeave,
                icon: isJoining
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Icon(Icons.close),
                label: Text('trips.leave'.tr()),
              )
            : FilledButton.icon(
                onPressed: isJoining ? null : onJoin,
                icon: isJoining
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Icon(Icons.check),
                label: Text('trips.join'.tr()),
              ),
      ),
    );
  }
}
