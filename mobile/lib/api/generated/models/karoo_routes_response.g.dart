// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'karoo_routes_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_KarooRoutesResponse _$KarooRoutesResponseFromJson(Map<String, dynamic> json) =>
    _KarooRoutesResponse(
      routes: (json['routes'] as List<dynamic>)
          .map((e) => KarooRouteDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$KarooRoutesResponseToJson(
  _KarooRoutesResponse instance,
) => <String, dynamic>{
  'routes': instance.routes.map((e) => e.toJson()).toList(),
};
