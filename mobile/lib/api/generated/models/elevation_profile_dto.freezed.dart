// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'elevation_profile_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ElevationProfileDto {

/// Route ID (TSID)
 String get routeId;/// Route slug
 String get slug;/// Distance covered by the profile, in meters
 double get distance;/// Lowest elevation of the profile, in meters
 double get minElevation;/// Highest elevation of the profile, in meters
 double get maxElevation;/// Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.
 int get samples;/// Profile points, by increasing distance
 List<ElevationPointDto> get points;
/// Create a copy of ElevationProfileDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ElevationProfileDtoCopyWith<ElevationProfileDto> get copyWith => _$ElevationProfileDtoCopyWithImpl<ElevationProfileDto>(this as ElevationProfileDto, _$identity);

  /// Serializes this ElevationProfileDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ElevationProfileDto&&(identical(other.routeId, routeId) || other.routeId == routeId)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.minElevation, minElevation) || other.minElevation == minElevation)&&(identical(other.maxElevation, maxElevation) || other.maxElevation == maxElevation)&&(identical(other.samples, samples) || other.samples == samples)&&const DeepCollectionEquality().equals(other.points, points));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,routeId,slug,distance,minElevation,maxElevation,samples,const DeepCollectionEquality().hash(points));

@override
String toString() {
  return 'ElevationProfileDto(routeId: $routeId, slug: $slug, distance: $distance, minElevation: $minElevation, maxElevation: $maxElevation, samples: $samples, points: $points)';
}


}

