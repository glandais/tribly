// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'client_error_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ClientErrorDto {

/// Error class, e.g. TypeError or _TypeError
 String get type;/// Error message
 String get message;/// Stack trace, as the client printed it
 String? get stack;
/// Create a copy of ClientErrorDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ClientErrorDtoCopyWith<ClientErrorDto> get copyWith => _$ClientErrorDtoCopyWithImpl<ClientErrorDto>(this as ClientErrorDto, _$identity);

  /// Serializes this ClientErrorDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ClientErrorDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ClientErrorDto&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.message, _this.message) || other.message == _this.message)&&(identical(other.stack, _this.stack) || other.stack == _this.stack));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ClientErrorDto;
  return Object.hash(runtimeType,_this.type,_this.message,_this.stack);
}

@override
String toString() {
  final _this = this as ClientErrorDto;
  return 'ClientErrorDto(type: ${_this.type}, message: ${_this.message}, stack: ${_this.stack})';
}


}

/// @nodoc
abstract mixin class $ClientErrorDtoCopyWith<$Res>  {
  factory $ClientErrorDtoCopyWith(ClientErrorDto value, $Res Function(ClientErrorDto) _then) = _$ClientErrorDtoCopyWithImpl;
@useResult
$Res call({
 String type, String message, String? stack
});




}
/// @nodoc
class _$ClientErrorDtoCopyWithImpl<$Res>
    implements $ClientErrorDtoCopyWith<$Res> {
  _$ClientErrorDtoCopyWithImpl(this._self, this._then);

  final ClientErrorDto _self;
  final $Res Function(ClientErrorDto) _then;

/// Create a copy of ClientErrorDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? message = null,Object? stack = freezed,}) {
  return _then(ClientErrorDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,stack: freezed == stack ? _self.stack : stack // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [ClientErrorDto].
extension ClientErrorDtoPatterns on ClientErrorDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ClientErrorDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ClientErrorDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ClientErrorDto value)  $default,){
final _that = this;
switch (_that) {
case _ClientErrorDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ClientErrorDto value)?  $default,){
final _that = this;
switch (_that) {
case _ClientErrorDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String message,  String? stack)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ClientErrorDto() when $default != null:
return $default(_that.type,_that.message,_that.stack);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String message,  String? stack)  $default,) {final _that = this;
switch (_that) {
case _ClientErrorDto():
return $default(_that.type,_that.message,_that.stack);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String message,  String? stack)?  $default,) {final _that = this;
switch (_that) {
case _ClientErrorDto() when $default != null:
return $default(_that.type,_that.message,_that.stack);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ClientErrorDto implements ClientErrorDto {
  const _ClientErrorDto({required this.type, required this.message, this.stack});
  factory _ClientErrorDto.fromJson(Map<String, dynamic> json) => _$ClientErrorDtoFromJson(json);

/// Error class, e.g. TypeError or _TypeError
@override final  String type;
/// Error message
@override final  String message;
/// Stack trace, as the client printed it
@override final  String? stack;

/// Create a copy of ClientErrorDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ClientErrorDtoCopyWith<_ClientErrorDto> get copyWith => __$ClientErrorDtoCopyWithImpl<_ClientErrorDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ClientErrorDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ClientErrorDto&&(identical(other.type, type) || other.type == type)&&(identical(other.message, message) || other.message == message)&&(identical(other.stack, stack) || other.stack == stack));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,message,stack);
}

@override
String toString() {
    return 'ClientErrorDto(type: $type, message: $message, stack: $stack)';
}


}

/// @nodoc
abstract mixin class _$ClientErrorDtoCopyWith<$Res> implements $ClientErrorDtoCopyWith<$Res> {
  factory _$ClientErrorDtoCopyWith(_ClientErrorDto value, $Res Function(_ClientErrorDto) _then) = __$ClientErrorDtoCopyWithImpl;
@override @useResult
$Res call({
 String type, String message, String? stack
});




}
/// @nodoc
class __$ClientErrorDtoCopyWithImpl<$Res>
    implements _$ClientErrorDtoCopyWith<$Res> {
  __$ClientErrorDtoCopyWithImpl(this._self, this._then);

  final _ClientErrorDto _self;
  final $Res Function(_ClientErrorDto) _then;

/// Create a copy of ClientErrorDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? message = null,Object? stack = freezed,}) {
  return _then(_ClientErrorDto(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,stack: freezed == stack ? _self.stack : stack // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
