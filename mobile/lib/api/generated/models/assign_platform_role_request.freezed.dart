// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'assign_platform_role_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AssignPlatformRoleRequest {

/// Platform role to assign (null to remove)
 String? get role;
/// Create a copy of AssignPlatformRoleRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AssignPlatformRoleRequestCopyWith<AssignPlatformRoleRequest> get copyWith => _$AssignPlatformRoleRequestCopyWithImpl<AssignPlatformRoleRequest>(this as AssignPlatformRoleRequest, _$identity);

  /// Serializes this AssignPlatformRoleRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AssignPlatformRoleRequest&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,role);

@override
String toString() {
  return 'AssignPlatformRoleRequest(role: $role)';
}


}

/// @nodoc
abstract mixin class $AssignPlatformRoleRequestCopyWith<$Res>  {
  factory $AssignPlatformRoleRequestCopyWith(AssignPlatformRoleRequest value, $Res Function(AssignPlatformRoleRequest) _then) = _$AssignPlatformRoleRequestCopyWithImpl;
@useResult
$Res call({
 String? role
});




}
/// @nodoc
class _$AssignPlatformRoleRequestCopyWithImpl<$Res>
    implements $AssignPlatformRoleRequestCopyWith<$Res> {
  _$AssignPlatformRoleRequestCopyWithImpl(this._self, this._then);

  final AssignPlatformRoleRequest _self;
  final $Res Function(AssignPlatformRoleRequest) _then;

/// Create a copy of AssignPlatformRoleRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? role = freezed,}) {
  return _then(_self.copyWith(
role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [AssignPlatformRoleRequest].
extension AssignPlatformRoleRequestPatterns on AssignPlatformRoleRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AssignPlatformRoleRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AssignPlatformRoleRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AssignPlatformRoleRequest value)  $default,){
final _that = this;
switch (_that) {
case _AssignPlatformRoleRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AssignPlatformRoleRequest value)?  $default,){
final _that = this;
switch (_that) {
case _AssignPlatformRoleRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String? role)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AssignPlatformRoleRequest() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String? role)  $default,) {final _that = this;
switch (_that) {
case _AssignPlatformRoleRequest():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String? role)?  $default,) {final _that = this;
switch (_that) {
case _AssignPlatformRoleRequest() when $default != null:
return $default(_that.role);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AssignPlatformRoleRequest implements AssignPlatformRoleRequest {
  const _AssignPlatformRoleRequest({this.role});
  factory _AssignPlatformRoleRequest.fromJson(Map<String, dynamic> json) => _$AssignPlatformRoleRequestFromJson(json);

/// Platform role to assign (null to remove)
@override final  String? role;

/// Create a copy of AssignPlatformRoleRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AssignPlatformRoleRequestCopyWith<_AssignPlatformRoleRequest> get copyWith => __$AssignPlatformRoleRequestCopyWithImpl<_AssignPlatformRoleRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AssignPlatformRoleRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AssignPlatformRoleRequest&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,role);

@override
String toString() {
  return 'AssignPlatformRoleRequest(role: $role)';
}


}

/// @nodoc
abstract mixin class _$AssignPlatformRoleRequestCopyWith<$Res> implements $AssignPlatformRoleRequestCopyWith<$Res> {
  factory _$AssignPlatformRoleRequestCopyWith(_AssignPlatformRoleRequest value, $Res Function(_AssignPlatformRoleRequest) _then) = __$AssignPlatformRoleRequestCopyWithImpl;
@override @useResult
$Res call({
 String? role
});




}
/// @nodoc
class __$AssignPlatformRoleRequestCopyWithImpl<$Res>
    implements _$AssignPlatformRoleRequestCopyWith<$Res> {
  __$AssignPlatformRoleRequestCopyWithImpl(this._self, this._then);

  final _AssignPlatformRoleRequest _self;
  final $Res Function(_AssignPlatformRoleRequest) _then;

/// Create a copy of AssignPlatformRoleRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? role = freezed,}) {
  return _then(_AssignPlatformRoleRequest(
role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
