// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_group_summary_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideGroupSummaryDto {

/// Group ID (TSID)
 String get id;/// Group name
 String get name;/// When the group leaves, as RideGroupDto.startAt. Replaces time, kept for the clients that still read it.
 String get startAt;/// Current number of participants
 int get countParticipants;/// Whether the group has reached maxParticipants. False when maxParticipants is not set.
 bool get full;/// Sort order
 int get sortOrder;/// Start time of the group, when it differs from the ride's
 String? get time;/// Average speed in km/h
 double? get averageSpeed;/// Maximum participants, null when the group is uncapped
 int? get maxParticipants;/// Slug of the group route, if it has one
 String? get routeSlug;/// Distance in meters of the group route, if it has one
 double? get distance;/// Total elevation gain in meters of the group route, if it has one
 double? get elevationGain;
/// Create a copy of RideGroupSummaryDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideGroupSummaryDtoCopyWith<RideGroupSummaryDto> get copyWith => _$RideGroupSummaryDtoCopyWithImpl<RideGroupSummaryDto>(this as RideGroupSummaryDto, _$identity);

  /// Serializes this RideGroupSummaryDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as RideGroupSummaryDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideGroupSummaryDto&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.startAt, _this.startAt) || other.startAt == _this.startAt)&&(identical(other.countParticipants, _this.countParticipants) || other.countParticipants == _this.countParticipants)&&(identical(other.full, _this.full) || other.full == _this.full)&&(identical(other.sortOrder, _this.sortOrder) || other.sortOrder == _this.sortOrder)&&(identical(other.time, _this.time) || other.time == _this.time)&&(identical(other.averageSpeed, _this.averageSpeed) || other.averageSpeed == _this.averageSpeed)&&(identical(other.maxParticipants, _this.maxParticipants) || other.maxParticipants == _this.maxParticipants)&&(identical(other.routeSlug, _this.routeSlug) || other.routeSlug == _this.routeSlug)&&(identical(other.distance, _this.distance) || other.distance == _this.distance)&&(identical(other.elevationGain, _this.elevationGain) || other.elevationGain == _this.elevationGain));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as RideGroupSummaryDto;
  return Object.hash(runtimeType,_this.id,_this.name,_this.startAt,_this.countParticipants,_this.full,_this.sortOrder,_this.time,_this.averageSpeed,_this.maxParticipants,_this.routeSlug,_this.distance,_this.elevationGain);
}

@override
String toString() {
  final _this = this as RideGroupSummaryDto;
  return 'RideGroupSummaryDto(id: ${_this.id}, name: ${_this.name}, startAt: ${_this.startAt}, countParticipants: ${_this.countParticipants}, full: ${_this.full}, sortOrder: ${_this.sortOrder}, time: ${_this.time}, averageSpeed: ${_this.averageSpeed}, maxParticipants: ${_this.maxParticipants}, routeSlug: ${_this.routeSlug}, distance: ${_this.distance}, elevationGain: ${_this.elevationGain})';
}


}

/// @nodoc
abstract mixin class $RideGroupSummaryDtoCopyWith<$Res>  {
  factory $RideGroupSummaryDtoCopyWith(RideGroupSummaryDto value, $Res Function(RideGroupSummaryDto) _then) = _$RideGroupSummaryDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, String startAt, int countParticipants, bool full, int sortOrder, String? time, double? averageSpeed, int? maxParticipants, String? routeSlug, double? distance, double? elevationGain
});




}
/// @nodoc
class _$RideGroupSummaryDtoCopyWithImpl<$Res>
    implements $RideGroupSummaryDtoCopyWith<$Res> {
  _$RideGroupSummaryDtoCopyWithImpl(this._self, this._then);

  final RideGroupSummaryDto _self;
  final $Res Function(RideGroupSummaryDto) _then;

/// Create a copy of RideGroupSummaryDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? startAt = null,Object? countParticipants = null,Object? full = null,Object? sortOrder = null,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? routeSlug = freezed,Object? distance = freezed,Object? elevationGain = freezed,}) {
  return _then(RideGroupSummaryDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,startAt: null == startAt ? _self.startAt : startAt // ignore: cast_nullable_to_non_nullable
as String,countParticipants: null == countParticipants ? _self.countParticipants : countParticipants // ignore: cast_nullable_to_non_nullable
as int,full: null == full ? _self.full : full // ignore: cast_nullable_to_non_nullable
as bool,sortOrder: null == sortOrder ? _self.sortOrder : sortOrder // ignore: cast_nullable_to_non_nullable
as int,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as String?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,elevationGain: freezed == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}

}


