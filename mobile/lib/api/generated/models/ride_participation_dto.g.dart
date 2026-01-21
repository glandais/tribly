// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_participation_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideParticipationDto _$RideParticipationDtoFromJson(
  Map<String, dynamic> json,
) => _RideParticipationDto(
  id: json['id'] as String,
  userId: json['userId'] as String,
  registeredAt: json['registeredAt'] as String?,
);

Map<String, dynamic> _$RideParticipationDtoToJson(
  _RideParticipationDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'userId': instance.userId,
  'registeredAt': instance.registeredAt,
};
