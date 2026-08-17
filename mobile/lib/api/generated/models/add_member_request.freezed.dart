// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'add_member_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AddMemberRequest {

/// User ID (TSID) to add
 String get userId;/// Role to assign (defaults to MEMBER)
 String? get role;
/// Create a copy of AddMemberRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AddMemberRequestCopyWith<AddMemberRequest> get copyWith => _$AddMemberRequestCopyWithImpl<AddMemberRequest>(this as AddMemberRequest, _$identity);

  /// Serializes this AddMemberRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AddMemberRequest&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,userId,role);

@override
String toString() {
  return 'AddMemberRequest(userId: $userId, role: $role)';
}


}

/// @nodoc
abstract mixin class $AddMemberRequestCopyWith<$Res>  {
  factory $AddMemberRequestCopyWith(AddMemberRequest value, $Res Function(AddMemberRequest) _then) = _$AddMemberRequestCopyWithImpl;
@useResult
$Res call({
 String userId, String? role
});




}
/// @nodoc
class _$AddMemberRequestCopyWithImpl<$Res>
    implements $AddMemberRequestCopyWith<$Res> {
  _$AddMemberRequestCopyWithImpl(this._self, this._then);

  final AddMemberRequest _self;
  final $Res Function(AddMemberRequest) _then;

/// Create a copy of AddMemberRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? userId = null,Object? role = freezed,}) {
  return _then(AddMemberRequest(
userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [AddMemberRequest].
extension AddMemberRequestPatterns on AddMemberRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AddMemberRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AddMemberRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AddMemberRequest value)  $default,){
final _that = this;
switch (_that) {
case _AddMemberRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AddMemberRequest value)?  $default,){
final _that = this;
switch (_that) {
case _AddMemberRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String userId,  String? role)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AddMemberRequest() when $default != null:
return $default(_that.userId,_that.role);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String userId,  String? role)  $default,) {final _that = this;
switch (_that) {
case _AddMemberRequest():
return $default(_that.userId,_that.role);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String userId,  String? role)?  $default,) {final _that = this;
switch (_that) {
case _AddMemberRequest() when $default != null:
return $default(_that.userId,_that.role);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AddMemberRequest implements AddMemberRequest {
  const _AddMemberRequest({required this.userId, this.role});
  factory _AddMemberRequest.fromJson(Map<String, dynamic> json) => _$AddMemberRequestFromJson(json);

/// User ID (TSID) to add
@override final  String userId;
/// Role to assign (defaults to MEMBER)
@override final  String? role;

/// Create a copy of AddMemberRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AddMemberRequestCopyWith<_AddMemberRequest> get copyWith => __$AddMemberRequestCopyWithImpl<_AddMemberRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AddMemberRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AddMemberRequest&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,userId,role);

@override
String toString() {
  return 'AddMemberRequest(userId: $userId, role: $role)';
}


}

/// @nodoc
abstract mixin class _$AddMemberRequestCopyWith<$Res> implements $AddMemberRequestCopyWith<$Res> {
  factory _$AddMemberRequestCopyWith(_AddMemberRequest value, $Res Function(_AddMemberRequest) _then) = __$AddMemberRequestCopyWithImpl;
@override @useResult
$Res call({
 String userId, String? role
});




}
/// @nodoc
class __$AddMemberRequestCopyWithImpl<$Res>
    implements _$AddMemberRequestCopyWith<$Res> {
  __$AddMemberRequestCopyWithImpl(this._self, this._then);

  final _AddMemberRequest _self;
  final $Res Function(_AddMemberRequest) _then;

/// Create a copy of AddMemberRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? userId = null,Object? role = freezed,}) {
  return _then(_AddMemberRequest(
userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
