// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'publication_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;
PublicationDto _$PublicationDtoFromJson(
  Map<String, dynamic> json
) {
        switch (json['type']) {
                  case 'RIDE':
          return PublicationDtoRide.fromJson(
            json
          );
                case 'POST':
          return PublicationDtoPost.fromJson(
            json
          );
                case 'TRIP':
          return PublicationDtoTrip.fromJson(
            json
          );
        
          default:
            throw CheckedFromJsonException(
  json,
  'type',
  'PublicationDto',
  'Invalid union type "${json['type']}"!'
);
        }
      
}

/// @nodoc
mixin _$PublicationDto {

/// Team
 TeamPublicationDto get team;/// Publication ID (TSID)
 String get id;/// Publication URL slug
 String get slug;/// Publication name
 String get name;/// Publication media
 MediaDto get media;/// Publication date/time
 String get dateTime;/// Publication status
 String get status;/// Visibility level
 String get visibility;/// Whether the ride is soft-deleted
 bool get deleted;/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
 String? get excerpt;/// Publication timestamp
 String? get publishAt;/// Creation timestamp
 String? get createdAt;/// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
 String? get thumbnailUrl;/// Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.
 int? get commentCount;
/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PublicationDtoCopyWith<PublicationDto> get copyWith => _$PublicationDtoCopyWithImpl<PublicationDto>(this as PublicationDto, _$identity);

  /// Serializes this PublicationDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PublicationDto&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.deleted, deleted) || other.deleted == deleted)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.commentCount, commentCount) || other.commentCount == commentCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,team,id,slug,name,media,dateTime,status,visibility,deleted,excerpt,publishAt,createdAt,thumbnailUrl,commentCount);

@override
String toString() {
  return 'PublicationDto(team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, deleted: $deleted, excerpt: $excerpt, publishAt: $publishAt, createdAt: $createdAt, thumbnailUrl: $thumbnailUrl, commentCount: $commentCount)';
}


}

