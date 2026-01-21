// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_template_group_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideTemplateGroupDto {

/// Group ID (TSID)
 String get id;/// Group name
 String get name;/// Sort order
 int get sortOrder; LocalTime? get time;/// Average speed in km/h
 double? get averageSpeed;/// Maximum participants
 int? get maxParticipants;
/// Create a copy of RideTemplateGroupDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideTemplateGroupDtoCopyWith<RideTemplateGroupDto> get copyWith => _$RideTemplateGroupDtoCopyWithImpl<RideTemplateGroupDto>(this as RideTemplateGroupDto, _$identity);

  /// Serializes this RideTemplateGroupDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideTemplateGroupDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.sortOrder, sortOrder) || other.sortOrder == sortOrder)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,sortOrder,time,averageSpeed,maxParticipants);

@override
String toString() {
  return 'RideTemplateGroupDto(id: $id, name: $name, sortOrder: $sortOrder, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants)';
}


}

/// @nodoc
abstract mixin class $RideTemplateGroupDtoCopyWith<$Res>  {
  factory $RideTemplateGroupDtoCopyWith(RideTemplateGroupDto value, $Res Function(RideTemplateGroupDto) _then) = _$RideTemplateGroupDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, int sortOrder, LocalTime? time, double? averageSpeed, int? maxParticipants
});




}
/// @nodoc
class _$RideTemplateGroupDtoCopyWithImpl<$Res>
    implements $RideTemplateGroupDtoCopyWith<$Res> {
  _$RideTemplateGroupDtoCopyWithImpl(this._self, this._then);

  final RideTemplateGroupDto _self;
  final $Res Function(RideTemplateGroupDto) _then;

/// Create a copy of RideTemplateGroupDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? sortOrder = null,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,sortOrder: null == sortOrder ? _self.sortOrder : sortOrder // ignore: cast_nullable_to_non_nullable
as int,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

}


/// Adds pattern-matching-related methods to [RideTemplateGroupDto].
extension RideTemplateGroupDtoPatterns on RideTemplateGroupDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideTemplateGroupDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideTemplateGroupDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideTemplateGroupDto value)  $default,){
final _that = this;
switch (_that) {
case _RideTemplateGroupDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideTemplateGroupDto value)?  $default,){
final _that = this;
switch (_that) {
case _RideTemplateGroupDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  int sortOrder,  LocalTime? time,  double? averageSpeed,  int? maxParticipants)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideTemplateGroupDto() when $default != null:
return $default(_that.id,_that.name,_that.sortOrder,_that.time,_that.averageSpeed,_that.maxParticipants);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  int sortOrder,  LocalTime? time,  double? averageSpeed,  int? maxParticipants)  $default,) {final _that = this;
switch (_that) {
case _RideTemplateGroupDto():
return $default(_that.id,_that.name,_that.sortOrder,_that.time,_that.averageSpeed,_that.maxParticipants);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  int sortOrder,  LocalTime? time,  double? averageSpeed,  int? maxParticipants)?  $default,) {final _that = this;
switch (_that) {
case _RideTemplateGroupDto() when $default != null:
return $default(_that.id,_that.name,_that.sortOrder,_that.time,_that.averageSpeed,_that.maxParticipants);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideTemplateGroupDto implements RideTemplateGroupDto {
  const _RideTemplateGroupDto({required this.id, required this.name, required this.sortOrder, this.time, this.averageSpeed, this.maxParticipants});
  factory _RideTemplateGroupDto.fromJson(Map<String, dynamic> json) => _$RideTemplateGroupDtoFromJson(json);

/// Group ID (TSID)
@override final  String id;
/// Group name
@override final  String name;
/// Sort order
@override final  int sortOrder;
@override final  LocalTime? time;
/// Average speed in km/h
@override final  double? averageSpeed;
/// Maximum participants
@override final  int? maxParticipants;

/// Create a copy of RideTemplateGroupDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideTemplateGroupDtoCopyWith<_RideTemplateGroupDto> get copyWith => __$RideTemplateGroupDtoCopyWithImpl<_RideTemplateGroupDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideTemplateGroupDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideTemplateGroupDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.sortOrder, sortOrder) || other.sortOrder == sortOrder)&&(identical(other.time, time) || other.time == time)&&(identical(other.averageSpeed, averageSpeed) || other.averageSpeed == averageSpeed)&&(identical(other.maxParticipants, maxParticipants) || other.maxParticipants == maxParticipants));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,sortOrder,time,averageSpeed,maxParticipants);

@override
String toString() {
  return 'RideTemplateGroupDto(id: $id, name: $name, sortOrder: $sortOrder, time: $time, averageSpeed: $averageSpeed, maxParticipants: $maxParticipants)';
}


}

/// @nodoc
abstract mixin class _$RideTemplateGroupDtoCopyWith<$Res> implements $RideTemplateGroupDtoCopyWith<$Res> {
  factory _$RideTemplateGroupDtoCopyWith(_RideTemplateGroupDto value, $Res Function(_RideTemplateGroupDto) _then) = __$RideTemplateGroupDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, int sortOrder, LocalTime? time, double? averageSpeed, int? maxParticipants
});




}
/// @nodoc
class __$RideTemplateGroupDtoCopyWithImpl<$Res>
    implements _$RideTemplateGroupDtoCopyWith<$Res> {
  __$RideTemplateGroupDtoCopyWithImpl(this._self, this._then);

  final _RideTemplateGroupDto _self;
  final $Res Function(_RideTemplateGroupDto) _then;

/// Create a copy of RideTemplateGroupDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? sortOrder = null,Object? time = freezed,Object? averageSpeed = freezed,Object? maxParticipants = freezed,}) {
  return _then(_RideTemplateGroupDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,sortOrder: null == sortOrder ? _self.sortOrder : sortOrder // ignore: cast_nullable_to_non_nullable
as int,time: freezed == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as LocalTime?,averageSpeed: freezed == averageSpeed ? _self.averageSpeed : averageSpeed // ignore: cast_nullable_to_non_nullable
as double?,maxParticipants: freezed == maxParticipants ? _self.maxParticipants : maxParticipants // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}


}

// dart format on
