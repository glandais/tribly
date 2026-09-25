// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'account_deletion_impact_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AccountDeletionImpactDto _$AccountDeletionImpactDtoFromJson(
  Map<String, dynamic> json,
) => _AccountDeletionImpactDto(
  blocked: json['blocked'] as bool,
  blockingTeams: (json['blockingTeams'] as List<dynamic>)
      .map((e) => TeamPublicationDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  deletedTeams: (json['deletedTeams'] as List<dynamic>)
      .map((e) => TeamPublicationDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$AccountDeletionImpactDtoToJson(
  _AccountDeletionImpactDto instance,
) => <String, dynamic>{
  'blocked': instance.blocked,
  'blockingTeams': instance.blockingTeams.map((e) => e.toJson()).toList(),
  'deletedTeams': instance.deletedTeams.map((e) => e.toJson()).toList(),
};
