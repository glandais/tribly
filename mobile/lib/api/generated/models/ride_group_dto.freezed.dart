// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_group_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideGroupDto {

/// Group ID (TSID)
 String get id;/// Group name
 String get name;/// Current number of participants
 int get countParticipants;/// Participants, empty if not access
 List<PublicUserDto> get participants;/// Sort order
 int get sortOrder;/// Whether the current user is registered in THIS group. False if anonymous.
 bool get registered;/// Whether the group has reached maxParticipants. False when maxParticipants is not set.
 bool get full; LocalTime? get time;/// Route slug
 String? get routeSlug;/// Average speed in km/h
 double? get averageSpeed;/// Maximum participants
 int? get maxParticipants;/// Distance in meters of the group route, if it has one
 double? get distance;/// Total elevation gain in meters of the group route, if it has one
 double? get elevationGain;/// The member who leads this group, when one is designated. Null means no leader was designated — render nothing rather than falling back on the ride's creator, who is the same person on every group of the ride.
 PublicUserDto? get leader;
/// Create a copy of RideGroupDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideGroupDtoCopyWith<RideGroupDto> get copyWith => _$RideGroupDtoCopyWithImpl<RideGroupDto>(this as RideGroupDto, _$identity);

  /// Serializes this RideGroupDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideGroupDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.countParticipants, countParticipants) || other.countParticipants == countParticipants)&&const DeepCollectionEquality().equals(other.participants, participants)&&(identical(other.sortOrder, sortOrder) || other.sortOrder == sortOrder)&&(identical(other.registered, registered) || other.registered == registered)&&(identical(other.full, full) || other.full == full)&&(identical(other.time, time) || other.time == time)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.leader, leader) || other.leader == leader));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,countParticipants,const DeepCollectionEquality().hash(participants),sortOrder,registered,full,time,routeSlug,averageSpeed,maxParticipants,distance,elevationGain,leader);

@override
String toString() {
  return 'RideGroupDto(id: $id, name: $name, countParticipants: $countParticipants, participants: $participants, sortOrder: $sortOrder, registered: $registered, full: $full, time: $time, routeSlug: $routeSlug, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants, distance: $distance, elevationGain: $elevationGain, leader: $leader)';
}


}

/// @nodoc
abstract mixin class $RideGroupDtoCopyWith<$Res>  {
  factory $RideGroupDtoCopyWith(RideGroupDto value, $Res Function(RideGroupDto) _then) = _$RideGroupDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, int countParticipants, List<PublicUserDto> participants, int sortOrder, bool registered, bool full, LocalTime? time, String? routeSlug, double? averageSpeed, int? maxParticipants, double? distance, double? elevationGain, PublicUserDto? leader
});


$PublicUserDtoCopyWith<$Res>? get leader;

}
/// @nodoc
class _$RideGroupDtoCopyWithImpl<$Res>
    implements $RideGroupDtoCopyWith<$Res> {
  _$RideGroupDtoCopyWithImpl(this._self, this._then);

  final RideGroupDto _self;
  final $Res Function(RideGroupDto) _then;

/// Create a copy of RideGroupDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? countParticipants = null,Object? participants = null,Object? sortOrder = null,Object? registered = null,Object? full = null,Object? time = freezed,Object? routeSlug = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? distance = freezed,Object? elevationGain = freezed,Object? leader = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,countParticipants: null == countParticipants ? _self.countParticipants : countParticipants // ignore: cast_nullable_to_non_nullable
as int,participants: null == participants ? _self.participants : participants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,sortOrder: null == sortOrder ? _self.sortOrder : sortOrder // ignore: cast_nullable_to_non_nullable
as int,registered: null == registered ? _self.registered : registered // ignore: cast_nullable_to_non_nullable
as bool,full: null == full ? _self.full : full // ignore: cast_nullable_to_non_nullable
as bool,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,elevationGain: freezed == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double?,leader: freezed == leader ? _self.leader : leader // ignore: cast_nullable_to_non_nullable
as PublicUserDto?,
  ));
}
/// Create a copy of RideGroupDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res>? get leader {
    if (_self.leader == null) {
    return null;
  }

  return $PublicUserDtoCopyWith<$Res>(_self.leader!, (value) {
    return _then(_self.copyWith(leader: value));
  });
}
}


