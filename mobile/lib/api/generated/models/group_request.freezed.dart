// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'group_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GroupRequest {

/// Group name
 String get name;/// id
 String? get id; LocalTime? get time;/// Average speed in km/h
 double? get averageSpeed;/// Maximum participants
 int? get maxParticipants;/// Route slug for this group
 String? get routeSlug;
/// Create a copy of GroupRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GroupRequestCopyWith<GroupRequest> get copyWith => _$GroupRequestCopyWithImpl<GroupRequest>(this as GroupRequest, _$identity);

  /// Serializes this GroupRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GroupRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.id, id) || other.id == id)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,id,time,averageSpeed,maxParticipants,routeSlug);

@override
String toString() {
  return 'GroupRequest(name: $name, id: $id, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants, routeSlug: $routeSlug)';
}


}

/// @nodoc
abstract mixin class $GroupRequestCopyWith<$Res>  {
  factory $GroupRequestCopyWith(GroupRequest value, $Res Function(GroupRequest) _then) = _$GroupRequestCopyWithImpl;
@useResult
$Res call({
 String name, String? id, LocalTime? time, double? averageSpeed, int? maxParticipants, String? routeSlug
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
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? id = freezed,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? routeSlug = freezed,}) {
  return _then(_self.copyWith(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GroupRequest() when $default != null:
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug)  $default,) {final _that = this;
switch (_that) {
case _GroupRequest():
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants,  String? routeSlug)?  $default,) {final _that = this;
switch (_that) {
case _GroupRequest() when $default != null:
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants,_that.routeSlug);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GroupRequest implements GroupRequest {
  const _GroupRequest({required this.name, this.id, this.time, this.averageSpeed, this.maxParticipants, this.routeSlug});
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
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GroupRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.id, id) || other.id == id)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants)&&(identical(other.routeSlug, routeSlug) || other.routeSlug == routeSlug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,id,time,averageSpeed,maxParticipants,routeSlug);

@override
String toString() {
  return 'GroupRequest(name: $name, id: $id, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants, routeSlug: $routeSlug)';
}


}

/// @nodoc
abstract mixin class _$GroupRequestCopyWith<$Res> implements $GroupRequestCopyWith<$Res> {
  factory _$GroupRequestCopyWith(_GroupRequest value, $Res Function(_GroupRequest) _then) = __$GroupRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, String? id, LocalTime? time, double? averageSpeed, int? maxParticipants, String? routeSlug
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
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? id = freezed,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,Object? routeSlug = freezed,}) {
  return _then(_GroupRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,routeSlug: freezed == routeSlug ? _self.routeSlug : routeSlug // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
