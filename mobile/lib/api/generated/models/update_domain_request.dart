// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'update_domain_request.freezed.dart';
part 'update_domain_request.g.dart';

/// Request to update a domain
@Freezed()
abstract class UpdateDomainRequest with _$UpdateDomainRequest {
  const factory UpdateDomainRequest({
    /// Domain display name
    required String name,

    /// Base URL for the domain
    required String baseUrl,

    /// Whether domain is single-team mode
    bool? singleTeam,
  }) = _UpdateDomainRequest;

  factory UpdateDomainRequest.fromJson(Map<String, Object?> json) =>
      _$UpdateDomainRequestFromJson(json);
}
