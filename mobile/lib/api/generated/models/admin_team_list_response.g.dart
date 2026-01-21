// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_team_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminTeamListResponse _$AdminTeamListResponseFromJson(
  Map<String, dynamic> json,
) => _AdminTeamListResponse(
  teams: (json['teams'] as List<dynamic>)
      .map((e) => AdminTeamDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$AdminTeamListResponseToJson(
  _AdminTeamListResponse instance,
) => <String, dynamic>{
  'teams': instance.teams.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
