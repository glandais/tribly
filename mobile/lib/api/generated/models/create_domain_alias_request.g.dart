// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_domain_alias_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CreateDomainAliasRequest _$CreateDomainAliasRequestFromJson(
  Map<String, dynamic> json,
) => _CreateDomainAliasRequest(
  hostname: json['hostname'] as String,
  teamSlug: json['teamSlug'] as String,
  name: json['name'] as String,
  baseUrl: json['baseUrl'] as String,
  androidFingerprints: json['androidFingerprints'] as String?,
);

Map<String, dynamic> _$CreateDomainAliasRequestToJson(
  _CreateDomainAliasRequest instance,
) => <String, dynamic>{
  'hostname': instance.hostname,
  'teamSlug': instance.teamSlug,
  'name': instance.name,
  'baseUrl': instance.baseUrl,
  'androidFingerprints': instance.androidFingerprints,
};
