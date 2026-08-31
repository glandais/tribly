// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_domain_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminDomainDto {

/// Domain ID (TSID)
 String get id;/// Domain hostname
 String get domain;/// Domain display name
 String get name;/// Base URL for the domain
 String get baseUrl;/// Whether domain is single-team mode
 bool get singleTeam;/// Whether the interactive planner is open in the GPX tools
 bool get enableGpxPlanner;/// Whether domain is active
 bool get active;/// Number of teams in this domain
 int get teamCount;/// Number of users in this domain
 int get userCount;/// Domain creation timestamp
 String get createdAt;/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
 String? get androidFingerprints;
/// Create a copy of AdminDomainDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminDomainDtoCopyWith<AdminDomainDto> get copyWith => _$AdminDomainDtoCopyWithImpl<AdminDomainDto>(this as AdminDomainDto, _$identity);

  /// Serializes this AdminDomainDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as AdminDomainDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminDomainDto&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.domain, _this.domain) || other.domain == _this.domain)&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.baseUrl, _this.baseUrl) || other.baseUrl == _this.baseUrl)&&(identical(other.singleTeam, _this.singleTeam) || other.singleTeam == _this.singleTeam)&&(identical(other.enableGpxPlanner, _this.enableGpxPlanner) || other.enableGpxPlanner == _this.enableGpxPlanner)&&(identical(other.active, _this.active) || other.active == _this.active)&&(identical(other.teamCount, _this.teamCount) || other.teamCount == _this.teamCount)&&(identical(other.userCount, _this.userCount) || other.userCount == _this.userCount)&&(identical(other.createdAt, _this.createdAt) || other.createdAt == _this.createdAt)&&(identical(other.androidFingerprints, _this.androidFingerprints) || other.androidFingerprints == _this.androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as AdminDomainDto;
  return Object.hash(runtimeType,_this.id,_this.domain,_this.name,_this.baseUrl,_this.singleTeam,_this.enableGpxPlanner,_this.active,_this.teamCount,_this.userCount,_this.createdAt,_this.androidFingerprints);
}

@override
String toString() {
  final _this = this as AdminDomainDto;
  return 'AdminDomainDto(id: ${_this.id}, domain: ${_this.domain}, name: ${_this.name}, baseUrl: ${_this.baseUrl}, singleTeam: ${_this.singleTeam}, enableGpxPlanner: ${_this.enableGpxPlanner}, active: ${_this.active}, teamCount: ${_this.teamCount}, userCount: ${_this.userCount}, createdAt: ${_this.createdAt}, androidFingerprints: ${_this.androidFingerprints})';
}


}

