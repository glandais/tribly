// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'route_usages_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RouteUsagesResponse _$RouteUsagesResponseFromJson(Map<String, dynamic> json) =>
    _RouteUsagesResponse(
      usages: (json['usages'] as List<dynamic>)
          .map((e) => RouteUsageDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$RouteUsagesResponseToJson(
  _RouteUsagesResponse instance,
) => <String, dynamic>{
  'usages': instance.usages.map((e) => e.toJson()).toList(),
};
