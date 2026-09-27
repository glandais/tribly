// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'client_context_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ClientContextDto {

/// The client
 String get platform;/// Version of the client, e.g. 1.0.0 or a git commit
 String get appVersion;/// Build number of a mobile client
 String? get buildNumber;/// OS name and version, e.g. Android 15
 String? get osVersion;/// Device model
 String? get device;/// Browser user agent
 String? get userAgent;/// Path of the current page or screen, without its query string
 String? get route;/// UI language, e.g. fr
 String? get locale;/// IANA time zone, e.g. Europe/Paris
 String? get timezone;/// Slug of the team being browsed, if any
 String? get teamSlug;
/// Create a copy of ClientContextDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ClientContextDtoCopyWith<ClientContextDto> get copyWith => _$ClientContextDtoCopyWithImpl<ClientContextDto>(this as ClientContextDto, _$identity);

  /// Serializes this ClientContextDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ClientContextDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ClientContextDto&&(identical(other.platform, _this.platform) || other.platform == _this.platform)&&(identical(other.appVersion, _this.appVersion) || other.appVersion == _this.appVersion)&&(identical(other.buildNumber, _this.buildNumber) || other.buildNumber == _this.buildNumber)&&(identical(other.osVersion, _this.osVersion) || other.osVersion == _this.osVersion)&&(identical(other.device, _this.device) || other.device == _this.device)&&(identical(other.userAgent, _this.userAgent) || other.userAgent == _this.userAgent)&&(identical(other.route, _this.route) || other.route == _this.route)&&(identical(other.locale, _this.locale) || other.locale == _this.locale)&&(identical(other.timezone, _this.timezone) || other.timezone == _this.timezone)&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ClientContextDto;
  return Object.hash(runtimeType,_this.platform,_this.appVersion,_this.buildNumber,_this.osVersion,_this.device,_this.userAgent,_this.route,_this.locale,_this.timezone,_this.teamSlug);
}

@override
String toString() {
  final _this = this as ClientContextDto;
  return 'ClientContextDto(platform: ${_this.platform}, appVersion: ${_this.appVersion}, buildNumber: ${_this.buildNumber}, osVersion: ${_this.osVersion}, device: ${_this.device}, userAgent: ${_this.userAgent}, route: ${_this.route}, locale: ${_this.locale}, timezone: ${_this.timezone}, teamSlug: ${_this.teamSlug})';
}


}

