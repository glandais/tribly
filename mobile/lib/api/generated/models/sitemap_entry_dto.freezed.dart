// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'sitemap_entry_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$SitemapEntryDto {

/// Which page this is
 String get type;/// Slug of the team the page belongs to
 String get teamSlug;/// Last modification of the page's content
 String get lastModified;/// Slug of the trip a TRIP_STAGE belongs to; null for every other type
 String? get tripSlug;/// Slug of the page itself; null for TEAM and TEAM_ABOUT
 String? get slug;
/// Create a copy of SitemapEntryDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$SitemapEntryDtoCopyWith<SitemapEntryDto> get copyWith => _$SitemapEntryDtoCopyWithImpl<SitemapEntryDto>(this as SitemapEntryDto, _$identity);

  /// Serializes this SitemapEntryDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as SitemapEntryDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is SitemapEntryDto&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.lastModified, _this.lastModified) || other.lastModified == _this.lastModified)&&(identical(other.tripSlug, _this.tripSlug) || other.tripSlug == _this.tripSlug)&&(identical(other.slug, _this.slug) || other.slug == _this.slug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as SitemapEntryDto;
  return Object.hash(runtimeType,_this.type,_this.teamSlug,_this.lastModified,_this.tripSlug,_this.slug);
}

@override
String toString() {
  final _this = this as SitemapEntryDto;
  return 'SitemapEntryDto(type: ${_this.type}, teamSlug: ${_this.teamSlug}, lastModified: ${_this.lastModified}, tripSlug: ${_this.tripSlug}, slug: ${_this.slug})';
}


}

/// @nodoc
abstract mixin class $SitemapEntryDtoCopyWith<$Res>  {
  factory $SitemapEntryDtoCopyWith(SitemapEntryDto value, $Res Function(SitemapEntryDto) _then) = _$SitemapEntryDtoCopyWithImpl;
@useResult
$Res call({
 String type, String teamSlug, String lastModified, String? tripSlug, String? slug
});




}
/// @nodoc
class _$SitemapEntryDtoCopyWithImpl<$Res>
    implements $SitemapEntryDtoCopyWith<$Res> {
  _$SitemapEntryDtoCopyWithImpl(this._self, this._then);

  final SitemapEntryDto _self;
  final $Res Function(SitemapEntryDto) _then;

/// Create a copy of SitemapEntryDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? teamSlug = null,Object? lastModified = null,Object? tripSlug = freezed,Object? slug = freezed,}) {
  return _then(SitemapEntryDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,lastModified: null == lastModified ? _self.lastModified : lastModified // ignore: cast_nullable_to_non_nullable
as String,tripSlug: freezed == tripSlug ? _self.tripSlug : tripSlug // ignore: cast_nullable_to_non_nullable
as String?,slug: freezed == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [SitemapEntryDto].
extension SitemapEntryDtoPatterns on SitemapEntryDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _SitemapEntryDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _SitemapEntryDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _SitemapEntryDto value)  $default,){
final _that = this;
switch (_that) {
case _SitemapEntryDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _SitemapEntryDto value)?  $default,){
final _that = this;
switch (_that) {
case _SitemapEntryDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String teamSlug,  String lastModified,  String? tripSlug,  String? slug)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _SitemapEntryDto() when $default != null:
return $default(_that.type,_that.teamSlug,_that.lastModified,_that.tripSlug,_that.slug);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String teamSlug,  String lastModified,  String? tripSlug,  String? slug)  $default,) {final _that = this;
switch (_that) {
case _SitemapEntryDto():
return $default(_that.type,_that.teamSlug,_that.lastModified,_that.tripSlug,_that.slug);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String teamSlug,  String lastModified,  String? tripSlug,  String? slug)?  $default,) {final _that = this;
switch (_that) {
case _SitemapEntryDto() when $default != null:
return $default(_that.type,_that.teamSlug,_that.lastModified,_that.tripSlug,_that.slug);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _SitemapEntryDto implements SitemapEntryDto {
  const _SitemapEntryDto({required this.type, required this.teamSlug, required this.lastModified, this.tripSlug, this.slug});
  factory _SitemapEntryDto.fromJson(Map<String, dynamic> json) => _$SitemapEntryDtoFromJson(json);

/// Which page this is
@override final  String type;
/// Slug of the team the page belongs to
@override final  String teamSlug;
/// Last modification of the page's content
@override final  String lastModified;
/// Slug of the trip a TRIP_STAGE belongs to; null for every other type
@override final  String? tripSlug;
/// Slug of the page itself; null for TEAM and TEAM_ABOUT
@override final  String? slug;

/// Create a copy of SitemapEntryDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$SitemapEntryDtoCopyWith<_SitemapEntryDto> get copyWith => __$SitemapEntryDtoCopyWithImpl<_SitemapEntryDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$SitemapEntryDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _SitemapEntryDto&&(identical(other.type, type) || other.type == type)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.lastModified, lastModified) || other.lastModified == lastModified)&&(identical(other.tripSlug, tripSlug) || other.tripSlug == tripSlug)&&(identical(other.slug, slug) || other.slug == slug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,teamSlug,lastModified,tripSlug,slug);
}

@override
String toString() {
    return 'SitemapEntryDto(type: $type, teamSlug: $teamSlug, lastModified: $lastModified, tripSlug: $tripSlug, slug: $slug)';
}


}

/// @nodoc
abstract mixin class _$SitemapEntryDtoCopyWith<$Res> implements $SitemapEntryDtoCopyWith<$Res> {
  factory _$SitemapEntryDtoCopyWith(_SitemapEntryDto value, $Res Function(_SitemapEntryDto) _then) = __$SitemapEntryDtoCopyWithImpl;
@override @useResult
$Res call({
 String type, String teamSlug, String lastModified, String? tripSlug, String? slug
});




}
/// @nodoc
class __$SitemapEntryDtoCopyWithImpl<$Res>
    implements _$SitemapEntryDtoCopyWith<$Res> {
  __$SitemapEntryDtoCopyWithImpl(this._self, this._then);

  final _SitemapEntryDto _self;
  final $Res Function(_SitemapEntryDto) _then;

/// Create a copy of SitemapEntryDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? teamSlug = null,Object? lastModified = null,Object? tripSlug = freezed,Object? slug = freezed,}) {
  return _then(_SitemapEntryDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,lastModified: null == lastModified ? _self.lastModified : lastModified // ignore: cast_nullable_to_non_nullable
as String,tripSlug: freezed == tripSlug ? _self.tripSlug : tripSlug // ignore: cast_nullable_to_non_nullable
as String?,slug: freezed == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
