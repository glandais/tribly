// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'device_ride_entry_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DeviceRideEntryDto {

/// Route slug
 String get routeSlug;/// Route name
 String get routeName;/// Distance in meters
 double get distance;/// Elevation gain in meters
 double get elevationGain;/// Group name (null for ride-level route)
 String? get groupName;/// Start latitude
 double? get startLat;/// Start longitude
 double? get startLon;
/// Create a copy of DeviceRideEntryDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DeviceRideEntryDtoCopyWith<DeviceRideEntryDto> get copyWith => _$DeviceRideEntryDtoCopyWithImpl<DeviceRideEntryDto>(this as DeviceRideEntryDto, _$identity);

  /// Serializes this DeviceRideEntryDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as DeviceRideEntryDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DeviceRideEntryDto&&(identical(other.routeSlug, _this.routeSlug) || other.routeSlug == _this.routeSlug)&&(identical(other.routeName, _this.routeName) || other.routeName == _this.routeName)&&(identical(other.distance, _this.distance) || other.distance == _this.distance)&&(identical(other.elevationGain, _this.elevationGain) || other.elevationGain == _this.elevationGain)&&(identical(other.groupName, _this.groupName) || other.groupName == _this.groupName)&&(identical(other.startLat, _this.startLat) || other.startLat == _this.startLat)&&(identical(other.startLon, _this.startLon) || other.startLon == _this.startLon));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as DeviceRideEntryDto;
  return Object.hash(runtimeType,_this.routeSlug,_this.routeName,_this.distance,_this.elevationGain,_this.groupName,_this.startLat,_this.startLon);
}

@override
String toString() {
  final _this = this as DeviceRideEntryDto;
  return 'DeviceRideEntryDto(routeSlug: ${_this.routeSlug}, routeName: ${_this.routeName}, distance: ${_this.distance}, elevationGain: ${_this.elevationGain}, groupName: ${_this.groupName}, startLat: ${_this.startLat}, startLon: ${_this.startLon})';
}


}

