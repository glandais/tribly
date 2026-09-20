// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'push_device_registration.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PushDeviceRegistration {

/// The FCM registration token. Registering a token already known moves it to the current user and refreshes its last-seen date.
 String get token;/// The device's platform
 String get platform;/// A human-readable device name, for the member's own device list
 String? get deviceName;/// The app version that registered, for support
 String? get appVersion;
/// Create a copy of PushDeviceRegistration
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PushDeviceRegistrationCopyWith<PushDeviceRegistration> get copyWith => _$PushDeviceRegistrationCopyWithImpl<PushDeviceRegistration>(this as PushDeviceRegistration, _$identity);

  /// Serializes this PushDeviceRegistration to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as PushDeviceRegistration;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PushDeviceRegistration&&(identical(other.token, _this.token) || other.token == _this.token)&&(identical(other.platform, _this.platform) || other.platform == _this.platform)&&(identical(other.deviceName, _this.deviceName) || other.deviceName == _this.deviceName)&&(identical(other.appVersion, _this.appVersion) || other.appVersion == _this.appVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as PushDeviceRegistration;
  return Object.hash(runtimeType,_this.token,_this.platform,_this.deviceName,_this.appVersion);
}

@override
String toString() {
  final _this = this as PushDeviceRegistration;
  return 'PushDeviceRegistration(token: ${_this.token}, platform: ${_this.platform}, deviceName: ${_this.deviceName}, appVersion: ${_this.appVersion})';
}


}

/// @nodoc
abstract mixin class $PushDeviceRegistrationCopyWith<$Res>  {
  factory $PushDeviceRegistrationCopyWith(PushDeviceRegistration value, $Res Function(PushDeviceRegistration) _then) = _$PushDeviceRegistrationCopyWithImpl;
@useResult
$Res call({
 String token, String platform, String? deviceName, String? appVersion
});




}
/// @nodoc
class _$PushDeviceRegistrationCopyWithImpl<$Res>
    implements $PushDeviceRegistrationCopyWith<$Res> {
  _$PushDeviceRegistrationCopyWithImpl(this._self, this._then);

  final PushDeviceRegistration _self;
  final $Res Function(PushDeviceRegistration) _then;

/// Create a copy of PushDeviceRegistration
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? token = null,Object? platform = null,Object? deviceName = freezed,Object? appVersion = freezed,}) {
  return _then(PushDeviceRegistration(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,platform: null == platform ? _self.platform : platform // ignore: cast_nullable_to_non_nullable
as String,deviceName: freezed == deviceName ? _self.deviceName : deviceName // ignore: cast_nullable_to_non_nullable
as String?,appVersion: freezed == appVersion ? _self.appVersion : appVersion // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [PushDeviceRegistration].
extension PushDeviceRegistrationPatterns on PushDeviceRegistration {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PushDeviceRegistration value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PushDeviceRegistration() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PushDeviceRegistration value)  $default,){
final _that = this;
switch (_that) {
case _PushDeviceRegistration():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PushDeviceRegistration value)?  $default,){
final _that = this;
switch (_that) {
case _PushDeviceRegistration() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String token,  String platform,  String? deviceName,  String? appVersion)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PushDeviceRegistration() when $default != null:
return $default(_that.token,_that.platform,_that.deviceName,_that.appVersion);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String token,  String platform,  String? deviceName,  String? appVersion)  $default,) {final _that = this;
switch (_that) {
case _PushDeviceRegistration():
return $default(_that.token,_that.platform,_that.deviceName,_that.appVersion);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String token,  String platform,  String? deviceName,  String? appVersion)?  $default,) {final _that = this;
switch (_that) {
case _PushDeviceRegistration() when $default != null:
return $default(_that.token,_that.platform,_that.deviceName,_that.appVersion);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PushDeviceRegistration implements PushDeviceRegistration {
  const _PushDeviceRegistration({required this.token, required this.platform, this.deviceName, this.appVersion});
  factory _PushDeviceRegistration.fromJson(Map<String, dynamic> json) => _$PushDeviceRegistrationFromJson(json);

/// The FCM registration token. Registering a token already known moves it to the current user and refreshes its last-seen date.
@override final  String token;
/// The device's platform
@override final  String platform;
/// A human-readable device name, for the member's own device list
@override final  String? deviceName;
/// The app version that registered, for support
@override final  String? appVersion;

/// Create a copy of PushDeviceRegistration
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PushDeviceRegistrationCopyWith<_PushDeviceRegistration> get copyWith => __$PushDeviceRegistrationCopyWithImpl<_PushDeviceRegistration>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PushDeviceRegistrationToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _PushDeviceRegistration&&(identical(other.token, token) || other.token == token)&&(identical(other.platform, platform) || other.platform == platform)&&(identical(other.deviceName, deviceName) || other.deviceName == deviceName)&&(identical(other.appVersion, appVersion) || other.appVersion == appVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,token,platform,deviceName,appVersion);
}

@override
String toString() {
    return 'PushDeviceRegistration(token: $token, platform: $platform, deviceName: $deviceName, appVersion: $appVersion)';
}


}

/// @nodoc
abstract mixin class _$PushDeviceRegistrationCopyWith<$Res> implements $PushDeviceRegistrationCopyWith<$Res> {
  factory _$PushDeviceRegistrationCopyWith(_PushDeviceRegistration value, $Res Function(_PushDeviceRegistration) _then) = __$PushDeviceRegistrationCopyWithImpl;
@override @useResult
$Res call({
 String token, String platform, String? deviceName, String? appVersion
});




}
/// @nodoc
class __$PushDeviceRegistrationCopyWithImpl<$Res>
    implements _$PushDeviceRegistrationCopyWith<$Res> {
  __$PushDeviceRegistrationCopyWithImpl(this._self, this._then);

  final _PushDeviceRegistration _self;
  final $Res Function(_PushDeviceRegistration) _then;

/// Create a copy of PushDeviceRegistration
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? token = null,Object? platform = null,Object? deviceName = freezed,Object? appVersion = freezed,}) {
  return _then(_PushDeviceRegistration(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,platform: null == platform ? _self.platform : platform // ignore: cast_nullable_to_non_nullable
as String,deviceName: freezed == deviceName ? _self.deviceName : deviceName // ignore: cast_nullable_to_non_nullable
as String?,appVersion: freezed == appVersion ? _self.appVersion : appVersion // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
