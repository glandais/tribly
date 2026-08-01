// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'beta_signup_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BetaSignupDto _$BetaSignupDtoFromJson(Map<String, dynamic> json) =>
    _BetaSignupDto(
      id: json['id'] as String,
      email: json['email'] as String,
      createdAt: json['createdAt'] as String,
    );

Map<String, dynamic> _$BetaSignupDtoToJson(_BetaSignupDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'email': instance.email,
      'createdAt': instance.createdAt,
    };
