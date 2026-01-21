// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamListResponse _$TeamListResponseFromJson(Map<String, dynamic> json) =>
    _TeamListResponse(
      teams: (json['teams'] as List<dynamic>)
          .map((e) => TeamDetailDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$TeamListResponseToJson(_TeamListResponse instance) =>
    <String, dynamic>{
      'teams': instance.teams.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