/// @nodoc
abstract mixin class $PublicationDtoCopyWith<$Res>  {
  factory $PublicationDtoCopyWith(PublicationDto value, $Res Function(PublicationDto) _then) = _$PublicationDtoCopyWithImpl;
@useResult
$Res call({
 TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, bool deleted, String? excerpt, String? publishAt, String? createdAt, String? thumbnailUrl, int? commentCount
});


$TeamPublicationDtoCopyWith<$Res> get team;$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$PublicationDtoCopyWithImpl<$Res>
    implements $PublicationDtoCopyWith<$Res> {
  _$PublicationDtoCopyWithImpl(this._self, this._then);

  final PublicationDto _self;
  final $Res Function(PublicationDto) _then;

/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? deleted = null,Object? excerpt = freezed,Object? publishAt = freezed,Object? createdAt = freezed,Object? thumbnailUrl = freezed,Object? commentCount = freezed,}) {
  return _then(_self.copyWith(
team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,deleted: null == deleted ? _self.deleted : deleted // ignore: cast_nullable_to_non_nullable
as bool,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,commentCount: freezed == commentCount ? _self.commentCount : commentCount // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}
/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [PublicationDto].
extension PublicationDtoPatterns on PublicationDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>({TResult Function( PublicationDtoRide value)?  ride,TResult Function( PublicationDtoPost value)?  post,TResult Function( PublicationDtoTrip value)?  trip,required TResult orElse(),}){
final _that = this;
switch (_that) {
case PublicationDtoRide() when ride != null:
return ride(_that);case PublicationDtoPost() when post != null:
return post(_that);case PublicationDtoTrip() when trip != null:
return trip(_that);case _:
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

@optionalTypeArgs TResult map<TResult extends Object?>({required TResult Function( PublicationDtoRide value)  ride,required TResult Function( PublicationDtoPost value)  post,required TResult Function( PublicationDtoTrip value)  trip,}){
final _that = this;
switch (_that) {
case PublicationDtoRide():
return ride(_that);case PublicationDtoPost():
return post(_that);case PublicationDtoTrip():
return trip(_that);}
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>({TResult? Function( PublicationDtoRide value)?  ride,TResult? Function( PublicationDtoPost value)?  post,TResult? Function( PublicationDtoTrip value)?  trip,}){
final _that = this;
switch (_that) {
case PublicationDtoRide() when ride != null:
return ride(_that);case PublicationDtoPost() when post != null:
return post(_that);case PublicationDtoTrip() when trip != null:
return trip(_that);case _:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>({TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  List<PublicUserDto> topParticipants,  bool deleted,  bool registered,  bool full,  String? excerpt,  String? publishAt,  String? createdAt,  String? routeSlug,  PlaceDetailDto? startPlace,  PlaceDetailDto? endPlace,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  String? registeredGroupId,  int? commentCount)?  ride,TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  bool deleted,  String? excerpt,  String? thumbnailUrl,  String? publishAt,  String? createdAt,  int? commentCount)?  post,TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int stageCount,  List<TripStageDto> stages,  List<PublicUserDto> participants,  bool deleted,  bool registered,  String? excerpt,  String? endDate,  String? publishAt,  String? createdAt,  String? routeSlug,  double? totalDistance,  double? totalElevationGain,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  int? commentCount)?  trip,required TResult orElse(),}) {final _that = this;
switch (_that) {
case PublicationDtoRide() when ride != null:
return ride(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.groupCount,_that.groups,_that.topParticipants,_that.deleted,_that.registered,_that.full,_that.excerpt,_that.publishAt,_that.createdAt,_that.routeSlug,_that.startPlace,_that.endPlace,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.registeredGroupId,_that.commentCount);case PublicationDtoPost() when post != null:
return post(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.deleted,_that.excerpt,_that.thumbnailUrl,_that.publishAt,_that.createdAt,_that.commentCount);case PublicationDtoTrip() when trip != null:
return trip(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.stageCount,_that.stages,_that.participants,_that.deleted,_that.registered,_that.excerpt,_that.endDate,_that.publishAt,_that.createdAt,_that.routeSlug,_that.totalDistance,_that.totalElevationGain,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.commentCount);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>({required TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  List<PublicUserDto> topParticipants,  bool deleted,  bool registered,  bool full,  String? excerpt,  String? publishAt,  String? createdAt,  String? routeSlug,  PlaceDetailDto? startPlace,  PlaceDetailDto? endPlace,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  String? registeredGroupId,  int? commentCount)  ride,required TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  bool deleted,  String? excerpt,  String? thumbnailUrl,  String? publishAt,  String? createdAt,  int? commentCount)  post,required TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int stageCount,  List<TripStageDto> stages,  List<PublicUserDto> participants,  bool deleted,  bool registered,  String? excerpt,  String? endDate,  String? publishAt,  String? createdAt,  String? routeSlug,  double? totalDistance,  double? totalElevationGain,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  int? commentCount)  trip,}) {final _that = this;
switch (_that) {
case PublicationDtoRide():
return ride(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.groupCount,_that.groups,_that.topParticipants,_that.deleted,_that.registered,_that.full,_that.excerpt,_that.publishAt,_that.createdAt,_that.routeSlug,_that.startPlace,_that.endPlace,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.registeredGroupId,_that.commentCount);case PublicationDtoPost():
return post(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.deleted,_that.excerpt,_that.thumbnailUrl,_that.publishAt,_that.createdAt,_that.commentCount);case PublicationDtoTrip():
return trip(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.stageCount,_that.stages,_that.participants,_that.deleted,_that.registered,_that.excerpt,_that.endDate,_that.publishAt,_that.createdAt,_that.routeSlug,_that.totalDistance,_that.totalElevationGain,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.commentCount);}
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>({TResult? Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  List<PublicUserDto> topParticipants,  bool deleted,  bool registered,  bool full,  String? excerpt,  String? publishAt,  String? createdAt,  String? routeSlug,  PlaceDetailDto? startPlace,  PlaceDetailDto? endPlace,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  String? registeredGroupId,  int? commentCount)?  ride,TResult? Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  bool deleted,  String? excerpt,  String? thumbnailUrl,  String? publishAt,  String? createdAt,  int? commentCount)?  post,TResult? Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int stageCount,  List<TripStageDto> stages,  List<PublicUserDto> participants,  bool deleted,  bool registered,  String? excerpt,  String? endDate,  String? publishAt,  String? createdAt,  String? routeSlug,  double? totalDistance,  double? totalElevationGain,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  int? commentCount)?  trip,}) {final _that = this;
switch (_that) {
case PublicationDtoRide() when ride != null:
return ride(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.groupCount,_that.groups,_that.topParticipants,_that.deleted,_that.registered,_that.full,_that.excerpt,_that.publishAt,_that.createdAt,_that.routeSlug,_that.startPlace,_that.endPlace,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.registeredGroupId,_that.commentCount);case PublicationDtoPost() when post != null:
return post(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.deleted,_that.excerpt,_that.thumbnailUrl,_that.publishAt,_that.createdAt,_that.commentCount);case PublicationDtoTrip() when trip != null:
return trip(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.stageCount,_that.stages,_that.participants,_that.deleted,_that.registered,_that.excerpt,_that.endDate,_that.publishAt,_that.createdAt,_that.routeSlug,_that.totalDistance,_that.totalElevationGain,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.commentCount);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class PublicationDtoRide implements PublicationDto {
  const PublicationDtoRide({required this.team, required this.id, required this.slug, required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, required this.participantCount, required this.groupCount, required final  List<RideGroupDto> groups, required final  List<PublicUserDto> topParticipants, required this.deleted, required this.registered, required this.full, this.excerpt, this.publishAt, this.createdAt, this.routeSlug, this.startPlace, this.endPlace, this.thumbnailLightUrl, this.thumbnailDarkUrl, this.thumbnailUrl, this.registeredGroupId, this.commentCount, final  String? $type}): _groups = groups,_topParticipants = topParticipants,$type = $type ?? 'RIDE';
  factory PublicationDtoRide.fromJson(Map<String, dynamic> json) => _$PublicationDtoRideFromJson(json);

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
/// Publication date/time
@override final  String dateTime;
/// Publication status
@override final  String status;
/// Visibility level
@override final  String visibility;
/// Number of participants
 final  int participantCount;
/// Number of groups
 final  int groupCount;
/// Ride groups
 final  List<RideGroupDto> _groups;
/// Ride groups
 List<RideGroupDto> get groups {
  if (_groups is EqualUnmodifiableListView) return _groups;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_groups);
}

/// Preview of first participants (max 5)
 final  List<PublicUserDto> _topParticipants;
/// Preview of first participants (max 5)
 List<PublicUserDto> get topParticipants {
  if (_topParticipants is EqualUnmodifiableListView) return _topParticipants;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_topParticipants);
}

/// Whether the ride is soft-deleted
@override final  bool deleted;
/// Whether the current user is registered in one of this ride's groups. False if anonymous.
 final  bool registered;
/// Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.
 final  bool full;
/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
@override final  String? excerpt;
/// Publication timestamp
@override final  String? publishAt;
/// Creation timestamp
@override final  String? createdAt;
/// Route slug
 final  String? routeSlug;
/// Start place
 final  PlaceDetailDto? startPlace;
/// End place
 final  PlaceDetailDto? endPlace;
/// Thumbnail URL (light)
 final  String? thumbnailLightUrl;
/// Thumbnail URL (dark)
 final  String? thumbnailDarkUrl;
/// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
@override final  String? thumbnailUrl;
/// ID (TSID) of the group the current user joined, null if not registered
 final  String? registeredGroupId;
/// Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.
@override final  int? commentCount;

@JsonKey(name: 'type')
final String $type;


/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PublicationDtoRideCopyWith<PublicationDtoRide> get copyWith => _$PublicationDtoRideCopyWithImpl<PublicationDtoRide>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PublicationDtoRideToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PublicationDtoRide&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.participantCount, participantCount) || other.participantCount == participantCount)&&(identical(other.groupCount, groupCount) || other.groupCount == groupCount)&&const DeepCollectionEquality().equals(other._groups, _groups)&&const DeepCollectionEquality().equals(other._topParticipants, _topParticipants)&&(identical(other.deleted, deleted) || other.deleted == deleted)&&(identical(other.registered, registered) || other.registered == registered)&&(identical(other.full, full) || other.full == full)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace)&&(identical(other.thumbnailLightUrl, thumbnailLightUrl) || other.thumbnailLightUrl == thumbnailLightUrl)&&(identical(other.thumbnailDarkUrl, thumbnailDarkUrl) || other.thumbnailDarkUrl == thumbnailDarkUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.registeredGroupId, registeredGroupId) || other.registeredGroupId == registeredGroupId)&&(identical(other.commentCount, commentCount) || other.commentCount == commentCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,team,id,slug,name,media,dateTime,status,visibility,participantCount,groupCount,const DeepCollectionEquality().hash(_groups),const DeepCollectionEquality().hash(_topParticipants),deleted,registered,full,excerpt,publishAt,createdAt,routeSlug,startPlace,endPlace,thumbnailLightUrl,thumbnailDarkUrl,thumbnailUrl,registeredGroupId,commentCount]);

