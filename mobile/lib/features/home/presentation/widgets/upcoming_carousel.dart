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
import '../../../rides/presentation/pages/ride_detail_page.dart';
import '../../../teams/presentation/widgets/publication_card.dart';
import '../../providers/upcoming_provider.dart';

/// Largeur d'une carte de carrousel.
const double _kCardWidth = 280;

/// Largeur du fondu de bord, qui annonce qu'il y a une suite.
const double _kEdgeFade = 24;

/// La rangée « À venir » : sorties et voyages des trente prochains jours,
/// toutes équipes confondues.
class UpcomingCarousel extends ConsumerWidget {
  const UpcomingCarousel({super.key, required this.items});

  final List<PublicationDto> items;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    if (items.isEmpty) return const SizedBox.shrink();
    final PdlTypography t = context.pdlText;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 4, 16, 8),
          child: Row(
            children: <Widget>[
              Expanded(
                child: Text('home.upcoming'.tr(), style: t.sectionTitle),
              ),
              Text('home.upcomingScope'.tr(), style: t.count),
            ],
          ),
        ),
        SizedBox(
          height: 320,
          // Le masque de bord dit qu'il y a une suite sans occuper de place :
          // c'est le même motif que `PdlChipRow`, à 24 px au lieu de 28.
          child: ShaderMask(
            shaderCallback: (Rect bounds) => LinearGradient(
              begin: Alignment.centerLeft,
              end: Alignment.centerRight,
              stops: <double>[0, 0, 1 - _kEdgeFade / bounds.width, 1],
              colors: const <Color>[
                Color(0xFFFFFFFF),
                Color(0xFFFFFFFF),
                Color(0xFFFFFFFF),
                Color(0x00FFFFFF),
              ],
            ).createShader(bounds),
            blendMode: BlendMode.dstIn,
            child: ListView.separated(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              itemCount: items.length,
              separatorBuilder: (BuildContext context, int index) =>
                  const SizedBox(width: PdlSpacing.feedGap),
              itemBuilder: (BuildContext context, int index) => SizedBox(
                width: _kCardWidth,
                child: _UpcomingCard(publication: items[index]),
              ),
            ),
          ),
        ),
      ],
    );
  }
}

class _UpcomingCard extends ConsumerWidget {
  const _UpcomingCard({required this.publication});

