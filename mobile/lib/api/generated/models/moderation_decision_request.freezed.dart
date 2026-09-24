// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'moderation_decision_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ModerationDecisionRequest {

/// Type of the reported target
 String get targetType;/// ID (TSID) of the reported target
 String get targetId;/// REMOVE_CONTENT deletes the content (not allowed on a MEMBER); DISMISS keeps it and shows it again if reports had hidden it
 String get action;
/// Create a copy of ModerationDecisionRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ModerationDecisionRequestCopyWith<ModerationDecisionRequest> get copyWith => _$ModerationDecisionRequestCopyWithImpl<ModerationDecisionRequest>(this as ModerationDecisionRequest, _$identity);

  /// Serializes this ModerationDecisionRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ModerationDecisionRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ModerationDecisionRequest&&(identical(other.targetType, _this.targetType) || other.targetType == _this.targetType)&&(identical(other.targetId, _this.targetId) || other.targetId == _this.targetId)&&(identical(other.action, _this.action) || other.action == _this.action));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ModerationDecisionRequest;
  return Object.hash(runtimeType,_this.targetType,_this.targetId,_this.action);
}

@override
String toString() {
  final _this = this as ModerationDecisionRequest;
  return 'ModerationDecisionRequest(targetType: ${_this.targetType}, targetId: ${_this.targetId}, action: ${_this.action})';
}


}

/// @nodoc
abstract mixin class $ModerationDecisionRequestCopyWith<$Res>  {
  factory $ModerationDecisionRequestCopyWith(ModerationDecisionRequest value, $Res Function(ModerationDecisionRequest) _then) = _$ModerationDecisionRequestCopyWithImpl;
@useResult
$Res call({
 String targetType, String targetId, String action
});




}
/// @nodoc
class _$ModerationDecisionRequestCopyWithImpl<$Res>
    implements $ModerationDecisionRequestCopyWith<$Res> {
  _$ModerationDecisionRequestCopyWithImpl(this._self, this._then);

  final ModerationDecisionRequest _self;
  final $Res Function(ModerationDecisionRequest) _then;

/// Create a copy of ModerationDecisionRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? targetType = null,Object? targetId = null,Object? action = null,}) {
  return _then(ModerationDecisionRequest(
targetType: null == targetType ? _self.targetType : targetType // ignore: cast_nullable_to_non_nullable
as String,targetId: null == targetId ? _self.targetId : targetId // ignore: cast_nullable_to_non_nullable
as String,action: null == action ? _self.action : action // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [ModerationDecisionRequest].
extension ModerationDecisionRequestPatterns on ModerationDecisionRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ModerationDecisionRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ModerationDecisionRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ModerationDecisionRequest value)  $default,){
final _that = this;
switch (_that) {
case _ModerationDecisionRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ModerationDecisionRequest value)?  $default,){
final _that = this;
switch (_that) {
case _ModerationDecisionRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String targetType,  String targetId,  String action)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ModerationDecisionRequest() when $default != null:
return $default(_that.targetType,_that.targetId,_that.action);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String targetType,  String targetId,  String action)  $default,) {final _that = this;
switch (_that) {
case _ModerationDecisionRequest():
return $default(_that.targetType,_that.targetId,_that.action);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String targetType,  String targetId,  String action)?  $default,) {final _that = this;
switch (_that) {
case _ModerationDecisionRequest() when $default != null:
return $default(_that.targetType,_that.targetId,_that.action);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ModerationDecisionRequest implements ModerationDecisionRequest {
  const _ModerationDecisionRequest({required this.targetType, required this.targetId, required this.action});
  factory _ModerationDecisionRequest.fromJson(Map<String, dynamic> json) => _$ModerationDecisionRequestFromJson(json);

/// Type of the reported target
@override final  String targetType;
/// ID (TSID) of the reported target
@override final  String targetId;
/// REMOVE_CONTENT deletes the content (not allowed on a MEMBER); DISMISS keeps it and shows it again if reports had hidden it
@override final  String action;

/// Create a copy of ModerationDecisionRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ModerationDecisionRequestCopyWith<_ModerationDecisionRequest> get copyWith => __$ModerationDecisionRequestCopyWithImpl<_ModerationDecisionRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ModerationDecisionRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ModerationDecisionRequest&&(identical(other.targetType, targetType) || other.targetType == targetType)&&(identical(other.targetId, targetId) || other.targetId == targetId)&&(identical(other.action, action) || other.action == action));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,targetType,targetId,action);
}

@override
String toString() {
    return 'ModerationDecisionRequest(targetType: $targetType, targetId: $targetId, action: $action)';
}


}

/// @nodoc
abstract mixin class _$ModerationDecisionRequestCopyWith<$Res> implements $ModerationDecisionRequestCopyWith<$Res> {
  factory _$ModerationDecisionRequestCopyWith(_ModerationDecisionRequest value, $Res Function(_ModerationDecisionRequest) _then) = __$ModerationDecisionRequestCopyWithImpl;
@override @useResult
$Res call({
 String targetType, String targetId, String action
});




}
/// @nodoc
class __$ModerationDecisionRequestCopyWithImpl<$Res>
    implements _$ModerationDecisionRequestCopyWith<$Res> {
  __$ModerationDecisionRequestCopyWithImpl(this._self, this._then);

  final _ModerationDecisionRequest _self;
  final $Res Function(_ModerationDecisionRequest) _then;

/// Create a copy of ModerationDecisionRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? targetType = null,Object? targetId = null,Object? action = null,}) {
  return _then(_ModerationDecisionRequest(
targetType: null == targetType ? _self.targetType : targetType // ignore: cast_nullable_to_non_nullable
as String,targetId: null == targetId ? _self.targetId : targetId // ignore: cast_nullable_to_non_nullable
as String,action: null == action ? _self.action : action // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
