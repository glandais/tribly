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
import '../../domain/ride_weather_display.dart';
import '../../../../keys.dart';
import '../../providers/ride_detail_provider.dart';
import '../../providers/ride_weather_provider.dart';
import '../pages/ride_weather_page.dart';
import 'ride_weather_widgets.dart';

/// La carte météo compacte du détail d'une sortie.
///
/// Le départ (icône, température, ressenti, pluie, vent), puis le vent le
/// long du parcours du **groupe sélectionné** (`selectedRideGroupProvider`,
/// que l'écran passe en [selectedGroupId]) et son alerte pluie. Un appui
/// ouvre l'écran « Météo du parcours » (`Navigator.push` : pas de route au
/// contrat, `docs/plans/2026-10-05-weather.md` §1).
///
/// Rien n'est rendu pour une sortie terminée ou annulée — sans même appeler
/// l'API —, ni pour un statut que l'app ne connaît pas. `NO_LOCATION` n'est
/// dit qu'aux organisateurs : un membre n'y peut rien.
class RideWeatherCard extends ConsumerWidget {
  const RideWeatherCard({
    super.key,
    required this.rideKey,
    required this.ride,
    required this.selectedGroupId,
  });

  final RideKey rideKey;
  final RideDto ride;
  final String? selectedGroupId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    if (ride.isPast || ride.isCancelled) return const SizedBox.shrink();

    final AsyncValue<RideWeatherDto> weather = ref.watch(
      rideWeatherProvider(rideKey),
    );
    final bool canSeeNoLocation = watchCanSeeWeatherNoLocation(
      ref,
      ride.team.slug,
    );

    Widget padded(Widget child) => Padding(
      padding: const EdgeInsets.fromLTRB(
        PdlSpacing.section,
        0,
        PdlSpacing.section,
        PdlSpacing.section,
      ),
      child: child,
    );

    void retry() => ref.invalidate(rideWeatherProvider(rideKey));

    return weather.when(
      loading: () => _loading(padded),
      // Un échec réseau se lit comme `UNAVAILABLE` : un enrichissement ne
      // casse pas l'écran, et « Réessayer » suffit.
      error: (Object _, StackTrace _) =>
          padded(_UnavailableCard(onRetry: retry)),
      data: (RideWeatherDto dto) {
        switch (rideWeatherViewOf(
          dto.status,
          canSeeNoLocation: canSeeNoLocation,
        )) {
          case RideWeatherView.hidden:
            return const SizedBox.shrink();
          case RideWeatherView.noLocation:
            return padded(
              PdlBanner(
                key: keys.ride.weatherNoLocation,
                tone: PdlBannerTone.info,
                icon: PdlIcons.weatherUnknown,
                message: 'rides.weather.noLocation'.tr(),
              ),
            );
          case RideWeatherView.unavailable:
            return padded(_UnavailableCard(onRetry: retry));
          case RideWeatherView.notYetAvailable:
            return padded(
              _NotYetAvailableCard(availableFrom: dto.availableFrom),
            );
          case RideWeatherView.forecast:
          case RideWeatherView.stale:
            return padded(
              _ForecastCard(
                weather: dto,
                stale:
                    WeatherStatus.fromJson(dto.status) == WeatherStatus.stale,
                leg: weatherLegFor(dto.legs, selectedGroupId),
                onOpen: () => RideWeatherPage.open(context, rideKey),
              ),
            );
        }
      },
    );
  }

  /// Pendant le chargement, on ne réserve que la place d'un bloc qu'on sait
  /// devoir afficher. Le détail porte déjà la ligne de résumé
  /// (`RideDto.weather`, la même que les listes), présente seulement pour
  /// `OK`, `STALE` et `NOT_YET_AVAILABLE` :
  /// - `OK`/`STALE` : la carte prévision suivra, son squelette tient sa place ;
  /// - `NOT_YET_AVAILABLE` : la carte est déjà connue, on la rend telle quelle ;
  /// - pas de résumé (brouillon, `NO_LOCATION`, rien en cache, statut
  ///   inconnu) : rien, comme le web. Le bloc qui apparaît ensuite (bandeau
  ///   aux organisateurs, « indisponible ») ne fait sauter l'écran qu'une fois,
  ///   et le cas le plus courant — un membre, sans lieu — pas du tout.
  Widget _loading(Widget Function(Widget) padded) {
    final RideWeatherSummaryDto? summary = ride.weather;
    if (summary == null) return const SizedBox.shrink();
    return switch (WeatherStatus.fromJson(summary.status)) {
      WeatherStatus.ok || WeatherStatus.stale => padded(
        const PdlSkeleton(height: 96, borderRadius: PdlRadii.cardAll),
      ),
      WeatherStatus.notYetAvailable => padded(
        _NotYetAvailableCard(availableFrom: summary.availableFrom),
      ),
      _ => const SizedBox.shrink(),
    };
  }
}

class _ForecastCard extends ConsumerWidget {
  const _ForecastCard({
    required this.weather,
    required this.stale,
    required this.leg,
    required this.onOpen,
  });

  final RideWeatherDto weather;
  final bool stale;
  final WeatherLegDto? leg;
  final VoidCallback onOpen;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final UnitSystem units = ref.watch(unitSystemProvider);
    final WeatherConditionsDto? now = weather.departure.conditions;
    final WeatherLegDto? leg = this.leg;
    final WeatherRainAlertDto? rain = leg?.rainAlert;

