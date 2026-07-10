// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'config_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ConfigDto _$ConfigDtoFromJson(Map<String, dynamic> json) => _ConfigDto(
  webAuthnRpId: json['webAuthnRpId'] as String,
  appName: json['appName'] as String,
  singleTeam: json['singleTeam'] as bool,
  pinnedTeamSlug: json['pinnedTeamSlug'] as String?,
);

Map<String, dynamic> _$ConfigDtoToJson(_ConfigDto instance) =>
    <String, dynamic>{
      'webAuthnRpId': instance.webAuthnRpId,
      'appName': instance.appName,
      'singleTeam': instance.singleTeam,
      'pinnedTeamSlug': instance.pinnedTeamSlug,
    };
