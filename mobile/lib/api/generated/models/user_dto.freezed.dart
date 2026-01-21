// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'user_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$UserDto {

/// User ID (TSID)
 String get id;/// User email address
 String get email;/// User display name
 String get displayName;/// User avatar URL
 String? get avatarUrl;/// Account creation timestamp
 String? get createdAt;/// Preferred unit system (metric or imperial)
 String? get unitSystem;/// Platform role (null if regular user)
 String? get platformRole;/// Connected GPS services
 List<GpsServiceConnectionDto>? get connectedServices;
/// Create a copy of UserDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$UserDtoCopyWith<UserDto> get copyWith => _$UserDtoCopyWithImpl<UserDto>(this as UserDto, _$identity);

  /// Serializes this UserDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is UserDto&&(identical(other.id, id) || other.id == id)&&(identical(other.email, email) || other.email == email)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.avatarUrl, avatarUrl) || other.avatarUrl == avatarUrl)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.unitSystem, unitSystem) || other.unitSystem == unitSystem)&&(identical(other.platformRole, platformRole) || other.platformRole == platformRole)&&const DeepCollectionEquality().equals(other.connectedServices, connectedServices));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,email,displayName,avatarUrl,createdAt,unitSystem,platformRole,const DeepCollectionEquality().hash(connectedServices));

@override
String toString() {
  return 'UserDto(id: $id, email: $email, displayName: $displayName, avatarUrl: $avatarUrl, createdAt: $createdAt, unitSystem: $unitSystem, platformRole: $platformRole, connectedServices: $connectedServices)';
}


}

/// @nodoc
abstract mixin class $UserDtoCopyWith<$Res>  {
  factory $UserDtoCopyWith(UserDto value, $Res Function(UserDto) _then) = _$UserDtoCopyWithImpl;
@useResult
$Res call({
 String id, String email, String displayName, String? avatarUrl, String? createdAt, String? unitSystem, String? platformRole, List<GpsServiceConnectionDto>? connectedServices
});




}
/// @nodoc
class _$UserDtoCopyWithImpl<$Res>
    implements $UserDtoCopyWith<$Res> {
  _$UserDtoCopyWithImpl(this._self, this._then);

  final UserDto _self;
  final $Res Function(UserDto) _then;

/// Create a copy of UserDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? email = null,Object? displayName = null,Object? avatarUrl = freezed,Object? createdAt = freezed,Object? unitSystem = freezed,Object? platformRole = freezed,Object? connectedServices = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,avatarUrl: freezed == avatarUrl ? _self.avatarUrl : avatarUrl // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,unitSystem: freezed == unitSystem ? _self.unitSystem : unitSystem // ignore: cast_nullable_to_non_nullable
as String?,platformRole: freezed == platformRole ? _self.platformRole : platformRole // ignore: cast_nullable_to_non_nullable
as String?,connectedServices: freezed == connectedServices ? _self.connectedServices : connectedServices // ignore: cast_nullable_to_non_nullable
as List<GpsServiceConnectionDto>?,
  ));
}

}


