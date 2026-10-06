// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'wind_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$WindDto {

/// Mean wind speed, km/h
 double get speed;/// Direction the wind comes FROM, degrees clockwise from north (0 = from the north, 90 = from the east)
 double get direction;/// direction on the eight-point rose, still the direction the wind comes FROM
 String get compass;/// Gusts, km/h. Absent when the model gives none.
 double? get gusts;
/// Create a copy of WindDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$WindDtoCopyWith<WindDto> get copyWith => _$WindDtoCopyWithImpl<WindDto>(this as WindDto, _$identity);

  /// Serializes this WindDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as WindDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is WindDto&&(identical(other.speed, _this.speed) || other.speed == _this.speed)&&(identical(other.direction, _this.direction) || other.direction == _this.direction)&&(identical(other.compass, _this.compass) || other.compass == _this.compass)&&(identical(other.gusts, _this.gusts) || other.gusts == _this.gusts));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as WindDto;
  return Object.hash(runtimeType,_this.speed,_this.direction,_this.compass,_this.gusts);
}

@override
String toString() {
  final _this = this as WindDto;
  return 'WindDto(speed: ${_this.speed}, direction: ${_this.direction}, compass: ${_this.compass}, gusts: ${_this.gusts})';
}


}

/// @nodoc
abstract mixin class $WindDtoCopyWith<$Res>  {
  factory $WindDtoCopyWith(WindDto value, $Res Function(WindDto) _then) = _$WindDtoCopyWithImpl;
@useResult
$Res call({
 double speed, double direction, String compass, double? gusts
});




}
/// @nodoc
class _$WindDtoCopyWithImpl<$Res>
    implements $WindDtoCopyWith<$Res> {
  _$WindDtoCopyWithImpl(this._self, this._then);

  final WindDto _self;
  final $Res Function(WindDto) _then;

/// Create a copy of WindDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? speed = null,Object? direction = null,Object? compass = null,Object? gusts = freezed,}) {
  return _then(WindDto(
speed: null == speed ? _self.speed : speed // ignore: cast_nullable_to_non_nullable
as double,direction: null == direction ? _self.direction : direction // ignore: cast_nullable_to_non_nullable
as double,compass: null == compass ? _self.compass : compass // ignore: cast_nullable_to_non_nullable
as String,gusts: freezed == gusts ? _self.gusts : gusts // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}

}


/// Adds pattern-matching-related methods to [WindDto].
extension WindDtoPatterns on WindDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _WindDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _WindDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _WindDto value)  $default,){
final _that = this;
switch (_that) {
case _WindDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _WindDto value)?  $default,){
final _that = this;
switch (_that) {
case _WindDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( double speed,  double direction,  String compass,  double? gusts)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _WindDto() when $default != null:
return $default(_that.speed,_that.direction,_that.compass,_that.gusts);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( double speed,  double direction,  String compass,  double? gusts)  $default,) {final _that = this;
switch (_that) {
case _WindDto():
return $default(_that.speed,_that.direction,_that.compass,_that.gusts);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( double speed,  double direction,  String compass,  double? gusts)?  $default,) {final _that = this;
switch (_that) {
case _WindDto() when $default != null:
return $default(_that.speed,_that.direction,_that.compass,_that.gusts);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _WindDto implements WindDto {
  const _WindDto({required this.speed, required this.direction, required this.compass, this.gusts});
  factory _WindDto.fromJson(Map<String, dynamic> json) => _$WindDtoFromJson(json);

/// Mean wind speed, km/h
@override final  double speed;
/// Direction the wind comes FROM, degrees clockwise from north (0 = from the north, 90 = from the east)
@override final  double direction;
/// direction on the eight-point rose, still the direction the wind comes FROM
@override final  String compass;
/// Gusts, km/h. Absent when the model gives none.
@override final  double? gusts;

/// Create a copy of WindDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$WindDtoCopyWith<_WindDto> get copyWith => __$WindDtoCopyWithImpl<_WindDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$WindDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _WindDto&&(identical(other.speed, speed) || other.speed == speed)&&(identical(other.direction, direction) || other.direction == direction)&&(identical(other.compass, compass) || other.compass == compass)&&(identical(other.gusts, gusts) || other.gusts == gusts));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,speed,direction,compass,gusts);
}

@override
String toString() {
    return 'WindDto(speed: $speed, direction: $direction, compass: $compass, gusts: $gusts)';
}


}

/// @nodoc
abstract mixin class _$WindDtoCopyWith<$Res> implements $WindDtoCopyWith<$Res> {
  factory _$WindDtoCopyWith(_WindDto value, $Res Function(_WindDto) _then) = __$WindDtoCopyWithImpl;
@override @useResult
$Res call({
 double speed, double direction, String compass, double? gusts
});




}
/// @nodoc
class __$WindDtoCopyWithImpl<$Res>
    implements _$WindDtoCopyWith<$Res> {
  __$WindDtoCopyWithImpl(this._self, this._then);

  final _WindDto _self;
  final $Res Function(_WindDto) _then;

/// Create a copy of WindDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? speed = null,Object? direction = null,Object? compass = null,Object? gusts = freezed,}) {
  return _then(_WindDto(
speed: null == speed ? _self.speed : speed // ignore: cast_nullable_to_non_nullable
as double,direction: null == direction ? _self.direction : direction // ignore: cast_nullable_to_non_nullable
as double,compass: null == compass ? _self.compass : compass // ignore: cast_nullable_to_non_nullable
as String,gusts: freezed == gusts ? _self.gusts : gusts // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}


}

// dart format on
