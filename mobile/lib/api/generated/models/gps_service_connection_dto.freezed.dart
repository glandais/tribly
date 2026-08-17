// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'gps_service_connection_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GpsServiceConnectionDto {

/// Service type identifier
 String get serviceType;/// Display name of the service
 String get displayName;/// When the service was connected
 String get connectedAt;
/// Create a copy of GpsServiceConnectionDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GpsServiceConnectionDtoCopyWith<GpsServiceConnectionDto> get copyWith => _$GpsServiceConnectionDtoCopyWithImpl<GpsServiceConnectionDto>(this as GpsServiceConnectionDto, _$identity);

  /// Serializes this GpsServiceConnectionDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GpsServiceConnectionDto&&(identical(other.serviceType, serviceType) || other.serviceType == serviceType)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.connectedAt, connectedAt) || other.connectedAt == connectedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,serviceType,displayName,connectedAt);

@override
String toString() {
  return 'GpsServiceConnectionDto(serviceType: $serviceType, displayName: $displayName, connectedAt: $connectedAt)';
}


}

/// @nodoc
abstract mixin class $GpsServiceConnectionDtoCopyWith<$Res>  {
  factory $GpsServiceConnectionDtoCopyWith(GpsServiceConnectionDto value, $Res Function(GpsServiceConnectionDto) _then) = _$GpsServiceConnectionDtoCopyWithImpl;
@useResult
$Res call({
 String serviceType, String displayName, String connectedAt
});




}
/// @nodoc
class _$GpsServiceConnectionDtoCopyWithImpl<$Res>
    implements $GpsServiceConnectionDtoCopyWith<$Res> {
  _$GpsServiceConnectionDtoCopyWithImpl(this._self, this._then);

  final GpsServiceConnectionDto _self;
  final $Res Function(GpsServiceConnectionDto) _then;

/// Create a copy of GpsServiceConnectionDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? serviceType = null,Object? displayName = null,Object? connectedAt = null,}) {
  return _then(GpsServiceConnectionDto(
serviceType: null == serviceType ? _self.serviceType : serviceType // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,connectedAt: null == connectedAt ? _self.connectedAt : connectedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [GpsServiceConnectionDto].
extension GpsServiceConnectionDtoPatterns on GpsServiceConnectionDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GpsServiceConnectionDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GpsServiceConnectionDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GpsServiceConnectionDto value)  $default,){
final _that = this;
switch (_that) {
case _GpsServiceConnectionDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GpsServiceConnectionDto value)?  $default,){
final _that = this;
switch (_that) {
case _GpsServiceConnectionDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String serviceType,  String displayName,  String connectedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GpsServiceConnectionDto() when $default != null:
return $default(_that.serviceType,_that.displayName,_that.connectedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String serviceType,  String displayName,  String connectedAt)  $default,) {final _that = this;
switch (_that) {
case _GpsServiceConnectionDto():
return $default(_that.serviceType,_that.displayName,_that.connectedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String serviceType,  String displayName,  String connectedAt)?  $default,) {final _that = this;
switch (_that) {
case _GpsServiceConnectionDto() when $default != null:
return $default(_that.serviceType,_that.displayName,_that.connectedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GpsServiceConnectionDto implements GpsServiceConnectionDto {
  const _GpsServiceConnectionDto({required this.serviceType, required this.displayName, required this.connectedAt});
  factory _GpsServiceConnectionDto.fromJson(Map<String, dynamic> json) => _$GpsServiceConnectionDtoFromJson(json);

/// Service type identifier
@override final  String serviceType;
/// Display name of the service
@override final  String displayName;
/// When the service was connected
@override final  String connectedAt;

/// Create a copy of GpsServiceConnectionDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GpsServiceConnectionDtoCopyWith<_GpsServiceConnectionDto> get copyWith => __$GpsServiceConnectionDtoCopyWithImpl<_GpsServiceConnectionDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GpsServiceConnectionDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GpsServiceConnectionDto&&(identical(other.serviceType, serviceType) || other.serviceType == serviceType)&&(identical(other.displayName, displayName) || other.displayName == displayName)&&(identical(other.connectedAt, connectedAt) || other.connectedAt == connectedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,serviceType,displayName,connectedAt);

@override
String toString() {
  return 'GpsServiceConnectionDto(serviceType: $serviceType, displayName: $displayName, connectedAt: $connectedAt)';
}


}

/// @nodoc
abstract mixin class _$GpsServiceConnectionDtoCopyWith<$Res> implements $GpsServiceConnectionDtoCopyWith<$Res> {
  factory _$GpsServiceConnectionDtoCopyWith(_GpsServiceConnectionDto value, $Res Function(_GpsServiceConnectionDto) _then) = __$GpsServiceConnectionDtoCopyWithImpl;
@override @useResult
$Res call({
 String serviceType, String displayName, String connectedAt
});




}
/// @nodoc
class __$GpsServiceConnectionDtoCopyWithImpl<$Res>
    implements _$GpsServiceConnectionDtoCopyWith<$Res> {
  __$GpsServiceConnectionDtoCopyWithImpl(this._self, this._then);

  final _GpsServiceConnectionDto _self;
  final $Res Function(_GpsServiceConnectionDto) _then;

/// Create a copy of GpsServiceConnectionDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? serviceType = null,Object? displayName = null,Object? connectedAt = null,}) {
  return _then(_GpsServiceConnectionDto(
serviceType: null == serviceType ? _self.serviceType : serviceType // ignore: cast_nullable_to_non_nullable
as String,displayName: null == displayName ? _self.displayName : displayName // ignore: cast_nullable_to_non_nullable
as String,connectedAt: null == connectedAt ? _self.connectedAt : connectedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
