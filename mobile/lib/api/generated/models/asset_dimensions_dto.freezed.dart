// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'asset_dimensions_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AssetDimensionsDto {

 int? get width; int? get height;
/// Create a copy of AssetDimensionsDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AssetDimensionsDtoCopyWith<AssetDimensionsDto> get copyWith => _$AssetDimensionsDtoCopyWithImpl<AssetDimensionsDto>(this as AssetDimensionsDto, _$identity);

  /// Serializes this AssetDimensionsDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as AssetDimensionsDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AssetDimensionsDto&&(identical(other.width, _this.width) || other.width == _this.width)&&(identical(other.height, _this.height) || other.height == _this.height));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as AssetDimensionsDto;
  return Object.hash(runtimeType,_this.width,_this.height);
}

@override
String toString() {
  final _this = this as AssetDimensionsDto;
  return 'AssetDimensionsDto(width: ${_this.width}, height: ${_this.height})';
}


}

/// @nodoc
abstract mixin class $AssetDimensionsDtoCopyWith<$Res>  {
  factory $AssetDimensionsDtoCopyWith(AssetDimensionsDto value, $Res Function(AssetDimensionsDto) _then) = _$AssetDimensionsDtoCopyWithImpl;
@useResult
$Res call({
 int? width, int? height
});




}
/// @nodoc
class _$AssetDimensionsDtoCopyWithImpl<$Res>
    implements $AssetDimensionsDtoCopyWith<$Res> {
  _$AssetDimensionsDtoCopyWithImpl(this._self, this._then);

  final AssetDimensionsDto _self;
  final $Res Function(AssetDimensionsDto) _then;

/// Create a copy of AssetDimensionsDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? width = freezed,Object? height = freezed,}) {
  return _then(AssetDimensionsDto(
width: freezed == width ? _self.width : width // ignore: cast_nullable_to_non_nullable
as int?,height: freezed == height ? _self.height : height // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

}


/// Adds pattern-matching-related methods to [AssetDimensionsDto].
extension AssetDimensionsDtoPatterns on AssetDimensionsDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AssetDimensionsDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AssetDimensionsDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AssetDimensionsDto value)  $default,){
final _that = this;
switch (_that) {
case _AssetDimensionsDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AssetDimensionsDto value)?  $default,){
final _that = this;
switch (_that) {
case _AssetDimensionsDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int? width,  int? height)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AssetDimensionsDto() when $default != null:
return $default(_that.width,_that.height);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int? width,  int? height)  $default,) {final _that = this;
switch (_that) {
case _AssetDimensionsDto():
return $default(_that.width,_that.height);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int? width,  int? height)?  $default,) {final _that = this;
switch (_that) {
case _AssetDimensionsDto() when $default != null:
return $default(_that.width,_that.height);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AssetDimensionsDto implements AssetDimensionsDto {
  const _AssetDimensionsDto({this.width, this.height});
  factory _AssetDimensionsDto.fromJson(Map<String, dynamic> json) => _$AssetDimensionsDtoFromJson(json);

@override final  int? width;
@override final  int? height;

/// Create a copy of AssetDimensionsDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AssetDimensionsDtoCopyWith<_AssetDimensionsDto> get copyWith => __$AssetDimensionsDtoCopyWithImpl<_AssetDimensionsDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AssetDimensionsDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _AssetDimensionsDto&&(identical(other.width, width) || other.width == width)&&(identical(other.height, height) || other.height == height));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,width,height);
}

@override
String toString() {
    return 'AssetDimensionsDto(width: $width, height: $height)';
}


}

/// @nodoc
abstract mixin class _$AssetDimensionsDtoCopyWith<$Res> implements $AssetDimensionsDtoCopyWith<$Res> {
  factory _$AssetDimensionsDtoCopyWith(_AssetDimensionsDto value, $Res Function(_AssetDimensionsDto) _then) = __$AssetDimensionsDtoCopyWithImpl;
@override @useResult
$Res call({
 int? width, int? height
});




}
/// @nodoc
class __$AssetDimensionsDtoCopyWithImpl<$Res>
    implements _$AssetDimensionsDtoCopyWith<$Res> {
  __$AssetDimensionsDtoCopyWithImpl(this._self, this._then);

  final _AssetDimensionsDto _self;
  final $Res Function(_AssetDimensionsDto) _then;

/// Create a copy of AssetDimensionsDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? width = freezed,Object? height = freezed,}) {
  return _then(_AssetDimensionsDto(
width: freezed == width ? _self.width : width // ignore: cast_nullable_to_non_nullable
as int?,height: freezed == height ? _self.height : height // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}


}

// dart format on
