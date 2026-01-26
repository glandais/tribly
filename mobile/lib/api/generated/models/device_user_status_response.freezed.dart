// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'device_user_status_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DeviceUserStatusResponse {

/// List of connected GPS services
 List<GpsServiceType> get connectedGpsServices;
/// Create a copy of DeviceUserStatusResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DeviceUserStatusResponseCopyWith<DeviceUserStatusResponse> get copyWith => _$DeviceUserStatusResponseCopyWithImpl<DeviceUserStatusResponse>(this as DeviceUserStatusResponse, _$identity);

  /// Serializes this DeviceUserStatusResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DeviceUserStatusResponse&&const DeepCollectionEquality().equals(other.connectedGpsServices, connectedGpsServices));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(connectedGpsServices));

@override
String toString() {
  return 'DeviceUserStatusResponse(connectedGpsServices: $connectedGpsServices)';
}


}

/// @nodoc
abstract mixin class $DeviceUserStatusResponseCopyWith<$Res>  {
  factory $DeviceUserStatusResponseCopyWith(DeviceUserStatusResponse value, $Res Function(DeviceUserStatusResponse) _then) = _$DeviceUserStatusResponseCopyWithImpl;
@useResult
$Res call({
 List<GpsServiceType> connectedGpsServices
});




}
/// @nodoc
class _$DeviceUserStatusResponseCopyWithImpl<$Res>
    implements $DeviceUserStatusResponseCopyWith<$Res> {
  _$DeviceUserStatusResponseCopyWithImpl(this._self, this._then);

  final DeviceUserStatusResponse _self;
  final $Res Function(DeviceUserStatusResponse) _then;

/// Create a copy of DeviceUserStatusResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? connectedGpsServices = null,}) {
  return _then(_self.copyWith(
connectedGpsServices: null == connectedGpsServices ? _self.connectedGpsServices : connectedGpsServices // ignore: cast_nullable_to_non_nullable
as List<GpsServiceType>,
  ));
}

}


/// Adds pattern-matching-related methods to [DeviceUserStatusResponse].
extension DeviceUserStatusResponsePatterns on DeviceUserStatusResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DeviceUserStatusResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DeviceUserStatusResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DeviceUserStatusResponse value)  $default,){
final _that = this;
switch (_that) {
case _DeviceUserStatusResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DeviceUserStatusResponse value)?  $default,){
final _that = this;
switch (_that) {
case _DeviceUserStatusResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<GpsServiceType> connectedGpsServices)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _DeviceUserStatusResponse() when $default != null:
return $default(_that.connectedGpsServices);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<GpsServiceType> connectedGpsServices)  $default,) {final _that = this;
switch (_that) {
case _DeviceUserStatusResponse():
return $default(_that.connectedGpsServices);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<GpsServiceType> connectedGpsServices)?  $default,) {final _that = this;
switch (_that) {
case _DeviceUserStatusResponse() when $default != null:
return $default(_that.connectedGpsServices);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DeviceUserStatusResponse implements DeviceUserStatusResponse {
  const _DeviceUserStatusResponse({required final  List<GpsServiceType> connectedGpsServices}): _connectedGpsServices = connectedGpsServices;
  factory _DeviceUserStatusResponse.fromJson(Map<String, dynamic> json) => _$DeviceUserStatusResponseFromJson(json);

/// List of connected GPS services
 final  List<GpsServiceType> _connectedGpsServices;
/// List of connected GPS services
@override List<GpsServiceType> get connectedGpsServices {
  if (_connectedGpsServices is EqualUnmodifiableListView) return _connectedGpsServices;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_connectedGpsServices);
}


/// Create a copy of DeviceUserStatusResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DeviceUserStatusResponseCopyWith<_DeviceUserStatusResponse> get copyWith => __$DeviceUserStatusResponseCopyWithImpl<_DeviceUserStatusResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DeviceUserStatusResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _DeviceUserStatusResponse&&const DeepCollectionEquality().equals(other._connectedGpsServices, _connectedGpsServices));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_connectedGpsServices));

@override
String toString() {
  return 'DeviceUserStatusResponse(connectedGpsServices: $connectedGpsServices)';
}


}

/// @nodoc
abstract mixin class _$DeviceUserStatusResponseCopyWith<$Res> implements $DeviceUserStatusResponseCopyWith<$Res> {
  factory _$DeviceUserStatusResponseCopyWith(_DeviceUserStatusResponse value, $Res Function(_DeviceUserStatusResponse) _then) = __$DeviceUserStatusResponseCopyWithImpl;
@override @useResult
$Res call({
 List<GpsServiceType> connectedGpsServices
});




}
/// @nodoc
class __$DeviceUserStatusResponseCopyWithImpl<$Res>
    implements _$DeviceUserStatusResponseCopyWith<$Res> {
  __$DeviceUserStatusResponseCopyWithImpl(this._self, this._then);

  final _DeviceUserStatusResponse _self;
  final $Res Function(_DeviceUserStatusResponse) _then;

/// Create a copy of DeviceUserStatusResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? connectedGpsServices = null,}) {
  return _then(_DeviceUserStatusResponse(
connectedGpsServices: null == connectedGpsServices ? _self._connectedGpsServices : connectedGpsServices // ignore: cast_nullable_to_non_nullable
as List<GpsServiceType>,
  ));
}


}

// dart format on
