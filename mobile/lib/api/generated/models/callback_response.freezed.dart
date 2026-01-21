// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'callback_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CallbackResponse {

/// Redirect URL with auth code
 String get redirectUrl;
/// Create a copy of CallbackResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CallbackResponseCopyWith<CallbackResponse> get copyWith => _$CallbackResponseCopyWithImpl<CallbackResponse>(this as CallbackResponse, _$identity);

  /// Serializes this CallbackResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CallbackResponse&&(identical(other.redirectUrl, redirectUrl) || other.redirectUrl == redirectUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,redirectUrl);

@override
String toString() {
  return 'CallbackResponse(redirectUrl: $redirectUrl)';
}


}

/// @nodoc
abstract mixin class $CallbackResponseCopyWith<$Res>  {
  factory $CallbackResponseCopyWith(CallbackResponse value, $Res Function(CallbackResponse) _then) = _$CallbackResponseCopyWithImpl;
@useResult
$Res call({
 String redirectUrl
});




}
/// @nodoc
class _$CallbackResponseCopyWithImpl<$Res>
    implements $CallbackResponseCopyWith<$Res> {
  _$CallbackResponseCopyWithImpl(this._self, this._then);

  final CallbackResponse _self;
  final $Res Function(CallbackResponse) _then;

/// Create a copy of CallbackResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? redirectUrl = null,}) {
  return _then(_self.copyWith(
redirectUrl: null == redirectUrl ? _self.redirectUrl : redirectUrl // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [CallbackResponse].
extension CallbackResponsePatterns on CallbackResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CallbackResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CallbackResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CallbackResponse value)  $default,){
final _that = this;
switch (_that) {
case _CallbackResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CallbackResponse value)?  $default,){
final _that = this;
switch (_that) {
case _CallbackResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String redirectUrl)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CallbackResponse() when $default != null:
return $default(_that.redirectUrl);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String redirectUrl)  $default,) {final _that = this;
switch (_that) {
case _CallbackResponse():
return $default(_that.redirectUrl);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String redirectUrl)?  $default,) {final _that = this;
switch (_that) {
case _CallbackResponse() when $default != null:
return $default(_that.redirectUrl);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CallbackResponse implements CallbackResponse {
  const _CallbackResponse({required this.redirectUrl});
  factory _CallbackResponse.fromJson(Map<String, dynamic> json) => _$CallbackResponseFromJson(json);

/// Redirect URL with auth code
@override final  String redirectUrl;

/// Create a copy of CallbackResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CallbackResponseCopyWith<_CallbackResponse> get copyWith => __$CallbackResponseCopyWithImpl<_CallbackResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CallbackResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _CallbackResponse&&(identical(other.redirectUrl, redirectUrl) || other.redirectUrl == redirectUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,redirectUrl);

@override
String toString() {
  return 'CallbackResponse(redirectUrl: $redirectUrl)';
}


}

/// @nodoc
abstract mixin class _$CallbackResponseCopyWith<$Res> implements $CallbackResponseCopyWith<$Res> {
  factory _$CallbackResponseCopyWith(_CallbackResponse value, $Res Function(_CallbackResponse) _then) = __$CallbackResponseCopyWithImpl;
@override @useResult
$Res call({
 String redirectUrl
});




}
/// @nodoc
class __$CallbackResponseCopyWithImpl<$Res>
    implements _$CallbackResponseCopyWith<$Res> {
  __$CallbackResponseCopyWithImpl(this._self, this._then);

  final _CallbackResponse _self;
  final $Res Function(_CallbackResponse) _then;

/// Create a copy of CallbackResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? redirectUrl = null,}) {
  return _then(_CallbackResponse(
redirectUrl: null == redirectUrl ? _self.redirectUrl : redirectUrl // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
