// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'thumbnail_owner_report.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ThumbnailOwnerReport _$ThumbnailOwnerReportFromJson(
  Map<String, dynamic> json,
) => _ThumbnailOwnerReport(
  id: json['id'] as String,
  kind: json['kind'] as String,
  teamSlug: json['teamSlug'] as String,
  slug: json['slug'] as String,
  reasons: (json['reasons'] as List<dynamic>)
      .map((e) => ThumbnailRegenerationReason.fromJson(e as String))
      .toList(),
  before: (json['before'] as List<dynamic>)
      .map((e) => ThumbnailFile.fromJson(e as Map<String, dynamic>))
      .toList(),
  outcome: json['outcome'] as String,
  after: (json['after'] as List<dynamic>?)
      ?.map((e) => ThumbnailFile.fromJson(e as Map<String, dynamic>))
      .toList(),
  error: json['error'] as String?,
);

Map<String, dynamic> _$ThumbnailOwnerReportToJson(
  _ThumbnailOwnerReport instance,
) => <String, dynamic>{
  'id': instance.id,
  'kind': instance.kind,
  'teamSlug': instance.teamSlug,
  'slug': instance.slug,
  'reasons': instance.reasons.map((e) => e.toJson()).toList(),
  'before': instance.before.map((e) => e.toJson()).toList(),
  'outcome': instance.outcome,
  'after': instance.after?.map((e) => e.toJson()).toList(),
  'error': instance.error,
};
