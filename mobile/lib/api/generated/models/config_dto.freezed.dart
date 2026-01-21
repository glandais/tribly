// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'config_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ConfigDto {

/// WebAuthn Relying Party ID (domain)
 String get webAuthnRpId;/// Application name
 String get appName;/// Single team mode - team creation disabled
 bool get singleTeam;
/// Create a copy of ConfigDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ConfigDtoCopyWith<ConfigDto> get copyWith => _$ConfigDtoCopyWithImpl<ConfigDto>(this as ConfigDto, _$identity);

  /// Serializes this ConfigDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ConfigDto&&(identical(other.webAuthnRpId, webAuthnRpId) || other.webAuthnRpId == webAuthnRpId)&&(identical(other.appName, appName) || other.appName == appName)&&(identical(other.singleTeam, singleTeam) || other.singleTeam == singleTeam));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,webAuthnRpId,appName,singleTeam);

@override
String toString() {
  return 'ConfigDto(webAuthnRpId: $webAuthnRpId, appName: $appName, singleTeam: $singleTeam)';
}


}

/// @nodoc
abstract mixin class $ConfigDtoCopyWith<$Res>  {
  factory $ConfigDtoCopyWith(ConfigDto value, $Res Function(ConfigDto) _then) = _$ConfigDtoCopyWithImpl;
@useResult
$Res call({
 String webAuthnRpId, String appName, bool singleTeam
});




}
/// @nodoc
class _$ConfigDtoCopyWithImpl<$Res>
    implements $ConfigDtoCopyWith<$Res> {
  _$ConfigDtoCopyWithImpl(this._self, this._then);

  final ConfigDto _self;
  final $Res Function(ConfigDto) _then;

/// Create a copy of ConfigDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? webAuthnRpId = null,Object? appName = null,Object? singleTeam = null,}) {
  return _then(_self.copyWith(
webAuthnRpId: null == webAuthnRpId ? _self.webAuthnRpId : webAuthnRpId // ignore: cast_nullable_to_non_nullable
as String,appName: null == appName ? _self.appName : appName // ignore: cast_nullable_to_non_nullable
as String,singleTeam: null == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [ConfigDto].
extension ConfigDtoPatterns on ConfigDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ConfigDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ConfigDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ConfigDto value)  $default,){
final _that = this;
switch (_that) {
case _ConfigDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ConfigDto value)?  $default,){
final _that = this;
switch (_that) {
case _ConfigDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String webAuthnRpId,  String appName,  bool singleTeam)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ConfigDto() when $default != null:
return $default(_that.webAuthnRpId,_that.appName,_that.singleTeam);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String webAuthnRpId,  String appName,  bool singleTeam)  $default,) {final _that = this;
switch (_that) {
case _ConfigDto():
return $default(_that.webAuthnRpId,_that.appName,_that.singleTeam);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String webAuthnRpId,  String appName,  bool singleTeam)?  $default,) {final _that = this;
switch (_that) {
case _ConfigDto() when $default != null:
return $default(_that.webAuthnRpId,_that.appName,_that.singleTeam);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ConfigDto implements ConfigDto {
  const _ConfigDto({required this.webAuthnRpId, required this.appName, required this.singleTeam});
  factory _ConfigDto.fromJson(Map<String, dynamic> json) => _$ConfigDtoFromJson(json);

/// WebAuthn Relying Party ID (domain)
@override final  String webAuthnRpId;
/// Application name
@override final  String appName;
/// Single team mode - team creation disabled
@override final  bool singleTeam;

/// Create a copy of ConfigDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ConfigDtoCopyWith<_ConfigDto> get copyWith => __$ConfigDtoCopyWithImpl<_ConfigDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ConfigDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ConfigDto&&(identical(other.webAuthnRpId, webAuthnRpId) || other.webAuthnRpId == webAuthnRpId)&&(identical(other.appName, appName) || other.appName == appName)&&(identical(other.singleTeam, singleTeam) || other.singleTeam == singleTeam));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,webAuthnRpId,appName,singleTeam);

@override
String toString() {
  return 'ConfigDto(webAuthnRpId: $webAuthnRpId, appName: $appName, singleTeam: $singleTeam)';
}


}

/// @nodoc
abstract mixin class _$ConfigDtoCopyWith<$Res> implements $ConfigDtoCopyWith<$Res> {
  factory _$ConfigDtoCopyWith(_ConfigDto value, $Res Function(_ConfigDto) _then) = __$ConfigDtoCopyWithImpl;
@override @useResult
$Res call({
 String webAuthnRpId, String appName, bool singleTeam
});




}
/// @nodoc
class __$ConfigDtoCopyWithImpl<$Res>
    implements _$ConfigDtoCopyWith<$Res> {
  __$ConfigDtoCopyWithImpl(this._self, this._then);

  final _ConfigDto _self;
  final $Res Function(_ConfigDto) _then;

/// Create a copy of ConfigDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? webAuthnRpId = null,Object? appName = null,Object? singleTeam = null,}) {
  return _then(_ConfigDto(
webAuthnRpId: null == webAuthnRpId ? _self.webAuthnRpId : webAuthnRpId // ignore: cast_nullable_to_non_nullable
as String,appName: null == appName ? _self.appName : appName // ignore: cast_nullable_to_non_nullable
as String,singleTeam: null == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
