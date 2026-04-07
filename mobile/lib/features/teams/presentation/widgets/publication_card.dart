import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../../core/widgets/widgets.dart';

/// Card widget that renders a [PublicationDto] variant (ride, post, or trip).
class PublicationCard extends ConsumerWidget {
  final PublicationDto publication;

  const PublicationCard({super.key, required this.publication});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return switch (publication) {
      PublicationDtoRide ride => _RidePublicationCard(ride: ride, ref: ref),
      PublicationDtoPost post => _PostPublicationCard(post: post),
      PublicationDtoTrip trip => _TripPublicationCard(trip: trip, ref: ref),
    };
  }
}

class _RidePublicationCard extends StatelessWidget {
  final PublicationDtoRide ride;
  final WidgetRef ref;

  const _RidePublicationCard({required this.ride, required this.ref});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final token = ref.watch(accessTokenHolderProvider);
    final thumbnailUrl = theme.brightness == Brightness.dark
        ? ride.thumbnailDarkUrl
        : ride.thumbnailLightUrl;

    return AnimatedCard(
      onTap: () => context.push(Paths.ride(ride.team.slug, ride.slug)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                color: theme.colorScheme.primaryContainer,
                image: thumbnailUrl != null
                    ? DecorationImage(
                        image: AuthenticatedDecorationImage.fromUrl(
                            thumbnailUrl, token)!,
                        fit: BoxFit.cover,
                      )
                    : null,
              ),
              child: thumbnailUrl == null
                  ? Icon(Icons.directions_bike,
                      color: theme.colorScheme.onPrimaryContainer)
                  : null,
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(Icons.directions_bike,
                          size: 14, color: theme.colorScheme.primary),
                      const SizedBox(width: 4),
                      Text('rides.title'.tr(),
                          style: theme.textTheme.labelSmall
                              ?.copyWith(color: theme.colorScheme.primary)),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Text(
                    ride.name,
                    style: theme.textTheme.titleSmall
                        ?.copyWith(fontWeight: FontWeight.bold),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '${AppFormatters.formatRideDate(DateTime.parse(ride.dateTime))} • ${'rides.participants'.tr(namedArgs: {'count': ride.participantCount.toString()})}',
                    style: theme.textTheme.bodySmall,
                  ),
                ],
              ),
            ),
            const Icon(Icons.chevron_right),
          ],
        ),
      ),
    );
  }
}

class _PostPublicationCard extends StatelessWidget {
  final PublicationDtoPost post;

  const _PostPublicationCard({required this.post});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return AnimatedCard(
      onTap: () => context.push(Paths.post(post.team.slug, post.slug)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                color: theme.colorScheme.tertiaryContainer,
              ),
              child: Icon(Icons.article,
                  color: theme.colorScheme.onTertiaryContainer),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(Icons.article,
                          size: 14, color: theme.colorScheme.tertiary),
                      const SizedBox(width: 4),
                      Text('posts.title'.tr(),
                          style: theme.textTheme.labelSmall
                              ?.copyWith(color: theme.colorScheme.tertiary)),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Text(
                    post.name,
                    style: theme.textTheme.titleSmall
                        ?.copyWith(fontWeight: FontWeight.bold),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    AppFormatters.formatRideDate(
                        DateTime.parse(post.dateTime)),
                    style: theme.textTheme.bodySmall,
                  ),
                ],
              ),
            ),
            const Icon(Icons.chevron_right),
          ],
        ),
      ),
    );
  }
}

class _TripPublicationCard extends StatelessWidget {
  final PublicationDtoTrip trip;
  final WidgetRef ref;

  const _TripPublicationCard({required this.trip, required this.ref});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final token = ref.watch(accessTokenHolderProvider);
    final thumbnailUrl = theme.brightness == Brightness.dark
        ? trip.thumbnailDarkUrl
        : trip.thumbnailLightUrl;

    return AnimatedCard(
      onTap: () => context.push(Paths.trip(trip.team.slug, trip.slug)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                color: theme.colorScheme.secondaryContainer,
                image: thumbnailUrl != null
                    ? DecorationImage(
                        image: AuthenticatedDecorationImage.fromUrl(
                            thumbnailUrl, token)!,
                        fit: BoxFit.cover,
                      )
                    : null,
              ),
              child: thumbnailUrl == null
                  ? Icon(Icons.hiking,
                      color: theme.colorScheme.onSecondaryContainer)
                  : null,
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(Icons.hiking,
                          size: 14, color: theme.colorScheme.secondary),
                      const SizedBox(width: 4),
                      Text('trips.title'.tr(),
                          style: theme.textTheme.labelSmall
                              ?.copyWith(color: theme.colorScheme.secondary)),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Text(
                    trip.name,
                    style: theme.textTheme.titleSmall
                        ?.copyWith(fontWeight: FontWeight.bold),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '${AppFormatters.formatRideDate(DateTime.parse(trip.dateTime))} • ${trip.stageCount} ${'trips.stages'.tr()}',
                    style: theme.textTheme.bodySmall,
                  ),
                ],
              ),
            ),
            const Icon(Icons.chevron_right),
          ],
        ),
      ),
    );
  }
}
