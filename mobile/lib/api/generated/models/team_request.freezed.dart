// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamRequest {

/// Team name
 String get name;/// Media
 MediaDto get media;/// Team visibility
 String get visibility;/// Trips enabled for team
 bool get enableTrips;/// Ads enabled for team
 bool get enableAds;/// Team location coordinates [longitude, latitude]
 TeamRequestGeometry? get geometry;
/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamRequestCopyWith<TeamRequest> get copyWith => _$TeamRequestCopyWithImpl<TeamRequest>(this as TeamRequest, _$identity);

  /// Serializes this TeamRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.enableTrips, enableTrips) || other.enableTrips == enableTrips)&&(identical(other.enableAds, enableAds) || other.enableAds == enableAds)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,visibility,enableTrips,enableAds,geometry);

@override
String toString() {
  return 'TeamRequest(name: $name, media: $media, visibility: $visibility, enableTrips: $enableTrips, enableAds: $enableAds, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class $TeamRequestCopyWith<$Res>  {
  factory $TeamRequestCopyWith(TeamRequest value, $Res Function(TeamRequest) _then) = _$TeamRequestCopyWithImpl;
@useResult
$Res call({
 String name, MediaDto media, String visibility, bool enableTrips, bool enableAds, TeamRequestGeometry? geometry
});


$MediaDtoCopyWith<$Res> get media;$TeamRequestGeometryCopyWith<$Res>? get geometry;

}
/// @nodoc
class _$TeamRequestCopyWithImpl<$Res>
    implements $TeamRequestCopyWith<$Res> {
  _$TeamRequestCopyWithImpl(this._self, this._then);

  final TeamRequest _self;
  final $Res Function(TeamRequest) _then;

/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? media = null,Object? visibility = null,Object? enableTrips = null,Object? enableAds = null,Object? geometry = freezed,}) {
  return _then(_self.copyWith(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,enableTrips: null == enableTrips ? _self.enableTrips : enableTrips // ignore: cast_nullable_to_non_nullable
as bool,enableAds: null == enableAds ? _self.enableAds : enableAds // ignore: cast_nullable_to_non_nullable
as bool,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as TeamRequestGeometry?,
  ));
}
/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamRequestGeometryCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $TeamRequestGeometryCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}


/// Adds pattern-matching-related methods to [TeamRequest].
extension TeamRequestPatterns on TeamRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamRequest value)  $default,){
final _that = this;
switch (_that) {
case _TeamRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamRequest value)?  $default,){
final _that = this;
switch (_that) {
case _TeamRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String visibility,  bool enableTrips,  bool enableAds,  TeamRequestGeometry? geometry)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamRequest() when $default != null:
return $default(_that.name,_that.media,_that.visibility,_that.enableTrips,_that.enableAds,_that.geometry);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String visibility,  bool enableTrips,  bool enableAds,  TeamRequestGeometry? geometry)  $default,) {final _that = this;
switch (_that) {
case _TeamRequest():
return $default(_that.name,_that.media,_that.visibility,_that.enableTrips,_that.enableAds,_that.geometry);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  MediaDto media,  String visibility,  bool enableTrips,  bool enableAds,  TeamRequestGeometry? geometry)?  $default,) {final _that = this;
switch (_that) {
case _TeamRequest() when $default != null:
return $default(_that.name,_that.media,_that.visibility,_that.enableTrips,_that.enableAds,_that.geometry);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamRequest implements TeamRequest {
  const _TeamRequest({required this.name, required this.media, required this.visibility, required this.enableTrips, required this.enableAds, this.geometry});
  factory _TeamRequest.fromJson(Map<String, dynamic> json) => _$TeamRequestFromJson(json);

/// Team name
@override final  String name;
/// Media
@override final  MediaDto media;
/// Team visibility
@override final  String visibility;
/// Trips enabled for team
@override final  bool enableTrips;
/// Ads enabled for team
@override final  bool enableAds;
/// Team location coordinates [longitude, latitude]
@override final  TeamRequestGeometry? geometry;

/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamRequestCopyWith<_TeamRequest> get copyWith => __$TeamRequestCopyWithImpl<_TeamRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.enableTrips, enableTrips) || other.enableTrips == enableTrips)&&(identical(other.enableAds, enableAds) || other.enableAds == enableAds)&&(identical(other.geometry, geometry) || other.geometry == geometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,visibility,enableTrips,enableAds,geometry);

@override
String toString() {
  return 'TeamRequest(name: $name, media: $media, visibility: $visibility, enableTrips: $enableTrips, enableAds: $enableAds, geometry: $geometry)';
}


}

/// @nodoc
abstract mixin class _$TeamRequestCopyWith<$Res> implements $TeamRequestCopyWith<$Res> {
  factory _$TeamRequestCopyWith(_TeamRequest value, $Res Function(_TeamRequest) _then) = __$TeamRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, MediaDto media, String visibility, bool enableTrips, bool enableAds, TeamRequestGeometry? geometry
});


@override $MediaDtoCopyWith<$Res> get media;@override $TeamRequestGeometryCopyWith<$Res>? get geometry;

}
/// @nodoc
class __$TeamRequestCopyWithImpl<$Res>
    implements _$TeamRequestCopyWith<$Res> {
  __$TeamRequestCopyWithImpl(this._self, this._then);

  final _TeamRequest _self;
  final $Res Function(_TeamRequest) _then;

/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? media = null,Object? visibility = null,Object? enableTrips = null,Object? enableAds = null,Object? geometry = freezed,}) {
  return _then(_TeamRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,enableTrips: null == enableTrips ? _self.enableTrips : enableTrips // ignore: cast_nullable_to_non_nullable
as bool,enableAds: null == enableAds ? _self.enableAds : enableAds // ignore: cast_nullable_to_non_nullable
as bool,geometry: freezed == geometry ? _self.geometry : geometry // ignore: cast_nullable_to_non_nullable
as TeamRequestGeometry?,
  ));
}

/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of TeamRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamRequestGeometryCopyWith<$Res>? get geometry {
    if (_self.geometry == null) {
    return null;
  }

  return $TeamRequestGeometryCopyWith<$Res>(_self.geometry!, (value) {
    return _then(_self.copyWith(geometry: value));
  });
}
}

// dart format on
