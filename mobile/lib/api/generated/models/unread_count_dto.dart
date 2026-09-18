// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'unread_count_dto.freezed.dart';
part 'unread_count_dto.g.dart';

/// Number of unread notifications — the badge on the bell
@Freezed()
abstract class UnreadCountDto with _$UnreadCountDto {
  const factory UnreadCountDto({
    /// Unread notifications
    required int count,
  }) = _UnreadCountDto;

  factory UnreadCountDto.fromJson(Map<String, Object?> json) =>
      _$UnreadCountDtoFromJson(json);
}
