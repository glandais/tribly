// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_stats_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminStatsDto _$AdminStatsDtoFromJson(Map<String, dynamic> json) =>
    _AdminStatsDto(
      totalDomains: (json['totalDomains'] as num).toInt(),
      totalTeams: (json['totalTeams'] as num).toInt(),
      totalUsers: (json['totalUsers'] as num).toInt(),
    );

Map<String, dynamic> _$AdminStatsDtoToJson(_AdminStatsDto instance) =>
    <String, dynamic>{
      'totalDomains': instance.totalDomains,
      'totalTeams': instance.totalTeams,
      'totalUsers': instance.totalUsers,
    };
