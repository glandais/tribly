// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'gpx_preview_from_points_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GpxPreviewFromPointsRequest {

/// Preview name
 String get name;/// Points from frontend routing
 List<GeoPoint> get points;
/// Create a copy of GpxPreviewFromPointsRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GpxPreviewFromPointsRequestCopyWith<GpxPreviewFromPointsRequest> get copyWith => _$GpxPreviewFromPointsRequestCopyWithImpl<GpxPreviewFromPointsRequest>(this as GpxPreviewFromPointsRequest, _$identity);

  /// Serializes this GpxPreviewFromPointsRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GpxPreviewFromPointsRequest&&(identical(other.name, name) || other.name == name)&&const DeepCollectionEquality().equals(other.points, points));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,const DeepCollectionEquality().hash(points));

@override
String toString() {
  return 'GpxPreviewFromPointsRequest(name: $name, points: $points)';
}


}

/// @nodoc
abstract mixin class $GpxPreviewFromPointsRequestCopyWith<$Res>  {
  factory $GpxPreviewFromPointsRequestCopyWith(GpxPreviewFromPointsRequest value, $Res Function(GpxPreviewFromPointsRequest) _then) = _$GpxPreviewFromPointsRequestCopyWithImpl;
@useResult
$Res call({
 String name, List<GeoPoint> points
});




}
/// @nodoc
class _$GpxPreviewFromPointsRequestCopyWithImpl<$Res>
    implements $GpxPreviewFromPointsRequestCopyWith<$Res> {
  _$GpxPreviewFromPointsRequestCopyWithImpl(this._self, this._then);

  final GpxPreviewFromPointsRequest _self;
  final $Res Function(GpxPreviewFromPointsRequest) _then;

/// Create a copy of GpxPreviewFromPointsRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? points = null,}) {
  return _then(GpxPreviewFromPointsRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,points: null == points ? _self.points : points // ignore: cast_nullable_to_non_nullable
as List<GeoPoint>,
  ));
}

}


/// Adds pattern-matching-related methods to [GpxPreviewFromPointsRequest].
extension GpxPreviewFromPointsRequestPatterns on GpxPreviewFromPointsRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GpxPreviewFromPointsRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GpxPreviewFromPointsRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GpxPreviewFromPointsRequest value)  $default,){
final _that = this;
switch (_that) {
case _GpxPreviewFromPointsRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GpxPreviewFromPointsRequest value)?  $default,){
final _that = this;
switch (_that) {
case _GpxPreviewFromPointsRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  List<GeoPoint> points)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GpxPreviewFromPointsRequest() when $default != null:
return $default(_that.name,_that.points);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  List<GeoPoint> points)  $default,) {final _that = this;
switch (_that) {
case _GpxPreviewFromPointsRequest():
return $default(_that.name,_that.points);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  List<GeoPoint> points)?  $default,) {final _that = this;
switch (_that) {
case _GpxPreviewFromPointsRequest() when $default != null:
return $default(_that.name,_that.points);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GpxPreviewFromPointsRequest implements GpxPreviewFromPointsRequest {
  const _GpxPreviewFromPointsRequest({required this.name, required  List<GeoPoint> points}): _points = points;
  factory _GpxPreviewFromPointsRequest.fromJson(Map<String, dynamic> json) => _$GpxPreviewFromPointsRequestFromJson(json);

/// Preview name
@override final  String name;
/// Points from frontend routing
 final  List<GeoPoint> _points;
/// Points from frontend routing
@override List<GeoPoint> get points {
  if (_points is EqualUnmodifiableListView) return _points;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_points);
}


/// Create a copy of GpxPreviewFromPointsRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GpxPreviewFromPointsRequestCopyWith<_GpxPreviewFromPointsRequest> get copyWith => __$GpxPreviewFromPointsRequestCopyWithImpl<_GpxPreviewFromPointsRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GpxPreviewFromPointsRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GpxPreviewFromPointsRequest&&(identical(other.name, name) || other.name == name)&&const DeepCollectionEquality().equals(other._points, _points));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,const DeepCollectionEquality().hash(_points));

@override
String toString() {
  return 'GpxPreviewFromPointsRequest(name: $name, points: $points)';
}


}

/// @nodoc
abstract mixin class _$GpxPreviewFromPointsRequestCopyWith<$Res> implements $GpxPreviewFromPointsRequestCopyWith<$Res> {
  factory _$GpxPreviewFromPointsRequestCopyWith(_GpxPreviewFromPointsRequest value, $Res Function(_GpxPreviewFromPointsRequest) _then) = __$GpxPreviewFromPointsRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, List<GeoPoint> points
});




}
/// @nodoc
class __$GpxPreviewFromPointsRequestCopyWithImpl<$Res>
    implements _$GpxPreviewFromPointsRequestCopyWith<$Res> {
  __$GpxPreviewFromPointsRequestCopyWithImpl(this._self, this._then);

  final _GpxPreviewFromPointsRequest _self;
  final $Res Function(_GpxPreviewFromPointsRequest) _then;

/// Create a copy of GpxPreviewFromPointsRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? points = null,}) {
  return _then(_GpxPreviewFromPointsRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,points: null == points ? _self._points : points // ignore: cast_nullable_to_non_nullable
as List<GeoPoint>,
  ));
}


}

// dart format on
