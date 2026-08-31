// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'strava_session_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$StravaSessionRequest {

/// One-time login code from the Strava callback
 String get code;
/// Create a copy of StravaSessionRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$StravaSessionRequestCopyWith<StravaSessionRequest> get copyWith => _$StravaSessionRequestCopyWithImpl<StravaSessionRequest>(this as StravaSessionRequest, _$identity);

  /// Serializes this StravaSessionRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as StravaSessionRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is StravaSessionRequest&&(identical(other.code, _this.code) || other.code == _this.code));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as StravaSessionRequest;
  return Object.hash(runtimeType,_this.code);
}

@override
String toString() {
  final _this = this as StravaSessionRequest;
  return 'StravaSessionRequest(code: ${_this.code})';
}


}

/// @nodoc
abstract mixin class $StravaSessionRequestCopyWith<$Res>  {
  factory $StravaSessionRequestCopyWith(StravaSessionRequest value, $Res Function(StravaSessionRequest) _then) = _$StravaSessionRequestCopyWithImpl;
@useResult
$Res call({
 String code
});




}
/// @nodoc
class _$StravaSessionRequestCopyWithImpl<$Res>
    implements $StravaSessionRequestCopyWith<$Res> {
  _$StravaSessionRequestCopyWithImpl(this._self, this._then);

  final StravaSessionRequest _self;
  final $Res Function(StravaSessionRequest) _then;

/// Create a copy of StravaSessionRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? code = null,}) {
  return _then(StravaSessionRequest(
code: null == code ? _self.code : code // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [StravaSessionRequest].
extension StravaSessionRequestPatterns on StravaSessionRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _StravaSessionRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _StravaSessionRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _StravaSessionRequest value)  $default,){
final _that = this;
switch (_that) {
case _StravaSessionRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _StravaSessionRequest value)?  $default,){
final _that = this;
switch (_that) {
case _StravaSessionRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String code)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _StravaSessionRequest() when $default != null:
return $default(_that.code);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String code)  $default,) {final _that = this;
switch (_that) {
case _StravaSessionRequest():
return $default(_that.code);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String code)?  $default,) {final _that = this;
switch (_that) {
case _StravaSessionRequest() when $default != null:
return $default(_that.code);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _StravaSessionRequest implements StravaSessionRequest {
  const _StravaSessionRequest({required this.code});
  factory _StravaSessionRequest.fromJson(Map<String, dynamic> json) => _$StravaSessionRequestFromJson(json);

/// One-time login code from the Strava callback
@override final  String code;

/// Create a copy of StravaSessionRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$StravaSessionRequestCopyWith<_StravaSessionRequest> get copyWith => __$StravaSessionRequestCopyWithImpl<_StravaSessionRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$StravaSessionRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _StravaSessionRequest&&(identical(other.code, code) || other.code == code));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,code);
}

@override
String toString() {
    return 'StravaSessionRequest(code: $code)';
}


}

/// @nodoc
abstract mixin class _$StravaSessionRequestCopyWith<$Res> implements $StravaSessionRequestCopyWith<$Res> {
  factory _$StravaSessionRequestCopyWith(_StravaSessionRequest value, $Res Function(_StravaSessionRequest) _then) = __$StravaSessionRequestCopyWithImpl;
@override @useResult
$Res call({
 String code
});




}
/// @nodoc
class __$StravaSessionRequestCopyWithImpl<$Res>
    implements _$StravaSessionRequestCopyWith<$Res> {
  __$StravaSessionRequestCopyWithImpl(this._self, this._then);

  final _StravaSessionRequest _self;
  final $Res Function(_StravaSessionRequest) _then;

/// Create a copy of StravaSessionRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? code = null,}) {
  return _then(_StravaSessionRequest(
code: null == code ? _self.code : code // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
