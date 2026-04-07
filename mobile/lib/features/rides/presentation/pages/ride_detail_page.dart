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
import '../../../../core/widgets/widgets.dart';
import '../../../../core/widgets/team_banner.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../data/ride_repository.dart';

final rideDetailProvider =
    FutureProvider.family<RideDto, ({String teamSlug, String rideSlug})>(
        (ref, params) async {
  final repository = ref.watch(rideRepositoryProvider);
  return repository.getRide(params.teamSlug, params.rideSlug);
});

class RideDetailPage extends ConsumerStatefulWidget {
  final String teamSlug;
  final String rideSlug;

  const RideDetailPage({
    super.key,
    required this.teamSlug,
    required this.rideSlug,
  });

  @override
  ConsumerState<RideDetailPage> createState() => _RideDetailPageState();
}

class _RideDetailPageState extends ConsumerState<RideDetailPage> {
  bool _isJoining = false;

  @override
  Widget build(BuildContext context) {
    final params = (teamSlug: widget.teamSlug, rideSlug: widget.rideSlug);
    final rideAsync = ref.watch(rideDetailProvider(params));

    return rideAsync.when(
      data: (ride) => _RideDetailContent(
        ride: ride,
        isJoining: _isJoining,
        onJoin: () => _joinRide(ride),
        onLeave: () => _leaveRide(ride),
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
              const Icon(Icons.error_outline, size: 48, color: Colors.red),
              const SizedBox(height: 16),
              Text(getErrorMessage(error)),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(rideDetailProvider(params)),
                child: Text('common.retry'.tr()),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _joinRide(RideDto ride) async {
    // Need to select a group to join
    if (ride.groups.isEmpty) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('rides.noGroupAvailable'.tr())),
        );
      }
      return;
    }

    // If only one group, join directly
    // Otherwise, show group selection dialog
    String? groupId;
    if (ride.groups.length == 1) {
      groupId = ride.groups.first.id;
    } else {
      groupId = await _showGroupSelectionDialog(ride);
    }

    if (groupId == null) return;

    setState(() => _isJoining = true);
    try {
      final repository = ref.read(rideRepositoryProvider);
      await repository.joinRideGroup(ride.team.slug, ride.slug, groupId);
      ref.invalidate(rideDetailProvider(
          (teamSlug: widget.teamSlug, rideSlug: widget.rideSlug)));
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('rides.joinSuccess'.tr())),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(getErrorMessage(e))),
        );
      }
    } finally {
      setState(() => _isJoining = false);
    }
  }

  Future<String?> _showGroupSelectionDialog(RideDto ride) async {
    return showDialog<String>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('rides.selectGroup'.tr()),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: ride.groups.map((group) {
              return ListTile(
                title: Text(group.name),
                subtitle: Text(
                  [
                    if (group.time != null) 'dates.departure'.tr(namedArgs: {'time': AppFormatters.formatLocalTime(group.time!)}),
                    if (group.averageSpeed != null)
                      '${group.averageSpeed!.toStringAsFixed(0)} km/h',
                  ].join(' • '),
                ),
                trailing: Text('${group.countParticipants}'),
                onTap: () => Navigator.of(context).pop(group.id),
              );
            }).toList(),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: Text('common.cancel'.tr()),
          ),
        ],
      ),
    );
  }

  Future<void> _leaveRide(RideDto ride) async {
    // Find the group the user is in (for now, try to leave all groups)
    // The API should handle this gracefully
    if (ride.groups.isEmpty) return;

    setState(() => _isJoining = true);
    try {
      final repository = ref.read(rideRepositoryProvider);
      // Try to leave each group (the one we're in will succeed)
      for (final group in ride.groups) {
        try {
          await repository.leaveRideGroup(ride.team.slug, ride.slug, group.id);
          break; // Successfully left, no need to try other groups
        } catch (_) {
          // Not in this group, try the next one
        }
      }
      ref.invalidate(rideDetailProvider(
          (teamSlug: widget.teamSlug, rideSlug: widget.rideSlug)));
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('rides.leaveSuccess'.tr())),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(getErrorMessage(e))),
        );
      }
    } finally {
      setState(() => _isJoining = false);
    }
  }
}

class _RideDetailContent extends ConsumerWidget {
  final RideDto ride;
  final bool isJoining;
  final VoidCallback onJoin;
  final VoidCallback onLeave;

