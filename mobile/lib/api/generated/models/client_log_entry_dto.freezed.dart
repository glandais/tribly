// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'client_log_entry_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ClientLogEntryDto {

/// When it was logged
 String get ts;/// Severity
 String get level;/// What logged it: console, http, navigation, error…
 String get source;/// The entry, truncated by the client
 String get message;
/// Create a copy of ClientLogEntryDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ClientLogEntryDtoCopyWith<ClientLogEntryDto> get copyWith => _$ClientLogEntryDtoCopyWithImpl<ClientLogEntryDto>(this as ClientLogEntryDto, _$identity);

  /// Serializes this ClientLogEntryDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ClientLogEntryDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ClientLogEntryDto&&(identical(other.ts, _this.ts) || other.ts == _this.ts)&&(identical(other.level, _this.level) || other.level == _this.level)&&(identical(other.source, _this.source) || other.source == _this.source)&&(identical(other.message, _this.message) || other.message == _this.message));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ClientLogEntryDto;
  return Object.hash(runtimeType,_this.ts,_this.level,_this.source,_this.message);
}

@override
String toString() {
  final _this = this as ClientLogEntryDto;
  return 'ClientLogEntryDto(ts: ${_this.ts}, level: ${_this.level}, source: ${_this.source}, message: ${_this.message})';
}


}

/// @nodoc
abstract mixin class $ClientLogEntryDtoCopyWith<$Res>  {
  factory $ClientLogEntryDtoCopyWith(ClientLogEntryDto value, $Res Function(ClientLogEntryDto) _then) = _$ClientLogEntryDtoCopyWithImpl;
@useResult
$Res call({
 String ts, String level, String source, String message
});




}
/// @nodoc
class _$ClientLogEntryDtoCopyWithImpl<$Res>
    implements $ClientLogEntryDtoCopyWith<$Res> {
  _$ClientLogEntryDtoCopyWithImpl(this._self, this._then);

  final ClientLogEntryDto _self;
  final $Res Function(ClientLogEntryDto) _then;

/// Create a copy of ClientLogEntryDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? ts = null,Object? level = null,Object? source = null,Object? message = null,}) {
  return _then(ClientLogEntryDto(
ts: null == ts ? _self.ts : ts // ignore: cast_nullable_to_non_nullable
as String,level: null == level ? _self.level : level // ignore: cast_nullable_to_non_nullable
as String,source: null == source ? _self.source : source // ignore: cast_nullable_to_non_nullable
as String,message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [ClientLogEntryDto].
extension ClientLogEntryDtoPatterns on ClientLogEntryDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ClientLogEntryDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ClientLogEntryDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ClientLogEntryDto value)  $default,){
final _that = this;
switch (_that) {
case _ClientLogEntryDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ClientLogEntryDto value)?  $default,){
final _that = this;
switch (_that) {
case _ClientLogEntryDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String ts,  String level,  String source,  String message)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ClientLogEntryDto() when $default != null:
return $default(_that.ts,_that.level,_that.source,_that.message);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String ts,  String level,  String source,  String message)  $default,) {final _that = this;
switch (_that) {
case _ClientLogEntryDto():
return $default(_that.ts,_that.level,_that.source,_that.message);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String ts,  String level,  String source,  String message)?  $default,) {final _that = this;
switch (_that) {
case _ClientLogEntryDto() when $default != null:
return $default(_that.ts,_that.level,_that.source,_that.message);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ClientLogEntryDto implements ClientLogEntryDto {
  const _ClientLogEntryDto({required this.ts, required this.level, required this.source, required this.message});
  factory _ClientLogEntryDto.fromJson(Map<String, dynamic> json) => _$ClientLogEntryDtoFromJson(json);

/// When it was logged
@override final  String ts;
/// Severity
@override final  String level;
/// What logged it: console, http, navigation, error…
@override final  String source;
/// The entry, truncated by the client
@override final  String message;

/// Create a copy of ClientLogEntryDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ClientLogEntryDtoCopyWith<_ClientLogEntryDto> get copyWith => __$ClientLogEntryDtoCopyWithImpl<_ClientLogEntryDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ClientLogEntryDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ClientLogEntryDto&&(identical(other.ts, ts) || other.ts == ts)&&(identical(other.level, level) || other.level == level)&&(identical(other.source, source) || other.source == source)&&(identical(other.message, message) || other.message == message));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,ts,level,source,message);
}

@override
String toString() {
    return 'ClientLogEntryDto(ts: $ts, level: $level, source: $source, message: $message)';
}


}

/// @nodoc
abstract mixin class _$ClientLogEntryDtoCopyWith<$Res> implements $ClientLogEntryDtoCopyWith<$Res> {
  factory _$ClientLogEntryDtoCopyWith(_ClientLogEntryDto value, $Res Function(_ClientLogEntryDto) _then) = __$ClientLogEntryDtoCopyWithImpl;
@override @useResult
$Res call({
 String ts, String level, String source, String message
});




}
/// @nodoc
class __$ClientLogEntryDtoCopyWithImpl<$Res>
    implements _$ClientLogEntryDtoCopyWith<$Res> {
  __$ClientLogEntryDtoCopyWithImpl(this._self, this._then);

  final _ClientLogEntryDto _self;
  final $Res Function(_ClientLogEntryDto) _then;

/// Create a copy of ClientLogEntryDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? ts = null,Object? level = null,Object? source = null,Object? message = null,}) {
  return _then(_ClientLogEntryDto(
ts: null == ts ? _self.ts : ts // ignore: cast_nullable_to_non_nullable
as String,level: null == level ? _self.level : level // ignore: cast_nullable_to_non_nullable
as String,source: null == source ? _self.source : source // ignore: cast_nullable_to_non_nullable
as String,message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
