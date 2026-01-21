// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'not_found_details.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotFoundDetails _$NotFoundDetailsFromJson(Map<String, dynamic> json) =>
    _NotFoundDetails(
      type: json['type'] as String,
      entityType: json['entityType'] as String,
      searchedBy: json['searchedBy'] as String,
      id: json['id'] as String,
    );

Map<String, dynamic> _$NotFoundDetailsToJson(_NotFoundDetails instance) =>
    <String, dynamic>{
      'type': instance.type,
      'entityType': instance.entityType,
      'searchedBy': instance.searchedBy,
      'id': instance.id,
    };