/// @nodoc
abstract mixin class $ElevationProfileDtoCopyWith<$Res>  {
  factory $ElevationProfileDtoCopyWith(ElevationProfileDto value, $Res Function(ElevationProfileDto) _then) = _$ElevationProfileDtoCopyWithImpl;
@useResult
$Res call({
 String routeId, String slug, double distance, double minElevation, double maxElevation, int samples, List<ElevationPointDto> points
});




}
/// @nodoc
class _$ElevationProfileDtoCopyWithImpl<$Res>
    implements $ElevationProfileDtoCopyWith<$Res> {
  _$ElevationProfileDtoCopyWithImpl(this._self, this._then);

  final ElevationProfileDto _self;
  final $Res Function(ElevationProfileDto) _then;

/// Create a copy of ElevationProfileDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? routeId = null,Object? slug = null,Object? distance = null,Object? minElevation = null,Object? maxElevation = null,Object? samples = null,Object? points = null,}) {
  return _then(ElevationProfileDto(
routeId: null == routeId ? _self.routeId : routeId // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,minElevation: null == minElevation ? _self.minElevation : minElevation // ignore: cast_nullable_to_non_nullable
as double,maxElevation: null == maxElevation ? _self.maxElevation : maxElevation // ignore: cast_nullable_to_non_nullable
as double,samples: null == samples ? _self.samples : samples // ignore: cast_nullable_to_non_nullable
as int,points: null == points ? _self.points : points // ignore: cast_nullable_to_non_nullable
as List<ElevationPointDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [ElevationProfileDto].
extension ElevationProfileDtoPatterns on ElevationProfileDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ElevationProfileDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ElevationProfileDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ElevationProfileDto value)  $default,){
final _that = this;
switch (_that) {
case _ElevationProfileDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ElevationProfileDto value)?  $default,){
final _that = this;
switch (_that) {
case _ElevationProfileDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String routeId,  String slug,  double distance,  double minElevation,  double maxElevation,  int samples,  List<ElevationPointDto> points)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ElevationProfileDto() when $default != null:
return $default(_that.routeId,_that.slug,_that.distance,_that.minElevation,_that.maxElevation,_that.samples,_that.points);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String routeId,  String slug,  double distance,  double minElevation,  double maxElevation,  int samples,  List<ElevationPointDto> points)  $default,) {final _that = this;
switch (_that) {
case _ElevationProfileDto():
return $default(_that.routeId,_that.slug,_that.distance,_that.minElevation,_that.maxElevation,_that.samples,_that.points);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String routeId,  String slug,  double distance,  double minElevation,  double maxElevation,  int samples,  List<ElevationPointDto> points)?  $default,) {final _that = this;
switch (_that) {
case _ElevationProfileDto() when $default != null:
return $default(_that.routeId,_that.slug,_that.distance,_that.minElevation,_that.maxElevation,_that.samples,_that.points);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ElevationProfileDto implements ElevationProfileDto {
  const _ElevationProfileDto({required this.routeId, required this.slug, required this.distance, required this.minElevation, required this.maxElevation, required this.samples, required  List<ElevationPointDto> points}): _points = points;
  factory _ElevationProfileDto.fromJson(Map<String, dynamic> json) => _$ElevationProfileDtoFromJson(json);

/// Route ID (TSID)
@override final  String routeId;
/// Route slug
@override final  String slug;
/// Distance covered by the profile, in meters
@override final  double distance;
/// Lowest elevation of the profile, in meters
@override final  double minElevation;
/// Highest elevation of the profile, in meters
@override final  double maxElevation;
/// Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.
@override final  int samples;
/// Profile points, by increasing distance
 final  List<ElevationPointDto> _points;
/// Profile points, by increasing distance
@override List<ElevationPointDto> get points {
  if (_points is EqualUnmodifiableListView) return _points;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_points);
}


/// Create a copy of ElevationProfileDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ElevationProfileDtoCopyWith<_ElevationProfileDto> get copyWith => __$ElevationProfileDtoCopyWithImpl<_ElevationProfileDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ElevationProfileDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ElevationProfileDto&&(identical(other.routeId, routeId) || other.routeId == routeId)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.minElevation, minElevation) || other.minElevation == minElevation)&&(identical(other.maxElevation, maxElevation) || other.maxElevation == maxElevation)&&(identical(other.samples, samples) || other.samples == samples)&&const DeepCollectionEquality().equals(other._points, _points));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,routeId,slug,distance,minElevation,maxElevation,samples,const DeepCollectionEquality().hash(_points));

@override
String toString() {
  return 'ElevationProfileDto(routeId: $routeId, slug: $slug, distance: $distance, minElevation: $minElevation, maxElevation: $maxElevation, samples: $samples, points: $points)';
}


}

/// @nodoc
abstract mixin class _$ElevationProfileDtoCopyWith<$Res> implements $ElevationProfileDtoCopyWith<$Res> {
  factory _$ElevationProfileDtoCopyWith(_ElevationProfileDto value, $Res Function(_ElevationProfileDto) _then) = __$ElevationProfileDtoCopyWithImpl;
@override @useResult
$Res call({
 String routeId, String slug, double distance, double minElevation, double maxElevation, int samples, List<ElevationPointDto> points
});




}
/// @nodoc
class __$ElevationProfileDtoCopyWithImpl<$Res>
    implements _$ElevationProfileDtoCopyWith<$Res> {
  __$ElevationProfileDtoCopyWithImpl(this._self, this._then);

  final _ElevationProfileDto _self;
  final $Res Function(_ElevationProfileDto) _then;

/// Create a copy of ElevationProfileDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? routeId = null,Object? slug = null,Object? distance = null,Object? minElevation = null,Object? maxElevation = null,Object? samples = null,Object? points = null,}) {
  return _then(_ElevationProfileDto(
routeId: null == routeId ? _self.routeId : routeId // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,minElevation: null == minElevation ? _self.minElevation : minElevation // ignore: cast_nullable_to_non_nullable
as double,maxElevation: null == maxElevation ? _self.maxElevation : maxElevation // ignore: cast_nullable_to_non_nullable
as double,samples: null == samples ? _self.samples : samples // ignore: cast_nullable_to_non_nullable
as int,points: null == points ? _self._points : points // ignore: cast_nullable_to_non_nullable
as List<ElevationPointDto>,
  ));
}


}

// dart format on
