// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'update_member_role_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$UpdateMemberRoleRequest {

/// New role
 String get role;
/// Create a copy of UpdateMemberRoleRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$UpdateMemberRoleRequestCopyWith<UpdateMemberRoleRequest> get copyWith => _$UpdateMemberRoleRequestCopyWithImpl<UpdateMemberRoleRequest>(this as UpdateMemberRoleRequest, _$identity);

  /// Serializes this UpdateMemberRoleRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as UpdateMemberRoleRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is UpdateMemberRoleRequest&&(identical(other.role, _this.role) || other.role == _this.role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as UpdateMemberRoleRequest;
  return Object.hash(runtimeType,_this.role);
}

@override
String toString() {
  final _this = this as UpdateMemberRoleRequest;
  return 'UpdateMemberRoleRequest(role: ${_this.role})';
}


}

/// @nodoc
abstract mixin class $UpdateMemberRoleRequestCopyWith<$Res>  {
  factory $UpdateMemberRoleRequestCopyWith(UpdateMemberRoleRequest value, $Res Function(UpdateMemberRoleRequest) _then) = _$UpdateMemberRoleRequestCopyWithImpl;
@useResult
$Res call({
 String role
});




}
/// @nodoc
class _$UpdateMemberRoleRequestCopyWithImpl<$Res>
    implements $UpdateMemberRoleRequestCopyWith<$Res> {
  _$UpdateMemberRoleRequestCopyWithImpl(this._self, this._then);

  final UpdateMemberRoleRequest _self;
  final $Res Function(UpdateMemberRoleRequest) _then;

/// Create a copy of UpdateMemberRoleRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? role = null,}) {
  return _then(UpdateMemberRoleRequest(
role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [UpdateMemberRoleRequest].
extension UpdateMemberRoleRequestPatterns on UpdateMemberRoleRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _UpdateMemberRoleRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _UpdateMemberRoleRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _UpdateMemberRoleRequest value)  $default,){
final _that = this;
switch (_that) {
case _UpdateMemberRoleRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _UpdateMemberRoleRequest value)?  $default,){
final _that = this;
switch (_that) {
case _UpdateMemberRoleRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String role)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _UpdateMemberRoleRequest() when $default != null:
return $default(_that.role);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String role)  $default,) {final _that = this;
switch (_that) {
case _UpdateMemberRoleRequest():
return $default(_that.role);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String role)?  $default,) {final _that = this;
switch (_that) {
case _UpdateMemberRoleRequest() when $default != null:
return $default(_that.role);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _UpdateMemberRoleRequest implements UpdateMemberRoleRequest {
  const _UpdateMemberRoleRequest({required this.role});
  factory _UpdateMemberRoleRequest.fromJson(Map<String, dynamic> json) => _$UpdateMemberRoleRequestFromJson(json);

/// New role
@override final  String role;

/// Create a copy of UpdateMemberRoleRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$UpdateMemberRoleRequestCopyWith<_UpdateMemberRoleRequest> get copyWith => __$UpdateMemberRoleRequestCopyWithImpl<_UpdateMemberRoleRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$UpdateMemberRoleRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _UpdateMemberRoleRequest&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,role);
}

@override
String toString() {
    return 'UpdateMemberRoleRequest(role: $role)';
}


}

/// @nodoc
abstract mixin class _$UpdateMemberRoleRequestCopyWith<$Res> implements $UpdateMemberRoleRequestCopyWith<$Res> {
  factory _$UpdateMemberRoleRequestCopyWith(_UpdateMemberRoleRequest value, $Res Function(_UpdateMemberRoleRequest) _then) = __$UpdateMemberRoleRequestCopyWithImpl;
@override @useResult
$Res call({
 String role
});




}
/// @nodoc
class __$UpdateMemberRoleRequestCopyWithImpl<$Res>
    implements _$UpdateMemberRoleRequestCopyWith<$Res> {
  __$UpdateMemberRoleRequestCopyWithImpl(this._self, this._then);

  final _UpdateMemberRoleRequest _self;
  final $Res Function(_UpdateMemberRoleRequest) _then;

/// Create a copy of UpdateMemberRoleRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? role = null,}) {
  return _then(_UpdateMemberRoleRequest(
role: null == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
