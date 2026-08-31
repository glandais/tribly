// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'verify_token_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$VerifyTokenRequest {

/// Verification token
 String get token;
/// Create a copy of VerifyTokenRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$VerifyTokenRequestCopyWith<VerifyTokenRequest> get copyWith => _$VerifyTokenRequestCopyWithImpl<VerifyTokenRequest>(this as VerifyTokenRequest, _$identity);

  /// Serializes this VerifyTokenRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as VerifyTokenRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is VerifyTokenRequest&&(identical(other.token, _this.token) || other.token == _this.token));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as VerifyTokenRequest;
  return Object.hash(runtimeType,_this.token);
}

@override
String toString() {
  final _this = this as VerifyTokenRequest;
  return 'VerifyTokenRequest(token: ${_this.token})';
}


}

/// @nodoc
abstract mixin class $VerifyTokenRequestCopyWith<$Res>  {
  factory $VerifyTokenRequestCopyWith(VerifyTokenRequest value, $Res Function(VerifyTokenRequest) _then) = _$VerifyTokenRequestCopyWithImpl;
@useResult
$Res call({
 String token
});




}
/// @nodoc
class _$VerifyTokenRequestCopyWithImpl<$Res>
    implements $VerifyTokenRequestCopyWith<$Res> {
  _$VerifyTokenRequestCopyWithImpl(this._self, this._then);

  final VerifyTokenRequest _self;
  final $Res Function(VerifyTokenRequest) _then;

/// Create a copy of VerifyTokenRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? token = null,}) {
  return _then(VerifyTokenRequest(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [VerifyTokenRequest].
extension VerifyTokenRequestPatterns on VerifyTokenRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _VerifyTokenRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _VerifyTokenRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _VerifyTokenRequest value)  $default,){
final _that = this;
switch (_that) {
case _VerifyTokenRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _VerifyTokenRequest value)?  $default,){
final _that = this;
switch (_that) {
case _VerifyTokenRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String token)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _VerifyTokenRequest() when $default != null:
return $default(_that.token);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String token)  $default,) {final _that = this;
switch (_that) {
case _VerifyTokenRequest():
return $default(_that.token);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String token)?  $default,) {final _that = this;
switch (_that) {
case _VerifyTokenRequest() when $default != null:
return $default(_that.token);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _VerifyTokenRequest implements VerifyTokenRequest {
  const _VerifyTokenRequest({required this.token});
  factory _VerifyTokenRequest.fromJson(Map<String, dynamic> json) => _$VerifyTokenRequestFromJson(json);

/// Verification token
@override final  String token;

/// Create a copy of VerifyTokenRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$VerifyTokenRequestCopyWith<_VerifyTokenRequest> get copyWith => __$VerifyTokenRequestCopyWithImpl<_VerifyTokenRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$VerifyTokenRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _VerifyTokenRequest&&(identical(other.token, token) || other.token == token));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,token);
}

@override
String toString() {
    return 'VerifyTokenRequest(token: $token)';
}


}

/// @nodoc
abstract mixin class _$VerifyTokenRequestCopyWith<$Res> implements $VerifyTokenRequestCopyWith<$Res> {
  factory _$VerifyTokenRequestCopyWith(_VerifyTokenRequest value, $Res Function(_VerifyTokenRequest) _then) = __$VerifyTokenRequestCopyWithImpl;
@override @useResult
$Res call({
 String token
});




}
/// @nodoc
class __$VerifyTokenRequestCopyWithImpl<$Res>
    implements _$VerifyTokenRequestCopyWith<$Res> {
  __$VerifyTokenRequestCopyWithImpl(this._self, this._then);

  final _VerifyTokenRequest _self;
  final $Res Function(_VerifyTokenRequest) _then;

/// Create a copy of VerifyTokenRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? token = null,}) {
  return _then(_VerifyTokenRequest(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
