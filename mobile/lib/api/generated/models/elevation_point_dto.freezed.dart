// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'elevation_point_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ElevationPointDto {

/// Cumulative distance from the start of the route, in meters
 double get distance;/// Elevation above sea level, in meters
 double get elevation;/// Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.
 double get grade;
/// Create a copy of ElevationPointDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ElevationPointDtoCopyWith<ElevationPointDto> get copyWith => _$ElevationPointDtoCopyWithImpl<ElevationPointDto>(this as ElevationPointDto, _$identity);

  /// Serializes this ElevationPointDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ElevationPointDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ElevationPointDto&&(identical(other.distance, _this.distance) || other.distance == _this.distance)&&(identical(other.elevation, _this.elevation) || other.elevation == _this.elevation)&&(identical(other.grade, _this.grade) || other.grade == _this.grade));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ElevationPointDto;
  return Object.hash(runtimeType,_this.distance,_this.elevation,_this.grade);
}

@override
String toString() {
  final _this = this as ElevationPointDto;
  return 'ElevationPointDto(distance: ${_this.distance}, elevation: ${_this.elevation}, grade: ${_this.grade})';
}


}

/// @nodoc
abstract mixin class $ElevationPointDtoCopyWith<$Res>  {
  factory $ElevationPointDtoCopyWith(ElevationPointDto value, $Res Function(ElevationPointDto) _then) = _$ElevationPointDtoCopyWithImpl;
@useResult
$Res call({
 double distance, double elevation, double grade
});




}
/// @nodoc
class _$ElevationPointDtoCopyWithImpl<$Res>
    implements $ElevationPointDtoCopyWith<$Res> {
  _$ElevationPointDtoCopyWithImpl(this._self, this._then);

  final ElevationPointDto _self;
  final $Res Function(ElevationPointDto) _then;

/// Create a copy of ElevationPointDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? distance = null,Object? elevation = null,Object? grade = null,}) {
  return _then(ElevationPointDto(
distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevation: null == elevation ? _self.elevation : elevation // ignore: cast_nullable_to_non_nullable
as double,grade: null == grade ? _self.grade : grade // ignore: cast_nullable_to_non_nullable
as double,
  ));
}

}


/// Adds pattern-matching-related methods to [ElevationPointDto].
extension ElevationPointDtoPatterns on ElevationPointDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ElevationPointDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ElevationPointDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ElevationPointDto value)  $default,){
final _that = this;
switch (_that) {
case _ElevationPointDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ElevationPointDto value)?  $default,){
final _that = this;
switch (_that) {
case _ElevationPointDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( double distance,  double elevation,  double grade)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ElevationPointDto() when $default != null:
return $default(_that.distance,_that.elevation,_that.grade);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( double distance,  double elevation,  double grade)  $default,) {final _that = this;
switch (_that) {
case _ElevationPointDto():
return $default(_that.distance,_that.elevation,_that.grade);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( double distance,  double elevation,  double grade)?  $default,) {final _that = this;
switch (_that) {
case _ElevationPointDto() when $default != null:
return $default(_that.distance,_that.elevation,_that.grade);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ElevationPointDto implements ElevationPointDto {
  const _ElevationPointDto({required this.distance, required this.elevation, required this.grade});
  factory _ElevationPointDto.fromJson(Map<String, dynamic> json) => _$ElevationPointDtoFromJson(json);

/// Cumulative distance from the start of the route, in meters
@override final  double distance;
/// Elevation above sea level, in meters
@override final  double elevation;
/// Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.
@override final  double grade;

/// Create a copy of ElevationPointDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ElevationPointDtoCopyWith<_ElevationPointDto> get copyWith => __$ElevationPointDtoCopyWithImpl<_ElevationPointDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ElevationPointDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ElevationPointDto&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevation, elevation) || other.elevation == elevation)&&(identical(other.grade, grade) || other.grade == grade));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,distance,elevation,grade);
}

@override
String toString() {
    return 'ElevationPointDto(distance: $distance, elevation: $elevation, grade: $grade)';
}


}

/// @nodoc
abstract mixin class _$ElevationPointDtoCopyWith<$Res> implements $ElevationPointDtoCopyWith<$Res> {
  factory _$ElevationPointDtoCopyWith(_ElevationPointDto value, $Res Function(_ElevationPointDto) _then) = __$ElevationPointDtoCopyWithImpl;
@override @useResult
$Res call({
 double distance, double elevation, double grade
});




}
/// @nodoc
class __$ElevationPointDtoCopyWithImpl<$Res>
    implements _$ElevationPointDtoCopyWith<$Res> {
  __$ElevationPointDtoCopyWithImpl(this._self, this._then);

  final _ElevationPointDto _self;
  final $Res Function(_ElevationPointDto) _then;

/// Create a copy of ElevationPointDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? distance = null,Object? elevation = null,Object? grade = null,}) {
  return _then(_ElevationPointDto(
distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevation: null == elevation ? _self.elevation : elevation // ignore: cast_nullable_to_non_nullable
as double,grade: null == grade ? _self.grade : grade // ignore: cast_nullable_to_non_nullable
as double,
  ));
}


}

// dart format on
