// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'garmin_token_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GarminTokenResponse {

/// Access token
 String get accessToken;/// Token type (always 'Bearer')
 String get tokenType;/// Token expiry in seconds
 int get expiresIn;/// Refresh token
 String? get refreshToken;
/// Create a copy of GarminTokenResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GarminTokenResponseCopyWith<GarminTokenResponse> get copyWith => _$GarminTokenResponseCopyWithImpl<GarminTokenResponse>(this as GarminTokenResponse, _$identity);

  /// Serializes this GarminTokenResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GarminTokenResponse&&(identical(other.accessToken, accessToken) || other.accessToken == accessToken)&&(identical(other.tokenType, tokenType) || other.tokenType == tokenType)&&(identical(other.expiresIn, expiresIn) || other.expiresIn == expiresIn)&&(identical(other.refreshToken, refreshToken) || other.refreshToken == refreshToken));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,accessToken,tokenType,expiresIn,refreshToken);

@override
String toString() {
  return 'GarminTokenResponse(accessToken: $accessToken, tokenType: $tokenType, expiresIn: $expiresIn, refreshToken: $refreshToken)';
}


}

/// @nodoc
abstract mixin class $GarminTokenResponseCopyWith<$Res>  {
  factory $GarminTokenResponseCopyWith(GarminTokenResponse value, $Res Function(GarminTokenResponse) _then) = _$GarminTokenResponseCopyWithImpl;
@useResult
$Res call({
 String accessToken, String tokenType, int expiresIn, String? refreshToken
});




}
/// @nodoc
class _$GarminTokenResponseCopyWithImpl<$Res>
    implements $GarminTokenResponseCopyWith<$Res> {
  _$GarminTokenResponseCopyWithImpl(this._self, this._then);

  final GarminTokenResponse _self;
  final $Res Function(GarminTokenResponse) _then;

/// Create a copy of GarminTokenResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? accessToken = null,Object? tokenType = null,Object? expiresIn = null,Object? refreshToken = freezed,}) {
  return _then(_self.copyWith(
accessToken: null == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String,tokenType: null == tokenType ? _self.tokenType : tokenType // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,refreshToken: freezed == refreshToken ? _self.refreshToken : refreshToken // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [GarminTokenResponse].
extension GarminTokenResponsePatterns on GarminTokenResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GarminTokenResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GarminTokenResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GarminTokenResponse value)  $default,){
final _that = this;
switch (_that) {
case _GarminTokenResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GarminTokenResponse value)?  $default,){
final _that = this;
switch (_that) {
case _GarminTokenResponse() when $default != null:
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
case _GarminTokenResponse() when $default != null:
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
case _GarminTokenResponse():
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
case _GarminTokenResponse() when $default != null:
return $default(_that.accessToken,_that.tokenType,_that.expiresIn,_that.refreshToken);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GarminTokenResponse implements GarminTokenResponse {
  const _GarminTokenResponse({required this.accessToken, required this.tokenType, required this.expiresIn, this.refreshToken});
  factory _GarminTokenResponse.fromJson(Map<String, dynamic> json) => _$GarminTokenResponseFromJson(json);

/// Access token
@override final  String accessToken;
/// Token type (always 'Bearer')
@override final  String tokenType;
/// Token expiry in seconds
@override final  int expiresIn;
/// Refresh token
@override final  String? refreshToken;

/// Create a copy of GarminTokenResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GarminTokenResponseCopyWith<_GarminTokenResponse> get copyWith => __$GarminTokenResponseCopyWithImpl<_GarminTokenResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GarminTokenResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GarminTokenResponse&&(identical(other.accessToken, accessToken) || other.accessToken == accessToken)&&(identical(other.tokenType, tokenType) || other.tokenType == tokenType)&&(identical(other.expiresIn, expiresIn) || other.expiresIn == expiresIn)&&(identical(other.refreshToken, refreshToken) || other.refreshToken == refreshToken));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,accessToken,tokenType,expiresIn,refreshToken);

@override
String toString() {
  return 'GarminTokenResponse(accessToken: $accessToken, tokenType: $tokenType, expiresIn: $expiresIn, refreshToken: $refreshToken)';
}


}

/// @nodoc
abstract mixin class _$GarminTokenResponseCopyWith<$Res> implements $GarminTokenResponseCopyWith<$Res> {
  factory _$GarminTokenResponseCopyWith(_GarminTokenResponse value, $Res Function(_GarminTokenResponse) _then) = __$GarminTokenResponseCopyWithImpl;
@override @useResult
$Res call({
 String accessToken, String tokenType, int expiresIn, String? refreshToken
});




}
/// @nodoc
class __$GarminTokenResponseCopyWithImpl<$Res>
    implements _$GarminTokenResponseCopyWith<$Res> {
  __$GarminTokenResponseCopyWithImpl(this._self, this._then);

  final _GarminTokenResponse _self;
  final $Res Function(_GarminTokenResponse) _then;

/// Create a copy of GarminTokenResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? accessToken = null,Object? tokenType = null,Object? expiresIn = null,Object? refreshToken = freezed,}) {
  return _then(_GarminTokenResponse(
accessToken: null == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String,tokenType: null == tokenType ? _self.tokenType : tokenType // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,refreshToken: freezed == refreshToken ? _self.refreshToken : refreshToken // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
