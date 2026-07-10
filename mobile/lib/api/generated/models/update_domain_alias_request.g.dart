// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_domain_alias_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UpdateDomainAliasRequest _$UpdateDomainAliasRequestFromJson(
  Map<String, dynamic> json,
) => _UpdateDomainAliasRequest(
  teamSlug: json['teamSlug'] as String,
  name: json['name'] as String,
  baseUrl: json['baseUrl'] as String,
  androidFingerprints: json['androidFingerprints'] as String?,
);

Map<String, dynamic> _$UpdateDomainAliasRequestToJson(
  _UpdateDomainAliasRequest instance,
) => <String, dynamic>{
  'teamSlug': instance.teamSlug,
  'name': instance.name,
  'baseUrl': instance.baseUrl,
  'androidFingerprints': instance.androidFingerprints,
};
