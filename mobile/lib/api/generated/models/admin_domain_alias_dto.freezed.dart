// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_domain_alias_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminDomainAliasDto {

/// Alias ID (TSID)
 String get id;/// Alias hostname
 String get hostname;/// Parent domain ID (TSID)
 String get domainId;/// Pinned team ID (TSID)
 String get pinnedTeamId;/// Pinned team slug
 String get pinnedTeamSlug;/// Alias display name
 String get name;/// Base URL for the alias
 String get baseUrl;/// Whether alias is active
 bool get active;/// Alias creation timestamp
 String get createdAt;/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
 String? get androidFingerprints;
/// Create a copy of AdminDomainAliasDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminDomainAliasDtoCopyWith<AdminDomainAliasDto> get copyWith => _$AdminDomainAliasDtoCopyWithImpl<AdminDomainAliasDto>(this as AdminDomainAliasDto, _$identity);

  /// Serializes this AdminDomainAliasDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminDomainAliasDto&&(identical(other.id, id) || other.id == id)&&(identical(other.hostname, hostname) || other.hostname == hostname)&&(identical(other.domainId, domainId) || other.domainId == domainId)&&(identical(other.pinnedTeamId, pinnedTeamId) || other.pinnedTeamId == pinnedTeamId)&&(identical(other.pinnedTeamSlug, pinnedTeamSlug) || other.pinnedTeamSlug == pinnedTeamSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.active, active) || other.active == active)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,hostname,domainId,pinnedTeamId,pinnedTeamSlug,name,baseUrl,active,createdAt,androidFingerprints);

