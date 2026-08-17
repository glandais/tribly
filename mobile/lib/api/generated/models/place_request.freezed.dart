// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'place_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PlaceRequest {

/// Place name
 String get name;/// Can be used as ride start point
 bool get startPlace;/// Can be used as ride end point
 bool get endPlace;/// Address
 String? get address;/// External link (e.g., Google Maps URL)
 String? get link;/// Geographic coordinates [longitude, latitude]
 PlaceRequestGeometry? get geometry;
/// Create a copy of PlaceRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PlaceRequestCopyWith<PlaceRequest> get copyWith => _$PlaceRequestCopyWithImpl<PlaceRequest>(this as PlaceRequest, _$identity);

  /// Serializes this PlaceRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PlaceRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace)&&(identical(other.address, address) || other.address == address)&&(identical(other.link, link) || other.link == link)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,startPlace,endPlace,address,link,geometry);

@override
String toString() {
  return 'PlaceRequest(name: $name, startPlace: $startPlace, endPlace: $endPlace, address: $address, link: $link, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class $PlaceRequestCopyWith<$Res>  {
  factory $PlaceRequestCopyWith(PlaceRequest value, $Res Function(PlaceRequest) _then) = _$PlaceRequestCopyWithImpl;
@useResult
$Res call({
 String name, bool startPlace, bool endPlace, String? address, String? link, PlaceRequestGeometry? geometry
});


$PlaceRequestGeometryCopyWith<$Res>? get geometry;

}
/// @nodoc
class _$PlaceRequestCopyWithImpl<$Res>
    implements $PlaceRequestCopyWith<$Res> {
  _$PlaceRequestCopyWithImpl(this._self, this._then);

  final PlaceRequest _self;
  final $Res Function(PlaceRequest) _then;

/// Create a copy of PlaceRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? startPlace = null,Object? endPlace = null,Object? address = freezed,Object? link = freezed,Object? geometry = freezed,}) {
  return _then(PlaceRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,startPlace: null == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as bool,endPlace: null == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as bool,address: freezed == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String?,link: freezed == link ? _self.link : link // ignore: cast_nullable_to_non_nullable
as String?,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as PlaceRequestGeometry?,
  ));
}
/// Create a copy of PlaceRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PlaceRequestGeometryCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $PlaceRequestGeometryCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}


/// Adds pattern-matching-related methods to [PlaceRequest].
extension PlaceRequestPatterns on PlaceRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PlaceRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PlaceRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PlaceRequest value)  $default,){
final _that = this;
switch (_that) {
case _PlaceRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PlaceRequest value)?  $default,){
final _that = this;
switch (_that) {
case _PlaceRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  bool startPlace,  bool endPlace,  String? address,  String? link,  PlaceRequestGeometry? geometry)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PlaceRequest() when $default != null:
return $default(_that.name,_that.startPlace,_that.endPlace,_that.address,_that.link,_that.geometry);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  bool startPlace,  bool endPlace,  String? address,  String? link,  PlaceRequestGeometry? geometry)  $default,) {final _that = this;
switch (_that) {
case _PlaceRequest():
return $default(_that.name,_that.startPlace,_that.endPlace,_that.address,_that.link,_that.geometry);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  bool startPlace,  bool endPlace,  String? address,  String? link,  PlaceRequestGeometry? geometry)?  $default,) {final _that = this;
switch (_that) {
case _PlaceRequest() when $default != null:
return $default(_that.name,_that.startPlace,_that.endPlace,_that.address,_that.link,_that.geometry);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PlaceRequest implements PlaceRequest {
  const _PlaceRequest({required this.name, required this.startPlace, required this.endPlace, this.address, this.link, this.geometry});
  factory _PlaceRequest.fromJson(Map<String, dynamic> json) => _$PlaceRequestFromJson(json);

/// Place name
@override final  String name;
/// Can be used as ride start point
@override final  bool startPlace;
/// Can be used as ride end point
@override final  bool endPlace;
/// Address
@override final  String? address;
/// External link (e.g., Google Maps URL)
@override final  String? link;
/// Geographic coordinates [longitude, latitude]
@override final  PlaceRequestGeometry? geometry;

/// Create a copy of PlaceRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PlaceRequestCopyWith<_PlaceRequest> get copyWith => __$PlaceRequestCopyWithImpl<_PlaceRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PlaceRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PlaceRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace)&&(identical(other.address, address) || other.address == address)&&(identical(other.link, link) || other.link == link)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,startPlace,endPlace,address,link,geometry);

@override
String toString() {
  return 'PlaceRequest(name: $name, startPlace: $startPlace, endPlace: $endPlace, address: $address, link: $link, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class _$PlaceRequestCopyWith<$Res> implements $PlaceRequestCopyWith<$Res> {
  factory _$PlaceRequestCopyWith(_PlaceRequest value, $Res Function(_PlaceRequest) _then) = __$PlaceRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, bool startPlace, bool endPlace, String? address, String? link, PlaceRequestGeometry? geometry
});


@override $PlaceRequestGeometryCopyWith<$Res>? get geometry;

}
/// @nodoc
class __$PlaceRequestCopyWithImpl<$Res>
    implements _$PlaceRequestCopyWith<$Res> {
  __$PlaceRequestCopyWithImpl(this._self, this._then);

  final _PlaceRequest _self;
  final $Res Function(_PlaceRequest) _then;

/// Create a copy of PlaceRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? startPlace = null,Object? endPlace = null,Object? address = freezed,Object? link = freezed,Object? geometry = freezed,}) {
  return _then(_PlaceRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,startPlace: null == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as bool,endPlace: null == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as bool,address: freezed == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String?,link: freezed == link ? _self.link : link // ignore: cast_nullable_to_non_nullable
as String?,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as PlaceRequestGeometry?,
  ));
}

/// Create a copy of PlaceRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PlaceRequestGeometryCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $PlaceRequestGeometryCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}

// dart format on