    return PdlCard(
      key: keys.ride.weatherCard,
      flat: true,
      onTap: onOpen,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Row(
            children: <Widget>[
              Expanded(
                child: Text('rides.weather.atDeparture'.tr(), style: t.xs),
              ),
              if (stale)
                PdlBadge(
                  label: 'rides.weather.stale'.tr(),
                  tone: PdlFamily.yellow.soft(c),
                  icon: PdlIcons.warning,
                ),
            ],
          ),
          const SizedBox(height: PdlSpacing.badgeGap),
          if (now != null)
            _DepartureSummary(conditions: now, units: units)
          else
            Text('rides.weather.unavailableTitle'.tr(), style: t.sub),
          if (leg != null && leg.segments.isNotEmpty) ...<Widget>[
            const SizedBox(height: PdlSpacing.cardTight),
            WeatherWindSegments(leg: leg, units: units),
          ],
          if (rain != null) ...<Widget>[
            const SizedBox(height: PdlSpacing.chipGap),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Icon(PdlIcons.precipitation, size: 16, color: c.warning),
                const SizedBox(width: PdlSpacing.badgeGap),
                Expanded(
                  child: Text(
                    formatRainAlert(rain, units),
                    style: t.xs.copyWith(color: c.text),
                  ),
                ),
              ],
            ),
          ],
          // Le crédit CC BY 4.0 en légende, à côté du lien vers l'écran qui le
          // rend cliquable ; il passe dessous quand la place manque.
          Wrap(
            alignment: WrapAlignment.spaceBetween,
            crossAxisAlignment: WrapCrossAlignment.center,
            children: <Widget>[
              PdlButton(
                key: keys.ride.weatherOpenButton,
                label: 'rides.weather.viewRoute'.tr(),
                variant: PdlButtonVariant.text,
                size: PdlButtonSize.sm,
                icon: PdlIcons.chevronRight,
                onPressed: onOpen,
              ),
              Text(
                weatherAttributionLabel(weather.attribution),
                key: keys.ride.weatherAttribution,
                style: t.xs,
              ),
            ],
          ),
        ],
      ),
    );
  }
}

/// Icône, température et condition, puis ressenti, pluie et vent.
class _DepartureSummary extends StatelessWidget {
  const _DepartureSummary({required this.conditions, required this.units});

  final WeatherConditionsDto conditions;
  final UnitSystem units;

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
    final String feelsLike = 'rides.weather.feelsLike'.tr(
      namedArgs: <String, String>{
        'temperature': AppFormatters.formatTemperature(
          conditions.apparentTemperature,
          units,
        ),
      },
    );
    final String? rainChance = conditions.precipitationProbability == null
        ? null
        : 'rides.weather.rainChance'.tr(
            namedArgs: <String, String>{
              'probability': AppFormatters.formatPercent(
                conditions.precipitationProbability!,
              ),
            },
          );
    final String wind = formatWind(conditions.wind, units, gusts: false);

    return Semantics(
      container: true,
      label: <String>[
        'rides.weather.atDeparture'.tr(),
        temperature,
        condition,
        feelsLike,
        ?rainChance,
        wind,
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
                  size: 32,
                ),
                const SizedBox(width: PdlSpacing.chipGap),
                Text(temperature, style: t.statBigValue),
                const SizedBox(width: PdlSpacing.chipGap),
                Expanded(
                  child: Text(
                    condition,
                    style: t.sub,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
            const SizedBox(height: PdlSpacing.badgeGap),
            Wrap(
              spacing: PdlSpacing.statsNowrap,
              runSpacing: PdlSpacing.badgeGap,
              crossAxisAlignment: WrapCrossAlignment.center,
              children: <Widget>[
                Text(feelsLike, style: t.xs),
                if (rainChance != null) Text(rainChance, style: t.xs),
                WeatherWindLine(
                  wind: conditions.wind,
                  units: units,
                  gusts: false,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _NotYetAvailableCard extends StatelessWidget {
  const _NotYetAvailableCard({required this.availableFrom});

  final String? availableFrom;

  @override
  Widget build(BuildContext context) {
    final PdlColors c = context.pdl;
    final PdlTypography t = context.pdlText;
    final DateTime? from = availableFrom == null
        ? null
        : DateTime.tryParse(availableFrom!);
    // Sans date lisible, la règle des sept jours, comme le web.
    return PdlCard(
      key: keys.ride.weatherNotYetAvailable,
      flat: true,
      child: Row(
        children: <Widget>[
          Icon(PdlIcons.weatherUnknown, size: 20, color: c.textDimmed),
          const SizedBox(width: PdlSpacing.chipGap),
          Expanded(child: Text(notYetAvailableMessage(from), style: t.sub)),
        ],
      ),
    );
  }
}

class _UnavailableCard extends StatelessWidget {
  const _UnavailableCard({required this.onRetry});

  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    return PdlBanner(
      key: keys.ride.weatherUnavailable,
      tone: PdlBannerTone.info,
      icon: PdlIcons.weatherUnknown,
      title: 'rides.weather.unavailableTitle'.tr(),
      message: 'rides.weather.unavailableMessage'.tr(),
      action: PdlButton(
        key: keys.ride.weatherRetryButton,
        label: 'common.retry'.tr(),
        variant: PdlButtonVariant.outline,
        size: PdlButtonSize.sm,
        onPressed: onRetry,
      ),
    );
  }
}
