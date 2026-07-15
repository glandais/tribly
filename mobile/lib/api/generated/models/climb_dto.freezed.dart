// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'climb_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ClimbDto {

/// Start distance from route start in meters
 int get startDistance;/// End distance from route start in meters
 int get endDistance;/// Elevation gain in meters
 int get elevationGain;/// Average gradient percentage
 num get averageGradient;/// Maximum gradient percentage
 num get maxGradient;/// Gradient segments making up the climb
 List<ClimbPartDto> get parts;/// Climb category (HC, 1, 2, 3, 4)
 String? get category;
/// Create a copy of ClimbDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ClimbDtoCopyWith<ClimbDto> get copyWith => _$ClimbDtoCopyWithImpl<ClimbDto>(this as ClimbDto, _$identity);

  /// Serializes this ClimbDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ClimbDto&&(identical(other.startDistance, startDistance) || other.startDistance == startDistance)&&(identical(other.endDistance, endDistance) || other.endDistance == endDistance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.averageGradient, averageGradient) || other.averageGradient == averageGradient)&&(identical(other.maxGradient, maxGradient) || other.maxGradient == maxGradient)&&const DeepCollectionEquality().equals(other.parts, parts)&&(identical(other.category, category) || other.category == category));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,startDistance,endDistance,elevationGain,averageGradient,maxGradient,const DeepCollectionEquality().hash(parts),category);

@override
String toString() {
  return 'ClimbDto(startDistance: $startDistance, endDistance: $endDistance, elevationGain: $elevationGain, averageGradient: $averageGradient, maxGradient: $maxGradient, parts: $parts, category: $category)';
}


}

