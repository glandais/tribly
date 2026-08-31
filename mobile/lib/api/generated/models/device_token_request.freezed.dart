// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'device_token_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DeviceTokenRequest {

/// Grant type: 'urn:ietf:params:oauth:grant-type:device_code' or 'refresh_token'
 String get grantType;/// Device code (for device_code grant)
 String? get deviceCode;/// Refresh token (for refresh_token grant)
 String? get refreshToken;
/// Create a copy of DeviceTokenRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DeviceTokenRequestCopyWith<DeviceTokenRequest> get copyWith => _$DeviceTokenRequestCopyWithImpl<DeviceTokenRequest>(this as DeviceTokenRequest, _$identity);

  /// Serializes this DeviceTokenRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as DeviceTokenRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DeviceTokenRequest&&(identical(other.grantType, _this.grantType) || other.grantType == _this.grantType)&&(identical(other.deviceCode, _this.deviceCode) || other.deviceCode == _this.deviceCode)&&(identical(other.refreshToken, _this.refreshToken) || other.refreshToken == _this.refreshToken));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as DeviceTokenRequest;
  return Object.hash(runtimeType,_this.grantType,_this.deviceCode,_this.refreshToken);
}

@override
String toString() {
  final _this = this as DeviceTokenRequest;
  return 'DeviceTokenRequest(grantType: ${_this.grantType}, deviceCode: ${_this.deviceCode}, refreshToken: ${_this.refreshToken})';
}


}

/// @nodoc
abstract mixin class $DeviceTokenRequestCopyWith<$Res>  {
  factory $DeviceTokenRequestCopyWith(DeviceTokenRequest value, $Res Function(DeviceTokenRequest) _then) = _$DeviceTokenRequestCopyWithImpl;
@useResult
$Res call({
 String grantType, String? deviceCode, String? refreshToken
});




}
/// @nodoc
class _$DeviceTokenRequestCopyWithImpl<$Res>
    implements $DeviceTokenRequestCopyWith<$Res> {
  _$DeviceTokenRequestCopyWithImpl(this._self, this._then);

  final DeviceTokenRequest _self;
  final $Res Function(DeviceTokenRequest) _then;

/// Create a copy of DeviceTokenRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? grantType = null,Object? deviceCode = freezed,Object? refreshToken = freezed,}) {
  return _then(DeviceTokenRequest(
grantType: null == grantType ? _self.grantType : grantType // ignore: cast_nullable_to_non_nullable
as String,deviceCode: freezed == deviceCode ? _self.deviceCode : deviceCode // ignore: cast_nullable_to_non_nullable
as String?,refreshToken: freezed == refreshToken ? _self.refreshToken : refreshToken // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [DeviceTokenRequest].
extension DeviceTokenRequestPatterns on DeviceTokenRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DeviceTokenRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DeviceTokenRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DeviceTokenRequest value)  $default,){
final _that = this;
switch (_that) {
case _DeviceTokenRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DeviceTokenRequest value)?  $default,){
final _that = this;
switch (_that) {
case _DeviceTokenRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String grantType,  String? deviceCode,  String? refreshToken)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _DeviceTokenRequest() when $default != null:
return $default(_that.grantType,_that.deviceCode,_that.refreshToken);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String grantType,  String? deviceCode,  String? refreshToken)  $default,) {final _that = this;
switch (_that) {
case _DeviceTokenRequest():
return $default(_that.grantType,_that.deviceCode,_that.refreshToken);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String grantType,  String? deviceCode,  String? refreshToken)?  $default,) {final _that = this;
switch (_that) {
case _DeviceTokenRequest() when $default != null:
return $default(_that.grantType,_that.deviceCode,_that.refreshToken);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DeviceTokenRequest implements DeviceTokenRequest {
  const _DeviceTokenRequest({required this.grantType, this.deviceCode, this.refreshToken});
  factory _DeviceTokenRequest.fromJson(Map<String, dynamic> json) => _$DeviceTokenRequestFromJson(json);

/// Grant type: 'urn:ietf:params:oauth:grant-type:device_code' or 'refresh_token'
@override final  String grantType;
/// Device code (for device_code grant)
@override final  String? deviceCode;
/// Refresh token (for refresh_token grant)
@override final  String? refreshToken;

/// Create a copy of DeviceTokenRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DeviceTokenRequestCopyWith<_DeviceTokenRequest> get copyWith => __$DeviceTokenRequestCopyWithImpl<_DeviceTokenRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DeviceTokenRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _DeviceTokenRequest&&(identical(other.grantType, grantType) || other.grantType == grantType)&&(identical(other.deviceCode, deviceCode) || other.deviceCode == deviceCode)&&(identical(other.refreshToken, refreshToken) || other.refreshToken == refreshToken));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,grantType,deviceCode,refreshToken);
}

@override
String toString() {
    return 'DeviceTokenRequest(grantType: $grantType, deviceCode: $deviceCode, refreshToken: $refreshToken)';
}


}

/// @nodoc
abstract mixin class _$DeviceTokenRequestCopyWith<$Res> implements $DeviceTokenRequestCopyWith<$Res> {
  factory _$DeviceTokenRequestCopyWith(_DeviceTokenRequest value, $Res Function(_DeviceTokenRequest) _then) = __$DeviceTokenRequestCopyWithImpl;
@override @useResult
$Res call({
 String grantType, String? deviceCode, String? refreshToken
});




}
/// @nodoc
class __$DeviceTokenRequestCopyWithImpl<$Res>
    implements _$DeviceTokenRequestCopyWith<$Res> {
  __$DeviceTokenRequestCopyWithImpl(this._self, this._then);

  final _DeviceTokenRequest _self;
  final $Res Function(_DeviceTokenRequest) _then;

/// Create a copy of DeviceTokenRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? grantType = null,Object? deviceCode = freezed,Object? refreshToken = freezed,}) {
  return _then(_DeviceTokenRequest(
grantType: null == grantType ? _self.grantType : grantType // ignore: cast_nullable_to_non_nullable
as String,deviceCode: freezed == deviceCode ? _self.deviceCode : deviceCode // ignore: cast_nullable_to_non_nullable
as String?,refreshToken: freezed == refreshToken ? _self.refreshToken : refreshToken // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
