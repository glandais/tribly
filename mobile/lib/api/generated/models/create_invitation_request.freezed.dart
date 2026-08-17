// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'create_invitation_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CreateInvitationRequest {

/// Address to invite. The response is the same whether or not an account already exists for it — only the wording of the e-mail differs. Answering differently would turn this into an account-existence oracle for anyone who administers a team.
 String get email;/// Role to grant on acceptance. Defaults to MEMBER.
 String? get role;
/// Create a copy of CreateInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CreateInvitationRequestCopyWith<CreateInvitationRequest> get copyWith => _$CreateInvitationRequestCopyWithImpl<CreateInvitationRequest>(this as CreateInvitationRequest, _$identity);

  /// Serializes this CreateInvitationRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CreateInvitationRequest&&(identical(other.email, email) || other.email == email)&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,email,role);

@override
String toString() {
  return 'CreateInvitationRequest(email: $email, role: $role)';
}


}

/// @nodoc
abstract mixin class $CreateInvitationRequestCopyWith<$Res>  {
  factory $CreateInvitationRequestCopyWith(CreateInvitationRequest value, $Res Function(CreateInvitationRequest) _then) = _$CreateInvitationRequestCopyWithImpl;
@useResult
$Res call({
 String email, String? role
});




}
/// @nodoc
class _$CreateInvitationRequestCopyWithImpl<$Res>
    implements $CreateInvitationRequestCopyWith<$Res> {
  _$CreateInvitationRequestCopyWithImpl(this._self, this._then);

  final CreateInvitationRequest _self;
  final $Res Function(CreateInvitationRequest) _then;

/// Create a copy of CreateInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? email = null,Object? role = freezed,}) {
  return _then(CreateInvitationRequest(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [CreateInvitationRequest].
extension CreateInvitationRequestPatterns on CreateInvitationRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CreateInvitationRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CreateInvitationRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CreateInvitationRequest value)  $default,){
final _that = this;
switch (_that) {
case _CreateInvitationRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CreateInvitationRequest value)?  $default,){
final _that = this;
switch (_that) {
case _CreateInvitationRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String email,  String? role)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CreateInvitationRequest() when $default != null:
return $default(_that.email,_that.role);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String email,  String? role)  $default,) {final _that = this;
switch (_that) {
case _CreateInvitationRequest():
return $default(_that.email,_that.role);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String email,  String? role)?  $default,) {final _that = this;
switch (_that) {
case _CreateInvitationRequest() when $default != null:
return $default(_that.email,_that.role);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CreateInvitationRequest implements CreateInvitationRequest {
  const _CreateInvitationRequest({required this.email, this.role});
  factory _CreateInvitationRequest.fromJson(Map<String, dynamic> json) => _$CreateInvitationRequestFromJson(json);

/// Address to invite. The response is the same whether or not an account already exists for it — only the wording of the e-mail differs. Answering differently would turn this into an account-existence oracle for anyone who administers a team.
@override final  String email;
/// Role to grant on acceptance. Defaults to MEMBER.
@override final  String? role;

/// Create a copy of CreateInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CreateInvitationRequestCopyWith<_CreateInvitationRequest> get copyWith => __$CreateInvitationRequestCopyWithImpl<_CreateInvitationRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CreateInvitationRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _CreateInvitationRequest&&(identical(other.email, email) || other.email == email)&&(identical(other.role, role) || other.role == role));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,email,role);

@override
String toString() {
  return 'CreateInvitationRequest(email: $email, role: $role)';
}


}

/// @nodoc
abstract mixin class _$CreateInvitationRequestCopyWith<$Res> implements $CreateInvitationRequestCopyWith<$Res> {
  factory _$CreateInvitationRequestCopyWith(_CreateInvitationRequest value, $Res Function(_CreateInvitationRequest) _then) = __$CreateInvitationRequestCopyWithImpl;
@override @useResult
$Res call({
 String email, String? role
});




}
/// @nodoc
class __$CreateInvitationRequestCopyWithImpl<$Res>
    implements _$CreateInvitationRequestCopyWith<$Res> {
  __$CreateInvitationRequestCopyWithImpl(this._self, this._then);

  final _CreateInvitationRequest _self;
  final $Res Function(_CreateInvitationRequest) _then;

/// Create a copy of CreateInvitationRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? email = null,Object? role = freezed,}) {
  return _then(_CreateInvitationRequest(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,role: freezed == role ? _self.role : role // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
