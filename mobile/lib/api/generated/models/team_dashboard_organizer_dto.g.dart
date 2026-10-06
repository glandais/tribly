// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_dashboard_organizer_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamDashboardOrganizerDto _$TeamDashboardOrganizerDtoFromJson(
  Map<String, dynamic> json,
) => _TeamDashboardOrganizerDto(
  drafts: PublicationListResponse.fromJson(
    json['drafts'] as Map<String, dynamic>,
  ),
  reports: TeamDashboardReportsDto.fromJson(
    json['reports'] as Map<String, dynamic>,
  ),
  ridesWithoutRoute: json['ridesWithoutRoute'] == null
      ? null
      : PublicationListResponse.fromJson(
          json['ridesWithoutRoute'] as Map<String, dynamic>,
        ),
  ridesWithFullGroup: json['ridesWithFullGroup'] == null
      ? null
      : PublicationListResponse.fromJson(
          json['ridesWithFullGroup'] as Map<String, dynamic>,
        ),
  rideTemplates: json['rideTemplates'] == null
      ? null
      : RideTemplateListResponse.fromJson(
          json['rideTemplates'] as Map<String, dynamic>,
        ),
);

Map<String, dynamic> _$TeamDashboardOrganizerDtoToJson(
  _TeamDashboardOrganizerDto instance,
) => <String, dynamic>{
  'drafts': instance.drafts.toJson(),
  'reports': instance.reports.toJson(),
  'ridesWithoutRoute': instance.ridesWithoutRoute?.toJson(),
  'ridesWithFullGroup': instance.ridesWithFullGroup?.toJson(),
  'rideTemplates': instance.rideTemplates?.toJson(),
};
