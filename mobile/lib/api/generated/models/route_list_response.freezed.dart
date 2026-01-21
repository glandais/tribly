// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'route_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouteListResponse {

/// List of routes
 List<RouteDto> get routes;/// Total number of routes
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of RouteListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouteListResponseCopyWith<RouteListResponse> get copyWith => _$RouteListResponseCopyWithImpl<RouteListResponse>(this as RouteListResponse, _$identity);

  /// Serializes this RouteListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouteListResponse&&const DeepCollectionEquality().equals(other.routes, routes)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(routes),total,page,size);

@override
String toString() {
  return 'RouteListResponse(routes: $routes, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $RouteListResponseCopyWith<$Res>  {
  factory $RouteListResponseCopyWith(RouteListResponse value, $Res Function(RouteListResponse) _then) = _$RouteListResponseCopyWithImpl;
@useResult
$Res call({
 List<RouteDto> routes, int total, int page, int size
});




}
/// @nodoc
class _$RouteListResponseCopyWithImpl<$Res>
    implements $RouteListResponseCopyWith<$Res> {
  _$RouteListResponseCopyWithImpl(this._self, this._then);

  final RouteListResponse _self;
  final $Res Function(RouteListResponse) _then;

/// Create a copy of RouteListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? routes = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_self.copyWith(
routes: null == routes ? _self.routes : routes // ignore: cast_nullable_to_non_nullable
as List<RouteDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [RouteListResponse].
extension RouteListResponsePatterns on RouteListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouteListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouteListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouteListResponse value)  $default,){
final _that = this;
switch (_that) {
case _RouteListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouteListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _RouteListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<RouteDto> routes,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouteListResponse() when $default != null:
return $default(_that.routes,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<RouteDto> routes,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _RouteListResponse():
return $default(_that.routes,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<RouteDto> routes,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _RouteListResponse() when $default != null:
return $default(_that.routes,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouteListResponse implements RouteListResponse {
  const _RouteListResponse({required final  List<RouteDto> routes, required this.total, required this.page, required this.size}): _routes = routes;
  factory _RouteListResponse.fromJson(Map<String, dynamic> json) => _$RouteListResponseFromJson(json);

/// List of routes
 final  List<RouteDto> _routes;
/// List of routes
@override List<RouteDto> get routes {
  if (_routes is EqualUnmodifiableListView) return _routes;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_routes);
}

/// Total number of routes
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of RouteListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouteListResponseCopyWith<_RouteListResponse> get copyWith => __$RouteListResponseCopyWithImpl<_RouteListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouteListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouteListResponse&&const DeepCollectionEquality().equals(other._routes, _routes)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_routes),total,page,size);

@override
String toString() {
  return 'RouteListResponse(routes: $routes, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$RouteListResponseCopyWith<$Res> implements $RouteListResponseCopyWith<$Res> {
  factory _$RouteListResponseCopyWith(_RouteListResponse value, $Res Function(_RouteListResponse) _then) = __$RouteListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<RouteDto> routes, int total, int page, int size
});




}
/// @nodoc
class __$RouteListResponseCopyWithImpl<$Res>
    implements _$RouteListResponseCopyWith<$Res> {
  __$RouteListResponseCopyWithImpl(this._self, this._then);

  final _RouteListResponse _self;
  final $Res Function(_RouteListResponse) _then;

/// Create a copy of RouteListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? routes = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_RouteListResponse(
routes: null == routes ? _self._routes : routes // ignore: cast_nullable_to_non_nullable
as List<RouteDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
