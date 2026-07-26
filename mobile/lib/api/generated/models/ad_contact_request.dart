// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'ad_contact_request.freezed.dart';
part 'ad_contact_request.g.dart';

/// A message to relay to the author of an ad
@Freezed()
abstract class AdContactRequest with _$AdContactRequest {
  const factory AdContactRequest({
    /// The message body, plain text. Rendered as text in the email: any markup is escaped rather than interpreted.
    required String message,
  }) = _AdContactRequest;

  factory AdContactRequest.fromJson(Map<String, Object?> json) =>
      _$AdContactRequestFromJson(json);
}
