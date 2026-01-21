// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'calendar_token_dto.freezed.dart';
part 'calendar_token_dto.g.dart';

/// Calendar token information
@Freezed()
abstract class CalendarTokenDto with _$CalendarTokenDto {
  const factory CalendarTokenDto({
    /// ICS feed token (include in URL)
    required String token,

    /// Global ICS feed URL
    required String globalFeedUrl,

    /// Team-specific feed URL template
    required String teamFeedUrlTemplate,
  }) = _CalendarTokenDto;

  factory CalendarTokenDto.fromJson(Map<String, Object?> json) =>
      _$CalendarTokenDtoFromJson(json);
}
