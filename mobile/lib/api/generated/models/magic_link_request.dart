// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'magic_link_request.freezed.dart';
part 'magic_link_request.g.dart';

/// Magic link request
@Freezed()
abstract class MagicLinkRequest with _$MagicLinkRequest {
  const factory MagicLinkRequest({
    /// Email address
    required String email,
  }) = _MagicLinkRequest;

  factory MagicLinkRequest.fromJson(Map<String, Object?> json) =>
      _$MagicLinkRequestFromJson(json);
}
