// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'map_terrain_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MapTerrainDto {

/// URL of a TileJSON document describing raster-DEM tiles. The document declares the encoding; the clients do not.
 String get url;/// Deepest zoom the provider renders. Honoured by the web, which sets it on the source; the mobile SDKs take their zoom range from the TileJSON instead.
 int get maxZoom;
/// Create a copy of MapTerrainDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MapTerrainDtoCopyWith<MapTerrainDto> get copyWith => _$MapTerrainDtoCopyWithImpl<MapTerrainDto>(this as MapTerrainDto, _$identity);

  /// Serializes this MapTerrainDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as MapTerrainDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MapTerrainDto&&(identical(other.url, _this.url) || other.url == _this.url)&&(identical(other.maxZoom, _this.maxZoom) || other.maxZoom == _this.maxZoom));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as MapTerrainDto;
  return Object.hash(runtimeType,_this.url,_this.maxZoom);
}

@override
String toString() {
  final _this = this as MapTerrainDto;
  return 'MapTerrainDto(url: ${_this.url}, maxZoom: ${_this.maxZoom})';
}


}

/// @nodoc
abstract mixin class $MapTerrainDtoCopyWith<$Res>  {
  factory $MapTerrainDtoCopyWith(MapTerrainDto value, $Res Function(MapTerrainDto) _then) = _$MapTerrainDtoCopyWithImpl;
@useResult
$Res call({
 String url, int maxZoom
});




}
/// @nodoc
class _$MapTerrainDtoCopyWithImpl<$Res>
    implements $MapTerrainDtoCopyWith<$Res> {
  _$MapTerrainDtoCopyWithImpl(this._self, this._then);

  final MapTerrainDto _self;
  final $Res Function(MapTerrainDto) _then;

/// Create a copy of MapTerrainDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? url = null,Object? maxZoom = null,}) {
  return _then(MapTerrainDto(
url: null == url ? _self.url : url // ignore: cast_nullable_to_non_nullable
as String,maxZoom: null == maxZoom ? _self.maxZoom : maxZoom // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [MapTerrainDto].
extension MapTerrainDtoPatterns on MapTerrainDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MapTerrainDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MapTerrainDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MapTerrainDto value)  $default,){
final _that = this;
switch (_that) {
case _MapTerrainDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MapTerrainDto value)?  $default,){
final _that = this;
switch (_that) {
case _MapTerrainDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String url,  int maxZoom)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MapTerrainDto() when $default != null:
return $default(_that.url,_that.maxZoom);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String url,  int maxZoom)  $default,) {final _that = this;
switch (_that) {
case _MapTerrainDto():
return $default(_that.url,_that.maxZoom);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String url,  int maxZoom)?  $default,) {final _that = this;
switch (_that) {
case _MapTerrainDto() when $default != null:
return $default(_that.url,_that.maxZoom);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MapTerrainDto implements MapTerrainDto {
  const _MapTerrainDto({required this.url, required this.maxZoom});
  factory _MapTerrainDto.fromJson(Map<String, dynamic> json) => _$MapTerrainDtoFromJson(json);

/// URL of a TileJSON document describing raster-DEM tiles. The document declares the encoding; the clients do not.
@override final  String url;
/// Deepest zoom the provider renders. Honoured by the web, which sets it on the source; the mobile SDKs take their zoom range from the TileJSON instead.
@override final  int maxZoom;

/// Create a copy of MapTerrainDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MapTerrainDtoCopyWith<_MapTerrainDto> get copyWith => __$MapTerrainDtoCopyWithImpl<_MapTerrainDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MapTerrainDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _MapTerrainDto&&(identical(other.url, url) || other.url == url)&&(identical(other.maxZoom, maxZoom) || other.maxZoom == maxZoom));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,url,maxZoom);
}

@override
String toString() {
    return 'MapTerrainDto(url: $url, maxZoom: $maxZoom)';
}


}

/// @nodoc
abstract mixin class _$MapTerrainDtoCopyWith<$Res> implements $MapTerrainDtoCopyWith<$Res> {
  factory _$MapTerrainDtoCopyWith(_MapTerrainDto value, $Res Function(_MapTerrainDto) _then) = __$MapTerrainDtoCopyWithImpl;
@override @useResult
$Res call({
 String url, int maxZoom
});




}
/// @nodoc
class __$MapTerrainDtoCopyWithImpl<$Res>
    implements _$MapTerrainDtoCopyWith<$Res> {
  __$MapTerrainDtoCopyWithImpl(this._self, this._then);

  final _MapTerrainDto _self;
  final $Res Function(_MapTerrainDto) _then;

/// Create a copy of MapTerrainDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? url = null,Object? maxZoom = null,}) {
  return _then(_MapTerrainDto(
url: null == url ? _self.url : url // ignore: cast_nullable_to_non_nullable
as String,maxZoom: null == maxZoom ? _self.maxZoom : maxZoom // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
