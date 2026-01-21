// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_team_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminTeamDto _$AdminTeamDtoFromJson(Map<String, dynamic> json) =>
    _AdminTeamDto(
      id: json['id'] as String,
      name: json['name'] as String,
      slug: json['slug'] as String,
      domainId: json['domainId'] as String,
      domainName: json['domainName'] as String,
      visibility: json['visibility'] as String,
      deleted: json['deleted'] as bool,
      memberCount: (json['memberCount'] as num).toInt(),
      createdAt: json['createdAt'] as String,
    );

Map<String, dynamic> _$AdminTeamDtoToJson(_AdminTeamDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'slug': instance.slug,
      'domainId': instance.domainId,
      'domainName': instance.domainName,
      'visibility': instance.visibility,
      'deleted': instance.deleted,
      'memberCount': instance.memberCount,
      'createdAt': instance.createdAt,
    };
