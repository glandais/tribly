import 'package:dio/dio.dart';
import 'package:easy_localization/easy_localization.dart';

/// Extracts a user-friendly, localized error message from an exception.
///
/// If the exception is a [DioException] with an error response body,
/// maps the error code to an i18n key `errors.api.<CODE>`.
/// Falls back to `common.error` if no translation exists.
String getErrorMessage(Object error) {
  if (error is DioException) {
    final responseData = error.response?.data;
    if (responseData is Map<String, dynamic>) {
      final code = responseData['code'];
      if (code is String) {
        final key = 'errors.api.$code';
        final translated = key.tr();
        if (translated != key) {
          return translated;
        }
      }
    }
  }
  return 'common.error'.tr();
}
