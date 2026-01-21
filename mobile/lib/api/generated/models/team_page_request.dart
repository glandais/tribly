// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'media_dto.dart';
import 'visibility.dart';

part 'team_page_request.freezed.dart';
part 'team_page_request.g.dart';

/// Team page request
@Freezed()
abstract class TeamPageRequest with _$TeamPageRequest {
  const factory TeamPageRequest({
    /// Page title
    required String title,

    /// Page content
    required MediaDto media,

    /// Visibility level
    required String visibility,
  }) = _TeamPageRequest;

  factory TeamPageRequest.fromJson(Map<String, Object?> json) =>
      _$TeamPageRequestFromJson(json);
}
