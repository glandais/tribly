import 'package:easy_localization/easy_localization.dart';
// `Visibility` est à la fois un widget Flutter et un enum du contrat.
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/theme/enum_colors.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/utils/link_launcher.dart';
import '../../../../keys.dart';
import '../../domain/ride_weather_display.dart';
import '../../providers/ride_detail_provider.dart';
import '../../providers/ride_group_selection_provider.dart';
import '../../providers/ride_weather_provider.dart';
import '../widgets/ride_weather_widgets.dart';

/// « Météo du parcours » — l'écran poussé depuis la carte météo du détail.
///
/// Poussé par `Navigator.push` et absent de `contracts/routes.yaml` : il n'a
/// pas d'URL à partager, il prolonge le détail (plan météo §1). Il partage
/// avec lui la météo (`rideWeatherProvider`) **et** le groupe sélectionné
/// (`selectedRideGroupProvider`) : changer de groupe ici le change au retour.
///
/// De haut en bas : sélecteur de groupe, le départ, l'étape du groupe
/// (horaires et vitesse, alerte pluie, exposition, vent le long du parcours,
/// frise verticale des points de passage), puis l'attribution.
class RideWeatherPage extends ConsumerWidget {
  const RideWeatherPage({super.key, required this.rideKey});

  final RideKey rideKey;

  static Future<void> open(BuildContext context, RideKey rideKey) =>
      Navigator.of(context).push(
        MaterialPageRoute<void>(
          builder: (BuildContext _) => RideWeatherPage(rideKey: rideKey),
        ),
      );

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final AsyncValue<RideWeatherDto> weather = ref.watch(
      rideWeatherProvider(rideKey),
    );
    final RideDto? ride = ref.watch(rideDetailProvider(rideKey)).value;
    final bool canSeeNoLocation = watchCanSeeWeatherNoLocation(
      ref,
      rideKey.teamSlug,
    );

    Future<void> refresh() async {
      ref.invalidate(rideWeatherProvider(rideKey));
      try {
        await ref.read(rideWeatherProvider(rideKey).future);
      } catch (_) {
        // L'état d'erreur se rend tout seul ; le geste n'a rien à ajouter.
      }
    }

