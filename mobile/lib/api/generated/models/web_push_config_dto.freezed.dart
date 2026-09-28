// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'web_push_config_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$WebPushConfigDto {

/// Firebase web API key
 String get apiKey;/// Firebase project id, the one the server sends through
 String get projectId;/// Firebase web app id
 String get appId;/// FCM sender id (the project number)
 String get messagingSenderId;/// Public VAPID key of the project's web push certificate
 String get vapidKey;
/// Create a copy of WebPushConfigDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$WebPushConfigDtoCopyWith<WebPushConfigDto> get copyWith => _$WebPushConfigDtoCopyWithImpl<WebPushConfigDto>(this as WebPushConfigDto, _$identity);

  /// Serializes this WebPushConfigDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as WebPushConfigDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is WebPushConfigDto&&(identical(other.apiKey, _this.apiKey) || other.apiKey == _this.apiKey)&&(identical(other.projectId, _this.projectId) || other.projectId == _this.projectId)&&(identical(other.appId, _this.appId) || other.appId == _this.appId)&&(identical(other.messagingSenderId, _this.messagingSenderId) || other.messagingSenderId == _this.messagingSenderId)&&(identical(other.vapidKey, _this.vapidKey) || other.vapidKey == _this.vapidKey));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as WebPushConfigDto;
  return Object.hash(runtimeType,_this.apiKey,_this.projectId,_this.appId,_this.messagingSenderId,_this.vapidKey);
}

@override
String toString() {
  final _this = this as WebPushConfigDto;
  return 'WebPushConfigDto(apiKey: ${_this.apiKey}, projectId: ${_this.projectId}, appId: ${_this.appId}, messagingSenderId: ${_this.messagingSenderId}, vapidKey: ${_this.vapidKey})';
}


}

/// @nodoc
abstract mixin class $WebPushConfigDtoCopyWith<$Res>  {
  factory $WebPushConfigDtoCopyWith(WebPushConfigDto value, $Res Function(WebPushConfigDto) _then) = _$WebPushConfigDtoCopyWithImpl;
@useResult
$Res call({
 String apiKey, String projectId, String appId, String messagingSenderId, String vapidKey
});




}
/// @nodoc
class _$WebPushConfigDtoCopyWithImpl<$Res>
    implements $WebPushConfigDtoCopyWith<$Res> {
  _$WebPushConfigDtoCopyWithImpl(this._self, this._then);

  final WebPushConfigDto _self;
  final $Res Function(WebPushConfigDto) _then;

/// Create a copy of WebPushConfigDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? apiKey = null,Object? projectId = null,Object? appId = null,Object? messagingSenderId = null,Object? vapidKey = null,}) {
  return _then(WebPushConfigDto(
apiKey: null == apiKey ? _self.apiKey : apiKey // ignore: cast_nullable_to_non_nullable
as String,projectId: null == projectId ? _self.projectId : projectId // ignore: cast_nullable_to_non_nullable
as String,appId: null == appId ? _self.appId : appId // ignore: cast_nullable_to_non_nullable
as String,messagingSenderId: null == messagingSenderId ? _self.messagingSenderId : messagingSenderId // ignore: cast_nullable_to_non_nullable
as String,vapidKey: null == vapidKey ? _self.vapidKey : vapidKey // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [WebPushConfigDto].
extension WebPushConfigDtoPatterns on WebPushConfigDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _WebPushConfigDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _WebPushConfigDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _WebPushConfigDto value)  $default,){
final _that = this;
switch (_that) {
case _WebPushConfigDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _WebPushConfigDto value)?  $default,){
final _that = this;
switch (_that) {
case _WebPushConfigDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String apiKey,  String projectId,  String appId,  String messagingSenderId,  String vapidKey)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _WebPushConfigDto() when $default != null:
return $default(_that.apiKey,_that.projectId,_that.appId,_that.messagingSenderId,_that.vapidKey);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String apiKey,  String projectId,  String appId,  String messagingSenderId,  String vapidKey)  $default,) {final _that = this;
switch (_that) {
case _WebPushConfigDto():
return $default(_that.apiKey,_that.projectId,_that.appId,_that.messagingSenderId,_that.vapidKey);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String apiKey,  String projectId,  String appId,  String messagingSenderId,  String vapidKey)?  $default,) {final _that = this;
switch (_that) {
case _WebPushConfigDto() when $default != null:
return $default(_that.apiKey,_that.projectId,_that.appId,_that.messagingSenderId,_that.vapidKey);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _WebPushConfigDto implements WebPushConfigDto {
  const _WebPushConfigDto({required this.apiKey, required this.projectId, required this.appId, required this.messagingSenderId, required this.vapidKey});
  factory _WebPushConfigDto.fromJson(Map<String, dynamic> json) => _$WebPushConfigDtoFromJson(json);

/// Firebase web API key
@override final  String apiKey;
/// Firebase project id, the one the server sends through
@override final  String projectId;
/// Firebase web app id
@override final  String appId;
/// FCM sender id (the project number)
@override final  String messagingSenderId;
/// Public VAPID key of the project's web push certificate
@override final  String vapidKey;

/// Create a copy of WebPushConfigDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$WebPushConfigDtoCopyWith<_WebPushConfigDto> get copyWith => __$WebPushConfigDtoCopyWithImpl<_WebPushConfigDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$WebPushConfigDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _WebPushConfigDto&&(identical(other.apiKey, apiKey) || other.apiKey == apiKey)&&(identical(other.projectId, projectId) || other.projectId == projectId)&&(identical(other.appId, appId) || other.appId == appId)&&(identical(other.messagingSenderId, messagingSenderId) || other.messagingSenderId == messagingSenderId)&&(identical(other.vapidKey, vapidKey) || other.vapidKey == vapidKey));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,apiKey,projectId,appId,messagingSenderId,vapidKey);
}

@override
String toString() {
    return 'WebPushConfigDto(apiKey: $apiKey, projectId: $projectId, appId: $appId, messagingSenderId: $messagingSenderId, vapidKey: $vapidKey)';
}


}

/// @nodoc
abstract mixin class _$WebPushConfigDtoCopyWith<$Res> implements $WebPushConfigDtoCopyWith<$Res> {
  factory _$WebPushConfigDtoCopyWith(_WebPushConfigDto value, $Res Function(_WebPushConfigDto) _then) = __$WebPushConfigDtoCopyWithImpl;
@override @useResult
$Res call({
 String apiKey, String projectId, String appId, String messagingSenderId, String vapidKey
});




}
/// @nodoc
class __$WebPushConfigDtoCopyWithImpl<$Res>
    implements _$WebPushConfigDtoCopyWith<$Res> {
  __$WebPushConfigDtoCopyWithImpl(this._self, this._then);

  final _WebPushConfigDto _self;
  final $Res Function(_WebPushConfigDto) _then;

/// Create a copy of WebPushConfigDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? apiKey = null,Object? projectId = null,Object? appId = null,Object? messagingSenderId = null,Object? vapidKey = null,}) {
  return _then(_WebPushConfigDto(
apiKey: null == apiKey ? _self.apiKey : apiKey // ignore: cast_nullable_to_non_nullable
as String,projectId: null == projectId ? _self.projectId : projectId // ignore: cast_nullable_to_non_nullable
as String,appId: null == appId ? _self.appId : appId // ignore: cast_nullable_to_non_nullable
as String,messagingSenderId: null == messagingSenderId ? _self.messagingSenderId : messagingSenderId // ignore: cast_nullable_to_non_nullable
as String,vapidKey: null == vapidKey ? _self.vapidKey : vapidKey // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
