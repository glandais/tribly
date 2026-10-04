// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'profile_participation_summary_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ProfileParticipationSummaryDto _$ProfileParticipationSummaryDtoFromJson(
  Map<String, dynamic> json,
) => _ProfileParticipationSummaryDto(
  upcomingCount: (json['upcomingCount'] as num).toInt(),
  pastCount: (json['pastCount'] as num).toInt(),
  next: (json['next'] as List<dynamic>)
      .map((e) => PublicationDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$ProfileParticipationSummaryDtoToJson(
  _ProfileParticipationSummaryDto instance,
) => <String, dynamic>{
  'upcomingCount': instance.upcomingCount,
  'pastCount': instance.pastCount,
  'next': instance.next.map((e) => e.toJson()).toList(),
};
