// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'gpx_preview_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GpxPreviewDto {

/// Public identifier used in URLs
 String get id;/// Track name
 String get name;/// Distance in meters
 double get distance;/// Total elevation gain in meters
 double get elevationGain;/// Total elevation loss in meters
 double get elevationLoss;/// Elevation gain per kilometer
 double get hilliness;/// Whether the current user created this preview
 bool get owned;/// Creation timestamp
 String get createdAt;/// Tracks
 List<TrackDto> get tracks;/// Waypoints
 List<WaypointDto> get waypoints;
/// Create a copy of GpxPreviewDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GpxPreviewDtoCopyWith<GpxPreviewDto> get copyWith => _$GpxPreviewDtoCopyWithImpl<GpxPreviewDto>(this as GpxPreviewDto, _$identity);

  /// Serializes this GpxPreviewDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GpxPreviewDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.elevationLoss, elevationLoss) || other.elevationLoss == elevationLoss)&&(identical(other.hilliness, hilliness) || other.hilliness == hilliness)&&(identical(other.owned, owned) || other.owned == owned)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&const DeepCollectionEquality().equals(other.tracks, tracks)&&const DeepCollectionEquality().equals(other.waypoints, waypoints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,distance,elevationGain,elevationLoss,hilliness,owned,createdAt,const DeepCollectionEquality().hash(tracks),const DeepCollectionEquality().hash(waypoints));

@override
String toString() {
  return 'GpxPreviewDto(id: $id, name: $name, distance: $distance, elevationGain: $elevationGain, elevationLoss: $elevationLoss, hilliness: $hilliness, owned: $owned, createdAt: $createdAt, tracks: $tracks, waypoints: $waypoints)';
}


}

/// @nodoc
abstract mixin class $GpxPreviewDtoCopyWith<$Res>  {
  factory $GpxPreviewDtoCopyWith(GpxPreviewDto value, $Res Function(GpxPreviewDto) _then) = _$GpxPreviewDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, double distance, double elevationGain, double elevationLoss, double hilliness, bool owned, String createdAt, List<TrackDto> tracks, List<WaypointDto> waypoints
});




}
/// @nodoc
class _$GpxPreviewDtoCopyWithImpl<$Res>
    implements $GpxPreviewDtoCopyWith<$Res> {
  _$GpxPreviewDtoCopyWithImpl(this._self, this._then);

  final GpxPreviewDto _self;
  final $Res Function(GpxPreviewDto) _then;

/// Create a copy of GpxPreviewDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? distance = null,Object? elevationGain = null,Object? elevationLoss = null,Object? hilliness = null,Object? owned = null,Object? createdAt = null,Object? tracks = null,Object? waypoints = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,elevationLoss: null == elevationLoss ? _self.elevationLoss : elevationLoss // ignore: cast_nullable_to_non_nullable
as double,hilliness: null == hilliness ? _self.hilliness : hilliness // ignore: cast_nullable_to_non_nullable
as double,owned: null == owned ? _self.owned : owned // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,tracks: null == tracks ? _self.tracks : tracks // ignore: cast_nullable_to_non_nullable
as List<TrackDto>,waypoints: null == waypoints ? _self.waypoints : waypoints // ignore: cast_nullable_to_non_nullable
as List<WaypointDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [GpxPreviewDto].
extension GpxPreviewDtoPatterns on GpxPreviewDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GpxPreviewDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GpxPreviewDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GpxPreviewDto value)  $default,){
final _that = this;
switch (_that) {
case _GpxPreviewDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GpxPreviewDto value)?  $default,){
final _that = this;
switch (_that) {
case _GpxPreviewDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  double distance,  double elevationGain,  double elevationLoss,  double hilliness,  bool owned,  String createdAt,  List<TrackDto> tracks,  List<WaypointDto> waypoints)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GpxPreviewDto() when $default != null:
return $default(_that.id,_that.name,_that.distance,_that.elevationGain,_that.elevationLoss,_that.hilliness,_that.owned,_that.createdAt,_that.tracks,_that.waypoints);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  double distance,  double elevationGain,  double elevationLoss,  double hilliness,  bool owned,  String createdAt,  List<TrackDto> tracks,  List<WaypointDto> waypoints)  $default,) {final _that = this;
switch (_that) {
case _GpxPreviewDto():
return $default(_that.id,_that.name,_that.distance,_that.elevationGain,_that.elevationLoss,_that.hilliness,_that.owned,_that.createdAt,_that.tracks,_that.waypoints);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  double distance,  double elevationGain,  double elevationLoss,  double hilliness,  bool owned,  String createdAt,  List<TrackDto> tracks,  List<WaypointDto> waypoints)?  $default,) {final _that = this;
switch (_that) {
case _GpxPreviewDto() when $default != null:
return $default(_that.id,_that.name,_that.distance,_that.elevationGain,_that.elevationLoss,_that.hilliness,_that.owned,_that.createdAt,_that.tracks,_that.waypoints);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GpxPreviewDto implements GpxPreviewDto {
  const _GpxPreviewDto({required this.id, required this.name, required this.distance, required this.elevationGain, required this.elevationLoss, required this.hilliness, required this.owned, required this.createdAt, required final  List<TrackDto> tracks, required final  List<WaypointDto> waypoints}): _tracks = tracks,_waypoints = waypoints;
  factory _GpxPreviewDto.fromJson(Map<String, dynamic> json) => _$GpxPreviewDtoFromJson(json);

/// Public identifier used in URLs
@override final  String id;
/// Track name
@override final  String name;
/// Distance in meters
@override final  double distance;
/// Total elevation gain in meters
@override final  double elevationGain;
/// Total elevation loss in meters
@override final  double elevationLoss;
/// Elevation gain per kilometer
@override final  double hilliness;
/// Whether the current user created this preview
@override final  bool owned;
/// Creation timestamp
@override final  String createdAt;
/// Tracks
 final  List<TrackDto> _tracks;
/// Tracks
@override List<TrackDto> get tracks {
  if (_tracks is EqualUnmodifiableListView) return _tracks;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_tracks);
}

/// Waypoints
 final  List<WaypointDto> _waypoints;
/// Waypoints
@override List<WaypointDto> get waypoints {
  if (_waypoints is EqualUnmodifiableListView) return _waypoints;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_waypoints);
}


/// Create a copy of GpxPreviewDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GpxPreviewDtoCopyWith<_GpxPreviewDto> get copyWith => __$GpxPreviewDtoCopyWithImpl<_GpxPreviewDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GpxPreviewDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GpxPreviewDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.elevationLoss, elevationLoss) || other.elevationLoss == elevationLoss)&&(identical(other.hilliness, hilliness) || other.hilliness == hilliness)&&(identical(other.owned, owned) || other.owned == owned)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&const DeepCollectionEquality().equals(other._tracks, _tracks)&&const DeepCollectionEquality().equals(other._waypoints, _waypoints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,distance,elevationGain,elevationLoss,hilliness,owned,createdAt,const DeepCollectionEquality().hash(_tracks),const DeepCollectionEquality().hash(_waypoints));

@override
String toString() {
  return 'GpxPreviewDto(id: $id, name: $name, distance: $distance, elevationGain: $elevationGain, elevationLoss: $elevationLoss, hilliness: $hilliness, owned: $owned, createdAt: $createdAt, tracks: $tracks, waypoints: $waypoints)';
}


}

