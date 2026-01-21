// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'trip_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TripRequest {

/// Trip name
 String get name;/// Trip media
 MediaDto get media;/// Trip start date/time
 String get dateTime;/// Trip status
 String get status;/// Visibility level
 String get visibility;/// Trip stages to create
 List<StageRequest> get stages;/// Overall route slug for the trip
 String? get routeSlug;/// Publication timestamp (for scheduled publishing)
 String? get publishAt;
/// Create a copy of TripRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TripRequestCopyWith<TripRequest> get copyWith => _$TripRequestCopyWithImpl<TripRequest>(this as TripRequest, _$identity);

  /// Serializes this TripRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TripRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&const DeepCollectionEquality().equals(other.stages, stages)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,dateTime,status,visibility,const DeepCollectionEquality().hash(stages),routeSlug,publishAt);

@override
String toString() {
  return 'TripRequest(name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, stages: $stages, routeSlug: $routeSlug, publishAt: $publishAt)';
}


}

/// @nodoc
abstract mixin class $TripRequestCopyWith<$Res>  {
  factory $TripRequestCopyWith(TripRequest value, $Res Function(TripRequest) _then) = _$TripRequestCopyWithImpl;
@useResult
$Res call({
 String name, MediaDto media, String dateTime, String status, String visibility, List<StageRequest> stages, String? routeSlug, String? publishAt
});


$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$TripRequestCopyWithImpl<$Res>
    implements $TripRequestCopyWith<$Res> {
  _$TripRequestCopyWithImpl(this._self, this._then);

  final TripRequest _self;
  final $Res Function(TripRequest) _then;

/// Create a copy of TripRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? stages = null,Object? routeSlug = freezed,Object? publishAt = freezed,}) {
  return _then(_self.copyWith(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,stages: null == stages ? _self.stages : stages // ignore: cast_nullable_to_non_nullable
as List<StageRequest>,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of TripRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [TripRequest].
extension TripRequestPatterns on TripRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TripRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TripRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TripRequest value)  $default,){
final _that = this;
switch (_that) {
case _TripRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TripRequest value)?  $default,){
final _that = this;
switch (_that) {
case _TripRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  List<StageRequest> stages,  String? routeSlug,  String? publishAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TripRequest() when $default != null:
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.stages,_that.routeSlug,_that.publishAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  List<StageRequest> stages,  String? routeSlug,  String? publishAt)  $default,) {final _that = this;
switch (_that) {
case _TripRequest():
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.stages,_that.routeSlug,_that.publishAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  List<StageRequest> stages,  String? routeSlug,  String? publishAt)?  $default,) {final _that = this;
switch (_that) {
case _TripRequest() when $default != null:
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.stages,_that.routeSlug,_that.publishAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TripRequest implements TripRequest {
  const _TripRequest({required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, required final  List<StageRequest> stages, this.routeSlug, this.publishAt}): _stages = stages;
  factory _TripRequest.fromJson(Map<String, dynamic> json) => _$TripRequestFromJson(json);

/// Trip name
@override final  String name;
/// Trip media
@override final  MediaDto media;
/// Trip start date/time
@override final  String dateTime;
/// Trip status
@override final  String status;
/// Visibility level
@override final  String visibility;
/// Trip stages to create
 final  List<StageRequest> _stages;
/// Trip stages to create
@override List<StageRequest> get stages {
  if (_stages is EqualUnmodifiableListView) return _stages;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_stages);
}

/// Overall route slug for the trip
@override final  String? routeSlug;
/// Publication timestamp (for scheduled publishing)
@override final  String? publishAt;

/// Create a copy of TripRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TripRequestCopyWith<_TripRequest> get copyWith => __$TripRequestCopyWithImpl<_TripRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TripRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TripRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&const DeepCollectionEquality().equals(other._stages, _stages)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,dateTime,status,visibility,const DeepCollectionEquality().hash(_stages),routeSlug,publishAt);

@override
String toString() {
  return 'TripRequest(name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, stages: $stages, routeSlug: $routeSlug, publishAt: $publishAt)';
}


}

/// @nodoc
abstract mixin class _$TripRequestCopyWith<$Res> implements $TripRequestCopyWith<$Res> {
  factory _$TripRequestCopyWith(_TripRequest value, $Res Function(_TripRequest) _then) = __$TripRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, MediaDto media, String dateTime, String status, String visibility, List<StageRequest> stages, String? routeSlug, String? publishAt
});


@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class __$TripRequestCopyWithImpl<$Res>
    implements _$TripRequestCopyWith<$Res> {
  __$TripRequestCopyWithImpl(this._self, this._then);

  final _TripRequest _self;
  final $Res Function(_TripRequest) _then;

/// Create a copy of TripRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? stages = null,Object? routeSlug = freezed,Object? publishAt = freezed,}) {
  return _then(_TripRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,stages: null == stages ? _self._stages : stages // ignore: cast_nullable_to_non_nullable
as List<StageRequest>,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of TripRequest
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
