// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reorder_pages_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ReorderPagesRequest _$ReorderPagesRequestFromJson(Map<String, dynamic> json) =>
    _ReorderPagesRequest(
      pageIds: (json['pageIds'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
    );

Map<String, dynamic> _$ReorderPagesRequestToJson(
  _ReorderPagesRequest instance,
) => <String, dynamic>{'pageIds': instance.pageIds};
