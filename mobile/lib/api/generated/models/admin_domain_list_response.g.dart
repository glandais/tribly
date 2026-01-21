// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_domain_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminDomainListResponse _$AdminDomainListResponseFromJson(
  Map<String, dynamic> json,
) => _AdminDomainListResponse(
  domains: (json['domains'] as List<dynamic>)
      .map((e) => AdminDomainDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$AdminDomainListResponseToJson(
  _AdminDomainListResponse instance,
) => <String, dynamic>{
  'domains': instance.domains.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
