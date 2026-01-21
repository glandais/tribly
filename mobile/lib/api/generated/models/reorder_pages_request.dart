// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'reorder_pages_request.freezed.dart';
part 'reorder_pages_request.g.dart';

/// Request to reorder team pages
@Freezed()
abstract class ReorderPagesRequest with _$ReorderPagesRequest {
  const factory ReorderPagesRequest({
    /// Ordered list of page IDs
    required List<String> pageIds,
  }) = _ReorderPagesRequest;

  factory ReorderPagesRequest.fromJson(Map<String, Object?> json) =>
      _$ReorderPagesRequestFromJson(json);
}
