// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'route_detail_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouteDetailDto {

/// Route ID (TSID)
 String get id;/// Route slug
 String get slug;/// Team
 TeamPublicationDto get team;/// Route name
 String get name;/// Media
 MediaDto get media;/// Distance in meters
 double get distance;/// Total elevation gain in meters
 double get elevationGain;/// Total elevation loss in meters
 double get elevationLoss;/// Surface type
 String get surfaceType;/// Whether the route is public
 String get visibility;/// Creator user
 PublicUserDto get createdBy;/// Creation timestamp
 String get createdAt;/// Last update timestamp
 String get updatedAt;/// Tracks
 List<TrackDto> get tracks;/// Waypoints
 List<WaypointDto> get waypoints; GeoJsonPoint? get start; GeoJsonPoint? get end;
/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouteDetailDtoCopyWith<RouteDetailDto> get copyWith => _$RouteDetailDtoCopyWithImpl<RouteDetailDto>(this as RouteDetailDto, _$identity);

  /// Serializes this RouteDetailDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouteDetailDto&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.team, team) || other.team == team)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.elevationLoss, elevationLoss) || other.elevationLoss == elevationLoss)&&(identical(other.surfaceType, surfaceType) || other.surfaceType == surfaceType)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.createdBy, createdBy) || other.createdBy == createdBy)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt)&&const DeepCollectionEquality().equals(other.tracks, tracks)&&const DeepCollectionEquality().equals(other.waypoints, waypoints)&&(identical(other.start, start) || other.start == start)&&(identical(other.end, end) || other.end == end));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,slug,team,name,media,distance,elevationGain,elevationLoss,surfaceType,visibility,createdBy,createdAt,updatedAt,const DeepCollectionEquality().hash(tracks),const DeepCollectionEquality().hash(waypoints),start,end);

@override
String toString() {
  return 'RouteDetailDto(id: $id, slug: $slug, team: $team, name: $name, media: $media, distance: $distance, elevationGain: $elevationGain, elevationLoss: $elevationLoss, surfaceType: $surfaceType, visibility: $visibility, createdBy: $createdBy, createdAt: $createdAt, updatedAt: $updatedAt, tracks: $tracks, waypoints: $waypoints, start: $start, end: $end)';
}


}

