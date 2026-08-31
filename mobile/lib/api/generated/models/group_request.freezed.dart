// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'group_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GroupRequest {

/// Group name
 String get name;/// id
 String? get id; LocalTime? get time;/// Average speed in km/h
 double? get averageSpeed;/// Maximum participants
 int? get maxParticipants;/// Route slug for this group
 String? get routeSlug;/// ID (TSID) of the member who leads this group. Must belong to the team owning the ride. Omit or send null for no designated leader — clients then show no leader at all rather than falling back on the ride's creator.
 String? get leaderId;
/// Create a copy of GroupRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GroupRequestCopyWith<GroupRequest> get copyWith => _$GroupRequestCopyWithImpl<GroupRequest>(this as GroupRequest, _$identity);

  /// Serializes this GroupRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as GroupRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GroupRequest&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.time, _this.time) || other.time == _this.time)&&(identical(other.averageSpeed, _this.averageSpeed) || other.averageSpeed == _this.averageSpeed)&&(identical(other.maxParticipants, _this.maxParticipants) || other.maxParticipants == _this.maxParticipants)&&(identical(other.routeSlug, _this.routeSlug) || other.routeSlug == _this.routeSlug)&&(identical(other.leaderId, _this.leaderId) || other.leaderId == _this.leaderId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as GroupRequest;
  return Object.hash(runtimeType,_this.name,_this.id,_this.time,_this.averageSpeed,_this.maxParticipants,_this.routeSlug,_this.leaderId);
}

@override
String toString() {
  final _this = this as GroupRequest;
  return 'GroupRequest(name: ${_this.name}, id: ${_this.id}, time: ${_this.time}, averageSpeed: ${_this.averageSpeed}, maxParticipants: ${_this.maxParticipants}, routeSlug: ${_this.routeSlug}, leaderId: ${_this.leaderId})';
}


}

/// @nodoc
abstract mixin class $GroupRequestCopyWith<$Res>  {
  factory $GroupRequestCopyWith(GroupRequest value, $Res Function(GroupRequest) _then) = _$GroupRequestCopyWithImpl;
@useResult
$Res call({
 String name, String? id, LocalTime? time, double? averageSpeed, int? maxParticipants, String? routeSlug, String? leaderId
});




}
/// @nodoc
class _$GroupRequestCopyWithImpl<$Res>
    implements $GroupRequestCopyWith<$Res> {
  _$GroupRequestCopyWithImpl(this._self, this._then);

  final GroupRequest _self;
  final $Res Function(GroupRequest) _then;

/// Create a copy of GroupRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? id = freezed,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? routeSlug = freezed,Object? leaderId = freezed,}) {
  return _then(GroupRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,leaderId: freezed == leaderId ? _self.leaderId : leaderId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [GroupRequest].
extension GroupRequestPatterns on GroupRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GroupRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GroupRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GroupRequest value)  $default,){
final _that = this;
switch (_that) {
case _GroupRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GroupRequest value)?  $default,){
final _that = this;
switch (_that) {
case _GroupRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug,  String? leaderId)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GroupRequest() when $default != null:
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug,_that.leaderId);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug,  String? leaderId)  $default,) {final _that = this;
switch (_that) {
case _GroupRequest():
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug,_that.leaderId);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug,  String? leaderId)?  $default,) {final _that = this;
switch (_that) {
case _GroupRequest() when $default != null:
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug,_that.leaderId);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GroupRequest implements GroupRequest {
  const _GroupRequest({required this.name, this.id, this.time, this.averageSpeed, this.maxParticipants, this.routeSlug, this.leaderId});
  factory _GroupRequest.fromJson(Map<String, dynamic> json) => _$GroupRequestFromJson(json);

/// Group name
@override final  String name;
/// id
@override final  String? id;
@override final  LocalTime? time;
/// Average speed in km/h
@override final  double? averageSpeed;
/// Maximum participants
@override final  int? maxParticipants;
/// Route slug for this group
@override final  String? routeSlug;
/// ID (TSID) of the member who leads this group. Must belong to the team owning the ride. Omit or send null for no designated leader — clients then show no leader at all rather than falling back on the ride's creator.
@override final  String? leaderId;

/// Create a copy of GroupRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GroupRequestCopyWith<_GroupRequest> get copyWith => __$GroupRequestCopyWithImpl<_GroupRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GroupRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _GroupRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.id, id) || other.id == id)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug)&&(identical(other.leaderId, leaderId) || other.leaderId == leaderId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,name,id,time,averageSpeed,maxParticipants,routeSlug,leaderId);
}

@override
String toString() {
    return 'GroupRequest(name: $name, id: $id, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants, routeSlug: $routeSlug, leaderId: $leaderId)';
}


}

/// @nodoc
abstract mixin class _$GroupRequestCopyWith<$Res> implements $GroupRequestCopyWith<$Res> {
  factory _$GroupRequestCopyWith(_GroupRequest value, $Res Function(_GroupRequest) _then) = __$GroupRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, String? id, LocalTime? time, double? averageSpeed, int? maxParticipants, String? routeSlug, String? leaderId
});




}
/// @nodoc
class __$GroupRequestCopyWithImpl<$Res>
    implements _$GroupRequestCopyWith<$Res> {
  __$GroupRequestCopyWithImpl(this._self, this._then);

  final _GroupRequest _self;
  final $Res Function(_GroupRequest) _then;

/// Create a copy of GroupRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? id = freezed,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? routeSlug = freezed,Object? leaderId = freezed,}) {
  return _then(_GroupRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,leaderId: freezed == leaderId ? _self.leaderId : leaderId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
