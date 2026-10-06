import 'package:easy_localization/easy_localization.dart';
// `Visibility` est à la fois un widget Flutter et un enum du contrat.
import 'package:flutter/material.dart' hide Visibility;
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/pdl/pdl.dart';
import '../../../../core/preferences/user_preferences_provider.dart';
import '../../../../core/theme/pdl_colors.dart';
import '../../../../core/theme/pdl_icons.dart';
import '../../../../core/theme/pdl_tokens.dart';
import '../../../../core/theme/pdl_typography.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../keys.dart';
import '../../domain/ride_weather_display.dart';
import '../../providers/ride_detail_provider.dart';
import '../../providers/ride_group_selection_provider.dart';
import '../../providers/ride_weather_provider.dart';
import '../widgets/ride_weather_widgets.dart';
import '../widgets/weather_leg_sections.dart';

/// « Météo du parcours » — l'écran poussé depuis la carte météo du détail.
///
/// Poussé par `Navigator.push` et absent de `contracts/routes.yaml` : il n'a
/// pas d'URL à partager, il prolonge le détail (plan météo §1). Il partage
/// avec lui la météo (`rideWeatherProvider`) **et** le groupe sélectionné
/// (`selectedRideGroupProvider`) : changer de groupe ici le change au retour.
///
/// De haut en bas : sélecteur de groupe, le départ, l'étape du groupe
/// (horaires et vitesse, alerte pluie, exposition, vent le long du parcours,
/// frise verticale des points de passage), puis l'attribution. Tout ce qui
/// suit le départ vit dans `weather_leg_sections.dart`, partagé avec l'écran
/// météo d'une étape de voyage.
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
            weatherUnavailableSliver(
              () => ref.invalidate(rideWeatherProvider(rideKey)),
            ),
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
        // Le détail ne pousse l'écran que depuis une prévision ; on peut
        // pourtant y arriver ici (pull-to-refresh après une annulation,
        // statut d'une build plus récente) : un état neutre, jamais un écran
        // blanc.
        return <Widget>[
          SliverToBoxAdapter(
            child: PdlEmptyState(
              key: keys.ride.weatherEmpty,
              variant: PdlEmptyVariant.empty,
              icon: PdlIcons.weatherUnknown,
              title: 'rides.weather.emptyTitle'.tr(),
              message: 'rides.weather.emptyMessage'.tr(),
            ),
          ),
        ];
      case RideWeatherView.unavailable:
        return <Widget>[
          weatherUnavailableSliver(
            () => ref.invalidate(rideWeatherProvider(rideKey)),
          ),
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
        return <Widget>[
          padded(
            PdlBanner(
              key: keys.ride.weatherNotYetAvailable,
              tone: PdlBannerTone.info,
              icon: PdlIcons.date,
              message: notYetAvailableMessage(from),
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
      if (stale) padded(WeatherStaleBanner(fetchedAt: fetchedAt)),
      padded(
        _DepartureSection(departure: dto.departure, timezone: ride?.timezone),
      ),
      if (leg != null) ...<Widget>[
        padded(
          WeatherLegHeader(
            leg: leg,
            title: _groupName(ride, leg),
            timezone: ride?.timezone,
          ),
        ),
        ...weatherLegBody(leg, padded, ride?.timezone),
      ],
      padded(
        WeatherAttributionBlock(
          attribution: dto.attribution,
          fetchedAt: fetchedAt,
        ),
      ),
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
  const _DepartureSection({required this.departure, required this.timezone});

  final DepartureWeatherDto departure;

  /// Le fuseau de la sortie : lever et coucher du soleil s'y lisent.
  final String? timezone;

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
                  timezone: timezone,
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
    this.timezone,
  });

  final WeatherConditionsDto conditions;
  final UnitSystem units;
  final String? sunrise;
  final String? sunset;
  final String? timezone;

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
            namedArgs: <String, String>{
              'time': formatWeatherTime(sunrise!, timezone),
            },
          );
    final String? set = sunset == null
        ? null
        : 'rides.weather.sunset'.tr(
            namedArgs: <String, String>{
              'time': formatWeatherTime(sunset!, timezone),
            },
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
