// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'feedback_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$FeedbackRequest {

/// Bug or suggestion
 String get kind;/// What happened, in the member's words
 String get message;/// Client, device and screen
 ClientContextDto get context;/// The unhandled error the report was opened from, if any. Links the report to the automatic error report of the same error.
 ClientErrorDto? get error;/// The client's recent log, oldest first. Absent when the member chose not to attach technical details.
 List<ClientLogEntryDto>? get logs;
/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$FeedbackRequestCopyWith<FeedbackRequest> get copyWith => _$FeedbackRequestCopyWithImpl<FeedbackRequest>(this as FeedbackRequest, _$identity);

  /// Serializes this FeedbackRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as FeedbackRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is FeedbackRequest&&(identical(other.kind, _this.kind) || other.kind == _this.kind)&&(identical(other.message, _this.message) || other.message == _this.message)&&(identical(other.context, _this.context) || other.context == _this.context)&&(identical(other.error, _this.error) || other.error == _this.error)&&const DeepCollectionEquality().equals(other.logs, _this.logs));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as FeedbackRequest;
  return Object.hash(runtimeType,_this.kind,_this.message,_this.context,_this.error,const DeepCollectionEquality().hash(_this.logs));
}

@override
String toString() {
  final _this = this as FeedbackRequest;
  return 'FeedbackRequest(kind: ${_this.kind}, message: ${_this.message}, context: ${_this.context}, error: ${_this.error}, logs: ${_this.logs})';
}


}

/// @nodoc
abstract mixin class $FeedbackRequestCopyWith<$Res>  {
  factory $FeedbackRequestCopyWith(FeedbackRequest value, $Res Function(FeedbackRequest) _then) = _$FeedbackRequestCopyWithImpl;
@useResult
$Res call({
 String kind, String message, ClientContextDto context, ClientErrorDto? error, List<ClientLogEntryDto>? logs
});


$ClientContextDtoCopyWith<$Res> get context;$ClientErrorDtoCopyWith<$Res>? get error;

}
/// @nodoc
class _$FeedbackRequestCopyWithImpl<$Res>
    implements $FeedbackRequestCopyWith<$Res> {
  _$FeedbackRequestCopyWithImpl(this._self, this._then);

  final FeedbackRequest _self;
  final $Res Function(FeedbackRequest) _then;

/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? kind = null,Object? message = null,Object? context = null,Object? error = freezed,Object? logs = freezed,}) {
  return _then(FeedbackRequest(
kind: null == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String,message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,context: null == context ? _self.context : context // ignore: cast_nullable_to_non_nullable
as ClientContextDto,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as ClientErrorDto?,logs: freezed == logs ? _self.logs : logs // ignore: cast_nullable_to_non_nullable
as List<ClientLogEntryDto>?,
  ));
}
/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientContextDtoCopyWith<$Res> get context {
  
  return $ClientContextDtoCopyWith<$Res>(_self.context, (value) {
    return _then(_self.copyWith(context: value));
  });
}/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientErrorDtoCopyWith<$Res>? get error {
    if (_self.error == null) {
    return null;
  }

  return $ClientErrorDtoCopyWith<$Res>(_self.error!, (value) {
    return _then(_self.copyWith(error: value));
  });
}
}


