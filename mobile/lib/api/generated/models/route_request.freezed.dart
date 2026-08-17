// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'route_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouteRequest {

/// Route name
 String get name;/// Media
 MediaDto get media;/// Surface type
 String get surfaceType;/// Whether the route is publicly visible
 String get visibility;/// Points from frontend routing
 List<GeoPoint>? get points;
/// Create a copy of RouteRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouteRequestCopyWith<RouteRequest> get copyWith => _$RouteRequestCopyWithImpl<RouteRequest>(this as RouteRequest, _$identity);

  /// Serializes this RouteRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouteRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.surfaceType, surfaceType) || other.surfaceType == surfaceType)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&const DeepCollectionEquality().equals(other.points, points));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,surfaceType,visibility,const DeepCollectionEquality().hash(points));

@override
String toString() {
  return 'RouteRequest(name: $name, media: $media, surfaceType: $surfaceType, visibility: $visibility, points: $points)';
}


}

/// @nodoc
abstract mixin class $RouteRequestCopyWith<$Res>  {
  factory $RouteRequestCopyWith(RouteRequest value, $Res Function(RouteRequest) _then) = _$RouteRequestCopyWithImpl;
@useResult
$Res call({
 String name, MediaDto media, String surfaceType, String visibility, List<GeoPoint>? points
});


$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$RouteRequestCopyWithImpl<$Res>
    implements $RouteRequestCopyWith<$Res> {
  _$RouteRequestCopyWithImpl(this._self, this._then);

  final RouteRequest _self;
  final $Res Function(RouteRequest) _then;

/// Create a copy of RouteRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? media = null,Object? surfaceType = null,Object? visibility = null,Object? points = freezed,}) {
  return _then(RouteRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,surfaceType: null == surfaceType ? _self.surfaceType : surfaceType // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,points: freezed == points ? _self.points : points // ignore: cast_nullable_to_non_nullable
as List<GeoPoint>?,
  ));
}
/// Create a copy of RouteRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [RouteRequest].
extension RouteRequestPatterns on RouteRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouteRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouteRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouteRequest value)  $default,){
final _that = this;
switch (_that) {
case _RouteRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouteRequest value)?  $default,){
final _that = this;
switch (_that) {
case _RouteRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String surfaceType,  String visibility,  List<GeoPoint>? points)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouteRequest() when $default != null:
return $default(_that.name,_that.media,_that.surfaceType,_that.visibility,_that.points);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String surfaceType,  String visibility,  List<GeoPoint>? points)  $default,) {final _that = this;
switch (_that) {
case _RouteRequest():
return $default(_that.name,_that.media,_that.surfaceType,_that.visibility,_that.points);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  MediaDto media,  String surfaceType,  String visibility,  List<GeoPoint>? points)?  $default,) {final _that = this;
switch (_that) {
case _RouteRequest() when $default != null:
return $default(_that.name,_that.media,_that.surfaceType,_that.visibility,_that.points);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouteRequest implements RouteRequest {
  const _RouteRequest({required this.name, required this.media, required this.surfaceType, required this.visibility,  List<GeoPoint>? points}): _points = points;
  factory _RouteRequest.fromJson(Map<String, dynamic> json) => _$RouteRequestFromJson(json);

/// Route name
@override final  String name;
/// Media
@override final  MediaDto media;
/// Surface type
@override final  String surfaceType;
/// Whether the route is publicly visible
@override final  String visibility;
/// Points from frontend routing
 final  List<GeoPoint>? _points;
/// Points from frontend routing
@override List<GeoPoint>? get points {
  final value = _points;
  if (value == null) return null;
  if (_points is EqualUnmodifiableListView) return _points;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}


/// Create a copy of RouteRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouteRequestCopyWith<_RouteRequest> get copyWith => __$RouteRequestCopyWithImpl<_RouteRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouteRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouteRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.surfaceType, surfaceType) || other.surfaceType == surfaceType)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&const DeepCollectionEquality().equals(other._points, _points));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,surfaceType,visibility,const DeepCollectionEquality().hash(_points));

@override
String toString() {
  return 'RouteRequest(name: $name, media: $media, surfaceType: $surfaceType, visibility: $visibility, points: $points)';
}


}

/// @nodoc
abstract mixin class _$RouteRequestCopyWith<$Res> implements $RouteRequestCopyWith<$Res> {
  factory _$RouteRequestCopyWith(_RouteRequest value, $Res Function(_RouteRequest) _then) = __$RouteRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, MediaDto media, String surfaceType, String visibility, List<GeoPoint>? points
});


@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class __$RouteRequestCopyWithImpl<$Res>
    implements _$RouteRequestCopyWith<$Res> {
  __$RouteRequestCopyWithImpl(this._self, this._then);

  final _RouteRequest _self;
  final $Res Function(_RouteRequest) _then;

/// Create a copy of RouteRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? media = null,Object? surfaceType = null,Object? visibility = null,Object? points = freezed,}) {
  return _then(_RouteRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,surfaceType: null == surfaceType ? _self.surfaceType : surfaceType // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,points: freezed == points ? _self._points : points // ignore: cast_nullable_to_non_nullable
as List<GeoPoint>?,
  ));
}

/// Create a copy of RouteRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}

// dart format on
