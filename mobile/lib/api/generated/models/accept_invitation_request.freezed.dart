// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'accept_invitation_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AcceptInvitationRequest {

/// The token from the invitation e-mail. Sent in the body rather than in the path, because a bearer secret in a URL path ends up in access logs and in the Referer of anything the page loads.
 String get token;
/// Create a copy of AcceptInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AcceptInvitationRequestCopyWith<AcceptInvitationRequest> get copyWith => _$AcceptInvitationRequestCopyWithImpl<AcceptInvitationRequest>(this as AcceptInvitationRequest, _$identity);

  /// Serializes this AcceptInvitationRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AcceptInvitationRequest&&(identical(other.token, token) || other.token == token));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,token);

@override
String toString() {
  return 'AcceptInvitationRequest(token: $token)';
}


}

/// @nodoc
abstract mixin class $AcceptInvitationRequestCopyWith<$Res>  {
  factory $AcceptInvitationRequestCopyWith(AcceptInvitationRequest value, $Res Function(AcceptInvitationRequest) _then) = _$AcceptInvitationRequestCopyWithImpl;
@useResult
$Res call({
 String token
});




}
/// @nodoc
class _$AcceptInvitationRequestCopyWithImpl<$Res>
    implements $AcceptInvitationRequestCopyWith<$Res> {
  _$AcceptInvitationRequestCopyWithImpl(this._self, this._then);

  final AcceptInvitationRequest _self;
  final $Res Function(AcceptInvitationRequest) _then;

/// Create a copy of AcceptInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? token = null,}) {
  return _then(AcceptInvitationRequest(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [AcceptInvitationRequest].
extension AcceptInvitationRequestPatterns on AcceptInvitationRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AcceptInvitationRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AcceptInvitationRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AcceptInvitationRequest value)  $default,){
final _that = this;
switch (_that) {
case _AcceptInvitationRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AcceptInvitationRequest value)?  $default,){
final _that = this;
switch (_that) {
case _AcceptInvitationRequest() when $default != null:
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
case _AcceptInvitationRequest() when $default != null:
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
case _AcceptInvitationRequest():
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
case _AcceptInvitationRequest() when $default != null:
return $default(_that.token);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AcceptInvitationRequest implements AcceptInvitationRequest {
  const _AcceptInvitationRequest({required this.token});
  factory _AcceptInvitationRequest.fromJson(Map<String, dynamic> json) => _$AcceptInvitationRequestFromJson(json);

/// The token from the invitation e-mail. Sent in the body rather than in the path, because a bearer secret in a URL path ends up in access logs and in the Referer of anything the page loads.
@override final  String token;

/// Create a copy of AcceptInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AcceptInvitationRequestCopyWith<_AcceptInvitationRequest> get copyWith => __$AcceptInvitationRequestCopyWithImpl<_AcceptInvitationRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AcceptInvitationRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AcceptInvitationRequest&&(identical(other.token, token) || other.token == token));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,token);

@override
String toString() {
  return 'AcceptInvitationRequest(token: $token)';
}


}

/// @nodoc
abstract mixin class _$AcceptInvitationRequestCopyWith<$Res> implements $AcceptInvitationRequestCopyWith<$Res> {
  factory _$AcceptInvitationRequestCopyWith(_AcceptInvitationRequest value, $Res Function(_AcceptInvitationRequest) _then) = __$AcceptInvitationRequestCopyWithImpl;
@override @useResult
$Res call({
 String token
});




}
/// @nodoc
class __$AcceptInvitationRequestCopyWithImpl<$Res>
    implements _$AcceptInvitationRequestCopyWith<$Res> {
  __$AcceptInvitationRequestCopyWithImpl(this._self, this._then);

  final _AcceptInvitationRequest _self;
  final $Res Function(_AcceptInvitationRequest) _then;

/// Create a copy of AcceptInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? token = null,}) {
  return _then(_AcceptInvitationRequest(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
