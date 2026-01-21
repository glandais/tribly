// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideRequest {

/// Ride name
 String get name;/// Ride media
 MediaDto get media;/// Ride date/time
 String get dateTime;/// Ride status
 String get status;/// Visibility level
 String get visibility;/// Ride groups to create
 List<GroupRequest> get groups;/// Route slug
 String? get routeSlug;/// Start place ID (TSID)
 String? get startPlaceId;/// End place ID (TSID)
 String? get endPlaceId;/// Publication timestamp (for scheduled publishing)
 String? get publishAt;
/// Create a copy of RideRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideRequestCopyWith<RideRequest> get copyWith => _$RideRequestCopyWithImpl<RideRequest>(this as RideRequest, _$identity);

  /// Serializes this RideRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&const DeepCollectionEquality().equals(other.groups, groups)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.startPlaceId, startPlaceId) || other.startPlaceId == startPlaceId)&&(identical(other.endPlaceId, endPlaceId) || other.endPlaceId == endPlaceId)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,dateTime,status,visibility,const DeepCollectionEquality().hash(groups),routeSlug,startPlaceId,endPlaceId,publishAt);

@override
String toString() {
  return 'RideRequest(name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, groups: $groups, routeSlug: $routeSlug, startPlaceId: $startPlaceId, endPlaceId: $endPlaceId, publishAt: $publishAt)';
}


}

/// @nodoc
abstract mixin class $RideRequestCopyWith<$Res>  {
  factory $RideRequestCopyWith(RideRequest value, $Res Function(RideRequest) _then) = _$RideRequestCopyWithImpl;
@useResult
$Res call({
 String name, MediaDto media, String dateTime, String status, String visibility, List<GroupRequest> groups, String? routeSlug, String? startPlaceId, String? endPlaceId, String? publishAt
});


$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$RideRequestCopyWithImpl<$Res>
    implements $RideRequestCopyWith<$Res> {
  _$RideRequestCopyWithImpl(this._self, this._then);

  final RideRequest _self;
  final $Res Function(RideRequest) _then;

/// Create a copy of RideRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? groups = null,Object? routeSlug = freezed,Object? startPlaceId = freezed,Object? endPlaceId = freezed,Object? publishAt = freezed,}) {
  return _then(_self.copyWith(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,groups: null == groups ? _self.groups : groups // ignore: cast_nullable_to_non_nullable
as List<GroupRequest>,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,startPlaceId: freezed == startPlaceId ? _self.startPlaceId : startPlaceId // ignore: cast_nullable_to_non_nullable
as String?,endPlaceId: freezed == endPlaceId ? _self.endPlaceId : endPlaceId // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of RideRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [RideRequest].
extension RideRequestPatterns on RideRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideRequest value)  $default,){
final _that = this;
switch (_that) {
case _RideRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideRequest value)?  $default,){
final _that = this;
switch (_that) {
case _RideRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  List<GroupRequest> groups,  String? routeSlug,  String? startPlaceId,  String? endPlaceId,  String? publishAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideRequest() when $default != null:
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.groups,_that.routeSlug,_that.startPlaceId,_that.endPlaceId,_that.publishAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  List<GroupRequest> groups,  String? routeSlug,  String? startPlaceId,  String? endPlaceId,  String? publishAt)  $default,) {final _that = this;
switch (_that) {
case _RideRequest():
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.groups,_that.routeSlug,_that.startPlaceId,_that.endPlaceId,_that.publishAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  List<GroupRequest> groups,  String? routeSlug,  String? startPlaceId,  String? endPlaceId,  String? publishAt)?  $default,) {final _that = this;
switch (_that) {
case _RideRequest() when $default != null:
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.groups,_that.routeSlug,_that.startPlaceId,_that.endPlaceId,_that.publishAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideRequest implements RideRequest {
  const _RideRequest({required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, required final  List<GroupRequest> groups, this.routeSlug, this.startPlaceId, this.endPlaceId, this.publishAt}): _groups = groups;
  factory _RideRequest.fromJson(Map<String, dynamic> json) => _$RideRequestFromJson(json);

/// Ride name
@override final  String name;
/// Ride media
@override final  MediaDto media;
/// Ride date/time
@override final  String dateTime;
/// Ride status
@override final  String status;
/// Visibility level
@override final  String visibility;
/// Ride groups to create
 final  List<GroupRequest> _groups;
/// Ride groups to create
@override List<GroupRequest> get groups {
  if (_groups is EqualUnmodifiableListView) return _groups;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_groups);
}

/// Route slug
@override final  String? routeSlug;
/// Start place ID (TSID)
@override final  String? startPlaceId;
/// End place ID (TSID)
@override final  String? endPlaceId;
/// Publication timestamp (for scheduled publishing)
@override final  String? publishAt;

/// Create a copy of RideRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideRequestCopyWith<_RideRequest> get copyWith => __$RideRequestCopyWithImpl<_RideRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&const DeepCollectionEquality().equals(other._groups, _groups)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.startPlaceId, startPlaceId) || other.startPlaceId == startPlaceId)&&(identical(other.endPlaceId, endPlaceId) || other.endPlaceId == endPlaceId)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,dateTime,status,visibility,const DeepCollectionEquality().hash(_groups),routeSlug,startPlaceId,endPlaceId,publishAt);

@override
String toString() {
  return 'RideRequest(name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, groups: $groups, routeSlug: $routeSlug, startPlaceId: $startPlaceId, endPlaceId: $endPlaceId, publishAt: $publishAt)';
}


}

/// @nodoc
abstract mixin class _$RideRequestCopyWith<$Res> implements $RideRequestCopyWith<$Res> {
  factory _$RideRequestCopyWith(_RideRequest value, $Res Function(_RideRequest) _then) = __$RideRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, MediaDto media, String dateTime, String status, String visibility, List<GroupRequest> groups, String? routeSlug, String? startPlaceId, String? endPlaceId, String? publishAt
});


@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class __$RideRequestCopyWithImpl<$Res>
    implements _$RideRequestCopyWith<$Res> {
  __$RideRequestCopyWithImpl(this._self, this._then);

  final _RideRequest _self;
  final $Res Function(_RideRequest) _then;

/// Create a copy of RideRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? groups = null,Object? routeSlug = freezed,Object? startPlaceId = freezed,Object? endPlaceId = freezed,Object? publishAt = freezed,}) {
  return _then(_RideRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,groups: null == groups ? _self._groups : groups // ignore: cast_nullable_to_non_nullable
as List<GroupRequest>,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,startPlaceId: freezed == startPlaceId ? _self.startPlaceId : startPlaceId // ignore: cast_nullable_to_non_nullable
as String?,endPlaceId: freezed == endPlaceId ? _self.endPlaceId : endPlaceId // ignore: cast_nullable_to_non_nullable
as String?,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of RideRequest
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
