// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'theme_preference.dart';
import 'unit_system.dart';

part 'user_preferences_request.freezed.dart';
part 'user_preferences_request.g.dart';

/// Partial update of the current user's display preferences
@Freezed()
abstract class UserPreferencesRequest with _$UserPreferencesRequest {
  const factory UserPreferencesRequest({
    /// Preferred unit system. Omit or send null to leave it unchanged.
    String? unitSystem,

    /// Preferred colour scheme. Omit or send null to leave it unchanged.
    String? theme,

    /// Preferred language as a BCP-47 tag ('fr', 'en', 'fr-CA'). Omit or send null to leave it unchanged. Not validated against the set of translations the app ships: a client asking for a language nobody has translated yet falls back on its own, which is better than a 400 the day a translation lands.
    String? language,
  }) = _UserPreferencesRequest;

  factory UserPreferencesRequest.fromJson(Map<String, Object?> json) =>
      _$UserPreferencesRequestFromJson(json);
}
