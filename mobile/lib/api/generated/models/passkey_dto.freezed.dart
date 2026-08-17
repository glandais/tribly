// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'passkey_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PasskeyDto {

/// Passkey ID
 String? get id;/// Credential ID (base64url)
 String? get credentialId;/// Device name
 String? get deviceName;/// Transport methods
 List<String>? get transports;/// Created timestamp
 String? get createdAt;/// Last used timestamp
 String? get lastUsedAt;
/// Create a copy of PasskeyDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PasskeyDtoCopyWith<PasskeyDto> get copyWith => _$PasskeyDtoCopyWithImpl<PasskeyDto>(this as PasskeyDto, _$identity);

  /// Serializes this PasskeyDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PasskeyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.credentialId, credentialId) || other.credentialId == credentialId)&&(identical(other.deviceName, deviceName) || other.deviceName == deviceName)&&const DeepCollectionEquality().equals(other.transports, transports)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.lastUsedAt, lastUsedAt) || other.lastUsedAt == lastUsedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,credentialId,deviceName,const DeepCollectionEquality().hash(transports),createdAt,lastUsedAt);

@override
String toString() {
  return 'PasskeyDto(id: $id, credentialId: $credentialId, deviceName: $deviceName, transports: $transports, createdAt: $createdAt, lastUsedAt: $lastUsedAt)';
}


}

/// @nodoc
abstract mixin class $PasskeyDtoCopyWith<$Res>  {
  factory $PasskeyDtoCopyWith(PasskeyDto value, $Res Function(PasskeyDto) _then) = _$PasskeyDtoCopyWithImpl;
@useResult
$Res call({
 String? id, String? credentialId, String? deviceName, List<String>? transports, String? createdAt, String? lastUsedAt
});




}
/// @nodoc
class _$PasskeyDtoCopyWithImpl<$Res>
    implements $PasskeyDtoCopyWith<$Res> {
  _$PasskeyDtoCopyWithImpl(this._self, this._then);

  final PasskeyDto _self;
  final $Res Function(PasskeyDto) _then;

/// Create a copy of PasskeyDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = freezed,Object? credentialId = freezed,Object? deviceName = freezed,Object? transports = freezed,Object? createdAt = freezed,Object? lastUsedAt = freezed,}) {
  return _then(PasskeyDto(
id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,credentialId: freezed == credentialId ? _self.credentialId : credentialId // ignore: cast_nullable_to_non_nullable
as String?,deviceName: freezed == deviceName ? _self.deviceName : deviceName // ignore: cast_nullable_to_non_nullable
as String?,transports: freezed == transports ? _self.transports : transports // ignore: cast_nullable_to_non_nullable
as List<String>?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,lastUsedAt: freezed == lastUsedAt ? _self.lastUsedAt : lastUsedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [PasskeyDto].
extension PasskeyDtoPatterns on PasskeyDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PasskeyDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PasskeyDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PasskeyDto value)  $default,){
final _that = this;
switch (_that) {
case _PasskeyDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PasskeyDto value)?  $default,){
final _that = this;
switch (_that) {
case _PasskeyDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String? id,  String? credentialId,  String? deviceName,  List<String>? transports,  String? createdAt,  String? lastUsedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PasskeyDto() when $default != null:
return $default(_that.id,_that.credentialId,_that.deviceName,_that.transports,_that.createdAt,_that.lastUsedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String? id,  String? credentialId,  String? deviceName,  List<String>? transports,  String? createdAt,  String? lastUsedAt)  $default,) {final _that = this;
switch (_that) {
case _PasskeyDto():
return $default(_that.id,_that.credentialId,_that.deviceName,_that.transports,_that.createdAt,_that.lastUsedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String? id,  String? credentialId,  String? deviceName,  List<String>? transports,  String? createdAt,  String? lastUsedAt)?  $default,) {final _that = this;
switch (_that) {
case _PasskeyDto() when $default != null:
return $default(_that.id,_that.credentialId,_that.deviceName,_that.transports,_that.createdAt,_that.lastUsedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PasskeyDto implements PasskeyDto {
  const _PasskeyDto({this.id, this.credentialId, this.deviceName,  List<String>? transports, this.createdAt, this.lastUsedAt}): _transports = transports;
  factory _PasskeyDto.fromJson(Map<String, dynamic> json) => _$PasskeyDtoFromJson(json);

/// Passkey ID
@override final  String? id;
/// Credential ID (base64url)
@override final  String? credentialId;
/// Device name
@override final  String? deviceName;
/// Transport methods
 final  List<String>? _transports;
/// Transport methods
@override List<String>? get transports {
  final value = _transports;
  if (value == null) return null;
  if (_transports is EqualUnmodifiableListView) return _transports;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

/// Created timestamp
@override final  String? createdAt;
/// Last used timestamp
@override final  String? lastUsedAt;

/// Create a copy of PasskeyDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PasskeyDtoCopyWith<_PasskeyDto> get copyWith => __$PasskeyDtoCopyWithImpl<_PasskeyDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PasskeyDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PasskeyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.credentialId, credentialId) || other.credentialId == credentialId)&&(identical(other.deviceName, deviceName) || other.deviceName == deviceName)&&const DeepCollectionEquality().equals(other._transports, _transports)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.lastUsedAt, lastUsedAt) || other.lastUsedAt == lastUsedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,credentialId,deviceName,const DeepCollectionEquality().hash(_transports),createdAt,lastUsedAt);

@override
String toString() {
  return 'PasskeyDto(id: $id, credentialId: $credentialId, deviceName: $deviceName, transports: $transports, createdAt: $createdAt, lastUsedAt: $lastUsedAt)';
}


}

/// @nodoc
abstract mixin class _$PasskeyDtoCopyWith<$Res> implements $PasskeyDtoCopyWith<$Res> {
  factory _$PasskeyDtoCopyWith(_PasskeyDto value, $Res Function(_PasskeyDto) _then) = __$PasskeyDtoCopyWithImpl;
@override @useResult
$Res call({
 String? id, String? credentialId, String? deviceName, List<String>? transports, String? createdAt, String? lastUsedAt
});




}
/// @nodoc
class __$PasskeyDtoCopyWithImpl<$Res>
    implements _$PasskeyDtoCopyWith<$Res> {
  __$PasskeyDtoCopyWithImpl(this._self, this._then);

  final _PasskeyDto _self;
  final $Res Function(_PasskeyDto) _then;

/// Create a copy of PasskeyDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = freezed,Object? credentialId = freezed,Object? deviceName = freezed,Object? transports = freezed,Object? createdAt = freezed,Object? lastUsedAt = freezed,}) {
  return _then(_PasskeyDto(
id: freezed == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String?,credentialId: freezed == credentialId ? _self.credentialId : credentialId // ignore: cast_nullable_to_non_nullable
as String?,deviceName: freezed == deviceName ? _self.deviceName : deviceName // ignore: cast_nullable_to_non_nullable
as String?,transports: freezed == transports ? _self._transports : transports // ignore: cast_nullable_to_non_nullable
as List<String>?,createdAt: freezed == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String?,lastUsedAt: freezed == lastUsedAt ? _self.lastUsedAt : lastUsedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