/// Adds pattern-matching-related methods to [FeedbackRequest].
extension FeedbackRequestPatterns on FeedbackRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _FeedbackRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _FeedbackRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _FeedbackRequest value)  $default,){
final _that = this;
switch (_that) {
case _FeedbackRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _FeedbackRequest value)?  $default,){
final _that = this;
switch (_that) {
case _FeedbackRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String kind,  String message,  ClientContextDto context,  ClientErrorDto? error,  List<ClientLogEntryDto>? logs)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _FeedbackRequest() when $default != null:
return $default(_that.kind,_that.message,_that.context,_that.error,_that.logs);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String kind,  String message,  ClientContextDto context,  ClientErrorDto? error,  List<ClientLogEntryDto>? logs)  $default,) {final _that = this;
switch (_that) {
case _FeedbackRequest():
return $default(_that.kind,_that.message,_that.context,_that.error,_that.logs);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String kind,  String message,  ClientContextDto context,  ClientErrorDto? error,  List<ClientLogEntryDto>? logs)?  $default,) {final _that = this;
switch (_that) {
case _FeedbackRequest() when $default != null:
return $default(_that.kind,_that.message,_that.context,_that.error,_that.logs);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _FeedbackRequest implements FeedbackRequest {
  const _FeedbackRequest({required this.kind, required this.message, required this.context, this.error,  List<ClientLogEntryDto>? logs}): _logs = logs;
  factory _FeedbackRequest.fromJson(Map<String, dynamic> json) => _$FeedbackRequestFromJson(json);

/// Bug or suggestion
@override final  String kind;
/// What happened, in the member's words
@override final  String message;
/// Client, device and screen
@override final  ClientContextDto context;
/// The unhandled error the report was opened from, if any. Links the report to the automatic error report of the same error.
@override final  ClientErrorDto? error;
/// The client's recent log, oldest first. Absent when the member chose not to attach technical details.
 final  List<ClientLogEntryDto>? _logs;
/// The client's recent log, oldest first. Absent when the member chose not to attach technical details.
@override List<ClientLogEntryDto>? get logs {
  final value = _logs;
  if (value == null) return null;
  if (_logs is EqualUnmodifiableListView) return _logs;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}


/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$FeedbackRequestCopyWith<_FeedbackRequest> get copyWith => __$FeedbackRequestCopyWithImpl<_FeedbackRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$FeedbackRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _FeedbackRequest&&(identical(other.kind, kind) || other.kind == kind)&&(identical(other.message, message) || other.message == message)&&(identical(other.context, context) || other.context == context)&&(identical(other.error, error) || other.error == error)&&const DeepCollectionEquality().equals(other.logs, _logs));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,kind,message,context,error,const DeepCollectionEquality().hash(_logs));
}

@override
String toString() {
    return 'FeedbackRequest(kind: $kind, message: $message, context: $context, error: $error, logs: $logs)';
}


}

/// @nodoc
abstract mixin class _$FeedbackRequestCopyWith<$Res> implements $FeedbackRequestCopyWith<$Res> {
  factory _$FeedbackRequestCopyWith(_FeedbackRequest value, $Res Function(_FeedbackRequest) _then) = __$FeedbackRequestCopyWithImpl;
@override @useResult
$Res call({
 String kind, String message, ClientContextDto context, ClientErrorDto? error, List<ClientLogEntryDto>? logs
});


@override $ClientContextDtoCopyWith<$Res> get context;@override $ClientErrorDtoCopyWith<$Res>? get error;

}
/// @nodoc
class __$FeedbackRequestCopyWithImpl<$Res>
    implements _$FeedbackRequestCopyWith<$Res> {
  __$FeedbackRequestCopyWithImpl(this._self, this._then);

  final _FeedbackRequest _self;
  final $Res Function(_FeedbackRequest) _then;

/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? kind = null,Object? message = null,Object? context = null,Object? error = freezed,Object? logs = freezed,}) {
  return _then(_FeedbackRequest(
kind: null == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String,message: null == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String,context: null == context ? _self.context : context // ignore: cast_nullable_to_non_nullable
as ClientContextDto,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as ClientErrorDto?,logs: freezed == logs ? _self._logs : logs // ignore: cast_nullable_to_non_nullable
as List<ClientLogEntryDto>?,
  ));
}

/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientContextDtoCopyWith<$Res> get context {
  
  return $ClientContextDtoCopyWith<$Res>(_self.context, (value) {
    return _then(_self.copyWith(context: value));
  });
}/// Create a copy of FeedbackRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$ClientErrorDtoCopyWith<$Res>? get error {
    if (_self.error == null) {
    return null;
  }

  return $ClientErrorDtoCopyWith<$Res>(_self.error!, (value) {
    return _then(_self.copyWith(error: value));
  });
}
}

// dart format on
