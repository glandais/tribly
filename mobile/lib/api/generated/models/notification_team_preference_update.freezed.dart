// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'notification_team_preference_update.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$NotificationTeamPreferenceUpdate {

/// Team slug — a team the user belongs to
 String get teamSlug;/// Whether to mute its announcements
 bool get muted;
/// Create a copy of NotificationTeamPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$NotificationTeamPreferenceUpdateCopyWith<NotificationTeamPreferenceUpdate> get copyWith => _$NotificationTeamPreferenceUpdateCopyWithImpl<NotificationTeamPreferenceUpdate>(this as NotificationTeamPreferenceUpdate, _$identity);

  /// Serializes this NotificationTeamPreferenceUpdate to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as NotificationTeamPreferenceUpdate;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is NotificationTeamPreferenceUpdate&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.muted, _this.muted) || other.muted == _this.muted));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as NotificationTeamPreferenceUpdate;
  return Object.hash(runtimeType,_this.teamSlug,_this.muted);
}

@override
String toString() {
  final _this = this as NotificationTeamPreferenceUpdate;
  return 'NotificationTeamPreferenceUpdate(teamSlug: ${_this.teamSlug}, muted: ${_this.muted})';
}


}

/// @nodoc
abstract mixin class $NotificationTeamPreferenceUpdateCopyWith<$Res>  {
  factory $NotificationTeamPreferenceUpdateCopyWith(NotificationTeamPreferenceUpdate value, $Res Function(NotificationTeamPreferenceUpdate) _then) = _$NotificationTeamPreferenceUpdateCopyWithImpl;
@useResult
$Res call({
 String teamSlug, bool muted
});




}
/// @nodoc
class _$NotificationTeamPreferenceUpdateCopyWithImpl<$Res>
    implements $NotificationTeamPreferenceUpdateCopyWith<$Res> {
  _$NotificationTeamPreferenceUpdateCopyWithImpl(this._self, this._then);

  final NotificationTeamPreferenceUpdate _self;
  final $Res Function(NotificationTeamPreferenceUpdate) _then;

/// Create a copy of NotificationTeamPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teamSlug = null,Object? muted = null,}) {
  return _then(NotificationTeamPreferenceUpdate(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,muted: null == muted ? _self.muted : muted // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [NotificationTeamPreferenceUpdate].
extension NotificationTeamPreferenceUpdatePatterns on NotificationTeamPreferenceUpdate {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _NotificationTeamPreferenceUpdate value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _NotificationTeamPreferenceUpdate() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _NotificationTeamPreferenceUpdate value)  $default,){
final _that = this;
switch (_that) {
case _NotificationTeamPreferenceUpdate():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _NotificationTeamPreferenceUpdate value)?  $default,){
final _that = this;
switch (_that) {
case _NotificationTeamPreferenceUpdate() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String teamSlug,  bool muted)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _NotificationTeamPreferenceUpdate() when $default != null:
return $default(_that.teamSlug,_that.muted);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String teamSlug,  bool muted)  $default,) {final _that = this;
switch (_that) {
case _NotificationTeamPreferenceUpdate():
return $default(_that.teamSlug,_that.muted);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String teamSlug,  bool muted)?  $default,) {final _that = this;
switch (_that) {
case _NotificationTeamPreferenceUpdate() when $default != null:
return $default(_that.teamSlug,_that.muted);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _NotificationTeamPreferenceUpdate implements NotificationTeamPreferenceUpdate {
  const _NotificationTeamPreferenceUpdate({required this.teamSlug, required this.muted});
  factory _NotificationTeamPreferenceUpdate.fromJson(Map<String, dynamic> json) => _$NotificationTeamPreferenceUpdateFromJson(json);

/// Team slug — a team the user belongs to
@override final  String teamSlug;
/// Whether to mute its announcements
@override final  bool muted;

/// Create a copy of NotificationTeamPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$NotificationTeamPreferenceUpdateCopyWith<_NotificationTeamPreferenceUpdate> get copyWith => __$NotificationTeamPreferenceUpdateCopyWithImpl<_NotificationTeamPreferenceUpdate>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$NotificationTeamPreferenceUpdateToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _NotificationTeamPreferenceUpdate&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.muted, muted) || other.muted == muted));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,teamSlug,muted);
}

@override
String toString() {
    return 'NotificationTeamPreferenceUpdate(teamSlug: $teamSlug, muted: $muted)';
}


}

/// @nodoc
abstract mixin class _$NotificationTeamPreferenceUpdateCopyWith<$Res> implements $NotificationTeamPreferenceUpdateCopyWith<$Res> {
  factory _$NotificationTeamPreferenceUpdateCopyWith(_NotificationTeamPreferenceUpdate value, $Res Function(_NotificationTeamPreferenceUpdate) _then) = __$NotificationTeamPreferenceUpdateCopyWithImpl;
@override @useResult
$Res call({
 String teamSlug, bool muted
});




}
/// @nodoc
class __$NotificationTeamPreferenceUpdateCopyWithImpl<$Res>
    implements _$NotificationTeamPreferenceUpdateCopyWith<$Res> {
  __$NotificationTeamPreferenceUpdateCopyWithImpl(this._self, this._then);

  final _NotificationTeamPreferenceUpdate _self;
  final $Res Function(_NotificationTeamPreferenceUpdate) _then;

/// Create a copy of NotificationTeamPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teamSlug = null,Object? muted = null,}) {
  return _then(_NotificationTeamPreferenceUpdate(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,muted: null == muted ? _self.muted : muted // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
