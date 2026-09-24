// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'blocked_users_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BlockedUsersResponse {

/// Blocked members
 List<PublicUserDto> get users;
/// Create a copy of BlockedUsersResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BlockedUsersResponseCopyWith<BlockedUsersResponse> get copyWith => _$BlockedUsersResponseCopyWithImpl<BlockedUsersResponse>(this as BlockedUsersResponse, _$identity);

  /// Serializes this BlockedUsersResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as BlockedUsersResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BlockedUsersResponse&&const DeepCollectionEquality().equals(other.users, _this.users));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as BlockedUsersResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.users));
}

@override
String toString() {
  final _this = this as BlockedUsersResponse;
  return 'BlockedUsersResponse(users: ${_this.users})';
}


}

/// @nodoc
abstract mixin class $BlockedUsersResponseCopyWith<$Res>  {
  factory $BlockedUsersResponseCopyWith(BlockedUsersResponse value, $Res Function(BlockedUsersResponse) _then) = _$BlockedUsersResponseCopyWithImpl;
@useResult
$Res call({
 List<PublicUserDto> users
});




}
/// @nodoc
class _$BlockedUsersResponseCopyWithImpl<$Res>
    implements $BlockedUsersResponseCopyWith<$Res> {
  _$BlockedUsersResponseCopyWithImpl(this._self, this._then);

  final BlockedUsersResponse _self;
  final $Res Function(BlockedUsersResponse) _then;

/// Create a copy of BlockedUsersResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? users = null,}) {
  return _then(BlockedUsersResponse(
users: null == users ? _self.users : users // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [BlockedUsersResponse].
extension BlockedUsersResponsePatterns on BlockedUsersResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BlockedUsersResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BlockedUsersResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BlockedUsersResponse value)  $default,){
final _that = this;
switch (_that) {
case _BlockedUsersResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BlockedUsersResponse value)?  $default,){
final _that = this;
switch (_that) {
case _BlockedUsersResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<PublicUserDto> users)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BlockedUsersResponse() when $default != null:
return $default(_that.users);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<PublicUserDto> users)  $default,) {final _that = this;
switch (_that) {
case _BlockedUsersResponse():
return $default(_that.users);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<PublicUserDto> users)?  $default,) {final _that = this;
switch (_that) {
case _BlockedUsersResponse() when $default != null:
return $default(_that.users);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BlockedUsersResponse implements BlockedUsersResponse {
  const _BlockedUsersResponse({required  List<PublicUserDto> users}): _users = users;
  factory _BlockedUsersResponse.fromJson(Map<String, dynamic> json) => _$BlockedUsersResponseFromJson(json);

/// Blocked members
 final  List<PublicUserDto> _users;
/// Blocked members
@override List<PublicUserDto> get users {
  if (_users is EqualUnmodifiableListView) return _users;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_users);
}


/// Create a copy of BlockedUsersResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BlockedUsersResponseCopyWith<_BlockedUsersResponse> get copyWith => __$BlockedUsersResponseCopyWithImpl<_BlockedUsersResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BlockedUsersResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _BlockedUsersResponse&&const DeepCollectionEquality().equals(other.users, _users));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_users));
}

@override
String toString() {
    return 'BlockedUsersResponse(users: $users)';
}


}

/// @nodoc
abstract mixin class _$BlockedUsersResponseCopyWith<$Res> implements $BlockedUsersResponseCopyWith<$Res> {
  factory _$BlockedUsersResponseCopyWith(_BlockedUsersResponse value, $Res Function(_BlockedUsersResponse) _then) = __$BlockedUsersResponseCopyWithImpl;
@override @useResult
$Res call({
 List<PublicUserDto> users
});




}
/// @nodoc
class __$BlockedUsersResponseCopyWithImpl<$Res>
    implements _$BlockedUsersResponseCopyWith<$Res> {
  __$BlockedUsersResponseCopyWithImpl(this._self, this._then);

  final _BlockedUsersResponse _self;
  final $Res Function(_BlockedUsersResponse) _then;

/// Create a copy of BlockedUsersResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? users = null,}) {
  return _then(_BlockedUsersResponse(
users: null == users ? _self._users : users // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,
  ));
}


}

// dart format on
