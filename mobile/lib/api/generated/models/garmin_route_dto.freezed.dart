// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'garmin_route_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GarminRouteDto {

/// Team slug
 String get teamSlug;/// Route slug
 String get routeSlug;/// Route name
 String get name;/// Distance in meters
 double get distance;/// Elevation gain in meters
 double get elevationGain;/// Label (e.g., 'Rapides - Sam 18 Jan 09:00')
 String? get label;/// Ride date/time if from a ride
 String? get rideDateTime;/// Start latitude
 double? get startLat;/// Start longitude
 double? get startLon;/// Distance from user in meters (if GPS provided)
 double? get distanceFromUser;
/// Create a copy of GarminRouteDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GarminRouteDtoCopyWith<GarminRouteDto> get copyWith => _$GarminRouteDtoCopyWithImpl<GarminRouteDto>(this as GarminRouteDto, _$identity);

  /// Serializes this GarminRouteDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GarminRouteDto&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.label, label) || other.label == label)&&(identical(other.rideDateTime, rideDateTime) || other.rideDateTime == rideDateTime)&&(identical(other.startLat, startLat) || other.startLat == startLat)&&(identical(other.startLon, startLon) || other.startLon == startLon)&&(identical(other.distanceFromUser, distanceFromUser) || other.distanceFromUser == distanceFromUser));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,teamSlug,routeSlug,name,distance,elevationGain,label,rideDateTime,startLat,startLon,distanceFromUser);

@override
String toString() {
  return 'GarminRouteDto(teamSlug: $teamSlug, routeSlug: $routeSlug, name: $name, distance: $distance, elevationGain: $elevationGain, label: $label, rideDateTime: $rideDateTime, startLat: $startLat, startLon: $startLon, distanceFromUser: $distanceFromUser)';
}


}