  final PublicationDto publication;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return switch (publication) {
      PublicationDtoRide ride => _rideCard(context, ref, ride),
      PublicationDtoTrip trip => _tripCard(context, ref, trip),
      // Le carrousel est filtré en amont sur les sorties et les voyages ;
      // ce cas ne se produit pas, mais le `switch` reste exhaustif.
      PublicationDtoPost _ => const SizedBox.shrink(),
    };
  }

  Widget _rideCard(BuildContext context, WidgetRef ref, PublicationDtoRide r) {
    final PdlColors c = context.pdl;
    final DateTime? at = DateTime.tryParse(r.dateTime)?.toLocal();
    final UpcomingAction action = upcomingAction(r);

    return _shell(
      context,
      onTap: () => context.push(Paths.ride(r.team.slug, r.slug)),
      imageUrl: Theme.of(context).brightness == Brightness.dark
          ? (r.thumbnailDarkUrl ?? r.thumbnailLightUrl)
          : (r.thumbnailLightUrl ?? r.thumbnailDarkUrl),
      tone: PdlMediaTone.ride,
      icon: PdlIcons.ride,
      teamName: r.team.name,
      title: r.name,
      rows: <List<PdlStat>>[
        <PdlStat>[
          if (at != null)
            PdlStat(
              value: AppFormatters.formatDayMonth(at),
              icon: PdlIcons.date,
            ),
          if (at != null)
            PdlStat(value: AppFormatters.formatTime(at), icon: PdlIcons.time),
        ],
        <PdlStat>[
          if (r.groupCount > 1)
            PdlStat(
              value: 'rides.groupCount'.plural(r.groupCount),
              icon: PdlIcons.terrain,
            ),
          PdlStat(
            // « N participants » et non « 2/48 » : la capacité agrégée
            // n'existe pas sur une ligne de liste (§5.2-5).
            value: '${r.participantCount}',
            icon: PdlIcons.people,
          ),
        ],
      ],
      action: switch (action) {
        UpcomingAction.registered => PdlBadge(
          label: 'rides.registered'.tr(),
          tone: PdlDerivedTones.registered(c),
          icon: PdlIcons.check,
          size: PdlBadgeSize.lg,
        ),
        UpcomingAction.full => PdlButton(
          label: 'rides.groupFull'.tr(),
          variant: PdlButtonVariant.outline,
          size: PdlButtonSize.sm,
          pill: true,
          fullWidth: true,
          enabled: false,
        ),
        UpcomingAction.join => PdlButton(
          label: 'rides.groupJoin'.tr(),
          size: PdlButtonSize.sm,
          pill: true,
          fullWidth: true,
          // Aller simple vers l'écran 12 : l'identifiant du groupe unique
          // n'est pas connu ici, c'est le détail qui l'a.
          onPressed: () => context.push(
            Paths.ride(r.team.slug, r.slug),
            extra: const RideDetailExtra(autoJoin: true),
          ),
        ),
        UpcomingAction.chooseGroup => PdlButton(
          label: 'home.chooseGroup'.tr(),
          variant: PdlButtonVariant.outline,
          size: PdlButtonSize.sm,
          pill: true,
          fullWidth: true,
          onPressed: () => context.push(Paths.ride(r.team.slug, r.slug)),
        ),
      },
    );
  }

  Widget _tripCard(BuildContext context, WidgetRef ref, PublicationDtoTrip t) {
    final UnitSystem units = ref.watch(unitSystemProvider);
    final DateTime? start = DateTime.tryParse(t.dateTime)?.toLocal();
    final DateTime? end = t.endDate == null
        ? null
        : DateTime.tryParse(t.endDate!)?.toLocal();

    return _shell(
      context,
      onTap: () => context.push(Paths.trip(t.team.slug, t.slug)),
      imageUrl: Theme.of(context).brightness == Brightness.dark
          ? (t.thumbnailDarkUrl ?? t.thumbnailLightUrl)
          : (t.thumbnailLightUrl ?? t.thumbnailDarkUrl),
      tone: PdlMediaTone.trip,
      icon: PdlIcons.trip,
      teamName: t.team.name,
      title: t.name,
      rows: <List<PdlStat>>[
        <PdlStat>[
          if (start != null)
            PdlStat(
              // `endDate` est un champ de la 1.3.0 : la plage complète était
              // impossible à rendre en 1.2.0.
              value: end == null
                  ? AppFormatters.formatDayMonth(start)
                  : '${AppFormatters.formatDayMonth(start)} → '
                        '${AppFormatters.formatDayMonth(end)}',
              icon: PdlIcons.date,
            ),
          if (t.stageCount > 0)
            PdlStat(
              value: 'trips.stageCount'.plural(t.stageCount),
              icon: PdlIcons.stage,
            ),
        ],
        <PdlStat>[
          if (t.totalDistance != null)
            PdlStat(
              value: AppFormatters.formatDistance(t.totalDistance!, units),
              icon: PdlIcons.distance,
            ),
          if (t.totalElevationGain != null)
            PdlStat(
              value: AppFormatters.formatElevation(
                t.totalElevationGain!,
                units,
              ),
              icon: PdlIcons.elevationUp,
              trend: PdlStatTrend.up,
            ),
        ],
      ],
      action: PdlButton(
        label: 'home.viewRide'.tr(),
        variant: PdlButtonVariant.outline,
        size: PdlButtonSize.sm,
        pill: true,
        fullWidth: true,
        onPressed: () => context.push(Paths.trip(t.team.slug, t.slug)),
      ),
    );
  }

  Widget _shell(
    BuildContext context, {
    required VoidCallback onTap,
    required PdlMediaTone tone,
    required IconData icon,
    required String teamName,
    required String title,
    required List<List<PdlStat>> rows,
    required Widget action,
    String? imageUrl,
  }) {
    final PdlTypography t = context.pdlText;
    return PdlCard(
      padding: PdlCardPadding.none,
      onTap: onTap,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: <Widget>[
          PdlCardMedia(
            tone: tone,
            icon: icon,
            imageUrl: imageUrl,
            height: PdlMediaHeights.carousel,
            borderRadius: BorderRadius.zero,
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(PdlSpacing.cardTight),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: <Widget>[
                  PdlTeamLine(label: teamName, showChevron: false),
                  Text(
                    title,
                    style: t.cardTitle.copyWith(fontSize: 15),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  for (final List<PdlStat> row in rows)
                    if (row.isNotEmpty)
                      Padding(
                        padding: const EdgeInsets.only(top: 6),
                        child: PdlStatRow(stats: row),
                      ),
                  const Spacer(),
                  Padding(
                    padding: const EdgeInsets.only(top: 10),
                    child: action,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
