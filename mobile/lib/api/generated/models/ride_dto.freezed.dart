// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideDto {

/// Every group of the ride in sort order, as a card shows it: name, pace, start time and fill (countParticipants against maxParticipants). Filled on list rows too, where groups is empty — a card draws its per-group fill bars without opening the ride. Carries no leader nor participants: those are on groups, in the detail.
 List<RideGroupSummaryDto> get groupSummaries;/// Team
 TeamPublicationDto get team;/// Publication ID (TSID)
 String get id;/// Publication URL slug
 String get slug;/// Publication name
 String get name;/// Publication media
 MediaDto get media;/// Publication date/time
 String get dateTime;/// Publication status
 String get status;/// Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.
 bool get finished;/// Visibility level
 String get visibility;/// Type
 String get type;/// Number of participants
 int get participantCount;/// Number of groups
 int get groupCount;/// Ride groups
 List<RideGroupDto> get groups;/// Whether the current user is registered in one of this ride's groups. False if anonymous.
 bool get registered;/// Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.
 bool get full;/// Whether the ride is soft-deleted
 bool get deleted;/// Preview of first participants (max 5)
 List<PublicUserDto> get topParticipants;/// The team's RIDE tags the ride carries, sorted by label. Empty when it carries none.
 List<TagDto> get tags;/// Total elevation gain in meters, from the same route as distance. Null when no route is set anywhere.
 double? get elevationGain;/// Surface type, from the same route as distance. Null when no route is set anywhere.
 String? get surfaceType;/// Start place
 PlaceDetailDto? get startPlace;/// Distance in meters of the ride's route — or, when the ride itself has none, of the route of its first group (in sort order) that has one. Null when no route is set anywhere.
 double? get distance;/// Creation timestamp
 String? get createdAt;/// Thumbnail URL (light)
 String? get thumbnailLightUrl;/// Thumbnail URL (dark)
 String? get thumbnailDarkUrl;/// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
 String? get thumbnailUrl;/// Route slug
 String? get routeSlug;/// Publication timestamp
 String? get publishAt;/// ID (TSID) of the group the current user joined, null if not registered
 String? get registeredGroupId;/// The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group's own, null when none was designated.
 RideGroupDto? get registeredGroup;/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
 String? get excerpt;/// Capacity of the whole ride: the sum of its groups' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.
 int? get maxParticipants;/// Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.
 int? get commentCount;/// End place
 PlaceDetailDto? get endPlace;