/// @nodoc
abstract mixin class $ClientContextDtoCopyWith<$Res>  {
  factory $ClientContextDtoCopyWith(ClientContextDto value, $Res Function(ClientContextDto) _then) = _$ClientContextDtoCopyWithImpl;
@useResult
$Res call({
 String platform, String appVersion, String? buildNumber, String? osVersion, String? device, String? userAgent, String? route, String? locale, String? timezone, String? teamSlug
});




}
/// @nodoc
class _$ClientContextDtoCopyWithImpl<$Res>
    implements $ClientContextDtoCopyWith<$Res> {
  _$ClientContextDtoCopyWithImpl(this._self, this._then);

  final ClientContextDto _self;
  final $Res Function(ClientContextDto) _then;

/// Create a copy of ClientContextDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? platform = null,Object? appVersion = null,Object? buildNumber = freezed,Object? osVersion = freezed,Object? device = freezed,Object? userAgent = freezed,Object? route = freezed,Object? locale = freezed,Object? timezone = freezed,Object? teamSlug = freezed,}) {
  return _then(ClientContextDto(
platform: null == platform ? _self.platform : platform // ignore: cast_nullable_to_non_nullable
as String,appVersion: null == appVersion ? _self.appVersion : appVersion // ignore: cast_nullable_to_non_nullable
as String,buildNumber: freezed == buildNumber ? _self.buildNumber : buildNumber // ignore: cast_nullable_to_non_nullable
as String?,osVersion: freezed == osVersion ? _self.osVersion : osVersion // ignore: cast_nullable_to_non_nullable
as String?,device: freezed == device ? _self.device : device // ignore: cast_nullable_to_non_nullable
as String?,userAgent: freezed == userAgent ? _self.userAgent : userAgent // ignore: cast_nullable_to_non_nullable
as String?,route: freezed == route ? _self.route : route // ignore: cast_nullable_to_non_nullable
as String?,locale: freezed == locale ? _self.locale : locale // ignore: cast_nullable_to_non_nullable
as String?,timezone: freezed == timezone ? _self.timezone : timezone // ignore: cast_nullable_to_non_nullable
as String?,teamSlug: freezed == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [ClientContextDto].
extension ClientContextDtoPatterns on ClientContextDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ClientContextDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ClientContextDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ClientContextDto value)  $default,){
final _that = this;
switch (_that) {
case _ClientContextDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ClientContextDto value)?  $default,){
final _that = this;
switch (_that) {
case _ClientContextDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String platform,  String appVersion,  String? buildNumber,  String? osVersion,  String? device,  String? userAgent,  String? route,  String? locale,  String? timezone,  String? teamSlug)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ClientContextDto() when $default != null:
return $default(_that.platform,_that.appVersion,_that.buildNumber,_that.osVersion,_that.device,_that.userAgent,_that.route,_that.locale,_that.timezone,_that.teamSlug);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String platform,  String appVersion,  String? buildNumber,  String? osVersion,  String? device,  String? userAgent,  String? route,  String? locale,  String? timezone,  String? teamSlug)  $default,) {final _that = this;
switch (_that) {
case _ClientContextDto():
return $default(_that.platform,_that.appVersion,_that.buildNumber,_that.osVersion,_that.device,_that.userAgent,_that.route,_that.locale,_that.timezone,_that.teamSlug);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String platform,  String appVersion,  String? buildNumber,  String? osVersion,  String? device,  String? userAgent,  String? route,  String? locale,  String? timezone,  String? teamSlug)?  $default,) {final _that = this;
switch (_that) {
case _ClientContextDto() when $default != null:
return $default(_that.platform,_that.appVersion,_that.buildNumber,_that.osVersion,_that.device,_that.userAgent,_that.route,_that.locale,_that.timezone,_that.teamSlug);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ClientContextDto implements ClientContextDto {
  const _ClientContextDto({required this.platform, required this.appVersion, this.buildNumber, this.osVersion, this.device, this.userAgent, this.route, this.locale, this.timezone, this.teamSlug});
  factory _ClientContextDto.fromJson(Map<String, dynamic> json) => _$ClientContextDtoFromJson(json);

/// The client
@override final  String platform;
/// Version of the client, e.g. 1.0.0 or a git commit
@override final  String appVersion;
/// Build number of a mobile client
@override final  String? buildNumber;
/// OS name and version, e.g. Android 15
@override final  String? osVersion;
/// Device model
@override final  String? device;
/// Browser user agent
@override final  String? userAgent;
/// Path of the current page or screen, without its query string
@override final  String? route;
/// UI language, e.g. fr
@override final  String? locale;
/// IANA time zone, e.g. Europe/Paris
@override final  String? timezone;
/// Slug of the team being browsed, if any
@override final  String? teamSlug;

/// Create a copy of ClientContextDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ClientContextDtoCopyWith<_ClientContextDto> get copyWith => __$ClientContextDtoCopyWithImpl<_ClientContextDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ClientContextDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ClientContextDto&&(identical(other.platform, platform) || other.platform == platform)&&(identical(other.appVersion, appVersion) || other.appVersion == appVersion)&&(identical(other.buildNumber, buildNumber) || other.buildNumber == buildNumber)&&(identical(other.osVersion, osVersion) || other.osVersion == osVersion)&&(identical(other.device, device) || other.device == device)&&(identical(other.userAgent, userAgent) || other.userAgent == userAgent)&&(identical(other.route, route) || other.route == route)&&(identical(other.locale, locale) || other.locale == locale)&&(identical(other.timezone, timezone) || other.timezone == timezone)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,platform,appVersion,buildNumber,osVersion,device,userAgent,route,locale,timezone,teamSlug);
}

@override
String toString() {
    return 'ClientContextDto(platform: $platform, appVersion: $appVersion, buildNumber: $buildNumber, osVersion: $osVersion, device: $device, userAgent: $userAgent, route: $route, locale: $locale, timezone: $timezone, teamSlug: $teamSlug)';
}


}

/// @nodoc
abstract mixin class _$ClientContextDtoCopyWith<$Res> implements $ClientContextDtoCopyWith<$Res> {
  factory _$ClientContextDtoCopyWith(_ClientContextDto value, $Res Function(_ClientContextDto) _then) = __$ClientContextDtoCopyWithImpl;
@override @useResult
$Res call({
 String platform, String appVersion, String? buildNumber, String? osVersion, String? device, String? userAgent, String? route, String? locale, String? timezone, String? teamSlug
});




}
/// @nodoc
class __$ClientContextDtoCopyWithImpl<$Res>
    implements _$ClientContextDtoCopyWith<$Res> {
  __$ClientContextDtoCopyWithImpl(this._self, this._then);

  final _ClientContextDto _self;
  final $Res Function(_ClientContextDto) _then;

/// Create a copy of ClientContextDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? platform = null,Object? appVersion = null,Object? buildNumber = freezed,Object? osVersion = freezed,Object? device = freezed,Object? userAgent = freezed,Object? route = freezed,Object? locale = freezed,Object? timezone = freezed,Object? teamSlug = freezed,}) {
  return _then(_ClientContextDto(
platform: null == platform ? _self.platform : platform // ignore: cast_nullable_to_non_nullable
as String,appVersion: null == appVersion ? _self.appVersion : appVersion // ignore: cast_nullable_to_non_nullable
as String,buildNumber: freezed == buildNumber ? _self.buildNumber : buildNumber // ignore: cast_nullable_to_non_nullable
as String?,osVersion: freezed == osVersion ? _self.osVersion : osVersion // ignore: cast_nullable_to_non_nullable
as String?,device: freezed == device ? _self.device : device // ignore: cast_nullable_to_non_nullable
as String?,userAgent: freezed == userAgent ? _self.userAgent : userAgent // ignore: cast_nullable_to_non_nullable
as String?,route: freezed == route ? _self.route : route // ignore: cast_nullable_to_non_nullable
as String?,locale: freezed == locale ? _self.locale : locale // ignore: cast_nullable_to_non_nullable
as String?,timezone: freezed == timezone ? _self.timezone : timezone // ignore: cast_nullable_to_non_nullable
as String?,teamSlug: freezed == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