@override
String toString() {
  return 'PublicationDto.ride(team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, participantCount: $participantCount, groupCount: $groupCount, groups: $groups, topParticipants: $topParticipants, deleted: $deleted, registered: $registered, full: $full, excerpt: $excerpt, publishAt: $publishAt, createdAt: $createdAt, routeSlug: $routeSlug, startPlace: $startPlace, endPlace: $endPlace, thumbnailLightUrl: $thumbnailLightUrl, thumbnailDarkUrl: $thumbnailDarkUrl, thumbnailUrl: $thumbnailUrl, registeredGroupId: $registeredGroupId, commentCount: $commentCount)';
}


}

/// @nodoc
abstract mixin class $PublicationDtoRideCopyWith<$Res> implements $PublicationDtoCopyWith<$Res> {
  factory $PublicationDtoRideCopyWith(PublicationDtoRide value, $Res Function(PublicationDtoRide) _then) = _$PublicationDtoRideCopyWithImpl;
@override @useResult
$Res call({
 TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, int participantCount, int groupCount, List<RideGroupDto> groups, List<PublicUserDto> topParticipants, bool deleted, bool registered, bool full, String? excerpt, String? publishAt, String? createdAt, String? routeSlug, PlaceDetailDto? startPlace, PlaceDetailDto? endPlace, String? thumbnailLightUrl, String? thumbnailDarkUrl, String? thumbnailUrl, String? registeredGroupId, int? commentCount
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;$PlaceDetailDtoCopyWith<$Res>? get startPlace;$PlaceDetailDtoCopyWith<$Res>? get endPlace;

}
/// @nodoc
class _$PublicationDtoRideCopyWithImpl<$Res>
    implements $PublicationDtoRideCopyWith<$Res> {
  _$PublicationDtoRideCopyWithImpl(this._self, this._then);

  final PublicationDtoRide _self;
  final $Res Function(PublicationDtoRide) _then;

/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? participantCount = null,Object? groupCount = null,Object? groups = null,Object? topParticipants = null,Object? deleted = null,Object? registered = null,Object? full = null,Object? excerpt = freezed,Object? publishAt = freezed,Object? createdAt = freezed,Object? routeSlug = freezed,Object? startPlace = freezed,Object? endPlace = freezed,Object? thumbnailLightUrl = freezed,Object? thumbnailDarkUrl = freezed,Object? thumbnailUrl = freezed,Object? registeredGroupId = freezed,Object? commentCount = freezed,}) {
  return _then(PublicationDtoRide(
team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,participantCount: null == participantCount ? _self.participantCount : participantCount // ignore: cast_nullable_to_non_nullable
as int,groupCount: null == groupCount ? _self.groupCount : groupCount // ignore: cast_nullable_to_non_nullable
as int,groups: null == groups ? _self._groups : groups // ignore: cast_nullable_to_non_nullable
as List<RideGroupDto>,topParticipants: null == topParticipants ? _self._topParticipants : topParticipants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,deleted: null == deleted ? _self.deleted : deleted // ignore: cast_nullable_to_non_nullable
as bool,registered: null == registered ? _self.registered : registered // ignore: cast_nullable_to_non_nullable
as bool,full: null == full ? _self.full : full // ignore: cast_nullable_to_non_nullable
as bool,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,startPlace: freezed == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,endPlace: freezed == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,thumbnailLightUrl: freezed == thumbnailLightUrl ? _self.thumbnailLightUrl : thumbnailLightUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailDarkUrl: freezed == thumbnailDarkUrl ? _self.thumbnailDarkUrl : thumbnailDarkUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,registeredGroupId: freezed == registeredGroupId ? _self.registeredGroupId : registeredGroupId // ignore: cast_nullable_to_non_nullable
as String?,commentCount: freezed == commentCount ? _self.commentCount : commentCount // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PlaceDetailDtoCopyWith<$Res>? get startPlace {
    if (_self.startPlace == null) {
    return null;
  }

  return $PlaceDetailDtoCopyWith<$Res>(_self.startPlace!, (value) {
    return _then(_self.copyWith(startPlace: value));
  });
}/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PlaceDetailDtoCopyWith<$Res>? get endPlace {
    if (_self.endPlace == null) {
    return null;
  }

  return $PlaceDetailDtoCopyWith<$Res>(_self.endPlace!, (value) {
    return _then(_self.copyWith(endPlace: value));
  });
}
}

/// @nodoc
@JsonSerializable()

class PublicationDtoPost implements PublicationDto {
  const PublicationDtoPost({required this.team, required this.id, required this.slug, required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, required this.deleted, this.excerpt, this.thumbnailUrl, this.publishAt, this.createdAt, this.commentCount, final  String? $type}): $type = $type ?? 'POST';
  factory PublicationDtoPost.fromJson(Map<String, dynamic> json) => _$PublicationDtoPostFromJson(json);

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
/// Publication date/time
@override final  String dateTime;
/// Publication status
@override final  String status;
/// Visibility level
@override final  String visibility;
/// Whether the post is soft-deleted
@override final  bool deleted;
/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
@override final  String? excerpt;
/// URL template of the post's first image, the one a card shows. Saves a compact row from carrying media.assets just to find a picture.
@override final  String? thumbnailUrl;
/// Publication timestamp
@override final  String? publishAt;
/// Creation timestamp
@override final  String? createdAt;
/// Number of comments, replies included. Absent when the caller may not read the comments of this post — comments are members-only, so an outsider is told nothing, not even zero.
@override final  int? commentCount;

@JsonKey(name: 'type')
final String $type;


/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PublicationDtoPostCopyWith<PublicationDtoPost> get copyWith => _$PublicationDtoPostCopyWithImpl<PublicationDtoPost>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PublicationDtoPostToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PublicationDtoPost&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.deleted, deleted) || other.deleted == deleted)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.commentCount, commentCount) || other.commentCount == commentCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,team,id,slug,name,media,dateTime,status,visibility,deleted,excerpt,thumbnailUrl,publishAt,createdAt,commentCount);

