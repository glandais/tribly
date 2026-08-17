// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'waypoint_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$WaypointDto {

 GeoJsonPoint get geometry; String? get name;
/// Create a copy of WaypointDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$WaypointDtoCopyWith<WaypointDto> get copyWith => _$WaypointDtoCopyWithImpl<WaypointDto>(this as WaypointDto, _$identity);

  /// Serializes this WaypointDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is WaypointDto&&(identical(other.geometry, geometry) || other.geometry == geometry)&&(identical(other.name, name) || other.name == name));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,geometry,name);

@override
String toString() {
  return 'WaypointDto(geometry: $geometry, name: $name)';
}


}

/// @nodoc
abstract mixin class $WaypointDtoCopyWith<$Res>  {
  factory $WaypointDtoCopyWith(WaypointDto value, $Res Function(WaypointDto) _then) = _$WaypointDtoCopyWithImpl;
@useResult
$Res call({
 GeoJsonPoint geometry, String? name
});


$GeoJsonPointCopyWith<$Res> get geometry;

}
/// @nodoc
class _$WaypointDtoCopyWithImpl<$Res>
    implements $WaypointDtoCopyWith<$Res> {
  _$WaypointDtoCopyWithImpl(this._self, this._then);

  final WaypointDto _self;
  final $Res Function(WaypointDto) _then;

/// Create a copy of WaypointDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? geometry = null,Object? name = freezed,}) {
  return _then(WaypointDto(
geometry: null == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint,name: freezed == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of WaypointDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res> get geometry {
  
  return $GeoJsonPointCopyWith<$Res>(_self.geometry, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}


/// Adds pattern-matching-related methods to [WaypointDto].
extension WaypointDtoPatterns on WaypointDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _WaypointDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _WaypointDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _WaypointDto value)  $default,){
final _that = this;
switch (_that) {
case _WaypointDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _WaypointDto value)?  $default,){
final _that = this;
switch (_that) {
case _WaypointDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( GeoJsonPoint geometry,  String? name)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _WaypointDto() when $default != null:
return $default(_that.geometry,_that.name);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( GeoJsonPoint geometry,  String? name)  $default,) {final _that = this;
switch (_that) {
case _WaypointDto():
return $default(_that.geometry,_that.name);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( GeoJsonPoint geometry,  String? name)?  $default,) {final _that = this;
switch (_that) {
case _WaypointDto() when $default != null:
return $default(_that.geometry,_that.name);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _WaypointDto implements WaypointDto {
  const _WaypointDto({required this.geometry, this.name});
  factory _WaypointDto.fromJson(Map<String, dynamic> json) => _$WaypointDtoFromJson(json);

@override final  GeoJsonPoint geometry;
@override final  String? name;

/// Create a copy of WaypointDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$WaypointDtoCopyWith<_WaypointDto> get copyWith => __$WaypointDtoCopyWithImpl<_WaypointDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$WaypointDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _WaypointDto&&(identical(other.geometry, geometry) || other.geometry == geometry)&&(identical(other.name, name) || other.name == name));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,geometry,name);

@override
String toString() {
  return 'WaypointDto(geometry: $geometry, name: $name)';
}


}

/// @nodoc
abstract mixin class _$WaypointDtoCopyWith<$Res> implements $WaypointDtoCopyWith<$Res> {
  factory _$WaypointDtoCopyWith(_WaypointDto value, $Res Function(_WaypointDto) _then) = __$WaypointDtoCopyWithImpl;
@override @useResult
$Res call({
 GeoJsonPoint geometry, String? name
});


@override $GeoJsonPointCopyWith<$Res> get geometry;

}
/// @nodoc
class __$WaypointDtoCopyWithImpl<$Res>
    implements _$WaypointDtoCopyWith<$Res> {
  __$WaypointDtoCopyWithImpl(this._self, this._then);

  final _WaypointDto _self;
  final $Res Function(_WaypointDto) _then;

/// Create a copy of WaypointDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? geometry = null,Object? name = freezed,}) {
  return _then(_WaypointDto(
geometry: null == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint,name: freezed == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of WaypointDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res> get geometry {
  
  return $GeoJsonPointCopyWith<$Res>(_self.geometry, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}

// dart format on
