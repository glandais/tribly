// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'place_request_geometry.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PlaceRequestGeometry {

 PlaceRequestGeometryTypeType get type;/// Coordinates [longitude, latitude]
 List<double> get coordinates;
/// Create a copy of PlaceRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PlaceRequestGeometryCopyWith<PlaceRequestGeometry> get copyWith => _$PlaceRequestGeometryCopyWithImpl<PlaceRequestGeometry>(this as PlaceRequestGeometry, _$identity);

  /// Serializes this PlaceRequestGeometry to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PlaceRequestGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.coordinates, coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(coordinates));

@override
String toString() {
  return 'PlaceRequestGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class $PlaceRequestGeometryCopyWith<$Res>  {
  factory $PlaceRequestGeometryCopyWith(PlaceRequestGeometry value, $Res Function(PlaceRequestGeometry) _then) = _$PlaceRequestGeometryCopyWithImpl;
@useResult
$Res call({
 PlaceRequestGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class _$PlaceRequestGeometryCopyWithImpl<$Res>
    implements $PlaceRequestGeometryCopyWith<$Res> {
  _$PlaceRequestGeometryCopyWithImpl(this._self, this._then);

  final PlaceRequestGeometry _self;
  final $Res Function(PlaceRequestGeometry) _then;

/// Create a copy of PlaceRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_self.copyWith(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as PlaceRequestGeometryTypeType,coordinates: null == coordinates ? _self.coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}

}


/// Adds pattern-matching-related methods to [PlaceRequestGeometry].
extension PlaceRequestGeometryPatterns on PlaceRequestGeometry {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PlaceRequestGeometry value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PlaceRequestGeometry() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PlaceRequestGeometry value)  $default,){
final _that = this;
switch (_that) {
case _PlaceRequestGeometry():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PlaceRequestGeometry value)?  $default,){
final _that = this;
switch (_that) {
case _PlaceRequestGeometry() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( PlaceRequestGeometryTypeType type,  List<double> coordinates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PlaceRequestGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( PlaceRequestGeometryTypeType type,  List<double> coordinates)  $default,) {final _that = this;
switch (_that) {
case _PlaceRequestGeometry():
return $default(_that.type,_that.coordinates);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( PlaceRequestGeometryTypeType type,  List<double> coordinates)?  $default,) {final _that = this;
switch (_that) {
case _PlaceRequestGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PlaceRequestGeometry implements PlaceRequestGeometry {
  const _PlaceRequestGeometry({required this.type, required final  List<double> coordinates}): _coordinates = coordinates;
  factory _PlaceRequestGeometry.fromJson(Map<String, dynamic> json) => _$PlaceRequestGeometryFromJson(json);

@override final  PlaceRequestGeometryTypeType type;
/// Coordinates [longitude, latitude]
 final  List<double> _coordinates;
/// Coordinates [longitude, latitude]
@override List<double> get coordinates {
  if (_coordinates is EqualUnmodifiableListView) return _coordinates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_coordinates);
}


/// Create a copy of PlaceRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PlaceRequestGeometryCopyWith<_PlaceRequestGeometry> get copyWith => __$PlaceRequestGeometryCopyWithImpl<_PlaceRequestGeometry>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PlaceRequestGeometryToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PlaceRequestGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other._coordinates, _coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_coordinates));

@override
String toString() {
  return 'PlaceRequestGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class _$PlaceRequestGeometryCopyWith<$Res> implements $PlaceRequestGeometryCopyWith<$Res> {
  factory _$PlaceRequestGeometryCopyWith(_PlaceRequestGeometry value, $Res Function(_PlaceRequestGeometry) _then) = __$PlaceRequestGeometryCopyWithImpl;
@override @useResult
$Res call({
 PlaceRequestGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class __$PlaceRequestGeometryCopyWithImpl<$Res>
    implements _$PlaceRequestGeometryCopyWith<$Res> {
  __$PlaceRequestGeometryCopyWithImpl(this._self, this._then);

  final _PlaceRequestGeometry _self;
  final $Res Function(_PlaceRequestGeometry) _then;

/// Create a copy of PlaceRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_PlaceRequestGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as PlaceRequestGeometryTypeType,coordinates: null == coordinates ? _self._coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}


}

// dart format on
