// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'karoo_routes_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$KarooRoutesResponse {

/// Available routes
 List<KarooRouteDto> get routes;
/// Create a copy of KarooRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$KarooRoutesResponseCopyWith<KarooRoutesResponse> get copyWith => _$KarooRoutesResponseCopyWithImpl<KarooRoutesResponse>(this as KarooRoutesResponse, _$identity);

  /// Serializes this KarooRoutesResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is KarooRoutesResponse&&const DeepCollectionEquality().equals(other.routes, routes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(routes));

@override
String toString() {
  return 'KarooRoutesResponse(routes: $routes)';
}


}

/// @nodoc
abstract mixin class $KarooRoutesResponseCopyWith<$Res>  {
  factory $KarooRoutesResponseCopyWith(KarooRoutesResponse value, $Res Function(KarooRoutesResponse) _then) = _$KarooRoutesResponseCopyWithImpl;
@useResult
$Res call({
 List<KarooRouteDto> routes
});




}
/// @nodoc
class _$KarooRoutesResponseCopyWithImpl<$Res>
    implements $KarooRoutesResponseCopyWith<$Res> {
  _$KarooRoutesResponseCopyWithImpl(this._self, this._then);

  final KarooRoutesResponse _self;
  final $Res Function(KarooRoutesResponse) _then;

/// Create a copy of KarooRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? routes = null,}) {
  return _then(KarooRoutesResponse(
routes: null == routes ? _self.routes : routes // ignore: cast_nullable_to_non_nullable
as List<KarooRouteDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [KarooRoutesResponse].
extension KarooRoutesResponsePatterns on KarooRoutesResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _KarooRoutesResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _KarooRoutesResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _KarooRoutesResponse value)  $default,){
final _that = this;
switch (_that) {
case _KarooRoutesResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _KarooRoutesResponse value)?  $default,){
final _that = this;
switch (_that) {
case _KarooRoutesResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<KarooRouteDto> routes)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _KarooRoutesResponse() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<KarooRouteDto> routes)  $default,) {final _that = this;
switch (_that) {
case _KarooRoutesResponse():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<KarooRouteDto> routes)?  $default,) {final _that = this;
switch (_that) {
case _KarooRoutesResponse() when $default != null:
return $default(_that.routes);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _KarooRoutesResponse implements KarooRoutesResponse {
  const _KarooRoutesResponse({required  List<KarooRouteDto> routes}): _routes = routes;
  factory _KarooRoutesResponse.fromJson(Map<String, dynamic> json) => _$KarooRoutesResponseFromJson(json);

/// Available routes
 final  List<KarooRouteDto> _routes;
/// Available routes
@override List<KarooRouteDto> get routes {
  if (_routes is EqualUnmodifiableListView) return _routes;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_routes);
}


/// Create a copy of KarooRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$KarooRoutesResponseCopyWith<_KarooRoutesResponse> get copyWith => __$KarooRoutesResponseCopyWithImpl<_KarooRoutesResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$KarooRoutesResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _KarooRoutesResponse&&const DeepCollectionEquality().equals(other._routes, _routes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_routes));

@override
String toString() {
  return 'KarooRoutesResponse(routes: $routes)';
}


}

/// @nodoc
abstract mixin class _$KarooRoutesResponseCopyWith<$Res> implements $KarooRoutesResponseCopyWith<$Res> {
  factory _$KarooRoutesResponseCopyWith(_KarooRoutesResponse value, $Res Function(_KarooRoutesResponse) _then) = __$KarooRoutesResponseCopyWithImpl;
@override @useResult
$Res call({
 List<KarooRouteDto> routes
});




}
/// @nodoc
class __$KarooRoutesResponseCopyWithImpl<$Res>
    implements _$KarooRoutesResponseCopyWith<$Res> {
  __$KarooRoutesResponseCopyWithImpl(this._self, this._then);

  final _KarooRoutesResponse _self;
  final $Res Function(_KarooRoutesResponse) _then;

/// Create a copy of KarooRoutesResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? routes = null,}) {
  return _then(_KarooRoutesResponse(
routes: null == routes ? _self._routes : routes // ignore: cast_nullable_to_non_nullable
as List<KarooRouteDto>,
  ));
}


}

// dart format on
