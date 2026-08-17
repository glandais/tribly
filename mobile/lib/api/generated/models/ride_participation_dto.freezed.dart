// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_participation_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideParticipationDto {

/// Participation ID (TSID)
 String get id;/// User ID (TSID)
 String get userId;/// Registration timestamp
 String? get registeredAt;
/// Create a copy of RideParticipationDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideParticipationDtoCopyWith<RideParticipationDto> get copyWith => _$RideParticipationDtoCopyWithImpl<RideParticipationDto>(this as RideParticipationDto, _$identity);

  /// Serializes this RideParticipationDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideParticipationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.registeredAt, registeredAt) || other.registeredAt == registeredAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,userId,registeredAt);

@override
String toString() {
  return 'RideParticipationDto(id: $id, userId: $userId, registeredAt: $registeredAt)';
}


}

/// @nodoc
abstract mixin class $RideParticipationDtoCopyWith<$Res>  {
  factory $RideParticipationDtoCopyWith(RideParticipationDto value, $Res Function(RideParticipationDto) _then) = _$RideParticipationDtoCopyWithImpl;
@useResult
$Res call({
 String id, String userId, String? registeredAt
});




}
/// @nodoc
class _$RideParticipationDtoCopyWithImpl<$Res>
    implements $RideParticipationDtoCopyWith<$Res> {
  _$RideParticipationDtoCopyWithImpl(this._self, this._then);

  final RideParticipationDto _self;
  final $Res Function(RideParticipationDto) _then;

/// Create a copy of RideParticipationDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? userId = null,Object? registeredAt = freezed,}) {
  return _then(RideParticipationDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,registeredAt: freezed == registeredAt ? _self.registeredAt : registeredAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [RideParticipationDto].
extension RideParticipationDtoPatterns on RideParticipationDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideParticipationDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideParticipationDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideParticipationDto value)  $default,){
final _that = this;
switch (_that) {
case _RideParticipationDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideParticipationDto value)?  $default,){
final _that = this;
switch (_that) {
case _RideParticipationDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String userId,  String? registeredAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideParticipationDto() when $default != null:
return $default(_that.id,_that.userId,_that.registeredAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String userId,  String? registeredAt)  $default,) {final _that = this;
switch (_that) {
case _RideParticipationDto():
return $default(_that.id,_that.userId,_that.registeredAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String userId,  String? registeredAt)?  $default,) {final _that = this;
switch (_that) {
case _RideParticipationDto() when $default != null:
return $default(_that.id,_that.userId,_that.registeredAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideParticipationDto implements RideParticipationDto {
  const _RideParticipationDto({required this.id, required this.userId, this.registeredAt});
  factory _RideParticipationDto.fromJson(Map<String, dynamic> json) => _$RideParticipationDtoFromJson(json);

/// Participation ID (TSID)
@override final  String id;
/// User ID (TSID)
@override final  String userId;
/// Registration timestamp
@override final  String? registeredAt;

/// Create a copy of RideParticipationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideParticipationDtoCopyWith<_RideParticipationDto> get copyWith => __$RideParticipationDtoCopyWithImpl<_RideParticipationDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideParticipationDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideParticipationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.registeredAt, registeredAt) || other.registeredAt == registeredAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,userId,registeredAt);

@override
String toString() {
  return 'RideParticipationDto(id: $id, userId: $userId, registeredAt: $registeredAt)';
}


}

/// @nodoc
abstract mixin class _$RideParticipationDtoCopyWith<$Res> implements $RideParticipationDtoCopyWith<$Res> {
  factory _$RideParticipationDtoCopyWith(_RideParticipationDto value, $Res Function(_RideParticipationDto) _then) = __$RideParticipationDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String userId, String? registeredAt
});




}
/// @nodoc
class __$RideParticipationDtoCopyWithImpl<$Res>
    implements _$RideParticipationDtoCopyWith<$Res> {
  __$RideParticipationDtoCopyWithImpl(this._self, this._then);

  final _RideParticipationDto _self;
  final $Res Function(_RideParticipationDto) _then;

/// Create a copy of RideParticipationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? userId = null,Object? registeredAt = freezed,}) {
  return _then(_RideParticipationDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,registeredAt: freezed == registeredAt ? _self.registeredAt : registeredAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
