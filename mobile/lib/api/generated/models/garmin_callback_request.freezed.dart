// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'garmin_callback_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GarminCallbackRequest {

/// User's access token from frontend auth
 String get accessToken;/// Original redirect URI from authorize request
 String get redirectUri;/// State parameter from authorize request
 String? get state;
/// Create a copy of GarminCallbackRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GarminCallbackRequestCopyWith<GarminCallbackRequest> get copyWith => _$GarminCallbackRequestCopyWithImpl<GarminCallbackRequest>(this as GarminCallbackRequest, _$identity);

  /// Serializes this GarminCallbackRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GarminCallbackRequest&&(identical(other.accessToken, accessToken) || other.accessToken == accessToken)&&(identical(other.redirectUri, redirectUri) || other.redirectUri == redirectUri)&&(identical(other.state, state) || other.state == state));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,accessToken,redirectUri,state);

@override
String toString() {
  return 'GarminCallbackRequest(accessToken: $accessToken, redirectUri: $redirectUri, state: $state)';
}


}

/// @nodoc
abstract mixin class $GarminCallbackRequestCopyWith<$Res>  {
  factory $GarminCallbackRequestCopyWith(GarminCallbackRequest value, $Res Function(GarminCallbackRequest) _then) = _$GarminCallbackRequestCopyWithImpl;
@useResult
$Res call({
 String accessToken, String redirectUri, String? state
});




}
/// @nodoc
class _$GarminCallbackRequestCopyWithImpl<$Res>
    implements $GarminCallbackRequestCopyWith<$Res> {
  _$GarminCallbackRequestCopyWithImpl(this._self, this._then);

  final GarminCallbackRequest _self;
  final $Res Function(GarminCallbackRequest) _then;

/// Create a copy of GarminCallbackRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? accessToken = null,Object? redirectUri = null,Object? state = freezed,}) {
  return _then(_self.copyWith(
accessToken: null == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String,redirectUri: null == redirectUri ? _self.redirectUri : redirectUri // ignore: cast_nullable_to_non_nullable
as String,state: freezed == state ? _self.state : state // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [GarminCallbackRequest].
extension GarminCallbackRequestPatterns on GarminCallbackRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GarminCallbackRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GarminCallbackRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GarminCallbackRequest value)  $default,){
final _that = this;
switch (_that) {
case _GarminCallbackRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GarminCallbackRequest value)?  $default,){
final _that = this;
switch (_that) {
case _GarminCallbackRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String accessToken,  String redirectUri,  String? state)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GarminCallbackRequest() when $default != null:
return $default(_that.accessToken,_that.redirectUri,_that.state);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String accessToken,  String redirectUri,  String? state)  $default,) {final _that = this;
switch (_that) {
case _GarminCallbackRequest():
return $default(_that.accessToken,_that.redirectUri,_that.state);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String accessToken,  String redirectUri,  String? state)?  $default,) {final _that = this;
switch (_that) {
case _GarminCallbackRequest() when $default != null:
return $default(_that.accessToken,_that.redirectUri,_that.state);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GarminCallbackRequest implements GarminCallbackRequest {
  const _GarminCallbackRequest({required this.accessToken, required this.redirectUri, this.state});
  factory _GarminCallbackRequest.fromJson(Map<String, dynamic> json) => _$GarminCallbackRequestFromJson(json);

/// User's access token from frontend auth
@override final  String accessToken;
/// Original redirect URI from authorize request
@override final  String redirectUri;
/// State parameter from authorize request
@override final  String? state;

/// Create a copy of GarminCallbackRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GarminCallbackRequestCopyWith<_GarminCallbackRequest> get copyWith => __$GarminCallbackRequestCopyWithImpl<_GarminCallbackRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GarminCallbackRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GarminCallbackRequest&&(identical(other.accessToken, accessToken) || other.accessToken == accessToken)&&(identical(other.redirectUri, redirectUri) || other.redirectUri == redirectUri)&&(identical(other.state, state) || other.state == state));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,accessToken,redirectUri,state);

@override
String toString() {
  return 'GarminCallbackRequest(accessToken: $accessToken, redirectUri: $redirectUri, state: $state)';
}


}

/// @nodoc
abstract mixin class _$GarminCallbackRequestCopyWith<$Res> implements $GarminCallbackRequestCopyWith<$Res> {
  factory _$GarminCallbackRequestCopyWith(_GarminCallbackRequest value, $Res Function(_GarminCallbackRequest) _then) = __$GarminCallbackRequestCopyWithImpl;
@override @useResult
$Res call({
 String accessToken, String redirectUri, String? state
});




}
/// @nodoc
class __$GarminCallbackRequestCopyWithImpl<$Res>
    implements _$GarminCallbackRequestCopyWith<$Res> {
  __$GarminCallbackRequestCopyWithImpl(this._self, this._then);

  final _GarminCallbackRequest _self;
  final $Res Function(_GarminCallbackRequest) _then;

/// Create a copy of GarminCallbackRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? accessToken = null,Object? redirectUri = null,Object? state = freezed,}) {
  return _then(_GarminCallbackRequest(
accessToken: null == accessToken ? _self.accessToken : accessToken // ignore: cast_nullable_to_non_nullable
as String,redirectUri: null == redirectUri ? _self.redirectUri : redirectUri // ignore: cast_nullable_to_non_nullable
as String,state: freezed == state ? _self.state : state // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
