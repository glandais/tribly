// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_domain_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminDomainDto _$AdminDomainDtoFromJson(Map<String, dynamic> json) =>
    _AdminDomainDto(
      id: json['id'] as String,
      domain: json['domain'] as String,
      name: json['name'] as String,
      baseUrl: json['baseUrl'] as String,
      singleTeam: json['singleTeam'] as bool,
      enableGpxPlanner: json['enableGpxPlanner'] as bool,
      active: json['active'] as bool,
      teamCount: (json['teamCount'] as num).toInt(),
      userCount: (json['userCount'] as num).toInt(),
      createdAt: json['createdAt'] as String,
      androidFingerprints: json['androidFingerprints'] as String?,
    );

Map<String, dynamic> _$AdminDomainDtoToJson(_AdminDomainDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'domain': instance.domain,
      'name': instance.name,
      'baseUrl': instance.baseUrl,
      'singleTeam': instance.singleTeam,
      'enableGpxPlanner': instance.enableGpxPlanner,
      'active': instance.active,
      'teamCount': instance.teamCount,
      'userCount': instance.userCount,
      'createdAt': instance.createdAt,
      'androidFingerprints': instance.androidFingerprints,
    };
