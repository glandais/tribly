// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'profile_notification_summary_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ProfileNotificationSummaryDto {

/// Channels that can be configured on this server, in display order
 List<NotificationChannel> get channels;/// Among channels, those on which at least one notification type is turned on for the user (their choices, or the defaults they never changed)
 List<NotificationChannel> get enabledChannels;/// Whether non-urgent e-mails are held for a daily digest. Always false when EMAIL is not among channels.
 bool get emailDigest;
/// Create a copy of ProfileNotificationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ProfileNotificationSummaryDtoCopyWith<ProfileNotificationSummaryDto> get copyWith => _$ProfileNotificationSummaryDtoCopyWithImpl<ProfileNotificationSummaryDto>(this as ProfileNotificationSummaryDto, _$identity);

  /// Serializes this ProfileNotificationSummaryDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ProfileNotificationSummaryDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ProfileNotificationSummaryDto&&const DeepCollectionEquality().equals(other.channels, _this.channels)&&const DeepCollectionEquality().equals(other.enabledChannels, _this.enabledChannels)&&(identical(other.emailDigest, _this.emailDigest) || other.emailDigest == _this.emailDigest));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ProfileNotificationSummaryDto;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.channels),const DeepCollectionEquality().hash(_this.enabledChannels),_this.emailDigest);
}

@override
String toString() {
  final _this = this as ProfileNotificationSummaryDto;
  return 'ProfileNotificationSummaryDto(channels: ${_this.channels}, enabledChannels: ${_this.enabledChannels}, emailDigest: ${_this.emailDigest})';
}


}

/// @nodoc
abstract mixin class $ProfileNotificationSummaryDtoCopyWith<$Res>  {
  factory $ProfileNotificationSummaryDtoCopyWith(ProfileNotificationSummaryDto value, $Res Function(ProfileNotificationSummaryDto) _then) = _$ProfileNotificationSummaryDtoCopyWithImpl;
@useResult
$Res call({
 List<NotificationChannel> channels, List<NotificationChannel> enabledChannels, bool emailDigest
});




}
/// @nodoc
class _$ProfileNotificationSummaryDtoCopyWithImpl<$Res>
    implements $ProfileNotificationSummaryDtoCopyWith<$Res> {
  _$ProfileNotificationSummaryDtoCopyWithImpl(this._self, this._then);

  final ProfileNotificationSummaryDto _self;
  final $Res Function(ProfileNotificationSummaryDto) _then;

/// Create a copy of ProfileNotificationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? channels = null,Object? enabledChannels = null,Object? emailDigest = null,}) {
  return _then(ProfileNotificationSummaryDto(
channels: null == channels ? _self.channels : channels // ignore: cast_nullable_to_non_nullable
as List<NotificationChannel>,enabledChannels: null == enabledChannels ? _self.enabledChannels : enabledChannels // ignore: cast_nullable_to_non_nullable
as List<NotificationChannel>,emailDigest: null == emailDigest ? _self.emailDigest : emailDigest // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [ProfileNotificationSummaryDto].
extension ProfileNotificationSummaryDtoPatterns on ProfileNotificationSummaryDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ProfileNotificationSummaryDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ProfileNotificationSummaryDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ProfileNotificationSummaryDto value)  $default,){
final _that = this;
switch (_that) {
case _ProfileNotificationSummaryDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ProfileNotificationSummaryDto value)?  $default,){
final _that = this;
switch (_that) {
case _ProfileNotificationSummaryDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<NotificationChannel> channels,  List<NotificationChannel> enabledChannels,  bool emailDigest)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ProfileNotificationSummaryDto() when $default != null:
return $default(_that.channels,_that.enabledChannels,_that.emailDigest);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<NotificationChannel> channels,  List<NotificationChannel> enabledChannels,  bool emailDigest)  $default,) {final _that = this;
switch (_that) {
case _ProfileNotificationSummaryDto():
return $default(_that.channels,_that.enabledChannels,_that.emailDigest);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<NotificationChannel> channels,  List<NotificationChannel> enabledChannels,  bool emailDigest)?  $default,) {final _that = this;
switch (_that) {
case _ProfileNotificationSummaryDto() when $default != null:
return $default(_that.channels,_that.enabledChannels,_that.emailDigest);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ProfileNotificationSummaryDto implements ProfileNotificationSummaryDto {
  const _ProfileNotificationSummaryDto({required  List<NotificationChannel> channels, required  List<NotificationChannel> enabledChannels, required this.emailDigest}): _channels = channels,_enabledChannels = enabledChannels;
  factory _ProfileNotificationSummaryDto.fromJson(Map<String, dynamic> json) => _$ProfileNotificationSummaryDtoFromJson(json);

/// Channels that can be configured on this server, in display order
 final  List<NotificationChannel> _channels;
/// Channels that can be configured on this server, in display order
@override List<NotificationChannel> get channels {
  if (_channels is EqualUnmodifiableListView) return _channels;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_channels);
}

/// Among channels, those on which at least one notification type is turned on for the user (their choices, or the defaults they never changed)
 final  List<NotificationChannel> _enabledChannels;
/// Among channels, those on which at least one notification type is turned on for the user (their choices, or the defaults they never changed)
@override List<NotificationChannel> get enabledChannels {
  if (_enabledChannels is EqualUnmodifiableListView) return _enabledChannels;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_enabledChannels);
}

/// Whether non-urgent e-mails are held for a daily digest. Always false when EMAIL is not among channels.
@override final  bool emailDigest;

/// Create a copy of ProfileNotificationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ProfileNotificationSummaryDtoCopyWith<_ProfileNotificationSummaryDto> get copyWith => __$ProfileNotificationSummaryDtoCopyWithImpl<_ProfileNotificationSummaryDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ProfileNotificationSummaryDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ProfileNotificationSummaryDto&&const DeepCollectionEquality().equals(other.channels, _channels)&&const DeepCollectionEquality().equals(other.enabledChannels, _enabledChannels)&&(identical(other.emailDigest, emailDigest) || other.emailDigest == emailDigest));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_channels),const DeepCollectionEquality().hash(_enabledChannels),emailDigest);
}

