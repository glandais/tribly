// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'place_detail_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PlaceDetailDto {

/// Place ID (TSID)
 String get id; String get name; bool get startPlace; bool get endPlace; String? get address; String? get link; GeoJsonPoint? get geometry;
/// Create a copy of PlaceDetailDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PlaceDetailDtoCopyWith<PlaceDetailDto> get copyWith => _$PlaceDetailDtoCopyWithImpl<PlaceDetailDto>(this as PlaceDetailDto, _$identity);

  /// Serializes this PlaceDetailDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PlaceDetailDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace)&&(identical(other.address, address) || other.address == address)&&(identical(other.link, link) || other.link == link)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,startPlace,endPlace,address,link,geometry);

@override
String toString() {
  return 'PlaceDetailDto(id: $id, name: $name, startPlace: $startPlace, endPlace: $endPlace, address: $address, link: $link, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class $PlaceDetailDtoCopyWith<$Res>  {
  factory $PlaceDetailDtoCopyWith(PlaceDetailDto value, $Res Function(PlaceDetailDto) _then) = _$PlaceDetailDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, bool startPlace, bool endPlace, String? address, String? link, GeoJsonPoint? geometry
});


$GeoJsonPointCopyWith<$Res>? get geometry;

}
/// @nodoc
class _$PlaceDetailDtoCopyWithImpl<$Res>
    implements $PlaceDetailDtoCopyWith<$Res> {
  _$PlaceDetailDtoCopyWithImpl(this._self, this._then);

  final PlaceDetailDto _self;
  final $Res Function(PlaceDetailDto) _then;

/// Create a copy of PlaceDetailDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? startPlace = null,Object? endPlace = null,Object? address = freezed,Object? link = freezed,Object? geometry = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,startPlace: null == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as bool,endPlace: null == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as bool,address: freezed == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String?,link: freezed == link ? _self.link : link // ignore: cast_nullable_to_non_nullable
as String?,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint?,
  ));
}
/// Create a copy of PlaceDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $GeoJsonPointCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}


/// Adds pattern-matching-related methods to [PlaceDetailDto].
extension PlaceDetailDtoPatterns on PlaceDetailDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PlaceDetailDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PlaceDetailDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PlaceDetailDto value)  $default,){
final _that = this;
switch (_that) {
case _PlaceDetailDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PlaceDetailDto value)?  $default,){
final _that = this;
switch (_that) {
case _PlaceDetailDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  bool startPlace,  bool endPlace,  String? address,  String? link,  GeoJsonPoint? geometry)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PlaceDetailDto() when $default != null:
return $default(_that.id,_that.name,_that.startPlace,_that.endPlace,_that.address,_that.link,_that.geometry);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  bool startPlace,  bool endPlace,  String? address,  String? link,  GeoJsonPoint? geometry)  $default,) {final _that = this;
switch (_that) {
case _PlaceDetailDto():
return $default(_that.id,_that.name,_that.startPlace,_that.endPlace,_that.address,_that.link,_that.geometry);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  bool startPlace,  bool endPlace,  String? address,  String? link,  GeoJsonPoint? geometry)?  $default,) {final _that = this;
switch (_that) {
case _PlaceDetailDto() when $default != null:
return $default(_that.id,_that.name,_that.startPlace,_that.endPlace,_that.address,_that.link,_that.geometry);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PlaceDetailDto implements PlaceDetailDto {
  const _PlaceDetailDto({required this.id, required this.name, required this.startPlace, required this.endPlace, this.address, this.link, this.geometry});
  factory _PlaceDetailDto.fromJson(Map<String, dynamic> json) => _$PlaceDetailDtoFromJson(json);

/// Place ID (TSID)
@override final  String id;
@override final  String name;
@override final  bool startPlace;
@override final  bool endPlace;
@override final  String? address;
@override final  String? link;
@override final  GeoJsonPoint? geometry;

/// Create a copy of PlaceDetailDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PlaceDetailDtoCopyWith<_PlaceDetailDto> get copyWith => __$PlaceDetailDtoCopyWithImpl<_PlaceDetailDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PlaceDetailDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PlaceDetailDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace)&&(identical(other.address, address) || other.address == address)&&(identical(other.link, link) || other.link == link)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,startPlace,endPlace,address,link,geometry);

@override
String toString() {
  return 'PlaceDetailDto(id: $id, name: $name, startPlace: $startPlace, endPlace: $endPlace, address: $address, link: $link, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class _$PlaceDetailDtoCopyWith<$Res> implements $PlaceDetailDtoCopyWith<$Res> {
  factory _$PlaceDetailDtoCopyWith(_PlaceDetailDto value, $Res Function(_PlaceDetailDto) _then) = __$PlaceDetailDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, bool startPlace, bool endPlace, String? address, String? link, GeoJsonPoint? geometry
});


@override $GeoJsonPointCopyWith<$Res>? get geometry;

}
/// @nodoc
class __$PlaceDetailDtoCopyWithImpl<$Res>
    implements _$PlaceDetailDtoCopyWith<$Res> {
  __$PlaceDetailDtoCopyWithImpl(this._self, this._then);

  final _PlaceDetailDto _self;
  final $Res Function(_PlaceDetailDto) _then;

/// Create a copy of PlaceDetailDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? startPlace = null,Object? endPlace = null,Object? address = freezed,Object? link = freezed,Object? geometry = freezed,}) {
  return _then(_PlaceDetailDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,startPlace: null == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as bool,endPlace: null == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as bool,address: freezed == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String?,link: freezed == link ? _self.link : link // ignore: cast_nullable_to_non_nullable
as String?,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint?,
  ));
}

/// Create a copy of PlaceDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $GeoJsonPointCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}

// dart format on
