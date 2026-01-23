/// Extension on String providing safe string operations that don't crash on empty strings.
extension SafeStringExtension on String {
  /// Returns the first character of the string, or [fallback] if the string is empty.
  ///
  /// Example:
  /// ```dart
  /// 'Hello'.safeFirst()      // 'H'
  /// ''.safeFirst()           // '?'
  /// ''.safeFirst('X')        // 'X'
  /// ```
  String safeFirst([String fallback = '?']) {
    return isEmpty ? fallback : this[0];
  }

  /// Returns the first character of the string uppercased, or [fallback] if the string is empty.
  ///
  /// Example:
  /// ```dart
  /// 'hello'.safeFirstUpper()  // 'H'
  /// ''.safeFirstUpper()       // '?'
  /// ```
  String safeFirstUpper([String fallback = '?']) {
    return isEmpty ? fallback : this[0].toUpperCase();
  }
}

/// Extension on nullable String providing safe string operations.
extension SafeNullableStringExtension on String? {
  /// Returns the first character of the string, or [fallback] if the string is null or empty.
  ///
  /// Example:
  /// ```dart
  /// String? name = 'Hello';
  /// name.safeFirst()          // 'H'
  /// name = null;
  /// name.safeFirst()          // '?'
  /// ```
  String safeFirst([String fallback = '?']) {
    final s = this;
    if (s == null || s.isEmpty) return fallback;
    return s[0];
  }

  /// Returns the first character of the string uppercased, or [fallback] if null/empty.
  ///
  /// Example:
  /// ```dart
  /// String? name = 'hello';
  /// name.safeFirstUpper()     // 'H'
  /// name = null;
  /// name.safeFirstUpper()     // '?'
  /// ```
  String safeFirstUpper([String fallback = '?']) {
    final s = this;
    if (s == null || s.isEmpty) return fallback;
    return s[0].toUpperCase();
  }
}