    return PdlScreenScaffold(
      appBar: PdlAppBar(
        title: 'rides.weather.routeTitle'.tr(),
        onBack: () => Navigator.of(context).maybePop(),
        backSemanticLabel: 'common.back'.tr(),
      ),
      onRefresh: refresh,
      slivers: <Widget>[
        ...weather.when(
          loading: () => <Widget>[
            const SliverPadding(
              padding: EdgeInsets.all(PdlSpacing.section),
              sliver: SliverToBoxAdapter(
                child: Column(
                  children: <Widget>[
                    PdlSkeleton(height: 120, borderRadius: PdlRadii.cardAll),
                    SizedBox(height: PdlSpacing.section),
                    PdlSkeleton(height: 240, borderRadius: PdlRadii.cardAll),
                  ],
                ),
              ),
            ),
          ],
          error: (Object _, StackTrace _) => <Widget>[
            _unavailable(() => ref.invalidate(rideWeatherProvider(rideKey))),
          ],
          data: (RideWeatherDto dto) => _content(
            context,
            ref,
            dto,
            ride,
            canSeeNoLocation: canSeeNoLocation,
          ),
        ),
        const SliverToBoxAdapter(child: SizedBox(height: PdlSpacing.section)),
      ],
    );
  }

  Widget _unavailable(VoidCallback onRetry) => SliverToBoxAdapter(
    child: PdlEmptyState(
      key: keys.ride.weatherUnavailable,
      variant: PdlEmptyVariant.error,
      icon: PdlIcons.weatherUnknown,
      title: 'rides.weather.unavailableTitle'.tr(),
      message: 'rides.weather.unavailableMessage'.tr(),
      actions: <Widget>[
        PdlButton(
          key: keys.ride.weatherRetryButton,
          label: 'common.retry'.tr(),
          variant: PdlButtonVariant.outline,
          onPressed: onRetry,
        ),
      ],
    ),
  );

  List<Widget> _content(
    BuildContext context,
    WidgetRef ref,
    RideWeatherDto dto,
    RideDto? ride, {
    required bool canSeeNoLocation,
  }) {
    Widget padded(Widget child) => SliverPadding(
      padding: const EdgeInsets.fromLTRB(
        PdlSpacing.section,
        PdlSpacing.section,
        PdlSpacing.section,
        0,
      ),
      sliver: SliverToBoxAdapter(child: child),
    );

    switch (rideWeatherViewOf(dto.status, canSeeNoLocation: canSeeNoLocation)) {
      case RideWeatherView.hidden:
        return const <Widget>[];
      case RideWeatherView.unavailable:
        return <Widget>[
          _unavailable(() => ref.invalidate(rideWeatherProvider(rideKey))),
        ];
      case RideWeatherView.noLocation:
        return <Widget>[
          padded(
            PdlBanner(
              key: keys.ride.weatherNoLocation,
              tone: PdlBannerTone.info,
              icon: PdlIcons.weatherUnknown,
              message: 'rides.weather.noLocation'.tr(),
            ),
          ),
        ];
      case RideWeatherView.notYetAvailable:
        final DateTime? from = dto.availableFrom == null
            ? null
            : DateTime.tryParse(dto.availableFrom!);
        if (from == null) return const <Widget>[];
        return <Widget>[
          padded(
            PdlBanner(
              key: keys.ride.weatherNotYetAvailable,
              tone: PdlBannerTone.info,
              icon: PdlIcons.date,
              message: 'rides.weather.notYetAvailable'.tr(
                namedArgs: <String, String>{
                  'date': AppFormatters.formatFullDate(from),
                },
              ),
            ),
          ),
        ];
      case RideWeatherView.forecast:
      case RideWeatherView.stale:
        break;
    }

    final bool stale =
        WeatherStatus.fromJson(dto.status) == WeatherStatus.stale;
    final String? selected =
        ref.watch(selectedRideGroupProvider(rideKey)) ??
        ride?.registeredGroupId ??
        ((ride == null || ride.groups.isEmpty) ? null : ride.groups.first.id);
    final WeatherLegDto? leg = weatherLegFor(dto.legs, selected);
    final DateTime? fetchedAt = dto.fetchedAt == null
        ? null
        : DateTime.tryParse(dto.fetchedAt!);

    return <Widget>[
      if (dto.legs.length > 1)
        SliverPadding(
          padding: const EdgeInsets.only(top: PdlSpacing.section),
          sliver: SliverToBoxAdapter(
            child: _GroupSelector(
              legs: dto.legs,
              ride: ride,
              selected: leg,
              onSelect: (String groupId) =>
                  ref.read(selectedRideGroupProvider(rideKey).notifier).state =
                      groupId,
            ),
          ),
        ),
      if (stale)
        padded(
          PdlBanner(
            tone: PdlBannerTone.warn,
            icon: PdlIcons.warning,
            title: 'rides.weather.stale'.tr(),
            message: fetchedAt == null
                ? ''
                : 'rides.weather.staleMessage'.tr(
                    namedArgs: <String, String>{
                      'time': AppFormatters.formatLongDateTime(fetchedAt),
                    },
                  ),
          ),
        ),
      padded(_DepartureSection(departure: dto.departure)),
      if (leg != null) ...<Widget>[
        padded(_LegHeader(leg: leg, groupName: _groupName(ride, leg))),
        ..._legBody(leg, padded),
      ],
      padded(_Attribution(attribution: dto.attribution, fetchedAt: fetchedAt)),
    ];
  }

  List<Widget> _legBody(WeatherLegDto leg, Widget Function(Widget) padded) {
    final WeatherStatus legStatus = WeatherStatus.fromJson(leg.status);
    if (legStatus == WeatherStatus.noLocation) {
      return <Widget>[
        padded(
          PdlBanner(
            tone: PdlBannerTone.info,
            icon: PdlIcons.route,
            message: 'rides.weather.legNoRoute'.tr(),
          ),
        ),
      ];
    }
    // Comme le web (`LegWeather`) : la frise ne se déroule que pour une étape
    // prévue (`OK`/`STALE`) avec des points. `UNAVAILABLE`, `NOT_YET_AVAILABLE`
    // (le groupe part au-delà de l'horizon) ou un statut inconnu de cette build :
    // un seul bandeau, plutôt qu'une frise de points sans prévision.
    final bool forecast =
        legStatus == WeatherStatus.ok || legStatus == WeatherStatus.stale;
    if (!forecast || leg.checkpoints.isEmpty) {
      return <Widget>[
        padded(
          PdlBanner(
            key: keys.ride.weatherLegUnavailable,
            tone: PdlBannerTone.info,
            icon: PdlIcons.weatherUnknown,
            message: 'rides.weather.legUnavailable'.tr(),
          ),
        ),
      ];
    }
    return <Widget>[
      if (leg.rainAlert != null)
        padded(_RainAlertBanner(alert: leg.rainAlert!)),
      if (leg.segments.isNotEmpty) padded(_WindSection(leg: leg)),
      if (leg.checkpoints.isNotEmpty) padded(_Timeline(leg: leg)),
    ];
  }

  String? _groupName(RideDto? ride, WeatherLegDto leg) {
    if (ride == null || leg.groupId == null) return null;
    for (final RideGroupDto g in ride.groups) {
      if (g.id == leg.groupId) return g.name;
    }
    return null;
  }
}

