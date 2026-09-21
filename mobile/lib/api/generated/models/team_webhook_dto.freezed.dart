// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_webhook_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamWebhookDto {

/// Whether the team has a webhook at all
 bool get configured;/// Whether announcements are posted
 bool get enabled;/// The URL, masked: scheme, host and the last characters only
 String? get maskedUrl;/// Message format, read from the URL
 String? get kind;/// Language the messages are written in
 String? get language;/// Outcome of the latest attempt
 String? get lastStatus;/// Why the latest attempt failed, when it did
 String? get lastError;/// When the latest attempt was made
 String? get lastAttemptAt;
/// Create a copy of TeamWebhookDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamWebhookDtoCopyWith<TeamWebhookDto> get copyWith => _$TeamWebhookDtoCopyWithImpl<TeamWebhookDto>(this as TeamWebhookDto, _$identity);

  /// Serializes this TeamWebhookDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamWebhookDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamWebhookDto&&(identical(other.configured, _this.configured) || other.configured == _this.configured)&&(identical(other.enabled, _this.enabled) || other.enabled == _this.enabled)&&(identical(other.maskedUrl, _this.maskedUrl) || other.maskedUrl == _this.maskedUrl)&&(identical(other.kind, _this.kind) || other.kind == _this.kind)&&(identical(other.language, _this.language) || other.language == _this.language)&&(identical(other.lastStatus, _this.lastStatus) || other.lastStatus == _this.lastStatus)&&(identical(other.lastError, _this.lastError) || other.lastError == _this.lastError)&&(identical(other.lastAttemptAt, _this.lastAttemptAt) || other.lastAttemptAt == _this.lastAttemptAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamWebhookDto;
  return Object.hash(runtimeType,_this.configured,_this.enabled,_this.maskedUrl,_this.kind,_this.language,_this.lastStatus,_this.lastError,_this.lastAttemptAt);
}

@override
String toString() {
  final _this = this as TeamWebhookDto;
  return 'TeamWebhookDto(configured: ${_this.configured}, enabled: ${_this.enabled}, maskedUrl: ${_this.maskedUrl}, kind: ${_this.kind}, language: ${_this.language}, lastStatus: ${_this.lastStatus}, lastError: ${_this.lastError}, lastAttemptAt: ${_this.lastAttemptAt})';
}


}