/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideDtoCopyWith<RideDto> get copyWith => _$RideDtoCopyWithImpl<RideDto>(this as RideDto, _$identity);

  /// Serializes this RideDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as RideDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideDto&&const DeepCollectionEquality().equals(other.groupSummaries, _this.groupSummaries)&&(identical(other.team, _this.team) || other.team == _this.team)&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.slug, _this.slug) || other.slug == _this.slug)&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.media, _this.media) || other.media == _this.media)&&(identical(other.dateTime, _this.dateTime) || other.dateTime == _this.dateTime)&&(identical(other.status, _this.status) || other.status == _this.status)&&(identical(other.finished, _this.finished) || other.finished == _this.finished)&&(identical(other.visibility, _this.visibility) || other.visibility == _this.visibility)&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.participantCount, _this.participantCount) || other.participantCount == _this.participantCount)&&(identical(other.groupCount, _this.groupCount) || other.groupCount == _this.groupCount)&&const DeepCollectionEquality().equals(other.groups, _this.groups)&&(identical(other.registered, _this.registered) || other.registered == _this.registered)&&(identical(other.full, _this.full) || other.full == _this.full)&&(identical(other.deleted, _this.deleted) || other.deleted == _this.deleted)&&const DeepCollectionEquality().equals(other.topParticipants, _this.topParticipants)&&const DeepCollectionEquality().equals(other.tags, _this.tags)&&(identical(other.elevationGain, _this.elevationGain) || other.elevationGain == _this.elevationGain)&&(identical(other.surfaceType, _this.surfaceType) || other.surfaceType == _this.surfaceType)&&(identical(other.startPlace, _this.startPlace) || other.startPlace == _this.startPlace)&&(identical(other.distance, _this.distance) || other.distance == _this.distance)&&(identical(other.createdAt, _this.createdAt) || other.createdAt == _this.createdAt)&&(identical(other.thumbnailLightUrl, _this.thumbnailLightUrl) || other.thumbnailLightUrl == _this.thumbnailLightUrl)&&(identical(other.thumbnailDarkUrl, _this.thumbnailDarkUrl) || other.thumbnailDarkUrl == _this.thumbnailDarkUrl)&&(identical(other.thumbnailUrl, _this.thumbnailUrl) || other.thumbnailUrl == _this.thumbnailUrl)&&(identical(other.routeSlug, _this.routeSlug) || other.routeSlug == _this.routeSlug)&&(identical(other.publishAt, _this.publishAt) || other.publishAt == _this.publishAt)&&(identical(other.registeredGroupId, _this.registeredGroupId) || other.registeredGroupId == _this.registeredGroupId)&&(identical(other.registeredGroup, _this.registeredGroup) || other.registeredGroup == _this.registeredGroup)&&(identical(other.excerpt, _this.excerpt) || other.excerpt == _this.excerpt)&&(identical(other.maxParticipants, _this.maxParticipants) || other.maxParticipants == _this.maxParticipants)&&(identical(other.commentCount, _this.commentCount) || other.commentCount == _this.commentCount)&&(identical(other.endPlace, _this.endPlace) || other.endPlace == _this.endPlace));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as RideDto;
  return Object.hashAll([runtimeType,const DeepCollectionEquality().hash(_this.groupSummaries),_this.team,_this.id,_this.slug,_this.name,_this.media,_this.dateTime,_this.status,_this.finished,_this.visibility,_this.type,_this.participantCount,_this.groupCount,const DeepCollectionEquality().hash(_this.groups),_this.registered,_this.full,_this.deleted,const DeepCollectionEquality().hash(_this.topParticipants),const DeepCollectionEquality().hash(_this.tags),_this.elevationGain,_this.surfaceType,_this.startPlace,_this.distance,_this.createdAt,_this.thumbnailLightUrl,_this.thumbnailDarkUrl,_this.thumbnailUrl,_this.routeSlug,_this.publishAt,_this.registeredGroupId,_this.registeredGroup,_this.excerpt,_this.maxParticipants,_this.commentCount,_this.endPlace]);
}

@override
String toString() {
  final _this = this as RideDto;
  return 'RideDto(groupSummaries: ${_this.groupSummaries}, team: ${_this.team}, id: ${_this.id}, slug: ${_this.slug}, name: ${_this.name}, media: ${_this.media}, dateTime: ${_this.dateTime}, status: ${_this.status}, finished: ${_this.finished}, visibility: ${_this.visibility}, type: ${_this.type}, participantCount: ${_this.participantCount}, groupCount: ${_this.groupCount}, groups: ${_this.groups}, registered: ${_this.registered}, full: ${_this.full}, deleted: ${_this.deleted}, topParticipants: ${_this.topParticipants}, tags: ${_this.tags}, elevationGain: ${_this.elevationGain}, surfaceType: ${_this.surfaceType}, startPlace: ${_this.startPlace}, distance: ${_this.distance}, createdAt: ${_this.createdAt}, thumbnailLightUrl: ${_this.thumbnailLightUrl}, thumbnailDarkUrl: ${_this.thumbnailDarkUrl}, thumbnailUrl: ${_this.thumbnailUrl}, routeSlug: ${_this.routeSlug}, publishAt: ${_this.publishAt}, registeredGroupId: ${_this.registeredGroupId}, registeredGroup: ${_this.registeredGroup}, excerpt: ${_this.excerpt}, maxParticipants: ${_this.maxParticipants}, commentCount: ${_this.commentCount}, endPlace: ${_this.endPlace})';
}


}

