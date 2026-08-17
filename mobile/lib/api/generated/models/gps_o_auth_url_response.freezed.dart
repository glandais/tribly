// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'gps_o_auth_url_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GpsOAuthUrlResponse {

/// URL to redirect user for OAuth authorization
 String get authorizationUrl;
/// Create a copy of GpsOAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GpsOAuthUrlResponseCopyWith<GpsOAuthUrlResponse> get copyWith => _$GpsOAuthUrlResponseCopyWithImpl<GpsOAuthUrlResponse>(this as GpsOAuthUrlResponse, _$identity);

  /// Serializes this GpsOAuthUrlResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GpsOAuthUrlResponse&&(identical(other.authorizationUrl, authorizationUrl) || other.authorizationUrl == authorizationUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,authorizationUrl);

@override
String toString() {
  return 'GpsOAuthUrlResponse(authorizationUrl: $authorizationUrl)';
}


}

/// @nodoc
abstract mixin class $GpsOAuthUrlResponseCopyWith<$Res>  {
  factory $GpsOAuthUrlResponseCopyWith(GpsOAuthUrlResponse value, $Res Function(GpsOAuthUrlResponse) _then) = _$GpsOAuthUrlResponseCopyWithImpl;
@useResult
$Res call({
 String authorizationUrl
});




}
/// @nodoc
class _$GpsOAuthUrlResponseCopyWithImpl<$Res>
    implements $GpsOAuthUrlResponseCopyWith<$Res> {
  _$GpsOAuthUrlResponseCopyWithImpl(this._self, this._then);

  final GpsOAuthUrlResponse _self;
  final $Res Function(GpsOAuthUrlResponse) _then;

/// Create a copy of GpsOAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? authorizationUrl = null,}) {
  return _then(GpsOAuthUrlResponse(
authorizationUrl: null == authorizationUrl ? _self.authorizationUrl : authorizationUrl // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [GpsOAuthUrlResponse].
extension GpsOAuthUrlResponsePatterns on GpsOAuthUrlResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GpsOAuthUrlResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GpsOAuthUrlResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GpsOAuthUrlResponse value)  $default,){
final _that = this;
switch (_that) {
case _GpsOAuthUrlResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GpsOAuthUrlResponse value)?  $default,){
final _that = this;
switch (_that) {
case _GpsOAuthUrlResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String authorizationUrl)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GpsOAuthUrlResponse() when $default != null:
return $default(_that.authorizationUrl);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String authorizationUrl)  $default,) {final _that = this;
switch (_that) {
case _GpsOAuthUrlResponse():
return $default(_that.authorizationUrl);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String authorizationUrl)?  $default,) {final _that = this;
switch (_that) {
case _GpsOAuthUrlResponse() when $default != null:
return $default(_that.authorizationUrl);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GpsOAuthUrlResponse implements GpsOAuthUrlResponse {
  const _GpsOAuthUrlResponse({required this.authorizationUrl});
  factory _GpsOAuthUrlResponse.fromJson(Map<String, dynamic> json) => _$GpsOAuthUrlResponseFromJson(json);

/// URL to redirect user for OAuth authorization
@override final  String authorizationUrl;

/// Create a copy of GpsOAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GpsOAuthUrlResponseCopyWith<_GpsOAuthUrlResponse> get copyWith => __$GpsOAuthUrlResponseCopyWithImpl<_GpsOAuthUrlResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GpsOAuthUrlResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GpsOAuthUrlResponse&&(identical(other.authorizationUrl, authorizationUrl) || other.authorizationUrl == authorizationUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,authorizationUrl);

@override
String toString() {
  return 'GpsOAuthUrlResponse(authorizationUrl: $authorizationUrl)';
}


}

/// @nodoc
abstract mixin class _$GpsOAuthUrlResponseCopyWith<$Res> implements $GpsOAuthUrlResponseCopyWith<$Res> {
  factory _$GpsOAuthUrlResponseCopyWith(_GpsOAuthUrlResponse value, $Res Function(_GpsOAuthUrlResponse) _then) = __$GpsOAuthUrlResponseCopyWithImpl;
@override @useResult
$Res call({
 String authorizationUrl
});




}
/// @nodoc
class __$GpsOAuthUrlResponseCopyWithImpl<$Res>
    implements _$GpsOAuthUrlResponseCopyWith<$Res> {
  __$GpsOAuthUrlResponseCopyWithImpl(this._self, this._then);

  final _GpsOAuthUrlResponse _self;
  final $Res Function(_GpsOAuthUrlResponse) _then;

/// Create a copy of GpsOAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? authorizationUrl = null,}) {
  return _then(_GpsOAuthUrlResponse(
authorizationUrl: null == authorizationUrl ? _self.authorizationUrl : authorizationUrl // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
