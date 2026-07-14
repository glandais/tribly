// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'social_backfill_response.freezed.dart';
part 'social_backfill_response.g.dart';

/// Result of a social-identity backfill
@Freezed()
abstract class SocialBackfillResponse with _$SocialBackfillResponse {
  const factory SocialBackfillResponse({
    /// Number of identity rows created
    required int identitiesCreated,
  }) = _SocialBackfillResponse;

  factory SocialBackfillResponse.fromJson(Map<String, Object?> json) =>
      _$SocialBackfillResponseFromJson(json);
}