/// @nodoc
abstract mixin class $RideDtoCopyWith<$Res>  {
  factory $RideDtoCopyWith(RideDto value, $Res Function(RideDto) _then) = _$RideDtoCopyWithImpl;
@useResult
$Res call({
 List<RideGroupSummaryDto> groupSummaries, TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, bool finished, String visibility, String type, int participantCount, int groupCount, List<RideGroupDto> groups, bool registered, bool full, bool deleted, List<PublicUserDto> topParticipants, List<TagDto> tags, double? elevationGain, String? surfaceType, PlaceDetailDto? startPlace, double? distance, String? createdAt, String? thumbnailLightUrl, String? thumbnailDarkUrl, String? thumbnailUrl, String? routeSlug, String? publishAt, String? registeredGroupId, RideGroupDto? registeredGroup, String? excerpt, int? maxParticipants, int? commentCount, PlaceDetailDto? endPlace
});


$TeamPublicationDtoCopyWith<$Res> get team;$MediaDtoCopyWith<$Res> get media;$PlaceDetailDtoCopyWith<$Res>? get startPlace;$RideGroupDtoCopyWith<$Res>? get registeredGroup;$PlaceDetailDtoCopyWith<$Res>? get endPlace;

}
/// @nodoc
class _$RideDtoCopyWithImpl<$Res>
    implements $RideDtoCopyWith<$Res> {
  _$RideDtoCopyWithImpl(this._self, this._then);

  final RideDto _self;
  final $Res Function(RideDto) _then;

/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? groupSummaries = null,Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? finished = null,Object? visibility = null,Object? type = null,Object? participantCount = null,Object? groupCount = null,Object? groups = null,Object? registered = null,Object? full = null,Object? deleted = null,Object? topParticipants = null,Object? tags = null,Object? elevationGain = freezed,Object? surfaceType = freezed,Object? startPlace = freezed,Object? distance = freezed,Object? createdAt = freezed,Object? thumbnailLightUrl = freezed,Object? thumbnailDarkUrl = freezed,Object? thumbnailUrl = freezed,Object? routeSlug = freezed,Object? publishAt = freezed,Object? registeredGroupId = freezed,Object? registeredGroup = freezed,Object? excerpt = freezed,Object? maxParticipants = freezed,Object? commentCount = freezed,Object? endPlace = freezed,}) {
  return _then(RideDto(
groupSummaries: null == groupSummaries ? _self.groupSummaries : groupSummaries // ignore: cast_nullable_to_non_nullable
as List<RideGroupSummaryDto>,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,finished: null == finished ? _self.finished : finished // ignore: cast_nullable_to_non_nullable
as bool,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,participantCount: null == participantCount ? _self.participantCount : participantCount // ignore: cast_nullable_to_non_nullable
as int,groupCount: null == groupCount ? _self.groupCount : groupCount // ignore: cast_nullable_to_non_nullable
as int,groups: null == groups ? _self.groups : groups // ignore: cast_nullable_to_non_nullable
as List<RideGroupDto>,registered: null == registered ? _self.registered : registered // ignore: cast_nullable_to_non_nullable
as bool,full: null == full ? _self.full : full // ignore: cast_nullable_to_non_nullable
as bool,deleted: null == deleted ? _self.deleted : deleted // ignore: cast_nullable_to_non_nullable
as bool,topParticipants: null == topParticipants ? _self.topParticipants : topParticipants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,tags: null == tags ? _self.tags : tags // ignore: cast_nullable_to_non_nullable
as List<TagDto>,elevationGain: freezed == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double?,surfaceType: freezed == surfaceType ? _self.surfaceType : surfaceType // ignore: cast_nullable_to_non_nullable
as String?,startPlace: freezed == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,thumbnailLightUrl: freezed == thumbnailLightUrl ? _self.thumbnailLightUrl : thumbnailLightUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailDarkUrl: freezed == thumbnailDarkUrl ? _self.thumbnailDarkUrl : thumbnailDarkUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,registeredGroupId: freezed == registeredGroupId ? _self.registeredGroupId : registeredGroupId // ignore: cast_nullable_to_non_nullable
as String?,registeredGroup: freezed == registeredGroup ? _self.registeredGroup : registeredGroup // ignore: cast_nullable_to_non_nullable
as RideGroupDto?,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,commentCount: freezed == commentCount ? _self.commentCount : commentCount // ignore: cast_nullable_to_non_nullable
as int?,endPlace: freezed == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,
  ));
}
/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of RideDto
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
}/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$RideGroupDtoCopyWith<$Res>? get registeredGroup {
    if (_self.registeredGroup == null) {
    return null;
  }

  return $RideGroupDtoCopyWith<$Res>(_self.registeredGroup!, (value) {
    return _then(_self.copyWith(registeredGroup: value));
  });
}/// Create a copy of RideDto
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


