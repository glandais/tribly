// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'social_identity_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_SocialIdentityDto _$SocialIdentityDtoFromJson(Map<String, dynamic> json) =>
    _SocialIdentityDto(
      provider: json['provider'] as String,
      displayName: json['displayName'] as String,
      linkedAt: json['linkedAt'] as String,
    );

Map<String, dynamic> _$SocialIdentityDtoToJson(_SocialIdentityDto instance) =>
    <String, dynamic>{
      'provider': instance.provider,
      'displayName': instance.displayName,
      'linkedAt': instance.linkedAt,
    };
