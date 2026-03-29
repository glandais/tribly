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
      memberCount: (json['memberCount'] as num).toInt(),
      createdAt: json['createdAt'] as String,
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
      'memberCount': instance.memberCount,
      'createdAt': instance.createdAt,
      'pages': instance.pages?.map((e) => e.toJson()).toList(),
      'role': instance.role,
      'geometry': instance.geometry?.toJson(),
    };
