// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'beta_signup_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BetaSignupDto {

/// Sign-up ID
 String get id;/// Email
 String get email;/// When the sign-up was submitted
 String get createdAt;
/// Create a copy of BetaSignupDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BetaSignupDtoCopyWith<BetaSignupDto> get copyWith => _$BetaSignupDtoCopyWithImpl<BetaSignupDto>(this as BetaSignupDto, _$identity);

  /// Serializes this BetaSignupDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as BetaSignupDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BetaSignupDto&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.email, _this.email) || other.email == _this.email)&&(identical(other.createdAt, _this.createdAt) || other.createdAt == _this.createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as BetaSignupDto;
  return Object.hash(runtimeType,_this.id,_this.email,_this.createdAt);
}

@override
String toString() {
  final _this = this as BetaSignupDto;
  return 'BetaSignupDto(id: ${_this.id}, email: ${_this.email}, createdAt: ${_this.createdAt})';
}


}

/// @nodoc
abstract mixin class $BetaSignupDtoCopyWith<$Res>  {
  factory $BetaSignupDtoCopyWith(BetaSignupDto value, $Res Function(BetaSignupDto) _then) = _$BetaSignupDtoCopyWithImpl;
@useResult
$Res call({
 String id, String email, String createdAt
});




}
/// @nodoc
class _$BetaSignupDtoCopyWithImpl<$Res>
    implements $BetaSignupDtoCopyWith<$Res> {
  _$BetaSignupDtoCopyWithImpl(this._self, this._then);

  final BetaSignupDto _self;
  final $Res Function(BetaSignupDto) _then;

/// Create a copy of BetaSignupDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? email = null,Object? createdAt = null,}) {
  return _then(BetaSignupDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [BetaSignupDto].
extension BetaSignupDtoPatterns on BetaSignupDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BetaSignupDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BetaSignupDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BetaSignupDto value)  $default,){
final _that = this;
switch (_that) {
case _BetaSignupDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BetaSignupDto value)?  $default,){
final _that = this;
switch (_that) {
case _BetaSignupDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String email,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BetaSignupDto() when $default != null:
return $default(_that.id,_that.email,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String email,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _BetaSignupDto():
return $default(_that.id,_that.email,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String email,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _BetaSignupDto() when $default != null:
return $default(_that.id,_that.email,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BetaSignupDto implements BetaSignupDto {
  const _BetaSignupDto({required this.id, required this.email, required this.createdAt});
  factory _BetaSignupDto.fromJson(Map<String, dynamic> json) => _$BetaSignupDtoFromJson(json);

/// Sign-up ID
@override final  String id;
/// Email
@override final  String email;
/// When the sign-up was submitted
@override final  String createdAt;

/// Create a copy of BetaSignupDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BetaSignupDtoCopyWith<_BetaSignupDto> get copyWith => __$BetaSignupDtoCopyWithImpl<_BetaSignupDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BetaSignupDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _BetaSignupDto&&(identical(other.id, id) || other.id == id)&&(identical(other.email, email) || other.email == email)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,id,email,createdAt);
}

@override
String toString() {
    return 'BetaSignupDto(id: $id, email: $email, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$BetaSignupDtoCopyWith<$Res> implements $BetaSignupDtoCopyWith<$Res> {
  factory _$BetaSignupDtoCopyWith(_BetaSignupDto value, $Res Function(_BetaSignupDto) _then) = __$BetaSignupDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String email, String createdAt
});




}
/// @nodoc
class __$BetaSignupDtoCopyWithImpl<$Res>
    implements _$BetaSignupDtoCopyWith<$Res> {
  __$BetaSignupDtoCopyWithImpl(this._self, this._then);

  final _BetaSignupDto _self;
  final $Res Function(_BetaSignupDto) _then;

/// Create a copy of BetaSignupDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? email = null,Object? createdAt = null,}) {
  return _then(_BetaSignupDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
