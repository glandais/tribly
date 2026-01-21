// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_publication_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamPublicationDto {

/// Team ID (TSID)
 String get id;/// Team name
 String get name;/// Team URL slug
 String get slug;/// Whether the team is public
 String get visibility;
/// Create a copy of TeamPublicationDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<TeamPublicationDto> get copyWith => _$TeamPublicationDtoCopyWithImpl<TeamPublicationDto>(this as TeamPublicationDto, _$identity);

  /// Serializes this TeamPublicationDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamPublicationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.visibility, visibility) || other.visibility == visibility));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,slug,visibility);

@override
String toString() {
  return 'TeamPublicationDto(id: $id, name: $name, slug: $slug, visibility: $visibility)';
}


}

/// @nodoc
abstract mixin class $TeamPublicationDtoCopyWith<$Res>  {
  factory $TeamPublicationDtoCopyWith(TeamPublicationDto value, $Res Function(TeamPublicationDto) _then) = _$TeamPublicationDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, String slug, String visibility
});




}
/// @nodoc
class _$TeamPublicationDtoCopyWithImpl<$Res>
    implements $TeamPublicationDtoCopyWith<$Res> {
  _$TeamPublicationDtoCopyWithImpl(this._self, this._then);

  final TeamPublicationDto _self;
  final $Res Function(TeamPublicationDto) _then;

/// Create a copy of TeamPublicationDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? slug = null,Object? visibility = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamPublicationDto].
extension TeamPublicationDtoPatterns on TeamPublicationDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamPublicationDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamPublicationDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamPublicationDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamPublicationDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamPublicationDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamPublicationDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  String slug,  String visibility)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamPublicationDto() when $default != null:
return $default(_that.id,_that.name,_that.slug,_that.visibility);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  String slug,  String visibility)  $default,) {final _that = this;
switch (_that) {
case _TeamPublicationDto():
return $default(_that.id,_that.name,_that.slug,_that.visibility);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  String slug,  String visibility)?  $default,) {final _that = this;
switch (_that) {
case _TeamPublicationDto() when $default != null:
return $default(_that.id,_that.name,_that.slug,_that.visibility);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamPublicationDto implements TeamPublicationDto {
  const _TeamPublicationDto({required this.id, required this.name, required this.slug, required this.visibility});
  factory _TeamPublicationDto.fromJson(Map<String, dynamic> json) => _$TeamPublicationDtoFromJson(json);

/// Team ID (TSID)
@override final  String id;
/// Team name
@override final  String name;
/// Team URL slug
@override final  String slug;
/// Whether the team is public
@override final  String visibility;

/// Create a copy of TeamPublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamPublicationDtoCopyWith<_TeamPublicationDto> get copyWith => __$TeamPublicationDtoCopyWithImpl<_TeamPublicationDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamPublicationDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamPublicationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.visibility, visibility) || other.visibility == visibility));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,slug,visibility);

@override
String toString() {
  return 'TeamPublicationDto(id: $id, name: $name, slug: $slug, visibility: $visibility)';
}


}

/// @nodoc
abstract mixin class _$TeamPublicationDtoCopyWith<$Res> implements $TeamPublicationDtoCopyWith<$Res> {
  factory _$TeamPublicationDtoCopyWith(_TeamPublicationDto value, $Res Function(_TeamPublicationDto) _then) = __$TeamPublicationDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, String slug, String visibility
});




}
/// @nodoc
class __$TeamPublicationDtoCopyWithImpl<$Res>
    implements _$TeamPublicationDtoCopyWith<$Res> {
  __$TeamPublicationDtoCopyWithImpl(this._self, this._then);

  final _TeamPublicationDto _self;
  final $Res Function(_TeamPublicationDto) _then;

/// Create a copy of TeamPublicationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? slug = null,Object? visibility = null,}) {
  return _then(_TeamPublicationDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
