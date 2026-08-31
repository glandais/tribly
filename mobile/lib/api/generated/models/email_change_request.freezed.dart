// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'email_change_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$EmailChangeRequest {

/// New email address
 String get email;
/// Create a copy of EmailChangeRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$EmailChangeRequestCopyWith<EmailChangeRequest> get copyWith => _$EmailChangeRequestCopyWithImpl<EmailChangeRequest>(this as EmailChangeRequest, _$identity);

  /// Serializes this EmailChangeRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as EmailChangeRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is EmailChangeRequest&&(identical(other.email, _this.email) || other.email == _this.email));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as EmailChangeRequest;
  return Object.hash(runtimeType,_this.email);
}

@override
String toString() {
  final _this = this as EmailChangeRequest;
  return 'EmailChangeRequest(email: ${_this.email})';
}


}

/// @nodoc
abstract mixin class $EmailChangeRequestCopyWith<$Res>  {
  factory $EmailChangeRequestCopyWith(EmailChangeRequest value, $Res Function(EmailChangeRequest) _then) = _$EmailChangeRequestCopyWithImpl;
@useResult
$Res call({
 String email
});




}
/// @nodoc
class _$EmailChangeRequestCopyWithImpl<$Res>
    implements $EmailChangeRequestCopyWith<$Res> {
  _$EmailChangeRequestCopyWithImpl(this._self, this._then);

  final EmailChangeRequest _self;
  final $Res Function(EmailChangeRequest) _then;

/// Create a copy of EmailChangeRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? email = null,}) {
  return _then(EmailChangeRequest(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [EmailChangeRequest].
extension EmailChangeRequestPatterns on EmailChangeRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _EmailChangeRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _EmailChangeRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _EmailChangeRequest value)  $default,){
final _that = this;
switch (_that) {
case _EmailChangeRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _EmailChangeRequest value)?  $default,){
final _that = this;
switch (_that) {
case _EmailChangeRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String email)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _EmailChangeRequest() when $default != null:
return $default(_that.email);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String email)  $default,) {final _that = this;
switch (_that) {
case _EmailChangeRequest():
return $default(_that.email);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String email)?  $default,) {final _that = this;
switch (_that) {
case _EmailChangeRequest() when $default != null:
return $default(_that.email);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _EmailChangeRequest implements EmailChangeRequest {
  const _EmailChangeRequest({required this.email});
  factory _EmailChangeRequest.fromJson(Map<String, dynamic> json) => _$EmailChangeRequestFromJson(json);

/// New email address
@override final  String email;

/// Create a copy of EmailChangeRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$EmailChangeRequestCopyWith<_EmailChangeRequest> get copyWith => __$EmailChangeRequestCopyWithImpl<_EmailChangeRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$EmailChangeRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _EmailChangeRequest&&(identical(other.email, email) || other.email == email));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,email);
}

@override
String toString() {
    return 'EmailChangeRequest(email: $email)';
}


}

/// @nodoc
abstract mixin class _$EmailChangeRequestCopyWith<$Res> implements $EmailChangeRequestCopyWith<$Res> {
  factory _$EmailChangeRequestCopyWith(_EmailChangeRequest value, $Res Function(_EmailChangeRequest) _then) = __$EmailChangeRequestCopyWithImpl;
@override @useResult
$Res call({
 String email
});




}
/// @nodoc
class __$EmailChangeRequestCopyWithImpl<$Res>
    implements _$EmailChangeRequestCopyWith<$Res> {
  __$EmailChangeRequestCopyWithImpl(this._self, this._then);

  final _EmailChangeRequest _self;
  final $Res Function(_EmailChangeRequest) _then;

/// Create a copy of EmailChangeRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? email = null,}) {
  return _then(_EmailChangeRequest(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
