// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_request_geometry.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamRequestGeometry {

 TeamRequestGeometryTypeType get type;/// Coordinates [longitude, latitude]
 List<double> get coordinates;
/// Create a copy of TeamRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamRequestGeometryCopyWith<TeamRequestGeometry> get copyWith => _$TeamRequestGeometryCopyWithImpl<TeamRequestGeometry>(this as TeamRequestGeometry, _$identity);

  /// Serializes this TeamRequestGeometry to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamRequestGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.coordinates, coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(coordinates));

@override
String toString() {
  return 'TeamRequestGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class $TeamRequestGeometryCopyWith<$Res>  {
  factory $TeamRequestGeometryCopyWith(TeamRequestGeometry value, $Res Function(TeamRequestGeometry) _then) = _$TeamRequestGeometryCopyWithImpl;
@useResult
$Res call({
 TeamRequestGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class _$TeamRequestGeometryCopyWithImpl<$Res>
    implements $TeamRequestGeometryCopyWith<$Res> {
  _$TeamRequestGeometryCopyWithImpl(this._self, this._then);

  final TeamRequestGeometry _self;
  final $Res Function(TeamRequestGeometry) _then;

/// Create a copy of TeamRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_self.copyWith(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as TeamRequestGeometryTypeType,coordinates: null == coordinates ? _self.coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamRequestGeometry].
extension TeamRequestGeometryPatterns on TeamRequestGeometry {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamRequestGeometry value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamRequestGeometry() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamRequestGeometry value)  $default,){
final _that = this;
switch (_that) {
case _TeamRequestGeometry():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamRequestGeometry value)?  $default,){
final _that = this;
switch (_that) {
case _TeamRequestGeometry() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( TeamRequestGeometryTypeType type,  List<double> coordinates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamRequestGeometry() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( TeamRequestGeometryTypeType type,  List<double> coordinates)  $default,) {final _that = this;
switch (_that) {
case _TeamRequestGeometry():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( TeamRequestGeometryTypeType type,  List<double> coordinates)?  $default,) {final _that = this;
switch (_that) {
case _TeamRequestGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamRequestGeometry implements TeamRequestGeometry {
  const _TeamRequestGeometry({required this.type, required final  List<double> coordinates}): _coordinates = coordinates;
  factory _TeamRequestGeometry.fromJson(Map<String, dynamic> json) => _$TeamRequestGeometryFromJson(json);

@override final  TeamRequestGeometryTypeType type;
/// Coordinates [longitude, latitude]
 final  List<double> _coordinates;
/// Coordinates [longitude, latitude]
@override List<double> get coordinates {
  if (_coordinates is EqualUnmodifiableListView) return _coordinates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_coordinates);
}


/// Create a copy of TeamRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamRequestGeometryCopyWith<_TeamRequestGeometry> get copyWith => __$TeamRequestGeometryCopyWithImpl<_TeamRequestGeometry>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamRequestGeometryToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamRequestGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other._coordinates, _coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_coordinates));

@override
String toString() {
  return 'TeamRequestGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class _$TeamRequestGeometryCopyWith<$Res> implements $TeamRequestGeometryCopyWith<$Res> {
  factory _$TeamRequestGeometryCopyWith(_TeamRequestGeometry value, $Res Function(_TeamRequestGeometry) _then) = __$TeamRequestGeometryCopyWithImpl;
@override @useResult
$Res call({
 TeamRequestGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class __$TeamRequestGeometryCopyWithImpl<$Res>
    implements _$TeamRequestGeometryCopyWith<$Res> {
  __$TeamRequestGeometryCopyWithImpl(this._self, this._then);

  final _TeamRequestGeometry _self;
  final $Res Function(_TeamRequestGeometry) _then;

/// Create a copy of TeamRequestGeometry
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_TeamRequestGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as TeamRequestGeometryTypeType,coordinates: null == coordinates ? _self._coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}


}

// dart format on
