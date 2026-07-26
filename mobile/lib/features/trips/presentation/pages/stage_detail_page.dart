import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/widgets/team_banner.dart';
import '../../../../core/widgets/widgets.dart';
import '../../providers/trip_detail_provider.dart';

class StageDetailPage extends ConsumerWidget {
  final String teamSlug;
  final String tripSlug;
  final String stageSlug;

  const StageDetailPage({
    super.key,
    required this.teamSlug,
    required this.tripSlug,
    required this.stageSlug,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = TripKey(teamSlug: teamSlug, tripSlug: tripSlug);
    final tripAsync = ref.watch(tripDetailProvider(params));

    return tripAsync.when(
      data: (trip) {
        final stage = trip.stages.where((s) => s.slug == stageSlug).firstOrNull;
        if (stage == null) {
          return Scaffold(
            appBar: AppBar(),
            body: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 48),
                  const SizedBox(height: 16),
                  Text('trips.stage.notFound'.tr()),
                  const SizedBox(height: 16),
                  FilledButton(
                    onPressed: () => context.go(Paths.trip(teamSlug, tripSlug)),
                    child: Text('trips.stage.backToTrip'.tr()),
                  ),
                ],
              ),
            ),
          );
        }
        final stageIndex = trip.stages.indexWhere((s) => s.slug == stageSlug);
        return _StageDetailContent(
          trip: trip,
          stage: stage,
          stageNumber: stageIndex + 1,
        );
      },
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
}

class _StageDetailContent extends StatelessWidget {
  final TripDto trip;
  final TripStageDto stage;
  final int stageNumber;

  const _StageDetailContent({
    required this.trip,
    required this.stage,
    required this.stageNumber,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(pinned: true, title: Text(stage.name)),

          // Team
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
              child: TeamBanner(team: trip.team),
            ),
          ),

          // Stage number + date
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      CircleAvatar(
                        backgroundColor: Theme.of(context).colorScheme.primary,
                        foregroundColor: Theme.of(
                          context,
                        ).colorScheme.onPrimary,
                        child: Text(
                          '$stageNumber',
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                      ),
                      const SizedBox(width: 16),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            AppFormatters.formatFullDate(
                              DateTime.parse(stage.dateTime),
                            ),
                            style: Theme.of(context).textTheme.titleMedium
                                ?.copyWith(fontWeight: FontWeight.bold),
                          ),
                          Text(
                            AppFormatters.formatTime(
                              DateTime.parse(stage.dateTime),
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

          // Start place
          if (stage.startPlace != null)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 4,
                ),
                child: Card(
                  child: ListTile(
                    leading: Icon(
                      Icons.location_on,
                      color: Colors.green.shade700,
                    ),
                    title: Text('trips.stage.startPlace'.tr()),
                    subtitle: Text(stage.startPlace!.name),
                  ),
                ),
              ),
            ),

          // End place
          if (stage.endPlace != null)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 4,
                ),
                child: Card(
                  child: ListTile(
                    leading: Icon(
                      Icons.location_on,
                      color: Colors.red.shade700,
                    ),
                    title: Text('trips.stage.endPlace'.tr()),
                    subtitle: Text(stage.endPlace!.name),
                  ),
                ),
              ),
            ),

          // Route
          if (stage.route != null)
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
                    title: Text('trips.stage.viewRoute'.tr()),
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () => context.push(
                      Paths.route(trip.team.slug, stage.route!.slug),
                    ),
                  ),
                ),
              ),
            ),

          // Description
          if (stage.media.markdown.isNotEmpty)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: MarkdownContent(
                      data: stage.media.markdown,
                      images: stage.media.assets.images,
                    ),
                  ),
                ),
              ),
            ),

          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }
}
