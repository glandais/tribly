// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'participant_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ParticipantListResponse _$ParticipantListResponseFromJson(
  Map<String, dynamic> json,
) => _ParticipantListResponse(
  participants: (json['participants'] as List<dynamic>)
      .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$ParticipantListResponseToJson(
  _ParticipantListResponse instance,
) => <String, dynamic>{
  'participants': instance.participants.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
