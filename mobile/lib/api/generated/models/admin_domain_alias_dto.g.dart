// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_domain_alias_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminDomainAliasDto _$AdminDomainAliasDtoFromJson(Map<String, dynamic> json) =>
    _AdminDomainAliasDto(
      id: json['id'] as String,
      hostname: json['hostname'] as String,
      domainId: json['domainId'] as String,
      pinnedTeamId: json['pinnedTeamId'] as String,
      pinnedTeamSlug: json['pinnedTeamSlug'] as String,
      name: json['name'] as String,
      baseUrl: json['baseUrl'] as String,
      active: json['active'] as bool,
      createdAt: json['createdAt'] as String,
      androidFingerprints: json['androidFingerprints'] as String?,
    );

Map<String, dynamic> _$AdminDomainAliasDtoToJson(
  _AdminDomainAliasDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'hostname': instance.hostname,
  'domainId': instance.domainId,
  'pinnedTeamId': instance.pinnedTeamId,
  'pinnedTeamSlug': instance.pinnedTeamSlug,
  'name': instance.name,
  'baseUrl': instance.baseUrl,
  'active': instance.active,
  'createdAt': instance.createdAt,
  'androidFingerprints': instance.androidFingerprints,
};
