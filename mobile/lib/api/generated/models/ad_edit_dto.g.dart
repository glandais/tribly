// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ad_edit_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdEditDto _$AdEditDtoFromJson(Map<String, dynamic> json) => _AdEditDto(
  team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
  id: json['id'] as String,
  slug: json['slug'] as String,
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  status: json['status'] as String,
  visibility: json['visibility'] as String,
  adType: json['adType'] as String,
  createdAt: json['createdAt'] as String,
  updatedAt: json['updatedAt'] as String,
  createdById: json['createdById'] as String,
  deleted: json['deleted'] as bool,
  price: json['price'] as num?,
  rentalPeriod: json['rentalPeriod'] as String?,
  locationGeometry: json['locationGeometry'] == null
      ? null
      : AdEditDtoLocationGeometry.fromJson(
          json['locationGeometry'] as Map<String, dynamic>,
        ),
  locationDescription: json['locationDescription'] as String?,
);

Map<String, dynamic> _$AdEditDtoToJson(_AdEditDto instance) =>
    <String, dynamic>{
      'team': instance.team.toJson(),
      'id': instance.id,
      'slug': instance.slug,
      'name': instance.name,
      'media': instance.media.toJson(),
      'status': instance.status,
      'visibility': instance.visibility,
      'adType': instance.adType,
      'createdAt': instance.createdAt,
      'updatedAt': instance.updatedAt,
      'createdById': instance.createdById,
      'deleted': instance.deleted,
      'price': instance.price,
      'rentalPeriod': instance.rentalPeriod,
      'locationGeometry': instance.locationGeometry?.toJson(),
      'locationDescription': instance.locationDescription,
    };
