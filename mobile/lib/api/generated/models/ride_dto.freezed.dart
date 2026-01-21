// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideDto {

/// Type
 String get type;/// Team
 TeamPublicationDto get team;/// Publication ID (TSID)
 String get id;/// Publication URL slug
 String get slug;/// Publication name
 String get name;/// Publication media
 MediaDto get media;/// Publication date/time
 String get dateTime;/// Publication status
 String get status;/// Visibility level
 String get visibility;/// Number of participants
 int get participantCount;/// Number of groups
 int get groupCount;/// Ride groups
 List<RideGroupDto> get groups;/// Preview of first participants (max 5)
 List<PublicUserDto> get topParticipants;/// Publication timestamp
 String? get publishAt;/// Creation timestamp
 String? get createdAt;/// Route slug
 String? get routeSlug;/// Start place
 PlaceDetailDto? get startPlace;/// End place
 PlaceDetailDto? get endPlace;/// Route thumbnail URL
 String? get routeThumbnailUrl;
/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideDtoCopyWith<RideDto> get copyWith => _$RideDtoCopyWithImpl<RideDto>(this as RideDto, _$identity);

  /// Serializes this RideDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideDto&&(identical(other.type, type) || other.type == type)&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.participantCount, participantCount) || other.participantCount == participantCount)&&(identical(other.groupCount, groupCount) || other.groupCount == groupCount)&&const DeepCollectionEquality().equals(other.groups, groups)&&const DeepCollectionEquality().equals(other.topParticipants, topParticipants)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace)&&(identical(other.routeThumbnailUrl, routeThumbnailUrl) || other.routeThumbnailUrl == routeThumbnailUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,type,team,id,slug,name,media,dateTime,status,visibility,participantCount,groupCount,const DeepCollectionEquality().hash(groups),const DeepCollectionEquality().hash(topParticipants),publishAt,createdAt,routeSlug,startPlace,endPlace,routeThumbnailUrl]);

@override
String toString() {
  return 'RideDto(type: $type, team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, participantCount: $participantCount, groupCount: $groupCount, groups: $groups, topParticipants: $topParticipants, publishAt: $publishAt, createdAt: $createdAt, routeSlug: $routeSlug, startPlace: $startPlace, endPlace: $endPlace, routeThumbnailUrl: $routeThumbnailUrl)';
}


}

/// @nodoc
abstract mixin class $RideDtoCopyWith<$Res>  {
  factory $RideDtoCopyWith(RideDto value, $Res Function(RideDto) _then) = _$RideDtoCopyWithImpl;
@useResult
$Res call({
 String type, TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, int participantCount, int groupCount, List<RideGroupDto> groups, List<PublicUserDto> topParticipants, String? publishAt, String? createdAt, String? routeSlug, PlaceDetailDto? startPlace, PlaceDetailDto? endPlace, String? routeThumbnailUrl
});


$TeamPublicationDtoCopyWith<$Res> get team;$MediaDtoCopyWith<$Res> get media;$PlaceDetailDtoCopyWith<$Res>? get startPlace;$PlaceDetailDtoCopyWith<$Res>? get endPlace;

}
/// @nodoc
class _$RideDtoCopyWithImpl<$Res>
    implements $RideDtoCopyWith<$Res> {
  _$RideDtoCopyWithImpl(this._self, this._then);

  final RideDto _self;
  final $Res Function(RideDto) _then;

/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? participantCount = null,Object? groupCount = null,Object? groups = null,Object? topParticipants = null,Object? publishAt = freezed,Object? createdAt = freezed,Object? routeSlug = freezed,Object? startPlace = freezed,Object? endPlace = freezed,Object? routeThumbnailUrl = freezed,}) {
  return _then(_self.copyWith(
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
as int,groupCount: null == groupCount ? _self.groupCount : groupCount // ignore: cast_nullable_to_non_nullable
as int,groups: null == groups ? _self.groups : groups // ignore: cast_nullable_to_non_nullable
as List<RideGroupDto>,topParticipants: null == topParticipants ? _self.topParticipants : topParticipants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,startPlace: freezed == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,endPlace: freezed == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,routeThumbnailUrl: freezed == routeThumbnailUrl ? _self.routeThumbnailUrl : routeThumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  List<PublicUserDto> topParticipants,  String? publishAt,  String? createdAt,  String? routeSlug,  PlaceDetailDto? startPlace,  PlaceDetailDto? endPlace,  String? routeThumbnailUrl)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideDto() when $default != null:
return $default(_that.type,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.groupCount,_that.groups,_that.topParticipants,_that.publishAt,_that.createdAt,_that.routeSlug,_that.startPlace,_that.endPlace,_that.routeThumbnailUrl);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  List<PublicUserDto> topParticipants,  String? publishAt,  String? createdAt,  String? routeSlug,  PlaceDetailDto? startPlace,  PlaceDetailDto? endPlace,  String? routeThumbnailUrl)  $default,) {final _that = this;
switch (_that) {
case _RideDto():
return $default(_that.type,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.groupCount,_that.groups,_that.topParticipants,_that.publishAt,_that.createdAt,_that.routeSlug,_that.startPlace,_that.endPlace,_that.routeThumbnailUrl);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String dateTime,  String status,  String visibility,  int participantCount,  int groupCount,  List<RideGroupDto> groups,  List<PublicUserDto> topParticipants,  String? publishAt,  String? createdAt,  String? routeSlug,  PlaceDetailDto? startPlace,  PlaceDetailDto? endPlace,  String? routeThumbnailUrl)?  $default,) {final _that = this;
switch (_that) {
case _RideDto() when $default != null:
return $default(_that.type,_that.team,_that.id,_that.slug,_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.participantCount,_that.groupCount,_that.groups,_that.topParticipants,_that.publishAt,_that.createdAt,_that.routeSlug,_that.startPlace,_that.endPlace,_that.routeThumbnailUrl);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideDto implements RideDto {
  const _RideDto({required this.type, required this.team, required this.id, required this.slug, required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, required this.participantCount, required this.groupCount, required final  List<RideGroupDto> groups, required final  List<PublicUserDto> topParticipants, this.publishAt, this.createdAt, this.routeSlug, this.startPlace, this.endPlace, this.routeThumbnailUrl}): _groups = groups,_topParticipants = topParticipants;
  factory _RideDto.fromJson(Map<String, dynamic> json) => _$RideDtoFromJson(json);

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
/// Publication date/time
@override final  String dateTime;
/// Publication status
@override final  String status;
/// Visibility level
@override final  String visibility;
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

/// Preview of first participants (max 5)
 final  List<PublicUserDto> _topParticipants;
/// Preview of first participants (max 5)
@override List<PublicUserDto> get topParticipants {
  if (_topParticipants is EqualUnmodifiableListView) return _topParticipants;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_topParticipants);
}

/// Publication timestamp
@override final  String? publishAt;
/// Creation timestamp
@override final  String? createdAt;
/// Route slug
@override final  String? routeSlug;
/// Start place
@override final  PlaceDetailDto? startPlace;
/// End place
@override final  PlaceDetailDto? endPlace;
/// Route thumbnail URL
@override final  String? routeThumbnailUrl;

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
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideDto&&(identical(other.type, type) || other.type == type)&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.participantCount, participantCount) || other.participantCount == participantCount)&&(identical(other.groupCount, groupCount) || other.groupCount == groupCount)&&const DeepCollectionEquality().equals(other._groups, _groups)&&const DeepCollectionEquality().equals(other._topParticipants, _topParticipants)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.startPlace, startPlace) || other.startPlace == startPlace)&&(identical(other.endPlace, endPlace) || other.endPlace == endPlace)&&(identical(other.routeThumbnailUrl, routeThumbnailUrl) || other.routeThumbnailUrl == routeThumbnailUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,type,team,id,slug,name,media,dateTime,status,visibility,participantCount,groupCount,const DeepCollectionEquality().hash(_groups),const DeepCollectionEquality().hash(_topParticipants),publishAt,createdAt,routeSlug,startPlace,endPlace,routeThumbnailUrl]);

