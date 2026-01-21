// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'callback_response.freezed.dart';
part 'callback_response.g.dart';

/// OAuth callback response with redirect URL
@Freezed()
abstract class CallbackResponse with _$CallbackResponse {
  const factory CallbackResponse({
    /// Redirect URL with auth code
    required String redirectUrl,
  }) = _CallbackResponse;

  factory CallbackResponse.fromJson(Map<String, Object?> json) =>
      _$CallbackResponseFromJson(json);
}
