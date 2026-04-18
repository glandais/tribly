// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'dart:convert';
import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/asset_dto.dart';
import '../models/asset_type_request.dart';

part 'assets_client.g.dart';

@RestApi()
abstract class AssetsClient {
  factory AssetsClient(Dio dio, {String? baseUrl}) = _AssetsClient;

  /// Create asset.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [assetType] - Asset type.
  ///
  /// [file] - Name not received - field will be skipped.
  @MultiPart()
  @POST('/api/teams/{teamSlug}/assets')
  Future<AssetDto> uploadAsset({
    @Path('teamSlug') required String teamSlug,
    @Query('assetType') required AssetTypeRequest assetType,
    @Part(name: 'file') MultipartFile? file,
  });
}