/// @nodoc
abstract mixin class _$GpxPreviewDtoCopyWith<$Res> implements $GpxPreviewDtoCopyWith<$Res> {
  factory _$GpxPreviewDtoCopyWith(_GpxPreviewDto value, $Res Function(_GpxPreviewDto) _then) = __$GpxPreviewDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, double distance, double elevationGain, double elevationLoss, double hilliness, bool owned, String createdAt, List<TrackDto> tracks, List<WaypointDto> waypoints
});




}
/// @nodoc
class __$GpxPreviewDtoCopyWithImpl<$Res>
    implements _$GpxPreviewDtoCopyWith<$Res> {
  __$GpxPreviewDtoCopyWithImpl(this._self, this._then);

  final _GpxPreviewDto _self;
  final $Res Function(_GpxPreviewDto) _then;

/// Create a copy of GpxPreviewDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? distance = null,Object? elevationGain = null,Object? elevationLoss = null,Object? hilliness = null,Object? owned = null,Object? createdAt = null,Object? tracks = null,Object? waypoints = null,}) {
  return _then(_GpxPreviewDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,elevationLoss: null == elevationLoss ? _self.elevationLoss : elevationLoss // ignore: cast_nullable_to_non_nullable
as double,hilliness: null == hilliness ? _self.hilliness : hilliness // ignore: cast_nullable_to_non_nullable
as double,owned: null == owned ? _self.owned : owned // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,tracks: null == tracks ? _self._tracks : tracks // ignore: cast_nullable_to_non_nullable
as List<TrackDto>,waypoints: null == waypoints ? _self._waypoints : waypoints // ignore: cast_nullable_to_non_nullable
as List<WaypointDto>,
  ));
}


}

// dart format on
