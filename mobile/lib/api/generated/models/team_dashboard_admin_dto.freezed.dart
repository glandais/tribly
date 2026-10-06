// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_dashboard_admin_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamDashboardAdminDto {

/// The newest members, latest joined first (at most 3), with role and joinedAt. total is the member count.
 MemberListResponse get newestMembers;/// The team's webhook: configured, kind, enabled, lastStatus, lastAttemptAt. The URL only comes masked.
 TeamWebhookDto get webhook;
/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamDashboardAdminDtoCopyWith<TeamDashboardAdminDto> get copyWith => _$TeamDashboardAdminDtoCopyWithImpl<TeamDashboardAdminDto>(this as TeamDashboardAdminDto, _$identity);

  /// Serializes this TeamDashboardAdminDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamDashboardAdminDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamDashboardAdminDto&&(identical(other.newestMembers, _this.newestMembers) || other.newestMembers == _this.newestMembers)&&(identical(other.webhook, _this.webhook) || other.webhook == _this.webhook));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamDashboardAdminDto;
  return Object.hash(runtimeType,_this.newestMembers,_this.webhook);
}

@override
String toString() {
  final _this = this as TeamDashboardAdminDto;
  return 'TeamDashboardAdminDto(newestMembers: ${_this.newestMembers}, webhook: ${_this.webhook})';
}


}

/// @nodoc
abstract mixin class $TeamDashboardAdminDtoCopyWith<$Res>  {
  factory $TeamDashboardAdminDtoCopyWith(TeamDashboardAdminDto value, $Res Function(TeamDashboardAdminDto) _then) = _$TeamDashboardAdminDtoCopyWithImpl;
@useResult
$Res call({
 MemberListResponse newestMembers, TeamWebhookDto webhook
});


$MemberListResponseCopyWith<$Res> get newestMembers;$TeamWebhookDtoCopyWith<$Res> get webhook;

}
/// @nodoc
class _$TeamDashboardAdminDtoCopyWithImpl<$Res>
    implements $TeamDashboardAdminDtoCopyWith<$Res> {
  _$TeamDashboardAdminDtoCopyWithImpl(this._self, this._then);

  final TeamDashboardAdminDto _self;
  final $Res Function(TeamDashboardAdminDto) _then;

/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? newestMembers = null,Object? webhook = null,}) {
  return _then(TeamDashboardAdminDto(
newestMembers: null == newestMembers ? _self.newestMembers : newestMembers // ignore: cast_nullable_to_non_nullable
as MemberListResponse,webhook: null == webhook ? _self.webhook : webhook // ignore: cast_nullable_to_non_nullable
as TeamWebhookDto,
  ));
}
/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MemberListResponseCopyWith<$Res> get newestMembers {
  
  return $MemberListResponseCopyWith<$Res>(_self.newestMembers, (value) {
    return _then(_self.copyWith(newestMembers: value));
  });
}/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamWebhookDtoCopyWith<$Res> get webhook {
  
  return $TeamWebhookDtoCopyWith<$Res>(_self.webhook, (value) {
    return _then(_self.copyWith(webhook: value));
  });
}
}


/// Adds pattern-matching-related methods to [TeamDashboardAdminDto].
extension TeamDashboardAdminDtoPatterns on TeamDashboardAdminDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamDashboardAdminDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamDashboardAdminDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamDashboardAdminDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamDashboardAdminDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamDashboardAdminDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamDashboardAdminDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( MemberListResponse newestMembers,  TeamWebhookDto webhook)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamDashboardAdminDto() when $default != null:
return $default(_that.newestMembers,_that.webhook);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( MemberListResponse newestMembers,  TeamWebhookDto webhook)  $default,) {final _that = this;
switch (_that) {
case _TeamDashboardAdminDto():
return $default(_that.newestMembers,_that.webhook);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( MemberListResponse newestMembers,  TeamWebhookDto webhook)?  $default,) {final _that = this;
switch (_that) {
case _TeamDashboardAdminDto() when $default != null:
return $default(_that.newestMembers,_that.webhook);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamDashboardAdminDto implements TeamDashboardAdminDto {
  const _TeamDashboardAdminDto({required this.newestMembers, required this.webhook});
  factory _TeamDashboardAdminDto.fromJson(Map<String, dynamic> json) => _$TeamDashboardAdminDtoFromJson(json);

/// The newest members, latest joined first (at most 3), with role and joinedAt. total is the member count.
@override final  MemberListResponse newestMembers;
/// The team's webhook: configured, kind, enabled, lastStatus, lastAttemptAt. The URL only comes masked.
@override final  TeamWebhookDto webhook;

/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamDashboardAdminDtoCopyWith<_TeamDashboardAdminDto> get copyWith => __$TeamDashboardAdminDtoCopyWithImpl<_TeamDashboardAdminDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamDashboardAdminDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamDashboardAdminDto&&(identical(other.newestMembers, newestMembers) || other.newestMembers == newestMembers)&&(identical(other.webhook, webhook) || other.webhook == webhook));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,newestMembers,webhook);
}

@override
String toString() {
    return 'TeamDashboardAdminDto(newestMembers: $newestMembers, webhook: $webhook)';
}


}

/// @nodoc
abstract mixin class _$TeamDashboardAdminDtoCopyWith<$Res> implements $TeamDashboardAdminDtoCopyWith<$Res> {
  factory _$TeamDashboardAdminDtoCopyWith(_TeamDashboardAdminDto value, $Res Function(_TeamDashboardAdminDto) _then) = __$TeamDashboardAdminDtoCopyWithImpl;
@override @useResult
$Res call({
 MemberListResponse newestMembers, TeamWebhookDto webhook
});


@override $MemberListResponseCopyWith<$Res> get newestMembers;@override $TeamWebhookDtoCopyWith<$Res> get webhook;

}
/// @nodoc
class __$TeamDashboardAdminDtoCopyWithImpl<$Res>
    implements _$TeamDashboardAdminDtoCopyWith<$Res> {
  __$TeamDashboardAdminDtoCopyWithImpl(this._self, this._then);

  final _TeamDashboardAdminDto _self;
  final $Res Function(_TeamDashboardAdminDto) _then;

/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? newestMembers = null,Object? webhook = null,}) {
  return _then(_TeamDashboardAdminDto(
newestMembers: null == newestMembers ? _self.newestMembers : newestMembers // ignore: cast_nullable_to_non_nullable
as MemberListResponse,webhook: null == webhook ? _self.webhook : webhook // ignore: cast_nullable_to_non_nullable
as TeamWebhookDto,
  ));
}

/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MemberListResponseCopyWith<$Res> get newestMembers {
  
  return $MemberListResponseCopyWith<$Res>(_self.newestMembers, (value) {
    return _then(_self.copyWith(newestMembers: value));
  });
}/// Create a copy of TeamDashboardAdminDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamWebhookDtoCopyWith<$Res> get webhook {
  
  return $TeamWebhookDtoCopyWith<$Res>(_self.webhook, (value) {
    return _then(_self.copyWith(webhook: value));
  });
}
}

// dart format on
