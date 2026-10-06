// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'team_timezone_dto.freezed.dart';
part 'team_timezone_dto.g.dart';

/// The zone of a point, else the team's
@Freezed()
abstract class TeamTimezoneDto with _$TeamTimezoneDto {
  const factory TeamTimezoneDto({
    /// IANA zone of the point; the team's own when no point is given or the point lies outside every zone
    required String timezone,
  }) = _TeamTimezoneDto;

  factory TeamTimezoneDto.fromJson(Map<String, Object?> json) =>
      _$TeamTimezoneDtoFromJson(json);
}
