// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/admin_domain_dto.dart';
import '../models/admin_domain_list_response.dart';
import '../models/admin_stats_dto.dart';
import '../models/create_domain_request.dart';
import '../models/update_domain_request.dart';

part 'admin_domains_client.g.dart';

@RestApi()
abstract class AdminDomainsClient {
  factory AdminDomainsClient(Dio dio, {String? baseUrl}) = _AdminDomainsClient;

  /// List all domains.
  ///
  /// Get a paginated list of all domains.
  ///
  /// [page] - Page number (0-indexed).
  ///
  /// [size] - Page size.
  @GET('/api/admin/domains')
  Future<AdminDomainListResponse> listDomains({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
  });

  /// Create domain.
  ///
  /// Create a new domain.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/admin/domains')
  Future<AdminDomainDto> createDomain({
    @Body() required CreateDomainRequest body,
  });

  /// Get platform statistics.
  ///
  /// Get overall platform statistics.
  @GET('/api/admin/domains/stats')
  Future<AdminStatsDto> getStats();

  /// Update domain.
  ///
  /// Update domain information.
  ///
  /// [domainId] - Domain ID.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/admin/domains/{domainId}')
  Future<AdminDomainDto> updateDomain({
    @Path('domainId') required String domainId,
    @Body() required UpdateDomainRequest body,
  });

  /// Get domain details.
  ///
  /// Get detailed domain information.
  ///
  /// [domainId] - Domain ID.
  @GET('/api/admin/domains/{domainId}')
  Future<AdminDomainDto> getDomain({
    @Path('domainId') required String domainId,
  });

  /// Toggle domain active status.
  ///
  /// Enable or disable a domain.
  ///
  /// [domainId] - Domain ID.
  @POST('/api/admin/domains/{domainId}/toggle-active')
  Future<AdminDomainDto> toggleDomainActive({
    @Path('domainId') required String domainId,
  });
}
