import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/formatters.dart';
import '../../../rides/providers/ride_detail_provider.dart';
import '../../../rides/providers/ride_registration_controller.dart';
import '../../../teams/presentation/widgets/publication_card.dart';
import '../../providers/next_ride_provider.dart';

/// « Ma prochaine sortie » — la réponse à la question du brief : *qu'est-ce que
/// je fais à vélo cette semaine ?*
///
/// Le bloc **disparaît** quand il n'y a rien à montrer, remplacé par une carte
/// compacte. Il ne reste jamais en place vide : un cadre « aucune sortie » de
/// 400 px en tête d'accueil coûterait la première carte du fil.
class NextRideCard extends ConsumerWidget {
  const NextRideCard({super.key, required this.next});

  final NextRide next;

  RideDto get ride => next.ride;
  RideGroupDto? get group => next.group;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);
    final DateTime? at = ride.startsAt;

    final RideKey key = RideKey(teamSlug: ride.team.slug, rideSlug: ride.slug);
    final RideRegistrationState registration = ref.watch(
      rideRegistrationProvider(key),
    );

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Row(
          children: <Widget>[
            Expanded(child: Text('home.nextRide'.tr(), style: t.sectionTitle)),
            if (at != null)
              PdlBadge(
                label: AppFormatters.formatRelative(at),
                tone: PdlDerivedTones.registered(c),
                size: PdlBadgeSize.lg,
                icon: PdlIcons.check,
              ),
          ],
        ),
        const SizedBox(height: PdlSpacing.cardTight),
        // Le bandeau d'échec de désinscription vit ici : le contrôleur est
        // partagé avec l'écran 12, donc l'erreur suit l'utilisateur.
        if (registration.failure != null) ...<Widget>[
          PdlBanner(
            tone: PdlBannerTone.danger,
            title: 'rides.failure.generic'.tr(
              namedArgs: <String, String>{
                'group': registration.failure!.groupName,
              },
            ),
            message: registration.failure!.message,
            onDismiss: ref
                .read(rideRegistrationProvider(key).notifier)
                .dismissFailure,
            dismissSemanticLabel: 'common.close'.tr(),
          ),
          const SizedBox(height: PdlSpacing.chipGap),
        ],
        PdlCard(
          padding: PdlCardPadding.none,
          onTap: () => context.push(Paths.ride(ride.team.slug, ride.slug)),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              PdlCardMedia(
                tone: PdlMediaTone.ride,
                icon: PdlIcons.ride,
                // **La vignette est celle de la sortie, pas du parcours du
                // groupe** : `RideGroupDto` n'a pas de `thumbnailUrl`
                // (§5.2-3). Deux groupes sur deux parcours partagent donc la
                // même image.
                imageUrl: Theme.of(context).brightness == Brightness.dark
                    ? (ride.thumbnailDarkUrl ?? ride.thumbnailLightUrl)
                    : (ride.thumbnailLightUrl ?? ride.thumbnailDarkUrl),
                height: PdlMediaHeights.hero,
                borderRadius: BorderRadius.zero,
              ),
              Padding(
                padding: const EdgeInsets.all(PdlSpacing.card),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    _badges(c),
                    const SizedBox(height: 6),
                    PdlTeamLine(
                      label: ride.team.name,
                      onTap: () => context.push(Paths.team(ride.team.slug)),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      ride.name,
                      style: t.screenTitle.copyWith(fontSize: 20),
                    ),
                    const SizedBox(height: PdlSpacing.cardTight),
                    ..._facts(context, c, t, at),
                    const SizedBox(height: PdlSpacing.cardTight),
                    _stats(units),
                    if (group != null) ...<Widget>[
                      const SizedBox(height: PdlSpacing.cardTight),
                      _seats(context, c, t, group!),
                    ],
                    const SizedBox(height: 14),
                    _actions(context, ref, key, registration),
                  ],
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _badges(PdlColors c) => Wrap(
    spacing: PdlSpacing.badgeGap,
    runSpacing: PdlSpacing.badgeGap,
    children: <Widget>[
      PdlBadge(
        label: 'publicationType.ride'.tr(),
        tone: PublicationType.ride.tone(c),
      ),
      PdlBadge(
        label: 'rides.registered'.tr(),
        tone: PdlDerivedTones.registered(c),
        icon: PdlIcons.check,
      ),
      if (ride.isCancelled)
        PdlBadge(
          label: 'status.cancelled'.tr(),
          tone: Status.cancelled.tone(c),
        ),
    ],
  );

  /// Date, groupe et heure de départ, lieu — les trois faits qui décident si
  /// l'on y va.
  List<Widget> _facts(
    BuildContext context,
    PdlColors c,
    PdlTypography t,
    DateTime? at,
  ) {
    final RideGroupDto? g = group;
    return <Widget>[
      if (at != null)
        _fact(c, t, PdlIcons.date, AppFormatters.formatLongDate(at)),
      if (g != null)
        _fact(
          c,
          t,
          PdlIcons.time,
          g.time == null
              ? g.name
              : 'home.groupDeparture'.tr(
                  namedArgs: <String, String>{
                    'group': g.name,
                    'time': AppFormatters.formatLocalTime(g.time!),
                  },
                ),
        ),
      if (ride.startPlace != null) ...<Widget>[
        const SizedBox(height: 10),
        PdlPlaceRow(
          kind: PdlPlaceKind.start,
          name: ride.startPlace!.name,
          address: ride.startPlace!.address,
        ),
      ],
    ];
  }

  Widget _fact(PdlColors c, PdlTypography t, IconData icon, String label) =>
      Padding(
        padding: const EdgeInsets.only(bottom: 10),
        child: Row(
          children: <Widget>[
            Icon(icon, size: 16, color: c.textDimmed),
            const SizedBox(width: 5),
            Expanded(
              child: Text(
                label,
                style: t.statValue,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ),
          ],
        ),
      );

  /// Distance et dénivelé viennent du **groupe**, pas de la sortie : deux
  /// groupes d'une même sortie ne roulent pas le même parcours.
  Widget _stats(UnitSystem units) {
    final RideGroupDto? g = group;
    return PdlStatRow(
      stats: <PdlStat>[
        if (g?.distance != null)
          PdlStat(
            value: AppFormatters.formatDistance(g!.distance!, units),
            icon: PdlIcons.distance,
          ),
        if (g?.elevationGain != null)
          PdlStat(
            value: AppFormatters.formatElevation(g!.elevationGain!, units),
            icon: PdlIcons.elevationUp,
            trend: PdlStatTrend.up,
          ),
        PdlStat(
          value: g?.maxParticipants == null
              ? '${ride.participantCount}'
              : '${g!.countParticipants}/${g.maxParticipants}',
          icon: PdlIcons.people,
        ),
      ],
    );
  }

  Widget _seats(
    BuildContext context,
    PdlColors c,
    PdlTypography t,
    RideGroupDto g,
  ) {
    return Row(
      children: <Widget>[
        if (g.participants.isNotEmpty)
          PdlAvatarStack(
            people: <PdlAvatarEntry>[
              for (final PublicUserDto p in g.participants)
                PdlAvatarEntry(name: p.displayName, imageUrl: p.avatarUrl),
            ],
          ),
        if (g.maxParticipants != null) ...<Widget>[
          const SizedBox(width: PdlSpacing.cardTight),
          Expanded(
            child: PdlProgressBar(
              value: g.countParticipants / g.maxParticipants!,
              semanticLabel: 'rides.participantsOf'.tr(
                namedArgs: <String, String>{
                  'count': '${g.countParticipants}',
                  'max': '${g.maxParticipants}',
                },
              ),
            ),
          ),
        ],
      ],
    );
  }

  Widget _actions(
    BuildContext context,
    WidgetRef ref,
    RideKey key,
    RideRegistrationState registration,
  ) {
    final String? groupId = ride.registeredGroupId;
    return Row(
      children: <Widget>[
        Expanded(
          child: PdlButton(
            label: 'home.viewRide'.tr(),
            onPressed: () =>
                context.push(Paths.ride(ride.team.slug, ride.slug)),
          ),
        ),
        // Aucune désinscription sur une sortie annulée : elle n'a plus lieu.
        if (groupId != null && !ride.isCancelled && !ride.isPast) ...<Widget>[
          const SizedBox(width: PdlSpacing.chipGap),
          PdlButton(
            label: 'rides.leave'.tr(),
            loadingLabel: 'rides.leaving'.tr(),
            variant: PdlButtonVariant.outline,
            loading: registration.pendingGroupId == groupId,
            // **`registeredGroupId`, jamais une boucle** sur les groupes.
            onPressed: () =>
                ref.read(rideRegistrationProvider(key).notifier).leave(groupId),
          ),
        ],
      ],
    );
  }
}

/// Ce qui remplace le bloc quand aucune participation n'est à venir.
///
/// Compacte et actionnable : « Explorer » mène au fil, pas à un cul-de-sac.
class NoNextRideCard extends StatelessWidget {
  const NoNextRideCard({super.key, this.onExplore});

  final VoidCallback? onExplore;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    return PdlCard(
      flat: true,
      child: Row(
        children: <Widget>[
          Icon(PdlIcons.dateBusy, size: 24, color: context.pdl.textDimmed),
          const SizedBox(width: PdlSpacing.cardTight),
          Expanded(child: Text('rides.noUpcoming'.tr(), style: t.bodyStrong)),
          if (onExplore != null)
            PdlButton(
              label: 'home.explore'.tr(),
              variant: PdlButtonVariant.text,
              size: PdlButtonSize.sm,
              onPressed: onExplore,
            ),
        ],
      ),
    );
  }
}
