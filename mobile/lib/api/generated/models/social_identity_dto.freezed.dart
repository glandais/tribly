// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'social_identity_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$SocialIdentityDto {

/// Provider identifier
 String get provider;/// Display name of the provider
 String get displayName;/// When the identity was linked
 String get linkedAt;
/// Create a copy of SocialIdentityDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$SocialIdentityDtoCopyWith<SocialIdentityDto> get copyWith => _$SocialIdentityDtoCopyWithImpl<SocialIdentityDto>(this as SocialIdentityDto, _$identity);

  /// Serializes this SocialIdentityDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is SocialIdentityDto&&(identical(other.provider, provider) || other.provider == provider)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.linkedAt, linkedAt) || other.linkedAt == linkedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,provider,displayName,linkedAt);

@override
String toString() {
  return 'SocialIdentityDto(provider: $provider, displayName: $displayName, linkedAt: $linkedAt)';
}


}

/// @nodoc
abstract mixin class $SocialIdentityDtoCopyWith<$Res>  {
  factory $SocialIdentityDtoCopyWith(SocialIdentityDto value, $Res Function(SocialIdentityDto) _then) = _$SocialIdentityDtoCopyWithImpl;
@useResult
$Res call({
 String provider, String displayName, String linkedAt
});




}
/// @nodoc
class _$SocialIdentityDtoCopyWithImpl<$Res>
    implements $SocialIdentityDtoCopyWith<$Res> {
  _$SocialIdentityDtoCopyWithImpl(this._self, this._then);

  final SocialIdentityDto _self;
  final $Res Function(SocialIdentityDto) _then;

/// Create a copy of SocialIdentityDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? provider = null,Object? displayName = null,Object? linkedAt = null,}) {
  return _then(_self.copyWith(
provider: null == provider ? _self.provider : provider // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,linkedAt: null == linkedAt ? _self.linkedAt : linkedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [SocialIdentityDto].
extension SocialIdentityDtoPatterns on SocialIdentityDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _SocialIdentityDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _SocialIdentityDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _SocialIdentityDto value)  $default,){
final _that = this;
switch (_that) {
case _SocialIdentityDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _SocialIdentityDto value)?  $default,){
final _that = this;
switch (_that) {
case _SocialIdentityDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String provider,  String displayName,  String linkedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _SocialIdentityDto() when $default != null:
return $default(_that.provider,_that.displayName,_that.linkedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String provider,  String displayName,  String linkedAt)  $default,) {final _that = this;
switch (_that) {
case _SocialIdentityDto():
return $default(_that.provider,_that.displayName,_that.linkedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String provider,  String displayName,  String linkedAt)?  $default,) {final _that = this;
switch (_that) {
case _SocialIdentityDto() when $default != null:
return $default(_that.provider,_that.displayName,_that.linkedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _SocialIdentityDto implements SocialIdentityDto {
  const _SocialIdentityDto({required this.provider, required this.displayName, required this.linkedAt});
  factory _SocialIdentityDto.fromJson(Map<String, dynamic> json) => _$SocialIdentityDtoFromJson(json);

/// Provider identifier
@override final  String provider;
/// Display name of the provider
@override final  String displayName;
/// When the identity was linked
@override final  String linkedAt;

/// Create a copy of SocialIdentityDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$SocialIdentityDtoCopyWith<_SocialIdentityDto> get copyWith => __$SocialIdentityDtoCopyWithImpl<_SocialIdentityDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$SocialIdentityDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _SocialIdentityDto&&(identical(other.provider, provider) || other.provider == provider)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.linkedAt, linkedAt) || other.linkedAt == linkedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,provider,displayName,linkedAt);

@override
String toString() {
  return 'SocialIdentityDto(provider: $provider, displayName: $displayName, linkedAt: $linkedAt)';
}


}

/// @nodoc
abstract mixin class _$SocialIdentityDtoCopyWith<$Res> implements $SocialIdentityDtoCopyWith<$Res> {
  factory _$SocialIdentityDtoCopyWith(_SocialIdentityDto value, $Res Function(_SocialIdentityDto) _then) = __$SocialIdentityDtoCopyWithImpl;
@override @useResult
$Res call({
 String provider, String displayName, String linkedAt
});




}
/// @nodoc
class __$SocialIdentityDtoCopyWithImpl<$Res>
    implements _$SocialIdentityDtoCopyWith<$Res> {
  __$SocialIdentityDtoCopyWithImpl(this._self, this._then);

  final _SocialIdentityDto _self;
  final $Res Function(_SocialIdentityDto) _then;

/// Create a copy of SocialIdentityDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? provider = null,Object? displayName = null,Object? linkedAt = null,}) {
  return _then(_SocialIdentityDto(
provider: null == provider ? _self.provider : provider // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,linkedAt: null == linkedAt ? _self.linkedAt : linkedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
