// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'create_gps_credential_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CreateGpsCredentialRequest {

/// GPS service type
 String get serviceType;/// OAuth client ID
 String get clientId;/// OAuth client secret, or consumer secret for OAuth 1.0a (required then)
 String? get clientSecret;/// Whether credential is active
 bool? get active;/// OAuth protocol of the client ID and secret (null = OAUTH2). OAUTH1 is accepted for GARMIN only; the client ID is then the consumer key
 String? get oauthVersion;
/// Create a copy of CreateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CreateGpsCredentialRequestCopyWith<CreateGpsCredentialRequest> get copyWith => _$CreateGpsCredentialRequestCopyWithImpl<CreateGpsCredentialRequest>(this as CreateGpsCredentialRequest, _$identity);

  /// Serializes this CreateGpsCredentialRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as CreateGpsCredentialRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CreateGpsCredentialRequest&&(identical(other.serviceType, _this.serviceType) || other.serviceType == _this.serviceType)&&(identical(other.clientId, _this.clientId) || other.clientId == _this.clientId)&&(identical(other.clientSecret, _this.clientSecret) || other.clientSecret == _this.clientSecret)&&(identical(other.active, _this.active) || other.active == _this.active)&&(identical(other.oauthVersion, _this.oauthVersion) || other.oauthVersion == _this.oauthVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as CreateGpsCredentialRequest;
  return Object.hash(runtimeType,_this.serviceType,_this.clientId,_this.clientSecret,_this.active,_this.oauthVersion);
}

@override
String toString() {
  final _this = this as CreateGpsCredentialRequest;
  return 'CreateGpsCredentialRequest(serviceType: ${_this.serviceType}, clientId: ${_this.clientId}, clientSecret: ${_this.clientSecret}, active: ${_this.active}, oauthVersion: ${_this.oauthVersion})';
}


}

