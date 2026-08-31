// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'climb_part_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ClimbPartDto {

/// Start distance from route start in meters
 int get startDistance;/// End distance from route start in meters
 int get endDistance;/// Elevation gain in meters
 int get elevationGain;/// Gradient percentage
 num get grade;
/// Create a copy of ClimbPartDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ClimbPartDtoCopyWith<ClimbPartDto> get copyWith => _$ClimbPartDtoCopyWithImpl<ClimbPartDto>(this as ClimbPartDto, _$identity);

  /// Serializes this ClimbPartDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ClimbPartDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ClimbPartDto&&(identical(other.startDistance, _this.startDistance) || other.startDistance == _this.startDistance)&&(identical(other.endDistance, _this.endDistance) || other.endDistance == _this.endDistance)&&(identical(other.elevationGain, _this.elevationGain) || other.elevationGain == _this.elevationGain)&&(identical(other.grade, _this.grade) || other.grade == _this.grade));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ClimbPartDto;
  return Object.hash(runtimeType,_this.startDistance,_this.endDistance,_this.elevationGain,_this.grade);
}

@override
String toString() {
  final _this = this as ClimbPartDto;
  return 'ClimbPartDto(startDistance: ${_this.startDistance}, endDistance: ${_this.endDistance}, elevationGain: ${_this.elevationGain}, grade: ${_this.grade})';
}


}

/// @nodoc
abstract mixin class $ClimbPartDtoCopyWith<$Res>  {
  factory $ClimbPartDtoCopyWith(ClimbPartDto value, $Res Function(ClimbPartDto) _then) = _$ClimbPartDtoCopyWithImpl;
@useResult
$Res call({
 int startDistance, int endDistance, int elevationGain, num grade
});




}
/// @nodoc
class _$ClimbPartDtoCopyWithImpl<$Res>
    implements $ClimbPartDtoCopyWith<$Res> {
  _$ClimbPartDtoCopyWithImpl(this._self, this._then);

  final ClimbPartDto _self;
  final $Res Function(ClimbPartDto) _then;

/// Create a copy of ClimbPartDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? startDistance = null,Object? endDistance = null,Object? elevationGain = null,Object? grade = null,}) {
  return _then(ClimbPartDto(
startDistance: null == startDistance ? _self.startDistance : startDistance // ignore: cast_nullable_to_non_nullable
as int,endDistance: null == endDistance ? _self.endDistance : endDistance // ignore: cast_nullable_to_non_nullable
as int,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as int,grade: null == grade ? _self.grade : grade // ignore: cast_nullable_to_non_nullable
as num,
  ));
}

}


/// Adds pattern-matching-related methods to [ClimbPartDto].
extension ClimbPartDtoPatterns on ClimbPartDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ClimbPartDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ClimbPartDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ClimbPartDto value)  $default,){
final _that = this;
switch (_that) {
case _ClimbPartDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ClimbPartDto value)?  $default,){
final _that = this;
switch (_that) {
case _ClimbPartDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int startDistance,  int endDistance,  int elevationGain,  num grade)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ClimbPartDto() when $default != null:
return $default(_that.startDistance,_that.endDistance,_that.elevationGain,_that.grade);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int startDistance,  int endDistance,  int elevationGain,  num grade)  $default,) {final _that = this;
switch (_that) {
case _ClimbPartDto():
return $default(_that.startDistance,_that.endDistance,_that.elevationGain,_that.grade);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int startDistance,  int endDistance,  int elevationGain,  num grade)?  $default,) {final _that = this;
switch (_that) {
case _ClimbPartDto() when $default != null:
return $default(_that.startDistance,_that.endDistance,_that.elevationGain,_that.grade);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ClimbPartDto implements ClimbPartDto {
  const _ClimbPartDto({required this.startDistance, required this.endDistance, required this.elevationGain, required this.grade});
  factory _ClimbPartDto.fromJson(Map<String, dynamic> json) => _$ClimbPartDtoFromJson(json);

/// Start distance from route start in meters
@override final  int startDistance;
/// End distance from route start in meters
@override final  int endDistance;
/// Elevation gain in meters
@override final  int elevationGain;
/// Gradient percentage
@override final  num grade;

/// Create a copy of ClimbPartDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ClimbPartDtoCopyWith<_ClimbPartDto> get copyWith => __$ClimbPartDtoCopyWithImpl<_ClimbPartDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ClimbPartDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ClimbPartDto&&(identical(other.startDistance, startDistance) || other.startDistance == startDistance)&&(identical(other.endDistance, endDistance) || other.endDistance == endDistance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.grade, grade) || other.grade == grade));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,startDistance,endDistance,elevationGain,grade);
}

@override
String toString() {
    return 'ClimbPartDto(startDistance: $startDistance, endDistance: $endDistance, elevationGain: $elevationGain, grade: $grade)';
}


}

/// @nodoc
abstract mixin class _$ClimbPartDtoCopyWith<$Res> implements $ClimbPartDtoCopyWith<$Res> {
  factory _$ClimbPartDtoCopyWith(_ClimbPartDto value, $Res Function(_ClimbPartDto) _then) = __$ClimbPartDtoCopyWithImpl;
@override @useResult
$Res call({
 int startDistance, int endDistance, int elevationGain, num grade
});




}
/// @nodoc
class __$ClimbPartDtoCopyWithImpl<$Res>
    implements _$ClimbPartDtoCopyWith<$Res> {
  __$ClimbPartDtoCopyWithImpl(this._self, this._then);

  final _ClimbPartDto _self;
  final $Res Function(_ClimbPartDto) _then;

/// Create a copy of ClimbPartDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? startDistance = null,Object? endDistance = null,Object? elevationGain = null,Object? grade = null,}) {
  return _then(_ClimbPartDto(
startDistance: null == startDistance ? _self.startDistance : startDistance // ignore: cast_nullable_to_non_nullable
as int,endDistance: null == endDistance ? _self.endDistance : endDistance // ignore: cast_nullable_to_non_nullable
as int,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as int,grade: null == grade ? _self.grade : grade // ignore: cast_nullable_to_non_nullable
as num,
  ));
}


}

// dart format on
