// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'device_code_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DeviceCodeResponse {

/// Device code for polling
 String get deviceCode;/// User code to display (e.g., 'ABCD12')
 String get userCode;/// Verification URL for user to visit
 String get verificationUri;/// Verification URL with user code embedded
 String get verificationUriComplete;/// Code expiry in seconds
 int get expiresIn;/// Minimum polling interval in seconds
 int get interval;
/// Create a copy of DeviceCodeResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DeviceCodeResponseCopyWith<DeviceCodeResponse> get copyWith => _$DeviceCodeResponseCopyWithImpl<DeviceCodeResponse>(this as DeviceCodeResponse, _$identity);

  /// Serializes this DeviceCodeResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as DeviceCodeResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DeviceCodeResponse&&(identical(other.deviceCode, _this.deviceCode) || other.deviceCode == _this.deviceCode)&&(identical(other.userCode, _this.userCode) || other.userCode == _this.userCode)&&(identical(other.verificationUri, _this.verificationUri) || other.verificationUri == _this.verificationUri)&&(identical(other.verificationUriComplete, _this.verificationUriComplete) || other.verificationUriComplete == _this.verificationUriComplete)&&(identical(other.expiresIn, _this.expiresIn) || other.expiresIn == _this.expiresIn)&&(identical(other.interval, _this.interval) || other.interval == _this.interval));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as DeviceCodeResponse;
  return Object.hash(runtimeType,_this.deviceCode,_this.userCode,_this.verificationUri,_this.verificationUriComplete,_this.expiresIn,_this.interval);
}

@override
String toString() {
  final _this = this as DeviceCodeResponse;
  return 'DeviceCodeResponse(deviceCode: ${_this.deviceCode}, userCode: ${_this.userCode}, verificationUri: ${_this.verificationUri}, verificationUriComplete: ${_this.verificationUriComplete}, expiresIn: ${_this.expiresIn}, interval: ${_this.interval})';
}


}