/// @nodoc
abstract mixin class $CreateGpsCredentialRequestCopyWith<$Res>  {
  factory $CreateGpsCredentialRequestCopyWith(CreateGpsCredentialRequest value, $Res Function(CreateGpsCredentialRequest) _then) = _$CreateGpsCredentialRequestCopyWithImpl;
@useResult
$Res call({
 String serviceType, String clientId, String? clientSecret, bool? active, String? oauthVersion
});




}
/// @nodoc
class _$CreateGpsCredentialRequestCopyWithImpl<$Res>
    implements $CreateGpsCredentialRequestCopyWith<$Res> {
  _$CreateGpsCredentialRequestCopyWithImpl(this._self, this._then);

  final CreateGpsCredentialRequest _self;
  final $Res Function(CreateGpsCredentialRequest) _then;

/// Create a copy of CreateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? serviceType = null,Object? clientId = null,Object? clientSecret = freezed,Object? active = freezed,Object? oauthVersion = freezed,}) {
  return _then(CreateGpsCredentialRequest(
serviceType: null == serviceType ? _self.serviceType : serviceType // ignore: cast_nullable_to_non_nullable
as String,clientId: null == clientId ? _self.clientId : clientId // ignore: cast_nullable_to_non_nullable
as String,clientSecret: freezed == clientSecret ? _self.clientSecret : clientSecret // ignore: cast_nullable_to_non_nullable
as String?,active: freezed == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool?,oauthVersion: freezed == oauthVersion ? _self.oauthVersion : oauthVersion // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [CreateGpsCredentialRequest].
extension CreateGpsCredentialRequestPatterns on CreateGpsCredentialRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CreateGpsCredentialRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CreateGpsCredentialRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CreateGpsCredentialRequest value)  $default,){
final _that = this;
switch (_that) {
case _CreateGpsCredentialRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CreateGpsCredentialRequest value)?  $default,){
final _that = this;
switch (_that) {
case _CreateGpsCredentialRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String serviceType,  String clientId,  String? clientSecret,  bool? active,  String? oauthVersion)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CreateGpsCredentialRequest() when $default != null:
return $default(_that.serviceType,_that.clientId,_that.clientSecret,_that.active,_that.oauthVersion);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String serviceType,  String clientId,  String? clientSecret,  bool? active,  String? oauthVersion)  $default,) {final _that = this;
switch (_that) {
case _CreateGpsCredentialRequest():
return $default(_that.serviceType,_that.clientId,_that.clientSecret,_that.active,_that.oauthVersion);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String serviceType,  String clientId,  String? clientSecret,  bool? active,  String? oauthVersion)?  $default,) {final _that = this;
switch (_that) {
case _CreateGpsCredentialRequest() when $default != null:
return $default(_that.serviceType,_that.clientId,_that.clientSecret,_that.active,_that.oauthVersion);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CreateGpsCredentialRequest implements CreateGpsCredentialRequest {
  const _CreateGpsCredentialRequest({required this.serviceType, required this.clientId, this.clientSecret, this.active, this.oauthVersion});
  factory _CreateGpsCredentialRequest.fromJson(Map<String, dynamic> json) => _$CreateGpsCredentialRequestFromJson(json);

/// GPS service type
@override final  String serviceType;
/// OAuth client ID
@override final  String clientId;
/// OAuth client secret, or consumer secret for OAuth 1.0a (required then)
@override final  String? clientSecret;
/// Whether credential is active
@override final  bool? active;
/// OAuth protocol of the client ID and secret (null = OAUTH2). OAUTH1 is accepted for GARMIN only; the client ID is then the consumer key
@override final  String? oauthVersion;

/// Create a copy of CreateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CreateGpsCredentialRequestCopyWith<_CreateGpsCredentialRequest> get copyWith => __$CreateGpsCredentialRequestCopyWithImpl<_CreateGpsCredentialRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CreateGpsCredentialRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _CreateGpsCredentialRequest&&(identical(other.serviceType, serviceType) || other.serviceType == serviceType)&&(identical(other.clientId, clientId) || other.clientId == clientId)&&(identical(other.clientSecret, clientSecret) || other.clientSecret == clientSecret)&&(identical(other.active, active) || other.active == active)&&(identical(other.oauthVersion, oauthVersion) || other.oauthVersion == oauthVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,serviceType,clientId,clientSecret,active,oauthVersion);
}

@override
String toString() {
    return 'CreateGpsCredentialRequest(serviceType: $serviceType, clientId: $clientId, clientSecret: $clientSecret, active: $active, oauthVersion: $oauthVersion)';
}


}

/// @nodoc
abstract mixin class _$CreateGpsCredentialRequestCopyWith<$Res> implements $CreateGpsCredentialRequestCopyWith<$Res> {
  factory _$CreateGpsCredentialRequestCopyWith(_CreateGpsCredentialRequest value, $Res Function(_CreateGpsCredentialRequest) _then) = __$CreateGpsCredentialRequestCopyWithImpl;
@override @useResult
$Res call({
 String serviceType, String clientId, String? clientSecret, bool? active, String? oauthVersion
});




}
/// @nodoc
class __$CreateGpsCredentialRequestCopyWithImpl<$Res>
    implements _$CreateGpsCredentialRequestCopyWith<$Res> {
  __$CreateGpsCredentialRequestCopyWithImpl(this._self, this._then);

  final _CreateGpsCredentialRequest _self;
  final $Res Function(_CreateGpsCredentialRequest) _then;

/// Create a copy of CreateGpsCredentialRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? serviceType = null,Object? clientId = null,Object? clientSecret = freezed,Object? active = freezed,Object? oauthVersion = freezed,}) {
  return _then(_CreateGpsCredentialRequest(
serviceType: null == serviceType ? _self.serviceType : serviceType // ignore: cast_nullable_to_non_nullable
as String,clientId: null == clientId ? _self.clientId : clientId // ignore: cast_nullable_to_non_nullable
as String,clientSecret: freezed == clientSecret ? _self.clientSecret : clientSecret // ignore: cast_nullable_to_non_nullable
as String?,active: freezed == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool?,oauthVersion: freezed == oauthVersion ? _self.oauthVersion : oauthVersion // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
