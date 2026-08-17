// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'trip_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TripDto {

/// Type
 String get type;/// Team
 TeamPublicationDto get team;/// Publication ID (TSID)
 String get id;/// Publication URL slug
 String get slug;/// Publication name
 String get name;/// Publication media
 MediaDto get media;/// Trip start date/time
 String get dateTime;/// Publication status
 String get status;/// Visibility level
 String get visibility;/// Number of participants
 int get participantCount;/// Number of stages
 int get stageCount;/// Trip stages
 List<TripStageDto> get stages;/// Trip participants
 List<PublicUserDto> get participants;/// Whether the trip is soft-deleted
 bool get deleted;/// Whether the current user is registered for this trip. False if anonymous.
 bool get registered;/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
 String? get excerpt;/// Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.
 String? get endDate;/// Publication timestamp
 String? get publishAt;/// Creation timestamp
 String? get createdAt;/// Route slug
 String? get routeSlug;/// Distance in metres over every stage that has a route. Null when no stage has one — an unrouted trip has no distance, which is not the same as a distance of zero.
 double? get totalDistance;/// Elevation gain in metres over every stage that has a route. Null when no stage has one.
 double? get totalElevationGain;/// Thumbnail URL (light)
 String? get thumbnailLightUrl;/// Thumbnail URL (dark)
 String? get thumbnailDarkUrl;/// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
 String? get thumbnailUrl;/// Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.
 int? get commentCount;
/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TripDtoCopyWith<TripDto> get copyWith => _$TripDtoCopyWithImpl<TripDto>(this as TripDto, _$identity);

  /// Serializes this TripDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TripDto&&(identical(other.type, type) || other.type == type)&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.participantCount, participantCount) || other.participantCount == participantCount)&&(identical(other.stageCount, stageCount) || other.stageCount == stageCount)&&const DeepCollectionEquality().equals(other.stages, stages)&&const DeepCollectionEquality().equals(other.participants, participants)&&(identical(other.deleted, deleted) || other.deleted == deleted)&&(identical(other.registered, registered) || other.registered == registered)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.totalDistance, totalDistance) || other.totalDistance == totalDistance)&&(identical(other.totalElevationGain, totalElevationGain) || other.totalElevationGain == totalElevationGain)&&(identical(other.thumbnailLightUrl, thumbnailLightUrl) || other.thumbnailLightUrl == thumbnailLightUrl)&&(identical(other.thumbnailDarkUrl, thumbnailDarkUrl) || other.thumbnailDarkUrl == thumbnailDarkUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.commentCount, commentCount) || other.commentCount == commentCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,type,team,id,slug,name,media,dateTime,status,visibility,participantCount,stageCount,const DeepCollectionEquality().hash(stages),const DeepCollectionEquality().hash(participants),deleted,registered,excerpt,endDate,publishAt,createdAt,routeSlug,totalDistance,totalElevationGain,thumbnailLightUrl,thumbnailDarkUrl,thumbnailUrl,commentCount]);

@override
String toString() {
  return 'TripDto(type: $type, team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, participantCount: $participantCount, stageCount: $stageCount, stages: $stages, participants: $participants, deleted: $deleted, registered: $registered, excerpt: $excerpt, endDate: $endDate, publishAt: $publishAt, createdAt: $createdAt, routeSlug: $routeSlug, totalDistance: $totalDistance, totalElevationGain: $totalElevationGain, thumbnailLightUrl: $thumbnailLightUrl, thumbnailDarkUrl: $thumbnailDarkUrl, thumbnailUrl: $thumbnailUrl, commentCount: $commentCount)';
}


}

