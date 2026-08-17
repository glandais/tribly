// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_gps_credential_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminGpsCredentialDto {

/// Credential ID (TSID)
 String get id;/// GPS service type
 String get serviceType;/// OAuth client ID
 String get clientId;/// Whether credential is active
 bool get active;/// Credential creation timestamp
 String get createdAt;
/// Create a copy of AdminGpsCredentialDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminGpsCredentialDtoCopyWith<AdminGpsCredentialDto> get copyWith => _$AdminGpsCredentialDtoCopyWithImpl<AdminGpsCredentialDto>(this as AdminGpsCredentialDto, _$identity);

  /// Serializes this AdminGpsCredentialDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminGpsCredentialDto&&(identical(other.id, id) || other.id == id)&&(identical(other.serviceType, serviceType) || other.serviceType == serviceType)&&(identical(other.clientId, clientId) || other.clientId == clientId)&&(identical(other.active, active) || other.active == active)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,serviceType,clientId,active,createdAt);

@override
String toString() {
  return 'AdminGpsCredentialDto(id: $id, serviceType: $serviceType, clientId: $clientId, active: $active, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $AdminGpsCredentialDtoCopyWith<$Res>  {
  factory $AdminGpsCredentialDtoCopyWith(AdminGpsCredentialDto value, $Res Function(AdminGpsCredentialDto) _then) = _$AdminGpsCredentialDtoCopyWithImpl;
@useResult
$Res call({
 String id, String serviceType, String clientId, bool active, String createdAt
});




}
/// @nodoc
class _$AdminGpsCredentialDtoCopyWithImpl<$Res>
    implements $AdminGpsCredentialDtoCopyWith<$Res> {
  _$AdminGpsCredentialDtoCopyWithImpl(this._self, this._then);

  final AdminGpsCredentialDto _self;
  final $Res Function(AdminGpsCredentialDto) _then;

/// Create a copy of AdminGpsCredentialDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? serviceType = null,Object? clientId = null,Object? active = null,Object? createdAt = null,}) {
  return _then(AdminGpsCredentialDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,serviceType: null == serviceType ? _self.serviceType : serviceType // ignore: cast_nullable_to_non_nullable
as String,clientId: null == clientId ? _self.clientId : clientId // ignore: cast_nullable_to_non_nullable
as String,active: null == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminGpsCredentialDto].
extension AdminGpsCredentialDtoPatterns on AdminGpsCredentialDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminGpsCredentialDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminGpsCredentialDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminGpsCredentialDto value)  $default,){
final _that = this;
switch (_that) {
case _AdminGpsCredentialDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminGpsCredentialDto value)?  $default,){
final _that = this;
switch (_that) {
case _AdminGpsCredentialDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String serviceType,  String clientId,  bool active,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminGpsCredentialDto() when $default != null:
return $default(_that.id,_that.serviceType,_that.clientId,_that.active,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String serviceType,  String clientId,  bool active,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _AdminGpsCredentialDto():
return $default(_that.id,_that.serviceType,_that.clientId,_that.active,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String serviceType,  String clientId,  bool active,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _AdminGpsCredentialDto() when $default != null:
return $default(_that.id,_that.serviceType,_that.clientId,_that.active,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminGpsCredentialDto implements AdminGpsCredentialDto {
  const _AdminGpsCredentialDto({required this.id, required this.serviceType, required this.clientId, required this.active, required this.createdAt});
  factory _AdminGpsCredentialDto.fromJson(Map<String, dynamic> json) => _$AdminGpsCredentialDtoFromJson(json);

/// Credential ID (TSID)
@override final  String id;
/// GPS service type
@override final  String serviceType;
/// OAuth client ID
@override final  String clientId;
/// Whether credential is active
@override final  bool active;
/// Credential creation timestamp
@override final  String createdAt;

/// Create a copy of AdminGpsCredentialDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminGpsCredentialDtoCopyWith<_AdminGpsCredentialDto> get copyWith => __$AdminGpsCredentialDtoCopyWithImpl<_AdminGpsCredentialDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminGpsCredentialDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminGpsCredentialDto&&(identical(other.id, id) || other.id == id)&&(identical(other.serviceType, serviceType) || other.serviceType == serviceType)&&(identical(other.clientId, clientId) || other.clientId == clientId)&&(identical(other.active, active) || other.active == active)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,serviceType,clientId,active,createdAt);

@override
String toString() {
  return 'AdminGpsCredentialDto(id: $id, serviceType: $serviceType, clientId: $clientId, active: $active, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$AdminGpsCredentialDtoCopyWith<$Res> implements $AdminGpsCredentialDtoCopyWith<$Res> {
  factory _$AdminGpsCredentialDtoCopyWith(_AdminGpsCredentialDto value, $Res Function(_AdminGpsCredentialDto) _then) = __$AdminGpsCredentialDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String serviceType, String clientId, bool active, String createdAt
});




}
/// @nodoc
class __$AdminGpsCredentialDtoCopyWithImpl<$Res>
    implements _$AdminGpsCredentialDtoCopyWith<$Res> {
  __$AdminGpsCredentialDtoCopyWithImpl(this._self, this._then);

  final _AdminGpsCredentialDto _self;
  final $Res Function(_AdminGpsCredentialDto) _then;

/// Create a copy of AdminGpsCredentialDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? serviceType = null,Object? clientId = null,Object? active = null,Object? createdAt = null,}) {
  return _then(_AdminGpsCredentialDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,serviceType: null == serviceType ? _self.serviceType : serviceType // ignore: cast_nullable_to_non_nullable
as String,clientId: null == clientId ? _self.clientId : clientId // ignore: cast_nullable_to_non_nullable
as String,active: null == active ? _self.active : active // ignore: cast_nullable_to_non_nullable
as bool,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
