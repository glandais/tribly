// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'moderation_decision_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ModerationDecisionRequest _$ModerationDecisionRequestFromJson(
  Map<String, dynamic> json,
) => _ModerationDecisionRequest(
  targetType: json['targetType'] as String,
  targetId: json['targetId'] as String,
  action: json['action'] as String,
);

Map<String, dynamic> _$ModerationDecisionRequestToJson(
  _ModerationDecisionRequest instance,
) => <String, dynamic>{
  'targetType': instance.targetType,
  'targetId': instance.targetId,
  'action': instance.action,
};
