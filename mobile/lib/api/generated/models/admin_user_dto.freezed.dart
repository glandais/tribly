// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_user_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminUserDto {

/// User ID (TSID)
 String get id;/// User email address
 String get email;/// User display name
 String get displayName;/// Domain ID this user belongs to
 String get domainId;/// Domain hostname
 String get domainName;/// Whether email is verified
 bool get emailVerified;/// User creation timestamp
 String get createdAt;/// Platform role (null if regular user)
 String? get platformRole;/// Last login timestamp
 String? get lastLoginAt;
/// Create a copy of AdminUserDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminUserDtoCopyWith<AdminUserDto> get copyWith => _$AdminUserDtoCopyWithImpl<AdminUserDto>(this as AdminUserDto, _$identity);

  /// Serializes this AdminUserDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminUserDto&&(identical(other.id, id) || other.id == id)&&(identical(other.email, email) || other.email == email)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.domainId, domainId) || other.domainId == domainId)&&(identical(other.domainName, domainName) || other.domainName == domainName)&&(identical(other.emailVerified, emailVerified) || other.emailVerified == emailVerified)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.platformRole, platformRole) || other.platformRole == platformRole)&&(identical(other.lastLoginAt, lastLoginAt) || other.lastLoginAt == lastLoginAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,email,displayName,domainId,domainName,emailVerified,createdAt,platformRole,lastLoginAt);

@override
String toString() {
  return 'AdminUserDto(id: $id, email: $email, displayName: $displayName, domainId: $domainId, domainName: $domainName, emailVerified: $emailVerified, createdAt: $createdAt, platformRole: $platformRole, lastLoginAt: $lastLoginAt)';
}


}

