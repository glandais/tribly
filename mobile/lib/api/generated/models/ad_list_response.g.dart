// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ad_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdListResponse _$AdListResponseFromJson(Map<String, dynamic> json) =>
    _AdListResponse(
      ads: (json['ads'] as List<dynamic>)
          .map((e) => AdDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$AdListResponseToJson(_AdListResponse instance) =>
    <String, dynamic>{
      'ads': instance.ads.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
