// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'calendar_token_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CalendarTokenDto _$CalendarTokenDtoFromJson(Map<String, dynamic> json) =>
    _CalendarTokenDto(
      token: json['token'] as String,
      globalFeedUrl: json['globalFeedUrl'] as String,
      teamFeedUrlTemplate: json['teamFeedUrlTemplate'] as String,
    );

Map<String, dynamic> _$CalendarTokenDtoToJson(_CalendarTokenDto instance) =>
    <String, dynamic>{
      'token': instance.token,
      'globalFeedUrl': instance.globalFeedUrl,
      'teamFeedUrlTemplate': instance.teamFeedUrlTemplate,
    };
