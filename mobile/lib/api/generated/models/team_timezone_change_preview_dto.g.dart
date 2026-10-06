// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_timezone_change_preview_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamTimezoneChangePreviewDto _$TeamTimezoneChangePreviewDtoFromJson(
  Map<String, dynamic> json,
) => _TeamTimezoneChangePreviewDto(
  from: json['from'] as String,
  to: json['to'] as String,
  upcomingCount: (json['upcomingCount'] as num).toInt(),
  pastCount: (json['pastCount'] as num).toInt(),
  upcoming: (json['upcoming'] as List<dynamic>)
      .map((e) => TeamTimezoneChangeItemDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$TeamTimezoneChangePreviewDtoToJson(
  _TeamTimezoneChangePreviewDto instance,
) => <String, dynamic>{
  'from': instance.from,
  'to': instance.to,
  'upcomingCount': instance.upcomingCount,
  'pastCount': instance.pastCount,
  'upcoming': instance.upcoming.map((e) => e.toJson()).toList(),
};