/// @nodoc
abstract mixin class $RouteDetailDtoCopyWith<$Res>  {
  factory $RouteDetailDtoCopyWith(RouteDetailDto value, $Res Function(RouteDetailDto) _then) = _$RouteDetailDtoCopyWithImpl;
@useResult
$Res call({
 String id, String slug, TeamPublicationDto team, String name, MediaDto media, double distance, double elevationGain, double elevationLoss, String surfaceType, String visibility, PublicUserDto createdBy, String createdAt, String updatedAt, List<TrackDto> tracks, List<WaypointDto> waypoints, GeoJsonPoint? start, GeoJsonPoint? end
});


$TeamPublicationDtoCopyWith<$Res> get team;$MediaDtoCopyWith<$Res> get media;$PublicUserDtoCopyWith<$Res> get createdBy;$GeoJsonPointCopyWith<$Res>? get start;$GeoJsonPointCopyWith<$Res>? get end;

}
/// @nodoc
class _$RouteDetailDtoCopyWithImpl<$Res>
    implements $RouteDetailDtoCopyWith<$Res> {
  _$RouteDetailDtoCopyWithImpl(this._self, this._then);

  final RouteDetailDto _self;
  final $Res Function(RouteDetailDto) _then;

/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? slug = null,Object? team = null,Object? name = null,Object? media = null,Object? distance = null,Object? elevationGain = null,Object? elevationLoss = null,Object? surfaceType = null,Object? visibility = null,Object? createdBy = null,Object? createdAt = null,Object? updatedAt = null,Object? tracks = null,Object? waypoints = null,Object? start = freezed,Object? end = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,elevationLoss: null == elevationLoss ? _self.elevationLoss : elevationLoss // ignore: cast_nullable_to_non_nullable
as double,surfaceType: null == surfaceType ? _self.surfaceType : surfaceType // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,createdBy: null == createdBy ? _self.createdBy : createdBy // ignore: cast_nullable_to_non_nullable
as PublicUserDto,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,tracks: null == tracks ? _self.tracks : tracks // ignore: cast_nullable_to_non_nullable
as List<TrackDto>,waypoints: null == waypoints ? _self.waypoints : waypoints // ignore: cast_nullable_to_non_nullable
as List<WaypointDto>,start: freezed == start ? _self.start : start // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint?,end: freezed == end ? _self.end : end // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint?,
  ));
}
/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get createdBy {
  
  return $PublicUserDtoCopyWith<$Res>(_self.createdBy, (value) {
    return _then(_self.copyWith(createdBy: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res>? get start {
    if (_self.start == null) {
    return null;
  }

  return $GeoJsonPointCopyWith<$Res>(_self.start!, (value) {
    return _then(_self.copyWith(start: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res>? get end {
    if (_self.end == null) {
    return null;
  }

  return $GeoJsonPointCopyWith<$Res>(_self.end!, (value) {
    return _then(_self.copyWith(end: value));
  });
}
}


/// Adds pattern-matching-related methods to [RouteDetailDto].
extension RouteDetailDtoPatterns on RouteDetailDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouteDetailDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouteDetailDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouteDetailDto value)  $default,){
final _that = this;
switch (_that) {
case _RouteDetailDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouteDetailDto value)?  $default,){
final _that = this;
switch (_that) {
case _RouteDetailDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String slug,  TeamPublicationDto team,  String name,  MediaDto media,  double distance,  double elevationGain,  double elevationLoss,  String surfaceType,  String visibility,  PublicUserDto createdBy,  String createdAt,  String updatedAt,  List<TrackDto> tracks,  List<WaypointDto> waypoints,  GeoJsonPoint? start,  GeoJsonPoint? end)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouteDetailDto() when $default != null:
return $default(_that.id,_that.slug,_that.team,_that.name,_that.media,_that.distance,_that.elevationGain,_that.elevationLoss,_that.surfaceType,_that.visibility,_that.createdBy,_that.createdAt,_that.updatedAt,_that.tracks,_that.waypoints,_that.start,_that.end);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String slug,  TeamPublicationDto team,  String name,  MediaDto media,  double distance,  double elevationGain,  double elevationLoss,  String surfaceType,  String visibility,  PublicUserDto createdBy,  String createdAt,  String updatedAt,  List<TrackDto> tracks,  List<WaypointDto> waypoints,  GeoJsonPoint? start,  GeoJsonPoint? end)  $default,) {final _that = this;
switch (_that) {
case _RouteDetailDto():
return $default(_that.id,_that.slug,_that.team,_that.name,_that.media,_that.distance,_that.elevationGain,_that.elevationLoss,_that.surfaceType,_that.visibility,_that.createdBy,_that.createdAt,_that.updatedAt,_that.tracks,_that.waypoints,_that.start,_that.end);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String slug,  TeamPublicationDto team,  String name,  MediaDto media,  double distance,  double elevationGain,  double elevationLoss,  String surfaceType,  String visibility,  PublicUserDto createdBy,  String createdAt,  String updatedAt,  List<TrackDto> tracks,  List<WaypointDto> waypoints,  GeoJsonPoint? start,  GeoJsonPoint? end)?  $default,) {final _that = this;
switch (_that) {
case _RouteDetailDto() when $default != null:
return $default(_that.id,_that.slug,_that.team,_that.name,_that.media,_that.distance,_that.elevationGain,_that.elevationLoss,_that.surfaceType,_that.visibility,_that.createdBy,_that.createdAt,_that.updatedAt,_that.tracks,_that.waypoints,_that.start,_that.end);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouteDetailDto implements RouteDetailDto {
  const _RouteDetailDto({required this.id, required this.slug, required this.team, required this.name, required this.media, required this.distance, required this.elevationGain, required this.elevationLoss, required this.surfaceType, required this.visibility, required this.createdBy, required this.createdAt, required this.updatedAt, required final  List<TrackDto> tracks, required final  List<WaypointDto> waypoints, this.start, this.end}): _tracks = tracks,_waypoints = waypoints;
  factory _RouteDetailDto.fromJson(Map<String, dynamic> json) => _$RouteDetailDtoFromJson(json);

/// Route ID (TSID)
@override final  String id;
/// Route slug
@override final  String slug;
/// Team
@override final  TeamPublicationDto team;
/// Route name
@override final  String name;
/// Media
@override final  MediaDto media;
/// Distance in meters
@override final  double distance;
/// Total elevation gain in meters
@override final  double elevationGain;
/// Total elevation loss in meters
@override final  double elevationLoss;
/// Surface type
@override final  String surfaceType;
/// Whether the route is public
@override final  String visibility;
/// Creator user
@override final  PublicUserDto createdBy;
/// Creation timestamp
@override final  String createdAt;
/// Last update timestamp
@override final  String updatedAt;
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

@override final  GeoJsonPoint? start;
@override final  GeoJsonPoint? end;

/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouteDetailDtoCopyWith<_RouteDetailDto> get copyWith => __$RouteDetailDtoCopyWithImpl<_RouteDetailDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouteDetailDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouteDetailDto&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.team, team) || other.team == team)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.elevationLoss, elevationLoss) || other.elevationLoss == elevationLoss)&&(identical(other.surfaceType, surfaceType) || other.surfaceType == surfaceType)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.createdBy, createdBy) || other.createdBy == createdBy)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt)&&const DeepCollectionEquality().equals(other._tracks, _tracks)&&const DeepCollectionEquality().equals(other._waypoints, _waypoints)&&(identical(other.start, start) || other.start == start)&&(identical(other.end, end) || other.end == end));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,slug,team,name,media,distance,elevationGain,elevationLoss,surfaceType,visibility,createdBy,createdAt,updatedAt,const DeepCollectionEquality().hash(_tracks),const DeepCollectionEquality().hash(_waypoints),start,end);

