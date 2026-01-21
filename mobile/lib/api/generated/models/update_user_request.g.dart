// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_user_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UpdateUserRequest _$UpdateUserRequestFromJson(Map<String, dynamic> json) =>
    _UpdateUserRequest(
      displayName: json['displayName'] as String?,
      unitSystem: json['unitSystem'] as String?,
    );

Map<String, dynamic> _$UpdateUserRequestToJson(_UpdateUserRequest instance) =>
    <String, dynamic>{
      'displayName': instance.displayName,
      'unitSystem': instance.unitSystem,
    };
