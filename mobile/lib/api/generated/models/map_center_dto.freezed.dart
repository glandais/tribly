// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'map_center_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MapCenterDto {

/// Latitude in WGS84 degrees
 double get lat;/// Longitude in WGS84 degrees
 double get lon;/// Initial zoom level
 double get zoom;
/// Create a copy of MapCenterDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MapCenterDtoCopyWith<MapCenterDto> get copyWith => _$MapCenterDtoCopyWithImpl<MapCenterDto>(this as MapCenterDto, _$identity);

  /// Serializes this MapCenterDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MapCenterDto&&(identical(other.lat, lat) || other.lat == lat)&&(identical(other.lon, lon) || other.lon == lon)&&(identical(other.zoom, zoom) || other.zoom == zoom));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,lat,lon,zoom);

@override
String toString() {
  return 'MapCenterDto(lat: $lat, lon: $lon, zoom: $zoom)';
}


}

/// @nodoc
abstract mixin class $MapCenterDtoCopyWith<$Res>  {
  factory $MapCenterDtoCopyWith(MapCenterDto value, $Res Function(MapCenterDto) _then) = _$MapCenterDtoCopyWithImpl;
@useResult
$Res call({
 double lat, double lon, double zoom
});




}
/// @nodoc
class _$MapCenterDtoCopyWithImpl<$Res>
    implements $MapCenterDtoCopyWith<$Res> {
  _$MapCenterDtoCopyWithImpl(this._self, this._then);

  final MapCenterDto _self;
  final $Res Function(MapCenterDto) _then;

/// Create a copy of MapCenterDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? lat = null,Object? lon = null,Object? zoom = null,}) {
  return _then(MapCenterDto(
lat: null == lat ? _self.lat : lat // ignore: cast_nullable_to_non_nullable
as double,lon: null == lon ? _self.lon : lon // ignore: cast_nullable_to_non_nullable
as double,zoom: null == zoom ? _self.zoom : zoom // ignore: cast_nullable_to_non_nullable
as double,
  ));
}

}


/// Adds pattern-matching-related methods to [MapCenterDto].
extension MapCenterDtoPatterns on MapCenterDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MapCenterDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MapCenterDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MapCenterDto value)  $default,){
final _that = this;
switch (_that) {
case _MapCenterDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MapCenterDto value)?  $default,){
final _that = this;
switch (_that) {
case _MapCenterDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( double lat,  double lon,  double zoom)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MapCenterDto() when $default != null:
return $default(_that.lat,_that.lon,_that.zoom);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( double lat,  double lon,  double zoom)  $default,) {final _that = this;
switch (_that) {
case _MapCenterDto():
return $default(_that.lat,_that.lon,_that.zoom);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( double lat,  double lon,  double zoom)?  $default,) {final _that = this;
switch (_that) {
case _MapCenterDto() when $default != null:
return $default(_that.lat,_that.lon,_that.zoom);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MapCenterDto implements MapCenterDto {
  const _MapCenterDto({required this.lat, required this.lon, required this.zoom});
  factory _MapCenterDto.fromJson(Map<String, dynamic> json) => _$MapCenterDtoFromJson(json);

/// Latitude in WGS84 degrees
@override final  double lat;
/// Longitude in WGS84 degrees
@override final  double lon;
/// Initial zoom level
@override final  double zoom;

/// Create a copy of MapCenterDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MapCenterDtoCopyWith<_MapCenterDto> get copyWith => __$MapCenterDtoCopyWithImpl<_MapCenterDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MapCenterDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MapCenterDto&&(identical(other.lat, lat) || other.lat == lat)&&(identical(other.lon, lon) || other.lon == lon)&&(identical(other.zoom, zoom) || other.zoom == zoom));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,lat,lon,zoom);

@override
String toString() {
  return 'MapCenterDto(lat: $lat, lon: $lon, zoom: $zoom)';
}


}

/// @nodoc
abstract mixin class _$MapCenterDtoCopyWith<$Res> implements $MapCenterDtoCopyWith<$Res> {
  factory _$MapCenterDtoCopyWith(_MapCenterDto value, $Res Function(_MapCenterDto) _then) = __$MapCenterDtoCopyWithImpl;
@override @useResult
$Res call({
 double lat, double lon, double zoom
});




}
/// @nodoc
class __$MapCenterDtoCopyWithImpl<$Res>
    implements _$MapCenterDtoCopyWith<$Res> {
  __$MapCenterDtoCopyWithImpl(this._self, this._then);

  final _MapCenterDto _self;
  final $Res Function(_MapCenterDto) _then;

/// Create a copy of MapCenterDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? lat = null,Object? lon = null,Object? zoom = null,}) {
  return _then(_MapCenterDto(
lat: null == lat ? _self.lat : lat // ignore: cast_nullable_to_non_nullable
as double,lon: null == lon ? _self.lon : lon // ignore: cast_nullable_to_non_nullable
as double,zoom: null == zoom ? _self.zoom : zoom // ignore: cast_nullable_to_non_nullable
as double,
  ));
}


}

// dart format on
