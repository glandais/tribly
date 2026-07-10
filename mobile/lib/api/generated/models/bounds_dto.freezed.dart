// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'bounds_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BoundsDto {

/// Western edge
 double get minLon;/// Southern edge
 double get minLat;/// Eastern edge
 double get maxLon;/// Northern edge
 double get maxLat;
/// Create a copy of BoundsDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BoundsDtoCopyWith<BoundsDto> get copyWith => _$BoundsDtoCopyWithImpl<BoundsDto>(this as BoundsDto, _$identity);

  /// Serializes this BoundsDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BoundsDto&&(identical(other.minLon, minLon) || other.minLon == minLon)&&(identical(other.minLat, minLat) || other.minLat == minLat)&&(identical(other.maxLon, maxLon) || other.maxLon == maxLon)&&(identical(other.maxLat, maxLat) || other.maxLat == maxLat));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,minLon,minLat,maxLon,maxLat);

@override
String toString() {
  return 'BoundsDto(minLon: $minLon, minLat: $minLat, maxLon: $maxLon, maxLat: $maxLat)';
}


}

/// @nodoc
abstract mixin class $BoundsDtoCopyWith<$Res>  {
  factory $BoundsDtoCopyWith(BoundsDto value, $Res Function(BoundsDto) _then) = _$BoundsDtoCopyWithImpl;
@useResult
$Res call({
 double minLon, double minLat, double maxLon, double maxLat
});




}
/// @nodoc
class _$BoundsDtoCopyWithImpl<$Res>
    implements $BoundsDtoCopyWith<$Res> {
  _$BoundsDtoCopyWithImpl(this._self, this._then);

  final BoundsDto _self;
  final $Res Function(BoundsDto) _then;

/// Create a copy of BoundsDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? minLon = null,Object? minLat = null,Object? maxLon = null,Object? maxLat = null,}) {
  return _then(_self.copyWith(
minLon: null == minLon ? _self.minLon : minLon // ignore: cast_nullable_to_non_nullable
as double,minLat: null == minLat ? _self.minLat : minLat // ignore: cast_nullable_to_non_nullable
as double,maxLon: null == maxLon ? _self.maxLon : maxLon // ignore: cast_nullable_to_non_nullable
as double,maxLat: null == maxLat ? _self.maxLat : maxLat // ignore: cast_nullable_to_non_nullable
as double,
  ));
}

}


/// Adds pattern-matching-related methods to [BoundsDto].
extension BoundsDtoPatterns on BoundsDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BoundsDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BoundsDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BoundsDto value)  $default,){
final _that = this;
switch (_that) {
case _BoundsDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BoundsDto value)?  $default,){
final _that = this;
switch (_that) {
case _BoundsDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( double minLon,  double minLat,  double maxLon,  double maxLat)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BoundsDto() when $default != null:
return $default(_that.minLon,_that.minLat,_that.maxLon,_that.maxLat);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( double minLon,  double minLat,  double maxLon,  double maxLat)  $default,) {final _that = this;
switch (_that) {
case _BoundsDto():
return $default(_that.minLon,_that.minLat,_that.maxLon,_that.maxLat);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( double minLon,  double minLat,  double maxLon,  double maxLat)?  $default,) {final _that = this;
switch (_that) {
case _BoundsDto() when $default != null:
return $default(_that.minLon,_that.minLat,_that.maxLon,_that.maxLat);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BoundsDto implements BoundsDto {
  const _BoundsDto({required this.minLon, required this.minLat, required this.maxLon, required this.maxLat});
  factory _BoundsDto.fromJson(Map<String, dynamic> json) => _$BoundsDtoFromJson(json);

/// Western edge
@override final  double minLon;
/// Southern edge
@override final  double minLat;
/// Eastern edge
@override final  double maxLon;
/// Northern edge
@override final  double maxLat;

/// Create a copy of BoundsDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BoundsDtoCopyWith<_BoundsDto> get copyWith => __$BoundsDtoCopyWithImpl<_BoundsDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BoundsDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _BoundsDto&&(identical(other.minLon, minLon) || other.minLon == minLon)&&(identical(other.minLat, minLat) || other.minLat == minLat)&&(identical(other.maxLon, maxLon) || other.maxLon == maxLon)&&(identical(other.maxLat, maxLat) || other.maxLat == maxLat));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,minLon,minLat,maxLon,maxLat);

@override
String toString() {
  return 'BoundsDto(minLon: $minLon, minLat: $minLat, maxLon: $maxLon, maxLat: $maxLat)';
}


}

/// @nodoc
abstract mixin class _$BoundsDtoCopyWith<$Res> implements $BoundsDtoCopyWith<$Res> {
  factory _$BoundsDtoCopyWith(_BoundsDto value, $Res Function(_BoundsDto) _then) = __$BoundsDtoCopyWithImpl;
@override @useResult
$Res call({
 double minLon, double minLat, double maxLon, double maxLat
});




}
/// @nodoc
class __$BoundsDtoCopyWithImpl<$Res>
    implements _$BoundsDtoCopyWith<$Res> {
  __$BoundsDtoCopyWithImpl(this._self, this._then);

  final _BoundsDto _self;
  final $Res Function(_BoundsDto) _then;

/// Create a copy of BoundsDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? minLon = null,Object? minLat = null,Object? maxLon = null,Object? maxLat = null,}) {
  return _then(_BoundsDto(
minLon: null == minLon ? _self.minLon : minLon // ignore: cast_nullable_to_non_nullable
as double,minLat: null == minLat ? _self.minLat : minLat // ignore: cast_nullable_to_non_nullable
as double,maxLon: null == maxLon ? _self.maxLon : maxLon // ignore: cast_nullable_to_non_nullable
as double,maxLat: null == maxLat ? _self.maxLat : maxLat // ignore: cast_nullable_to_non_nullable
as double,
  ));
}


}

// dart format on
