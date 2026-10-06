// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'member_count_by_role_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MemberCountByRoleDto {

/// Members with the ADMIN role
 int get admins;/// Members with the ORGANIZER role
 int get organizers;/// Members with the MEMBER role
 int get members;
/// Create a copy of MemberCountByRoleDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MemberCountByRoleDtoCopyWith<MemberCountByRoleDto> get copyWith => _$MemberCountByRoleDtoCopyWithImpl<MemberCountByRoleDto>(this as MemberCountByRoleDto, _$identity);

  /// Serializes this MemberCountByRoleDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as MemberCountByRoleDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MemberCountByRoleDto&&(identical(other.admins, _this.admins) || other.admins == _this.admins)&&(identical(other.organizers, _this.organizers) || other.organizers == _this.organizers)&&(identical(other.members, _this.members) || other.members == _this.members));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as MemberCountByRoleDto;
  return Object.hash(runtimeType,_this.admins,_this.organizers,_this.members);
}

@override
String toString() {
  final _this = this as MemberCountByRoleDto;
  return 'MemberCountByRoleDto(admins: ${_this.admins}, organizers: ${_this.organizers}, members: ${_this.members})';
}


}

/// @nodoc
abstract mixin class $MemberCountByRoleDtoCopyWith<$Res>  {
  factory $MemberCountByRoleDtoCopyWith(MemberCountByRoleDto value, $Res Function(MemberCountByRoleDto) _then) = _$MemberCountByRoleDtoCopyWithImpl;
@useResult
$Res call({
 int admins, int organizers, int members
});




}
/// @nodoc
class _$MemberCountByRoleDtoCopyWithImpl<$Res>
    implements $MemberCountByRoleDtoCopyWith<$Res> {
  _$MemberCountByRoleDtoCopyWithImpl(this._self, this._then);

  final MemberCountByRoleDto _self;
  final $Res Function(MemberCountByRoleDto) _then;

/// Create a copy of MemberCountByRoleDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? admins = null,Object? organizers = null,Object? members = null,}) {
  return _then(MemberCountByRoleDto(
admins: null == admins ? _self.admins : admins // ignore: cast_nullable_to_non_nullable
as int,organizers: null == organizers ? _self.organizers : organizers // ignore: cast_nullable_to_non_nullable
as int,members: null == members ? _self.members : members // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [MemberCountByRoleDto].
extension MemberCountByRoleDtoPatterns on MemberCountByRoleDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MemberCountByRoleDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MemberCountByRoleDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MemberCountByRoleDto value)  $default,){
final _that = this;
switch (_that) {
case _MemberCountByRoleDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MemberCountByRoleDto value)?  $default,){
final _that = this;
switch (_that) {
case _MemberCountByRoleDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int admins,  int organizers,  int members)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MemberCountByRoleDto() when $default != null:
return $default(_that.admins,_that.organizers,_that.members);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int admins,  int organizers,  int members)  $default,) {final _that = this;
switch (_that) {
case _MemberCountByRoleDto():
return $default(_that.admins,_that.organizers,_that.members);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int admins,  int organizers,  int members)?  $default,) {final _that = this;
switch (_that) {
case _MemberCountByRoleDto() when $default != null:
return $default(_that.admins,_that.organizers,_that.members);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MemberCountByRoleDto implements MemberCountByRoleDto {
  const _MemberCountByRoleDto({required this.admins, required this.organizers, required this.members});
  factory _MemberCountByRoleDto.fromJson(Map<String, dynamic> json) => _$MemberCountByRoleDtoFromJson(json);

/// Members with the ADMIN role
@override final  int admins;
/// Members with the ORGANIZER role
@override final  int organizers;
/// Members with the MEMBER role
@override final  int members;

/// Create a copy of MemberCountByRoleDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MemberCountByRoleDtoCopyWith<_MemberCountByRoleDto> get copyWith => __$MemberCountByRoleDtoCopyWithImpl<_MemberCountByRoleDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MemberCountByRoleDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _MemberCountByRoleDto&&(identical(other.admins, admins) || other.admins == admins)&&(identical(other.organizers, organizers) || other.organizers == organizers)&&(identical(other.members, members) || other.members == members));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,admins,organizers,members);
}

@override
String toString() {
    return 'MemberCountByRoleDto(admins: $admins, organizers: $organizers, members: $members)';
}


}

/// @nodoc
abstract mixin class _$MemberCountByRoleDtoCopyWith<$Res> implements $MemberCountByRoleDtoCopyWith<$Res> {
  factory _$MemberCountByRoleDtoCopyWith(_MemberCountByRoleDto value, $Res Function(_MemberCountByRoleDto) _then) = __$MemberCountByRoleDtoCopyWithImpl;
@override @useResult
$Res call({
 int admins, int organizers, int members
});




}
/// @nodoc
class __$MemberCountByRoleDtoCopyWithImpl<$Res>
    implements _$MemberCountByRoleDtoCopyWith<$Res> {
  __$MemberCountByRoleDtoCopyWithImpl(this._self, this._then);

  final _MemberCountByRoleDto _self;
  final $Res Function(_MemberCountByRoleDto) _then;

/// Create a copy of MemberCountByRoleDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? admins = null,Object? organizers = null,Object? members = null,}) {
  return _then(_MemberCountByRoleDto(
admins: null == admins ? _self.admins : admins // ignore: cast_nullable_to_non_nullable
as int,organizers: null == organizers ? _self.organizers : organizers // ignore: cast_nullable_to_non_nullable
as int,members: null == members ? _self.members : members // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
