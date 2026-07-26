import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

import '../../api/generated/export.dart';
import '../units/unit_system.dart';

/// **Le** point d'entrée du formatage de l'app.
///
/// Il n'y en a pas d'autre : pas de `toStringAsFixed` dans une page, pas de
/// `'km'` codé en dur dans un widget, pas de second fichier de libellés de
/// filtres. Tout ce qui se lit à l'écran sous forme de nombre, d'unité, de
/// date ou de prix passe par ici.
///
/// Trois règles typographiques, appliquées sans exception :
///
/// * **espace insécable** (U+00A0) entre la valeur et son unité — « 46,3 km »
///   ne se coupe jamais en fin de ligne ;
/// * **espace fine insécable** (U+202F) comme séparateur de milliers, ce que
///   `NumberFormat.decimalPattern('fr')` produit déjà : on ne la fabrique pas
///   à la main, on se contente de ne pas la casser ;
/// * la locale est celle d'`Intl` — les tests peuvent donc la fixer avec
///   `Intl.withLocale`, et rien n'est mis en cache au-delà de sa locale.
///
/// Les unités viennent de `UserDto.unitSystem` via `unitSystemProvider`. Elles
/// se passent explicitement en argument : un formateur qui irait lire un
/// provider tout seul ne serait plus testable, et une valeur par défaut
/// implicite finirait par afficher des kilomètres à un utilisateur américain.
/// Le défaut [UnitSystem.metric] n'est là que pour les rares appels qui n'ont
/// aucun utilisateur sous la main.
class AppFormatters {
  AppFormatters._();

  /// Espace insécable, entre une valeur et son unité.
  static const String nbsp = ' ';

  /// Espace fine insécable, séparateur de milliers du français.
  static const String narrowNbsp = ' ';

  // ─────────────────────────────────────────────────────────────────────────
  // Nombres
  // ─────────────────────────────────────────────────────────────────────────

  static final Map<String, NumberFormat> _decimalCache =
      <String, NumberFormat>{};

  /// `NumberFormat` de la locale courante, à nombre de décimales fixé.
  ///
  /// Mis en cache **par locale**, sans quoi un changement de langue
  /// continuerait d'afficher les séparateurs de l'ancienne.
  static NumberFormat _decimal(int fractionDigits) {
    final String locale = Intl.getCurrentLocale();
    return _decimalCache.putIfAbsent(
      '$locale/$fractionDigits',
      () => NumberFormat.decimalPattern(locale)
        ..minimumFractionDigits = fractionDigits
        ..maximumFractionDigits = fractionDigits,
    );
  }

  /// Nombre seul, sans unité.
  static String formatNumber(num value, {int fractionDigits = 0}) =>
      _decimal(fractionDigits).format(value);

  /// Assemble une valeur et son unité avec l'espace insécable qui les lie.
  static String withUnit(String value, String symbol) => '$value$nbsp$symbol';

  // ─────────────────────────────────────────────────────────────────────────
  // Distances, dénivelés, vitesses, pentes
  // ─────────────────────────────────────────────────────────────────────────

  /// Distance d'un parcours, au dixième près : « 46,3 km », « 28.8 mi ».
  ///
  /// L'entrée est **toujours en mètres**, comme l'API.
  static String formatDistance(
    num meters, [
    UnitSystem units = UnitSystem.metric,
  ]) => withUnit(
    formatNumber(units.longDistance(meters), fractionDigits: 1),
    units.longDistanceSymbol,
  );

  /// Distance arrondie, pour les bornes de filtre et les puces : « 30 km ».
  static String formatDistanceRounded(
    num meters, [
    UnitSystem units = UnitSystem.metric,
  ]) => withUnit(
    formatNumber(units.longDistance(meters)),
    units.longDistanceSymbol,
  );

  /// Dénivelé ou altitude, à l'unité près : « 9 840 m », « 32 283 ft ».
  static String formatElevation(
    num meters, [
    UnitSystem units = UnitSystem.metric,
  ]) => withUnit(
    formatNumber(units.shortDistance(meters)),
    units.shortDistanceSymbol,
  );

