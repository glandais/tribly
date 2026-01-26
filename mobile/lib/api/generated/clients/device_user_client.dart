// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/device_user_status_response.dart';

part 'device_user_client.g.dart';

@RestApi()
abstract class DeviceUserClient {
  factory DeviceUserClient(Dio dio, {String? baseUrl}) = _DeviceUserClient;

  /// Get current user status.
  ///
  /// Get authenticated user's status including connected GPS services.
  @GET('/api/device/me')
  Future<DeviceUserStatusResponse> deviceGetMe();
}
