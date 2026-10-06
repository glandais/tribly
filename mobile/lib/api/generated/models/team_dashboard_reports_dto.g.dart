// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_dashboard_reports_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamDashboardReportsDto _$TeamDashboardReportsDtoFromJson(
  Map<String, dynamic> json,
) => _TeamDashboardReportsDto(
  openCount: (json['openCount'] as num).toInt(),
  latestReason: json['latestReason'] as String?,
  latestTargetType: json['latestTargetType'] as String?,
  latestExcerpt: json['latestExcerpt'] as String?,
  latestReportedAt: json['latestReportedAt'] as String?,
);

Map<String, dynamic> _$TeamDashboardReportsDtoToJson(
  _TeamDashboardReportsDto instance,
) => <String, dynamic>{
  'openCount': instance.openCount,
  'latestReason': instance.latestReason,
  'latestTargetType': instance.latestTargetType,
  'latestExcerpt': instance.latestExcerpt,
  'latestReportedAt': instance.latestReportedAt,
};