@override
String toString() {
  return 'RouteDetailDto(id: $id, slug: $slug, team: $team, name: $name, media: $media, distance: $distance, elevationGain: $elevationGain, elevationLoss: $elevationLoss, surfaceType: $surfaceType, visibility: $visibility, createdBy: $createdBy, createdAt: $createdAt, updatedAt: $updatedAt, tracks: $tracks, waypoints: $waypoints, start: $start, end: $end)';
}


}

/// @nodoc
abstract mixin class _$RouteDetailDtoCopyWith<$Res> implements $RouteDetailDtoCopyWith<$Res> {
  factory _$RouteDetailDtoCopyWith(_RouteDetailDto value, $Res Function(_RouteDetailDto) _then) = __$RouteDetailDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String slug, TeamPublicationDto team, String name, MediaDto media, double distance, double elevationGain, double elevationLoss, String surfaceType, String visibility, PublicUserDto createdBy, String createdAt, String updatedAt, List<TrackDto> tracks, List<WaypointDto> waypoints, GeoJsonPoint? start, GeoJsonPoint? end
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;@override $PublicUserDtoCopyWith<$Res> get createdBy;@override $GeoJsonPointCopyWith<$Res>? get start;@override $GeoJsonPointCopyWith<$Res>? get end;

}
/// @nodoc
class __$RouteDetailDtoCopyWithImpl<$Res>
    implements _$RouteDetailDtoCopyWith<$Res> {
  __$RouteDetailDtoCopyWithImpl(this._self, this._then);

  final _RouteDetailDto _self;
  final $Res Function(_RouteDetailDto) _then;

/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? slug = null,Object? team = null,Object? name = null,Object? media = null,Object? distance = null,Object? elevationGain = null,Object? elevationLoss = null,Object? surfaceType = null,Object? visibility = null,Object? createdBy = null,Object? createdAt = null,Object? updatedAt = null,Object? tracks = null,Object? waypoints = null,Object? start = freezed,Object? end = freezed,}) {
  return _then(_RouteDetailDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,distance: null == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double,elevationGain: null == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double,elevationLoss: null == elevationLoss ? _self.elevationLoss : elevationLoss // ignore: cast_nullable_to_non_nullable
as double,surfaceType: null == surfaceType ? _self.surfaceType : surfaceType // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,createdBy: null == createdBy ? _self.createdBy : createdBy // ignore: cast_nullable_to_non_nullable
as PublicUserDto,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,tracks: null == tracks ? _self._tracks : tracks // ignore: cast_nullable_to_non_nullable
as List<TrackDto>,waypoints: null == waypoints ? _self._waypoints : waypoints // ignore: cast_nullable_to_non_nullable
as List<WaypointDto>,start: freezed == start ? _self.start : start // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint?,end: freezed == end ? _self.end : end // ignore: cast_nullable_to_non_nullable
as GeoJsonPoint?,
  ));
}

/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get createdBy {
  
  return $PublicUserDtoCopyWith<$Res>(_self.createdBy, (value) {
    return _then(_self.copyWith(createdBy: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res>? get start {
    if (_self.start == null) {
    return null;
  }

  return $GeoJsonPointCopyWith<$Res>(_self.start!, (value) {
    return _then(_self.copyWith(start: value));
  });
}/// Create a copy of RouteDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoJsonPointCopyWith<$Res>? get end {
    if (_self.end == null) {
    return null;
  }

  return $GeoJsonPointCopyWith<$Res>(_self.end!, (value) {
    return _then(_self.copyWith(end: value));
  });
}
}

// dart format on
