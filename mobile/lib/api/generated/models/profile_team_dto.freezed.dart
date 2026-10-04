// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'profile_team_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ProfileTeamDto {

/// Team URL slug
 String get slug;/// Team name
 String get name;/// The user's role in the team
 String get role;
/// Create a copy of ProfileTeamDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ProfileTeamDtoCopyWith<ProfileTeamDto> get copyWith => _$ProfileTeamDtoCopyWithImpl<ProfileTeamDto>(this as ProfileTeamDto, _$identity);

  /// Serializes this ProfileTeamDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ProfileTeamDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ProfileTeamDto&&(identical(other.slug, _this.slug) || other.slug == _this.slug)&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.role, _this.role) || other.role == _this.role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ProfileTeamDto;
  return Object.hash(runtimeType,_this.slug,_this.name,_this.role);
}

@override
String toString() {
  final _this = this as ProfileTeamDto;
  return 'ProfileTeamDto(slug: ${_this.slug}, name: ${_this.name}, role: ${_this.role})';
}


}

/// @nodoc
abstract mixin class $ProfileTeamDtoCopyWith<$Res>  {
  factory $ProfileTeamDtoCopyWith(ProfileTeamDto value, $Res Function(ProfileTeamDto) _then) = _$ProfileTeamDtoCopyWithImpl;
@useResult
$Res call({
 String slug, String name, String role
});




}
/// @nodoc
class _$ProfileTeamDtoCopyWithImpl<$Res>
    implements $ProfileTeamDtoCopyWith<$Res> {
  _$ProfileTeamDtoCopyWithImpl(this._self, this._then);

  final ProfileTeamDto _self;
  final $Res Function(ProfileTeamDto) _then;

/// Create a copy of ProfileTeamDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? slug = null,Object? name = null,Object? role = null,}) {
  return _then(ProfileTeamDto(
slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [ProfileTeamDto].
extension ProfileTeamDtoPatterns on ProfileTeamDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ProfileTeamDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ProfileTeamDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ProfileTeamDto value)  $default,){
final _that = this;
switch (_that) {
case _ProfileTeamDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ProfileTeamDto value)?  $default,){
final _that = this;
switch (_that) {
case _ProfileTeamDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String slug,  String name,  String role)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ProfileTeamDto() when $default != null:
return $default(_that.slug,_that.name,_that.role);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String slug,  String name,  String role)  $default,) {final _that = this;
switch (_that) {
case _ProfileTeamDto():
return $default(_that.slug,_that.name,_that.role);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String slug,  String name,  String role)?  $default,) {final _that = this;
switch (_that) {
case _ProfileTeamDto() when $default != null:
return $default(_that.slug,_that.name,_that.role);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ProfileTeamDto implements ProfileTeamDto {
  const _ProfileTeamDto({required this.slug, required this.name, required this.role});
  factory _ProfileTeamDto.fromJson(Map<String, dynamic> json) => _$ProfileTeamDtoFromJson(json);

/// Team URL slug
@override final  String slug;
/// Team name
@override final  String name;
/// The user's role in the team
@override final  String role;

/// Create a copy of ProfileTeamDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ProfileTeamDtoCopyWith<_ProfileTeamDto> get copyWith => __$ProfileTeamDtoCopyWithImpl<_ProfileTeamDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ProfileTeamDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ProfileTeamDto&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,slug,name,role);
}

@override
String toString() {
    return 'ProfileTeamDto(slug: $slug, name: $name, role: $role)';
}


}

/// @nodoc
abstract mixin class _$ProfileTeamDtoCopyWith<$Res> implements $ProfileTeamDtoCopyWith<$Res> {
  factory _$ProfileTeamDtoCopyWith(_ProfileTeamDto value, $Res Function(_ProfileTeamDto) _then) = __$ProfileTeamDtoCopyWithImpl;
@override @useResult
$Res call({
 String slug, String name, String role
});




}
/// @nodoc
class __$ProfileTeamDtoCopyWithImpl<$Res>
    implements _$ProfileTeamDtoCopyWith<$Res> {
  __$ProfileTeamDtoCopyWithImpl(this._self, this._then);

  final _ProfileTeamDto _self;
  final $Res Function(_ProfileTeamDto) _then;

/// Create a copy of ProfileTeamDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? slug = null,Object? name = null,Object? role = null,}) {
  return _then(_ProfileTeamDto(
slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
