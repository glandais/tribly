// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'error_report_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ErrorReportRequest {

/// Client, device and screen
 ClientContextDto get context;/// The error
 ClientErrorDto get error;/// The client's recent log, oldest first
 List<ClientLogEntryDto>? get logs;
/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ErrorReportRequestCopyWith<ErrorReportRequest> get copyWith => _$ErrorReportRequestCopyWithImpl<ErrorReportRequest>(this as ErrorReportRequest, _$identity);

  /// Serializes this ErrorReportRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ErrorReportRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ErrorReportRequest&&(identical(other.context, _this.context) || other.context == _this.context)&&(identical(other.error, _this.error) || other.error == _this.error)&&const DeepCollectionEquality().equals(other.logs, _this.logs));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ErrorReportRequest;
  return Object.hash(runtimeType,_this.context,_this.error,const DeepCollectionEquality().hash(_this.logs));
}

@override
String toString() {
  final _this = this as ErrorReportRequest;
  return 'ErrorReportRequest(context: ${_this.context}, error: ${_this.error}, logs: ${_this.logs})';
}


}

/// @nodoc
abstract mixin class $ErrorReportRequestCopyWith<$Res>  {
  factory $ErrorReportRequestCopyWith(ErrorReportRequest value, $Res Function(ErrorReportRequest) _then) = _$ErrorReportRequestCopyWithImpl;
@useResult
$Res call({
 ClientContextDto context, ClientErrorDto error, List<ClientLogEntryDto>? logs
});


$ClientContextDtoCopyWith<$Res> get context;$ClientErrorDtoCopyWith<$Res> get error;

}
/// @nodoc
class _$ErrorReportRequestCopyWithImpl<$Res>
    implements $ErrorReportRequestCopyWith<$Res> {
  _$ErrorReportRequestCopyWithImpl(this._self, this._then);

  final ErrorReportRequest _self;
  final $Res Function(ErrorReportRequest) _then;

/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? context = null,Object? error = null,Object? logs = freezed,}) {
  return _then(ErrorReportRequest(
context: null == context ? _self.context : context // ignore: cast_nullable_to_non_nullable
as ClientContextDto,error: null == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as ClientErrorDto,logs: freezed == logs ? _self.logs : logs // ignore: cast_nullable_to_non_nullable
as List<ClientLogEntryDto>?,
  ));
}
/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientContextDtoCopyWith<$Res> get context {
  
  return $ClientContextDtoCopyWith<$Res>(_self.context, (value) {
    return _then(_self.copyWith(context: value));
  });
}/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientErrorDtoCopyWith<$Res> get error {
  
  return $ClientErrorDtoCopyWith<$Res>(_self.error, (value) {
    return _then(_self.copyWith(error: value));
  });
}
}


/// Adds pattern-matching-related methods to [ErrorReportRequest].
extension ErrorReportRequestPatterns on ErrorReportRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ErrorReportRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ErrorReportRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ErrorReportRequest value)  $default,){
final _that = this;
switch (_that) {
case _ErrorReportRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ErrorReportRequest value)?  $default,){
final _that = this;
switch (_that) {
case _ErrorReportRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( ClientContextDto context,  ClientErrorDto error,  List<ClientLogEntryDto>? logs)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ErrorReportRequest() when $default != null:
return $default(_that.context,_that.error,_that.logs);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( ClientContextDto context,  ClientErrorDto error,  List<ClientLogEntryDto>? logs)  $default,) {final _that = this;
switch (_that) {
case _ErrorReportRequest():
return $default(_that.context,_that.error,_that.logs);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( ClientContextDto context,  ClientErrorDto error,  List<ClientLogEntryDto>? logs)?  $default,) {final _that = this;
switch (_that) {
case _ErrorReportRequest() when $default != null:
return $default(_that.context,_that.error,_that.logs);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ErrorReportRequest implements ErrorReportRequest {
  const _ErrorReportRequest({required this.context, required this.error,  List<ClientLogEntryDto>? logs}): _logs = logs;
  factory _ErrorReportRequest.fromJson(Map<String, dynamic> json) => _$ErrorReportRequestFromJson(json);

/// Client, device and screen
@override final  ClientContextDto context;
/// The error
@override final  ClientErrorDto error;
/// The client's recent log, oldest first
 final  List<ClientLogEntryDto>? _logs;
/// The client's recent log, oldest first
@override List<ClientLogEntryDto>? get logs {
  final value = _logs;
  if (value == null) return null;
  if (_logs is EqualUnmodifiableListView) return _logs;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}


/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ErrorReportRequestCopyWith<_ErrorReportRequest> get copyWith => __$ErrorReportRequestCopyWithImpl<_ErrorReportRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ErrorReportRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ErrorReportRequest&&(identical(other.context, context) || other.context == context)&&(identical(other.error, error) || other.error == error)&&const DeepCollectionEquality().equals(other.logs, _logs));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,context,error,const DeepCollectionEquality().hash(_logs));
}

@override
String toString() {
    return 'ErrorReportRequest(context: $context, error: $error, logs: $logs)';
}


}

/// @nodoc
abstract mixin class _$ErrorReportRequestCopyWith<$Res> implements $ErrorReportRequestCopyWith<$Res> {
  factory _$ErrorReportRequestCopyWith(_ErrorReportRequest value, $Res Function(_ErrorReportRequest) _then) = __$ErrorReportRequestCopyWithImpl;
@override @useResult
$Res call({
 ClientContextDto context, ClientErrorDto error, List<ClientLogEntryDto>? logs
});


@override $ClientContextDtoCopyWith<$Res> get context;@override $ClientErrorDtoCopyWith<$Res> get error;

}
/// @nodoc
class __$ErrorReportRequestCopyWithImpl<$Res>
    implements _$ErrorReportRequestCopyWith<$Res> {
  __$ErrorReportRequestCopyWithImpl(this._self, this._then);

  final _ErrorReportRequest _self;
  final $Res Function(_ErrorReportRequest) _then;

/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? context = null,Object? error = null,Object? logs = freezed,}) {
  return _then(_ErrorReportRequest(
context: null == context ? _self.context : context // ignore: cast_nullable_to_non_nullable
as ClientContextDto,error: null == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as ClientErrorDto,logs: freezed == logs ? _self._logs : logs // ignore: cast_nullable_to_non_nullable
as List<ClientLogEntryDto>?,
  ));
}

/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientContextDtoCopyWith<$Res> get context {
  
  return $ClientContextDtoCopyWith<$Res>(_self.context, (value) {
    return _then(_self.copyWith(context: value));
  });
}/// Create a copy of ErrorReportRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientErrorDtoCopyWith<$Res> get error {
  
  return $ClientErrorDtoCopyWith<$Res>(_self.error, (value) {
    return _then(_self.copyWith(error: value));
  });
}
}

// dart format on
