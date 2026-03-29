// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_detail_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamDetailDto {

/// Team ID (TSID)
 String get id;/// Team name
 String get name;/// Team URL slug
 String get slug;/// About page content
 MediaDto get about;/// Whether the team is public
 String get visibility;/// Trips enabled
 bool get enableTrips;/// Ads enabled
 bool get enableAds;/// Posts enabled
 bool get enablePosts;/// Rides enabled
 bool get enableRides;/// Routes enabled
 bool get enableRoutes;/// Number of team members
 int get memberCount;/// Team creation timestamp
 String get createdAt;/// Additional team pages
 List<TeamPageSummaryDto>? get pages;/// Current user's role (null if not a member)
 String? get role;/// Team location coordinates [longitude, latitude]
 TeamDetailDtoGeometry? get geometry;
/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamDetailDtoCopyWith<TeamDetailDto> get copyWith => _$TeamDetailDtoCopyWithImpl<TeamDetailDto>(this as TeamDetailDto, _$identity);

  /// Serializes this TeamDetailDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamDetailDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.about, about) || other.about == about)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.enableTrips, enableTrips) || other.enableTrips == enableTrips)&&(identical(other.enableAds, enableAds) || other.enableAds == enableAds)&&(identical(other.enablePosts, enablePosts) || other.enablePosts == enablePosts)&&(identical(other.enableRides, enableRides) || other.enableRides == enableRides)&&(identical(other.enableRoutes, enableRoutes) || other.enableRoutes == enableRoutes)&&(identical(other.memberCount, memberCount) || other.memberCount == memberCount)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&const DeepCollectionEquality().equals(other.pages, pages)&&(identical(other.role, role) || other.role == role)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,slug,about,visibility,enableTrips,enableAds,enablePosts,enableRides,enableRoutes,memberCount,createdAt,const DeepCollectionEquality().hash(pages),role,geometry);

@override
String toString() {
  return 'TeamDetailDto(id: $id, name: $name, slug: $slug, about: $about, visibility: $visibility, enableTrips: $enableTrips, enableAds: $enableAds, enablePosts: $enablePosts, enableRides: $enableRides, enableRoutes: $enableRoutes, memberCount: $memberCount, createdAt: $createdAt, pages: $pages, role: $role, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class $TeamDetailDtoCopyWith<$Res>  {
  factory $TeamDetailDtoCopyWith(TeamDetailDto value, $Res Function(TeamDetailDto) _then) = _$TeamDetailDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, String slug, MediaDto about, String visibility, bool enableTrips, bool enableAds, bool enablePosts, bool enableRides, bool enableRoutes, int memberCount, String createdAt, List<TeamPageSummaryDto>? pages, String? role, TeamDetailDtoGeometry? geometry
});


$MediaDtoCopyWith<$Res> get about;$TeamDetailDtoGeometryCopyWith<$Res>? get geometry;

}
/// @nodoc
class _$TeamDetailDtoCopyWithImpl<$Res>
    implements $TeamDetailDtoCopyWith<$Res> {
  _$TeamDetailDtoCopyWithImpl(this._self, this._then);

  final TeamDetailDto _self;
  final $Res Function(TeamDetailDto) _then;

/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? slug = null,Object? about = null,Object? visibility = null,Object? enableTrips = null,Object? enableAds = null,Object? enablePosts = null,Object? enableRides = null,Object? enableRoutes = null,Object? memberCount = null,Object? createdAt = null,Object? pages = freezed,Object? role = freezed,Object? geometry = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,about: null == about ? _self.about : about // ignore: cast_nullable_to_non_nullable
as MediaDto,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,enableTrips: null == enableTrips ? _self.enableTrips : enableTrips // ignore: cast_nullable_to_non_nullable
as bool,enableAds: null == enableAds ? _self.enableAds : enableAds // ignore: cast_nullable_to_non_nullable
as bool,enablePosts: null == enablePosts ? _self.enablePosts : enablePosts // ignore: cast_nullable_to_non_nullable
as bool,enableRides: null == enableRides ? _self.enableRides : enableRides // ignore: cast_nullable_to_non_nullable
as bool,enableRoutes: null == enableRoutes ? _self.enableRoutes : enableRoutes // ignore: cast_nullable_to_non_nullable
as bool,memberCount: null == memberCount ? _self.memberCount : memberCount // ignore: cast_nullable_to_non_nullable
as int,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,pages: freezed == pages ? _self.pages : pages // ignore: cast_nullable_to_non_nullable
as List<TeamPageSummaryDto>?,role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as TeamDetailDtoGeometry?,
  ));
}
/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get about {
  
  return $MediaDtoCopyWith<$Res>(_self.about, (value) {
    return _then(_self.copyWith(about: value));
  });
}/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamDetailDtoGeometryCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $TeamDetailDtoGeometryCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}


