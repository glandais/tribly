// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_detail_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamDetailDto _$TeamDetailDtoFromJson(Map<String, dynamic> json) =>
    _TeamDetailDto(
      id: json['id'] as String,
      name: json['name'] as String,
      slug: json['slug'] as String,
      about: MediaDto.fromJson(json['about'] as Map<String, dynamic>),
      visibility: json['visibility'] as String,
      enableTrips: json['enableTrips'] as bool,
      enableAds: json['enableAds'] as bool,
      enablePosts: json['enablePosts'] as bool,
      enableRides: json['enableRides'] as bool,
      enableRoutes: json['enableRoutes'] as bool,
      enableMemberDirectory: json['enableMemberDirectory'] as bool,
      visibilityEditable: json['visibilityEditable'] as bool,
      joinable: json['joinable'] as bool,
      addMemberAllowed: json['addMemberAllowed'] as bool,
      enableRoutePlanner: json['enableRoutePlanner'] as bool,
      memberCount: (json['memberCount'] as num).toInt(),
      upcomingRideCount: (json['upcomingRideCount'] as num).toInt(),
      routeCount: (json['routeCount'] as num).toInt(),
      createdAt: json['createdAt'] as String,
      excerpt: json['excerpt'] as String?,
      logoUrl: json['logoUrl'] as String?,
      pages: (json['pages'] as List<dynamic>?)
          ?.map((e) => TeamPageSummaryDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      role: json['role'] as String?,
      geometry: json['geometry'] == null
          ? null
          : TeamDetailDtoGeometry.fromJson(
              json['geometry'] as Map<String, dynamic>,
            ),
    );

Map<String, dynamic> _$TeamDetailDtoToJson(_TeamDetailDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'slug': instance.slug,
      'about': instance.about.toJson(),
      'visibility': instance.visibility,
      'enableTrips': instance.enableTrips,
      'enableAds': instance.enableAds,
      'enablePosts': instance.enablePosts,
      'enableRides': instance.enableRides,
      'enableRoutes': instance.enableRoutes,
      'enableMemberDirectory': instance.enableMemberDirectory,
      'visibilityEditable': instance.visibilityEditable,
      'joinable': instance.joinable,
      'addMemberAllowed': instance.addMemberAllowed,
      'enableRoutePlanner': instance.enableRoutePlanner,
      'memberCount': instance.memberCount,
      'upcomingRideCount': instance.upcomingRideCount,
      'routeCount': instance.routeCount,
      'createdAt': instance.createdAt,
      'excerpt': instance.excerpt,
      'logoUrl': instance.logoUrl,
      'pages': instance.pages?.map((e) => e.toJson()).toList(),
      'role': instance.role,
      'geometry': instance.geometry?.toJson(),
    };