// ─────────────────────────────────────────────────────── sélecteur de groupe

class _GroupSelector extends StatelessWidget {
  const _GroupSelector({
    required this.legs,
    required this.ride,
    required this.selected,
    required this.onSelect,
  });

  final List<WeatherLegDto> legs;
  final RideDto? ride;
  final WeatherLegDto? selected;
  final ValueChanged<String> onSelect;

  @override
  Widget build(BuildContext context) {
    final Map<String, RideGroupDto> groups = <String, RideGroupDto>{
      for (final RideGroupDto g in ride?.groups ?? const <RideGroupDto>[])
        g.id: g,
    };
    return Semantics(
      label: 'rides.weather.group'.tr(),
      container: true,
      child: PdlChipRow(
        children: <Widget>[
          for (final WeatherLegDto leg in legs)
            if (leg.groupId != null)
              PdlChip(
                key: keys.ride.weatherGroupChip(leg.groupId!),
                label: groups[leg.groupId]?.name ?? '—',
                selected: identical(leg, selected),
                onTap: () => onSelect(leg.groupId!),
              ),
        ],
      ),
    );
  }
}

// ──────────────────────────────────────────────────────────────── le départ

class _DepartureSection extends ConsumerWidget {
  const _DepartureSection({required this.departure});

  final DepartureWeatherDto departure;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);
    final WeatherConditionsDto? now = departure.conditions;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlSectionHeader(title: 'rides.weather.atDeparture'.tr()),
        PdlCard(
          flat: true,
          child: now == null
              ? Text('rides.weather.unavailableTitle'.tr(), style: t.sub)
              : _ConditionsBlock(
                  conditions: now,
                  units: units,
                  sunrise: departure.sunrise,
                  sunset: departure.sunset,
                ),
        ),
      ],
    );
  }
}

class _ConditionsBlock extends StatelessWidget {
  const _ConditionsBlock({
    required this.conditions,
    required this.units,
    this.sunrise,
    this.sunset,
  });

