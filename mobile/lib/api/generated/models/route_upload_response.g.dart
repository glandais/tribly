// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'route_upload_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RouteUploadResponse _$RouteUploadResponseFromJson(Map<String, dynamic> json) =>
    _RouteUploadResponse(
      success: json['success'] as bool,
      message: json['message'] as String?,
      externalRouteId: json['externalRouteId'] as String?,
    );

Map<String, dynamic> _$RouteUploadResponseToJson(
  _RouteUploadResponse instance,
) => <String, dynamic>{
  'success': instance.success,
  'message': instance.message,
  'externalRouteId': instance.externalRouteId,
};
