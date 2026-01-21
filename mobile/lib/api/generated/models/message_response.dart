// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'message_response.freezed.dart';
part 'message_response.g.dart';

/// Simple message response
@Freezed()
abstract class MessageResponse with _$MessageResponse {
  const factory MessageResponse({
    /// Response message
    String? message,
  }) = _MessageResponse;

  factory MessageResponse.fromJson(Map<String, Object?> json) =>
      _$MessageResponseFromJson(json);
}
