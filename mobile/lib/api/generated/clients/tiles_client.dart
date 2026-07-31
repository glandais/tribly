// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/tile_token_dto.dart';

part 'tiles_client.g.dart';

@RestApi()
abstract class TilesClient {
  factory TilesClient(Dio dio, {String? baseUrl}) = _TilesClient;

  /// Mint a tile token.
  ///
  /// Short-lived token authorizing the caller's own vector tile requests, to be passed as the 't' query parameter of the .mvt endpoints. Meant for clients whose map renderer has its own HTTP stack and can carry neither an Authorization header nor a session cookie. It grants nothing but tiles: no other endpoint accepts it.
  @POST('/api/tiles/token')
  Future<TileTokenDto> mint();
}