/// Adds pattern-matching-related methods to [RideDto].
extension RideDtoPatterns on RideDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideDto value)  $default,){
final _that = this;
switch (_that) {
case _RideDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideDto value)?  $default,){
final _that = this;
switch (_that) {
case _RideDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<RideGroupSummaryDto> groupSummaries,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  bool finished,  String visibility,  String type,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  bool registered,  bool full,  bool deleted,  List<PublicUserDto> topParticipants,  List<TagDto> tags,  double? elevationGain,  String? surfaceType,  PlaceDetailDto? startPlace,  double? distance,  String? createdAt,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  String? routeSlug,  String? publishAt,  String? registeredGroupId,  RideGroupDto? registeredGroup,  String? excerpt,  int? maxParticipants,  int? commentCount,  PlaceDetailDto? endPlace)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideDto() when $default != null:
return $default(_that.groupSummaries,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.finished,_that.visibility,_that.type,_that.participantCount,_that.groupCount,_that.groups,_that.registered,_that.full,_that.deleted,_that.topParticipants,_that.tags,_that.elevationGain,_that.surfaceType,_that.startPlace,_that.distance,_that.createdAt,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.routeSlug,_that.publishAt,_that.registeredGroupId,_that.registeredGroup,_that.excerpt,_that.maxParticipants,_that.commentCount,_that.endPlace);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<RideGroupSummaryDto> groupSummaries,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  bool finished,  String visibility,  String type,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  bool registered,  bool full,  bool deleted,  List<PublicUserDto> topParticipants,  List<TagDto> tags,  double? elevationGain,  String? surfaceType,  PlaceDetailDto? startPlace,  double? distance,  String? createdAt,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  String? routeSlug,  String? publishAt,  String? registeredGroupId,  RideGroupDto? registeredGroup,  String? excerpt,  int? maxParticipants,  int? commentCount,  PlaceDetailDto? endPlace)  $default,) {final _that = this;
switch (_that) {
case _RideDto():
return $default(_that.groupSummaries,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.finished,_that.visibility,_that.type,_that.participantCount,_that.groupCount,_that.groups,_that.registered,_that.full,_that.deleted,_that.topParticipants,_that.tags,_that.elevationGain,_that.surfaceType,_that.startPlace,_that.distance,_that.createdAt,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.routeSlug,_that.publishAt,_that.registeredGroupId,_that.registeredGroup,_that.excerpt,_that.maxParticipants,_that.commentCount,_that.endPlace);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<RideGroupSummaryDto> groupSummaries,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  bool finished,  String visibility,  String type,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  bool registered,  bool full,  bool deleted,  List<PublicUserDto> topParticipants,  List<TagDto> tags,  double? elevationGain,  String? surfaceType,  PlaceDetailDto? startPlace,  double? distance,  String? createdAt,  String? thumbnailLightUrl,  String? thumbnailDarkUrl,  String? thumbnailUrl,  String? routeSlug,  String? publishAt,  String? registeredGroupId,  RideGroupDto? registeredGroup,  String? excerpt,  int? maxParticipants,  int? commentCount,  PlaceDetailDto? endPlace)?  $default,) {final _that = this;
switch (_that) {
case _RideDto() when $default != null:
return $default(_that.groupSummaries,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.finished,_that.visibility,_that.type,_that.participantCount,_that.groupCount,_that.groups,_that.registered,_that.full,_that.deleted,_that.topParticipants,_that.tags,_that.elevationGain,_that.surfaceType,_that.startPlace,_that.distance,_that.createdAt,_that.thumbnailLightUrl,_that.thumbnailDarkUrl,_that.thumbnailUrl,_that.routeSlug,_that.publishAt,_that.registeredGroupId,_that.registeredGroup,_that.excerpt,_that.maxParticipants,_that.commentCount,_that.endPlace);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideDto implements RideDto {
  const _RideDto({required  List<RideGroupSummaryDto> groupSummaries, required this.team, required this.id, required this.slug, required this.name, required this.media, required this.dateTime, required this.status, required this.finished, required this.visibility, required this.type, required this.participantCount, required this.groupCount, required  List<RideGroupDto> groups, required this.registered, required this.full, required this.deleted, required  List<PublicUserDto> topParticipants, required  List<TagDto> tags, this.elevationGain, this.surfaceType, this.startPlace, this.distance, this.createdAt, this.thumbnailLightUrl, this.thumbnailDarkUrl, this.thumbnailUrl, this.routeSlug, this.publishAt, this.registeredGroupId, this.registeredGroup, this.excerpt, this.maxParticipants, this.commentCount, this.endPlace}): _groupSummaries = groupSummaries,_groups = groups,_topParticipants = topParticipants,_tags = tags;
  factory _RideDto.fromJson(Map<String, dynamic> json) => _$RideDtoFromJson(json);

/// Every group of the ride in sort order, as a card shows it: name, pace, start time and fill (countParticipants against maxParticipants). Filled on list rows too, where groups is empty — a card draws its per-group fill bars without opening the ride. Carries no leader nor participants: those are on groups, in the detail.
 final  List<RideGroupSummaryDto> _groupSummaries;
/// Every group of the ride in sort order, as a card shows it: name, pace, start time and fill (countParticipants against maxParticipants). Filled on list rows too, where groups is empty — a card draws its per-group fill bars without opening the ride. Carries no leader nor participants: those are on groups, in the detail.
@override List<RideGroupSummaryDto> get groupSummaries {
  if (_groupSummaries is EqualUnmodifiableListView) return _groupSummaries;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_groupSummaries);
}

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
/// Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.
@override final  bool finished;
/// Visibility level
@override final  String visibility;
/// Type
@override final  String type;
/// Number of participants
@override final  int participantCount;
/// Number of groups
@override final  int groupCount;
/// Ride groups
 final  List<RideGroupDto> _groups;
/// Ride groups
@override List<RideGroupDto> get groups {
  if (_groups is EqualUnmodifiableListView) return _groups;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_groups);
}

/// Whether the current user is registered in one of this ride's groups. False if anonymous.
@override final  bool registered;
/// Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.
@override final  bool full;
/// Whether the ride is soft-deleted
@override final  bool deleted;
/// Preview of first participants (max 5)
 final  List<PublicUserDto> _topParticipants;
/// Preview of first participants (max 5)
@override List<PublicUserDto> get topParticipants {
  if (_topParticipants is EqualUnmodifiableListView) return _topParticipants;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_topParticipants);
}

