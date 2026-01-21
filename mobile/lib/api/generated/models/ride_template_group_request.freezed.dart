// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_template_group_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideTemplateGroupRequest {

/// Group name
 String get name;/// Group ID (TSID) - only for updates
 String? get id; LocalTime? get time;/// Average speed in km/h
 double? get averageSpeed;/// Maximum participants
 int? get maxParticipants;
/// Create a copy of RideTemplateGroupRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideTemplateGroupRequestCopyWith<RideTemplateGroupRequest> get copyWith => _$RideTemplateGroupRequestCopyWithImpl<RideTemplateGroupRequest>(this as RideTemplateGroupRequest, _$identity);

  /// Serializes this RideTemplateGroupRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideTemplateGroupRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.id, id) || other.id == id)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,id,time,averageSpeed,maxParticipants);

@override
String toString() {
  return 'RideTemplateGroupRequest(name: $name, id: $id, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants)';
}


}

/// @nodoc
abstract mixin class $RideTemplateGroupRequestCopyWith<$Res>  {
  factory $RideTemplateGroupRequestCopyWith(RideTemplateGroupRequest value, $Res Function(RideTemplateGroupRequest) _then) = _$RideTemplateGroupRequestCopyWithImpl;
@useResult
$Res call({
 String name, String? id, LocalTime? time, double? averageSpeed, int? maxParticipants
});




}
/// @nodoc
class _$RideTemplateGroupRequestCopyWithImpl<$Res>
    implements $RideTemplateGroupRequestCopyWith<$Res> {
  _$RideTemplateGroupRequestCopyWithImpl(this._self, this._then);

  final RideTemplateGroupRequest _self;
  final $Res Function(RideTemplateGroupRequest) _then;

/// Create a copy of RideTemplateGroupRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? id = freezed,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,}) {
  return _then(_self.copyWith(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

}


/// Adds pattern-matching-related methods to [RideTemplateGroupRequest].
extension RideTemplateGroupRequestPatterns on RideTemplateGroupRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideTemplateGroupRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideTemplateGroupRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideTemplateGroupRequest value)  $default,){
final _that = this;
switch (_that) {
case _RideTemplateGroupRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideTemplateGroupRequest value)?  $default,){
final _that = this;
switch (_that) {
case _RideTemplateGroupRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideTemplateGroupRequest() when $default != null:
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants)  $default,) {final _that = this;
switch (_that) {
case _RideTemplateGroupRequest():
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  String? id,  LocalTime? time,  double? averageSpeed,  int? maxParticipants)?  $default,) {final _that = this;
switch (_that) {
case _RideTemplateGroupRequest() when $default != null:
return $default(_that.name,_that.id,_that.time,_that.averageSpeed,_that.maxParticipants);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideTemplateGroupRequest implements RideTemplateGroupRequest {
  const _RideTemplateGroupRequest({required this.name, this.id, this.time, this.averageSpeed, this.maxParticipants});
  factory _RideTemplateGroupRequest.fromJson(Map<String, dynamic> json) => _$RideTemplateGroupRequestFromJson(json);

/// Group name
@override final  String name;
/// Group ID (TSID) - only for updates
@override final  String? id;
@override final  LocalTime? time;
/// Average speed in km/h
@override final  double? averageSpeed;
/// Maximum participants
@override final  int? maxParticipants;

/// Create a copy of RideTemplateGroupRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideTemplateGroupRequestCopyWith<_RideTemplateGroupRequest> get copyWith => __$RideTemplateGroupRequestCopyWithImpl<_RideTemplateGroupRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideTemplateGroupRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideTemplateGroupRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.id, id) || other.id == id)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,id,time,averageSpeed,maxParticipants);

@override
String toString() {
  return 'RideTemplateGroupRequest(name: $name, id: $id, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants)';
}


}

/// @nodoc
abstract mixin class _$RideTemplateGroupRequestCopyWith<$Res> implements $RideTemplateGroupRequestCopyWith<$Res> {
  factory _$RideTemplateGroupRequestCopyWith(_RideTemplateGroupRequest value, $Res Function(_RideTemplateGroupRequest) _then) = __$RideTemplateGroupRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, String? id, LocalTime? time, double? averageSpeed, int? maxParticipants
});




}
/// @nodoc
class __$RideTemplateGroupRequestCopyWithImpl<$Res>
    implements _$RideTemplateGroupRequestCopyWith<$Res> {
  __$RideTemplateGroupRequestCopyWithImpl(this._self, this._then);

  final _RideTemplateGroupRequest _self;
  final $Res Function(_RideTemplateGroupRequest) _then;

/// Create a copy of RideTemplateGroupRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? id = freezed,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,}) {
  return _then(_RideTemplateGroupRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}


}

// dart format on