/// Adds pattern-matching-related methods to [UserDto].
extension UserDtoPatterns on UserDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _UserDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _UserDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _UserDto value)  $default,){
final _that = this;
switch (_that) {
case _UserDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _UserDto value)?  $default,){
final _that = this;
switch (_that) {
case _UserDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String email,  String displayName,  String? avatarUrl,  String? createdAt,  String? unitSystem,  String? platformRole,  List<GpsServiceConnectionDto>? connectedServices)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _UserDto() when $default != null:
return $default(_that.id,_that.email,_that.displayName,_that.avatarUrl,_that.createdAt,_that.unitSystem,_that.platformRole,_that.connectedServices);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String email,  String displayName,  String? avatarUrl,  String? createdAt,  String? unitSystem,  String? platformRole,  List<GpsServiceConnectionDto>? connectedServices)  $default,) {final _that = this;
switch (_that) {
case _UserDto():
return $default(_that.id,_that.email,_that.displayName,_that.avatarUrl,_that.createdAt,_that.unitSystem,_that.platformRole,_that.connectedServices);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String email,  String displayName,  String? avatarUrl,  String? createdAt,  String? unitSystem,  String? platformRole,  List<GpsServiceConnectionDto>? connectedServices)?  $default,) {final _that = this;
switch (_that) {
case _UserDto() when $default != null:
return $default(_that.id,_that.email,_that.displayName,_that.avatarUrl,_that.createdAt,_that.unitSystem,_that.platformRole,_that.connectedServices);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _UserDto implements UserDto {
  const _UserDto({required this.id, required this.email, required this.displayName, this.avatarUrl, this.createdAt, this.unitSystem, this.platformRole, final  List<GpsServiceConnectionDto>? connectedServices}): _connectedServices = connectedServices;
  factory _UserDto.fromJson(Map<String, dynamic> json) => _$UserDtoFromJson(json);

/// User ID (TSID)
@override final  String id;
/// User email address
@override final  String email;
/// User display name
@override final  String displayName;
/// User avatar URL
@override final  String? avatarUrl;
/// Account creation timestamp
@override final  String? createdAt;
/// Preferred unit system (metric or imperial)
@override final  String? unitSystem;
/// Platform role (null if regular user)
@override final  String? platformRole;
/// Connected GPS services
 final  List<GpsServiceConnectionDto>? _connectedServices;
/// Connected GPS services
@override List<GpsServiceConnectionDto>? get connectedServices {
  final value = _connectedServices;
  if (value == null) return null;
  if (_connectedServices is EqualUnmodifiableListView) return _connectedServices;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}


/// Create a copy of UserDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$UserDtoCopyWith<_UserDto> get copyWith => __$UserDtoCopyWithImpl<_UserDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$UserDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _UserDto&&(identical(other.id, id) || other.id == id)&&(identical(other.email, email) || other.email == email)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.avatarUrl, avatarUrl) || other.avatarUrl == avatarUrl)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.unitSystem, unitSystem) || other.unitSystem == unitSystem)&&(identical(other.platformRole, platformRole) || other.platformRole == platformRole)&&const DeepCollectionEquality().equals(other._connectedServices, _connectedServices));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,email,displayName,avatarUrl,createdAt,unitSystem,platformRole,const DeepCollectionEquality().hash(_connectedServices));

@override
String toString() {
  return 'UserDto(id: $id, email: $email, displayName: $displayName, avatarUrl: $avatarUrl, createdAt: $createdAt, unitSystem: $unitSystem, platformRole: $platformRole, connectedServices: $connectedServices)';
}


}

/// @nodoc
abstract mixin class _$UserDtoCopyWith<$Res> implements $UserDtoCopyWith<$Res> {
  factory _$UserDtoCopyWith(_UserDto value, $Res Function(_UserDto) _then) = __$UserDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String email, String displayName, String? avatarUrl, String? createdAt, String? unitSystem, String? platformRole, List<GpsServiceConnectionDto>? connectedServices
});




}
/// @nodoc
class __$UserDtoCopyWithImpl<$Res>
    implements _$UserDtoCopyWith<$Res> {
  __$UserDtoCopyWithImpl(this._self, this._then);

  final _UserDto _self;
  final $Res Function(_UserDto) _then;

/// Create a copy of UserDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? email = null,Object? displayName = null,Object? avatarUrl = freezed,Object? createdAt = freezed,Object? unitSystem = freezed,Object? platformRole = freezed,Object? connectedServices = freezed,}) {
  return _then(_UserDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,avatarUrl: freezed == avatarUrl ? _self.avatarUrl : avatarUrl // ignore: cast_nullable_to_non_nullable
as String?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,unitSystem: freezed == unitSystem ? _self.unitSystem : unitSystem // ignore: cast_nullable_to_non_nullable
as String?,platformRole: freezed == platformRole ? _self.platformRole : platformRole // ignore: cast_nullable_to_non_nullable
as String?,connectedServices: freezed == connectedServices ? _self._connectedServices : connectedServices // ignore: cast_nullable_to_non_nullable
as List<GpsServiceConnectionDto>?,
  ));
}


}

// dart format on