/// Adds pattern-matching-related methods to [RideGroupSummaryDto].
extension RideGroupSummaryDtoPatterns on RideGroupSummaryDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideGroupSummaryDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideGroupSummaryDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideGroupSummaryDto value)  $default,){
final _that = this;
switch (_that) {
case _RideGroupSummaryDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideGroupSummaryDto value)?  $default,){
final _that = this;
switch (_that) {
case _RideGroupSummaryDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  String startAt,  int countParticipants,  bool full,  int sortOrder,  String? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug,  double? distance,  double? elevationGain)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideGroupSummaryDto() when $default != null:
return $default(_that.id,_that.name,_that.startAt,_that.countParticipants,_that.full,_that.sortOrder,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug,_that.distance,_that.elevationGain);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  String startAt,  int countParticipants,  bool full,  int sortOrder,  String? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug,  double? distance,  double? elevationGain)  $default,) {final _that = this;
switch (_that) {
case _RideGroupSummaryDto():
return $default(_that.id,_that.name,_that.startAt,_that.countParticipants,_that.full,_that.sortOrder,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug,_that.distance,_that.elevationGain);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  String startAt,  int countParticipants,  bool full,  int sortOrder,  String? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug,  double? distance,  double? elevationGain)?  $default,) {final _that = this;
switch (_that) {
case _RideGroupSummaryDto() when $default != null:
return $default(_that.id,_that.name,_that.startAt,_that.countParticipants,_that.full,_that.sortOrder,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug,_that.distance,_that.elevationGain);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideGroupSummaryDto implements RideGroupSummaryDto {
  const _RideGroupSummaryDto({required this.id, required this.name, required this.startAt, required this.countParticipants, required this.full, required this.sortOrder, this.time, this.averageSpeed, this.maxParticipants, this.routeSlug, this.distance, this.elevationGain});
  factory _RideGroupSummaryDto.fromJson(Map<String, dynamic> json) => _$RideGroupSummaryDtoFromJson(json);

/// Group ID (TSID)
@override final  String id;
/// Group name
@override final  String name;
/// When the group leaves, as RideGroupDto.startAt. Replaces time, kept for the clients that still read it.
@override final  String startAt;
/// Current number of participants
@override final  int countParticipants;
/// Whether the group has reached maxParticipants. False when maxParticipants is not set.
@override final  bool full;
/// Sort order
@override final  int sortOrder;
/// Start time of the group, when it differs from the ride's
@override final  String? time;
/// Average speed in km/h
@override final  double? averageSpeed;
/// Maximum participants, null when the group is uncapped
@override final  int? maxParticipants;
/// Slug of the group route, if it has one
@override final  String? routeSlug;
/// Distance in meters of the group route, if it has one
@override final  double? distance;
/// Total elevation gain in meters of the group route, if it has one
@override final  double? elevationGain;

/// Create a copy of RideGroupSummaryDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideGroupSummaryDtoCopyWith<_RideGroupSummaryDto> get copyWith => __$RideGroupSummaryDtoCopyWithImpl<_RideGroupSummaryDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideGroupSummaryDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideGroupSummaryDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.startAt, startAt) || other.startAt == startAt)&&(identical(other.countParticipants, countParticipants) || other.countParticipants == countParticipants)&&(identical(other.full, full) || other.full == full)&&(identical(other.sortOrder, sortOrder) || other.sortOrder == sortOrder)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,id,name,startAt,countParticipants,full,sortOrder,time,averageSpeed,maxParticipants,routeSlug,distance,elevationGain);
}

@override
String toString() {
    return 'RideGroupSummaryDto(id: $id, name: $name, startAt: $startAt, countParticipants: $countParticipants, full: $full, sortOrder: $sortOrder, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants, routeSlug: $routeSlug, distance: $distance, elevationGain: $elevationGain)';
}


}

/// @nodoc
abstract mixin class _$RideGroupSummaryDtoCopyWith<$Res> implements $RideGroupSummaryDtoCopyWith<$Res> {
  factory _$RideGroupSummaryDtoCopyWith(_RideGroupSummaryDto value, $Res Function(_RideGroupSummaryDto) _then) = __$RideGroupSummaryDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, String startAt, int countParticipants, bool full, int sortOrder, String? time, double? averageSpeed, int? maxParticipants, String? routeSlug, double? distance, double? elevationGain
});




}
/// @nodoc
class __$RideGroupSummaryDtoCopyWithImpl<$Res>
    implements _$RideGroupSummaryDtoCopyWith<$Res> {
  __$RideGroupSummaryDtoCopyWithImpl(this._self, this._then);

  final _RideGroupSummaryDto _self;
  final $Res Function(_RideGroupSummaryDto) _then;

/// Create a copy of RideGroupSummaryDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? startAt = null,Object? countParticipants = null,Object? full = null,Object? sortOrder = null,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? routeSlug = freezed,Object? distance = freezed,Object? elevationGain = freezed,}) {
  return _then(_RideGroupSummaryDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,startAt: null == startAt ? _self.startAt : startAt // ignore: cast_nullable_to_non_nullable
as String,countParticipants: null == countParticipants ? _self.countParticipants : countParticipants // ignore: cast_nullable_to_non_nullable
as int,full: null == full ? _self.full : full // ignore: cast_nullable_to_non_nullable
as bool,sortOrder: null == sortOrder ? _self.sortOrder : sortOrder // ignore: cast_nullable_to_non_nullable
as int,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as String?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,elevationGain: freezed == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}


}

// dart format on
