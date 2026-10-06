// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'departure_weather_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DepartureWeatherDto {

/// State of the departure forecast. conditions is present for OK and STALE only.
 String get status;/// The forecast of the departure hour, for OK and STALE
 WeatherConditionsDto? get conditions;/// Sunrise at the meeting point, on the departure's local date
 String? get sunrise;/// Sunset at the meeting point, on the departure's local date
 String? get sunset;/// When the forecast of the meeting point was last fetched
 String? get fetchedAt;
/// Create a copy of DepartureWeatherDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DepartureWeatherDtoCopyWith<DepartureWeatherDto> get copyWith => _$DepartureWeatherDtoCopyWithImpl<DepartureWeatherDto>(this as DepartureWeatherDto, _$identity);

  /// Serializes this DepartureWeatherDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as DepartureWeatherDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DepartureWeatherDto&&(identical(other.status, _this.status) || other.status == _this.status)&&(identical(other.conditions, _this.conditions) || other.conditions == _this.conditions)&&(identical(other.sunrise, _this.sunrise) || other.sunrise == _this.sunrise)&&(identical(other.sunset, _this.sunset) || other.sunset == _this.sunset)&&(identical(other.fetchedAt, _this.fetchedAt) || other.fetchedAt == _this.fetchedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as DepartureWeatherDto;
  return Object.hash(runtimeType,_this.status,_this.conditions,_this.sunrise,_this.sunset,_this.fetchedAt);
}

@override
String toString() {
  final _this = this as DepartureWeatherDto;
  return 'DepartureWeatherDto(status: ${_this.status}, conditions: ${_this.conditions}, sunrise: ${_this.sunrise}, sunset: ${_this.sunset}, fetchedAt: ${_this.fetchedAt})';
}


}

