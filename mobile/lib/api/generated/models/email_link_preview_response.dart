// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'email_link_kind.dart';

part 'email_link_preview_response.freezed.dart';
part 'email_link_preview_response.g.dart';

/// What a verification link is about, read without spending it: the page shows the address before anything happens, so that nobody activates someone else's account unaware.
@Freezed()
abstract class EmailLinkPreviewResponse with _$EmailLinkPreviewResponse {
  const factory EmailLinkPreviewResponse({
    /// The address the link verifies
    required String email,

    /// What following the link does
    required String kind,
  }) = _EmailLinkPreviewResponse;

  factory EmailLinkPreviewResponse.fromJson(Map<String, Object?> json) =>
      _$EmailLinkPreviewResponseFromJson(json);
}
