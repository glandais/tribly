// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'tile_token_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TileTokenDto {

/// Token to pass as the 't' query parameter of the .mvt endpoints
 String get token;/// Absolute expiry instant (ISO 8601), for logs and diagnostics
 String get expiresAt;/// Seconds until expiry, measured at issuance. Schedule renewal on this, not on expiresAt: it is immune to a device clock that drifts.
 int get expiresIn;
/// Create a copy of TileTokenDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TileTokenDtoCopyWith<TileTokenDto> get copyWith => _$TileTokenDtoCopyWithImpl<TileTokenDto>(this as TileTokenDto, _$identity);

  /// Serializes this TileTokenDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TileTokenDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TileTokenDto&&(identical(other.token, _this.token) || other.token == _this.token)&&(identical(other.expiresAt, _this.expiresAt) || other.expiresAt == _this.expiresAt)&&(identical(other.expiresIn, _this.expiresIn) || other.expiresIn == _this.expiresIn));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TileTokenDto;
  return Object.hash(runtimeType,_this.token,_this.expiresAt,_this.expiresIn);
}

@override
String toString() {
  final _this = this as TileTokenDto;
  return 'TileTokenDto(token: ${_this.token}, expiresAt: ${_this.expiresAt}, expiresIn: ${_this.expiresIn})';
}


}

/// @nodoc
abstract mixin class $TileTokenDtoCopyWith<$Res>  {
  factory $TileTokenDtoCopyWith(TileTokenDto value, $Res Function(TileTokenDto) _then) = _$TileTokenDtoCopyWithImpl;
@useResult
$Res call({
 String token, String expiresAt, int expiresIn
});




}
/// @nodoc
class _$TileTokenDtoCopyWithImpl<$Res>
    implements $TileTokenDtoCopyWith<$Res> {
  _$TileTokenDtoCopyWithImpl(this._self, this._then);

  final TileTokenDto _self;
  final $Res Function(TileTokenDto) _then;

/// Create a copy of TileTokenDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? token = null,Object? expiresAt = null,Object? expiresIn = null,}) {
  return _then(TileTokenDto(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,expiresAt: null == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [TileTokenDto].
extension TileTokenDtoPatterns on TileTokenDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TileTokenDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TileTokenDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TileTokenDto value)  $default,){
final _that = this;
switch (_that) {
case _TileTokenDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TileTokenDto value)?  $default,){
final _that = this;
switch (_that) {
case _TileTokenDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String token,  String expiresAt,  int expiresIn)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TileTokenDto() when $default != null:
return $default(_that.token,_that.expiresAt,_that.expiresIn);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String token,  String expiresAt,  int expiresIn)  $default,) {final _that = this;
switch (_that) {
case _TileTokenDto():
return $default(_that.token,_that.expiresAt,_that.expiresIn);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String token,  String expiresAt,  int expiresIn)?  $default,) {final _that = this;
switch (_that) {
case _TileTokenDto() when $default != null:
return $default(_that.token,_that.expiresAt,_that.expiresIn);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TileTokenDto implements TileTokenDto {
  const _TileTokenDto({required this.token, required this.expiresAt, required this.expiresIn});
  factory _TileTokenDto.fromJson(Map<String, dynamic> json) => _$TileTokenDtoFromJson(json);

/// Token to pass as the 't' query parameter of the .mvt endpoints
@override final  String token;
/// Absolute expiry instant (ISO 8601), for logs and diagnostics
@override final  String expiresAt;
/// Seconds until expiry, measured at issuance. Schedule renewal on this, not on expiresAt: it is immune to a device clock that drifts.
@override final  int expiresIn;

/// Create a copy of TileTokenDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TileTokenDtoCopyWith<_TileTokenDto> get copyWith => __$TileTokenDtoCopyWithImpl<_TileTokenDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TileTokenDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TileTokenDto&&(identical(other.token, token) || other.token == token)&&(identical(other.expiresAt, expiresAt) || other.expiresAt == expiresAt)&&(identical(other.expiresIn, expiresIn) || other.expiresIn == expiresIn));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,token,expiresAt,expiresIn);
}

@override
String toString() {
    return 'TileTokenDto(token: $token, expiresAt: $expiresAt, expiresIn: $expiresIn)';
}


}

/// @nodoc
abstract mixin class _$TileTokenDtoCopyWith<$Res> implements $TileTokenDtoCopyWith<$Res> {
  factory _$TileTokenDtoCopyWith(_TileTokenDto value, $Res Function(_TileTokenDto) _then) = __$TileTokenDtoCopyWithImpl;
@override @useResult
$Res call({
 String token, String expiresAt, int expiresIn
});




}
/// @nodoc
class __$TileTokenDtoCopyWithImpl<$Res>
    implements _$TileTokenDtoCopyWith<$Res> {
  __$TileTokenDtoCopyWithImpl(this._self, this._then);

  final _TileTokenDto _self;
  final $Res Function(_TileTokenDto) _then;

/// Create a copy of TileTokenDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? token = null,Object? expiresAt = null,Object? expiresIn = null,}) {
  return _then(_TileTokenDto(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,expiresAt: null == expiresAt ? _self.expiresAt : expiresAt // ignore: cast_nullable_to_non_nullable
as String,expiresIn: null == expiresIn ? _self.expiresIn : expiresIn // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
