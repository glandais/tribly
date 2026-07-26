import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
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

/// La couleur d'un événement de calendrier.
///
/// **Une seule couleur d'étape** (§3.2) : celle de `PublicationType.trip`
/// (`#12b886` clair / `#099268` sombre). Le `#40c057` que la maquette employait
/// aussi reste réservé au marqueur de départ cartographique — deux verts
/// voisins sur deux plans différents ne se distinguent pas.
Color calendarEventColor(PdlColors c, String type) => switch (type) {
  'RIDE' => PublicationType.ride.tone(c).fill,
  'TRIP_STAGE' => PublicationType.trip.tone(c).fill,
  _ => c.neutral,
};

IconData calendarEventIcon(String type) => switch (type) {
  'RIDE' => PdlIcons.ride,
  'TRIP_STAGE' => PdlIcons.trip,
  _ => PdlIcons.date,
};

/// Une ligne d'agenda — la carte de ~92 px de l'écran 22.
///
/// **Elle ne déclenche aucun appel.** Tout ce qu'elle rend — le lieu, la
/// distance, le D+, le groupe rejoint, le statut — vient de l'unique
/// `GET /api/calendar/events` du mois. C'est le gain principal de l'écran, et
/// il se perd à la première lecture qui rappellerait l'API depuis ici.
class AgendaCard extends ConsumerWidget {
  const AgendaCard({super.key, required this.event, this.now});

  final CalendarEventDto event;

  /// Injectable pour les tests ; à défaut, l'heure de l'appareil.
  final DateTime? now;

  DateTime get _start => DateTime.parse(event.start).toLocal();

  bool get _isPast => _start.isBefore(now ?? DateTime.now());

  bool get _isCancelled => event.status == 'CANCELLED';

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);
    final Color accent = calendarEventColor(c, event.type);

    final List<String> meta = <String>[
      event.teamName,
      if (!event.allDay) AppFormatters.formatTime(_start),
      if (event.startPlaceName != null && event.startPlaceName!.isNotEmpty)
        event.startPlaceName!,
    ];

    final List<PdlStat> stats = <PdlStat>[
      if (event.distance != null)
        PdlStat(
          icon: PdlIcons.distance,
          value: AppFormatters.formatDistance(event.distance!, units),
        ),
      if (event.elevationGain != null)
        PdlStat(
          icon: PdlIcons.elevationUp,
          value: AppFormatters.formatElevation(event.elevationGain!, units),
        ),
    ];

    final Widget card = PdlCard(
      padding: PdlCardPadding.none,
      onTap: _onTap(context),
      child: IntrinsicHeight(
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            // Le filet porte la couleur du type : c'est lui qui relie la carte
            // au point de la grille, et à la légende.
            Container(width: 4, color: accent),
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(PdlSpacing.cardTight),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: <Widget>[
                    Row(
                      children: <Widget>[
                        Icon(
                          calendarEventIcon(event.type),
                          size: 16,
                          color: accent,
                        ),
                        const SizedBox(width: 6),
                        Expanded(
                          child: Text(
                            event.title,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: t.cardTitle.copyWith(color: c.text),
                          ),
                        ),
                        Icon(
                          PdlIcons.chevronRight,
                          size: 18,
                          color: c.textDimmed,
                        ),
                      ],
                    ),
                    const SizedBox(height: 2),
                    Text(
                      meta.join(' · '),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: t.sub,
                    ),
                    if (stats.isNotEmpty) ...<Widget>[
                      const SizedBox(height: 6),
                      PdlStatRow(stats: stats),
                    ],
                    if (_badges(c).isNotEmpty) ...<Widget>[
                      const SizedBox(height: 6),
                      Wrap(
                        spacing: PdlSpacing.badgeGap,
                        runSpacing: PdlSpacing.badgeGap,
                        children: _badges(c),
                      ),
                    ],
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );

    // Le passé s'efface sans disparaître : un mois se lit d'un bloc, et une
    // sortie d'avant-hier reste une information.
    return _isPast ? Opacity(opacity: 0.55, child: card) : card;
  }

  List<Widget> _badges(PdlColors c) => <Widget>[
    if (event.registered)
      PdlBadge(
        // « Inscrit · Chill route long » : le groupe rejoint est la moitié
        // utile de l'information, et `groupName` la porte depuis la 1.5.0.
        label: event.groupName == null
            ? 'rides.registered'.tr()
            : 'rides.registeredIn'.tr(
                namedArgs: <String, String>{'group': event.groupName!},
              ),
        icon: PdlIcons.check,
        tone: PdlDerivedTones.registered(c),
      ),
    // Oublié par la maquette, ajouté depuis `CalendarEventDto.status` (§3.2) :
    // une sortie annulée qui se rend comme les autres est un rendez-vous
    // manqué.
    if (_isCancelled)
      PdlBadge(
        label: 'calendar.cancelled'.tr(),
        icon: PdlIcons.cancelled,
        tone: Status.cancelled.tone(c),
      ),
  ];

  VoidCallback? _onTap(BuildContext context) {
    switch (event.type) {
      case 'RIDE':
        return () => context.push(Paths.ride(event.teamSlug, event.entitySlug));
      case 'TRIP_STAGE':
        final String? tripSlug = event.tripSlug;
        if (tripSlug == null) return null;
        return () => context.push(
          Paths.stage(event.teamSlug, tripSlug, event.entitySlug),
        );
      default:
        return null;
    }
  }
}
