import 'package:flutter/material.dart';

/// Centralized formatting utilities for the Tribly app.
///
/// Use these instead of inline formatting logic to ensure consistency
/// and reduce code duplication.
class AppFormatters {
  AppFormatters._();

  // ─────────────────────────────────────────────────────────────────────────
  // Day names
  // ─────────────────────────────────────────────────────────────────────────

  /// Short French day names (Mon-Sun), 1-indexed by weekday.
  static const dayNamesShort = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];

  /// Full French day names, 1-indexed by weekday.
  static const dayNamesFull = [
    'Lundi',
    'Mardi',
    'Mercredi',
    'Jeudi',
    'Vendredi',
    'Samedi',
    'Dimanche',
  ];

  /// Returns the short day name for a weekday (1=Monday, 7=Sunday).
  static String dayAbbrev(int weekday) {
    if (weekday < 1 || weekday > 7) return '?';
    return dayNamesShort[weekday - 1];
  }

  /// Returns the full day name for a weekday (1=Monday, 7=Sunday).
  static String dayFull(int weekday) {
    if (weekday < 1 || weekday > 7) return '?';
    return dayNamesFull[weekday - 1];
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Month names
  // ─────────────────────────────────────────────────────────────────────────

  /// French month names (capitalized), 1-indexed.
  static const monthNamesCapitalized = [
    'Janvier',
    'Février',
    'Mars',
    'Avril',
    'Mai',
    'Juin',
    'Juillet',
    'Août',
    'Septembre',
    'Octobre',
    'Novembre',
    'Décembre',
  ];

  /// French month names (lowercase), 1-indexed.
  static const monthNamesLower = [
    'janvier',
    'février',
    'mars',
    'avril',
    'mai',
    'juin',
    'juillet',
    'août',
    'septembre',
    'octobre',
    'novembre',
    'décembre',
  ];

  /// Returns the capitalized month name (1=January, 12=December).
  static String monthCapitalized(int month) {
    if (month < 1 || month > 12) return '?';
    return monthNamesCapitalized[month - 1];
  }

  /// Returns the lowercase month name (1=January, 12=December).
  static String monthLower(int month) {
    if (month < 1 || month > 12) return '?';
    return monthNamesLower[month - 1];
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Role names
  // ─────────────────────────────────────────────────────────────────────────

  /// Returns the localized display name for a team role.
  static String roleName(String role) {
    return switch (role.toUpperCase()) {
      'OWNER' => 'Propriétaire',
      'ADMIN' => 'Admin',
      'MEMBER' => 'Membre',
      _ => role,
    };
  }

  // ─────────────────────────────────────────────────────────────────────────
  // Surface types
  // ─────────────────────────────────────────────────────────────────────────

  /// Returns the localized display name for a surface type.
  static String surfaceName(String surface) {
    return switch (surface.toUpperCase()) {
      'ROAD' => 'Route',
      'GRAVEL' => 'Gravel',
      'MTB' => 'VTT',
      _ => surface,
    };
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
  // Date/time formatting
  // ─────────────────────────────────────────────────────────────────────────

  /// Formats time as HH:mm.
  static String formatTime(DateTime date) {
    return '${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }

  /// Formats a date as "day month" (e.g., "15 janvier").
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
}