  /// Dénivelé positif, suffixé « D+ ».
  static String formatElevationGain(
    num meters, [
    UnitSystem units = UnitSystem.metric,
  ]) => '${formatElevation(meters, units)} ${'units.elevationGain'.tr()}';

  /// Dénivelé négatif, suffixé « D− ».
  static String formatElevationLoss(
    num meters, [
    UnitSystem units = UnitSystem.metric,
  ]) => '${formatElevation(meters, units)} ${'units.elevationLoss'.tr()}';

  /// Altitude d'un point du profil : même rendu qu'un dénivelé.
  static String formatAltitude(
    num meters, [
    UnitSystem units = UnitSystem.metric,
  ]) => formatElevation(meters, units);

  /// Vitesse moyenne d'un groupe : « 25 km/h », « 16 mph ».
  ///
  /// L'entrée est en **km/h**, comme `RideGroupDto.averageSpeed`.
  static String formatSpeed(
    num kilometersPerHour, [
    UnitSystem units = UnitSystem.metric,
  ]) =>
      withUnit(formatNumber(units.speed(kilometersPerHour)), units.speedSymbol);

  /// Pente, au dixième de point près : « 6,5 % ». Sans conversion : un
  /// pourcentage est un rapport, il ne dépend pas du système d'unités.
  static String formatGrade(num percent) =>
      withUnit(formatNumber(percent, fractionDigits: 1), UnitSymbols.percent);

  // ─────────────────────────────────────────────────────────────────────────
  // Prix
  // ─────────────────────────────────────────────────────────────────────────

  static final NumberFormat _currency = NumberFormat.currency(
    locale: 'fr_FR',
    symbol: '€',
    decimalDigits: 2,
  );

  /// Montant seul : « 1 200,00 € ».
  static String formatAmount(num price) => _currency.format(price);

