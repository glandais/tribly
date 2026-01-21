// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'trip_participation_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TripParticipationDto _$TripParticipationDtoFromJson(
  Map<String, dynamic> json,
) => _TripParticipationDto(
  id: json['id'] as String,
  userId: json['userId'] as String,
  registeredAt: json['registeredAt'] as String?,
);

Map<String, dynamic> _$TripParticipationDtoToJson(
  _TripParticipationDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'userId': instance.userId,
  'registeredAt': instance.registeredAt,
};
