// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'garmin_routes_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GarminRoutesResponse {

/// Routes list
 List<GarminRouteDto> get routes;
/// Create a copy of GarminRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GarminRoutesResponseCopyWith<GarminRoutesResponse> get copyWith => _$GarminRoutesResponseCopyWithImpl<GarminRoutesResponse>(this as GarminRoutesResponse, _$identity);

  /// Serializes this GarminRoutesResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GarminRoutesResponse&&const DeepCollectionEquality().equals(other.routes, routes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(routes));

@override
String toString() {
  return 'GarminRoutesResponse(routes: $routes)';
}


}

/// @nodoc
abstract mixin class $GarminRoutesResponseCopyWith<$Res>  {
  factory $GarminRoutesResponseCopyWith(GarminRoutesResponse value, $Res Function(GarminRoutesResponse) _then) = _$GarminRoutesResponseCopyWithImpl;
@useResult
$Res call({
 List<GarminRouteDto> routes
});




}
/// @nodoc
class _$GarminRoutesResponseCopyWithImpl<$Res>
    implements $GarminRoutesResponseCopyWith<$Res> {
  _$GarminRoutesResponseCopyWithImpl(this._self, this._then);

  final GarminRoutesResponse _self;
  final $Res Function(GarminRoutesResponse) _then;

/// Create a copy of GarminRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? routes = null,}) {
  return _then(_self.copyWith(
routes: null == routes ? _self.routes : routes // ignore: cast_nullable_to_non_nullable
as List<GarminRouteDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [GarminRoutesResponse].
extension GarminRoutesResponsePatterns on GarminRoutesResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GarminRoutesResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GarminRoutesResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GarminRoutesResponse value)  $default,){
final _that = this;
switch (_that) {
case _GarminRoutesResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GarminRoutesResponse value)?  $default,){
final _that = this;
switch (_that) {
case _GarminRoutesResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<GarminRouteDto> routes)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GarminRoutesResponse() when $default != null:
return $default(_that.routes);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<GarminRouteDto> routes)  $default,) {final _that = this;
switch (_that) {
case _GarminRoutesResponse():
return $default(_that.routes);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<GarminRouteDto> routes)?  $default,) {final _that = this;
switch (_that) {
case _GarminRoutesResponse() when $default != null:
return $default(_that.routes);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GarminRoutesResponse implements GarminRoutesResponse {
  const _GarminRoutesResponse({required final  List<GarminRouteDto> routes}): _routes = routes;
  factory _GarminRoutesResponse.fromJson(Map<String, dynamic> json) => _$GarminRoutesResponseFromJson(json);

/// Routes list
 final  List<GarminRouteDto> _routes;
/// Routes list
@override List<GarminRouteDto> get routes {
  if (_routes is EqualUnmodifiableListView) return _routes;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_routes);
}


/// Create a copy of GarminRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GarminRoutesResponseCopyWith<_GarminRoutesResponse> get copyWith => __$GarminRoutesResponseCopyWithImpl<_GarminRoutesResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GarminRoutesResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GarminRoutesResponse&&const DeepCollectionEquality().equals(other._routes, _routes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_routes));

@override
String toString() {
  return 'GarminRoutesResponse(routes: $routes)';
}


}

/// @nodoc
abstract mixin class _$GarminRoutesResponseCopyWith<$Res> implements $GarminRoutesResponseCopyWith<$Res> {
  factory _$GarminRoutesResponseCopyWith(_GarminRoutesResponse value, $Res Function(_GarminRoutesResponse) _then) = __$GarminRoutesResponseCopyWithImpl;
@override @useResult
$Res call({
 List<GarminRouteDto> routes
});




}
/// @nodoc
class __$GarminRoutesResponseCopyWithImpl<$Res>
    implements _$GarminRoutesResponseCopyWith<$Res> {
  __$GarminRoutesResponseCopyWithImpl(this._self, this._then);

  final _GarminRoutesResponse _self;
  final $Res Function(_GarminRoutesResponse) _then;

/// Create a copy of GarminRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? routes = null,}) {
  return _then(_GarminRoutesResponse(
routes: null == routes ? _self._routes : routes // ignore: cast_nullable_to_non_nullable
as List<GarminRouteDto>,
  ));
}


}

// dart format on