@override
String toString() {
    return 'ProfileNotificationSummaryDto(channels: $channels, enabledChannels: $enabledChannels, emailDigest: $emailDigest)';
}


}

/// @nodoc
abstract mixin class _$ProfileNotificationSummaryDtoCopyWith<$Res> implements $ProfileNotificationSummaryDtoCopyWith<$Res> {
  factory _$ProfileNotificationSummaryDtoCopyWith(_ProfileNotificationSummaryDto value, $Res Function(_ProfileNotificationSummaryDto) _then) = __$ProfileNotificationSummaryDtoCopyWithImpl;
@override @useResult
$Res call({
 List<NotificationChannel> channels, List<NotificationChannel> enabledChannels, bool emailDigest
});




}
/// @nodoc
class __$ProfileNotificationSummaryDtoCopyWithImpl<$Res>
    implements _$ProfileNotificationSummaryDtoCopyWith<$Res> {
  __$ProfileNotificationSummaryDtoCopyWithImpl(this._self, this._then);

  final _ProfileNotificationSummaryDto _self;
  final $Res Function(_ProfileNotificationSummaryDto) _then;

/// Create a copy of ProfileNotificationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? channels = null,Object? enabledChannels = null,Object? emailDigest = null,}) {
  return _then(_ProfileNotificationSummaryDto(
channels: null == channels ? _self._channels : channels // ignore: cast_nullable_to_non_nullable
as List<NotificationChannel>,enabledChannels: null == enabledChannels ? _self._enabledChannels : enabledChannels // ignore: cast_nullable_to_non_nullable
as List<NotificationChannel>,emailDigest: null == emailDigest ? _self.emailDigest : emailDigest // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