  final WeatherConditionsDto conditions;
  final UnitSystem units;
  final String? sunrise;
  final String? sunset;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    final String temperature = AppFormatters.formatTemperature(
      conditions.temperature,
      units,
    );
    final String condition = weatherConditionOf(
      conditions.condition,
    ).labelKey.tr();
    final List<String> facts = <String>[
      'rides.weather.feelsLike'.tr(
        namedArgs: <String, String>{
          'temperature': AppFormatters.formatTemperature(
            conditions.apparentTemperature,
            units,
          ),
        },
      ),
      if (conditions.precipitationProbability != null)
        'rides.weather.rainChance'.tr(
          namedArgs: <String, String>{
            'probability': AppFormatters.formatPercent(
              conditions.precipitationProbability!,
            ),
          },
        ),
      if (conditions.precipitation > 0)
        'rides.weather.precipitation'.tr(
          namedArgs: <String, String>{
            'amount': AppFormatters.formatPrecipitation(
              conditions.precipitation,
            ),
          },
        ),
    ];
    final String wind = formatWind(conditions.wind, units);
    final String? rise = sunrise == null
        ? null
        : 'rides.weather.sunrise'.tr(
            namedArgs: <String, String>{'time': formatWeatherTime(sunrise!)},
          );
    final String? set = sunset == null
        ? null
        : 'rides.weather.sunset'.tr(
            namedArgs: <String, String>{'time': formatWeatherTime(sunset!)},
          );

