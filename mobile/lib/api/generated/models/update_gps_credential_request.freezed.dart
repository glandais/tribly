// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'update_gps_credential_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$UpdateGpsCredentialRequest {

/// OAuth client ID
 String get clientId;/// OAuth client secret (null = keep current)
 String? get clientSecret;/// Whether credential is active
 bool? get active;
/// Create a copy of UpdateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$UpdateGpsCredentialRequestCopyWith<UpdateGpsCredentialRequest> get copyWith => _$UpdateGpsCredentialRequestCopyWithImpl<UpdateGpsCredentialRequest>(this as UpdateGpsCredentialRequest, _$identity);

  /// Serializes this UpdateGpsCredentialRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is UpdateGpsCredentialRequest&&(identical(other.clientId, clientId) || other.clientId == clientId)&&(identical(other.clientSecret, clientSecret) || other.clientSecret == clientSecret)&&(identical(other.active, active) || other.active == active));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,clientId,clientSecret,active);

@override
String toString() {
  return 'UpdateGpsCredentialRequest(clientId: $clientId, clientSecret: $clientSecret, active: $active)';
}


}

/// @nodoc
abstract mixin class $UpdateGpsCredentialRequestCopyWith<$Res>  {
  factory $UpdateGpsCredentialRequestCopyWith(UpdateGpsCredentialRequest value, $Res Function(UpdateGpsCredentialRequest) _then) = _$UpdateGpsCredentialRequestCopyWithImpl;
@useResult
$Res call({
 String clientId, String? clientSecret, bool? active
});




}
/// @nodoc
class _$UpdateGpsCredentialRequestCopyWithImpl<$Res>
    implements $UpdateGpsCredentialRequestCopyWith<$Res> {
  _$UpdateGpsCredentialRequestCopyWithImpl(this._self, this._then);

  final UpdateGpsCredentialRequest _self;
  final $Res Function(UpdateGpsCredentialRequest) _then;

/// Create a copy of UpdateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? clientId = null,Object? clientSecret = freezed,Object? active = freezed,}) {
  return _then(UpdateGpsCredentialRequest(
clientId: null == clientId ? _self.clientId : clientId // ignore: cast_nullable_to_non_nullable
as String,clientSecret: freezed == clientSecret ? _self.clientSecret : clientSecret // ignore: cast_nullable_to_non_nullable
as String?,active: freezed == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}

}


/// Adds pattern-matching-related methods to [UpdateGpsCredentialRequest].
extension UpdateGpsCredentialRequestPatterns on UpdateGpsCredentialRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _UpdateGpsCredentialRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _UpdateGpsCredentialRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _UpdateGpsCredentialRequest value)  $default,){
final _that = this;
switch (_that) {
case _UpdateGpsCredentialRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _UpdateGpsCredentialRequest value)?  $default,){
final _that = this;
switch (_that) {
case _UpdateGpsCredentialRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String clientId,  String? clientSecret,  bool? active)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _UpdateGpsCredentialRequest() when $default != null:
return $default(_that.clientId,_that.clientSecret,_that.active);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String clientId,  String? clientSecret,  bool? active)  $default,) {final _that = this;
switch (_that) {
case _UpdateGpsCredentialRequest():
return $default(_that.clientId,_that.clientSecret,_that.active);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String clientId,  String? clientSecret,  bool? active)?  $default,) {final _that = this;
switch (_that) {
case _UpdateGpsCredentialRequest() when $default != null:
return $default(_that.clientId,_that.clientSecret,_that.active);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _UpdateGpsCredentialRequest implements UpdateGpsCredentialRequest {
  const _UpdateGpsCredentialRequest({required this.clientId, this.clientSecret, this.active});
  factory _UpdateGpsCredentialRequest.fromJson(Map<String, dynamic> json) => _$UpdateGpsCredentialRequestFromJson(json);

/// OAuth client ID
@override final  String clientId;
/// OAuth client secret (null = keep current)
@override final  String? clientSecret;
/// Whether credential is active
@override final  bool? active;

/// Create a copy of UpdateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$UpdateGpsCredentialRequestCopyWith<_UpdateGpsCredentialRequest> get copyWith => __$UpdateGpsCredentialRequestCopyWithImpl<_UpdateGpsCredentialRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$UpdateGpsCredentialRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _UpdateGpsCredentialRequest&&(identical(other.clientId, clientId) || other.clientId == clientId)&&(identical(other.clientSecret, clientSecret) || other.clientSecret == clientSecret)&&(identical(other.active, active) || other.active == active));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,clientId,clientSecret,active);

@override
String toString() {
  return 'UpdateGpsCredentialRequest(clientId: $clientId, clientSecret: $clientSecret, active: $active)';
}


}

/// @nodoc
abstract mixin class _$UpdateGpsCredentialRequestCopyWith<$Res> implements $UpdateGpsCredentialRequestCopyWith<$Res> {
  factory _$UpdateGpsCredentialRequestCopyWith(_UpdateGpsCredentialRequest value, $Res Function(_UpdateGpsCredentialRequest) _then) = __$UpdateGpsCredentialRequestCopyWithImpl;
@override @useResult
$Res call({
 String clientId, String? clientSecret, bool? active
});




}
/// @nodoc
class __$UpdateGpsCredentialRequestCopyWithImpl<$Res>
    implements _$UpdateGpsCredentialRequestCopyWith<$Res> {
  __$UpdateGpsCredentialRequestCopyWithImpl(this._self, this._then);

  final _UpdateGpsCredentialRequest _self;
  final $Res Function(_UpdateGpsCredentialRequest) _then;

/// Create a copy of UpdateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? clientId = null,Object? clientSecret = freezed,Object? active = freezed,}) {
  return _then(_UpdateGpsCredentialRequest(
clientId: null == clientId ? _self.clientId : clientId // ignore: cast_nullable_to_non_nullable
as String,clientSecret: freezed == clientSecret ? _self.clientSecret : clientSecret // ignore: cast_nullable_to_non_nullable
as String?,active: freezed == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}


}

// dart format on