/// @nodoc
abstract mixin class $AdminUserDtoCopyWith<$Res>  {
  factory $AdminUserDtoCopyWith(AdminUserDto value, $Res Function(AdminUserDto) _then) = _$AdminUserDtoCopyWithImpl;
@useResult
$Res call({
 String id, String email, String displayName, String domainId, String domainName, bool emailVerified, String createdAt, String? platformRole, String? lastLoginAt
});




}
/// @nodoc
class _$AdminUserDtoCopyWithImpl<$Res>
    implements $AdminUserDtoCopyWith<$Res> {
  _$AdminUserDtoCopyWithImpl(this._self, this._then);

  final AdminUserDto _self;
  final $Res Function(AdminUserDto) _then;

/// Create a copy of AdminUserDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? email = null,Object? displayName = null,Object? domainId = null,Object? domainName = null,Object? emailVerified = null,Object? createdAt = null,Object? platformRole = freezed,Object? lastLoginAt = freezed,}) {
  return _then(AdminUserDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,domainId: null == domainId ? _self.domainId : domainId // ignore: cast_nullable_to_non_nullable
as String,domainName: null == domainName ? _self.domainName : domainName // ignore: cast_nullable_to_non_nullable
as String,emailVerified: null == emailVerified ? _self.emailVerified : emailVerified // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,platformRole: freezed == platformRole ? _self.platformRole : platformRole // ignore: cast_nullable_to_non_nullable
as String?,lastLoginAt: freezed == lastLoginAt ? _self.lastLoginAt : lastLoginAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminUserDto].
extension AdminUserDtoPatterns on AdminUserDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminUserDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminUserDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminUserDto value)  $default,){
final _that = this;
switch (_that) {
case _AdminUserDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminUserDto value)?  $default,){
final _that = this;
switch (_that) {
case _AdminUserDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String email,  String displayName,  String domainId,  String domainName,  bool emailVerified,  String createdAt,  String? platformRole,  String? lastLoginAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminUserDto() when $default != null:
return $default(_that.id,_that.email,_that.displayName,_that.domainId,_that.domainName,_that.emailVerified,_that.createdAt,_that.platformRole,_that.lastLoginAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String email,  String displayName,  String domainId,  String domainName,  bool emailVerified,  String createdAt,  String? platformRole,  String? lastLoginAt)  $default,) {final _that = this;
switch (_that) {
case _AdminUserDto():
return $default(_that.id,_that.email,_that.displayName,_that.domainId,_that.domainName,_that.emailVerified,_that.createdAt,_that.platformRole,_that.lastLoginAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String email,  String displayName,  String domainId,  String domainName,  bool emailVerified,  String createdAt,  String? platformRole,  String? lastLoginAt)?  $default,) {final _that = this;
switch (_that) {
case _AdminUserDto() when $default != null:
return $default(_that.id,_that.email,_that.displayName,_that.domainId,_that.domainName,_that.emailVerified,_that.createdAt,_that.platformRole,_that.lastLoginAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminUserDto implements AdminUserDto {
  const _AdminUserDto({required this.id, required this.email, required this.displayName, required this.domainId, required this.domainName, required this.emailVerified, required this.createdAt, this.platformRole, this.lastLoginAt});
  factory _AdminUserDto.fromJson(Map<String, dynamic> json) => _$AdminUserDtoFromJson(json);

/// User ID (TSID)
@override final  String id;
/// User email address
@override final  String email;
/// User display name
@override final  String displayName;
/// Domain ID this user belongs to
@override final  String domainId;
/// Domain hostname
@override final  String domainName;
/// Whether email is verified
@override final  bool emailVerified;
/// User creation timestamp
@override final  String createdAt;
/// Platform role (null if regular user)
@override final  String? platformRole;
/// Last login timestamp
@override final  String? lastLoginAt;

/// Create a copy of AdminUserDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminUserDtoCopyWith<_AdminUserDto> get copyWith => __$AdminUserDtoCopyWithImpl<_AdminUserDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminUserDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminUserDto&&(identical(other.id, id) || other.id == id)&&(identical(other.email, email) || other.email == email)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.domainId, domainId) || other.domainId == domainId)&&(identical(other.domainName, domainName) || other.domainName == domainName)&&(identical(other.emailVerified, emailVerified) || other.emailVerified == emailVerified)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.platformRole, platformRole) || other.platformRole == platformRole)&&(identical(other.lastLoginAt, lastLoginAt) || other.lastLoginAt == lastLoginAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,email,displayName,domainId,domainName,emailVerified,createdAt,platformRole,lastLoginAt);

@override
String toString() {
  return 'AdminUserDto(id: $id, email: $email, displayName: $displayName, domainId: $domainId, domainName: $domainName, emailVerified: $emailVerified, createdAt: $createdAt, platformRole: $platformRole, lastLoginAt: $lastLoginAt)';
}


}

/// @nodoc
abstract mixin class _$AdminUserDtoCopyWith<$Res> implements $AdminUserDtoCopyWith<$Res> {
  factory _$AdminUserDtoCopyWith(_AdminUserDto value, $Res Function(_AdminUserDto) _then) = __$AdminUserDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String email, String displayName, String domainId, String domainName, bool emailVerified, String createdAt, String? platformRole, String? lastLoginAt
});




}
/// @nodoc
class __$AdminUserDtoCopyWithImpl<$Res>
    implements _$AdminUserDtoCopyWith<$Res> {
  __$AdminUserDtoCopyWithImpl(this._self, this._then);

  final _AdminUserDto _self;
  final $Res Function(_AdminUserDto) _then;

/// Create a copy of AdminUserDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? email = null,Object? displayName = null,Object? domainId = null,Object? domainName = null,Object? emailVerified = null,Object? createdAt = null,Object? platformRole = freezed,Object? lastLoginAt = freezed,}) {
  return _then(_AdminUserDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,domainId: null == domainId ? _self.domainId : domainId // ignore: cast_nullable_to_non_nullable
as String,domainName: null == domainName ? _self.domainName : domainName // ignore: cast_nullable_to_non_nullable
as String,emailVerified: null == emailVerified ? _self.emailVerified : emailVerified // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,platformRole: freezed == platformRole ? _self.platformRole : platformRole // ignore: cast_nullable_to_non_nullable
as String?,lastLoginAt: freezed == lastLoginAt ? _self.lastLoginAt : lastLoginAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
