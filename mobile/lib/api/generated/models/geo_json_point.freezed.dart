// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'geo_json_point.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GeoJsonPoint {

 GeoJsonPointTypeType get type;/// Coordinates [longitude, latitude]
 List<double> get coordinates;
/// Create a copy of GeoJsonPoint
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<GeoJsonPoint> get copyWith => _$GeoJsonPointCopyWithImpl<GeoJsonPoint>(this as GeoJsonPoint, _$identity);

  /// Serializes this GeoJsonPoint to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GeoJsonPoint&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.coordinates, coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(coordinates));

@override
String toString() {
  return 'GeoJsonPoint(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class $GeoJsonPointCopyWith<$Res>  {
  factory $GeoJsonPointCopyWith(GeoJsonPoint value, $Res Function(GeoJsonPoint) _then) = _$GeoJsonPointCopyWithImpl;
@useResult
$Res call({
 GeoJsonPointTypeType type, List<double> coordinates
});




}
/// @nodoc
class _$GeoJsonPointCopyWithImpl<$Res>
    implements $GeoJsonPointCopyWith<$Res> {
  _$GeoJsonPointCopyWithImpl(this._self, this._then);

  final GeoJsonPoint _self;
  final $Res Function(GeoJsonPoint) _then;

/// Create a copy of GeoJsonPoint
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_self.copyWith(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as GeoJsonPointTypeType,coordinates: null == coordinates ? _self.coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}

}


/// Adds pattern-matching-related methods to [GeoJsonPoint].
extension GeoJsonPointPatterns on GeoJsonPoint {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GeoJsonPoint value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GeoJsonPoint() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GeoJsonPoint value)  $default,){
final _that = this;
switch (_that) {
case _GeoJsonPoint():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GeoJsonPoint value)?  $default,){
final _that = this;
switch (_that) {
case _GeoJsonPoint() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( GeoJsonPointTypeType type,  List<double> coordinates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GeoJsonPoint() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( GeoJsonPointTypeType type,  List<double> coordinates)  $default,) {final _that = this;
switch (_that) {
case _GeoJsonPoint():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( GeoJsonPointTypeType type,  List<double> coordinates)?  $default,) {final _that = this;
switch (_that) {
case _GeoJsonPoint() when $default != null:
return $default(_that.type,_that.coordinates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GeoJsonPoint implements GeoJsonPoint {
  const _GeoJsonPoint({required this.type, required final  List<double> coordinates}): _coordinates = coordinates;
  factory _GeoJsonPoint.fromJson(Map<String, dynamic> json) => _$GeoJsonPointFromJson(json);

@override final  GeoJsonPointTypeType type;
/// Coordinates [longitude, latitude]
 final  List<double> _coordinates;
/// Coordinates [longitude, latitude]
@override List<double> get coordinates {
  if (_coordinates is EqualUnmodifiableListView) return _coordinates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_coordinates);
}


/// Create a copy of GeoJsonPoint
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GeoJsonPointCopyWith<_GeoJsonPoint> get copyWith => __$GeoJsonPointCopyWithImpl<_GeoJsonPoint>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GeoJsonPointToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GeoJsonPoint&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other._coordinates, _coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_coordinates));

@override
String toString() {
  return 'GeoJsonPoint(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class _$GeoJsonPointCopyWith<$Res> implements $GeoJsonPointCopyWith<$Res> {
  factory _$GeoJsonPointCopyWith(_GeoJsonPoint value, $Res Function(_GeoJsonPoint) _then) = __$GeoJsonPointCopyWithImpl;
@override @useResult
$Res call({
 GeoJsonPointTypeType type, List<double> coordinates
});




}
/// @nodoc
class __$GeoJsonPointCopyWithImpl<$Res>
    implements _$GeoJsonPointCopyWith<$Res> {
  __$GeoJsonPointCopyWithImpl(this._self, this._then);

  final _GeoJsonPoint _self;
  final $Res Function(_GeoJsonPoint) _then;

/// Create a copy of GeoJsonPoint
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_GeoJsonPoint(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as GeoJsonPointTypeType,coordinates: null == coordinates ? _self._coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}


}

// dart format on