/// Adds pattern-matching-related methods to [TeamDetailDto].
extension TeamDetailDtoPatterns on TeamDetailDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamDetailDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamDetailDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamDetailDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamDetailDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamDetailDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamDetailDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  String slug,  MediaDto about,  String visibility,  bool enableTrips,  bool enableAds,  bool enablePosts,  bool enableRides,  bool enableRoutes,  int memberCount,  String createdAt,  List<TeamPageSummaryDto>? pages,  String? role,  TeamDetailDtoGeometry? geometry)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamDetailDto() when $default != null:
return $default(_that.id,_that.name,_that.slug,_that.about,_that.visibility,_that.enableTrips,_that.enableAds,_that.enablePosts,_that.enableRides,_that.enableRoutes,_that.memberCount,_that.createdAt,_that.pages,_that.role,_that.geometry);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  String slug,  MediaDto about,  String visibility,  bool enableTrips,  bool enableAds,  bool enablePosts,  bool enableRides,  bool enableRoutes,  int memberCount,  String createdAt,  List<TeamPageSummaryDto>? pages,  String? role,  TeamDetailDtoGeometry? geometry)  $default,) {final _that = this;
switch (_that) {
case _TeamDetailDto():
return $default(_that.id,_that.name,_that.slug,_that.about,_that.visibility,_that.enableTrips,_that.enableAds,_that.enablePosts,_that.enableRides,_that.enableRoutes,_that.memberCount,_that.createdAt,_that.pages,_that.role,_that.geometry);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  String slug,  MediaDto about,  String visibility,  bool enableTrips,  bool enableAds,  bool enablePosts,  bool enableRides,  bool enableRoutes,  int memberCount,  String createdAt,  List<TeamPageSummaryDto>? pages,  String? role,  TeamDetailDtoGeometry? geometry)?  $default,) {final _that = this;
switch (_that) {
case _TeamDetailDto() when $default != null:
return $default(_that.id,_that.name,_that.slug,_that.about,_that.visibility,_that.enableTrips,_that.enableAds,_that.enablePosts,_that.enableRides,_that.enableRoutes,_that.memberCount,_that.createdAt,_that.pages,_that.role,_that.geometry);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamDetailDto implements TeamDetailDto {
  const _TeamDetailDto({required this.id, required this.name, required this.slug, required this.about, required this.visibility, required this.enableTrips, required this.enableAds, required this.enablePosts, required this.enableRides, required this.enableRoutes, required this.memberCount, required this.createdAt, final  List<TeamPageSummaryDto>? pages, this.role, this.geometry}): _pages = pages;
  factory _TeamDetailDto.fromJson(Map<String, dynamic> json) => _$TeamDetailDtoFromJson(json);

/// Team ID (TSID)
@override final  String id;
/// Team name
@override final  String name;
/// Team URL slug
@override final  String slug;
/// About page content
@override final  MediaDto about;
/// Whether the team is public
@override final  String visibility;
/// Trips enabled
@override final  bool enableTrips;
/// Ads enabled
@override final  bool enableAds;
/// Posts enabled
@override final  bool enablePosts;
/// Rides enabled
@override final  bool enableRides;
/// Routes enabled
@override final  bool enableRoutes;
/// Number of team members
@override final  int memberCount;
/// Team creation timestamp
@override final  String createdAt;
/// Additional team pages
 final  List<TeamPageSummaryDto>? _pages;
/// Additional team pages
@override List<TeamPageSummaryDto>? get pages {
  final value = _pages;
  if (value == null) return null;
  if (_pages is EqualUnmodifiableListView) return _pages;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

/// Current user's role (null if not a member)
@override final  String? role;
/// Team location coordinates [longitude, latitude]
@override final  TeamDetailDtoGeometry? geometry;

/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamDetailDtoCopyWith<_TeamDetailDto> get copyWith => __$TeamDetailDtoCopyWithImpl<_TeamDetailDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamDetailDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamDetailDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.about, about) || other.about == about)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.enableTrips, enableTrips) || other.enableTrips == enableTrips)&&(identical(other.enableAds, enableAds) || other.enableAds == enableAds)&&(identical(other.enablePosts, enablePosts) || other.enablePosts == enablePosts)&&(identical(other.enableRides, enableRides) || other.enableRides == enableRides)&&(identical(other.enableRoutes, enableRoutes) || other.enableRoutes == enableRoutes)&&(identical(other.memberCount, memberCount) || other.memberCount == memberCount)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&const DeepCollectionEquality().equals(other._pages, _pages)&&(identical(other.role, role) || other.role == role)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,slug,about,visibility,enableTrips,enableAds,enablePosts,enableRides,enableRoutes,memberCount,createdAt,const DeepCollectionEquality().hash(_pages),role,geometry);