/// @nodoc
abstract mixin class $TeamWebhookDtoCopyWith<$Res>  {
  factory $TeamWebhookDtoCopyWith(TeamWebhookDto value, $Res Function(TeamWebhookDto) _then) = _$TeamWebhookDtoCopyWithImpl;
@useResult
$Res call({
 bool configured, bool enabled, String? maskedUrl, String? kind, String? language, String? lastStatus, String? lastError, String? lastAttemptAt
});




}
/// @nodoc
class _$TeamWebhookDtoCopyWithImpl<$Res>
    implements $TeamWebhookDtoCopyWith<$Res> {
  _$TeamWebhookDtoCopyWithImpl(this._self, this._then);

  final TeamWebhookDto _self;
  final $Res Function(TeamWebhookDto) _then;

/// Create a copy of TeamWebhookDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? configured = null,Object? enabled = null,Object? maskedUrl = freezed,Object? kind = freezed,Object? language = freezed,Object? lastStatus = freezed,Object? lastError = freezed,Object? lastAttemptAt = freezed,}) {
  return _then(TeamWebhookDto(
configured: null == configured ? _self.configured : configured // ignore: cast_nullable_to_non_nullable
as bool,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,maskedUrl: freezed == maskedUrl ? _self.maskedUrl : maskedUrl // ignore: cast_nullable_to_non_nullable
as String?,kind: freezed == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String?,language: freezed == language ? _self.language : language // ignore: cast_nullable_to_non_nullable
as String?,lastStatus: freezed == lastStatus ? _self.lastStatus : lastStatus // ignore: cast_nullable_to_non_nullable
as String?,lastError: freezed == lastError ? _self.lastError : lastError // ignore: cast_nullable_to_non_nullable
as String?,lastAttemptAt: freezed == lastAttemptAt ? _self.lastAttemptAt : lastAttemptAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamWebhookDto].
extension TeamWebhookDtoPatterns on TeamWebhookDto {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamWebhookDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamWebhookDto() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamWebhookDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamWebhookDto():
return $default(_that);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamWebhookDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamWebhookDto() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( bool configured,  bool enabled,  String? maskedUrl,  String? kind,  String? language,  String? lastStatus,  String? lastError,  String? lastAttemptAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamWebhookDto() when $default != null:
return $default(_that.configured,_that.enabled,_that.maskedUrl,_that.kind,_that.language,_that.lastStatus,_that.lastError,_that.lastAttemptAt);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( bool configured,  bool enabled,  String? maskedUrl,  String? kind,  String? language,  String? lastStatus,  String? lastError,  String? lastAttemptAt)  $default,) {final _that = this;
switch (_that) {
case _TeamWebhookDto():
return $default(_that.configured,_that.enabled,_that.maskedUrl,_that.kind,_that.language,_that.lastStatus,_that.lastError,_that.lastAttemptAt);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( bool configured,  bool enabled,  String? maskedUrl,  String? kind,  String? language,  String? lastStatus,  String? lastError,  String? lastAttemptAt)?  $default,) {final _that = this;
switch (_that) {
case _TeamWebhookDto() when $default != null:
return $default(_that.configured,_that.enabled,_that.maskedUrl,_that.kind,_that.language,_that.lastStatus,_that.lastError,_that.lastAttemptAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamWebhookDto implements TeamWebhookDto {
  const _TeamWebhookDto({required this.configured, required this.enabled, this.maskedUrl, this.kind, this.language, this.lastStatus, this.lastError, this.lastAttemptAt});
  factory _TeamWebhookDto.fromJson(Map<String, dynamic> json) => _$TeamWebhookDtoFromJson(json);

/// Whether the team has a webhook at all
@override final  bool configured;
/// Whether announcements are posted
@override final  bool enabled;
/// The URL, masked: scheme, host and the last characters only
@override final  String? maskedUrl;
/// Message format, read from the URL
@override final  String? kind;
/// Language the messages are written in
@override final  String? language;
/// Outcome of the latest attempt
@override final  String? lastStatus;
/// Why the latest attempt failed, when it did
@override final  String? lastError;
/// When the latest attempt was made
@override final  String? lastAttemptAt;

/// Create a copy of TeamWebhookDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamWebhookDtoCopyWith<_TeamWebhookDto> get copyWith => __$TeamWebhookDtoCopyWithImpl<_TeamWebhookDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamWebhookDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamWebhookDto&&(identical(other.configured, configured) || other.configured == configured)&&(identical(other.enabled, enabled) || other.enabled == enabled)&&(identical(other.maskedUrl, maskedUrl) || other.maskedUrl == maskedUrl)&&(identical(other.kind, kind) || other.kind == kind)&&(identical(other.language, language) || other.language == language)&&(identical(other.lastStatus, lastStatus) || other.lastStatus == lastStatus)&&(identical(other.lastError, lastError) || other.lastError == lastError)&&(identical(other.lastAttemptAt, lastAttemptAt) || other.lastAttemptAt == lastAttemptAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,configured,enabled,maskedUrl,kind,language,lastStatus,lastError,lastAttemptAt);
}

@override
String toString() {
    return 'TeamWebhookDto(configured: $configured, enabled: $enabled, maskedUrl: $maskedUrl, kind: $kind, language: $language, lastStatus: $lastStatus, lastError: $lastError, lastAttemptAt: $lastAttemptAt)';
}


}

/// @nodoc
abstract mixin class _$TeamWebhookDtoCopyWith<$Res> implements $TeamWebhookDtoCopyWith<$Res> {
  factory _$TeamWebhookDtoCopyWith(_TeamWebhookDto value, $Res Function(_TeamWebhookDto) _then) = __$TeamWebhookDtoCopyWithImpl;
@override @useResult
$Res call({
 bool configured, bool enabled, String? maskedUrl, String? kind, String? language, String? lastStatus, String? lastError, String? lastAttemptAt
});




}
/// @nodoc
class __$TeamWebhookDtoCopyWithImpl<$Res>
    implements _$TeamWebhookDtoCopyWith<$Res> {
  __$TeamWebhookDtoCopyWithImpl(this._self, this._then);

  final _TeamWebhookDto _self;
  final $Res Function(_TeamWebhookDto) _then;

/// Create a copy of TeamWebhookDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? configured = null,Object? enabled = null,Object? maskedUrl = freezed,Object? kind = freezed,Object? language = freezed,Object? lastStatus = freezed,Object? lastError = freezed,Object? lastAttemptAt = freezed,}) {
  return _then(_TeamWebhookDto(
configured: null == configured ? _self.configured : configured // ignore: cast_nullable_to_non_nullable
as bool,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,maskedUrl: freezed == maskedUrl ? _self.maskedUrl : maskedUrl // ignore: cast_nullable_to_non_nullable
as String?,kind: freezed == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String?,language: freezed == language ? _self.language : language // ignore: cast_nullable_to_non_nullable
as String?,lastStatus: freezed == lastStatus ? _self.lastStatus : lastStatus // ignore: cast_nullable_to_non_nullable
as String?,lastError: freezed == lastError ? _self.lastError : lastError // ignore: cast_nullable_to_non_nullable
as String?,lastAttemptAt: freezed == lastAttemptAt ? _self.lastAttemptAt : lastAttemptAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