/// @nodoc
abstract mixin class $TripDtoCopyWith<$Res>  {
  factory $TripDtoCopyWith(TripDto value, $Res Function(TripDto) _then) = _$TripDtoCopyWithImpl;
@useResult
$Res call({
 String type, TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, int participantCount, int stageCount, List<TripStageDto> stages, List<PublicUserDto> participants, bool deleted, bool registered, String? excerpt, String? endDate, String? publishAt, String? createdAt, String? routeSlug, double? totalDistance, double? totalElevationGain, String? thumbnailLightUrl, String? thumbnailDarkUrl, String? thumbnailUrl, int? commentCount
});


$TeamPublicationDtoCopyWith<$Res> get team;$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$TripDtoCopyWithImpl<$Res>
    implements $TripDtoCopyWith<$Res> {
  _$TripDtoCopyWithImpl(this._self, this._then);

  final TripDto _self;
  final $Res Function(TripDto) _then;

/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? participantCount = null,Object? stageCount = null,Object? stages = null,Object? participants = null,Object? deleted = null,Object? registered = null,Object? excerpt = freezed,Object? endDate = freezed,Object? publishAt = freezed,Object? createdAt = freezed,Object? routeSlug = freezed,Object? totalDistance = freezed,Object? totalElevationGain = freezed,Object? thumbnailLightUrl = freezed,Object? thumbnailDarkUrl = freezed,Object? thumbnailUrl = freezed,Object? commentCount = freezed,}) {
  return _then(TripDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,participantCount: null == participantCount ? _self.participantCount : participantCount // ignore: cast_nullable_to_non_nullable
as int,stageCount: null == stageCount ? _self.stageCount : stageCount // ignore: cast_nullable_to_non_nullable
as int,stages: null == stages ? _self.stages : stages // ignore: cast_nullable_to_non_nullable
as List<TripStageDto>,participants: null == participants ? _self.participants : participants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,deleted: null == deleted ? _self.deleted : deleted // ignore: cast_nullable_to_non_nullable
as bool,registered: null == registered ? _self.registered : registered // ignore: cast_nullable_to_non_nullable
as bool,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,endDate: freezed == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,totalDistance: freezed == totalDistance ? _self.totalDistance : totalDistance // ignore: cast_nullable_to_non_nullable
as double?,totalElevationGain: freezed == totalElevationGain ? _self.totalElevationGain : totalElevationGain // ignore: cast_nullable_to_non_nullable
as double?,thumbnailLightUrl: freezed == thumbnailLightUrl ? _self.thumbnailLightUrl : thumbnailLightUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailDarkUrl: freezed == thumbnailDarkUrl ? _self.thumbnailDarkUrl : thumbnailDarkUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,commentCount: freezed == commentCount ? _self.commentCount : commentCount // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}
/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [TripDto].
extension TripDtoPatterns on TripDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TripDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TripDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TripDto value)  $default,){
final _that = this;
switch (_that) {
case _TripDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TripDto value)?  $default,){
final _that = this;
switch (_that) {
case _TripDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int stageCount,  List<TripStageDto> stages,  List<PublicUserDto> participants,  bool deleted,  bool registered,  String? excerpt,  String? endDate,  String? publishAt,  String? createdAt,  String? routeSlug,  double? totalDistance,  double? totalElevationGain,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  int? commentCount)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TripDto() when $default != null:
return $default(_that.type,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.stageCount,_that.stages,_that.participants,_that.deleted,_that.registered,_that.excerpt,_that.endDate,_that.publishAt,_that.createdAt,_that.routeSlug,_that.totalDistance,_that.totalElevationGain,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.commentCount);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int stageCount,  List<TripStageDto> stages,  List<PublicUserDto> participants,  bool deleted,  bool registered,  String? excerpt,  String? endDate,  String? publishAt,  String? createdAt,  String? routeSlug,  double? totalDistance,  double? totalElevationGain,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  int? commentCount)  $default,) {final _that = this;
switch (_that) {
case _TripDto():
return $default(_that.type,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.stageCount,_that.stages,_that.participants,_that.deleted,_that.registered,_that.excerpt,_that.endDate,_that.publishAt,_that.createdAt,_that.routeSlug,_that.totalDistance,_that.totalElevationGain,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.commentCount);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int stageCount,  List<TripStageDto> stages,  List<PublicUserDto> participants,  bool deleted,  bool registered,  String? excerpt,  String? endDate,  String? publishAt,  String? createdAt,  String? routeSlug,  double? totalDistance,  double? totalElevationGain,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  int? commentCount)?  $default,) {final _that = this;
switch (_that) {
case _TripDto() when $default != null:
return $default(_that.type,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.stageCount,_that.stages,_that.participants,_that.deleted,_that.registered,_that.excerpt,_that.endDate,_that.publishAt,_that.createdAt,_that.routeSlug,_that.totalDistance,_that.totalElevationGain,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.commentCount);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TripDto implements TripDto {
  const _TripDto({required this.type, required this.team, required this.id, required this.slug, required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, required this.participantCount, required this.stageCount, required  List<TripStageDto> stages, required  List<PublicUserDto> participants, required this.deleted, required this.registered, this.excerpt, this.endDate, this.publishAt, this.createdAt, this.routeSlug, this.totalDistance, this.totalElevationGain, this.thumbnailLightUrl, this.thumbnailDarkUrl, this.thumbnailUrl, this.commentCount}): _stages = stages,_participants = participants;
  factory _TripDto.fromJson(Map<String, dynamic> json) => _$TripDtoFromJson(json);

/// Type
@override final  String type;
/// Team
@override final  TeamPublicationDto team;
/// Publication ID (TSID)
@override final  String id;
/// Publication URL slug
@override final  String slug;
/// Publication name
@override final  String name;
/// Publication media
@override final  MediaDto media;
/// Trip start date/time
@override final  String dateTime;
/// Publication status
@override final  String status;
/// Visibility level
@override final  String visibility;
/// Number of participants
@override final  int participantCount;
/// Number of stages
@override final  int stageCount;
/// Trip stages
 final  List<TripStageDto> _stages;
/// Trip stages
@override List<TripStageDto> get stages {
  if (_stages is EqualUnmodifiableListView) return _stages;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_stages);
}

/// Trip participants
 final  List<PublicUserDto> _participants;
/// Trip participants
@override List<PublicUserDto> get participants {
  if (_participants is EqualUnmodifiableListView) return _participants;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_participants);
}

/// Whether the trip is soft-deleted
@override final  bool deleted;
/// Whether the current user is registered for this trip. False if anonymous.
@override final  bool registered;
/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
@override final  String? excerpt;
/// Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.
@override final  String? endDate;
/// Publication timestamp
@override final  String? publishAt;
/// Creation timestamp
@override final  String? createdAt;
/// Route slug
@override final  String? routeSlug;
/// Distance in metres over every stage that has a route. Null when no stage has one — an unrouted trip has no distance, which is not the same as a distance of zero.
@override final  double? totalDistance;
/// Elevation gain in metres over every stage that has a route. Null when no stage has one.
@override final  double? totalElevationGain;
/// Thumbnail URL (light)
@override final  String? thumbnailLightUrl;
/// Thumbnail URL (dark)
@override final  String? thumbnailDarkUrl;
/// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
@override final  String? thumbnailUrl;
/// Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.
@override final  int? commentCount;

/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TripDtoCopyWith<_TripDto> get copyWith => __$TripDtoCopyWithImpl<_TripDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TripDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TripDto&&(identical(other.type, type) || other.type == type)&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.participantCount, participantCount) || other.participantCount == participantCount)&&(identical(other.stageCount, stageCount) || other.stageCount == stageCount)&&const DeepCollectionEquality().equals(other._stages, _stages)&&const DeepCollectionEquality().equals(other._participants, _participants)&&(identical(other.deleted, deleted) || other.deleted == deleted)&&(identical(other.registered, registered) || other.registered == registered)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.totalDistance, totalDistance) || other.totalDistance == totalDistance)&&(identical(other.totalElevationGain, totalElevationGain) || other.totalElevationGain == totalElevationGain)&&(identical(other.thumbnailLightUrl, thumbnailLightUrl) || other.thumbnailLightUrl == thumbnailLightUrl)&&(identical(other.thumbnailDarkUrl, thumbnailDarkUrl) || other.thumbnailDarkUrl == thumbnailDarkUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.commentCount, commentCount) || other.commentCount == commentCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,type,team,id,slug,name,media,dateTime,status,visibility,participantCount,stageCount,const DeepCollectionEquality().hash(_stages),const DeepCollectionEquality().hash(_participants),deleted,registered,excerpt,endDate,publishAt,createdAt,routeSlug,totalDistance,totalElevationGain,thumbnailLightUrl,thumbnailDarkUrl,thumbnailUrl,commentCount]);