/// @nodoc
abstract mixin class $GarminRouteDtoCopyWith<$Res>  {
  factory $GarminRouteDtoCopyWith(GarminRouteDto value, $Res Function(GarminRouteDto) _then) = _$GarminRouteDtoCopyWithImpl;
@useResult
$Res call({
 String teamSlug, String routeSlug, String name, double distance, double elevationGain, String? label, String? rideDateTime, double? startLat, double? startLon, double? distanceFromUser
});




}
/// @nodoc
class _$GarminRouteDtoCopyWithImpl<$Res>
    implements $GarminRouteDtoCopyWith<$Res> {
  _$GarminRouteDtoCopyWithImpl(this._self, this._then);

  final GarminRouteDto _self;
  final $Res Function(GarminRouteDto) _then;

/// Create a copy of GarminRouteDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teamSlug = null,Object? routeSlug = null,Object? name = null,Object? distance = null,Object? elevationGain = null,Object? label = freezed,Object? rideDateTime = freezed,Object? startLat = freezed,Object? startLon = freezed,Object? distanceFromUser = freezed,}) {
  return _then(GarminRouteDto(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,routeSlug: null == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,label: freezed == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String?,rideDateTime: freezed == rideDateTime ? _self.rideDateTime : rideDateTime // ignore: cast_nullable_to_non_nullable
as String?,startLat: freezed == startLat ? _self.startLat : startLat // ignore: cast_nullable_to_non_nullable
as double?,startLon: freezed == startLon ? _self.startLon : startLon // ignore: cast_nullable_to_non_nullable
as double?,distanceFromUser: freezed == distanceFromUser ? _self.distanceFromUser : distanceFromUser // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}

}


/// Adds pattern-matching-related methods to [GarminRouteDto].
extension GarminRouteDtoPatterns on GarminRouteDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GarminRouteDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GarminRouteDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GarminRouteDto value)  $default,){
final _that = this;
switch (_that) {
case _GarminRouteDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GarminRouteDto value)?  $default,){
final _that = this;
switch (_that) {
case _GarminRouteDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String teamSlug,  String routeSlug,  String name,  double distance,  double elevationGain,  String? label,  String? rideDateTime,  double? startLat,  double? startLon,  double? distanceFromUser)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GarminRouteDto() when $default != null:
return $default(_that.teamSlug,_that.routeSlug,_that.name,_that.distance,_that.elevationGain,_that.label,_that.rideDateTime,_that.startLat,_that.startLon,_that.distanceFromUser);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String teamSlug,  String routeSlug,  String name,  double distance,  double elevationGain,  String? label,  String? rideDateTime,  double? startLat,  double? startLon,  double? distanceFromUser)  $default,) {final _that = this;
switch (_that) {
case _GarminRouteDto():
return $default(_that.teamSlug,_that.routeSlug,_that.name,_that.distance,_that.elevationGain,_that.label,_that.rideDateTime,_that.startLat,_that.startLon,_that.distanceFromUser);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String teamSlug,  String routeSlug,  String name,  double distance,  double elevationGain,  String? label,  String? rideDateTime,  double? startLat,  double? startLon,  double? distanceFromUser)?  $default,) {final _that = this;
switch (_that) {
case _GarminRouteDto() when $default != null:
return $default(_that.teamSlug,_that.routeSlug,_that.name,_that.distance,_that.elevationGain,_that.label,_that.rideDateTime,_that.startLat,_that.startLon,_that.distanceFromUser);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GarminRouteDto implements GarminRouteDto {
  const _GarminRouteDto({required this.teamSlug, required this.routeSlug, required this.name, required this.distance, required this.elevationGain, this.label, this.rideDateTime, this.startLat, this.startLon, this.distanceFromUser});
  factory _GarminRouteDto.fromJson(Map<String, dynamic> json) => _$GarminRouteDtoFromJson(json);

/// Team slug
@override final  String teamSlug;
/// Route slug
@override final  String routeSlug;
/// Route name
@override final  String name;
/// Distance in meters
@override final  double distance;
/// Elevation gain in meters
@override final  double elevationGain;
/// Label (e.g., 'Rapides - Sam 18 Jan 09:00')
@override final  String? label;
/// Ride date/time if from a ride
@override final  String? rideDateTime;
/// Start latitude
@override final  double? startLat;
/// Start longitude
@override final  double? startLon;
/// Distance from user in meters (if GPS provided)
@override final  double? distanceFromUser;

/// Create a copy of GarminRouteDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GarminRouteDtoCopyWith<_GarminRouteDto> get copyWith => __$GarminRouteDtoCopyWithImpl<_GarminRouteDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GarminRouteDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GarminRouteDto&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.label, label) || other.label == label)&&(identical(other.rideDateTime, rideDateTime) || other.rideDateTime == rideDateTime)&&(identical(other.startLat, startLat) || other.startLat == startLat)&&(identical(other.startLon, startLon) || other.startLon == startLon)&&(identical(other.distanceFromUser, distanceFromUser) || other.distanceFromUser == distanceFromUser));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,teamSlug,routeSlug,name,distance,elevationGain,label,rideDateTime,startLat,startLon,distanceFromUser);

@override
String toString() {
  return 'GarminRouteDto(teamSlug: $teamSlug, routeSlug: $routeSlug, name: $name, distance: $distance, elevationGain: $elevationGain, label: $label, rideDateTime: $rideDateTime, startLat: $startLat, startLon: $startLon, distanceFromUser: $distanceFromUser)';
}


}

/// @nodoc
abstract mixin class _$GarminRouteDtoCopyWith<$Res> implements $GarminRouteDtoCopyWith<$Res> {
  factory _$GarminRouteDtoCopyWith(_GarminRouteDto value, $Res Function(_GarminRouteDto) _then) = __$GarminRouteDtoCopyWithImpl;
@override @useResult
$Res call({
 String teamSlug, String routeSlug, String name, double distance, double elevationGain, String? label, String? rideDateTime, double? startLat, double? startLon, double? distanceFromUser
});




}
/// @nodoc
class __$GarminRouteDtoCopyWithImpl<$Res>
    implements _$GarminRouteDtoCopyWith<$Res> {
  __$GarminRouteDtoCopyWithImpl(this._self, this._then);

  final _GarminRouteDto _self;
  final $Res Function(_GarminRouteDto) _then;

/// Create a copy of GarminRouteDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teamSlug = null,Object? routeSlug = null,Object? name = null,Object? distance = null,Object? elevationGain = null,Object? label = freezed,Object? rideDateTime = freezed,Object? startLat = freezed,Object? startLon = freezed,Object? distanceFromUser = freezed,}) {
  return _then(_GarminRouteDto(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,routeSlug: null == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,label: freezed == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String?,rideDateTime: freezed == rideDateTime ? _self.rideDateTime : rideDateTime // ignore: cast_nullable_to_non_nullable
as String?,startLat: freezed == startLat ? _self.startLat : startLat // ignore: cast_nullable_to_non_nullable
as double?,startLon: freezed == startLon ? _self.startLon : startLon // ignore: cast_nullable_to_non_nullable
as double?,distanceFromUser: freezed == distanceFromUser ? _self.distanceFromUser : distanceFromUser // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}


}

// dart format on
