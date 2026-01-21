// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ad_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdRequest _$AdRequestFromJson(Map<String, dynamic> json) => _AdRequest(
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  status: json['status'] as String,
  adType: json['adType'] as String,
  price: json['price'] as num?,
  rentalPeriod: json['rentalPeriod'] as String?,
  locationDescription: json['locationDescription'] as String?,
  locationGeometry: json['locationGeometry'] == null
      ? null
      : AdRequestLocationGeometry.fromJson(
          json['locationGeometry'] as Map<String, dynamic>,
        ),
);

Map<String, dynamic> _$AdRequestToJson(_AdRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'media': instance.media.toJson(),
      'status': instance.status,
      'adType': instance.adType,
      'price': instance.price,
      'rentalPeriod': instance.rentalPeriod,
      'locationDescription': instance.locationDescription,
      'locationGeometry': instance.locationGeometry?.toJson(),
    };
