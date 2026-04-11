// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_domain_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CreateDomainRequest _$CreateDomainRequestFromJson(Map<String, dynamic> json) =>
    _CreateDomainRequest(
      domain: json['domain'] as String,
      name: json['name'] as String,
      baseUrl: json['baseUrl'] as String,
      singleTeam: json['singleTeam'] as bool?,
      androidFingerprints: json['androidFingerprints'] as String?,
    );

Map<String, dynamic> _$CreateDomainRequestToJson(
  _CreateDomainRequest instance,
) => <String, dynamic>{
  'domain': instance.domain,
  'name': instance.name,
  'baseUrl': instance.baseUrl,
  'singleTeam': instance.singleTeam,
  'androidFingerprints': instance.androidFingerprints,
};