/// @nodoc
abstract mixin class $AdminDomainDtoCopyWith<$Res>  {
  factory $AdminDomainDtoCopyWith(AdminDomainDto value, $Res Function(AdminDomainDto) _then) = _$AdminDomainDtoCopyWithImpl;
@useResult
$Res call({
 String id, String domain, String name, String baseUrl, bool singleTeam, bool enableGpxPlanner, bool active, int teamCount, int userCount, String createdAt, String? androidFingerprints
});




}
/// @nodoc
class _$AdminDomainDtoCopyWithImpl<$Res>
    implements $AdminDomainDtoCopyWith<$Res> {
  _$AdminDomainDtoCopyWithImpl(this._self, this._then);

  final AdminDomainDto _self;
  final $Res Function(AdminDomainDto) _then;

/// Create a copy of AdminDomainDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? domain = null,Object? name = null,Object? baseUrl = null,Object? singleTeam = null,Object? enableGpxPlanner = null,Object? active = null,Object? teamCount = null,Object? userCount = null,Object? createdAt = null,Object? androidFingerprints = freezed,}) {
  return _then(AdminDomainDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,domain: null == domain ? _self.domain : domain // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,singleTeam: null == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool,enableGpxPlanner: null == enableGpxPlanner ? _self.enableGpxPlanner : enableGpxPlanner // ignore: cast_nullable_to_non_nullable
as bool,active: null == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool,teamCount: null == teamCount ? _self.teamCount : teamCount // ignore: cast_nullable_to_non_nullable
as int,userCount: null == userCount ? _self.userCount : userCount // ignore: cast_nullable_to_non_nullable
as int,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminDomainDto].
extension AdminDomainDtoPatterns on AdminDomainDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminDomainDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminDomainDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminDomainDto value)  $default,){
final _that = this;
switch (_that) {
case _AdminDomainDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminDomainDto value)?  $default,){
final _that = this;
switch (_that) {
case _AdminDomainDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String domain,  String name,  String baseUrl,  bool singleTeam,  bool enableGpxPlanner,  bool active,  int teamCount,  int userCount,  String createdAt,  String? androidFingerprints)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminDomainDto() when $default != null:
return $default(_that.id,_that.domain,_that.name,_that.baseUrl,_that.singleTeam,_that.enableGpxPlanner,_that.active,_that.teamCount,_that.userCount,_that.createdAt,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String domain,  String name,  String baseUrl,  bool singleTeam,  bool enableGpxPlanner,  bool active,  int teamCount,  int userCount,  String createdAt,  String? androidFingerprints)  $default,) {final _that = this;
switch (_that) {
case _AdminDomainDto():
return $default(_that.id,_that.domain,_that.name,_that.baseUrl,_that.singleTeam,_that.enableGpxPlanner,_that.active,_that.teamCount,_that.userCount,_that.createdAt,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String domain,  String name,  String baseUrl,  bool singleTeam,  bool enableGpxPlanner,  bool active,  int teamCount,  int userCount,  String createdAt,  String? androidFingerprints)?  $default,) {final _that = this;
switch (_that) {
case _AdminDomainDto() when $default != null:
return $default(_that.id,_that.domain,_that.name,_that.baseUrl,_that.singleTeam,_that.enableGpxPlanner,_that.active,_that.teamCount,_that.userCount,_that.createdAt,_that.androidFingerprints);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminDomainDto implements AdminDomainDto {
  const _AdminDomainDto({required this.id, required this.domain, required this.name, required this.baseUrl, required this.singleTeam, required this.enableGpxPlanner, required this.active, required this.teamCount, required this.userCount, required this.createdAt, this.androidFingerprints});
  factory _AdminDomainDto.fromJson(Map<String, dynamic> json) => _$AdminDomainDtoFromJson(json);

/// Domain ID (TSID)
@override final  String id;
/// Domain hostname
@override final  String domain;
/// Domain display name
@override final  String name;
/// Base URL for the domain
@override final  String baseUrl;
/// Whether domain is single-team mode
@override final  bool singleTeam;
/// Whether the interactive planner is open in the GPX tools
@override final  bool enableGpxPlanner;
/// Whether domain is active
@override final  bool active;
/// Number of teams in this domain
@override final  int teamCount;
/// Number of users in this domain
@override final  int userCount;
/// Domain creation timestamp
@override final  String createdAt;
/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
@override final  String? androidFingerprints;

/// Create a copy of AdminDomainDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminDomainDtoCopyWith<_AdminDomainDto> get copyWith => __$AdminDomainDtoCopyWithImpl<_AdminDomainDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminDomainDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminDomainDto&&(identical(other.id, id) || other.id == id)&&(identical(other.domain, domain) || other.domain == domain)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.singleTeam, singleTeam) || other.singleTeam == singleTeam)&&(identical(other.enableGpxPlanner, enableGpxPlanner) || other.enableGpxPlanner == enableGpxPlanner)&&(identical(other.active, active) || other.active == active)&&(identical(other.teamCount, teamCount) || other.teamCount == teamCount)&&(identical(other.userCount, userCount) || other.userCount == userCount)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,id,domain,name,baseUrl,singleTeam,enableGpxPlanner,active,teamCount,userCount,createdAt,androidFingerprints);
}

@override
String toString() {
    return 'AdminDomainDto(id: $id, domain: $domain, name: $name, baseUrl: $baseUrl, singleTeam: $singleTeam, enableGpxPlanner: $enableGpxPlanner, active: $active, teamCount: $teamCount, userCount: $userCount, createdAt: $createdAt, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class _$AdminDomainDtoCopyWith<$Res> implements $AdminDomainDtoCopyWith<$Res> {
  factory _$AdminDomainDtoCopyWith(_AdminDomainDto value, $Res Function(_AdminDomainDto) _then) = __$AdminDomainDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String domain, String name, String baseUrl, bool singleTeam, bool enableGpxPlanner, bool active, int teamCount, int userCount, String createdAt, String? androidFingerprints
});




}
/// @nodoc
class __$AdminDomainDtoCopyWithImpl<$Res>
    implements _$AdminDomainDtoCopyWith<$Res> {
  __$AdminDomainDtoCopyWithImpl(this._self, this._then);

  final _AdminDomainDto _self;
  final $Res Function(_AdminDomainDto) _then;

/// Create a copy of AdminDomainDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? domain = null,Object? name = null,Object? baseUrl = null,Object? singleTeam = null,Object? enableGpxPlanner = null,Object? active = null,Object? teamCount = null,Object? userCount = null,Object? createdAt = null,Object? androidFingerprints = freezed,}) {
  return _then(_AdminDomainDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,domain: null == domain ? _self.domain : domain // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,singleTeam: null == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool,enableGpxPlanner: null == enableGpxPlanner ? _self.enableGpxPlanner : enableGpxPlanner // ignore: cast_nullable_to_non_nullable
as bool,active: null == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool,teamCount: null == teamCount ? _self.teamCount : teamCount // ignore: cast_nullable_to_non_nullable
as int,userCount: null == userCount ? _self.userCount : userCount // ignore: cast_nullable_to_non_nullable
as int,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
