// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'beta_signup_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BetaSignupListResponse _$BetaSignupListResponseFromJson(
  Map<String, dynamic> json,
) => _BetaSignupListResponse(
  signups: (json['signups'] as List<dynamic>)
      .map((e) => BetaSignupDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$BetaSignupListResponseToJson(
  _BetaSignupListResponse instance,
) => <String, dynamic>{
  'signups': instance.signups.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
