// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'config_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ConfigDto _$ConfigDtoFromJson(Map<String, dynamic> json) => _ConfigDto(
  webAuthnRpId: json['webAuthnRpId'] as String,
  appName: json['appName'] as String,
  singleTeam: json['singleTeam'] as bool,
  mapStyles: (json['mapStyles'] as List<dynamic>)
      .map((e) => MapStyleDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  tileServerBaseUrl: json['tileServerBaseUrl'] as String,
  defaultCenter: MapCenterDto.fromJson(
    json['defaultCenter'] as Map<String, dynamic>,
  ),
  pinnedTeamSlug: json['pinnedTeamSlug'] as String?,
  minSupportedAppVersion: json['minSupportedAppVersion'] as String?,
);

Map<String, dynamic> _$ConfigDtoToJson(_ConfigDto instance) =>
    <String, dynamic>{
      'webAuthnRpId': instance.webAuthnRpId,
      'appName': instance.appName,
      'singleTeam': instance.singleTeam,
      'mapStyles': instance.mapStyles.map((e) => e.toJson()).toList(),
      'tileServerBaseUrl': instance.tileServerBaseUrl,
      'defaultCenter': instance.defaultCenter.toJson(),
      'pinnedTeamSlug': instance.pinnedTeamSlug,
      'minSupportedAppVersion': instance.minSupportedAppVersion,
    };
