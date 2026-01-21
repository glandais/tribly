// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'garmin_callback_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GarminCallbackRequest _$GarminCallbackRequestFromJson(
  Map<String, dynamic> json,
) => _GarminCallbackRequest(
  accessToken: json['accessToken'] as String,
  redirectUri: json['redirectUri'] as String,
  state: json['state'] as String?,
);

Map<String, dynamic> _$GarminCallbackRequestToJson(
  _GarminCallbackRequest instance,
) => <String, dynamic>{
  'accessToken': instance.accessToken,
  'redirectUri': instance.redirectUri,
  'state': instance.state,
};
