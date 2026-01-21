// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_detail_dto_geometry.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamDetailDtoGeometry {

 TeamDetailDtoGeometryTypeType get type;/// Coordinates [longitude, latitude]
 List<double> get coordinates;
/// Create a copy of TeamDetailDtoGeometry
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamDetailDtoGeometryCopyWith<TeamDetailDtoGeometry> get copyWith => _$TeamDetailDtoGeometryCopyWithImpl<TeamDetailDtoGeometry>(this as TeamDetailDtoGeometry, _$identity);

  /// Serializes this TeamDetailDtoGeometry to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamDetailDtoGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.coordinates, coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(coordinates));

@override
String toString() {
  return 'TeamDetailDtoGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class $TeamDetailDtoGeometryCopyWith<$Res>  {
  factory $TeamDetailDtoGeometryCopyWith(TeamDetailDtoGeometry value, $Res Function(TeamDetailDtoGeometry) _then) = _$TeamDetailDtoGeometryCopyWithImpl;
@useResult
$Res call({
 TeamDetailDtoGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class _$TeamDetailDtoGeometryCopyWithImpl<$Res>
    implements $TeamDetailDtoGeometryCopyWith<$Res> {
  _$TeamDetailDtoGeometryCopyWithImpl(this._self, this._then);

  final TeamDetailDtoGeometry _self;
  final $Res Function(TeamDetailDtoGeometry) _then;

/// Create a copy of TeamDetailDtoGeometry
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_self.copyWith(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as TeamDetailDtoGeometryTypeType,coordinates: null == coordinates ? _self.coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamDetailDtoGeometry].
extension TeamDetailDtoGeometryPatterns on TeamDetailDtoGeometry {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamDetailDtoGeometry value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamDetailDtoGeometry() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamDetailDtoGeometry value)  $default,){
final _that = this;
switch (_that) {
case _TeamDetailDtoGeometry():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamDetailDtoGeometry value)?  $default,){
final _that = this;
switch (_that) {
case _TeamDetailDtoGeometry() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( TeamDetailDtoGeometryTypeType type,  List<double> coordinates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamDetailDtoGeometry() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( TeamDetailDtoGeometryTypeType type,  List<double> coordinates)  $default,) {final _that = this;
switch (_that) {
case _TeamDetailDtoGeometry():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( TeamDetailDtoGeometryTypeType type,  List<double> coordinates)?  $default,) {final _that = this;
switch (_that) {
case _TeamDetailDtoGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamDetailDtoGeometry implements TeamDetailDtoGeometry {
  const _TeamDetailDtoGeometry({required this.type, required final  List<double> coordinates}): _coordinates = coordinates;
  factory _TeamDetailDtoGeometry.fromJson(Map<String, dynamic> json) => _$TeamDetailDtoGeometryFromJson(json);

@override final  TeamDetailDtoGeometryTypeType type;
/// Coordinates [longitude, latitude]
 final  List<double> _coordinates;
/// Coordinates [longitude, latitude]
@override List<double> get coordinates {
  if (_coordinates is EqualUnmodifiableListView) return _coordinates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_coordinates);
}


/// Create a copy of TeamDetailDtoGeometry
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamDetailDtoGeometryCopyWith<_TeamDetailDtoGeometry> get copyWith => __$TeamDetailDtoGeometryCopyWithImpl<_TeamDetailDtoGeometry>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamDetailDtoGeometryToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamDetailDtoGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other._coordinates, _coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_coordinates));

@override
String toString() {
  return 'TeamDetailDtoGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class _$TeamDetailDtoGeometryCopyWith<$Res> implements $TeamDetailDtoGeometryCopyWith<$Res> {
  factory _$TeamDetailDtoGeometryCopyWith(_TeamDetailDtoGeometry value, $Res Function(_TeamDetailDtoGeometry) _then) = __$TeamDetailDtoGeometryCopyWithImpl;
@override @useResult
$Res call({
 TeamDetailDtoGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class __$TeamDetailDtoGeometryCopyWithImpl<$Res>
    implements _$TeamDetailDtoGeometryCopyWith<$Res> {
  __$TeamDetailDtoGeometryCopyWithImpl(this._self, this._then);

  final _TeamDetailDtoGeometry _self;
  final $Res Function(_TeamDetailDtoGeometry) _then;

/// Create a copy of TeamDetailDtoGeometry
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_TeamDetailDtoGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as TeamDetailDtoGeometryTypeType,coordinates: null == coordinates ? _self._coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}


}

// dart format on
