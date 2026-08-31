// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'stage_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$StageRequest {

/// Stage name
 String get name;/// Stage date/time
 String get dateTime;/// Stage media
 MediaDto get media;/// Stage ID (for updates)
 String? get id;/// Route slug for this stage
 String? get routeSlug;/// Start place ID (TSID)
 String? get startPlaceId;/// End place ID (TSID)
 String? get endPlaceId;
/// Create a copy of StageRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$StageRequestCopyWith<StageRequest> get copyWith => _$StageRequestCopyWithImpl<StageRequest>(this as StageRequest, _$identity);

  /// Serializes this StageRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as StageRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is StageRequest&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.dateTime, _this.dateTime) || other.dateTime == _this.dateTime)&&(identical(other.media, _this.media) || other.media == _this.media)&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.routeSlug, _this.routeSlug) || other.routeSlug == _this.routeSlug)&&(identical(other.startPlaceId, _this.startPlaceId) || other.startPlaceId == _this.startPlaceId)&&(identical(other.endPlaceId, _this.endPlaceId) || other.endPlaceId == _this.endPlaceId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as StageRequest;
  return Object.hash(runtimeType,_this.name,_this.dateTime,_this.media,_this.id,_this.routeSlug,_this.startPlaceId,_this.endPlaceId);
}

@override
String toString() {
  final _this = this as StageRequest;
  return 'StageRequest(name: ${_this.name}, dateTime: ${_this.dateTime}, media: ${_this.media}, id: ${_this.id}, routeSlug: ${_this.routeSlug}, startPlaceId: ${_this.startPlaceId}, endPlaceId: ${_this.endPlaceId})';
}


}

/// @nodoc
abstract mixin class $StageRequestCopyWith<$Res>  {
  factory $StageRequestCopyWith(StageRequest value, $Res Function(StageRequest) _then) = _$StageRequestCopyWithImpl;
@useResult
$Res call({
 String name, String dateTime, MediaDto media, String? id, String? routeSlug, String? startPlaceId, String? endPlaceId
});


$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$StageRequestCopyWithImpl<$Res>
    implements $StageRequestCopyWith<$Res> {
  _$StageRequestCopyWithImpl(this._self, this._then);

  final StageRequest _self;
  final $Res Function(StageRequest) _then;

/// Create a copy of StageRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? dateTime = null,Object? media = null,Object? id = freezed,Object? routeSlug = freezed,Object? startPlaceId = freezed,Object? endPlaceId = freezed,}) {
  return _then(StageRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,startPlaceId: freezed == startPlaceId ? _self.startPlaceId : startPlaceId // ignore: cast_nullable_to_non_nullable
as String?,endPlaceId: freezed == endPlaceId ? _self.endPlaceId : endPlaceId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of StageRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [StageRequest].
extension StageRequestPatterns on StageRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _StageRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _StageRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _StageRequest value)  $default,){
final _that = this;
switch (_that) {
case _StageRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _StageRequest value)?  $default,){
final _that = this;
switch (_that) {
case _StageRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  String dateTime,  MediaDto media,  String? id,  String? routeSlug,  String? startPlaceId,  String? endPlaceId)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _StageRequest() when $default != null:
return $default(_that.name,_that.dateTime,_that.media,_that.id,_that.routeSlug,_that.startPlaceId,_that.endPlaceId);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  String dateTime,  MediaDto media,  String? id,  String? routeSlug,  String? startPlaceId,  String? endPlaceId)  $default,) {final _that = this;
switch (_that) {
case _StageRequest():
return $default(_that.name,_that.dateTime,_that.media,_that.id,_that.routeSlug,_that.startPlaceId,_that.endPlaceId);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  String dateTime,  MediaDto media,  String? id,  String? routeSlug,  String? startPlaceId,  String? endPlaceId)?  $default,) {final _that = this;
switch (_that) {
case _StageRequest() when $default != null:
return $default(_that.name,_that.dateTime,_that.media,_that.id,_that.routeSlug,_that.startPlaceId,_that.endPlaceId);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _StageRequest implements StageRequest {
  const _StageRequest({required this.name, required this.dateTime, required this.media, this.id, this.routeSlug, this.startPlaceId, this.endPlaceId});
  factory _StageRequest.fromJson(Map<String, dynamic> json) => _$StageRequestFromJson(json);

/// Stage name
@override final  String name;
/// Stage date/time
@override final  String dateTime;
/// Stage media
@override final  MediaDto media;
/// Stage ID (for updates)
@override final  String? id;
/// Route slug for this stage
@override final  String? routeSlug;
/// Start place ID (TSID)
@override final  String? startPlaceId;
/// End place ID (TSID)
@override final  String? endPlaceId;

/// Create a copy of StageRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$StageRequestCopyWith<_StageRequest> get copyWith => __$StageRequestCopyWithImpl<_StageRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$StageRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _StageRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.media, media) || other.media == media)&&(identical(other.id, id) || other.id == id)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.startPlaceId, startPlaceId) || other.startPlaceId == startPlaceId)&&(identical(other.endPlaceId, endPlaceId) || other.endPlaceId == endPlaceId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,name,dateTime,media,id,routeSlug,startPlaceId,endPlaceId);
}

@override
String toString() {
    return 'StageRequest(name: $name, dateTime: $dateTime, media: $media, id: $id, routeSlug: $routeSlug, startPlaceId: $startPlaceId, endPlaceId: $endPlaceId)';
}


}

/// @nodoc
abstract mixin class _$StageRequestCopyWith<$Res> implements $StageRequestCopyWith<$Res> {
  factory _$StageRequestCopyWith(_StageRequest value, $Res Function(_StageRequest) _then) = __$StageRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, String dateTime, MediaDto media, String? id, String? routeSlug, String? startPlaceId, String? endPlaceId
});


@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class __$StageRequestCopyWithImpl<$Res>
    implements _$StageRequestCopyWith<$Res> {
  __$StageRequestCopyWithImpl(this._self, this._then);

  final _StageRequest _self;
  final $Res Function(_StageRequest) _then;

/// Create a copy of StageRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? dateTime = null,Object? media = null,Object? id = freezed,Object? routeSlug = freezed,Object? startPlaceId = freezed,Object? endPlaceId = freezed,}) {
  return _then(_StageRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,startPlaceId: freezed == startPlaceId ? _self.startPlaceId : startPlaceId // ignore: cast_nullable_to_non_nullable
as String?,endPlaceId: freezed == endPlaceId ? _self.endPlaceId : endPlaceId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of StageRequest
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