/// @nodoc
abstract mixin class $DepartureWeatherDtoCopyWith<$Res>  {
  factory $DepartureWeatherDtoCopyWith(DepartureWeatherDto value, $Res Function(DepartureWeatherDto) _then) = _$DepartureWeatherDtoCopyWithImpl;
@useResult
$Res call({
 String status, WeatherConditionsDto? conditions, String? sunrise, String? sunset, String? fetchedAt
});


$WeatherConditionsDtoCopyWith<$Res>? get conditions;

}
/// @nodoc
class _$DepartureWeatherDtoCopyWithImpl<$Res>
    implements $DepartureWeatherDtoCopyWith<$Res> {
  _$DepartureWeatherDtoCopyWithImpl(this._self, this._then);

  final DepartureWeatherDto _self;
  final $Res Function(DepartureWeatherDto) _then;

/// Create a copy of DepartureWeatherDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? status = null,Object? conditions = freezed,Object? sunrise = freezed,Object? sunset = freezed,Object? fetchedAt = freezed,}) {
  return _then(DepartureWeatherDto(
status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,conditions: freezed == conditions ? _self.conditions : conditions // ignore: cast_nullable_to_non_nullable
as WeatherConditionsDto?,sunrise: freezed == sunrise ? _self.sunrise : sunrise // ignore: cast_nullable_to_non_nullable
as String?,sunset: freezed == sunset ? _self.sunset : sunset // ignore: cast_nullable_to_non_nullable
as String?,fetchedAt: freezed == fetchedAt ? _self.fetchedAt : fetchedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of DepartureWeatherDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$WeatherConditionsDtoCopyWith<$Res>? get conditions {
    if (_self.conditions == null) {
    return null;
  }

  return $WeatherConditionsDtoCopyWith<$Res>(_self.conditions!, (value) {
    return _then(_self.copyWith(conditions: value));
  });
}
}


/// Adds pattern-matching-related methods to [DepartureWeatherDto].
extension DepartureWeatherDtoPatterns on DepartureWeatherDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DepartureWeatherDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DepartureWeatherDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DepartureWeatherDto value)  $default,){
final _that = this;
switch (_that) {
case _DepartureWeatherDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DepartureWeatherDto value)?  $default,){
final _that = this;
switch (_that) {
case _DepartureWeatherDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String status,  WeatherConditionsDto? conditions,  String? sunrise,  String? sunset,  String? fetchedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _DepartureWeatherDto() when $default != null:
return $default(_that.status,_that.conditions,_that.sunrise,_that.sunset,_that.fetchedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String status,  WeatherConditionsDto? conditions,  String? sunrise,  String? sunset,  String? fetchedAt)  $default,) {final _that = this;
switch (_that) {
case _DepartureWeatherDto():
return $default(_that.status,_that.conditions,_that.sunrise,_that.sunset,_that.fetchedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String status,  WeatherConditionsDto? conditions,  String? sunrise,  String? sunset,  String? fetchedAt)?  $default,) {final _that = this;
switch (_that) {
case _DepartureWeatherDto() when $default != null:
return $default(_that.status,_that.conditions,_that.sunrise,_that.sunset,_that.fetchedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DepartureWeatherDto implements DepartureWeatherDto {
  const _DepartureWeatherDto({required this.status, this.conditions, this.sunrise, this.sunset, this.fetchedAt});
  factory _DepartureWeatherDto.fromJson(Map<String, dynamic> json) => _$DepartureWeatherDtoFromJson(json);

/// State of the departure forecast. conditions is present for OK and STALE only.
@override final  String status;
/// The forecast of the departure hour, for OK and STALE
@override final  WeatherConditionsDto? conditions;
/// Sunrise at the meeting point, on the departure's local date
@override final  String? sunrise;
/// Sunset at the meeting point, on the departure's local date
@override final  String? sunset;
/// When the forecast of the meeting point was last fetched
@override final  String? fetchedAt;

/// Create a copy of DepartureWeatherDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DepartureWeatherDtoCopyWith<_DepartureWeatherDto> get copyWith => __$DepartureWeatherDtoCopyWithImpl<_DepartureWeatherDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DepartureWeatherDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _DepartureWeatherDto&&(identical(other.status, status) || other.status == status)&&(identical(other.conditions, conditions) || other.conditions == conditions)&&(identical(other.sunrise, sunrise) || other.sunrise == sunrise)&&(identical(other.sunset, sunset) || other.sunset == sunset)&&(identical(other.fetchedAt, fetchedAt) || other.fetchedAt == fetchedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,status,conditions,sunrise,sunset,fetchedAt);
}

@override
String toString() {
    return 'DepartureWeatherDto(status: $status, conditions: $conditions, sunrise: $sunrise, sunset: $sunset, fetchedAt: $fetchedAt)';
}


}

/// @nodoc
abstract mixin class _$DepartureWeatherDtoCopyWith<$Res> implements $DepartureWeatherDtoCopyWith<$Res> {
  factory _$DepartureWeatherDtoCopyWith(_DepartureWeatherDto value, $Res Function(_DepartureWeatherDto) _then) = __$DepartureWeatherDtoCopyWithImpl;
@override @useResult
$Res call({
 String status, WeatherConditionsDto? conditions, String? sunrise, String? sunset, String? fetchedAt
});


@override $WeatherConditionsDtoCopyWith<$Res>? get conditions;

}
/// @nodoc
class __$DepartureWeatherDtoCopyWithImpl<$Res>
    implements _$DepartureWeatherDtoCopyWith<$Res> {
  __$DepartureWeatherDtoCopyWithImpl(this._self, this._then);

  final _DepartureWeatherDto _self;
  final $Res Function(_DepartureWeatherDto) _then;

/// Create a copy of DepartureWeatherDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? status = null,Object? conditions = freezed,Object? sunrise = freezed,Object? sunset = freezed,Object? fetchedAt = freezed,}) {
  return _then(_DepartureWeatherDto(
status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,conditions: freezed == conditions ? _self.conditions : conditions // ignore: cast_nullable_to_non_nullable
as WeatherConditionsDto?,sunrise: freezed == sunrise ? _self.sunrise : sunrise // ignore: cast_nullable_to_non_nullable
as String?,sunset: freezed == sunset ? _self.sunset : sunset // ignore: cast_nullable_to_non_nullable
as String?,fetchedAt: freezed == fetchedAt ? _self.fetchedAt : fetchedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of DepartureWeatherDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$WeatherConditionsDtoCopyWith<$Res>? get conditions {
    if (_self.conditions == null) {
    return null;
  }

  return $WeatherConditionsDtoCopyWith<$Res>(_self.conditions!, (value) {
    return _then(_self.copyWith(conditions: value));
  });
}
}

// dart format on
