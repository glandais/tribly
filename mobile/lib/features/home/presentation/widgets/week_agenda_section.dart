import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../keys.dart';
import '../../../calendar/presentation/widgets/agenda_card.dart';
import '../../providers/next_ride_provider.dart';
import '../../providers/week_events_provider.dart';

/// Combien de lignes « Cette semaine » montre au plus ; le reste est au
/// calendrier.
const int kWeekAgendaLimit = 5;

/// « Cette semaine » — l'agenda des sept prochains jours, toutes équipes.
///
/// La sortie que « Ma prochaine sortie » montre déjà en grand n'est **pas**
/// répétée ici : la maquette la retire, et deux fois la même ligne l'une sous
/// l'autre ne dit rien de plus.
///
/// Un échec masque le bloc ; une semaine vide le réduit à une carte compacte
/// qui mène au calendrier.
class WeekAgendaSection extends ConsumerWidget {
  const WeekAgendaSection({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AsyncValue<List<CalendarEventDto>> week = ref.watch(
      weekEventsProvider,
    );
    final RideDto? nextRide = ref.watch(nextRideProvider).value?.ride;

    return week.when(
      loading: () => _frame(
        context,
        const PdlSkeletonCard(variant: PdlSkeletonCardVariant.compact),
      ),
      error: (Object error, StackTrace stack) => const SizedBox.shrink(),
      data: (List<CalendarEventDto> all) {
        final List<CalendarEventDto> events = <CalendarEventDto>[
          for (final CalendarEventDto e in all)
            if (!_isNextRide(e, nextRide)) e,
        ];
        if (events.isEmpty) {
          // Only the next ride this week: it sits right above, so « nothing else ».
          return _frame(context, _WeekEmpty(nothingElse: all.isNotEmpty));
        }

        final List<CalendarEventDto> shown = events
            .take(kWeekAgendaLimit)
            .toList();
        final int hidden = events.length - shown.length;
        return _frame(
          context,
          Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              for (final CalendarEventDto e in shown) ...<Widget>[
                WeekAgendaRow(key: keys.home.weekEvent(e.entitySlug), event: e),
                const SizedBox(height: PdlSpacing.feedGap),
              ],
              if (hidden > 0)
                Align(
                  alignment: Alignment.centerLeft,
                  child: PdlButton(
                    label: 'home.week.more'.plural(hidden),
                    variant: PdlButtonVariant.text,
                    size: PdlButtonSize.sm,
                    onPressed: () => context.go(Paths.calendar()),
                  ),
                ),
            ],
          ),
        );
      },
    );
  }

  static bool _isNextRide(CalendarEventDto e, RideDto? ride) =>
      ride != null &&
      e.type == 'RIDE' &&
      e.teamSlug == ride.team.slug &&
      e.entitySlug == ride.slug;

  Widget _frame(BuildContext context, Widget body) => Padding(
    key: keys.home.weekSection,
    padding: const EdgeInsets.fromLTRB(
      PdlSpacing.section,
      0,
      PdlSpacing.section,
      PdlSpacing.section,
    ),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlSectionHeader(
          title: 'home.week.title'.tr(),
          action: PdlButton(
            label: 'home.week.calendar'.tr(),
            variant: PdlButtonVariant.text,
            size: PdlButtonSize.sm,
            onPressed: () => context.go(Paths.calendar()),
          ),
        ),
        body,
      ],
    ),
  );
}

class _WeekEmpty extends StatelessWidget {
  const _WeekEmpty({required this.nothingElse});

  final bool nothingElse;

  @override
  Widget build(BuildContext context) {
    return PdlCard(
      key: keys.home.weekEmpty,
      flat: true,
      child: Row(
        children: <Widget>[
          Icon(PdlIcons.date, size: 24, color: context.pdl.textDimmed),
          const SizedBox(width: PdlSpacing.cardTight),
          Expanded(
            child: Text(
              (nothingElse ? 'home.week.nothingElse' : 'home.week.empty').tr(),
              style: context.pdlText.body,
            ),
          ),
        ],
      ),
    );
  }
}

/// Une ligne de « Cette semaine » : le jour en vignette, le titre, l'heure et
/// l'équipe. Tout vient de l'événement de calendrier, **sans appel**.
class WeekAgendaRow extends StatelessWidget {
  const WeekAgendaRow({super.key, required this.event});

  final CalendarEventDto event;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final DateTime start = AppFormatters.toDisplayTime(
      DateTime.parse(event.start),
    );
    final PublicationType type = event.type == 'TRIP_STAGE'
        ? PublicationType.trip
        : PublicationType.ride;
    final PdlTone tone = type.tone(c);
    final bool cancelled = event.status == 'CANCELLED';

    final String meta = <String>[
      if (!event.allDay) AppFormatters.formatTime(start),
      event.teamName,
    ].join(' · ');

    return PdlCard(
      padding: PdlCardPadding.tight,
      onTap: calendarEventTap(context, event),
      child: Row(
        children: <Widget>[
          _DayTile(date: start, tone: tone),
          const SizedBox(width: PdlSpacing.cardTight),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Text(
                  event.title,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: t.cardTitle.copyWith(color: c.text),
                ),
                const SizedBox(height: 2),
                Text(
                  meta,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: t.sub,
                ),
                if (event.registered || cancelled) ...<Widget>[
                  const SizedBox(height: 6),
                  Wrap(
                    spacing: PdlSpacing.badgeGap,
                    runSpacing: PdlSpacing.badgeGap,
                    children: <Widget>[
                      if (event.registered)
                        PdlBadge(
                          label: 'rides.registered'.tr(),
                          icon: PdlIcons.check,
                          tone: PdlDerivedTones.registered(c),
                        ),
                      if (cancelled)
                        PdlBadge(
                          label: 'calendar.cancelled'.tr(),
                          icon: PdlIcons.cancelled,
                          tone: Status.cancelled.tone(c),
                        ),
                    ],
                  ),
                ],
              ],
            ),
          ),
          Icon(PdlIcons.chevronRight, size: 18, color: c.textDimmed),
        ],
      ),
    );
  }
}

/// Le jour de l'événement en vignette — « sam. / 11 » —, dans la teinte de
/// son type (sortie ou étape), la même que le calendrier.
class _DayTile extends StatelessWidget {
  const _DayTile({required this.date, required this.tone});

  final DateTime date;
  final PdlTone tone;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    return Container(
      width: PdlMetrics.tapTarget,
      padding: const EdgeInsets.symmetric(vertical: 6),
      decoration: BoxDecoration(color: tone.soft, borderRadius: PdlRadii.mdAll),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          Text(
            AppFormatters.dayAbbrev(date.weekday),
            style: t.xs.copyWith(color: tone.onSoft),
          ),
          Text('${date.day}', style: t.cardTitle.copyWith(color: tone.onSoft)),
        ],
      ),
    );
  }
}
