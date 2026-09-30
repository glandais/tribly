// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'deny_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DenyRequest {

/// User code from device display
 String get userCode;
/// Create a copy of DenyRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DenyRequestCopyWith<DenyRequest> get copyWith => _$DenyRequestCopyWithImpl<DenyRequest>(this as DenyRequest, _$identity);

  /// Serializes this DenyRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as DenyRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DenyRequest&&(identical(other.userCode, _this.userCode) || other.userCode == _this.userCode));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as DenyRequest;
  return Object.hash(runtimeType,_this.userCode);
}

@override
String toString() {
  final _this = this as DenyRequest;
  return 'DenyRequest(userCode: ${_this.userCode})';
}


}

/// @nodoc
abstract mixin class $DenyRequestCopyWith<$Res>  {
  factory $DenyRequestCopyWith(DenyRequest value, $Res Function(DenyRequest) _then) = _$DenyRequestCopyWithImpl;
@useResult
$Res call({
 String userCode
});




}
/// @nodoc
class _$DenyRequestCopyWithImpl<$Res>
    implements $DenyRequestCopyWith<$Res> {
  _$DenyRequestCopyWithImpl(this._self, this._then);

  final DenyRequest _self;
  final $Res Function(DenyRequest) _then;

/// Create a copy of DenyRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? userCode = null,}) {
  return _then(DenyRequest(
userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [DenyRequest].
extension DenyRequestPatterns on DenyRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DenyRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DenyRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DenyRequest value)  $default,){
final _that = this;
switch (_that) {
case _DenyRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DenyRequest value)?  $default,){
final _that = this;
switch (_that) {
case _DenyRequest() when $default != null:
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
case _DenyRequest() when $default != null:
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
case _DenyRequest():
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
case _DenyRequest() when $default != null:
return $default(_that.userCode);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DenyRequest implements DenyRequest {
  const _DenyRequest({required this.userCode});
  factory _DenyRequest.fromJson(Map<String, dynamic> json) => _$DenyRequestFromJson(json);

/// User code from device display
@override final  String userCode;

/// Create a copy of DenyRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DenyRequestCopyWith<_DenyRequest> get copyWith => __$DenyRequestCopyWithImpl<_DenyRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DenyRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _DenyRequest&&(identical(other.userCode, userCode) || other.userCode == userCode));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,userCode);
}

@override
String toString() {
    return 'DenyRequest(userCode: $userCode)';
}


}

/// @nodoc
abstract mixin class _$DenyRequestCopyWith<$Res> implements $DenyRequestCopyWith<$Res> {
  factory _$DenyRequestCopyWith(_DenyRequest value, $Res Function(_DenyRequest) _then) = __$DenyRequestCopyWithImpl;
@override @useResult
$Res call({
 String userCode
});




}
/// @nodoc
class __$DenyRequestCopyWithImpl<$Res>
    implements _$DenyRequestCopyWith<$Res> {
  __$DenyRequestCopyWithImpl(this._self, this._then);

  final _DenyRequest _self;
  final $Res Function(_DenyRequest) _then;

/// Create a copy of DenyRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? userCode = null,}) {
  return _then(_DenyRequest(
userCode: null == userCode ? _self.userCode : userCode // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
