// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_timezone_change_item_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamTimezoneChangeItemDto _$TeamTimezoneChangeItemDtoFromJson(
  Map<String, dynamic> json,
) => _TeamTimezoneChangeItemDto(
  type: json['type'] as String,
  id: json['id'] as String,
  slug: json['slug'] as String,
  title: json['title'] as String,
  dateTime: json['dateTime'] as String,
  tripTitle: json['tripTitle'] as String?,
);

Map<String, dynamic> _$TeamTimezoneChangeItemDtoToJson(
  _TeamTimezoneChangeItemDto instance,
) => <String, dynamic>{
  'type': instance.type,
  'id': instance.id,
  'slug': instance.slug,
  'title': instance.title,
  'dateTime': instance.dateTime,
  'tripTitle': instance.tripTitle,
};