/// Adds pattern-matching-related methods to [RideGroupDto].
extension RideGroupDtoPatterns on RideGroupDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideGroupDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideGroupDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideGroupDto value)  $default,){
final _that = this;
switch (_that) {
case _RideGroupDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideGroupDto value)?  $default,){
final _that = this;
switch (_that) {
case _RideGroupDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  int countParticipants,  List<PublicUserDto> participants,  int sortOrder,  bool registered,  bool full,  LocalTime? time,  String? routeSlug,  double? averageSpeed,  int? maxParticipants,  double? distance,  double? elevationGain,  PublicUserDto? leader)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideGroupDto() when $default != null:
return $default(_that.id,_that.name,_that.countParticipants,_that.participants,_that.sortOrder,_that.registered,_that.full,_that.time,_that.routeSlug,_that.averageSpeed,_that.maxParticipants,_that.distance,_that.elevationGain,_that.leader);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  int countParticipants,  List<PublicUserDto> participants,  int sortOrder,  bool registered,  bool full,  LocalTime? time,  String? routeSlug,  double? averageSpeed,  int? maxParticipants,  double? distance,  double? elevationGain,  PublicUserDto? leader)  $default,) {final _that = this;
switch (_that) {
case _RideGroupDto():
return $default(_that.id,_that.name,_that.countParticipants,_that.participants,_that.sortOrder,_that.registered,_that.full,_that.time,_that.routeSlug,_that.averageSpeed,_that.maxParticipants,_that.distance,_that.elevationGain,_that.leader);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  int countParticipants,  List<PublicUserDto> participants,  int sortOrder,  bool registered,  bool full,  LocalTime? time,  String? routeSlug,  double? averageSpeed,  int? maxParticipants,  double? distance,  double? elevationGain,  PublicUserDto? leader)?  $default,) {final _that = this;
switch (_that) {
case _RideGroupDto() when $default != null:
return $default(_that.id,_that.name,_that.countParticipants,_that.participants,_that.sortOrder,_that.registered,_that.full,_that.time,_that.routeSlug,_that.averageSpeed,_that.maxParticipants,_that.distance,_that.elevationGain,_that.leader);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideGroupDto implements RideGroupDto {
  const _RideGroupDto({required this.id, required this.name, required this.countParticipants, required final  List<PublicUserDto> participants, required this.sortOrder, required this.registered, required this.full, this.time, this.routeSlug, this.averageSpeed, this.maxParticipants, this.distance, this.elevationGain, this.leader}): _participants = participants;
  factory _RideGroupDto.fromJson(Map<String, dynamic> json) => _$RideGroupDtoFromJson(json);

/// Group ID (TSID)
@override final  String id;
/// Group name
@override final  String name;
/// Current number of participants
@override final  int countParticipants;
/// Participants, empty if not access
 final  List<PublicUserDto> _participants;
/// Participants, empty if not access
@override List<PublicUserDto> get participants {
  if (_participants is EqualUnmodifiableListView) return _participants;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_participants);
}

/// Sort order
@override final  int sortOrder;
/// Whether the current user is registered in THIS group. False if anonymous.
@override final  bool registered;
/// Whether the group has reached maxParticipants. False when maxParticipants is not set.
@override final  bool full;
@override final  LocalTime? time;
/// Route slug
@override final  String? routeSlug;
/// Average speed in km/h
@override final  double? averageSpeed;
/// Maximum participants
@override final  int? maxParticipants;
/// Distance in meters of the group route, if it has one
@override final  double? distance;
/// Total elevation gain in meters of the group route, if it has one
@override final  double? elevationGain;
/// The member who leads this group, when one is designated. Null means no leader was designated — render nothing rather than falling back on the ride's creator, who is the same person on every group of the ride.
@override final  PublicUserDto? leader;

/// Create a copy of RideGroupDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideGroupDtoCopyWith<_RideGroupDto> get copyWith => __$RideGroupDtoCopyWithImpl<_RideGroupDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideGroupDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideGroupDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.countParticipants, countParticipants) || other.countParticipants == countParticipants)&&const DeepCollectionEquality().equals(other._participants, _participants)&&(identical(other.sortOrder, sortOrder) || other.sortOrder == sortOrder)&&(identical(other.registered, registered) || other.registered == registered)&&(identical(other.full, full) || other.full == full)&&(identical(other.time, time) || other.time == time)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.leader, leader) || other.leader == leader));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,countParticipants,const DeepCollectionEquality().hash(_participants),sortOrder,registered,full,time,routeSlug,averageSpeed,maxParticipants,distance,elevationGain,leader);

@override
String toString() {
  return 'RideGroupDto(id: $id, name: $name, countParticipants: $countParticipants, participants: $participants, sortOrder: $sortOrder, registered: $registered, full: $full, time: $time, routeSlug: $routeSlug, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants, distance: $distance, elevationGain: $elevationGain, leader: $leader)';
}


}

/// @nodoc
abstract mixin class _$RideGroupDtoCopyWith<$Res> implements $RideGroupDtoCopyWith<$Res> {
  factory _$RideGroupDtoCopyWith(_RideGroupDto value, $Res Function(_RideGroupDto) _then) = __$RideGroupDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, int countParticipants, List<PublicUserDto> participants, int sortOrder, bool registered, bool full, LocalTime? time, String? routeSlug, double? averageSpeed, int? maxParticipants, double? distance, double? elevationGain, PublicUserDto? leader
});


@override $PublicUserDtoCopyWith<$Res>? get leader;

}
/// @nodoc
class __$RideGroupDtoCopyWithImpl<$Res>
    implements _$RideGroupDtoCopyWith<$Res> {
  __$RideGroupDtoCopyWithImpl(this._self, this._then);

  final _RideGroupDto _self;
  final $Res Function(_RideGroupDto) _then;

/// Create a copy of RideGroupDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? countParticipants = null,Object? participants = null,Object? sortOrder = null,Object? registered = null,Object? full = null,Object? time = freezed,Object? routeSlug = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? distance = freezed,Object? elevationGain = freezed,Object? leader = freezed,}) {
  return _then(_RideGroupDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,countParticipants: null == countParticipants ? _self.countParticipants : countParticipants // ignore: cast_nullable_to_non_nullable
as int,participants: null == participants ? _self._participants : participants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,sortOrder: null == sortOrder ? _self.sortOrder : sortOrder // ignore: cast_nullable_to_non_nullable
as int,registered: null == registered ? _self.registered : registered // ignore: cast_nullable_to_non_nullable
as bool,full: null == full ? _self.full : full // ignore: cast_nullable_to_non_nullable
as bool,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,elevationGain: freezed == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double?,leader: freezed == leader ? _self.leader : leader // ignore: cast_nullable_to_non_nullable
as PublicUserDto?,
  ));
}

/// Create a copy of RideGroupDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res>? get leader {
    if (_self.leader == null) {
    return null;
  }

  return $PublicUserDtoCopyWith<$Res>(_self.leader!, (value) {
    return _then(_self.copyWith(leader: value));
  });
}
}

// dart format on
