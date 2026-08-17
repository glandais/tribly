// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'route_upload_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouteUploadResponse {

/// Whether the upload was successful
 bool get success;/// Error message if upload failed
 String? get message;/// External route ID on the GPS service
 String? get externalRouteId;
/// Create a copy of RouteUploadResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouteUploadResponseCopyWith<RouteUploadResponse> get copyWith => _$RouteUploadResponseCopyWithImpl<RouteUploadResponse>(this as RouteUploadResponse, _$identity);

  /// Serializes this RouteUploadResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouteUploadResponse&&(identical(other.success, success) || other.success == success)&&(identical(other.message, message) || other.message == message)&&(identical(other.externalRouteId, externalRouteId) || other.externalRouteId == externalRouteId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,success,message,externalRouteId);

@override
String toString() {
  return 'RouteUploadResponse(success: $success, message: $message, externalRouteId: $externalRouteId)';
}


}

/// @nodoc
abstract mixin class $RouteUploadResponseCopyWith<$Res>  {
  factory $RouteUploadResponseCopyWith(RouteUploadResponse value, $Res Function(RouteUploadResponse) _then) = _$RouteUploadResponseCopyWithImpl;
@useResult
$Res call({
 bool success, String? message, String? externalRouteId
});




}
/// @nodoc
class _$RouteUploadResponseCopyWithImpl<$Res>
    implements $RouteUploadResponseCopyWith<$Res> {
  _$RouteUploadResponseCopyWithImpl(this._self, this._then);

  final RouteUploadResponse _self;
  final $Res Function(RouteUploadResponse) _then;

/// Create a copy of RouteUploadResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? success = null,Object? message = freezed,Object? externalRouteId = freezed,}) {
  return _then(RouteUploadResponse(
success: null == success ? _self.success : success // ignore: cast_nullable_to_non_nullable
as bool,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,externalRouteId: freezed == externalRouteId ? _self.externalRouteId : externalRouteId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [RouteUploadResponse].
extension RouteUploadResponsePatterns on RouteUploadResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouteUploadResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouteUploadResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouteUploadResponse value)  $default,){
final _that = this;
switch (_that) {
case _RouteUploadResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouteUploadResponse value)?  $default,){
final _that = this;
switch (_that) {
case _RouteUploadResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( bool success,  String? message,  String? externalRouteId)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouteUploadResponse() when $default != null:
return $default(_that.success,_that.message,_that.externalRouteId);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( bool success,  String? message,  String? externalRouteId)  $default,) {final _that = this;
switch (_that) {
case _RouteUploadResponse():
return $default(_that.success,_that.message,_that.externalRouteId);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( bool success,  String? message,  String? externalRouteId)?  $default,) {final _that = this;
switch (_that) {
case _RouteUploadResponse() when $default != null:
return $default(_that.success,_that.message,_that.externalRouteId);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouteUploadResponse implements RouteUploadResponse {
  const _RouteUploadResponse({required this.success, this.message, this.externalRouteId});
  factory _RouteUploadResponse.fromJson(Map<String, dynamic> json) => _$RouteUploadResponseFromJson(json);

/// Whether the upload was successful
@override final  bool success;
/// Error message if upload failed
@override final  String? message;
/// External route ID on the GPS service
@override final  String? externalRouteId;

/// Create a copy of RouteUploadResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouteUploadResponseCopyWith<_RouteUploadResponse> get copyWith => __$RouteUploadResponseCopyWithImpl<_RouteUploadResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouteUploadResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouteUploadResponse&&(identical(other.success, success) || other.success == success)&&(identical(other.message, message) || other.message == message)&&(identical(other.externalRouteId, externalRouteId) || other.externalRouteId == externalRouteId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,success,message,externalRouteId);

@override
String toString() {
  return 'RouteUploadResponse(success: $success, message: $message, externalRouteId: $externalRouteId)';
}


}

/// @nodoc
abstract mixin class _$RouteUploadResponseCopyWith<$Res> implements $RouteUploadResponseCopyWith<$Res> {
  factory _$RouteUploadResponseCopyWith(_RouteUploadResponse value, $Res Function(_RouteUploadResponse) _then) = __$RouteUploadResponseCopyWithImpl;
@override @useResult
$Res call({
 bool success, String? message, String? externalRouteId
});




}
/// @nodoc
class __$RouteUploadResponseCopyWithImpl<$Res>
    implements _$RouteUploadResponseCopyWith<$Res> {
  __$RouteUploadResponseCopyWithImpl(this._self, this._then);

  final _RouteUploadResponse _self;
  final $Res Function(_RouteUploadResponse) _then;

/// Create a copy of RouteUploadResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? success = null,Object? message = freezed,Object? externalRouteId = freezed,}) {
  return _then(_RouteUploadResponse(
success: null == success ? _self.success : success // ignore: cast_nullable_to_non_nullable
as bool,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,externalRouteId: freezed == externalRouteId ? _self.externalRouteId : externalRouteId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