@override
String toString() {
  return 'RideDto(type: $type, team: $team, id: $id, slug: $slug, name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, participantCount: $participantCount, groupCount: $groupCount, groups: $groups, topParticipants: $topParticipants, publishAt: $publishAt, createdAt: $createdAt, routeSlug: $routeSlug, startPlace: $startPlace, endPlace: $endPlace, routeThumbnailUrl: $routeThumbnailUrl)';
}


}

/// @nodoc
abstract mixin class _$RideDtoCopyWith<$Res> implements $RideDtoCopyWith<$Res> {
  factory _$RideDtoCopyWith(_RideDto value, $Res Function(_RideDto) _then) = __$RideDtoCopyWithImpl;
@override @useResult
$Res call({
 String type, TeamPublicationDto team, String id, String slug, String name, MediaDto media, String dateTime, String status, String visibility, int participantCount, int groupCount, List<RideGroupDto> groups, List<PublicUserDto> topParticipants, String? publishAt, String? createdAt, String? routeSlug, PlaceDetailDto? startPlace, PlaceDetailDto? endPlace, String? routeThumbnailUrl
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;@override $PlaceDetailDtoCopyWith<$Res>? get startPlace;@override $PlaceDetailDtoCopyWith<$Res>? get endPlace;

}
/// @nodoc
class __$RideDtoCopyWithImpl<$Res>
    implements _$RideDtoCopyWith<$Res> {
  __$RideDtoCopyWithImpl(this._self, this._then);

  final _RideDto _self;
  final $Res Function(_RideDto) _then;

/// Create a copy of RideDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? participantCount = null,Object? groupCount = null,Object? groups = null,Object? topParticipants = null,Object? publishAt = freezed,Object? createdAt = freezed,Object? routeSlug = freezed,Object? startPlace = freezed,Object? endPlace = freezed,Object? routeThumbnailUrl = freezed,}) {
  return _then(_RideDto(
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
as int,groupCount: null == groupCount ? _self.groupCount : groupCount // ignore: cast_nullable_to_non_nullable
as int,groups: null == groups ? _self._groups : groups // ignore: cast_nullable_to_non_nullable
as List<RideGroupDto>,topParticipants: null == topParticipants ? _self._topParticipants : topParticipants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,startPlace: freezed == startPlace ? _self.startPlace : startPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,endPlace: freezed == endPlace ? _self.endPlace : endPlace // ignore: cast_nullable_to_non_nullable
as PlaceDetailDto?,routeThumbnailUrl: freezed == routeThumbnailUrl ? _self.routeThumbnailUrl : routeThumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,
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
