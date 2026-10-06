// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_dashboard_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamDashboardDto _$TeamDashboardDtoFromJson(
  Map<String, dynamic> json,
) => _TeamDashboardDto(
  team: TeamDetailDto.fromJson(json['team'] as Map<String, dynamic>),
  role: json['role'] as String?,
  myUpcoming: json['myUpcoming'] == null
      ? null
      : PublicationListResponse.fromJson(
          json['myUpcoming'] as Map<String, dynamic>,
        ),
  upcomingRides: json['upcomingRides'] == null
      ? null
      : PublicationListResponse.fromJson(
          json['upcomingRides'] as Map<String, dynamic>,
        ),
  latestPosts: json['latestPosts'] == null
      ? null
      : PublicationListResponse.fromJson(
          json['latestPosts'] as Map<String, dynamic>,
        ),
  newRoutes: json['newRoutes'] == null
      ? null
      : RouteListResponse.fromJson(json['newRoutes'] as Map<String, dynamic>),
  latestAds: json['latestAds'] == null
      ? null
      : AdListResponse.fromJson(json['latestAds'] as Map<String, dynamic>),
  organizer: json['organizer'] == null
      ? null
      : TeamDashboardOrganizerDto.fromJson(
          json['organizer'] as Map<String, dynamic>,
        ),
  admin: json['admin'] == null
      ? null
      : TeamDashboardAdminDto.fromJson(json['admin'] as Map<String, dynamic>),
);

Map<String, dynamic> _$TeamDashboardDtoToJson(_TeamDashboardDto instance) =>
    <String, dynamic>{
      'team': instance.team.toJson(),
      'role': instance.role,
      'myUpcoming': instance.myUpcoming?.toJson(),
      'upcomingRides': instance.upcomingRides?.toJson(),
      'latestPosts': instance.latestPosts?.toJson(),
      'newRoutes': instance.newRoutes?.toJson(),
      'latestAds': instance.latestAds?.toJson(),
      'organizer': instance.organizer?.toJson(),
      'admin': instance.admin?.toJson(),
    };