@override
String toString() {
  return 'PublicationDto.post(team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, deleted: $deleted, excerpt: $excerpt, thumbnailUrl: $thumbnailUrl, publishAt: $publishAt, createdAt: $createdAt, commentCount: $commentCount)';
}


}

/// @nodoc
abstract mixin class $PublicationDtoPostCopyWith<$Res> implements $PublicationDtoCopyWith<$Res> {
  factory $PublicationDtoPostCopyWith(PublicationDtoPost value, $Res Function(PublicationDtoPost) _then) = _$PublicationDtoPostCopyWithImpl;
@override @useResult
$Res call({
 TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, bool deleted, String? excerpt, String? thumbnailUrl, String? publishAt, String? createdAt, int? commentCount
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$PublicationDtoPostCopyWithImpl<$Res>
    implements $PublicationDtoPostCopyWith<$Res> {
  _$PublicationDtoPostCopyWithImpl(this._self, this._then);

  final PublicationDtoPost _self;
  final $Res Function(PublicationDtoPost) _then;

/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? deleted = null,Object? excerpt = freezed,Object? thumbnailUrl = freezed,Object? publishAt = freezed,Object? createdAt = freezed,Object? commentCount = freezed,}) {
  return _then(PublicationDtoPost(
team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,deleted: null == deleted ? _self.deleted : deleted // ignore: cast_nullable_to_non_nullable
as bool,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,commentCount: freezed == commentCount ? _self.commentCount : commentCount // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}

/// @nodoc
@JsonSerializable()

class PublicationDtoTrip implements PublicationDto {
  const PublicationDtoTrip({required this.team, required this.id, required this.slug, required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, required this.participantCount, required this.stageCount, required final  List<TripStageDto> stages, required final  List<PublicUserDto> participants, required this.deleted, required this.registered, this.excerpt, this.endDate, this.publishAt, this.createdAt, this.routeSlug, this.totalDistance, this.totalElevationGain, this.thumbnailLightUrl, this.thumbnailDarkUrl, this.thumbnailUrl, this.commentCount, final  String? $type}): _stages = stages,_participants = participants,$type = $type ?? 'TRIP';
  factory PublicationDtoTrip.fromJson(Map<String, dynamic> json) => _$PublicationDtoTripFromJson(json);

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
 final  int participantCount;
/// Number of stages
 final  int stageCount;
/// Trip stages
 final  List<TripStageDto> _stages;
/// Trip stages
 List<TripStageDto> get stages {
  if (_stages is EqualUnmodifiableListView) return _stages;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_stages);
}

/// Trip participants
 final  List<PublicUserDto> _participants;
/// Trip participants
 List<PublicUserDto> get participants {
  if (_participants is EqualUnmodifiableListView) return _participants;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_participants);
}

/// Whether the trip is soft-deleted
@override final  bool deleted;
/// Whether the current user is registered for this trip. False if anonymous.
 final  bool registered;
/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
@override final  String? excerpt;
/// Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.
 final  String? endDate;
/// Publication timestamp
@override final  String? publishAt;
/// Creation timestamp
@override final  String? createdAt;
/// Route slug
 final  String? routeSlug;
/// Distance in metres over every stage that has a route. Null when no stage has one — an unrouted trip has no distance, which is not the same as a distance of zero.
 final  double? totalDistance;
/// Elevation gain in metres over every stage that has a route. Null when no stage has one.
 final  double? totalElevationGain;
/// Thumbnail URL (light)
 final  String? thumbnailLightUrl;
/// Thumbnail URL (dark)
 final  String? thumbnailDarkUrl;
/// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
@override final  String? thumbnailUrl;
/// Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.
@override final  int? commentCount;

@JsonKey(name: 'type')
final String $type;


/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PublicationDtoTripCopyWith<PublicationDtoTrip> get copyWith => _$PublicationDtoTripCopyWithImpl<PublicationDtoTrip>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PublicationDtoTripToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PublicationDtoTrip&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.participantCount, participantCount) || other.participantCount == participantCount)&&(identical(other.stageCount, stageCount) || other.stageCount == stageCount)&&const DeepCollectionEquality().equals(other._stages, _stages)&&const DeepCollectionEquality().equals(other._participants, _participants)&&(identical(other.deleted, deleted) || other.deleted == deleted)&&(identical(other.registered, registered) || other.registered == registered)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.totalDistance, totalDistance) || other.totalDistance == totalDistance)&&(identical(other.totalElevationGain, totalElevationGain) || other.totalElevationGain == totalElevationGain)&&(identical(other.thumbnailLightUrl, thumbnailLightUrl) || other.thumbnailLightUrl == thumbnailLightUrl)&&(identical(other.thumbnailDarkUrl, thumbnailDarkUrl) || other.thumbnailDarkUrl == thumbnailDarkUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.commentCount, commentCount) || other.commentCount == commentCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,team,id,slug,name,media,dateTime,status,visibility,participantCount,stageCount,const DeepCollectionEquality().hash(_stages),const DeepCollectionEquality().hash(_participants),deleted,registered,excerpt,endDate,publishAt,createdAt,routeSlug,totalDistance,totalElevationGain,thumbnailLightUrl,thumbnailDarkUrl,thumbnailUrl,commentCount]);