/// The team's RIDE tags the ride carries, sorted by label. Empty when it carries none.
 final  List<TagDto> _tags;
/// The team's RIDE tags the ride carries, sorted by label. Empty when it carries none.
@override List<TagDto> get tags {
  if (_tags is EqualUnmodifiableListView) return _tags;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_tags);
}

/// Total elevation gain in meters, from the same route as distance. Null when no route is set anywhere.
@override final  double? elevationGain;
/// Surface type, from the same route as distance. Null when no route is set anywhere.
@override final  String? surfaceType;
/// Start place
@override final  PlaceDetailDto? startPlace;
/// Distance in meters of the ride's route — or, when the ride itself has none, of the route of its first group (in sort order) that has one. Null when no route is set anywhere.
@override final  double? distance;
/// Creation timestamp
@override final  String? createdAt;
/// Thumbnail URL (light)
@override final  String? thumbnailLightUrl;
/// Thumbnail URL (dark)
@override final  String? thumbnailDarkUrl;
/// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
@override final  String? thumbnailUrl;
/// Route slug
@override final  String? routeSlug;
/// Publication timestamp
@override final  String? publishAt;
/// ID (TSID) of the group the current user joined, null if not registered
@override final  String? registeredGroupId;
/// The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group's own, null when none was designated.
@override final  RideGroupDto? registeredGroup;
/// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
@override final  String? excerpt;
/// Capacity of the whole ride: the sum of its groups' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.
@override final  int? maxParticipants;
/// Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.
@override final  int? commentCount;
/// End place
@override final  PlaceDetailDto? endPlace;

