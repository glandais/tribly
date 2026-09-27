// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'client_error_dto.freezed.dart';
part 'client_error_dto.g.dart';

/// An unhandled error caught by a client
@Freezed()
abstract class ClientErrorDto with _$ClientErrorDto {
  const factory ClientErrorDto({
    /// Error class, e.g. TypeError or _TypeError
    required String type,

    /// Error message
    required String message,

    /// Stack trace, as the client printed it
    String? stack,
  }) = _ClientErrorDto;

  factory ClientErrorDto.fromJson(Map<String, Object?> json) =>
      _$ClientErrorDtoFromJson(json);
}
