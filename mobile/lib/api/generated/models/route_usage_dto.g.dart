// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'route_usage_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RouteUsageDto _$RouteUsageDtoFromJson(Map<String, dynamic> json) =>
    _RouteUsageDto(
      type: json['type'] as String,
      slug: json['slug'] as String,
      name: json['name'] as String,
      dateTime: json['dateTime'] as String,
      teamSlug: json['teamSlug'] as String,
      referencedDirectly: json['referencedDirectly'] as bool,
      viaChildNames: (json['viaChildNames'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
    );

Map<String, dynamic> _$RouteUsageDtoToJson(_RouteUsageDto instance) =>
    <String, dynamic>{
      'type': instance.type,
      'slug': instance.slug,
      'name': instance.name,
      'dateTime': instance.dateTime,
      'teamSlug': instance.teamSlug,
      'referencedDirectly': instance.referencedDirectly,
      'viaChildNames': instance.viaChildNames,
    };
