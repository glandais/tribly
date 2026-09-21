// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'notification_preferences_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$NotificationPreferencesRequest {

/// The cells to change
 List<NotificationPreferenceUpdate> get preferences;/// The teams to mute or unmute
 List<NotificationTeamPreferenceUpdate>? get teams;/// Switch the daily e-mail digest on or off; absent leaves it
 bool? get emailDigest;
/// Create a copy of NotificationPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$NotificationPreferencesRequestCopyWith<NotificationPreferencesRequest> get copyWith => _$NotificationPreferencesRequestCopyWithImpl<NotificationPreferencesRequest>(this as NotificationPreferencesRequest, _$identity);

  /// Serializes this NotificationPreferencesRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as NotificationPreferencesRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is NotificationPreferencesRequest&&const DeepCollectionEquality().equals(other.preferences, _this.preferences)&&const DeepCollectionEquality().equals(other.teams, _this.teams)&&(identical(other.emailDigest, _this.emailDigest) || other.emailDigest == _this.emailDigest));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as NotificationPreferencesRequest;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.preferences),const DeepCollectionEquality().hash(_this.teams),_this.emailDigest);
}

@override
String toString() {
  final _this = this as NotificationPreferencesRequest;
  return 'NotificationPreferencesRequest(preferences: ${_this.preferences}, teams: ${_this.teams}, emailDigest: ${_this.emailDigest})';
}


}

/// @nodoc
abstract mixin class $NotificationPreferencesRequestCopyWith<$Res>  {
  factory $NotificationPreferencesRequestCopyWith(NotificationPreferencesRequest value, $Res Function(NotificationPreferencesRequest) _then) = _$NotificationPreferencesRequestCopyWithImpl;
@useResult
$Res call({
 List<NotificationPreferenceUpdate> preferences, List<NotificationTeamPreferenceUpdate>? teams, bool? emailDigest
});




}
/// @nodoc
class _$NotificationPreferencesRequestCopyWithImpl<$Res>
    implements $NotificationPreferencesRequestCopyWith<$Res> {
  _$NotificationPreferencesRequestCopyWithImpl(this._self, this._then);

  final NotificationPreferencesRequest _self;
  final $Res Function(NotificationPreferencesRequest) _then;

/// Create a copy of NotificationPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? preferences = null,Object? teams = freezed,Object? emailDigest = freezed,}) {
  return _then(NotificationPreferencesRequest(
preferences: null == preferences ? _self.preferences : preferences // ignore: cast_nullable_to_non_nullable
as List<NotificationPreferenceUpdate>,teams: freezed == teams ? _self.teams : teams // ignore: cast_nullable_to_non_nullable
as List<NotificationTeamPreferenceUpdate>?,emailDigest: freezed == emailDigest ? _self.emailDigest : emailDigest // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}

}


/// Adds pattern-matching-related methods to [NotificationPreferencesRequest].
extension NotificationPreferencesRequestPatterns on NotificationPreferencesRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _NotificationPreferencesRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _NotificationPreferencesRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _NotificationPreferencesRequest value)  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferencesRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _NotificationPreferencesRequest value)?  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferencesRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<NotificationPreferenceUpdate> preferences,  List<NotificationTeamPreferenceUpdate>? teams,  bool? emailDigest)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _NotificationPreferencesRequest() when $default != null:
return $default(_that.preferences,_that.teams,_that.emailDigest);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<NotificationPreferenceUpdate> preferences,  List<NotificationTeamPreferenceUpdate>? teams,  bool? emailDigest)  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferencesRequest():
return $default(_that.preferences,_that.teams,_that.emailDigest);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<NotificationPreferenceUpdate> preferences,  List<NotificationTeamPreferenceUpdate>? teams,  bool? emailDigest)?  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferencesRequest() when $default != null:
return $default(_that.preferences,_that.teams,_that.emailDigest);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _NotificationPreferencesRequest implements NotificationPreferencesRequest {
  const _NotificationPreferencesRequest({required  List<NotificationPreferenceUpdate> preferences,  List<NotificationTeamPreferenceUpdate>? teams, this.emailDigest}): _preferences = preferences,_teams = teams;
  factory _NotificationPreferencesRequest.fromJson(Map<String, dynamic> json) => _$NotificationPreferencesRequestFromJson(json);

/// The cells to change
 final  List<NotificationPreferenceUpdate> _preferences;
/// The cells to change
@override List<NotificationPreferenceUpdate> get preferences {
  if (_preferences is EqualUnmodifiableListView) return _preferences;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_preferences);
}

/// The teams to mute or unmute
 final  List<NotificationTeamPreferenceUpdate>? _teams;
/// The teams to mute or unmute
@override List<NotificationTeamPreferenceUpdate>? get teams {
  final value = _teams;
  if (value == null) return null;
  if (_teams is EqualUnmodifiableListView) return _teams;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

/// Switch the daily e-mail digest on or off; absent leaves it
@override final  bool? emailDigest;

/// Create a copy of NotificationPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$NotificationPreferencesRequestCopyWith<_NotificationPreferencesRequest> get copyWith => __$NotificationPreferencesRequestCopyWithImpl<_NotificationPreferencesRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$NotificationPreferencesRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _NotificationPreferencesRequest&&const DeepCollectionEquality().equals(other.preferences, _preferences)&&const DeepCollectionEquality().equals(other.teams, _teams)&&(identical(other.emailDigest, emailDigest) || other.emailDigest == emailDigest));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_preferences),const DeepCollectionEquality().hash(_teams),emailDigest);
}

@override
String toString() {
    return 'NotificationPreferencesRequest(preferences: $preferences, teams: $teams, emailDigest: $emailDigest)';
}


}

/// @nodoc
abstract mixin class _$NotificationPreferencesRequestCopyWith<$Res> implements $NotificationPreferencesRequestCopyWith<$Res> {
  factory _$NotificationPreferencesRequestCopyWith(_NotificationPreferencesRequest value, $Res Function(_NotificationPreferencesRequest) _then) = __$NotificationPreferencesRequestCopyWithImpl;
@override @useResult
$Res call({
 List<NotificationPreferenceUpdate> preferences, List<NotificationTeamPreferenceUpdate>? teams, bool? emailDigest
});




}
/// @nodoc
class __$NotificationPreferencesRequestCopyWithImpl<$Res>
    implements _$NotificationPreferencesRequestCopyWith<$Res> {
  __$NotificationPreferencesRequestCopyWithImpl(this._self, this._then);

  final _NotificationPreferencesRequest _self;
  final $Res Function(_NotificationPreferencesRequest) _then;

/// Create a copy of NotificationPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? preferences = null,Object? teams = freezed,Object? emailDigest = freezed,}) {
  return _then(_NotificationPreferencesRequest(
preferences: null == preferences ? _self._preferences : preferences // ignore: cast_nullable_to_non_nullable
as List<NotificationPreferenceUpdate>,teams: freezed == teams ? _self._teams : teams // ignore: cast_nullable_to_non_nullable
as List<NotificationTeamPreferenceUpdate>?,emailDigest: freezed == emailDigest ? _self.emailDigest : emailDigest // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}


}

// dart format on
