// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/version_dto.dart';

part 'server_version_client.g.dart';

@RestApi()
abstract class ServerVersionClient {
  factory ServerVersionClient(Dio dio, {String? baseUrl}) =
      _ServerVersionClient;

  /// Get server version.
  ///
  /// Get the API contract version the server implements, plus the git commit and build time it was built from.
  @GET('/api/version')
  Future<VersionDto> getVersion();
}
