// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'notification_preference_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$NotificationPreferenceDto {

/// Notification type
 String get type;/// Delivery channel
 String get channel;/// Whether it is delivered, as currently in effect
 bool get enabled;/// What applies when the user never touched this cell. Lets a client offer a 'restore defaults' without hard-coding them.
 bool get enabledByDefault;
/// Create a copy of NotificationPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$NotificationPreferenceDtoCopyWith<NotificationPreferenceDto> get copyWith => _$NotificationPreferenceDtoCopyWithImpl<NotificationPreferenceDto>(this as NotificationPreferenceDto, _$identity);

  /// Serializes this NotificationPreferenceDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as NotificationPreferenceDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is NotificationPreferenceDto&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.channel, _this.channel) || other.channel == _this.channel)&&(identical(other.enabled, _this.enabled) || other.enabled == _this.enabled)&&(identical(other.enabledByDefault, _this.enabledByDefault) || other.enabledByDefault == _this.enabledByDefault));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as NotificationPreferenceDto;
  return Object.hash(runtimeType,_this.type,_this.channel,_this.enabled,_this.enabledByDefault);
}

@override
String toString() {
  final _this = this as NotificationPreferenceDto;
  return 'NotificationPreferenceDto(type: ${_this.type}, channel: ${_this.channel}, enabled: ${_this.enabled}, enabledByDefault: ${_this.enabledByDefault})';
}


}

/// @nodoc
abstract mixin class $NotificationPreferenceDtoCopyWith<$Res>  {
  factory $NotificationPreferenceDtoCopyWith(NotificationPreferenceDto value, $Res Function(NotificationPreferenceDto) _then) = _$NotificationPreferenceDtoCopyWithImpl;
@useResult
$Res call({
 String type, String channel, bool enabled, bool enabledByDefault
});




}
/// @nodoc
class _$NotificationPreferenceDtoCopyWithImpl<$Res>
    implements $NotificationPreferenceDtoCopyWith<$Res> {
  _$NotificationPreferenceDtoCopyWithImpl(this._self, this._then);

  final NotificationPreferenceDto _self;
  final $Res Function(NotificationPreferenceDto) _then;

/// Create a copy of NotificationPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? channel = null,Object? enabled = null,Object? enabledByDefault = null,}) {
  return _then(NotificationPreferenceDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,channel: null == channel ? _self.channel : channel // ignore: cast_nullable_to_non_nullable
as String,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,enabledByDefault: null == enabledByDefault ? _self.enabledByDefault : enabledByDefault // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [NotificationPreferenceDto].
extension NotificationPreferenceDtoPatterns on NotificationPreferenceDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _NotificationPreferenceDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _NotificationPreferenceDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _NotificationPreferenceDto value)  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferenceDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _NotificationPreferenceDto value)?  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferenceDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String channel,  bool enabled,  bool enabledByDefault)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _NotificationPreferenceDto() when $default != null:
return $default(_that.type,_that.channel,_that.enabled,_that.enabledByDefault);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String channel,  bool enabled,  bool enabledByDefault)  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferenceDto():
return $default(_that.type,_that.channel,_that.enabled,_that.enabledByDefault);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String channel,  bool enabled,  bool enabledByDefault)?  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferenceDto() when $default != null:
return $default(_that.type,_that.channel,_that.enabled,_that.enabledByDefault);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _NotificationPreferenceDto implements NotificationPreferenceDto {
  const _NotificationPreferenceDto({required this.type, required this.channel, required this.enabled, required this.enabledByDefault});
  factory _NotificationPreferenceDto.fromJson(Map<String, dynamic> json) => _$NotificationPreferenceDtoFromJson(json);

/// Notification type
@override final  String type;
/// Delivery channel
@override final  String channel;
/// Whether it is delivered, as currently in effect
@override final  bool enabled;
/// What applies when the user never touched this cell. Lets a client offer a 'restore defaults' without hard-coding them.
@override final  bool enabledByDefault;

/// Create a copy of NotificationPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$NotificationPreferenceDtoCopyWith<_NotificationPreferenceDto> get copyWith => __$NotificationPreferenceDtoCopyWithImpl<_NotificationPreferenceDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$NotificationPreferenceDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _NotificationPreferenceDto&&(identical(other.type, type) || other.type == type)&&(identical(other.channel, channel) || other.channel == channel)&&(identical(other.enabled, enabled) || other.enabled == enabled)&&(identical(other.enabledByDefault, enabledByDefault) || other.enabledByDefault == enabledByDefault));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,channel,enabled,enabledByDefault);
}

@override
String toString() {
    return 'NotificationPreferenceDto(type: $type, channel: $channel, enabled: $enabled, enabledByDefault: $enabledByDefault)';
}


}

/// @nodoc
abstract mixin class _$NotificationPreferenceDtoCopyWith<$Res> implements $NotificationPreferenceDtoCopyWith<$Res> {
  factory _$NotificationPreferenceDtoCopyWith(_NotificationPreferenceDto value, $Res Function(_NotificationPreferenceDto) _then) = __$NotificationPreferenceDtoCopyWithImpl;
@override @useResult
$Res call({
 String type, String channel, bool enabled, bool enabledByDefault
});




}
/// @nodoc
class __$NotificationPreferenceDtoCopyWithImpl<$Res>
    implements _$NotificationPreferenceDtoCopyWith<$Res> {
  __$NotificationPreferenceDtoCopyWithImpl(this._self, this._then);

  final _NotificationPreferenceDto _self;
  final $Res Function(_NotificationPreferenceDto) _then;

/// Create a copy of NotificationPreferenceDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? channel = null,Object? enabled = null,Object? enabledByDefault = null,}) {
  return _then(_NotificationPreferenceDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,channel: null == channel ? _self.channel : channel // ignore: cast_nullable_to_non_nullable
as String,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,enabledByDefault: null == enabledByDefault ? _self.enabledByDefault : enabledByDefault // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
