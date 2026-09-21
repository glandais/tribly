// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'notification_team_preference_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$NotificationTeamPreferenceDto {

/// Team slug
 String get teamSlug;/// Team name
 String get teamName;/// Muted: none of the team's announcements (publications) reach the user, inbox included. What concerns them personally — a cancelled ride they joined, a reply — still does.
 bool get muted;
/// Create a copy of NotificationTeamPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$NotificationTeamPreferenceDtoCopyWith<NotificationTeamPreferenceDto> get copyWith => _$NotificationTeamPreferenceDtoCopyWithImpl<NotificationTeamPreferenceDto>(this as NotificationTeamPreferenceDto, _$identity);

  /// Serializes this NotificationTeamPreferenceDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as NotificationTeamPreferenceDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is NotificationTeamPreferenceDto&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.teamName, _this.teamName) || other.teamName == _this.teamName)&&(identical(other.muted, _this.muted) || other.muted == _this.muted));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as NotificationTeamPreferenceDto;
  return Object.hash(runtimeType,_this.teamSlug,_this.teamName,_this.muted);
}

@override
String toString() {
  final _this = this as NotificationTeamPreferenceDto;
  return 'NotificationTeamPreferenceDto(teamSlug: ${_this.teamSlug}, teamName: ${_this.teamName}, muted: ${_this.muted})';
}


}

/// @nodoc
abstract mixin class $NotificationTeamPreferenceDtoCopyWith<$Res>  {
  factory $NotificationTeamPreferenceDtoCopyWith(NotificationTeamPreferenceDto value, $Res Function(NotificationTeamPreferenceDto) _then) = _$NotificationTeamPreferenceDtoCopyWithImpl;
@useResult
$Res call({
 String teamSlug, String teamName, bool muted
});




}
/// @nodoc
class _$NotificationTeamPreferenceDtoCopyWithImpl<$Res>
    implements $NotificationTeamPreferenceDtoCopyWith<$Res> {
  _$NotificationTeamPreferenceDtoCopyWithImpl(this._self, this._then);

  final NotificationTeamPreferenceDto _self;
  final $Res Function(NotificationTeamPreferenceDto) _then;

/// Create a copy of NotificationTeamPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teamSlug = null,Object? teamName = null,Object? muted = null,}) {
  return _then(NotificationTeamPreferenceDto(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,muted: null == muted ? _self.muted : muted // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [NotificationTeamPreferenceDto].
extension NotificationTeamPreferenceDtoPatterns on NotificationTeamPreferenceDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _NotificationTeamPreferenceDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _NotificationTeamPreferenceDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _NotificationTeamPreferenceDto value)  $default,){
final _that = this;
switch (_that) {
case _NotificationTeamPreferenceDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _NotificationTeamPreferenceDto value)?  $default,){
final _that = this;
switch (_that) {
case _NotificationTeamPreferenceDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String teamSlug,  String teamName,  bool muted)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _NotificationTeamPreferenceDto() when $default != null:
return $default(_that.teamSlug,_that.teamName,_that.muted);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String teamSlug,  String teamName,  bool muted)  $default,) {final _that = this;
switch (_that) {
case _NotificationTeamPreferenceDto():
return $default(_that.teamSlug,_that.teamName,_that.muted);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String teamSlug,  String teamName,  bool muted)?  $default,) {final _that = this;
switch (_that) {
case _NotificationTeamPreferenceDto() when $default != null:
return $default(_that.teamSlug,_that.teamName,_that.muted);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _NotificationTeamPreferenceDto implements NotificationTeamPreferenceDto {
  const _NotificationTeamPreferenceDto({required this.teamSlug, required this.teamName, required this.muted});
  factory _NotificationTeamPreferenceDto.fromJson(Map<String, dynamic> json) => _$NotificationTeamPreferenceDtoFromJson(json);

/// Team slug
@override final  String teamSlug;
/// Team name
@override final  String teamName;
/// Muted: none of the team's announcements (publications) reach the user, inbox included. What concerns them personally — a cancelled ride they joined, a reply — still does.
@override final  bool muted;

/// Create a copy of NotificationTeamPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$NotificationTeamPreferenceDtoCopyWith<_NotificationTeamPreferenceDto> get copyWith => __$NotificationTeamPreferenceDtoCopyWithImpl<_NotificationTeamPreferenceDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$NotificationTeamPreferenceDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _NotificationTeamPreferenceDto&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.teamName, teamName) || other.teamName == teamName)&&(identical(other.muted, muted) || other.muted == muted));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,teamSlug,teamName,muted);
}

@override
String toString() {
    return 'NotificationTeamPreferenceDto(teamSlug: $teamSlug, teamName: $teamName, muted: $muted)';
}


}

/// @nodoc
abstract mixin class _$NotificationTeamPreferenceDtoCopyWith<$Res> implements $NotificationTeamPreferenceDtoCopyWith<$Res> {
  factory _$NotificationTeamPreferenceDtoCopyWith(_NotificationTeamPreferenceDto value, $Res Function(_NotificationTeamPreferenceDto) _then) = __$NotificationTeamPreferenceDtoCopyWithImpl;
@override @useResult
$Res call({
 String teamSlug, String teamName, bool muted
});




}
/// @nodoc
class __$NotificationTeamPreferenceDtoCopyWithImpl<$Res>
    implements _$NotificationTeamPreferenceDtoCopyWith<$Res> {
  __$NotificationTeamPreferenceDtoCopyWithImpl(this._self, this._then);

  final _NotificationTeamPreferenceDto _self;
  final $Res Function(_NotificationTeamPreferenceDto) _then;

/// Create a copy of NotificationTeamPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teamSlug = null,Object? teamName = null,Object? muted = null,}) {
  return _then(_NotificationTeamPreferenceDto(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,muted: null == muted ? _self.muted : muted // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
