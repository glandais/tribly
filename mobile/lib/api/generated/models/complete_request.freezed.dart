// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'complete_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CompleteRequest {

/// User code from device display
 String get userCode;
/// Create a copy of CompleteRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CompleteRequestCopyWith<CompleteRequest> get copyWith => _$CompleteRequestCopyWithImpl<CompleteRequest>(this as CompleteRequest, _$identity);

  /// Serializes this CompleteRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CompleteRequest&&(identical(other.userCode, userCode) || other.userCode == userCode));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,userCode);

@override
String toString() {
  return 'CompleteRequest(userCode: $userCode)';
}


}

/// @nodoc
abstract mixin class $CompleteRequestCopyWith<$Res>  {
  factory $CompleteRequestCopyWith(CompleteRequest value, $Res Function(CompleteRequest) _then) = _$CompleteRequestCopyWithImpl;
@useResult
$Res call({
 String userCode
});




}
/// @nodoc
class _$CompleteRequestCopyWithImpl<$Res>
    implements $CompleteRequestCopyWith<$Res> {
  _$CompleteRequestCopyWithImpl(this._self, this._then);

  final CompleteRequest _self;
  final $Res Function(CompleteRequest) _then;

/// Create a copy of CompleteRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? userCode = null,}) {
  return _then(CompleteRequest(
userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [CompleteRequest].
extension CompleteRequestPatterns on CompleteRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CompleteRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CompleteRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CompleteRequest value)  $default,){
final _that = this;
switch (_that) {
case _CompleteRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CompleteRequest value)?  $default,){
final _that = this;
switch (_that) {
case _CompleteRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String userCode)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CompleteRequest() when $default != null:
return $default(_that.userCode);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String userCode)  $default,) {final _that = this;
switch (_that) {
case _CompleteRequest():
return $default(_that.userCode);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String userCode)?  $default,) {final _that = this;
switch (_that) {
case _CompleteRequest() when $default != null:
return $default(_that.userCode);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CompleteRequest implements CompleteRequest {
  const _CompleteRequest({required this.userCode});
  factory _CompleteRequest.fromJson(Map<String, dynamic> json) => _$CompleteRequestFromJson(json);

/// User code from device display
@override final  String userCode;

/// Create a copy of CompleteRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CompleteRequestCopyWith<_CompleteRequest> get copyWith => __$CompleteRequestCopyWithImpl<_CompleteRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CompleteRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _CompleteRequest&&(identical(other.userCode, userCode) || other.userCode == userCode));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,userCode);

@override
String toString() {
  return 'CompleteRequest(userCode: $userCode)';
}


}

/// @nodoc
abstract mixin class _$CompleteRequestCopyWith<$Res> implements $CompleteRequestCopyWith<$Res> {
  factory _$CompleteRequestCopyWith(_CompleteRequest value, $Res Function(_CompleteRequest) _then) = __$CompleteRequestCopyWithImpl;
@override @useResult
$Res call({
 String userCode
});




}
/// @nodoc
class __$CompleteRequestCopyWithImpl<$Res>
    implements _$CompleteRequestCopyWith<$Res> {
  __$CompleteRequestCopyWithImpl(this._self, this._then);

  final _CompleteRequest _self;
  final $Res Function(_CompleteRequest) _then;

/// Create a copy of CompleteRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? userCode = null,}) {
  return _then(_CompleteRequest(
userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
