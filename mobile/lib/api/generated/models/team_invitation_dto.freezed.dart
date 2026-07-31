// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_invitation_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamInvitationDto {

/// Invitation ID (TSID)
 String get id;/// Invited e-mail address
 String get email;/// Role granted on acceptance
 String get role;/// Where the invitation stands
 String get status;/// When the invitation stops being redeemable
 String get expiresAt;/// When the invitation was sent
 String get createdAt;/// Who sent it
 PublicUserDto get invitedBy;/// When it was accepted, if it was
 String? get acceptedAt;
/// Create a copy of TeamInvitationDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamInvitationDtoCopyWith<TeamInvitationDto> get copyWith => _$TeamInvitationDtoCopyWithImpl<TeamInvitationDto>(this as TeamInvitationDto, _$identity);

  /// Serializes this TeamInvitationDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamInvitationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.email, email) || other.email == email)&&(identical(other.role, role) || other.role == role)&&(identical(other.status, status) || other.status == status)&&(identical(other.expiresAt, expiresAt) || other.expiresAt == expiresAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.invitedBy, invitedBy) || other.invitedBy == invitedBy)&&(identical(other.acceptedAt, acceptedAt) || other.acceptedAt == acceptedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,email,role,status,expiresAt,createdAt,invitedBy,acceptedAt);

@override
String toString() {
  return 'TeamInvitationDto(id: $id, email: $email, role: $role, status: $status, expiresAt: $expiresAt, createdAt: $createdAt, invitedBy: $invitedBy, acceptedAt: $acceptedAt)';
}


}

/// @nodoc
abstract mixin class $TeamInvitationDtoCopyWith<$Res>  {
  factory $TeamInvitationDtoCopyWith(TeamInvitationDto value, $Res Function(TeamInvitationDto) _then) = _$TeamInvitationDtoCopyWithImpl;
@useResult
$Res call({
 String id, String email, String role, String status, String expiresAt, String createdAt, PublicUserDto invitedBy, String? acceptedAt
});


$PublicUserDtoCopyWith<$Res> get invitedBy;

}
/// @nodoc
class _$TeamInvitationDtoCopyWithImpl<$Res>
    implements $TeamInvitationDtoCopyWith<$Res> {
  _$TeamInvitationDtoCopyWithImpl(this._self, this._then);

  final TeamInvitationDto _self;
  final $Res Function(TeamInvitationDto) _then;

/// Create a copy of TeamInvitationDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? email = null,Object? role = null,Object? status = null,Object? expiresAt = null,Object? createdAt = null,Object? invitedBy = null,Object? acceptedAt = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,expiresAt: null == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,invitedBy: null == invitedBy ? _self.invitedBy : invitedBy // ignore: cast_nullable_to_non_nullable
as PublicUserDto,acceptedAt: freezed == acceptedAt ? _self.acceptedAt : acceptedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of TeamInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get invitedBy {
  
  return $PublicUserDtoCopyWith<$Res>(_self.invitedBy, (value) {
    return _then(_self.copyWith(invitedBy: value));
  });
}
}


/// Adds pattern-matching-related methods to [TeamInvitationDto].
extension TeamInvitationDtoPatterns on TeamInvitationDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamInvitationDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamInvitationDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamInvitationDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamInvitationDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamInvitationDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamInvitationDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String email,  String role,  String status,  String expiresAt,  String createdAt,  PublicUserDto invitedBy,  String? acceptedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamInvitationDto() when $default != null:
return $default(_that.id,_that.email,_that.role,_that.status,_that.expiresAt,_that.createdAt,_that.invitedBy,_that.acceptedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String email,  String role,  String status,  String expiresAt,  String createdAt,  PublicUserDto invitedBy,  String? acceptedAt)  $default,) {final _that = this;
switch (_that) {
case _TeamInvitationDto():
return $default(_that.id,_that.email,_that.role,_that.status,_that.expiresAt,_that.createdAt,_that.invitedBy,_that.acceptedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String email,  String role,  String status,  String expiresAt,  String createdAt,  PublicUserDto invitedBy,  String? acceptedAt)?  $default,) {final _that = this;
switch (_that) {
case _TeamInvitationDto() when $default != null:
return $default(_that.id,_that.email,_that.role,_that.status,_that.expiresAt,_that.createdAt,_that.invitedBy,_that.acceptedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamInvitationDto implements TeamInvitationDto {
  const _TeamInvitationDto({required this.id, required this.email, required this.role, required this.status, required this.expiresAt, required this.createdAt, required this.invitedBy, this.acceptedAt});
  factory _TeamInvitationDto.fromJson(Map<String, dynamic> json) => _$TeamInvitationDtoFromJson(json);

/// Invitation ID (TSID)
@override final  String id;
/// Invited e-mail address
@override final  String email;
/// Role granted on acceptance
@override final  String role;
/// Where the invitation stands
@override final  String status;
/// When the invitation stops being redeemable
@override final  String expiresAt;
/// When the invitation was sent
@override final  String createdAt;
/// Who sent it
@override final  PublicUserDto invitedBy;
/// When it was accepted, if it was
@override final  String? acceptedAt;

/// Create a copy of TeamInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamInvitationDtoCopyWith<_TeamInvitationDto> get copyWith => __$TeamInvitationDtoCopyWithImpl<_TeamInvitationDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamInvitationDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamInvitationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.email, email) || other.email == email)&&(identical(other.role, role) || other.role == role)&&(identical(other.status, status) || other.status == status)&&(identical(other.expiresAt, expiresAt) || other.expiresAt == expiresAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.invitedBy, invitedBy) || other.invitedBy == invitedBy)&&(identical(other.acceptedAt, acceptedAt) || other.acceptedAt == acceptedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,email,role,status,expiresAt,createdAt,invitedBy,acceptedAt);

@override
String toString() {
  return 'TeamInvitationDto(id: $id, email: $email, role: $role, status: $status, expiresAt: $expiresAt, createdAt: $createdAt, invitedBy: $invitedBy, acceptedAt: $acceptedAt)';
}


}

/// @nodoc
abstract mixin class _$TeamInvitationDtoCopyWith<$Res> implements $TeamInvitationDtoCopyWith<$Res> {
  factory _$TeamInvitationDtoCopyWith(_TeamInvitationDto value, $Res Function(_TeamInvitationDto) _then) = __$TeamInvitationDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String email, String role, String status, String expiresAt, String createdAt, PublicUserDto invitedBy, String? acceptedAt
});


@override $PublicUserDtoCopyWith<$Res> get invitedBy;

}
/// @nodoc
class __$TeamInvitationDtoCopyWithImpl<$Res>
    implements _$TeamInvitationDtoCopyWith<$Res> {
  __$TeamInvitationDtoCopyWithImpl(this._self, this._then);

  final _TeamInvitationDto _self;
  final $Res Function(_TeamInvitationDto) _then;

/// Create a copy of TeamInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? email = null,Object? role = null,Object? status = null,Object? expiresAt = null,Object? createdAt = null,Object? invitedBy = null,Object? acceptedAt = freezed,}) {
  return _then(_TeamInvitationDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,expiresAt: null == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,invitedBy: null == invitedBy ? _self.invitedBy : invitedBy // ignore: cast_nullable_to_non_nullable
as PublicUserDto,acceptedAt: freezed == acceptedAt ? _self.acceptedAt : acceptedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of TeamInvitationDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get invitedBy {
  
  return $PublicUserDtoCopyWith<$Res>(_self.invitedBy, (value) {
    return _then(_self.copyWith(invitedBy: value));
  });
}
}

// dart format on
