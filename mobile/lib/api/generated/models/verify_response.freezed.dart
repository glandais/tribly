// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'verify_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$VerifyResponse {

/// User code
 String get userCode;/// Whether authorization is already completed
 bool? get authorized;
/// Create a copy of VerifyResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$VerifyResponseCopyWith<VerifyResponse> get copyWith => _$VerifyResponseCopyWithImpl<VerifyResponse>(this as VerifyResponse, _$identity);

  /// Serializes this VerifyResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is VerifyResponse&&(identical(other.userCode, userCode) || other.userCode == userCode)&&(identical(other.authorized, authorized) || other.authorized == authorized));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,userCode,authorized);

@override
String toString() {
  return 'VerifyResponse(userCode: $userCode, authorized: $authorized)';
}


}

/// @nodoc
abstract mixin class $VerifyResponseCopyWith<$Res>  {
  factory $VerifyResponseCopyWith(VerifyResponse value, $Res Function(VerifyResponse) _then) = _$VerifyResponseCopyWithImpl;
@useResult
$Res call({
 String userCode, bool? authorized
});




}
/// @nodoc
class _$VerifyResponseCopyWithImpl<$Res>
    implements $VerifyResponseCopyWith<$Res> {
  _$VerifyResponseCopyWithImpl(this._self, this._then);

  final VerifyResponse _self;
  final $Res Function(VerifyResponse) _then;

/// Create a copy of VerifyResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? userCode = null,Object? authorized = freezed,}) {
  return _then(_self.copyWith(
userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,authorized: freezed == authorized ? _self.authorized : authorized // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}

}


/// Adds pattern-matching-related methods to [VerifyResponse].
extension VerifyResponsePatterns on VerifyResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _VerifyResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _VerifyResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _VerifyResponse value)  $default,){
final _that = this;
switch (_that) {
case _VerifyResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _VerifyResponse value)?  $default,){
final _that = this;
switch (_that) {
case _VerifyResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String userCode,  bool? authorized)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _VerifyResponse() when $default != null:
return $default(_that.userCode,_that.authorized);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String userCode,  bool? authorized)  $default,) {final _that = this;
switch (_that) {
case _VerifyResponse():
return $default(_that.userCode,_that.authorized);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String userCode,  bool? authorized)?  $default,) {final _that = this;
switch (_that) {
case _VerifyResponse() when $default != null:
return $default(_that.userCode,_that.authorized);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _VerifyResponse implements VerifyResponse {
  const _VerifyResponse({required this.userCode, this.authorized});
  factory _VerifyResponse.fromJson(Map<String, dynamic> json) => _$VerifyResponseFromJson(json);

/// User code
@override final  String userCode;
/// Whether authorization is already completed
@override final  bool? authorized;

/// Create a copy of VerifyResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$VerifyResponseCopyWith<_VerifyResponse> get copyWith => __$VerifyResponseCopyWithImpl<_VerifyResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$VerifyResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _VerifyResponse&&(identical(other.userCode, userCode) || other.userCode == userCode)&&(identical(other.authorized, authorized) || other.authorized == authorized));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,userCode,authorized);

@override
String toString() {
  return 'VerifyResponse(userCode: $userCode, authorized: $authorized)';
}


}

/// @nodoc
abstract mixin class _$VerifyResponseCopyWith<$Res> implements $VerifyResponseCopyWith<$Res> {
  factory _$VerifyResponseCopyWith(_VerifyResponse value, $Res Function(_VerifyResponse) _then) = __$VerifyResponseCopyWithImpl;
@override @useResult
$Res call({
 String userCode, bool? authorized
});




}
/// @nodoc
class __$VerifyResponseCopyWithImpl<$Res>
    implements _$VerifyResponseCopyWith<$Res> {
  __$VerifyResponseCopyWithImpl(this._self, this._then);

  final _VerifyResponse _self;
  final $Res Function(_VerifyResponse) _then;

/// Create a copy of VerifyResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? userCode = null,Object? authorized = freezed,}) {
  return _then(_VerifyResponse(
userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,authorized: freezed == authorized ? _self.authorized : authorized // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}


}

// dart format on
