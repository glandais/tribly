// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'status_change_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$StatusChangeRequest {

/// New status
 String get status;
/// Create a copy of StatusChangeRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$StatusChangeRequestCopyWith<StatusChangeRequest> get copyWith => _$StatusChangeRequestCopyWithImpl<StatusChangeRequest>(this as StatusChangeRequest, _$identity);

  /// Serializes this StatusChangeRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as StatusChangeRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is StatusChangeRequest&&(identical(other.status, _this.status) || other.status == _this.status));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as StatusChangeRequest;
  return Object.hash(runtimeType,_this.status);
}

@override
String toString() {
  final _this = this as StatusChangeRequest;
  return 'StatusChangeRequest(status: ${_this.status})';
}


}

/// @nodoc
abstract mixin class $StatusChangeRequestCopyWith<$Res>  {
  factory $StatusChangeRequestCopyWith(StatusChangeRequest value, $Res Function(StatusChangeRequest) _then) = _$StatusChangeRequestCopyWithImpl;
@useResult
$Res call({
 String status
});




}
/// @nodoc
class _$StatusChangeRequestCopyWithImpl<$Res>
    implements $StatusChangeRequestCopyWith<$Res> {
  _$StatusChangeRequestCopyWithImpl(this._self, this._then);

  final StatusChangeRequest _self;
  final $Res Function(StatusChangeRequest) _then;

/// Create a copy of StatusChangeRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? status = null,}) {
  return _then(StatusChangeRequest(
status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [StatusChangeRequest].
extension StatusChangeRequestPatterns on StatusChangeRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _StatusChangeRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _StatusChangeRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _StatusChangeRequest value)  $default,){
final _that = this;
switch (_that) {
case _StatusChangeRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _StatusChangeRequest value)?  $default,){
final _that = this;
switch (_that) {
case _StatusChangeRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String status)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _StatusChangeRequest() when $default != null:
return $default(_that.status);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String status)  $default,) {final _that = this;
switch (_that) {
case _StatusChangeRequest():
return $default(_that.status);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String status)?  $default,) {final _that = this;
switch (_that) {
case _StatusChangeRequest() when $default != null:
return $default(_that.status);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _StatusChangeRequest implements StatusChangeRequest {
  const _StatusChangeRequest({required this.status});
  factory _StatusChangeRequest.fromJson(Map<String, dynamic> json) => _$StatusChangeRequestFromJson(json);

/// New status
@override final  String status;

/// Create a copy of StatusChangeRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$StatusChangeRequestCopyWith<_StatusChangeRequest> get copyWith => __$StatusChangeRequestCopyWithImpl<_StatusChangeRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$StatusChangeRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _StatusChangeRequest&&(identical(other.status, status) || other.status == status));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,status);
}

@override
String toString() {
    return 'StatusChangeRequest(status: $status)';
}


}

/// @nodoc
abstract mixin class _$StatusChangeRequestCopyWith<$Res> implements $StatusChangeRequestCopyWith<$Res> {
  factory _$StatusChangeRequestCopyWith(_StatusChangeRequest value, $Res Function(_StatusChangeRequest) _then) = __$StatusChangeRequestCopyWithImpl;
@override @useResult
$Res call({
 String status
});




}
/// @nodoc
class __$StatusChangeRequestCopyWithImpl<$Res>
    implements _$StatusChangeRequestCopyWith<$Res> {
  __$StatusChangeRequestCopyWithImpl(this._self, this._then);

  final _StatusChangeRequest _self;
  final $Res Function(_StatusChangeRequest) _then;

/// Create a copy of StatusChangeRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? status = null,}) {
  return _then(_StatusChangeRequest(
status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
