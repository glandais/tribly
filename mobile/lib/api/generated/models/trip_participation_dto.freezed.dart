// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'trip_participation_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TripParticipationDto {

/// Participation ID (TSID)
 String get id;/// User ID (TSID)
 String get userId;/// Registration timestamp
 String? get registeredAt;
/// Create a copy of TripParticipationDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TripParticipationDtoCopyWith<TripParticipationDto> get copyWith => _$TripParticipationDtoCopyWithImpl<TripParticipationDto>(this as TripParticipationDto, _$identity);

  /// Serializes this TripParticipationDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TripParticipationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.registeredAt, registeredAt) || other.registeredAt == registeredAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,userId,registeredAt);

@override
String toString() {
  return 'TripParticipationDto(id: $id, userId: $userId, registeredAt: $registeredAt)';
}


}

/// @nodoc
abstract mixin class $TripParticipationDtoCopyWith<$Res>  {
  factory $TripParticipationDtoCopyWith(TripParticipationDto value, $Res Function(TripParticipationDto) _then) = _$TripParticipationDtoCopyWithImpl;
@useResult
$Res call({
 String id, String userId, String? registeredAt
});




}
/// @nodoc
class _$TripParticipationDtoCopyWithImpl<$Res>
    implements $TripParticipationDtoCopyWith<$Res> {
  _$TripParticipationDtoCopyWithImpl(this._self, this._then);

  final TripParticipationDto _self;
  final $Res Function(TripParticipationDto) _then;

/// Create a copy of TripParticipationDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? userId = null,Object? registeredAt = freezed,}) {
  return _then(TripParticipationDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,registeredAt: freezed == registeredAt ? _self.registeredAt : registeredAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [TripParticipationDto].
extension TripParticipationDtoPatterns on TripParticipationDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TripParticipationDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TripParticipationDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TripParticipationDto value)  $default,){
final _that = this;
switch (_that) {
case _TripParticipationDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TripParticipationDto value)?  $default,){
final _that = this;
switch (_that) {
case _TripParticipationDto() when $default != null:
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
case _TripParticipationDto() when $default != null:
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
case _TripParticipationDto():
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
case _TripParticipationDto() when $default != null:
return $default(_that.id,_that.userId,_that.registeredAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TripParticipationDto implements TripParticipationDto {
  const _TripParticipationDto({required this.id, required this.userId, this.registeredAt});
  factory _TripParticipationDto.fromJson(Map<String, dynamic> json) => _$TripParticipationDtoFromJson(json);

/// Participation ID (TSID)
@override final  String id;
/// User ID (TSID)
@override final  String userId;
/// Registration timestamp
@override final  String? registeredAt;

/// Create a copy of TripParticipationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TripParticipationDtoCopyWith<_TripParticipationDto> get copyWith => __$TripParticipationDtoCopyWithImpl<_TripParticipationDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TripParticipationDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TripParticipationDto&&(identical(other.id, id) || other.id == id)&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.registeredAt, registeredAt) || other.registeredAt == registeredAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,userId,registeredAt);

@override
String toString() {
  return 'TripParticipationDto(id: $id, userId: $userId, registeredAt: $registeredAt)';
}


}

/// @nodoc
abstract mixin class _$TripParticipationDtoCopyWith<$Res> implements $TripParticipationDtoCopyWith<$Res> {
  factory _$TripParticipationDtoCopyWith(_TripParticipationDto value, $Res Function(_TripParticipationDto) _then) = __$TripParticipationDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String userId, String? registeredAt
});




}
/// @nodoc
class __$TripParticipationDtoCopyWithImpl<$Res>
    implements _$TripParticipationDtoCopyWith<$Res> {
  __$TripParticipationDtoCopyWithImpl(this._self, this._then);

  final _TripParticipationDto _self;
  final $Res Function(_TripParticipationDto) _then;

/// Create a copy of TripParticipationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? userId = null,Object? registeredAt = freezed,}) {
  return _then(_TripParticipationDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,registeredAt: freezed == registeredAt ? _self.registeredAt : registeredAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
