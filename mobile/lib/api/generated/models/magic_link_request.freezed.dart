// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'magic_link_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MagicLinkRequest {

/// Email address
 String get email;
/// Create a copy of MagicLinkRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MagicLinkRequestCopyWith<MagicLinkRequest> get copyWith => _$MagicLinkRequestCopyWithImpl<MagicLinkRequest>(this as MagicLinkRequest, _$identity);

  /// Serializes this MagicLinkRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MagicLinkRequest&&(identical(other.email, email) || other.email == email));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,email);

@override
String toString() {
  return 'MagicLinkRequest(email: $email)';
}


}

/// @nodoc
abstract mixin class $MagicLinkRequestCopyWith<$Res>  {
  factory $MagicLinkRequestCopyWith(MagicLinkRequest value, $Res Function(MagicLinkRequest) _then) = _$MagicLinkRequestCopyWithImpl;
@useResult
$Res call({
 String email
});




}
/// @nodoc
class _$MagicLinkRequestCopyWithImpl<$Res>
    implements $MagicLinkRequestCopyWith<$Res> {
  _$MagicLinkRequestCopyWithImpl(this._self, this._then);

  final MagicLinkRequest _self;
  final $Res Function(MagicLinkRequest) _then;

/// Create a copy of MagicLinkRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? email = null,}) {
  return _then(_self.copyWith(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [MagicLinkRequest].
extension MagicLinkRequestPatterns on MagicLinkRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MagicLinkRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MagicLinkRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MagicLinkRequest value)  $default,){
final _that = this;
switch (_that) {
case _MagicLinkRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MagicLinkRequest value)?  $default,){
final _that = this;
switch (_that) {
case _MagicLinkRequest() when $default != null:
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
case _MagicLinkRequest() when $default != null:
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
case _MagicLinkRequest():
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
case _MagicLinkRequest() when $default != null:
return $default(_that.email);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MagicLinkRequest implements MagicLinkRequest {
  const _MagicLinkRequest({required this.email});
  factory _MagicLinkRequest.fromJson(Map<String, dynamic> json) => _$MagicLinkRequestFromJson(json);

/// Email address
@override final  String email;

/// Create a copy of MagicLinkRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MagicLinkRequestCopyWith<_MagicLinkRequest> get copyWith => __$MagicLinkRequestCopyWithImpl<_MagicLinkRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MagicLinkRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MagicLinkRequest&&(identical(other.email, email) || other.email == email));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,email);

@override
String toString() {
  return 'MagicLinkRequest(email: $email)';
}


}

/// @nodoc
abstract mixin class _$MagicLinkRequestCopyWith<$Res> implements $MagicLinkRequestCopyWith<$Res> {
  factory _$MagicLinkRequestCopyWith(_MagicLinkRequest value, $Res Function(_MagicLinkRequest) _then) = __$MagicLinkRequestCopyWithImpl;
@override @useResult
$Res call({
 String email
});




}
/// @nodoc
class __$MagicLinkRequestCopyWithImpl<$Res>
    implements _$MagicLinkRequestCopyWith<$Res> {
  __$MagicLinkRequestCopyWithImpl(this._self, this._then);

  final _MagicLinkRequest _self;
  final $Res Function(_MagicLinkRequest) _then;

/// Create a copy of MagicLinkRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? email = null,}) {
  return _then(_MagicLinkRequest(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
