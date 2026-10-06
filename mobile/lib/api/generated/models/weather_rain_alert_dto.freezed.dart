// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'weather_rain_alert_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$WeatherRainAlertDto {

/// Probability of precipitation then, % (0–100)
 int get probability;/// When — the passage at the checkpoint, or the hour
 String get time;/// The condition forecast then
 String get condition;/// Where, metres from the start of the leg. Absent in a ride's summary, which only knows the departure point.
 double? get distance;
/// Create a copy of WeatherRainAlertDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$WeatherRainAlertDtoCopyWith<WeatherRainAlertDto> get copyWith => _$WeatherRainAlertDtoCopyWithImpl<WeatherRainAlertDto>(this as WeatherRainAlertDto, _$identity);

  /// Serializes this WeatherRainAlertDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as WeatherRainAlertDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is WeatherRainAlertDto&&(identical(other.probability, _this.probability) || other.probability == _this.probability)&&(identical(other.time, _this.time) || other.time == _this.time)&&(identical(other.condition, _this.condition) || other.condition == _this.condition)&&(identical(other.distance, _this.distance) || other.distance == _this.distance));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as WeatherRainAlertDto;
  return Object.hash(runtimeType,_this.probability,_this.time,_this.condition,_this.distance);
}

@override
String toString() {
  final _this = this as WeatherRainAlertDto;
  return 'WeatherRainAlertDto(probability: ${_this.probability}, time: ${_this.time}, condition: ${_this.condition}, distance: ${_this.distance})';
}


}

/// @nodoc
abstract mixin class $WeatherRainAlertDtoCopyWith<$Res>  {
  factory $WeatherRainAlertDtoCopyWith(WeatherRainAlertDto value, $Res Function(WeatherRainAlertDto) _then) = _$WeatherRainAlertDtoCopyWithImpl;
@useResult
$Res call({
 int probability, String time, String condition, double? distance
});




}
/// @nodoc
class _$WeatherRainAlertDtoCopyWithImpl<$Res>
    implements $WeatherRainAlertDtoCopyWith<$Res> {
  _$WeatherRainAlertDtoCopyWithImpl(this._self, this._then);

  final WeatherRainAlertDto _self;
  final $Res Function(WeatherRainAlertDto) _then;

/// Create a copy of WeatherRainAlertDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? probability = null,Object? time = null,Object? condition = null,Object? distance = freezed,}) {
  return _then(WeatherRainAlertDto(
probability: null == probability ? _self.probability : probability // ignore: cast_nullable_to_non_nullable
as int,time: null == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as String,condition: null == condition ? _self.condition : condition // ignore: cast_nullable_to_non_nullable
as String,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}

}


/// Adds pattern-matching-related methods to [WeatherRainAlertDto].
extension WeatherRainAlertDtoPatterns on WeatherRainAlertDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _WeatherRainAlertDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _WeatherRainAlertDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _WeatherRainAlertDto value)  $default,){
final _that = this;
switch (_that) {
case _WeatherRainAlertDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _WeatherRainAlertDto value)?  $default,){
final _that = this;
switch (_that) {
case _WeatherRainAlertDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int probability,  String time,  String condition,  double? distance)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _WeatherRainAlertDto() when $default != null:
return $default(_that.probability,_that.time,_that.condition,_that.distance);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int probability,  String time,  String condition,  double? distance)  $default,) {final _that = this;
switch (_that) {
case _WeatherRainAlertDto():
return $default(_that.probability,_that.time,_that.condition,_that.distance);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int probability,  String time,  String condition,  double? distance)?  $default,) {final _that = this;
switch (_that) {
case _WeatherRainAlertDto() when $default != null:
return $default(_that.probability,_that.time,_that.condition,_that.distance);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _WeatherRainAlertDto implements WeatherRainAlertDto {
  const _WeatherRainAlertDto({required this.probability, required this.time, required this.condition, this.distance});
  factory _WeatherRainAlertDto.fromJson(Map<String, dynamic> json) => _$WeatherRainAlertDtoFromJson(json);

/// Probability of precipitation then, % (0–100)
@override final  int probability;
/// When — the passage at the checkpoint, or the hour
@override final  String time;
/// The condition forecast then
@override final  String condition;
/// Where, metres from the start of the leg. Absent in a ride's summary, which only knows the departure point.
@override final  double? distance;

/// Create a copy of WeatherRainAlertDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$WeatherRainAlertDtoCopyWith<_WeatherRainAlertDto> get copyWith => __$WeatherRainAlertDtoCopyWithImpl<_WeatherRainAlertDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$WeatherRainAlertDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _WeatherRainAlertDto&&(identical(other.probability, probability) || other.probability == probability)&&(identical(other.time, time) || other.time == time)&&(identical(other.condition, condition) || other.condition == condition)&&(identical(other.distance, distance) || other.distance == distance));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,probability,time,condition,distance);
}

@override
String toString() {
    return 'WeatherRainAlertDto(probability: $probability, time: $time, condition: $condition, distance: $distance)';
}


}

/// @nodoc
abstract mixin class _$WeatherRainAlertDtoCopyWith<$Res> implements $WeatherRainAlertDtoCopyWith<$Res> {
  factory _$WeatherRainAlertDtoCopyWith(_WeatherRainAlertDto value, $Res Function(_WeatherRainAlertDto) _then) = __$WeatherRainAlertDtoCopyWithImpl;
@override @useResult
$Res call({
 int probability, String time, String condition, double? distance
});




}
/// @nodoc
class __$WeatherRainAlertDtoCopyWithImpl<$Res>
    implements _$WeatherRainAlertDtoCopyWith<$Res> {
  __$WeatherRainAlertDtoCopyWithImpl(this._self, this._then);

  final _WeatherRainAlertDto _self;
  final $Res Function(_WeatherRainAlertDto) _then;

/// Create a copy of WeatherRainAlertDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? probability = null,Object? time = null,Object? condition = null,Object? distance = freezed,}) {
  return _then(_WeatherRainAlertDto(
probability: null == probability ? _self.probability : probability // ignore: cast_nullable_to_non_nullable
as int,time: null == time ? _self.time : time // ignore: cast_nullable_to_non_nullable
as String,condition: null == condition ? _self.condition : condition // ignore: cast_nullable_to_non_nullable
as String,distance: freezed == distance ? _self.distance : distance // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}


}

// dart format on