@override
String toString() {
  return 'TeamDetailDto(id: $id, name: $name, slug: $slug, about: $about, visibility: $visibility, enableTrips: $enableTrips, enableAds: $enableAds, enablePosts: $enablePosts, enableRides: $enableRides, enableRoutes: $enableRoutes, memberCount: $memberCount, createdAt: $createdAt, pages: $pages, role: $role, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class _$TeamDetailDtoCopyWith<$Res> implements $TeamDetailDtoCopyWith<$Res> {
  factory _$TeamDetailDtoCopyWith(_TeamDetailDto value, $Res Function(_TeamDetailDto) _then) = __$TeamDetailDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, String slug, MediaDto about, String visibility, bool enableTrips, bool enableAds, bool enablePosts, bool enableRides, bool enableRoutes, int memberCount, String createdAt, List<TeamPageSummaryDto>? pages, String? role, TeamDetailDtoGeometry? geometry
});


@override $MediaDtoCopyWith<$Res> get about;@override $TeamDetailDtoGeometryCopyWith<$Res>? get geometry;

}
/// @nodoc
class __$TeamDetailDtoCopyWithImpl<$Res>
    implements _$TeamDetailDtoCopyWith<$Res> {
  __$TeamDetailDtoCopyWithImpl(this._self, this._then);

  final _TeamDetailDto _self;
  final $Res Function(_TeamDetailDto) _then;

/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? slug = null,Object? about = null,Object? visibility = null,Object? enableTrips = null,Object? enableAds = null,Object? enablePosts = null,Object? enableRides = null,Object? enableRoutes = null,Object? memberCount = null,Object? createdAt = null,Object? pages = freezed,Object? role = freezed,Object? geometry = freezed,}) {
  return _then(_TeamDetailDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,about: null == about ? _self.about : about // ignore: cast_nullable_to_non_nullable
as MediaDto,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,enableTrips: null == enableTrips ? _self.enableTrips : enableTrips // ignore: cast_nullable_to_non_nullable
as bool,enableAds: null == enableAds ? _self.enableAds : enableAds // ignore: cast_nullable_to_non_nullable
as bool,enablePosts: null == enablePosts ? _self.enablePosts : enablePosts // ignore: cast_nullable_to_non_nullable
as bool,enableRides: null == enableRides ? _self.enableRides : enableRides // ignore: cast_nullable_to_non_nullable
as bool,enableRoutes: null == enableRoutes ? _self.enableRoutes : enableRoutes // ignore: cast_nullable_to_non_nullable
as bool,memberCount: null == memberCount ? _self.memberCount : memberCount // ignore: cast_nullable_to_non_nullable
as int,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,pages: freezed == pages ? _self._pages : pages // ignore: cast_nullable_to_non_nullable
as List<TeamPageSummaryDto>?,role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as TeamDetailDtoGeometry?,
  ));
}

/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get about {
  
  return $MediaDtoCopyWith<$Res>(_self.about, (value) {
    return _then(_self.copyWith(about: value));
  });
}/// Create a copy of TeamDetailDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamDetailDtoGeometryCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $TeamDetailDtoGeometryCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}

// dart format on
