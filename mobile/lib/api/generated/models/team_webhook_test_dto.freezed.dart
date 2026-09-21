// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_webhook_test_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamWebhookTestDto {

/// Whether the endpoint accepted it (2xx)
 bool get success;/// HTTP status the endpoint answered, if it answered
 int? get statusCode;/// Why it failed, when it did
 String? get error;
/// Create a copy of TeamWebhookTestDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamWebhookTestDtoCopyWith<TeamWebhookTestDto> get copyWith => _$TeamWebhookTestDtoCopyWithImpl<TeamWebhookTestDto>(this as TeamWebhookTestDto, _$identity);

  /// Serializes this TeamWebhookTestDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamWebhookTestDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamWebhookTestDto&&(identical(other.success, _this.success) || other.success == _this.success)&&(identical(other.statusCode, _this.statusCode) || other.statusCode == _this.statusCode)&&(identical(other.error, _this.error) || other.error == _this.error));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamWebhookTestDto;
  return Object.hash(runtimeType,_this.success,_this.statusCode,_this.error);
}

@override
String toString() {
  final _this = this as TeamWebhookTestDto;
  return 'TeamWebhookTestDto(success: ${_this.success}, statusCode: ${_this.statusCode}, error: ${_this.error})';
}


}

/// @nodoc
abstract mixin class $TeamWebhookTestDtoCopyWith<$Res>  {
  factory $TeamWebhookTestDtoCopyWith(TeamWebhookTestDto value, $Res Function(TeamWebhookTestDto) _then) = _$TeamWebhookTestDtoCopyWithImpl;
@useResult
$Res call({
 bool success, int? statusCode, String? error
});




}
/// @nodoc
class _$TeamWebhookTestDtoCopyWithImpl<$Res>
    implements $TeamWebhookTestDtoCopyWith<$Res> {
  _$TeamWebhookTestDtoCopyWithImpl(this._self, this._then);

  final TeamWebhookTestDto _self;
  final $Res Function(TeamWebhookTestDto) _then;

/// Create a copy of TeamWebhookTestDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? success = null,Object? statusCode = freezed,Object? error = freezed,}) {
  return _then(TeamWebhookTestDto(
success: null == success ? _self.success : success // ignore: cast_nullable_to_non_nullable
as bool,statusCode: freezed == statusCode ? _self.statusCode : statusCode // ignore: cast_nullable_to_non_nullable
as int?,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamWebhookTestDto].
extension TeamWebhookTestDtoPatterns on TeamWebhookTestDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamWebhookTestDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamWebhookTestDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamWebhookTestDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamWebhookTestDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamWebhookTestDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamWebhookTestDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( bool success,  int? statusCode,  String? error)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamWebhookTestDto() when $default != null:
return $default(_that.success,_that.statusCode,_that.error);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( bool success,  int? statusCode,  String? error)  $default,) {final _that = this;
switch (_that) {
case _TeamWebhookTestDto():
return $default(_that.success,_that.statusCode,_that.error);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( bool success,  int? statusCode,  String? error)?  $default,) {final _that = this;
switch (_that) {
case _TeamWebhookTestDto() when $default != null:
return $default(_that.success,_that.statusCode,_that.error);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamWebhookTestDto implements TeamWebhookTestDto {
  const _TeamWebhookTestDto({required this.success, this.statusCode, this.error});
  factory _TeamWebhookTestDto.fromJson(Map<String, dynamic> json) => _$TeamWebhookTestDtoFromJson(json);

/// Whether the endpoint accepted it (2xx)
@override final  bool success;
/// HTTP status the endpoint answered, if it answered
@override final  int? statusCode;
/// Why it failed, when it did
@override final  String? error;

/// Create a copy of TeamWebhookTestDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamWebhookTestDtoCopyWith<_TeamWebhookTestDto> get copyWith => __$TeamWebhookTestDtoCopyWithImpl<_TeamWebhookTestDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamWebhookTestDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamWebhookTestDto&&(identical(other.success, success) || other.success == success)&&(identical(other.statusCode, statusCode) || other.statusCode == statusCode)&&(identical(other.error, error) || other.error == error));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,success,statusCode,error);
}

@override
String toString() {
    return 'TeamWebhookTestDto(success: $success, statusCode: $statusCode, error: $error)';
}


}

/// @nodoc
abstract mixin class _$TeamWebhookTestDtoCopyWith<$Res> implements $TeamWebhookTestDtoCopyWith<$Res> {
  factory _$TeamWebhookTestDtoCopyWith(_TeamWebhookTestDto value, $Res Function(_TeamWebhookTestDto) _then) = __$TeamWebhookTestDtoCopyWithImpl;
@override @useResult
$Res call({
 bool success, int? statusCode, String? error
});




}
/// @nodoc
class __$TeamWebhookTestDtoCopyWithImpl<$Res>
    implements _$TeamWebhookTestDtoCopyWith<$Res> {
  __$TeamWebhookTestDtoCopyWithImpl(this._self, this._then);

  final _TeamWebhookTestDto _self;
  final $Res Function(_TeamWebhookTestDto) _then;

/// Create a copy of TeamWebhookTestDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? success = null,Object? statusCode = freezed,Object? error = freezed,}) {
  return _then(_TeamWebhookTestDto(
success: null == success ? _self.success : success // ignore: cast_nullable_to_non_nullable
as bool,statusCode: freezed == statusCode ? _self.statusCode : statusCode // ignore: cast_nullable_to_non_nullable
as int?,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
