// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'routes_bulk_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RoutesBulkResponse {

/// Route details, one per readable requested slug
 List<RouteDetailDto> get routes;/// Bounding box of every returned route, or null when routes is empty
 BoundsDto? get extent;
/// Create a copy of RoutesBulkResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RoutesBulkResponseCopyWith<RoutesBulkResponse> get copyWith => _$RoutesBulkResponseCopyWithImpl<RoutesBulkResponse>(this as RoutesBulkResponse, _$identity);

  /// Serializes this RoutesBulkResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as RoutesBulkResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RoutesBulkResponse&&const DeepCollectionEquality().equals(other.routes, _this.routes)&&(identical(other.extent, _this.extent) || other.extent == _this.extent));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as RoutesBulkResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.routes),_this.extent);
}

@override
String toString() {
  final _this = this as RoutesBulkResponse;
  return 'RoutesBulkResponse(routes: ${_this.routes}, extent: ${_this.extent})';
}


}

/// @nodoc
abstract mixin class $RoutesBulkResponseCopyWith<$Res>  {
  factory $RoutesBulkResponseCopyWith(RoutesBulkResponse value, $Res Function(RoutesBulkResponse) _then) = _$RoutesBulkResponseCopyWithImpl;
@useResult
$Res call({
 List<RouteDetailDto> routes, BoundsDto? extent
});


$BoundsDtoCopyWith<$Res>? get extent;

}
/// @nodoc
class _$RoutesBulkResponseCopyWithImpl<$Res>
    implements $RoutesBulkResponseCopyWith<$Res> {
  _$RoutesBulkResponseCopyWithImpl(this._self, this._then);

  final RoutesBulkResponse _self;
  final $Res Function(RoutesBulkResponse) _then;

/// Create a copy of RoutesBulkResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? routes = null,Object? extent = freezed,}) {
  return _then(RoutesBulkResponse(
routes: null == routes ? _self.routes : routes // ignore: cast_nullable_to_non_nullable
as List<RouteDetailDto>,extent: freezed == extent ? _self.extent : extent // ignore: cast_nullable_to_non_nullable
as BoundsDto?,
  ));
}
/// Create a copy of RoutesBulkResponse
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$BoundsDtoCopyWith<$Res>? get extent {
    if (_self.extent == null) {
    return null;
  }

  return $BoundsDtoCopyWith<$Res>(_self.extent!, (value) {
    return _then(_self.copyWith(extent: value));
  });
}
}


/// Adds pattern-matching-related methods to [RoutesBulkResponse].
extension RoutesBulkResponsePatterns on RoutesBulkResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RoutesBulkResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RoutesBulkResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RoutesBulkResponse value)  $default,){
final _that = this;
switch (_that) {
case _RoutesBulkResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RoutesBulkResponse value)?  $default,){
final _that = this;
switch (_that) {
case _RoutesBulkResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<RouteDetailDto> routes,  BoundsDto? extent)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RoutesBulkResponse() when $default != null:
return $default(_that.routes,_that.extent);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<RouteDetailDto> routes,  BoundsDto? extent)  $default,) {final _that = this;
switch (_that) {
case _RoutesBulkResponse():
return $default(_that.routes,_that.extent);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<RouteDetailDto> routes,  BoundsDto? extent)?  $default,) {final _that = this;
switch (_that) {
case _RoutesBulkResponse() when $default != null:
return $default(_that.routes,_that.extent);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RoutesBulkResponse implements RoutesBulkResponse {
  const _RoutesBulkResponse({required  List<RouteDetailDto> routes, this.extent}): _routes = routes;
  factory _RoutesBulkResponse.fromJson(Map<String, dynamic> json) => _$RoutesBulkResponseFromJson(json);

/// Route details, one per readable requested slug
 final  List<RouteDetailDto> _routes;
/// Route details, one per readable requested slug
@override List<RouteDetailDto> get routes {
  if (_routes is EqualUnmodifiableListView) return _routes;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_routes);
}

/// Bounding box of every returned route, or null when routes is empty
@override final  BoundsDto? extent;

/// Create a copy of RoutesBulkResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RoutesBulkResponseCopyWith<_RoutesBulkResponse> get copyWith => __$RoutesBulkResponseCopyWithImpl<_RoutesBulkResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RoutesBulkResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _RoutesBulkResponse&&const DeepCollectionEquality().equals(other.routes, _routes)&&(identical(other.extent, extent) || other.extent == extent));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_routes),extent);
}

@override
String toString() {
    return 'RoutesBulkResponse(routes: $routes, extent: $extent)';
}


}

/// @nodoc
abstract mixin class _$RoutesBulkResponseCopyWith<$Res> implements $RoutesBulkResponseCopyWith<$Res> {
  factory _$RoutesBulkResponseCopyWith(_RoutesBulkResponse value, $Res Function(_RoutesBulkResponse) _then) = __$RoutesBulkResponseCopyWithImpl;
@override @useResult
$Res call({
 List<RouteDetailDto> routes, BoundsDto? extent
});


@override $BoundsDtoCopyWith<$Res>? get extent;

}
/// @nodoc
class __$RoutesBulkResponseCopyWithImpl<$Res>
    implements _$RoutesBulkResponseCopyWith<$Res> {
  __$RoutesBulkResponseCopyWithImpl(this._self, this._then);

  final _RoutesBulkResponse _self;
  final $Res Function(_RoutesBulkResponse) _then;

/// Create a copy of RoutesBulkResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? routes = null,Object? extent = freezed,}) {
  return _then(_RoutesBulkResponse(
routes: null == routes ? _self._routes : routes // ignore: cast_nullable_to_non_nullable
as List<RouteDetailDto>,extent: freezed == extent ? _self.extent : extent // ignore: cast_nullable_to_non_nullable
as BoundsDto?,
  ));
}

/// Create a copy of RoutesBulkResponse
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$BoundsDtoCopyWith<$Res>? get extent {
    if (_self.extent == null) {
    return null;
  }

  return $BoundsDtoCopyWith<$Res>(_self.extent!, (value) {
    return _then(_self.copyWith(extent: value));
  });
}
}

// dart format on