@override
String toString() {
  return 'TripDto(type: $type, team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, participantCount: $participantCount, stageCount: $stageCount, stages: $stages, participants: $participants, deleted: $deleted, registered: $registered, excerpt: $excerpt, endDate: $endDate, publishAt: $publishAt, createdAt: $createdAt, routeSlug: $routeSlug, totalDistance: $totalDistance, totalElevationGain: $totalElevationGain, thumbnailLightUrl: $thumbnailLightUrl, thumbnailDarkUrl: $thumbnailDarkUrl, thumbnailUrl: $thumbnailUrl, commentCount: $commentCount)';
}


}

/// @nodoc
abstract mixin class _$TripDtoCopyWith<$Res> implements $TripDtoCopyWith<$Res> {
  factory _$TripDtoCopyWith(_TripDto value, $Res Function(_TripDto) _then) = __$TripDtoCopyWithImpl;
@override @useResult
$Res call({
 String type, TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, int participantCount, int stageCount, List<TripStageDto> stages, List<PublicUserDto> participants, bool deleted, bool registered, String? excerpt, String? endDate, String? publishAt, String? createdAt, String? routeSlug, double? totalDistance, double? totalElevationGain, String? thumbnailLightUrl, String? thumbnailDarkUrl, String? thumbnailUrl, int? commentCount
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class __$TripDtoCopyWithImpl<$Res>
    implements _$TripDtoCopyWith<$Res> {
  __$TripDtoCopyWithImpl(this._self, this._then);

  final _TripDto _self;
  final $Res Function(_TripDto) _then;

/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? participantCount = null,Object? stageCount = null,Object? stages = null,Object? participants = null,Object? deleted = null,Object? registered = null,Object? excerpt = freezed,Object? endDate = freezed,Object? publishAt = freezed,Object? createdAt = freezed,Object? routeSlug = freezed,Object? totalDistance = freezed,Object? totalElevationGain = freezed,Object? thumbnailLightUrl = freezed,Object? thumbnailDarkUrl = freezed,Object? thumbnailUrl = freezed,Object? commentCount = freezed,}) {
  return _then(_TripDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,participantCount: null == participantCount ? _self.participantCount : participantCount // ignore: cast_nullable_to_non_nullable
as int,stageCount: null == stageCount ? _self.stageCount : stageCount // ignore: cast_nullable_to_non_nullable
as int,stages: null == stages ? _self._stages : stages // ignore: cast_nullable_to_non_nullable
as List<TripStageDto>,participants: null == participants ? _self._participants : participants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,deleted: null == deleted ? _self.deleted : deleted // ignore: cast_nullable_to_non_nullable
as bool,registered: null == registered ? _self.registered : registered // ignore: cast_nullable_to_non_nullable
as bool,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,endDate: freezed == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,totalDistance: freezed == totalDistance ? _self.totalDistance : totalDistance // ignore: cast_nullable_to_non_nullable
as double?,totalElevationGain: freezed == totalElevationGain ? _self.totalElevationGain : totalElevationGain // ignore: cast_nullable_to_non_nullable
as double?,thumbnailLightUrl: freezed == thumbnailLightUrl ? _self.thumbnailLightUrl : thumbnailLightUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailDarkUrl: freezed == thumbnailDarkUrl ? _self.thumbnailDarkUrl : thumbnailDarkUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,commentCount: freezed == commentCount ? _self.commentCount : commentCount // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of TripDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}

// dart format on
