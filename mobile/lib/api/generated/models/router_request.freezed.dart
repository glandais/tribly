// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'router_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouterRequest {

 GeoPoint get from; GeoPoint get to; RouterProfile get profile;
/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouterRequestCopyWith<RouterRequest> get copyWith => _$RouterRequestCopyWithImpl<RouterRequest>(this as RouterRequest, _$identity);

  /// Serializes this RouterRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouterRequest&&(identical(other.from, from) || other.from == from)&&(identical(other.to, to) || other.to == to)&&(identical(other.profile, profile) || other.profile == profile));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,from,to,profile);

@override
String toString() {
  return 'RouterRequest(from: $from, to: $to, profile: $profile)';
}


}

/// @nodoc
abstract mixin class $RouterRequestCopyWith<$Res>  {
  factory $RouterRequestCopyWith(RouterRequest value, $Res Function(RouterRequest) _then) = _$RouterRequestCopyWithImpl;
@useResult
$Res call({
 GeoPoint from, GeoPoint to, RouterProfile profile
});


$GeoPointCopyWith<$Res> get from;$GeoPointCopyWith<$Res> get to;

}
/// @nodoc
class _$RouterRequestCopyWithImpl<$Res>
    implements $RouterRequestCopyWith<$Res> {
  _$RouterRequestCopyWithImpl(this._self, this._then);

  final RouterRequest _self;
  final $Res Function(RouterRequest) _then;

/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? from = null,Object? to = null,Object? profile = null,}) {
  return _then(_self.copyWith(
from: null == from ? _self.from : from // ignore: cast_nullable_to_non_nullable
as GeoPoint,to: null == to ? _self.to : to // ignore: cast_nullable_to_non_nullable
as GeoPoint,profile: null == profile ? _self.profile : profile // ignore: cast_nullable_to_non_nullable
as RouterProfile,
  ));
}
/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoPointCopyWith<$Res> get from {
  
  return $GeoPointCopyWith<$Res>(_self.from, (value) {
    return _then(_self.copyWith(from: value));
  });
}/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoPointCopyWith<$Res> get to {
  
  return $GeoPointCopyWith<$Res>(_self.to, (value) {
    return _then(_self.copyWith(to: value));
  });
}
}


/// Adds pattern-matching-related methods to [RouterRequest].
extension RouterRequestPatterns on RouterRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouterRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouterRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouterRequest value)  $default,){
final _that = this;
switch (_that) {
case _RouterRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouterRequest value)?  $default,){
final _that = this;
switch (_that) {
case _RouterRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( GeoPoint from,  GeoPoint to,  RouterProfile profile)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouterRequest() when $default != null:
return $default(_that.from,_that.to,_that.profile);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( GeoPoint from,  GeoPoint to,  RouterProfile profile)  $default,) {final _that = this;
switch (_that) {
case _RouterRequest():
return $default(_that.from,_that.to,_that.profile);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( GeoPoint from,  GeoPoint to,  RouterProfile profile)?  $default,) {final _that = this;
switch (_that) {
case _RouterRequest() when $default != null:
return $default(_that.from,_that.to,_that.profile);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouterRequest implements RouterRequest {
  const _RouterRequest({required this.from, required this.to, required this.profile});
  factory _RouterRequest.fromJson(Map<String, dynamic> json) => _$RouterRequestFromJson(json);

@override final  GeoPoint from;
@override final  GeoPoint to;
@override final  RouterProfile profile;

/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouterRequestCopyWith<_RouterRequest> get copyWith => __$RouterRequestCopyWithImpl<_RouterRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouterRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouterRequest&&(identical(other.from, from) || other.from == from)&&(identical(other.to, to) || other.to == to)&&(identical(other.profile, profile) || other.profile == profile));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,from,to,profile);

@override
String toString() {
  return 'RouterRequest(from: $from, to: $to, profile: $profile)';
}


}

/// @nodoc
abstract mixin class _$RouterRequestCopyWith<$Res> implements $RouterRequestCopyWith<$Res> {
  factory _$RouterRequestCopyWith(_RouterRequest value, $Res Function(_RouterRequest) _then) = __$RouterRequestCopyWithImpl;
@override @useResult
$Res call({
 GeoPoint from, GeoPoint to, RouterProfile profile
});


@override $GeoPointCopyWith<$Res> get from;@override $GeoPointCopyWith<$Res> get to;

}
/// @nodoc
class __$RouterRequestCopyWithImpl<$Res>
    implements _$RouterRequestCopyWith<$Res> {
  __$RouterRequestCopyWithImpl(this._self, this._then);

  final _RouterRequest _self;
  final $Res Function(_RouterRequest) _then;

/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? from = null,Object? to = null,Object? profile = null,}) {
  return _then(_RouterRequest(
from: null == from ? _self.from : from // ignore: cast_nullable_to_non_nullable
as GeoPoint,to: null == to ? _self.to : to // ignore: cast_nullable_to_non_nullable
as GeoPoint,profile: null == profile ? _self.profile : profile // ignore: cast_nullable_to_non_nullable
as RouterProfile,
  ));
}

/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoPointCopyWith<$Res> get from {
  
  return $GeoPointCopyWith<$Res>(_self.from, (value) {
    return _then(_self.copyWith(from: value));
  });
}/// Create a copy of RouterRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$GeoPointCopyWith<$Res> get to {
  
  return $GeoPointCopyWith<$Res>(_self.to, (value) {
    return _then(_self.copyWith(to: value));
  });
}
}

// dart format on
