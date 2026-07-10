// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'gpx_preview_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_GpxPreviewListResponse _$GpxPreviewListResponseFromJson(
  Map<String, dynamic> json,
) => _GpxPreviewListResponse(
  previews: (json['previews'] as List<dynamic>)
      .map((e) => GpxPreviewSummaryDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$GpxPreviewListResponseToJson(
  _GpxPreviewListResponse instance,
) => <String, dynamic>{
  'previews': instance.previews.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