  /// Le prix d'une annonce, décomposé pour l'affichage.
  ///
  /// La période n'est pas concaténée au montant : elle se rend dans un
  /// `TextSpan` distinct, en graisse 400 et en couleur atténuée, tandis que le
  /// montant reste en 600. [FormattedPrice] porte donc les deux morceaux
  /// séparément — ce qui permet à `core/utils` de ne rien savoir du thème ni
  /// de `core/pdl`.
  ///
  /// `price == null` n'est **jamais** un tiret : c'est « Prix à négocier »,
  /// une information, pas une absence.
  static FormattedPrice formatPrice(num? price, {String? rentalPeriod}) {
    if (price == null) {
      return FormattedPrice(
        amount: 'ads.detail.priceNegotiable'.tr(),
        isNegotiable: true,
      );
    }
    return FormattedPrice(
      amount: formatAmount(price),
      period: rentalPeriod == null
          ? null
          : '/ ${'ads.rentalPeriod.$rentalPeriod'.tr().toLowerCase()}',
    );
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Jours et mois
  // ─────────────────────────────────────────────────────────────────────────

  /// Nom court du jour de la semaine (1 = lundi, 7 = dimanche).
  static String dayAbbrev(int weekday, {BuildContext? context}) {
    if (weekday < 1 || weekday > 7) return '?';
    // 2026-01-05 est un lundi.
    final DateTime date = DateTime(2026, 1, 4 + weekday);
    return DateFormat.E().format(date);
  }

  /// Nom complet du jour de la semaine (1 = lundi, 7 = dimanche).
  static String dayFull(int weekday, {BuildContext? context}) {
    if (weekday < 1 || weekday > 7) return '?';
    final DateTime date = DateTime(2026, 1, 4 + weekday);
    return DateFormat.EEEE().format(date);
  }

  /// Nom du mois, initiale capitale (1 = janvier, 12 = décembre).
  static String monthCapitalized(int month, {BuildContext? context}) {
    if (month < 1 || month > 12) return '?';
    final String name = DateFormat.MMMM().format(DateTime(2026, month));
    return name[0].toUpperCase() + name.substring(1);
  }

  /// Nom du mois en minuscules (1 = janvier, 12 = décembre).
  static String monthLower(int month, {BuildContext? context}) {
    if (month < 1 || month > 12) return '?';
    return DateFormat.MMMM().format(DateTime(2026, month)).toLowerCase();
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Rôles et revêtements
  // ─────────────────────────────────────────────────────────────────────────

  static String roleName(String role) => tr('roles.${role.toLowerCase()}');

  static String surfaceName(String surface) =>
      tr('surfaces.${surface.toLowerCase()}');

  static IconData surfaceIcon(String surface) {
    return switch (surface.toUpperCase()) {
      'ROAD' => Icons.add_road,
      'GRAVEL' => Icons.terrain,
      'MTB' => Icons.landscape,
      _ => Icons.help_outline,
    };
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Dates et heures
  //
  // Fuseau de **l'appareil**, toujours : un instant venu du serveur en UTC est
  // ramené à l'heure locale avant d'être rendu. Le « fuseau d'équipe » a été
  // abandonné (§1.0.3-11), et `package:timezone` avec lui.
  // ─────────────────────────────────────────────────────────────────────────

  /// Ramène un instant au fuseau de l'appareil. Sans effet sur une date déjà
  /// locale.
  static DateTime toDeviceTime(DateTime date) => date.toLocal();

  /// Heure au format HH:mm de la locale courante.
  static String formatTime(DateTime date) =>
      DateFormat.Hm().format(toDeviceTime(date));

  /// Formate une `LocalTime` (« 08:30:00 ») en HH:mm, secondes ôtées.
  ///
  /// Volontairement sans fuseau : une `LocalTime` est une heure de cadran, pas
  /// un instant. « Départ 8 h 30 » reste 8 h 30 où que soit le téléphone.
  static String formatLocalTime(String localTime) {
    final List<String> parts = localTime.split(':');
    if (parts.length >= 2) return '${parts[0]}:${parts[1]}';
    return localTime;
  }

  /// « 15 janvier ».
  static String formatDayMonth(DateTime date) {
    final DateTime local = toDeviceTime(date);
    return '${local.day} ${monthLower(local.month)}';
  }

  /// « lundi 15 janvier ».
  static String formatFullDate(DateTime date) {
    final DateTime local = toDeviceTime(date);
    return '${dayFull(local.weekday)} ${local.day} ${monthLower(local.month)}';
  }

  /// « Janvier 2026 ».
  static String formatMonthYear(DateTime date) {
    final DateTime local = toDeviceTime(date);
    return '${monthCapitalized(local.month)} ${local.year}';
  }

  /// « lundi 15 janvier 2026 ».
  static String formatLongDate(DateTime date) =>
      '${formatFullDate(date)} ${toDeviceTime(date).year}';

  /// « lundi 15 janvier 2026 à 08:30 ».
  static String formatLongDateTime(DateTime date) => 'dates.at'.tr(
    namedArgs: <String, String>{
      'date': formatLongDate(date),
      'time': formatTime(date),
    },
  );

  static String get today => tr('dates.today');

  static String get tomorrow => tr('dates.tomorrow');

  /// Date d'une sortie, relative au jour courant, avec l'heure.
  static String formatRideDate(DateTime date, {DateTime? now}) {
    final DateTime local = toDeviceTime(date);
    final DateTime reference = toDeviceTime(now ?? DateTime.now());
    final int dayDiff = DateTime(local.year, local.month, local.day)
        .difference(DateTime(reference.year, reference.month, reference.day))
        .inDays;
    final String time = formatTime(date);
    if (dayDiff == 0) return '$today $time';
    if (dayDiff == 1) return '$tomorrow $time';
    return '${formatFullDate(date)} $time';
  }

  /// « il y a 3 jours », « dans 2 h », « à l'instant ».
  ///
  /// Au-delà de [RelativeTime.absoluteThresholdDays] jours, le relatif n'aide
  /// plus personne : on rend la date longue.
  static String formatRelative(DateTime date, {DateTime? now}) {
    final RelativeTime relative = RelativeTime.between(
      toDeviceTime(date),
      toDeviceTime(now ?? DateTime.now()),
    );
    return switch (relative.unit) {
      RelativeUnit.now => 'dates.relative.now'.tr(),
      RelativeUnit.absolute => formatLongDate(date),
      RelativeUnit.minutes ||
      RelativeUnit.hours ||
      RelativeUnit.days => 'dates.relative.${relative.translationKey}'.plural(
        relative.count,
        namedArgs: <String, String>{'count': '${relative.count}'},
      ),
    };
  }
}

/// Un prix d'annonce prêt à composer, en deux morceaux.
///
/// Le montant se rend en graisse 600, la période — quand il y en a une — en
/// 400 atténué, dans un `TextSpan` séparé. Cette classe ne connaît ni le thème
/// ni `core/pdl` : elle porte du texte, la mise en forme reste à l'appelant.
@immutable
class FormattedPrice {
  const FormattedPrice({
    required this.amount,
    this.period,
    this.isNegotiable = false,
  });

  /// « 1 200,00 € », ou « Prix à négocier ».
  final String amount;

  /// « / semaine ». `null` pour une vente, et pour une location dont la
  /// période n'a pas été renseignée.
  final String? period;

  /// Vrai quand [amount] porte « Prix à négocier » plutôt qu'un montant.
  final bool isNegotiable;

  /// Rendu d'un seul tenant, pour les contextes sans `TextSpan` sous la main
  /// (accessibilité, partage, tests).
  String get flat => period == null ? amount : '$amount $period';

  @override
  bool operator ==(Object other) =>
      other is FormattedPrice &&
      other.amount == amount &&
      other.period == period &&
      other.isNegotiable == isNegotiable;

  @override
  int get hashCode => Object.hash(amount, period, isNegotiable);

  @override
  String toString() => flat;
}

/// L'unité dans laquelle un écart de temps se raconte.
enum RelativeUnit { now, minutes, hours, days, absolute }

/// L'écart entre deux instants, réduit à ce qu'il faut pour le dire.
///
/// Pur : pas de traduction, pas de locale. C'est [AppFormatters.formatRelative]
/// qui l'habille, ce qui rend le découpage testable sans binding.
@immutable
class RelativeTime {
  const RelativeTime({
    required this.unit,
    required this.count,
    required this.isPast,
  });

  final RelativeUnit unit;

  /// Toujours positif : le sens est porté par [isPast].
  final int count;

  final bool isPast;

  /// Au-delà, un relatif désoriente plus qu'il n'informe.
  static const int absoluteThresholdDays = 7;

  /// En deçà, « à l'instant » vaut mieux que « il y a 0 minute ».
  static const int nowThresholdSeconds = 60;

  static RelativeTime between(DateTime date, DateTime now) {
    final Duration delta = now.difference(date);
    final bool isPast = !delta.isNegative;
    final Duration abs = delta.abs();
    if (abs.inSeconds < nowThresholdSeconds) {
      return RelativeTime(unit: RelativeUnit.now, count: 0, isPast: isPast);
    }
    if (abs.inMinutes < 60) {
      return RelativeTime(
        unit: RelativeUnit.minutes,
        count: abs.inMinutes,
        isPast: isPast,
      );
    }
    if (abs.inHours < 24) {
      return RelativeTime(
        unit: RelativeUnit.hours,
        count: abs.inHours,
        isPast: isPast,
      );
    }
    if (abs.inDays <= absoluteThresholdDays) {
      return RelativeTime(
        unit: RelativeUnit.days,
        count: abs.inDays,
        isPast: isPast,
      );
    }
    return RelativeTime(
      unit: RelativeUnit.absolute,
      count: abs.inDays,
      isPast: isPast,
    );
  }

  /// Segment de clé de traduction : « minutesAgo », « inDays »…
  String get translationKey => switch (unit) {
    RelativeUnit.now => 'now',
    RelativeUnit.absolute => 'absolute',
    RelativeUnit.minutes => isPast ? 'minutesAgo' : 'inMinutes',
    RelativeUnit.hours => isPast ? 'hoursAgo' : 'inHours',
    RelativeUnit.days => isPast ? 'daysAgo' : 'inDays',
  };

  @override
  bool operator ==(Object other) =>
      other is RelativeTime &&
      other.unit == unit &&
      other.count == count &&
      other.isPast == isPast;

  @override
  int get hashCode => Object.hash(unit, count, isPast);

  @override
  String toString() => 'RelativeTime($unit, $count, isPast: $isPast)';
}
