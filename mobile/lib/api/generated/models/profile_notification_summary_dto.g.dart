// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'profile_notification_summary_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ProfileNotificationSummaryDto _$ProfileNotificationSummaryDtoFromJson(
  Map<String, dynamic> json,
) => _ProfileNotificationSummaryDto(
  channels: (json['channels'] as List<dynamic>)
      .map((e) => NotificationChannel.fromJson(e as String))
      .toList(),
  enabledChannels: (json['enabledChannels'] as List<dynamic>)
      .map((e) => NotificationChannel.fromJson(e as String))
      .toList(),
  emailDigest: json['emailDigest'] as bool,
);

Map<String, dynamic> _$ProfileNotificationSummaryDtoToJson(
  _ProfileNotificationSummaryDto instance,
) => <String, dynamic>{
  'channels': instance.channels.map((e) => e.toJson()).toList(),
  'enabledChannels': instance.enabledChannels.map((e) => e.toJson()).toList(),
  'emailDigest': instance.emailDigest,
};
