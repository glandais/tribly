// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'admin_domain_dto.dart';

part 'admin_domain_list_response.freezed.dart';
part 'admin_domain_list_response.g.dart';

/// Paginated admin domain list response
@Freezed()
abstract class AdminDomainListResponse with _$AdminDomainListResponse {
  const factory AdminDomainListResponse({
    /// List of domains
    required List<AdminDomainDto> domains,

    /// Total number of domains
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _AdminDomainListResponse;

  factory AdminDomainListResponse.fromJson(Map<String, Object?> json) =>
      _$AdminDomainListResponseFromJson(json);
}
