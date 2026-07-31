// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_domain_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UpdateDomainRequest _$UpdateDomainRequestFromJson(Map<String, dynamic> json) =>
    _UpdateDomainRequest(
      name: json['name'] as String,
      baseUrl: json['baseUrl'] as String,
      singleTeam: json['singleTeam'] as bool?,
      enableGpxPlanner: json['enableGpxPlanner'] as bool?,
      androidFingerprints: json['androidFingerprints'] as String?,
    );

Map<String, dynamic> _$UpdateDomainRequestToJson(
  _UpdateDomainRequest instance,
) => <String, dynamic>{
  'name': instance.name,
  'baseUrl': instance.baseUrl,
  'singleTeam': instance.singleTeam,
  'enableGpxPlanner': instance.enableGpxPlanner,
  'androidFingerprints': instance.androidFingerprints,
};
