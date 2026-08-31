// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'device_token_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DeviceTokenResponse {

/// Access token
 String get accessToken;/// Token type (always 'Bearer')
 String get tokenType;/// Token expiry in seconds
 int get expiresIn;/// Refresh token
 String? get refreshToken;
/// Create a copy of DeviceTokenResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DeviceTokenResponseCopyWith<DeviceTokenResponse> get copyWith => _$DeviceTokenResponseCopyWithImpl<DeviceTokenResponse>(this as DeviceTokenResponse, _$identity);

  /// Serializes this DeviceTokenResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as DeviceTokenResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DeviceTokenResponse&&(identical(other.accessToken, _this.accessToken) || other.accessToken == _this.accessToken)&&(identical(other.tokenType, _this.tokenType) || other.tokenType == _this.tokenType)&&(identical(other.expiresIn, _this.expiresIn) || other.expiresIn == _this.expiresIn)&&(identical(other.refreshToken, _this.refreshToken) || other.refreshToken == _this.refreshToken));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as DeviceTokenResponse;
  return Object.hash(runtimeType,_this.accessToken,_this.tokenType,_this.expiresIn,_this.refreshToken);
}

@override
String toString() {
  final _this = this as DeviceTokenResponse;
  return 'DeviceTokenResponse(accessToken: ${_this.accessToken}, tokenType: ${_this.tokenType}, expiresIn: ${_this.expiresIn}, refreshToken: ${_this.refreshToken})';
}


}

/// @nodoc
abstract mixin class $DeviceTokenResponseCopyWith<$Res>  {
  factory $DeviceTokenResponseCopyWith(DeviceTokenResponse value, $Res Function(DeviceTokenResponse) _then) = _$DeviceTokenResponseCopyWithImpl;
@useResult
$Res call({
 String accessToken, String tokenType, int expiresIn, String? refreshToken
});




}
/// @nodoc
class _$DeviceTokenResponseCopyWithImpl<$Res>
    implements $DeviceTokenResponseCopyWith<$Res> {
  _$DeviceTokenResponseCopyWithImpl(this._self, this._then);

  final DeviceTokenResponse _self;
  final $Res Function(DeviceTokenResponse) _then;

/// Create a copy of DeviceTokenResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? accessToken = null,Object? tokenType = null,Object? expiresIn = null,Object? refreshToken = freezed,}) {
  return _then(DeviceTokenResponse(
accessToken: null == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String,tokenType: null == tokenType ? _self.tokenType : tokenType // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,refreshToken: freezed == refreshToken ? _self.refreshToken : refreshToken // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [DeviceTokenResponse].
extension DeviceTokenResponsePatterns on DeviceTokenResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DeviceTokenResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DeviceTokenResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DeviceTokenResponse value)  $default,){
final _that = this;
switch (_that) {
case _DeviceTokenResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DeviceTokenResponse value)?  $default,){
final _that = this;
switch (_that) {
case _DeviceTokenResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String accessToken,  String tokenType,  int expiresIn,  String? refreshToken)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _DeviceTokenResponse() when $default != null:
return $default(_that.accessToken,_that.tokenType,_that.expiresIn,_that.refreshToken);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String accessToken,  String tokenType,  int expiresIn,  String? refreshToken)  $default,) {final _that = this;
switch (_that) {
case _DeviceTokenResponse():
return $default(_that.accessToken,_that.tokenType,_that.expiresIn,_that.refreshToken);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String accessToken,  String tokenType,  int expiresIn,  String? refreshToken)?  $default,) {final _that = this;
switch (_that) {
case _DeviceTokenResponse() when $default != null:
return $default(_that.accessToken,_that.tokenType,_that.expiresIn,_that.refreshToken);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DeviceTokenResponse implements DeviceTokenResponse {
  const _DeviceTokenResponse({required this.accessToken, required this.tokenType, required this.expiresIn, this.refreshToken});
  factory _DeviceTokenResponse.fromJson(Map<String, dynamic> json) => _$DeviceTokenResponseFromJson(json);

/// Access token
@override final  String accessToken;
/// Token type (always 'Bearer')
@override final  String tokenType;
/// Token expiry in seconds
@override final  int expiresIn;
/// Refresh token
@override final  String? refreshToken;

/// Create a copy of DeviceTokenResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DeviceTokenResponseCopyWith<_DeviceTokenResponse> get copyWith => __$DeviceTokenResponseCopyWithImpl<_DeviceTokenResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DeviceTokenResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _DeviceTokenResponse&&(identical(other.accessToken, accessToken) || other.accessToken == accessToken)&&(identical(other.tokenType, tokenType) || other.tokenType == tokenType)&&(identical(other.expiresIn, expiresIn) || other.expiresIn == expiresIn)&&(identical(other.refreshToken, refreshToken) || other.refreshToken == refreshToken));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,accessToken,tokenType,expiresIn,refreshToken);
}

@override
String toString() {
    return 'DeviceTokenResponse(accessToken: $accessToken, tokenType: $tokenType, expiresIn: $expiresIn, refreshToken: $refreshToken)';
}


}

/// @nodoc
abstract mixin class _$DeviceTokenResponseCopyWith<$Res> implements $DeviceTokenResponseCopyWith<$Res> {
  factory _$DeviceTokenResponseCopyWith(_DeviceTokenResponse value, $Res Function(_DeviceTokenResponse) _then) = __$DeviceTokenResponseCopyWithImpl;
@override @useResult
$Res call({
 String accessToken, String tokenType, int expiresIn, String? refreshToken
});




}
/// @nodoc
class __$DeviceTokenResponseCopyWithImpl<$Res>
    implements _$DeviceTokenResponseCopyWith<$Res> {
  __$DeviceTokenResponseCopyWithImpl(this._self, this._then);

  final _DeviceTokenResponse _self;
  final $Res Function(_DeviceTokenResponse) _then;

/// Create a copy of DeviceTokenResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? accessToken = null,Object? tokenType = null,Object? expiresIn = null,Object? refreshToken = freezed,}) {
  return _then(_DeviceTokenResponse(
accessToken: null == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String,tokenType: null == tokenType ? _self.tokenType : tokenType // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,refreshToken: freezed == refreshToken ? _self.refreshToken : refreshToken // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