/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideDtoCopyWith<_RideDto> get copyWith => __$RideDtoCopyWithImpl<_RideDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideDto&&const DeepCollectionEquality().equals(other.groupSummaries, _groupSummaries)&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.finished, finished) || other.finished == finished)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.type, type) || other.type == type)&&(identical(other.participantCount, participantCount) || other.participantCount == participantCount)&&(identical(other.groupCount, groupCount) || other.groupCount == groupCount)&&const DeepCollectionEquality().equals(other.groups, _groups)&&(identical(other.registered, registered) || other.registered == registered)&&(identical(other.full, full) || other.full == full)&&(identical(other.deleted, deleted) || other.deleted == deleted)&&const DeepCollectionEquality().equals(other.topParticipants, _topParticipants)&&const DeepCollectionEquality().equals(other.tags, _tags)&&(identical(other.elevationGain, elevationGain) || other.elevationGain == elevationGain)&&(identical(other.surfaceType, surfaceType) || other.surfaceType == surfaceType)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.distance, distance) || other.distance == distance)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.thumbnailLightUrl, thumbnailLightUrl) || other.thumbnailLightUrl == thumbnailLightUrl)&&(identical(other.thumbnailDarkUrl, thumbnailDarkUrl) || other.thumbnailDarkUrl == thumbnailDarkUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.registeredGroupId, registeredGroupId) || other.registeredGroupId == registeredGroupId)&&(identical(other.registeredGroup, registeredGroup) || other.registeredGroup == registeredGroup)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants)&&(identical(other.commentCount, commentCount) || other.commentCount == commentCount)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hashAll([runtimeType,const DeepCollectionEquality().hash(_groupSummaries),team,id,slug,name,media,dateTime,status,finished,visibility,type,participantCount,groupCount,const DeepCollectionEquality().hash(_groups),registered,full,deleted,const DeepCollectionEquality().hash(_topParticipants),const DeepCollectionEquality().hash(_tags),elevationGain,surfaceType,startPlace,distance,createdAt,thumbnailLightUrl,thumbnailDarkUrl,thumbnailUrl,routeSlug,publishAt,registeredGroupId,registeredGroup,excerpt,maxParticipants,commentCount,endPlace]);
}

@override
String toString() {
    return 'RideDto(groupSummaries: $groupSummaries, team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, finished: $finished, visibility: $visibility, type: $type, participantCount: $participantCount, groupCount: $groupCount, groups: $groups, registered: $registered, full: $full, deleted: $deleted, topParticipants: $topParticipants, tags: $tags, elevationGain: $elevationGain, surfaceType: $surfaceType, startPlace: $startPlace, distance: $distance, createdAt: $createdAt, thumbnailLightUrl: $thumbnailLightUrl, thumbnailDarkUrl: $thumbnailDarkUrl, thumbnailUrl: $thumbnailUrl, routeSlug: $routeSlug, publishAt: $publishAt, registeredGroupId: $registeredGroupId, registeredGroup: $registeredGroup, excerpt: $excerpt, maxParticipants: $maxParticipants, commentCount: $commentCount, endPlace: $endPlace)';
}


}

