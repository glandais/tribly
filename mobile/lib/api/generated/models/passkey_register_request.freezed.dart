// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'passkey_register_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PasskeyRegisterRequest {

/// Optional device name for this passkey
 String? get deviceName;
/// Create a copy of PasskeyRegisterRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PasskeyRegisterRequestCopyWith<PasskeyRegisterRequest> get copyWith => _$PasskeyRegisterRequestCopyWithImpl<PasskeyRegisterRequest>(this as PasskeyRegisterRequest, _$identity);

  /// Serializes this PasskeyRegisterRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PasskeyRegisterRequest&&(identical(other.deviceName, deviceName) || other.deviceName == deviceName));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,deviceName);

@override
String toString() {
  return 'PasskeyRegisterRequest(deviceName: $deviceName)';
}


}

/// @nodoc
abstract mixin class $PasskeyRegisterRequestCopyWith<$Res>  {
  factory $PasskeyRegisterRequestCopyWith(PasskeyRegisterRequest value, $Res Function(PasskeyRegisterRequest) _then) = _$PasskeyRegisterRequestCopyWithImpl;
@useResult
$Res call({
 String? deviceName
});




}
/// @nodoc
class _$PasskeyRegisterRequestCopyWithImpl<$Res>
    implements $PasskeyRegisterRequestCopyWith<$Res> {
  _$PasskeyRegisterRequestCopyWithImpl(this._self, this._then);

  final PasskeyRegisterRequest _self;
  final $Res Function(PasskeyRegisterRequest) _then;

/// Create a copy of PasskeyRegisterRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? deviceName = freezed,}) {
  return _then(_self.copyWith(
deviceName: freezed == deviceName ? _self.deviceName : deviceName // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [PasskeyRegisterRequest].
extension PasskeyRegisterRequestPatterns on PasskeyRegisterRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PasskeyRegisterRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PasskeyRegisterRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PasskeyRegisterRequest value)  $default,){
final _that = this;
switch (_that) {
case _PasskeyRegisterRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PasskeyRegisterRequest value)?  $default,){
final _that = this;
switch (_that) {
case _PasskeyRegisterRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String? deviceName)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PasskeyRegisterRequest() when $default != null:
return $default(_that.deviceName);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String? deviceName)  $default,) {final _that = this;
switch (_that) {
case _PasskeyRegisterRequest():
return $default(_that.deviceName);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String? deviceName)?  $default,) {final _that = this;
switch (_that) {
case _PasskeyRegisterRequest() when $default != null:
return $default(_that.deviceName);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PasskeyRegisterRequest implements PasskeyRegisterRequest {
  const _PasskeyRegisterRequest({this.deviceName});
  factory _PasskeyRegisterRequest.fromJson(Map<String, dynamic> json) => _$PasskeyRegisterRequestFromJson(json);

/// Optional device name for this passkey
@override final  String? deviceName;

/// Create a copy of PasskeyRegisterRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PasskeyRegisterRequestCopyWith<_PasskeyRegisterRequest> get copyWith => __$PasskeyRegisterRequestCopyWithImpl<_PasskeyRegisterRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PasskeyRegisterRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PasskeyRegisterRequest&&(identical(other.deviceName, deviceName) || other.deviceName == deviceName));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,deviceName);

@override
String toString() {
  return 'PasskeyRegisterRequest(deviceName: $deviceName)';
}


}

/// @nodoc
abstract mixin class _$PasskeyRegisterRequestCopyWith<$Res> implements $PasskeyRegisterRequestCopyWith<$Res> {
  factory _$PasskeyRegisterRequestCopyWith(_PasskeyRegisterRequest value, $Res Function(_PasskeyRegisterRequest) _then) = __$PasskeyRegisterRequestCopyWithImpl;
@override @useResult
$Res call({
 String? deviceName
});




}
/// @nodoc
class __$PasskeyRegisterRequestCopyWithImpl<$Res>
    implements _$PasskeyRegisterRequestCopyWith<$Res> {
  __$PasskeyRegisterRequestCopyWithImpl(this._self, this._then);

  final _PasskeyRegisterRequest _self;
  final $Res Function(_PasskeyRegisterRequest) _then;

/// Create a copy of PasskeyRegisterRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? deviceName = freezed,}) {
  return _then(_PasskeyRegisterRequest(
deviceName: freezed == deviceName ? _self.deviceName : deviceName // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
