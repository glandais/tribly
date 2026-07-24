// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'version_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$VersionDto {

/// API contract version (semver). Bumped whenever the OpenAPI contract changes, and also exposed as info.version in the contract itself.
 String get apiVersion;/// Short git commit the server was built from, null when unavailable
 String? get commit;/// Build timestamp (ISO-8601, UTC), null when unavailable
 String? get buildTime;
/// Create a copy of VersionDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$VersionDtoCopyWith<VersionDto> get copyWith => _$VersionDtoCopyWithImpl<VersionDto>(this as VersionDto, _$identity);

  /// Serializes this VersionDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is VersionDto&&(identical(other.apiVersion, apiVersion) || other.apiVersion == apiVersion)&&(identical(other.commit, commit) || other.commit == commit)&&(identical(other.buildTime, buildTime) || other.buildTime == buildTime));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,apiVersion,commit,buildTime);

@override
String toString() {
  return 'VersionDto(apiVersion: $apiVersion, commit: $commit, buildTime: $buildTime)';
}


}

/// @nodoc
abstract mixin class $VersionDtoCopyWith<$Res>  {
  factory $VersionDtoCopyWith(VersionDto value, $Res Function(VersionDto) _then) = _$VersionDtoCopyWithImpl;
@useResult
$Res call({
 String apiVersion, String? commit, String? buildTime
});




}
/// @nodoc
class _$VersionDtoCopyWithImpl<$Res>
    implements $VersionDtoCopyWith<$Res> {
  _$VersionDtoCopyWithImpl(this._self, this._then);

  final VersionDto _self;
  final $Res Function(VersionDto) _then;

/// Create a copy of VersionDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? apiVersion = null,Object? commit = freezed,Object? buildTime = freezed,}) {
  return _then(_self.copyWith(
apiVersion: null == apiVersion ? _self.apiVersion : apiVersion // ignore: cast_nullable_to_non_nullable
as String,commit: freezed == commit ? _self.commit : commit // ignore: cast_nullable_to_non_nullable
as String?,buildTime: freezed == buildTime ? _self.buildTime : buildTime // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [VersionDto].
extension VersionDtoPatterns on VersionDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _VersionDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _VersionDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _VersionDto value)  $default,){
final _that = this;
switch (_that) {
case _VersionDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _VersionDto value)?  $default,){
final _that = this;
switch (_that) {
case _VersionDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String apiVersion,  String? commit,  String? buildTime)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _VersionDto() when $default != null:
return $default(_that.apiVersion,_that.commit,_that.buildTime);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String apiVersion,  String? commit,  String? buildTime)  $default,) {final _that = this;
switch (_that) {
case _VersionDto():
return $default(_that.apiVersion,_that.commit,_that.buildTime);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String apiVersion,  String? commit,  String? buildTime)?  $default,) {final _that = this;
switch (_that) {
case _VersionDto() when $default != null:
return $default(_that.apiVersion,_that.commit,_that.buildTime);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _VersionDto implements VersionDto {
  const _VersionDto({required this.apiVersion, this.commit, this.buildTime});
  factory _VersionDto.fromJson(Map<String, dynamic> json) => _$VersionDtoFromJson(json);

/// API contract version (semver). Bumped whenever the OpenAPI contract changes, and also exposed as info.version in the contract itself.
@override final  String apiVersion;
/// Short git commit the server was built from, null when unavailable
@override final  String? commit;
/// Build timestamp (ISO-8601, UTC), null when unavailable
@override final  String? buildTime;

/// Create a copy of VersionDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$VersionDtoCopyWith<_VersionDto> get copyWith => __$VersionDtoCopyWithImpl<_VersionDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$VersionDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _VersionDto&&(identical(other.apiVersion, apiVersion) || other.apiVersion == apiVersion)&&(identical(other.commit, commit) || other.commit == commit)&&(identical(other.buildTime, buildTime) || other.buildTime == buildTime));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,apiVersion,commit,buildTime);

@override
String toString() {
  return 'VersionDto(apiVersion: $apiVersion, commit: $commit, buildTime: $buildTime)';
}


}

/// @nodoc
abstract mixin class _$VersionDtoCopyWith<$Res> implements $VersionDtoCopyWith<$Res> {
  factory _$VersionDtoCopyWith(_VersionDto value, $Res Function(_VersionDto) _then) = __$VersionDtoCopyWithImpl;
@override @useResult
$Res call({
 String apiVersion, String? commit, String? buildTime
});




}
/// @nodoc
class __$VersionDtoCopyWithImpl<$Res>
    implements _$VersionDtoCopyWith<$Res> {
  __$VersionDtoCopyWithImpl(this._self, this._then);

  final _VersionDto _self;
  final $Res Function(_VersionDto) _then;

/// Create a copy of VersionDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? apiVersion = null,Object? commit = freezed,Object? buildTime = freezed,}) {
  return _then(_VersionDto(
apiVersion: null == apiVersion ? _self.apiVersion : apiVersion // ignore: cast_nullable_to_non_nullable
as String,commit: freezed == commit ? _self.commit : commit // ignore: cast_nullable_to_non_nullable
as String?,buildTime: freezed == buildTime ? _self.buildTime : buildTime // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