/// @nodoc
abstract mixin class _$RideDtoCopyWith<$Res> implements $RideDtoCopyWith<$Res> {
  factory _$RideDtoCopyWith(_RideDto value, $Res Function(_RideDto) _then) = __$RideDtoCopyWithImpl;
@override @useResult
$Res call({
 List<RideGroupSummaryDto> groupSummaries, TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, bool finished, String visibility, String type, int participantCount, int groupCount, List<RideGroupDto> groups, bool registered, bool full, bool deleted, List<PublicUserDto> topParticipants, List<TagDto> tags, double? elevationGain, String? surfaceType, PlaceDetailDto? startPlace, double? distance, String? createdAt, String? thumbnailLightUrl, String? thumbnailDarkUrl, String? thumbnailUrl, String? routeSlug, String? publishAt, String? registeredGroupId, RideGroupDto? registeredGroup, String? excerpt, int? maxParticipants, int? commentCount, PlaceDetailDto? endPlace
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;@override $PlaceDetailDtoCopyWith<$Res>? get startPlace;@override $RideGroupDtoCopyWith<$Res>? get registeredGroup;@override $PlaceDetailDtoCopyWith<$Res>? get endPlace;

}
/// @nodoc
class __$RideDtoCopyWithImpl<$Res>
    implements _$RideDtoCopyWith<$Res> {
  __$RideDtoCopyWithImpl(this._self, this._then);

  final _RideDto _self;
  final $Res Function(_RideDto) _then;

/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? groupSummaries = null,Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? finished = null,Object? visibility = null,Object? type = null,Object? participantCount = null,Object? groupCount = null,Object? groups = null,Object? registered = null,Object? full = null,Object? deleted = null,Object? topParticipants = null,Object? tags = null,Object? elevationGain = freezed,Object? surfaceType = freezed,Object? startPlace = freezed,Object? distance = freezed,Object? createdAt = freezed,Object? thumbnailLightUrl = freezed,Object? thumbnailDarkUrl = freezed,Object? thumbnailUrl = freezed,Object? routeSlug = freezed,Object? publishAt = freezed,Object? registeredGroupId = freezed,Object? registeredGroup = freezed,Object? excerpt = freezed,Object? maxParticipants = freezed,Object? commentCount = freezed,Object? endPlace = freezed,}) {
  return _then(_RideDto(
groupSummaries: null == groupSummaries ? _self._groupSummaries : groupSummaries // ignore: cast_nullable_to_non_nullable
as List<RideGroupSummaryDto>,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,finished: null == finished ? _self.finished : finished // ignore: cast_nullable_to_non_nullable
as bool,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,participantCount: null == participantCount ? _self.participantCount : participantCount // ignore: cast_nullable_to_non_nullable
as int,groupCount: null == groupCount ? _self.groupCount : groupCount // ignore: cast_nullable_to_non_nullable
as int,groups: null == groups ? _self._groups : groups // ignore: cast_nullable_to_non_nullable
as List<RideGroupDto>,registered: null == registered ? _self.registered : registered // ignore: cast_nullable_to_non_nullable
as bool,full: null == full ? _self.full : full // ignore: cast_nullable_to_non_nullable
as bool,deleted: null == deleted ? _self.deleted : deleted // ignore: cast_nullable_to_non_nullable
as bool,topParticipants: null == topParticipants ? _self._topParticipants : topParticipants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,tags: null == tags ? _self._tags : tags // ignore: cast_nullable_to_non_nullable
as List<TagDto>,elevationGain: freezed == elevationGain ? _self.elevationGain : elevationGain // ignore: cast_nullable_to_non_nullable
as double?,surfaceType: freezed == surfaceType ? _self.surfaceType : surfaceType // ignore: cast_nullable_to_non_nullable
as String?,startPlace: freezed == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,thumbnailLightUrl: freezed == thumbnailLightUrl ? _self.thumbnailLightUrl : thumbnailLightUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailDarkUrl: freezed == thumbnailDarkUrl ? _self.thumbnailDarkUrl : thumbnailDarkUrl // ignore: cast_nullable_to_non_nullable
as String?,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,registeredGroupId: freezed == registeredGroupId ? _self.registeredGroupId : registeredGroupId // ignore: cast_nullable_to_non_nullable
as String?,registeredGroup: freezed == registeredGroup ? _self.registeredGroup : registeredGroup // ignore: cast_nullable_to_non_nullable
as RideGroupDto?,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,commentCount: freezed == commentCount ? _self.commentCount : commentCount // ignore: cast_nullable_to_non_nullable
as int?,endPlace: freezed == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,
  ));
}

/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of RideDto
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
}/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$RideGroupDtoCopyWith<$Res>? get registeredGroup {
    if (_self.registeredGroup == null) {
    return null;
  }

  return $RideGroupDtoCopyWith<$Res>(_self.registeredGroup!, (value) {
    return _then(_self.copyWith(registeredGroup: value));
  });
}/// Create a copy of RideDto
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

// dart format on