@override
String toString() {
  return 'PublicationDto.trip(team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, participantCount: $participantCount, stageCount: $stageCount, stages: $stages, participants: $participants, deleted: $deleted, registered: $registered, excerpt: $excerpt, endDate: $endDate, publishAt: $publishAt, createdAt: $createdAt, routeSlug: $routeSlug, totalDistance: $totalDistance, totalElevationGain: $totalElevationGain, thumbnailLightUrl: $thumbnailLightUrl, thumbnailDarkUrl: $thumbnailDarkUrl, thumbnailUrl: $thumbnailUrl, commentCount: $commentCount)';
}


}

/// @nodoc
abstract mixin class $PublicationDtoTripCopyWith<$Res> implements $PublicationDtoCopyWith<$Res> {
  factory $PublicationDtoTripCopyWith(PublicationDtoTrip value, $Res Function(PublicationDtoTrip) _then) = _$PublicationDtoTripCopyWithImpl;
@override @useResult
$Res call({
 TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, int participantCount, int stageCount, List<TripStageDto> stages, List<PublicUserDto> participants, bool deleted, bool registered, String? excerpt, String? endDate, String? publishAt, String? createdAt, String? routeSlug, double? totalDistance, double? totalElevationGain, String? thumbnailLightUrl, String? thumbnailDarkUrl, String? thumbnailUrl, int? commentCount
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$PublicationDtoTripCopyWithImpl<$Res>
    implements $PublicationDtoTripCopyWith<$Res> {
  _$PublicationDtoTripCopyWithImpl(this._self, this._then);

  final PublicationDtoTrip _self;
  final $Res Function(PublicationDtoTrip) _then;

/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? participantCount = null,Object? stageCount = null,Object? stages = null,Object? participants = null,Object? deleted = null,Object? registered = null,Object? excerpt = freezed,Object? endDate = freezed,Object? publishAt = freezed,Object? createdAt = freezed,Object? routeSlug = freezed,Object? totalDistance = freezed,Object? totalElevationGain = freezed,Object? thumbnailLightUrl = freezed,Object? thumbnailDarkUrl = freezed,Object? thumbnailUrl = freezed,Object? commentCount = freezed,}) {
  return _then(PublicationDtoTrip(
team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
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

/// Create a copy of PublicationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of PublicationDto
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
