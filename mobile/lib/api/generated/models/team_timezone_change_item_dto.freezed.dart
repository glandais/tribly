// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_timezone_change_item_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamTimezoneChangeItemDto {

/// Kind of event
 String get type;/// Event ID (TSID)
 String get id;/// Event URL slug
 String get slug;/// Event title; a stage's own name
 String get title;/// Start, as stored today: read it in the team's current zone for the wall time it keeps
 String get dateTime;/// For a stage, the title of its trip
 String? get tripTitle;
/// Create a copy of TeamTimezoneChangeItemDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamTimezoneChangeItemDtoCopyWith<TeamTimezoneChangeItemDto> get copyWith => _$TeamTimezoneChangeItemDtoCopyWithImpl<TeamTimezoneChangeItemDto>(this as TeamTimezoneChangeItemDto, _$identity);

  /// Serializes this TeamTimezoneChangeItemDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamTimezoneChangeItemDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamTimezoneChangeItemDto&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.slug, _this.slug) || other.slug == _this.slug)&&(identical(other.title, _this.title) || other.title == _this.title)&&(identical(other.dateTime, _this.dateTime) || other.dateTime == _this.dateTime)&&(identical(other.tripTitle, _this.tripTitle) || other.tripTitle == _this.tripTitle));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamTimezoneChangeItemDto;
  return Object.hash(runtimeType,_this.type,_this.id,_this.slug,_this.title,_this.dateTime,_this.tripTitle);
}

@override
String toString() {
  final _this = this as TeamTimezoneChangeItemDto;
  return 'TeamTimezoneChangeItemDto(type: ${_this.type}, id: ${_this.id}, slug: ${_this.slug}, title: ${_this.title}, dateTime: ${_this.dateTime}, tripTitle: ${_this.tripTitle})';
}


}

/// @nodoc
abstract mixin class $TeamTimezoneChangeItemDtoCopyWith<$Res>  {
  factory $TeamTimezoneChangeItemDtoCopyWith(TeamTimezoneChangeItemDto value, $Res Function(TeamTimezoneChangeItemDto) _then) = _$TeamTimezoneChangeItemDtoCopyWithImpl;
@useResult
$Res call({
 String type, String id, String slug, String title, String dateTime, String? tripTitle
});




}
/// @nodoc
class _$TeamTimezoneChangeItemDtoCopyWithImpl<$Res>
    implements $TeamTimezoneChangeItemDtoCopyWith<$Res> {
  _$TeamTimezoneChangeItemDtoCopyWithImpl(this._self, this._then);

  final TeamTimezoneChangeItemDto _self;
  final $Res Function(TeamTimezoneChangeItemDto) _then;

/// Create a copy of TeamTimezoneChangeItemDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? id = null,Object? slug = null,Object? title = null,Object? dateTime = null,Object? tripTitle = freezed,}) {
  return _then(TeamTimezoneChangeItemDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,tripTitle: freezed == tripTitle ? _self.tripTitle : tripTitle // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamTimezoneChangeItemDto].
extension TeamTimezoneChangeItemDtoPatterns on TeamTimezoneChangeItemDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamTimezoneChangeItemDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamTimezoneChangeItemDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamTimezoneChangeItemDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamTimezoneChangeItemDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamTimezoneChangeItemDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamTimezoneChangeItemDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String id,  String slug,  String title,  String dateTime,  String? tripTitle)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamTimezoneChangeItemDto() when $default != null:
return $default(_that.type,_that.id,_that.slug,_that.title,_that.dateTime,_that.tripTitle);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String id,  String slug,  String title,  String dateTime,  String? tripTitle)  $default,) {final _that = this;
switch (_that) {
case _TeamTimezoneChangeItemDto():
return $default(_that.type,_that.id,_that.slug,_that.title,_that.dateTime,_that.tripTitle);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String id,  String slug,  String title,  String dateTime,  String? tripTitle)?  $default,) {final _that = this;
switch (_that) {
case _TeamTimezoneChangeItemDto() when $default != null:
return $default(_that.type,_that.id,_that.slug,_that.title,_that.dateTime,_that.tripTitle);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamTimezoneChangeItemDto implements TeamTimezoneChangeItemDto {
  const _TeamTimezoneChangeItemDto({required this.type, required this.id, required this.slug, required this.title, required this.dateTime, this.tripTitle});
  factory _TeamTimezoneChangeItemDto.fromJson(Map<String, dynamic> json) => _$TeamTimezoneChangeItemDtoFromJson(json);

/// Kind of event
@override final  String type;
/// Event ID (TSID)
@override final  String id;
/// Event URL slug
@override final  String slug;
/// Event title; a stage's own name
@override final  String title;
/// Start, as stored today: read it in the team's current zone for the wall time it keeps
@override final  String dateTime;
/// For a stage, the title of its trip
@override final  String? tripTitle;

/// Create a copy of TeamTimezoneChangeItemDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamTimezoneChangeItemDtoCopyWith<_TeamTimezoneChangeItemDto> get copyWith => __$TeamTimezoneChangeItemDtoCopyWithImpl<_TeamTimezoneChangeItemDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamTimezoneChangeItemDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamTimezoneChangeItemDto&&(identical(other.type, type) || other.type == type)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.title, title) || other.title == title)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.tripTitle, tripTitle) || other.tripTitle == tripTitle));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,id,slug,title,dateTime,tripTitle);
}

@override
String toString() {
    return 'TeamTimezoneChangeItemDto(type: $type, id: $id, slug: $slug, title: $title, dateTime: $dateTime, tripTitle: $tripTitle)';
}


}

/// @nodoc
abstract mixin class _$TeamTimezoneChangeItemDtoCopyWith<$Res> implements $TeamTimezoneChangeItemDtoCopyWith<$Res> {
  factory _$TeamTimezoneChangeItemDtoCopyWith(_TeamTimezoneChangeItemDto value, $Res Function(_TeamTimezoneChangeItemDto) _then) = __$TeamTimezoneChangeItemDtoCopyWithImpl;
@override @useResult
$Res call({
 String type, String id, String slug, String title, String dateTime, String? tripTitle
});




}
/// @nodoc
class __$TeamTimezoneChangeItemDtoCopyWithImpl<$Res>
    implements _$TeamTimezoneChangeItemDtoCopyWith<$Res> {
  __$TeamTimezoneChangeItemDtoCopyWithImpl(this._self, this._then);

  final _TeamTimezoneChangeItemDto _self;
  final $Res Function(_TeamTimezoneChangeItemDto) _then;

/// Create a copy of TeamTimezoneChangeItemDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? id = null,Object? slug = null,Object? title = null,Object? dateTime = null,Object? tripTitle = freezed,}) {
  return _then(_TeamTimezoneChangeItemDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,tripTitle: freezed == tripTitle ? _self.tripTitle : tripTitle // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
