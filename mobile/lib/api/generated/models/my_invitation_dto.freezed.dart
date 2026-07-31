// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'my_invitation_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MyInvitationDto {

/// Invitation ID (TSID)
 String get id;/// Team being offered
 TeamPublicationDto get team;/// Display name of whoever sent it
 String get inviterName;/// Role granted on acceptance
 String get role;/// When it stops being redeemable
 String get expiresAt;
/// Create a copy of MyInvitationDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MyInvitationDtoCopyWith<MyInvitationDto> get copyWith => _$MyInvitationDtoCopyWithImpl<MyInvitationDto>(this as MyInvitationDto, _$identity);

  /// Serializes this MyInvitationDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MyInvitationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.team, team) || other.team == team)&&(identical(other.inviterName, inviterName) || other.inviterName == inviterName)&&(identical(other.role, role) || other.role == role)&&(identical(other.expiresAt, expiresAt) || other.expiresAt == expiresAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,team,inviterName,role,expiresAt);

@override
String toString() {
  return 'MyInvitationDto(id: $id, team: $team, inviterName: $inviterName, role: $role, expiresAt: $expiresAt)';
}


}

/// @nodoc
abstract mixin class $MyInvitationDtoCopyWith<$Res>  {
  factory $MyInvitationDtoCopyWith(MyInvitationDto value, $Res Function(MyInvitationDto) _then) = _$MyInvitationDtoCopyWithImpl;
@useResult
$Res call({
 String id, TeamPublicationDto team, String inviterName, String role, String expiresAt
});


$TeamPublicationDtoCopyWith<$Res> get team;

}
/// @nodoc
class _$MyInvitationDtoCopyWithImpl<$Res>
    implements $MyInvitationDtoCopyWith<$Res> {
  _$MyInvitationDtoCopyWithImpl(this._self, this._then);

  final MyInvitationDto _self;
  final $Res Function(MyInvitationDto) _then;

/// Create a copy of MyInvitationDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? team = null,Object? inviterName = null,Object? role = null,Object? expiresAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,inviterName: null == inviterName ? _self.inviterName : inviterName // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,expiresAt: null == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}
/// Create a copy of MyInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}
}


/// Adds pattern-matching-related methods to [MyInvitationDto].
extension MyInvitationDtoPatterns on MyInvitationDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MyInvitationDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MyInvitationDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MyInvitationDto value)  $default,){
final _that = this;
switch (_that) {
case _MyInvitationDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MyInvitationDto value)?  $default,){
final _that = this;
switch (_that) {
case _MyInvitationDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  TeamPublicationDto team,  String inviterName,  String role,  String expiresAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MyInvitationDto() when $default != null:
return $default(_that.id,_that.team,_that.inviterName,_that.role,_that.expiresAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  TeamPublicationDto team,  String inviterName,  String role,  String expiresAt)  $default,) {final _that = this;
switch (_that) {
case _MyInvitationDto():
return $default(_that.id,_that.team,_that.inviterName,_that.role,_that.expiresAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  TeamPublicationDto team,  String inviterName,  String role,  String expiresAt)?  $default,) {final _that = this;
switch (_that) {
case _MyInvitationDto() when $default != null:
return $default(_that.id,_that.team,_that.inviterName,_that.role,_that.expiresAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MyInvitationDto implements MyInvitationDto {
  const _MyInvitationDto({required this.id, required this.team, required this.inviterName, required this.role, required this.expiresAt});
  factory _MyInvitationDto.fromJson(Map<String, dynamic> json) => _$MyInvitationDtoFromJson(json);

/// Invitation ID (TSID)
@override final  String id;
/// Team being offered
@override final  TeamPublicationDto team;
/// Display name of whoever sent it
@override final  String inviterName;
/// Role granted on acceptance
@override final  String role;
/// When it stops being redeemable
@override final  String expiresAt;

/// Create a copy of MyInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MyInvitationDtoCopyWith<_MyInvitationDto> get copyWith => __$MyInvitationDtoCopyWithImpl<_MyInvitationDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MyInvitationDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MyInvitationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.team, team) || other.team == team)&&(identical(other.inviterName, inviterName) || other.inviterName == inviterName)&&(identical(other.role, role) || other.role == role)&&(identical(other.expiresAt, expiresAt) || other.expiresAt == expiresAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,team,inviterName,role,expiresAt);

@override
String toString() {
  return 'MyInvitationDto(id: $id, team: $team, inviterName: $inviterName, role: $role, expiresAt: $expiresAt)';
}


}

/// @nodoc
abstract mixin class _$MyInvitationDtoCopyWith<$Res> implements $MyInvitationDtoCopyWith<$Res> {
  factory _$MyInvitationDtoCopyWith(_MyInvitationDto value, $Res Function(_MyInvitationDto) _then) = __$MyInvitationDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, TeamPublicationDto team, String inviterName, String role, String expiresAt
});


@override $TeamPublicationDtoCopyWith<$Res> get team;

}
/// @nodoc
class __$MyInvitationDtoCopyWithImpl<$Res>
    implements _$MyInvitationDtoCopyWith<$Res> {
  __$MyInvitationDtoCopyWithImpl(this._self, this._then);

  final _MyInvitationDto _self;
  final $Res Function(_MyInvitationDto) _then;

/// Create a copy of MyInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? team = null,Object? inviterName = null,Object? role = null,Object? expiresAt = null,}) {
  return _then(_MyInvitationDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,inviterName: null == inviterName ? _self.inviterName : inviterName // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,expiresAt: null == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

/// Create a copy of MyInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}
}

// dart format on