/// @nodoc
abstract mixin class $ClimbDtoCopyWith<$Res>  {
  factory $ClimbDtoCopyWith(ClimbDto value, $Res Function(ClimbDto) _then) = _$ClimbDtoCopyWithImpl;
@useResult
$Res call({
 int startDistance, int endDistance, int elevationGain, num averageGradient, num maxGradient, List<ClimbPartDto> parts, String? category
});




}
/// @nodoc
class _$ClimbDtoCopyWithImpl<$Res>
    implements $ClimbDtoCopyWith<$Res> {
  _$ClimbDtoCopyWithImpl(this._self, this._then);

  final ClimbDto _self;
  final $Res Function(ClimbDto) _then;

/// Create a copy of ClimbDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? startDistance = null,Object? endDistance = null,Object? elevationGain = null,Object? averageGradient = null,Object? maxGradient = null,Object? parts = null,Object? category = freezed,}) {
  return _then(_self.copyWith(
startDistance: null == startDistance ? _self.startDistance : startDistance // ignore: cast_nullable_to_non_nullable
as int,endDistance: null == endDistance ? _self.endDistance : endDistance // ignore: cast_nullable_to_non_nullable
as int,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as int,averageGradient: null == averageGradient ? _self.averageGradient : averageGradient // ignore: cast_nullable_to_non_nullable
as num,maxGradient: null == maxGradient ? _self.maxGradient : maxGradient // ignore: cast_nullable_to_non_nullable
as num,parts: null == parts ? _self.parts : parts // ignore: cast_nullable_to_non_nullable
as List<ClimbPartDto>,category: freezed == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [ClimbDto].
extension ClimbDtoPatterns on ClimbDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ClimbDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ClimbDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ClimbDto value)  $default,){
final _that = this;
switch (_that) {
case _ClimbDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ClimbDto value)?  $default,){
final _that = this;
switch (_that) {
case _ClimbDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int startDistance,  int endDistance,  int elevationGain,  num averageGradient,  num maxGradient,  List<ClimbPartDto> parts,  String? category)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ClimbDto() when $default != null:
return $default(_that.startDistance,_that.endDistance,_that.elevationGain,_that.averageGradient,_that.maxGradient,_that.parts,_that.category);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int startDistance,  int endDistance,  int elevationGain,  num averageGradient,  num maxGradient,  List<ClimbPartDto> parts,  String? category)  $default,) {final _that = this;
switch (_that) {
case _ClimbDto():
return $default(_that.startDistance,_that.endDistance,_that.elevationGain,_that.averageGradient,_that.maxGradient,_that.parts,_that.category);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int startDistance,  int endDistance,  int elevationGain,  num averageGradient,  num maxGradient,  List<ClimbPartDto> parts,  String? category)?  $default,) {final _that = this;
switch (_that) {
case _ClimbDto() when $default != null:
return $default(_that.startDistance,_that.endDistance,_that.elevationGain,_that.averageGradient,_that.maxGradient,_that.parts,_that.category);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ClimbDto implements ClimbDto {
  const _ClimbDto({required this.startDistance, required this.endDistance, required this.elevationGain, required this.averageGradient, required this.maxGradient, required final  List<ClimbPartDto> parts, this.category}): _parts = parts;
  factory _ClimbDto.fromJson(Map<String, dynamic> json) => _$ClimbDtoFromJson(json);

/// Start distance from route start in meters
@override final  int startDistance;
/// End distance from route start in meters
@override final  int endDistance;
/// Elevation gain in meters
@override final  int elevationGain;
/// Average gradient percentage
@override final  num averageGradient;
/// Maximum gradient percentage
@override final  num maxGradient;
/// Gradient segments making up the climb
 final  List<ClimbPartDto> _parts;
/// Gradient segments making up the climb
@override List<ClimbPartDto> get parts {
  if (_parts is EqualUnmodifiableListView) return _parts;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_parts);
}

/// Climb category (HC, 1, 2, 3, 4)
@override final  String? category;

/// Create a copy of ClimbDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ClimbDtoCopyWith<_ClimbDto> get copyWith => __$ClimbDtoCopyWithImpl<_ClimbDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ClimbDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ClimbDto&&(identical(other.startDistance, startDistance) || other.startDistance == startDistance)&&(identical(other.endDistance, endDistance) || other.endDistance == endDistance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.averageGradient, averageGradient) || other.averageGradient == averageGradient)&&(identical(other.maxGradient, maxGradient) || other.maxGradient == maxGradient)&&const DeepCollectionEquality().equals(other._parts, _parts)&&(identical(other.category, category) || other.category == category));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,startDistance,endDistance,elevationGain,averageGradient,maxGradient,const DeepCollectionEquality().hash(_parts),category);

@override
String toString() {
  return 'ClimbDto(startDistance: $startDistance, endDistance: $endDistance, elevationGain: $elevationGain, averageGradient: $averageGradient, maxGradient: $maxGradient, parts: $parts, category: $category)';
}


}

/// @nodoc
abstract mixin class _$ClimbDtoCopyWith<$Res> implements $ClimbDtoCopyWith<$Res> {
  factory _$ClimbDtoCopyWith(_ClimbDto value, $Res Function(_ClimbDto) _then) = __$ClimbDtoCopyWithImpl;
@override @useResult
$Res call({
 int startDistance, int endDistance, int elevationGain, num averageGradient, num maxGradient, List<ClimbPartDto> parts, String? category
});




}
/// @nodoc
class __$ClimbDtoCopyWithImpl<$Res>
    implements _$ClimbDtoCopyWith<$Res> {
  __$ClimbDtoCopyWithImpl(this._self, this._then);

  final _ClimbDto _self;
  final $Res Function(_ClimbDto) _then;

/// Create a copy of ClimbDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? startDistance = null,Object? endDistance = null,Object? elevationGain = null,Object? averageGradient = null,Object? maxGradient = null,Object? parts = null,Object? category = freezed,}) {
  return _then(_ClimbDto(
startDistance: null == startDistance ? _self.startDistance : startDistance // ignore: cast_nullable_to_non_nullable
as int,endDistance: null == endDistance ? _self.endDistance : endDistance // ignore: cast_nullable_to_non_nullable
as int,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as int,averageGradient: null == averageGradient ? _self.averageGradient : averageGradient // ignore: cast_nullable_to_non_nullable
as num,maxGradient: null == maxGradient ? _self.maxGradient : maxGradient // ignore: cast_nullable_to_non_nullable
as num,parts: null == parts ? _self._parts : parts // ignore: cast_nullable_to_non_nullable
as List<ClimbPartDto>,category: freezed == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
