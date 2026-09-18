// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'notification_preference_update.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$NotificationPreferenceUpdate {

/// Notification type
 String get type;/// Delivery channel. IN_APP is refused: the inbox is always on.
 String get channel;/// Whether to deliver it
 bool get enabled;
/// Create a copy of NotificationPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$NotificationPreferenceUpdateCopyWith<NotificationPreferenceUpdate> get copyWith => _$NotificationPreferenceUpdateCopyWithImpl<NotificationPreferenceUpdate>(this as NotificationPreferenceUpdate, _$identity);

  /// Serializes this NotificationPreferenceUpdate to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as NotificationPreferenceUpdate;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is NotificationPreferenceUpdate&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.channel, _this.channel) || other.channel == _this.channel)&&(identical(other.enabled, _this.enabled) || other.enabled == _this.enabled));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as NotificationPreferenceUpdate;
  return Object.hash(runtimeType,_this.type,_this.channel,_this.enabled);
}

@override
String toString() {
  final _this = this as NotificationPreferenceUpdate;
  return 'NotificationPreferenceUpdate(type: ${_this.type}, channel: ${_this.channel}, enabled: ${_this.enabled})';
}


}

/// @nodoc
abstract mixin class $NotificationPreferenceUpdateCopyWith<$Res>  {
  factory $NotificationPreferenceUpdateCopyWith(NotificationPreferenceUpdate value, $Res Function(NotificationPreferenceUpdate) _then) = _$NotificationPreferenceUpdateCopyWithImpl;
@useResult
$Res call({
 String type, String channel, bool enabled
});




}
/// @nodoc
class _$NotificationPreferenceUpdateCopyWithImpl<$Res>
    implements $NotificationPreferenceUpdateCopyWith<$Res> {
  _$NotificationPreferenceUpdateCopyWithImpl(this._self, this._then);

  final NotificationPreferenceUpdate _self;
  final $Res Function(NotificationPreferenceUpdate) _then;

/// Create a copy of NotificationPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? channel = null,Object? enabled = null,}) {
  return _then(NotificationPreferenceUpdate(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,channel: null == channel ? _self.channel : channel // ignore: cast_nullable_to_non_nullable
as String,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [NotificationPreferenceUpdate].
extension NotificationPreferenceUpdatePatterns on NotificationPreferenceUpdate {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _NotificationPreferenceUpdate value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _NotificationPreferenceUpdate() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _NotificationPreferenceUpdate value)  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferenceUpdate():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _NotificationPreferenceUpdate value)?  $default,){
final _that = this;
switch (_that) {
case _NotificationPreferenceUpdate() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String channel,  bool enabled)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _NotificationPreferenceUpdate() when $default != null:
return $default(_that.type,_that.channel,_that.enabled);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String channel,  bool enabled)  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferenceUpdate():
return $default(_that.type,_that.channel,_that.enabled);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String channel,  bool enabled)?  $default,) {final _that = this;
switch (_that) {
case _NotificationPreferenceUpdate() when $default != null:
return $default(_that.type,_that.channel,_that.enabled);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _NotificationPreferenceUpdate implements NotificationPreferenceUpdate {
  const _NotificationPreferenceUpdate({required this.type, required this.channel, required this.enabled});
  factory _NotificationPreferenceUpdate.fromJson(Map<String, dynamic> json) => _$NotificationPreferenceUpdateFromJson(json);

/// Notification type
@override final  String type;
/// Delivery channel. IN_APP is refused: the inbox is always on.
@override final  String channel;
/// Whether to deliver it
@override final  bool enabled;

/// Create a copy of NotificationPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$NotificationPreferenceUpdateCopyWith<_NotificationPreferenceUpdate> get copyWith => __$NotificationPreferenceUpdateCopyWithImpl<_NotificationPreferenceUpdate>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$NotificationPreferenceUpdateToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _NotificationPreferenceUpdate&&(identical(other.type, type) || other.type == type)&&(identical(other.channel, channel) || other.channel == channel)&&(identical(other.enabled, enabled) || other.enabled == enabled));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,channel,enabled);
}

@override
String toString() {
    return 'NotificationPreferenceUpdate(type: $type, channel: $channel, enabled: $enabled)';
}


}

/// @nodoc
abstract mixin class _$NotificationPreferenceUpdateCopyWith<$Res> implements $NotificationPreferenceUpdateCopyWith<$Res> {
  factory _$NotificationPreferenceUpdateCopyWith(_NotificationPreferenceUpdate value, $Res Function(_NotificationPreferenceUpdate) _then) = __$NotificationPreferenceUpdateCopyWithImpl;
@override @useResult
$Res call({
 String type, String channel, bool enabled
});




}
/// @nodoc
class __$NotificationPreferenceUpdateCopyWithImpl<$Res>
    implements _$NotificationPreferenceUpdateCopyWith<$Res> {
  __$NotificationPreferenceUpdateCopyWithImpl(this._self, this._then);

  final _NotificationPreferenceUpdate _self;
  final $Res Function(_NotificationPreferenceUpdate) _then;

/// Create a copy of NotificationPreferenceUpdate
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? channel = null,Object? enabled = null,}) {
  return _then(_NotificationPreferenceUpdate(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,channel: null == channel ? _self.channel : channel // ignore: cast_nullable_to_non_nullable
as String,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
