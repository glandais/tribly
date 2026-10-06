// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'wind_exposure_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$WindExposureDto {

/// Metres with a HEAD wind
 double get head;/// Metres with a CROSS wind
 double get cross;/// Metres with a TAIL wind
 double get tail;
/// Create a copy of WindExposureDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$WindExposureDtoCopyWith<WindExposureDto> get copyWith => _$WindExposureDtoCopyWithImpl<WindExposureDto>(this as WindExposureDto, _$identity);

  /// Serializes this WindExposureDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as WindExposureDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is WindExposureDto&&(identical(other.head, _this.head) || other.head == _this.head)&&(identical(other.cross, _this.cross) || other.cross == _this.cross)&&(identical(other.tail, _this.tail) || other.tail == _this.tail));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as WindExposureDto;
  return Object.hash(runtimeType,_this.head,_this.cross,_this.tail);
}

@override
String toString() {
  final _this = this as WindExposureDto;
  return 'WindExposureDto(head: ${_this.head}, cross: ${_this.cross}, tail: ${_this.tail})';
}


}

/// @nodoc
abstract mixin class $WindExposureDtoCopyWith<$Res>  {
  factory $WindExposureDtoCopyWith(WindExposureDto value, $Res Function(WindExposureDto) _then) = _$WindExposureDtoCopyWithImpl;
@useResult
$Res call({
 double head, double cross, double tail
});




}
/// @nodoc
class _$WindExposureDtoCopyWithImpl<$Res>
    implements $WindExposureDtoCopyWith<$Res> {
  _$WindExposureDtoCopyWithImpl(this._self, this._then);

  final WindExposureDto _self;
  final $Res Function(WindExposureDto) _then;

/// Create a copy of WindExposureDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? head = null,Object? cross = null,Object? tail = null,}) {
  return _then(WindExposureDto(
head: null == head ? _self.head : head // ignore: cast_nullable_to_non_nullable
as double,cross: null == cross ? _self.cross : cross // ignore: cast_nullable_to_non_nullable
as double,tail: null == tail ? _self.tail : tail // ignore: cast_nullable_to_non_nullable
as double,
  ));
}

}


/// Adds pattern-matching-related methods to [WindExposureDto].
extension WindExposureDtoPatterns on WindExposureDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _WindExposureDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _WindExposureDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _WindExposureDto value)  $default,){
final _that = this;
switch (_that) {
case _WindExposureDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _WindExposureDto value)?  $default,){
final _that = this;
switch (_that) {
case _WindExposureDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( double head,  double cross,  double tail)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _WindExposureDto() when $default != null:
return $default(_that.head,_that.cross,_that.tail);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( double head,  double cross,  double tail)  $default,) {final _that = this;
switch (_that) {
case _WindExposureDto():
return $default(_that.head,_that.cross,_that.tail);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( double head,  double cross,  double tail)?  $default,) {final _that = this;
switch (_that) {
case _WindExposureDto() when $default != null:
return $default(_that.head,_that.cross,_that.tail);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _WindExposureDto implements WindExposureDto {
  const _WindExposureDto({required this.head, required this.cross, required this.tail});
  factory _WindExposureDto.fromJson(Map<String, dynamic> json) => _$WindExposureDtoFromJson(json);

/// Metres with a HEAD wind
@override final  double head;
/// Metres with a CROSS wind
@override final  double cross;
/// Metres with a TAIL wind
@override final  double tail;

/// Create a copy of WindExposureDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$WindExposureDtoCopyWith<_WindExposureDto> get copyWith => __$WindExposureDtoCopyWithImpl<_WindExposureDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$WindExposureDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _WindExposureDto&&(identical(other.head, head) || other.head == head)&&(identical(other.cross, cross) || other.cross == cross)&&(identical(other.tail, tail) || other.tail == tail));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,head,cross,tail);
}

@override
String toString() {
    return 'WindExposureDto(head: $head, cross: $cross, tail: $tail)';
}


}

/// @nodoc
abstract mixin class _$WindExposureDtoCopyWith<$Res> implements $WindExposureDtoCopyWith<$Res> {
  factory _$WindExposureDtoCopyWith(_WindExposureDto value, $Res Function(_WindExposureDto) _then) = __$WindExposureDtoCopyWithImpl;
@override @useResult
$Res call({
 double head, double cross, double tail
});




}
/// @nodoc
class __$WindExposureDtoCopyWithImpl<$Res>
    implements _$WindExposureDtoCopyWith<$Res> {
  __$WindExposureDtoCopyWithImpl(this._self, this._then);

  final _WindExposureDto _self;
  final $Res Function(_WindExposureDto) _then;

/// Create a copy of WindExposureDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? head = null,Object? cross = null,Object? tail = null,}) {
  return _then(_WindExposureDto(
head: null == head ? _self.head : head // ignore: cast_nullable_to_non_nullable
as double,cross: null == cross ? _self.cross : cross // ignore: cast_nullable_to_non_nullable
as double,tail: null == tail ? _self.tail : tail // ignore: cast_nullable_to_non_nullable
as double,
  ));
}


}

// dart format on