/// @nodoc
abstract mixin class $DeviceRideEntryDtoCopyWith<$Res>  {
  factory $DeviceRideEntryDtoCopyWith(DeviceRideEntryDto value, $Res Function(DeviceRideEntryDto) _then) = _$DeviceRideEntryDtoCopyWithImpl;
@useResult
$Res call({
 String routeSlug, String routeName, double distance, double elevationGain, String? groupName, double? startLat, double? startLon
});




}
/// @nodoc
class _$DeviceRideEntryDtoCopyWithImpl<$Res>
    implements $DeviceRideEntryDtoCopyWith<$Res> {
  _$DeviceRideEntryDtoCopyWithImpl(this._self, this._then);

  final DeviceRideEntryDto _self;
  final $Res Function(DeviceRideEntryDto) _then;

/// Create a copy of DeviceRideEntryDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? routeSlug = null,Object? routeName = null,Object? distance = null,Object? elevationGain = null,Object? groupName = freezed,Object? startLat = freezed,Object? startLon = freezed,}) {
  return _then(DeviceRideEntryDto(
routeSlug: null == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String,routeName: null == routeName ? _self.routeName : routeName // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,groupName: freezed == groupName ? _self.groupName : groupName // ignore: cast_nullable_to_non_nullable
as String?,startLat: freezed == startLat ? _self.startLat : startLat // ignore: cast_nullable_to_non_nullable
as double?,startLon: freezed == startLon ? _self.startLon : startLon // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}

}


/// Adds pattern-matching-related methods to [DeviceRideEntryDto].
extension DeviceRideEntryDtoPatterns on DeviceRideEntryDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DeviceRideEntryDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DeviceRideEntryDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DeviceRideEntryDto value)  $default,){
final _that = this;
switch (_that) {
case _DeviceRideEntryDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DeviceRideEntryDto value)?  $default,){
final _that = this;
switch (_that) {
case _DeviceRideEntryDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String routeSlug,  String routeName,  double distance,  double elevationGain,  String? groupName,  double? startLat,  double? startLon)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _DeviceRideEntryDto() when $default != null:
return $default(_that.routeSlug,_that.routeName,_that.distance,_that.elevationGain,_that.groupName,_that.startLat,_that.startLon);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String routeSlug,  String routeName,  double distance,  double elevationGain,  String? groupName,  double? startLat,  double? startLon)  $default,) {final _that = this;
switch (_that) {
case _DeviceRideEntryDto():
return $default(_that.routeSlug,_that.routeName,_that.distance,_that.elevationGain,_that.groupName,_that.startLat,_that.startLon);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String routeSlug,  String routeName,  double distance,  double elevationGain,  String? groupName,  double? startLat,  double? startLon)?  $default,) {final _that = this;
switch (_that) {
case _DeviceRideEntryDto() when $default != null:
return $default(_that.routeSlug,_that.routeName,_that.distance,_that.elevationGain,_that.groupName,_that.startLat,_that.startLon);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DeviceRideEntryDto implements DeviceRideEntryDto {
  const _DeviceRideEntryDto({required this.routeSlug, required this.routeName, required this.distance, required this.elevationGain, this.groupName, this.startLat, this.startLon});
  factory _DeviceRideEntryDto.fromJson(Map<String, dynamic> json) => _$DeviceRideEntryDtoFromJson(json);

/// Route slug
@override final  String routeSlug;
/// Route name
@override final  String routeName;
/// Distance in meters
@override final  double distance;
/// Elevation gain in meters
@override final  double elevationGain;
/// Group name (null for ride-level route)
@override final  String? groupName;
/// Start latitude
@override final  double? startLat;
/// Start longitude
@override final  double? startLon;

/// Create a copy of DeviceRideEntryDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DeviceRideEntryDtoCopyWith<_DeviceRideEntryDto> get copyWith => __$DeviceRideEntryDtoCopyWithImpl<_DeviceRideEntryDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DeviceRideEntryDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _DeviceRideEntryDto&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.routeName, routeName) || other.routeName == routeName)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.groupName, groupName) || other.groupName == groupName)&&(identical(other.startLat, startLat) || other.startLat == startLat)&&(identical(other.startLon, startLon) || other.startLon == startLon));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,routeSlug,routeName,distance,elevationGain,groupName,startLat,startLon);
}

@override
String toString() {
    return 'DeviceRideEntryDto(routeSlug: $routeSlug, routeName: $routeName, distance: $distance, elevationGain: $elevationGain, groupName: $groupName, startLat: $startLat, startLon: $startLon)';
}


}

/// @nodoc
abstract mixin class _$DeviceRideEntryDtoCopyWith<$Res> implements $DeviceRideEntryDtoCopyWith<$Res> {
  factory _$DeviceRideEntryDtoCopyWith(_DeviceRideEntryDto value, $Res Function(_DeviceRideEntryDto) _then) = __$DeviceRideEntryDtoCopyWithImpl;
@override @useResult
$Res call({
 String routeSlug, String routeName, double distance, double elevationGain, String? groupName, double? startLat, double? startLon
});




}
/// @nodoc
class __$DeviceRideEntryDtoCopyWithImpl<$Res>
    implements _$DeviceRideEntryDtoCopyWith<$Res> {
  __$DeviceRideEntryDtoCopyWithImpl(this._self, this._then);

  final _DeviceRideEntryDto _self;
  final $Res Function(_DeviceRideEntryDto) _then;

/// Create a copy of DeviceRideEntryDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? routeSlug = null,Object? routeName = null,Object? distance = null,Object? elevationGain = null,Object? groupName = freezed,Object? startLat = freezed,Object? startLon = freezed,}) {
  return _then(_DeviceRideEntryDto(
routeSlug: null == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String,routeName: null == routeName ? _self.routeName : routeName // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,groupName: freezed == groupName ? _self.groupName : groupName // ignore: cast_nullable_to_non_nullable
as String?,startLat: freezed == startLat ? _self.startLat : startLat // ignore: cast_nullable_to_non_nullable
as double?,startLon: freezed == startLon ? _self.startLon : startLon // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}


}

// dart format on
