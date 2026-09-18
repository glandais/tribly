// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'notification_preferences_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$NotificationPreferencesDto {

/// Channels that can be configured on this server, in display order
 List<NotificationChannel> get channels;/// One cell per type and configurable channel
 List<NotificationPreferenceDto> get preferences;
/// Create a copy of NotificationPreferencesDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$NotificationPreferencesDtoCopyWith<NotificationPreferencesDto> get copyWith => _$NotificationPreferencesDtoCopyWithImpl<NotificationPreferencesDto>(this as NotificationPreferencesDto, _$identity);

  /// Serializes this NotificationPreferencesDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as NotificationPreferencesDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is NotificationPreferencesDto&&const DeepCollectionEquality().equals(other.channels, _this.channels)&&const DeepCollectionEquality().equals(other.preferences, _this.preferences));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as NotificationPreferencesDto;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.channels),const DeepCollectionEquality().hash(_this.preferences));
}

@override
String toString() {
  final _this = this as NotificationPreferencesDto;
  return 'NotificationPreferencesDto(channels: ${_this.channels}, preferences: ${_this.preferences})';
}


}

/// @nodoc
abstract mixin class $NotificationPreferencesDtoCopyWith<$Res>  {
  factory $NotificationPreferencesDtoCopyWith(NotificationPreferencesDto value, $Res Function(NotificationPreferencesDto) _then) = _$NotificationPreferencesDtoCopyWithImpl;
@useResult
$Res call({
 List<NotificationChannel> channels, List<NotificationPreferenceDto> preferences
});




}
/// @nodoc
class _$NotificationPreferencesDtoCopyWithImpl<$Res>
    implements $NotificationPreferencesDtoCopyWith<$Res> {
  _$NotificationPreferencesDtoCopyWithImpl(this._self, this._then);

  final NotificationPreferencesDto _self;
  final $Res Function(NotificationPreferencesDto) _then;

/// Create a copy of NotificationPreferencesDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? channels = null,Object? preferences = null,}) {
  return _then(NotificationPreferencesDto(
channels: null == channels ? _self.channels : channels // ignore: cast_nullable_to_non_nullable
as List<NotificationChannel>,preferences: null == preferences ? _self.preferences : preferences // ignore: cast_nullable_to_non_nullable
as List<NotificationPreferenceDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [NotificationPreferencesDto].
extension NotificationPreferencesDtoPatterns on NotificationPreferencesDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _NotificationPreferencesDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _NotificationPreferencesDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _NotificationPreferencesDto value)  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferencesDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _NotificationPreferencesDto value)?  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferencesDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<NotificationChannel> channels,  List<NotificationPreferenceDto> preferences)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _NotificationPreferencesDto() when $default != null:
return $default(_that.channels,_that.preferences);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<NotificationChannel> channels,  List<NotificationPreferenceDto> preferences)  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferencesDto():
return $default(_that.channels,_that.preferences);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<NotificationChannel> channels,  List<NotificationPreferenceDto> preferences)?  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferencesDto() when $default != null:
return $default(_that.channels,_that.preferences);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _NotificationPreferencesDto implements NotificationPreferencesDto {
  const _NotificationPreferencesDto({required  List<NotificationChannel> channels, required  List<NotificationPreferenceDto> preferences}): _channels = channels,_preferences = preferences;
  factory _NotificationPreferencesDto.fromJson(Map<String, dynamic> json) => _$NotificationPreferencesDtoFromJson(json);

/// Channels that can be configured on this server, in display order
 final  List<NotificationChannel> _channels;
/// Channels that can be configured on this server, in display order
@override List<NotificationChannel> get channels {
  if (_channels is EqualUnmodifiableListView) return _channels;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_channels);
}

/// One cell per type and configurable channel
 final  List<NotificationPreferenceDto> _preferences;
/// One cell per type and configurable channel
@override List<NotificationPreferenceDto> get preferences {
  if (_preferences is EqualUnmodifiableListView) return _preferences;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_preferences);
}


/// Create a copy of NotificationPreferencesDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$NotificationPreferencesDtoCopyWith<_NotificationPreferencesDto> get copyWith => __$NotificationPreferencesDtoCopyWithImpl<_NotificationPreferencesDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$NotificationPreferencesDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _NotificationPreferencesDto&&const DeepCollectionEquality().equals(other.channels, _channels)&&const DeepCollectionEquality().equals(other.preferences, _preferences));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_channels),const DeepCollectionEquality().hash(_preferences));
}

@override
String toString() {
    return 'NotificationPreferencesDto(channels: $channels, preferences: $preferences)';
}


}

/// @nodoc
abstract mixin class _$NotificationPreferencesDtoCopyWith<$Res> implements $NotificationPreferencesDtoCopyWith<$Res> {
  factory _$NotificationPreferencesDtoCopyWith(_NotificationPreferencesDto value, $Res Function(_NotificationPreferencesDto) _then) = __$NotificationPreferencesDtoCopyWithImpl;
@override @useResult
$Res call({
 List<NotificationChannel> channels, List<NotificationPreferenceDto> preferences
});




}
/// @nodoc
class __$NotificationPreferencesDtoCopyWithImpl<$Res>
    implements _$NotificationPreferencesDtoCopyWith<$Res> {
  __$NotificationPreferencesDtoCopyWithImpl(this._self, this._then);

  final _NotificationPreferencesDto _self;
  final $Res Function(_NotificationPreferencesDto) _then;

/// Create a copy of NotificationPreferencesDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? channels = null,Object? preferences = null,}) {
  return _then(_NotificationPreferencesDto(
channels: null == channels ? _self._channels : channels // ignore: cast_nullable_to_non_nullable
as List<NotificationChannel>,preferences: null == preferences ? _self._preferences : preferences // ignore: cast_nullable_to_non_nullable
as List<NotificationPreferenceDto>,
  ));
}


}

// dart format on
