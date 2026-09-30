// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'activate_account_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ActivateAccountRequest {

/// Verification token
 String get token;/// Password (min 8 chars)
 String get password;
/// Create a copy of ActivateAccountRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ActivateAccountRequestCopyWith<ActivateAccountRequest> get copyWith => _$ActivateAccountRequestCopyWithImpl<ActivateAccountRequest>(this as ActivateAccountRequest, _$identity);

  /// Serializes this ActivateAccountRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ActivateAccountRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ActivateAccountRequest&&(identical(other.token, _this.token) || other.token == _this.token)&&(identical(other.password, _this.password) || other.password == _this.password));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ActivateAccountRequest;
  return Object.hash(runtimeType,_this.token,_this.password);
}

@override
String toString() {
  final _this = this as ActivateAccountRequest;
  return 'ActivateAccountRequest(token: ${_this.token}, password: ${_this.password})';
}


}

/// @nodoc
abstract mixin class $ActivateAccountRequestCopyWith<$Res>  {
  factory $ActivateAccountRequestCopyWith(ActivateAccountRequest value, $Res Function(ActivateAccountRequest) _then) = _$ActivateAccountRequestCopyWithImpl;
@useResult
$Res call({
 String token, String password
});




}
/// @nodoc
class _$ActivateAccountRequestCopyWithImpl<$Res>
    implements $ActivateAccountRequestCopyWith<$Res> {
  _$ActivateAccountRequestCopyWithImpl(this._self, this._then);

  final ActivateAccountRequest _self;
  final $Res Function(ActivateAccountRequest) _then;

/// Create a copy of ActivateAccountRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? token = null,Object? password = null,}) {
  return _then(ActivateAccountRequest(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,password: null == password ? _self.password : password // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [ActivateAccountRequest].
extension ActivateAccountRequestPatterns on ActivateAccountRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ActivateAccountRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ActivateAccountRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ActivateAccountRequest value)  $default,){
final _that = this;
switch (_that) {
case _ActivateAccountRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ActivateAccountRequest value)?  $default,){
final _that = this;
switch (_that) {
case _ActivateAccountRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String token,  String password)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ActivateAccountRequest() when $default != null:
return $default(_that.token,_that.password);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String token,  String password)  $default,) {final _that = this;
switch (_that) {
case _ActivateAccountRequest():
return $default(_that.token,_that.password);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String token,  String password)?  $default,) {final _that = this;
switch (_that) {
case _ActivateAccountRequest() when $default != null:
return $default(_that.token,_that.password);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ActivateAccountRequest implements ActivateAccountRequest {
  const _ActivateAccountRequest({required this.token, required this.password});
  factory _ActivateAccountRequest.fromJson(Map<String, dynamic> json) => _$ActivateAccountRequestFromJson(json);

/// Verification token
@override final  String token;
/// Password (min 8 chars)
@override final  String password;

/// Create a copy of ActivateAccountRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ActivateAccountRequestCopyWith<_ActivateAccountRequest> get copyWith => __$ActivateAccountRequestCopyWithImpl<_ActivateAccountRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ActivateAccountRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ActivateAccountRequest&&(identical(other.token, token) || other.token == token)&&(identical(other.password, password) || other.password == password));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,token,password);
}

@override
String toString() {
    return 'ActivateAccountRequest(token: $token, password: $password)';
}


}

/// @nodoc
abstract mixin class _$ActivateAccountRequestCopyWith<$Res> implements $ActivateAccountRequestCopyWith<$Res> {
  factory _$ActivateAccountRequestCopyWith(_ActivateAccountRequest value, $Res Function(_ActivateAccountRequest) _then) = __$ActivateAccountRequestCopyWithImpl;
@override @useResult
$Res call({
 String token, String password
});




}
/// @nodoc
class __$ActivateAccountRequestCopyWithImpl<$Res>
    implements _$ActivateAccountRequestCopyWith<$Res> {
  __$ActivateAccountRequestCopyWithImpl(this._self, this._then);

  final _ActivateAccountRequest _self;
  final $Res Function(_ActivateAccountRequest) _then;

/// Create a copy of ActivateAccountRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? token = null,Object? password = null,}) {
  return _then(_ActivateAccountRequest(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,password: null == password ? _self.password : password // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
