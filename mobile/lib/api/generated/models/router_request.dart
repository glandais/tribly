// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_point.dart';
import 'router_profile.dart';

part 'router_request.freezed.dart';
part 'router_request.g.dart';

@Freezed()
abstract class RouterRequest with _$RouterRequest {
  const factory RouterRequest({
    required GeoPoint from,
    required GeoPoint to,
    required RouterProfile profile,
  }) = _RouterRequest;

  factory RouterRequest.fromJson(Map<String, Object?> json) =>
      _$RouterRequestFromJson(json);
}
