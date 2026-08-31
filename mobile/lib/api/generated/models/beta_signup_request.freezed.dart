// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'beta_signup_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BetaSignupRequest {

/// Email to contact once a beta is open
 String get email;
/// Create a copy of BetaSignupRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BetaSignupRequestCopyWith<BetaSignupRequest> get copyWith => _$BetaSignupRequestCopyWithImpl<BetaSignupRequest>(this as BetaSignupRequest, _$identity);

  /// Serializes this BetaSignupRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as BetaSignupRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BetaSignupRequest&&(identical(other.email, _this.email) || other.email == _this.email));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as BetaSignupRequest;
  return Object.hash(runtimeType,_this.email);
}

@override
String toString() {
  final _this = this as BetaSignupRequest;
  return 'BetaSignupRequest(email: ${_this.email})';
}


}

/// @nodoc
abstract mixin class $BetaSignupRequestCopyWith<$Res>  {
  factory $BetaSignupRequestCopyWith(BetaSignupRequest value, $Res Function(BetaSignupRequest) _then) = _$BetaSignupRequestCopyWithImpl;
@useResult
$Res call({
 String email
});




}
/// @nodoc
class _$BetaSignupRequestCopyWithImpl<$Res>
    implements $BetaSignupRequestCopyWith<$Res> {
  _$BetaSignupRequestCopyWithImpl(this._self, this._then);

  final BetaSignupRequest _self;
  final $Res Function(BetaSignupRequest) _then;

/// Create a copy of BetaSignupRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? email = null,}) {
  return _then(BetaSignupRequest(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [BetaSignupRequest].
extension BetaSignupRequestPatterns on BetaSignupRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BetaSignupRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BetaSignupRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BetaSignupRequest value)  $default,){
final _that = this;
switch (_that) {
case _BetaSignupRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BetaSignupRequest value)?  $default,){
final _that = this;
switch (_that) {
case _BetaSignupRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String email)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BetaSignupRequest() when $default != null:
return $default(_that.email);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String email)  $default,) {final _that = this;
switch (_that) {
case _BetaSignupRequest():
return $default(_that.email);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String email)?  $default,) {final _that = this;
switch (_that) {
case _BetaSignupRequest() when $default != null:
return $default(_that.email);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BetaSignupRequest implements BetaSignupRequest {
  const _BetaSignupRequest({required this.email});
  factory _BetaSignupRequest.fromJson(Map<String, dynamic> json) => _$BetaSignupRequestFromJson(json);

/// Email to contact once a beta is open
@override final  String email;

/// Create a copy of BetaSignupRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BetaSignupRequestCopyWith<_BetaSignupRequest> get copyWith => __$BetaSignupRequestCopyWithImpl<_BetaSignupRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BetaSignupRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _BetaSignupRequest&&(identical(other.email, email) || other.email == email));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,email);
}

@override
String toString() {
    return 'BetaSignupRequest(email: $email)';
}


}

/// @nodoc
abstract mixin class _$BetaSignupRequestCopyWith<$Res> implements $BetaSignupRequestCopyWith<$Res> {
  factory _$BetaSignupRequestCopyWith(_BetaSignupRequest value, $Res Function(_BetaSignupRequest) _then) = __$BetaSignupRequestCopyWithImpl;
@override @useResult
$Res call({
 String email
});




}
/// @nodoc
class __$BetaSignupRequestCopyWithImpl<$Res>
    implements _$BetaSignupRequestCopyWith<$Res> {
  __$BetaSignupRequestCopyWithImpl(this._self, this._then);

  final _BetaSignupRequest _self;
  final $Res Function(_BetaSignupRequest) _then;

/// Create a copy of BetaSignupRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? email = null,}) {
  return _then(_BetaSignupRequest(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
