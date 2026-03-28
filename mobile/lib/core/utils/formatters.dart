import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

/// Centralized formatting utilities for the Pédalons app.
///
/// Use these instead of inline formatting logic to ensure consistency
/// and reduce code duplication.
class AppFormatters {
  AppFormatters._();

  // ─────────────────────────────────────────────────────────────────────────
  // Day names - using translations
  // ─────────────────────────────────────────────────────────────────────────

  /// Returns the short day name for a weekday (1=Monday, 7=Sunday).
  static String dayAbbrev(int weekday, {BuildContext? context}) {
    if (weekday < 1 || weekday > 7) return '?';
    // Create a date with the target weekday (2026-01-05 is a Monday)
    final date = DateTime(2026, 1, 4 + weekday);
    return DateFormat.E().format(date);
  }

  /// Returns the full day name for a weekday (1=Monday, 7=Sunday).
  static String dayFull(int weekday, {BuildContext? context}) {
    if (weekday < 1 || weekday > 7) return '?';
    // Create a date with the target weekday (2026-01-05 is a Monday)
    final date = DateTime(2026, 1, 4 + weekday);
    return DateFormat.EEEE().format(date);
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Month names - using translations
  // ─────────────────────────────────────────────────────────────────────────

  /// Returns the capitalized month name (1=January, 12=December).
  static String monthCapitalized(int month, {BuildContext? context}) {
    if (month < 1 || month > 12) return '?';
    final date = DateTime(2026, month);
    final name = DateFormat.MMMM().format(date);
    return name[0].toUpperCase() + name.substring(1);
  }

  /// Returns the lowercase month name (1=January, 12=December).
  static String monthLower(int month, {BuildContext? context}) {
    if (month < 1 || month > 12) return '?';
    final date = DateTime(2026, month);
    return DateFormat.MMMM().format(date).toLowerCase();
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Role names - using translations
  // ─────────────────────────────────────────────────────────────────────────

  /// Returns the localized display name for a team role.
  static String roleName(String role) {
    return tr('roles.${role.toLowerCase()}');
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Surface types - using translations
  // ─────────────────────────────────────────────────────────────────────────

  /// Returns the localized display name for a surface type.
  static String surfaceName(String surface) {
    return tr('surfaces.${surface.toLowerCase()}');
  }

  /// Returns the icon for a surface type.
  static IconData surfaceIcon(String surface) {
    return switch (surface.toUpperCase()) {
      'ROAD' => Icons.add_road,
      'GRAVEL' => Icons.terrain,
      'MTB' => Icons.landscape,
      _ => Icons.help_outline,
    };
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Date/time formatting - using intl for locale-aware formatting
  // ─────────────────────────────────────────────────────────────────────────

  /// Formats time as HH:mm using current locale.
  static String formatTime(DateTime date) {
    return DateFormat.Hm().format(date);
  }

  /// Formats a LocalTime string (e.g. "08:30:00") as HH:mm, stripping seconds.
  static String formatLocalTime(String localTime) {
    final parts = localTime.split(':');
    if (parts.length >= 2) {
      return '${parts[0]}:${parts[1]}';
    }
    return localTime;
  }

  /// Formats a date as "day month" (e.g., "15 janvier" in French, "January 15" in English).
  static String formatDayMonth(DateTime date) {
    return '${date.day} ${monthLower(date.month)}';
  }

  /// Formats a date as "DayName day month" (e.g., "Lundi 15 janvier").
  static String formatFullDate(DateTime date) {
    return '${dayFull(date.weekday)} ${date.day} ${monthLower(date.month)}';
  }

  /// Formats a date as "Month Year" (e.g., "Janvier 2026").
  static String formatMonthYear(DateTime date) {
    return '${monthCapitalized(date.month)} ${date.year}';
  }

  /// Returns "Today" translation
  static String get today => tr('dates.today');

  /// Returns "Tomorrow" translation
  static String get tomorrow => tr('dates.tomorrow');
}