  const _RideDetailContent({
    required this.ride,
    required this.isJoining,
    required this.onJoin,
    required this.onLeave,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final thumbnailUrl = Theme.of(context).brightness == Brightness.dark
        ? ride.thumbnailDarkUrl
        : ride.thumbnailLightUrl;
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: thumbnailUrl != null ? 200 : null,
            pinned: true,
            flexibleSpace: thumbnailUrl != null
                ? FlexibleSpaceBar(
                    title: Text(
                      ride.name,
                      style: const TextStyle(fontSize: 16),
                    ),
                    background: Hero(
                      tag: 'ride-thumbnail-${ride.slug}',
                      child: AuthenticatedImage(
                        imageUrl: thumbnailUrl,
                        fit: BoxFit.cover,
                        errorWidget: const SizedBox.shrink(),
                      ),
                    ),
                  )
                : null,
            title: thumbnailUrl == null ? Text(ride.name) : null,
          ),

          // Team
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
              child: TeamBanner(team: ride.team),
            ),
          ),

          // Date and time
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
                            AppFormatters.formatFullDate(DateTime.parse(ride.dateTime)),
                            style:
                                Theme.of(context).textTheme.titleMedium?.copyWith(
                                      fontWeight: FontWeight.bold,
                                    ),
                          ),
                          Text(
                            AppFormatters.formatTime(DateTime.parse(ride.dateTime)),
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

          // Start place
          if (ride.startPlace != null)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Card(
                  child: ListTile(
                    leading: Icon(
                      Icons.location_on,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                    title: Text(ride.startPlace!.name),
                    subtitle: ride.startPlace!.address != null
                        ? Text(ride.startPlace!.address!)
                        : null,
                  ),
                ),
              ),
            ),

          // Route
          if (ride.routeSlug != null)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                child: Card(
                  child: ListTile(
                    leading: Icon(
                      Icons.route,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                    title: Text('routes.route'.tr()),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () => context.push(
                      Paths.route(ride.team.slug, ride.routeSlug!),
                    ),
                  ),
                ),
              ),
            ),

          // Participants count
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Icon(
                    Icons.people,
                    color: Theme.of(context).colorScheme.outline,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'rides.participants'.tr(namedArgs: {'count': ride.participantCount.toString()}),
                    style: Theme.of(context).textTheme.titleSmall,
                  ),
                  const Spacer(),
                  // Avatar stack for top participants
                  ...ride.topParticipants.take(5).map(
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

          // Groups
          if (ride.groups.isNotEmpty) ...[
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Text(
                  'rides.groups'.tr(),
                  style: Theme.of(context).textTheme.titleMedium,
                ),
              ),
            ),
            SliverList(
              delegate: SliverChildBuilderDelegate(
                (context, index) {
                  final group = ride.groups[index];
                  return ContentWidthConstraint(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                    child: Card(
                      child: ListTile(
                        title: Text(group.name),
                        subtitle: Text(
                          [
                            if (group.time != null) 'dates.departure'.tr(namedArgs: {'time': AppFormatters.formatLocalTime(group.time!)}),
                            if (group.averageSpeed != null)
                              '${group.averageSpeed!.toStringAsFixed(0)} km/h',
                          ].join(' • '),
                        ),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text('${group.countParticipants}'),
                            if ((group.routeSlug ?? ride.routeSlug) != null) ...[
                              const SizedBox(width: 8),
                              Icon(
                                Icons.route,
                                size: 20,
                                color: Theme.of(context).colorScheme.primary,
                              ),
                            ],
                          ],
                        ),
                        onTap: (group.routeSlug ?? ride.routeSlug) != null
                            ? () => context.push(
                                  Paths.route(ride.team.slug, (group.routeSlug ?? ride.routeSlug)!),
                                )
                            : null,
                      ),
                    ),
                  );
                },
                childCount: ride.groups.length,
              ),
            ),
          ],

          // Description
          if (ride.media.markdown.isNotEmpty)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'rides.description'.tr(),
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        const SizedBox(height: 8),
                        MarkdownContent(
                          data: ride.media.markdown,
                          images: ride.media.assets.images,
                        ),
                      ],
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
    return ride.groups.any(
      (group) => group.participants.any((p) => p.id == currentUser.id),
    );
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
                label: Text('rides.leave'.tr()),
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
                label: Text('rides.join'.tr()),
              ),
      ),
    );
  }
}
