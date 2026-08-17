// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'route_bounds_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouteBoundsResponse {

/// Bounding box, or null when no route matches
 BoundsDto? get bounds;
/// Create a copy of RouteBoundsResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouteBoundsResponseCopyWith<RouteBoundsResponse> get copyWith => _$RouteBoundsResponseCopyWithImpl<RouteBoundsResponse>(this as RouteBoundsResponse, _$identity);

  /// Serializes this RouteBoundsResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouteBoundsResponse&&(identical(other.bounds, bounds) || other.bounds == bounds));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,bounds);

@override
String toString() {
  return 'RouteBoundsResponse(bounds: $bounds)';
}


}

/// @nodoc
abstract mixin class $RouteBoundsResponseCopyWith<$Res>  {
  factory $RouteBoundsResponseCopyWith(RouteBoundsResponse value, $Res Function(RouteBoundsResponse) _then) = _$RouteBoundsResponseCopyWithImpl;
@useResult
$Res call({
 BoundsDto? bounds
});


$BoundsDtoCopyWith<$Res>? get bounds;

}
/// @nodoc
class _$RouteBoundsResponseCopyWithImpl<$Res>
    implements $RouteBoundsResponseCopyWith<$Res> {
  _$RouteBoundsResponseCopyWithImpl(this._self, this._then);

  final RouteBoundsResponse _self;
  final $Res Function(RouteBoundsResponse) _then;

/// Create a copy of RouteBoundsResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? bounds = freezed,}) {
  return _then(RouteBoundsResponse(
bounds: freezed == bounds ? _self.bounds : bounds // ignore: cast_nullable_to_non_nullable
as BoundsDto?,
  ));
}
/// Create a copy of RouteBoundsResponse
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$BoundsDtoCopyWith<$Res>? get bounds {
    if (_self.bounds == null) {
    return null;
  }

  return $BoundsDtoCopyWith<$Res>(_self.bounds!, (value) {
    return _then(_self.copyWith(bounds: value));
  });
}
}


/// Adds pattern-matching-related methods to [RouteBoundsResponse].
extension RouteBoundsResponsePatterns on RouteBoundsResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouteBoundsResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouteBoundsResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouteBoundsResponse value)  $default,){
final _that = this;
switch (_that) {
case _RouteBoundsResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouteBoundsResponse value)?  $default,){
final _that = this;
switch (_that) {
case _RouteBoundsResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( BoundsDto? bounds)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouteBoundsResponse() when $default != null:
return $default(_that.bounds);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( BoundsDto? bounds)  $default,) {final _that = this;
switch (_that) {
case _RouteBoundsResponse():
return $default(_that.bounds);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( BoundsDto? bounds)?  $default,) {final _that = this;
switch (_that) {
case _RouteBoundsResponse() when $default != null:
return $default(_that.bounds);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouteBoundsResponse implements RouteBoundsResponse {
  const _RouteBoundsResponse({this.bounds});
  factory _RouteBoundsResponse.fromJson(Map<String, dynamic> json) => _$RouteBoundsResponseFromJson(json);

/// Bounding box, or null when no route matches
@override final  BoundsDto? bounds;

/// Create a copy of RouteBoundsResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouteBoundsResponseCopyWith<_RouteBoundsResponse> get copyWith => __$RouteBoundsResponseCopyWithImpl<_RouteBoundsResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouteBoundsResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouteBoundsResponse&&(identical(other.bounds, bounds) || other.bounds == bounds));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,bounds);

@override
String toString() {
  return 'RouteBoundsResponse(bounds: $bounds)';
}


}

/// @nodoc
abstract mixin class _$RouteBoundsResponseCopyWith<$Res> implements $RouteBoundsResponseCopyWith<$Res> {
  factory _$RouteBoundsResponseCopyWith(_RouteBoundsResponse value, $Res Function(_RouteBoundsResponse) _then) = __$RouteBoundsResponseCopyWithImpl;
@override @useResult
$Res call({
 BoundsDto? bounds
});


@override $BoundsDtoCopyWith<$Res>? get bounds;

}
/// @nodoc
class __$RouteBoundsResponseCopyWithImpl<$Res>
    implements _$RouteBoundsResponseCopyWith<$Res> {
  __$RouteBoundsResponseCopyWithImpl(this._self, this._then);

  final _RouteBoundsResponse _self;
  final $Res Function(_RouteBoundsResponse) _then;

/// Create a copy of RouteBoundsResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? bounds = freezed,}) {
  return _then(_RouteBoundsResponse(
bounds: freezed == bounds ? _self.bounds : bounds // ignore: cast_nullable_to_non_nullable
as BoundsDto?,
  ));
}

/// Create a copy of RouteBoundsResponse
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$BoundsDtoCopyWith<$Res>? get bounds {
    if (_self.bounds == null) {
    return null;
  }

  return $BoundsDtoCopyWith<$Res>(_self.bounds!, (value) {
    return _then(_self.copyWith(bounds: value));
  });
}
}

// dart format on
