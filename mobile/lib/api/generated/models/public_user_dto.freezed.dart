// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'public_user_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PublicUserDto {

/// User ID (TSID)
 String get id;/// User display name
 String get displayName;/// User avatar URL
 String? get avatarUrl;
/// Create a copy of PublicUserDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<PublicUserDto> get copyWith => _$PublicUserDtoCopyWithImpl<PublicUserDto>(this as PublicUserDto, _$identity);

  /// Serializes this PublicUserDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PublicUserDto&&(identical(other.id, id) || other.id == id)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.avatarUrl, avatarUrl) || other.avatarUrl == avatarUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,displayName,avatarUrl);

@override
String toString() {
  return 'PublicUserDto(id: $id, displayName: $displayName, avatarUrl: $avatarUrl)';
}


}

/// @nodoc
abstract mixin class $PublicUserDtoCopyWith<$Res>  {
  factory $PublicUserDtoCopyWith(PublicUserDto value, $Res Function(PublicUserDto) _then) = _$PublicUserDtoCopyWithImpl;
@useResult
$Res call({
 String id, String displayName, String? avatarUrl
});




}
/// @nodoc
class _$PublicUserDtoCopyWithImpl<$Res>
    implements $PublicUserDtoCopyWith<$Res> {
  _$PublicUserDtoCopyWithImpl(this._self, this._then);

  final PublicUserDto _self;
  final $Res Function(PublicUserDto) _then;

/// Create a copy of PublicUserDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? displayName = null,Object? avatarUrl = freezed,}) {
  return _then(PublicUserDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,avatarUrl: freezed == avatarUrl ? _self.avatarUrl : avatarUrl // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [PublicUserDto].
extension PublicUserDtoPatterns on PublicUserDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PublicUserDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PublicUserDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PublicUserDto value)  $default,){
final _that = this;
switch (_that) {
case _PublicUserDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PublicUserDto value)?  $default,){
final _that = this;
switch (_that) {
case _PublicUserDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String displayName,  String? avatarUrl)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PublicUserDto() when $default != null:
return $default(_that.id,_that.displayName,_that.avatarUrl);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String displayName,  String? avatarUrl)  $default,) {final _that = this;
switch (_that) {
case _PublicUserDto():
return $default(_that.id,_that.displayName,_that.avatarUrl);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String displayName,  String? avatarUrl)?  $default,) {final _that = this;
switch (_that) {
case _PublicUserDto() when $default != null:
return $default(_that.id,_that.displayName,_that.avatarUrl);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PublicUserDto implements PublicUserDto {
  const _PublicUserDto({required this.id, required this.displayName, this.avatarUrl});
  factory _PublicUserDto.fromJson(Map<String, dynamic> json) => _$PublicUserDtoFromJson(json);

/// User ID (TSID)
@override final  String id;
/// User display name
@override final  String displayName;
/// User avatar URL
@override final  String? avatarUrl;

/// Create a copy of PublicUserDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PublicUserDtoCopyWith<_PublicUserDto> get copyWith => __$PublicUserDtoCopyWithImpl<_PublicUserDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PublicUserDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PublicUserDto&&(identical(other.id, id) || other.id == id)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.avatarUrl, avatarUrl) || other.avatarUrl == avatarUrl));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,displayName,avatarUrl);

@override
String toString() {
  return 'PublicUserDto(id: $id, displayName: $displayName, avatarUrl: $avatarUrl)';
}


}

/// @nodoc
abstract mixin class _$PublicUserDtoCopyWith<$Res> implements $PublicUserDtoCopyWith<$Res> {
  factory _$PublicUserDtoCopyWith(_PublicUserDto value, $Res Function(_PublicUserDto) _then) = __$PublicUserDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String displayName, String? avatarUrl
});




}
/// @nodoc
class __$PublicUserDtoCopyWithImpl<$Res>
    implements _$PublicUserDtoCopyWith<$Res> {
  __$PublicUserDtoCopyWithImpl(this._self, this._then);

  final _PublicUserDto _self;
  final $Res Function(_PublicUserDto) _then;

/// Create a copy of PublicUserDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? displayName = null,Object? avatarUrl = freezed,}) {
  return _then(_PublicUserDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,avatarUrl: freezed == avatarUrl ? _self.avatarUrl : avatarUrl // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
