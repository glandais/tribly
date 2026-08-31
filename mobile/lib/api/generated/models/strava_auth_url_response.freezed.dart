// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'strava_auth_url_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$StravaAuthUrlResponse {

/// URL to redirect the user to for Strava authorization
 String get authorizationUrl;
/// Create a copy of StravaAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$StravaAuthUrlResponseCopyWith<StravaAuthUrlResponse> get copyWith => _$StravaAuthUrlResponseCopyWithImpl<StravaAuthUrlResponse>(this as StravaAuthUrlResponse, _$identity);

  /// Serializes this StravaAuthUrlResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as StravaAuthUrlResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is StravaAuthUrlResponse&&(identical(other.authorizationUrl, _this.authorizationUrl) || other.authorizationUrl == _this.authorizationUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as StravaAuthUrlResponse;
  return Object.hash(runtimeType,_this.authorizationUrl);
}

@override
String toString() {
  final _this = this as StravaAuthUrlResponse;
  return 'StravaAuthUrlResponse(authorizationUrl: ${_this.authorizationUrl})';
}


}

/// @nodoc
abstract mixin class $StravaAuthUrlResponseCopyWith<$Res>  {
  factory $StravaAuthUrlResponseCopyWith(StravaAuthUrlResponse value, $Res Function(StravaAuthUrlResponse) _then) = _$StravaAuthUrlResponseCopyWithImpl;
@useResult
$Res call({
 String authorizationUrl
});




}
/// @nodoc
class _$StravaAuthUrlResponseCopyWithImpl<$Res>
    implements $StravaAuthUrlResponseCopyWith<$Res> {
  _$StravaAuthUrlResponseCopyWithImpl(this._self, this._then);

  final StravaAuthUrlResponse _self;
  final $Res Function(StravaAuthUrlResponse) _then;

/// Create a copy of StravaAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? authorizationUrl = null,}) {
  return _then(StravaAuthUrlResponse(
authorizationUrl: null == authorizationUrl ? _self.authorizationUrl : authorizationUrl // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [StravaAuthUrlResponse].
extension StravaAuthUrlResponsePatterns on StravaAuthUrlResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _StravaAuthUrlResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _StravaAuthUrlResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _StravaAuthUrlResponse value)  $default,){
final _that = this;
switch (_that) {
case _StravaAuthUrlResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _StravaAuthUrlResponse value)?  $default,){
final _that = this;
switch (_that) {
case _StravaAuthUrlResponse() when $default != null:
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
case _StravaAuthUrlResponse() when $default != null:
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
case _StravaAuthUrlResponse():
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
case _StravaAuthUrlResponse() when $default != null:
return $default(_that.authorizationUrl);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _StravaAuthUrlResponse implements StravaAuthUrlResponse {
  const _StravaAuthUrlResponse({required this.authorizationUrl});
  factory _StravaAuthUrlResponse.fromJson(Map<String, dynamic> json) => _$StravaAuthUrlResponseFromJson(json);

/// URL to redirect the user to for Strava authorization
@override final  String authorizationUrl;

/// Create a copy of StravaAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$StravaAuthUrlResponseCopyWith<_StravaAuthUrlResponse> get copyWith => __$StravaAuthUrlResponseCopyWithImpl<_StravaAuthUrlResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$StravaAuthUrlResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _StravaAuthUrlResponse&&(identical(other.authorizationUrl, authorizationUrl) || other.authorizationUrl == authorizationUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,authorizationUrl);
}

@override
String toString() {
    return 'StravaAuthUrlResponse(authorizationUrl: $authorizationUrl)';
}


}

/// @nodoc
abstract mixin class _$StravaAuthUrlResponseCopyWith<$Res> implements $StravaAuthUrlResponseCopyWith<$Res> {
  factory _$StravaAuthUrlResponseCopyWith(_StravaAuthUrlResponse value, $Res Function(_StravaAuthUrlResponse) _then) = __$StravaAuthUrlResponseCopyWithImpl;
@override @useResult
$Res call({
 String authorizationUrl
});




}
/// @nodoc
class __$StravaAuthUrlResponseCopyWithImpl<$Res>
    implements _$StravaAuthUrlResponseCopyWith<$Res> {
  __$StravaAuthUrlResponseCopyWithImpl(this._self, this._then);

  final _StravaAuthUrlResponse _self;
  final $Res Function(_StravaAuthUrlResponse) _then;

/// Create a copy of StravaAuthUrlResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? authorizationUrl = null,}) {
  return _then(_StravaAuthUrlResponse(
authorizationUrl: null == authorizationUrl ? _self.authorizationUrl : authorizationUrl // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