@override
String toString() {
  return 'AdminDomainAliasDto(id: $id, hostname: $hostname, domainId: $domainId, pinnedTeamId: $pinnedTeamId, pinnedTeamSlug: $pinnedTeamSlug, name: $name, baseUrl: $baseUrl, active: $active, createdAt: $createdAt, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class $AdminDomainAliasDtoCopyWith<$Res>  {
  factory $AdminDomainAliasDtoCopyWith(AdminDomainAliasDto value, $Res Function(AdminDomainAliasDto) _then) = _$AdminDomainAliasDtoCopyWithImpl;
@useResult
$Res call({
 String id, String hostname, String domainId, String pinnedTeamId, String pinnedTeamSlug, String name, String baseUrl, bool active, String createdAt, String? androidFingerprints
});




}
/// @nodoc
class _$AdminDomainAliasDtoCopyWithImpl<$Res>
    implements $AdminDomainAliasDtoCopyWith<$Res> {
  _$AdminDomainAliasDtoCopyWithImpl(this._self, this._then);

  final AdminDomainAliasDto _self;
  final $Res Function(AdminDomainAliasDto) _then;

/// Create a copy of AdminDomainAliasDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? hostname = null,Object? domainId = null,Object? pinnedTeamId = null,Object? pinnedTeamSlug = null,Object? name = null,Object? baseUrl = null,Object? active = null,Object? createdAt = null,Object? androidFingerprints = freezed,}) {
  return _then(AdminDomainAliasDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,hostname: null == hostname ? _self.hostname : hostname // ignore: cast_nullable_to_non_nullable
as String,domainId: null == domainId ? _self.domainId : domainId // ignore: cast_nullable_to_non_nullable
as String,pinnedTeamId: null == pinnedTeamId ? _self.pinnedTeamId : pinnedTeamId // ignore: cast_nullable_to_non_nullable
as String,pinnedTeamSlug: null == pinnedTeamSlug ? _self.pinnedTeamSlug : pinnedTeamSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,active: null == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminDomainAliasDto].
extension AdminDomainAliasDtoPatterns on AdminDomainAliasDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminDomainAliasDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminDomainAliasDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminDomainAliasDto value)  $default,){
final _that = this;
switch (_that) {
case _AdminDomainAliasDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminDomainAliasDto value)?  $default,){
final _that = this;
switch (_that) {
case _AdminDomainAliasDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String hostname,  String domainId,  String pinnedTeamId,  String pinnedTeamSlug,  String name,  String baseUrl,  bool active,  String createdAt,  String? androidFingerprints)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminDomainAliasDto() when $default != null:
return $default(_that.id,_that.hostname,_that.domainId,_that.pinnedTeamId,_that.pinnedTeamSlug,_that.name,_that.baseUrl,_that.active,_that.createdAt,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String hostname,  String domainId,  String pinnedTeamId,  String pinnedTeamSlug,  String name,  String baseUrl,  bool active,  String createdAt,  String? androidFingerprints)  $default,) {final _that = this;
switch (_that) {
case _AdminDomainAliasDto():
return $default(_that.id,_that.hostname,_that.domainId,_that.pinnedTeamId,_that.pinnedTeamSlug,_that.name,_that.baseUrl,_that.active,_that.createdAt,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String hostname,  String domainId,  String pinnedTeamId,  String pinnedTeamSlug,  String name,  String baseUrl,  bool active,  String createdAt,  String? androidFingerprints)?  $default,) {final _that = this;
switch (_that) {
case _AdminDomainAliasDto() when $default != null:
return $default(_that.id,_that.hostname,_that.domainId,_that.pinnedTeamId,_that.pinnedTeamSlug,_that.name,_that.baseUrl,_that.active,_that.createdAt,_that.androidFingerprints);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminDomainAliasDto implements AdminDomainAliasDto {
  const _AdminDomainAliasDto({required this.id, required this.hostname, required this.domainId, required this.pinnedTeamId, required this.pinnedTeamSlug, required this.name, required this.baseUrl, required this.active, required this.createdAt, this.androidFingerprints});
  factory _AdminDomainAliasDto.fromJson(Map<String, dynamic> json) => _$AdminDomainAliasDtoFromJson(json);

/// Alias ID (TSID)
@override final  String id;
/// Alias hostname
@override final  String hostname;
/// Parent domain ID (TSID)
@override final  String domainId;
/// Pinned team ID (TSID)
@override final  String pinnedTeamId;
/// Pinned team slug
@override final  String pinnedTeamSlug;
/// Alias display name
@override final  String name;
/// Base URL for the alias
@override final  String baseUrl;
/// Whether alias is active
@override final  bool active;
/// Alias creation timestamp
@override final  String createdAt;
/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
@override final  String? androidFingerprints;

/// Create a copy of AdminDomainAliasDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminDomainAliasDtoCopyWith<_AdminDomainAliasDto> get copyWith => __$AdminDomainAliasDtoCopyWithImpl<_AdminDomainAliasDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminDomainAliasDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminDomainAliasDto&&(identical(other.id, id) || other.id == id)&&(identical(other.hostname, hostname) || other.hostname == hostname)&&(identical(other.domainId, domainId) || other.domainId == domainId)&&(identical(other.pinnedTeamId, pinnedTeamId) || other.pinnedTeamId == pinnedTeamId)&&(identical(other.pinnedTeamSlug, pinnedTeamSlug) || other.pinnedTeamSlug == pinnedTeamSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.active, active) || other.active == active)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,hostname,domainId,pinnedTeamId,pinnedTeamSlug,name,baseUrl,active,createdAt,androidFingerprints);

@override
String toString() {
  return 'AdminDomainAliasDto(id: $id, hostname: $hostname, domainId: $domainId, pinnedTeamId: $pinnedTeamId, pinnedTeamSlug: $pinnedTeamSlug, name: $name, baseUrl: $baseUrl, active: $active, createdAt: $createdAt, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class _$AdminDomainAliasDtoCopyWith<$Res> implements $AdminDomainAliasDtoCopyWith<$Res> {
  factory _$AdminDomainAliasDtoCopyWith(_AdminDomainAliasDto value, $Res Function(_AdminDomainAliasDto) _then) = __$AdminDomainAliasDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String hostname, String domainId, String pinnedTeamId, String pinnedTeamSlug, String name, String baseUrl, bool active, String createdAt, String? androidFingerprints
});




}
/// @nodoc
class __$AdminDomainAliasDtoCopyWithImpl<$Res>
    implements _$AdminDomainAliasDtoCopyWith<$Res> {
  __$AdminDomainAliasDtoCopyWithImpl(this._self, this._then);

  final _AdminDomainAliasDto _self;
  final $Res Function(_AdminDomainAliasDto) _then;

/// Create a copy of AdminDomainAliasDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? hostname = null,Object? domainId = null,Object? pinnedTeamId = null,Object? pinnedTeamSlug = null,Object? name = null,Object? baseUrl = null,Object? active = null,Object? createdAt = null,Object? androidFingerprints = freezed,}) {
  return _then(_AdminDomainAliasDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,hostname: null == hostname ? _self.hostname : hostname // ignore: cast_nullable_to_non_nullable
as String,domainId: null == domainId ? _self.domainId : domainId // ignore: cast_nullable_to_non_nullable
as String,pinnedTeamId: null == pinnedTeamId ? _self.pinnedTeamId : pinnedTeamId // ignore: cast_nullable_to_non_nullable
as String,pinnedTeamSlug: null == pinnedTeamSlug ? _self.pinnedTeamSlug : pinnedTeamSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,active: null == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