    return Semantics(
      container: true,
      label: <String>[
        temperature,
        condition,
        ...facts,
        wind,
        ?rise,
        ?set,
      ].join(', '),
      child: ExcludeSemantics(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Row(
              children: <Widget>[
                WeatherConditionIcon(
                  condition: conditions.condition,
                  daylight: conditions.daylight,
                  size: 40,
                ),
                const SizedBox(width: PdlSpacing.cardTight),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(temperature, style: t.statBigValue),
                      Text(condition, style: t.sub),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: PdlSpacing.chipGap),
            Wrap(
              spacing: PdlSpacing.statsNowrap,
              runSpacing: PdlSpacing.badgeGap,
              crossAxisAlignment: WrapCrossAlignment.center,
              children: <Widget>[
                for (final String f in facts) Text(f, style: t.xs),
                WeatherWindLine(wind: conditions.wind, units: units),
              ],
            ),
            if (rise != null || set != null) ...<Widget>[
              const SizedBox(height: PdlSpacing.chipGap),
              Wrap(
                spacing: PdlSpacing.statsNowrap,
                runSpacing: PdlSpacing.badgeGap,
                children: <Widget>[
                  if (rise != null)
                    _IconText(icon: PdlIcons.sunrise, text: rise),
                  if (set != null) _IconText(icon: PdlIcons.sunset, text: set),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _IconText extends StatelessWidget {
  const _IconText({required this.icon, required this.text});

  final IconData icon;
  final String text;

  @override
  Widget build(BuildContext context) => Row(
    mainAxisSize: MainAxisSize.min,
    children: <Widget>[
      Icon(icon, size: 14, color: context.pdl.textDimmed),
      const SizedBox(width: 2),
      Flexible(child: Text(text, style: context.pdlText.xs)),
    ],
  );
}

// ─────────────────────────────────────────────────────────────── l'étape

class _LegHeader extends ConsumerWidget {
  const _LegHeader({required this.leg, required this.groupName});

  final WeatherLegDto leg;
  final String? groupName;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);
    final String speed = AppFormatters.formatSpeed(leg.averageSpeed, units);
    final WindDto? prevailing = leg.prevailingWind;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlSectionHeader(
          title: groupName ?? 'rides.weather.routeTitle'.tr(),
          count: leg.distance > 0
              ? AppFormatters.formatDistance(leg.distance, units)
              : null,
        ),
        Text(
          'rides.weather.legTimes'.tr(
            namedArgs: <String, String>{
              'start': formatWeatherTime(leg.startTime),
              'arrival': formatWeatherTime(leg.arrivalTime),
            },
          ),
          style: t.body,
        ),
        const SizedBox(height: 2),
        Text(
          (leg.speedIsDefault
                  ? 'rides.weather.legSpeedDefault'
                  : 'rides.weather.legSpeed')
              .tr(namedArgs: <String, String>{'speed': speed}),
          style: t.xs,
        ),
        if (prevailing != null) ...<Widget>[
          const SizedBox(height: 2),
          Text(
            'rides.weather.prevailingWind'.tr(
              namedArgs: <String, String>{
                'wind': formatWind(prevailing, units),
              },
            ),
            style: t.xs,
          ),
        ],
      ],
    );
  }
}

class _RainAlertBanner extends ConsumerWidget {
  const _RainAlertBanner({required this.alert});

  final WeatherRainAlertDto alert;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final UnitSystem units = ref.watch(unitSystemProvider);
    return PdlBanner(
      tone: PdlBannerTone.warn,
      icon: weatherConditionOf(alert.condition).icon(daylight: true),
      message: formatRainAlert(alert, units),
    );
  }
}

class _WindSection extends ConsumerWidget {
  const _WindSection({required this.leg});

  final WeatherLegDto leg;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlSectionHeader(title: 'rides.weather.exposureTitle'.tr()),
        PdlCard(
          flat: true,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              Text('rides.weather.segmentsTitle'.tr(), style: t.xs),
              const SizedBox(height: PdlSpacing.badgeGap),
              WeatherWindSegments(leg: leg, units: units),
              const SizedBox(height: PdlSpacing.badgeGap),
              // Les bornes de la barre : elle se lit du départ à l'arrivée.
              ExcludeSemantics(
                child: Row(
                  children: <Widget>[
                    Text(AppFormatters.formatDistance(0, units), style: t.xs),
                    const Spacer(),
                    Text(
                      AppFormatters.formatDistance(leg.distance, units),
                      style: t.xs,
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

// ───────────────────────────────────────────────────── frise des passages

class _Timeline extends StatelessWidget {
  const _Timeline({required this.leg});

  final WeatherLegDto leg;

  @override
  Widget build(BuildContext context) {
    final List<WeatherCheckpointDto> points = leg.checkpoints;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        PdlSectionHeader(title: 'rides.weather.timelineTitle'.tr()),
        for (int i = 0; i < points.length; i++)
          _CheckpointRow(
            key: keys.ride.weatherCheckpoint(points[i].indexField),
            checkpoint: points[i],
            first: i == 0,
            last: i == points.length - 1,
          ),
      ],
    );
  }
}

class _CheckpointRow extends ConsumerWidget {
  const _CheckpointRow({
    super.key,
    required this.checkpoint,
    required this.first,
    required this.last,
  });

  final WeatherCheckpointDto checkpoint;
  final bool first;
  final bool last;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);
    final WeatherCheckpointDto p = checkpoint;
    final WeatherConditionsDto? w = p.weather;
    final RelativeWind? relative = relativeWindOf(p.relativeWind);

    final String time = formatWeatherTime(p.time);
    final String distance = AppFormatters.formatDistance(p.distance, units);
    final String kind = _kindLabel(p.kind);
    final String? temperature = w == null
        ? null
        : AppFormatters.formatTemperature(w.temperature, units);
    final String? condition = w == null
        ? null
        : weatherConditionOf(w.condition).labelKey.tr();
    final String? rain = w?.precipitationProbability == null
        ? null
        : 'rides.weather.rainChance'.tr(
            namedArgs: <String, String>{
              'probability': AppFormatters.formatPercent(
                w!.precipitationProbability!,
              ),
            },
          );
    final String? relativeLabel = relative?.labelKey.tr();
    final String? wind = w == null
        ? null
        : formatWind(w.wind, units, gusts: true);

    final bool endpoint = first || last;
    final Color dot = endpoint ? c.primary : c.textDimmed;

    return Semantics(
      container: true,
      label: <String>[
        kind,
        time,
        distance,
        ?temperature,
        ?condition,
        ?relativeLabel,
        ?wind,
        ?rain,
        if (w == null) 'rides.weather.checkpointNoWeather'.tr(),
      ].join(', '),
      child: ExcludeSemantics(
        child: IntrinsicHeight(
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              SizedBox(
                width: 64,
                child: Padding(
                  padding: const EdgeInsets.only(top: PdlSpacing.badgeGap),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(time, style: t.bodyStrong),
                      Text(distance, style: t.xs),
                    ],
                  ),
                ),
              ),
              // Le rail : un trait continu, interrompu au premier et au
              // dernier point, et une pastille par point.
              SizedBox(
                width: 24,
                child: Column(
                  children: <Widget>[
                    Container(
                      width: 2,
                      height: 10,
                      color: first ? null : c.border,
                    ),
                    Container(
                      width: endpoint ? 12 : 8,
                      height: endpoint ? 12 : 8,
                      decoration: BoxDecoration(
                        color: dot,
                        shape: BoxShape.circle,
                      ),
                    ),
                    Expanded(
                      child: Container(width: 2, color: last ? null : c.border),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: PdlSpacing.chipGap),
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.only(
                    top: PdlSpacing.badgeGap,
                    bottom: PdlSpacing.section,
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: <Widget>[
                      Text(kind, style: t.xs),
                      const SizedBox(height: 2),
                      if (w == null)
                        Text(
                          'rides.weather.checkpointNoWeather'.tr(),
                          style: t.sub,
                        )
                      else ...<Widget>[
                        Row(
                          children: <Widget>[
                            WeatherConditionIcon(
                              condition: w.condition,
                              daylight: w.daylight,
                            ),
                            const SizedBox(width: PdlSpacing.badgeGap),
                            Text(temperature!, style: t.statValue),
                            const SizedBox(width: PdlSpacing.chipGap),
                            Expanded(
                              child: Text(
                                condition!,
                                style: t.sub,
                                maxLines: 1,
                                overflow: TextOverflow.ellipsis,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: PdlSpacing.badgeGap),
                        Wrap(
                          spacing: PdlSpacing.chipGap,
                          runSpacing: PdlSpacing.badgeGap,
                          crossAxisAlignment: WrapCrossAlignment.center,
                          children: <Widget>[
                            if (relative != null) ...<Widget>[
                              Row(
                                mainAxisSize: MainAxisSize.min,
                                children: <Widget>[
                                  if (p.relativeWindAngle != null) ...<Widget>[
                                    PdlWindArrow(
                                      angle: p.relativeWindAngle!,
                                      color: relativeWindColor(c, relative),
                                    ),
                                    const SizedBox(width: 2),
                                  ],
                                  PdlBadge(
                                    label: relative.shortLabelKey.tr(),
                                    tone: relative.tone(c),
                                  ),
                                ],
                              ),
                            ],
                            Text(wind!, style: t.xs),
                            if (rain != null) Text(rain, style: t.xs),
                          ],
                        ),
                      ],
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  String _kindLabel(String kind) =>
      switch (WeatherCheckpointKind.fromJson(kind)) {
        WeatherCheckpointKind.start => 'rides.weather.checkpointKind.start',
        WeatherCheckpointKind.finish => 'rides.weather.checkpointKind.finish',
        WeatherCheckpointKind.enRoute || WeatherCheckpointKind.$unknown =>
          'rides.weather.checkpointKind.enRoute',
      }.tr();
}

// ─────────────────────────────────────────────────────────── attribution

/// Le crédit que la licence des prévisions demande (CC BY 4.0), et l'heure
/// de la prévision la plus ancienne lue.
class _Attribution extends StatelessWidget {
  const _Attribution({required this.attribution, required this.fetchedAt});

  final WeatherAttributionDto attribution;
  final DateTime? fetchedAt;

  @override
  Widget build(BuildContext context) {
    final PdlTypography t = context.pdlText;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        if (fetchedAt != null)
          Text(
            'rides.weather.updatedAt'.tr(
              namedArgs: <String, String>{
                'time': AppFormatters.formatLongDateTime(fetchedAt!),
              },
            ),
            style: t.xs,
          ),
        PdlButton(
          label: 'rides.weather.attribution'.tr(
            namedArgs: <String, String>{'name': attribution.name},
          ),
          variant: PdlButtonVariant.text,
          size: PdlButtonSize.sm,
          icon: PdlIcons.openExternal,
          onPressed: () => openLink(context, attribution.url),
        ),
      ],
    );
  }
}