/// @nodoc
abstract mixin class $DeviceCodeResponseCopyWith<$Res>  {
  factory $DeviceCodeResponseCopyWith(DeviceCodeResponse value, $Res Function(DeviceCodeResponse) _then) = _$DeviceCodeResponseCopyWithImpl;
@useResult
$Res call({
 String deviceCode, String userCode, String verificationUri, String verificationUriComplete, int expiresIn, int interval
});




}
/// @nodoc
class _$DeviceCodeResponseCopyWithImpl<$Res>
    implements $DeviceCodeResponseCopyWith<$Res> {
  _$DeviceCodeResponseCopyWithImpl(this._self, this._then);

  final DeviceCodeResponse _self;
  final $Res Function(DeviceCodeResponse) _then;

/// Create a copy of DeviceCodeResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? deviceCode = null,Object? userCode = null,Object? verificationUri = null,Object? verificationUriComplete = null,Object? expiresIn = null,Object? interval = null,}) {
  return _then(DeviceCodeResponse(
deviceCode: null == deviceCode ? _self.deviceCode : deviceCode // ignore: cast_nullable_to_non_nullable
as String,userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,verificationUri: null == verificationUri ? _self.verificationUri : verificationUri // ignore: cast_nullable_to_non_nullable
as String,verificationUriComplete: null == verificationUriComplete ? _self.verificationUriComplete : verificationUriComplete // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,interval: null == interval ? _self.interval : interval // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [DeviceCodeResponse].
extension DeviceCodeResponsePatterns on DeviceCodeResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DeviceCodeResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DeviceCodeResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DeviceCodeResponse value)  $default,){
final _that = this;
switch (_that) {
case _DeviceCodeResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DeviceCodeResponse value)?  $default,){
final _that = this;
switch (_that) {
case _DeviceCodeResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String deviceCode,  String userCode,  String verificationUri,  String verificationUriComplete,  int expiresIn,  int interval)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _DeviceCodeResponse() when $default != null:
return $default(_that.deviceCode,_that.userCode,_that.verificationUri,_that.verificationUriComplete,_that.expiresIn,_that.interval);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String deviceCode,  String userCode,  String verificationUri,  String verificationUriComplete,  int expiresIn,  int interval)  $default,) {final _that = this;
switch (_that) {
case _DeviceCodeResponse():
return $default(_that.deviceCode,_that.userCode,_that.verificationUri,_that.verificationUriComplete,_that.expiresIn,_that.interval);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String deviceCode,  String userCode,  String verificationUri,  String verificationUriComplete,  int expiresIn,  int interval)?  $default,) {final _that = this;
switch (_that) {
case _DeviceCodeResponse() when $default != null:
return $default(_that.deviceCode,_that.userCode,_that.verificationUri,_that.verificationUriComplete,_that.expiresIn,_that.interval);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DeviceCodeResponse implements DeviceCodeResponse {
  const _DeviceCodeResponse({required this.deviceCode, required this.userCode, required this.verificationUri, required this.verificationUriComplete, required this.expiresIn, required this.interval});
  factory _DeviceCodeResponse.fromJson(Map<String, dynamic> json) => _$DeviceCodeResponseFromJson(json);

/// Device code for polling
@override final  String deviceCode;
/// User code to display (e.g., 'ABCD12')
@override final  String userCode;
/// Verification URL for user to visit
@override final  String verificationUri;
/// Verification URL with user code embedded
@override final  String verificationUriComplete;
/// Code expiry in seconds
@override final  int expiresIn;
/// Minimum polling interval in seconds
@override final  int interval;

/// Create a copy of DeviceCodeResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DeviceCodeResponseCopyWith<_DeviceCodeResponse> get copyWith => __$DeviceCodeResponseCopyWithImpl<_DeviceCodeResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DeviceCodeResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _DeviceCodeResponse&&(identical(other.deviceCode, deviceCode) || other.deviceCode == deviceCode)&&(identical(other.userCode, userCode) || other.userCode == userCode)&&(identical(other.verificationUri, verificationUri) || other.verificationUri == verificationUri)&&(identical(other.verificationUriComplete, verificationUriComplete) || other.verificationUriComplete == verificationUriComplete)&&(identical(other.expiresIn, expiresIn) || other.expiresIn == expiresIn)&&(identical(other.interval, interval) || other.interval == interval));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,deviceCode,userCode,verificationUri,verificationUriComplete,expiresIn,interval);
}

@override
String toString() {
    return 'DeviceCodeResponse(deviceCode: $deviceCode, userCode: $userCode, verificationUri: $verificationUri, verificationUriComplete: $verificationUriComplete, expiresIn: $expiresIn, interval: $interval)';
}


}

/// @nodoc
abstract mixin class _$DeviceCodeResponseCopyWith<$Res> implements $DeviceCodeResponseCopyWith<$Res> {
  factory _$DeviceCodeResponseCopyWith(_DeviceCodeResponse value, $Res Function(_DeviceCodeResponse) _then) = __$DeviceCodeResponseCopyWithImpl;
@override @useResult
$Res call({
 String deviceCode, String userCode, String verificationUri, String verificationUriComplete, int expiresIn, int interval
});




}
/// @nodoc
class __$DeviceCodeResponseCopyWithImpl<$Res>
    implements _$DeviceCodeResponseCopyWith<$Res> {
  __$DeviceCodeResponseCopyWithImpl(this._self, this._then);

  final _DeviceCodeResponse _self;
  final $Res Function(_DeviceCodeResponse) _then;

/// Create a copy of DeviceCodeResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? deviceCode = null,Object? userCode = null,Object? verificationUri = null,Object? verificationUriComplete = null,Object? expiresIn = null,Object? interval = null,}) {
  return _then(_DeviceCodeResponse(
deviceCode: null == deviceCode ? _self.deviceCode : deviceCode // ignore: cast_nullable_to_non_nullable
as String,userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,verificationUri: null == verificationUri ? _self.verificationUri : verificationUri // ignore: cast_nullable_to_non_nullable
as String,verificationUriComplete: null == verificationUriComplete ? _self.verificationUriComplete : verificationUriComplete // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,interval: null == interval ? _self.interval : interval // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
