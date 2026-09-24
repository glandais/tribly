// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'report_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ReportRequest {

/// The team the target belongs to. Its organizers and administrators moderate the report.
 String get teamSlug;/// What is reported
 String get targetType;/// ID (TSID) of the comment, publication, ad or route — or of the user for a MEMBER
 String get targetId;/// Why
 String get reason;/// Optional free text for the moderators, up to 500 characters
 String? get message;
/// Create a copy of ReportRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ReportRequestCopyWith<ReportRequest> get copyWith => _$ReportRequestCopyWithImpl<ReportRequest>(this as ReportRequest, _$identity);

  /// Serializes this ReportRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ReportRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ReportRequest&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.targetType, _this.targetType) || other.targetType == _this.targetType)&&(identical(other.targetId, _this.targetId) || other.targetId == _this.targetId)&&(identical(other.reason, _this.reason) || other.reason == _this.reason)&&(identical(other.message, _this.message) || other.message == _this.message));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ReportRequest;
  return Object.hash(runtimeType,_this.teamSlug,_this.targetType,_this.targetId,_this.reason,_this.message);
}

@override
String toString() {
  final _this = this as ReportRequest;
  return 'ReportRequest(teamSlug: ${_this.teamSlug}, targetType: ${_this.targetType}, targetId: ${_this.targetId}, reason: ${_this.reason}, message: ${_this.message})';
}


}

/// @nodoc
abstract mixin class $ReportRequestCopyWith<$Res>  {
  factory $ReportRequestCopyWith(ReportRequest value, $Res Function(ReportRequest) _then) = _$ReportRequestCopyWithImpl;
@useResult
$Res call({
 String teamSlug, String targetType, String targetId, String reason, String? message
});




}
/// @nodoc
class _$ReportRequestCopyWithImpl<$Res>
    implements $ReportRequestCopyWith<$Res> {
  _$ReportRequestCopyWithImpl(this._self, this._then);

  final ReportRequest _self;
  final $Res Function(ReportRequest) _then;

/// Create a copy of ReportRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teamSlug = null,Object? targetType = null,Object? targetId = null,Object? reason = null,Object? message = freezed,}) {
  return _then(ReportRequest(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,targetType: null == targetType ? _self.targetType : targetType // ignore: cast_nullable_to_non_nullable
as String,targetId: null == targetId ? _self.targetId : targetId // ignore: cast_nullable_to_non_nullable
as String,reason: null == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [ReportRequest].
extension ReportRequestPatterns on ReportRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ReportRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ReportRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ReportRequest value)  $default,){
final _that = this;
switch (_that) {
case _ReportRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ReportRequest value)?  $default,){
final _that = this;
switch (_that) {
case _ReportRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String teamSlug,  String targetType,  String targetId,  String reason,  String? message)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ReportRequest() when $default != null:
return $default(_that.teamSlug,_that.targetType,_that.targetId,_that.reason,_that.message);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String teamSlug,  String targetType,  String targetId,  String reason,  String? message)  $default,) {final _that = this;
switch (_that) {
case _ReportRequest():
return $default(_that.teamSlug,_that.targetType,_that.targetId,_that.reason,_that.message);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String teamSlug,  String targetType,  String targetId,  String reason,  String? message)?  $default,) {final _that = this;
switch (_that) {
case _ReportRequest() when $default != null:
return $default(_that.teamSlug,_that.targetType,_that.targetId,_that.reason,_that.message);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ReportRequest implements ReportRequest {
  const _ReportRequest({required this.teamSlug, required this.targetType, required this.targetId, required this.reason, this.message});
  factory _ReportRequest.fromJson(Map<String, dynamic> json) => _$ReportRequestFromJson(json);

/// The team the target belongs to. Its organizers and administrators moderate the report.
@override final  String teamSlug;
/// What is reported
@override final  String targetType;
/// ID (TSID) of the comment, publication, ad or route — or of the user for a MEMBER
@override final  String targetId;
/// Why
@override final  String reason;
/// Optional free text for the moderators, up to 500 characters
@override final  String? message;

/// Create a copy of ReportRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ReportRequestCopyWith<_ReportRequest> get copyWith => __$ReportRequestCopyWithImpl<_ReportRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ReportRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ReportRequest&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.targetType, targetType) || other.targetType == targetType)&&(identical(other.targetId, targetId) || other.targetId == targetId)&&(identical(other.reason, reason) || other.reason == reason)&&(identical(other.message, message) || other.message == message));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,teamSlug,targetType,targetId,reason,message);
}

@override
String toString() {
    return 'ReportRequest(teamSlug: $teamSlug, targetType: $targetType, targetId: $targetId, reason: $reason, message: $message)';
}


}

/// @nodoc
abstract mixin class _$ReportRequestCopyWith<$Res> implements $ReportRequestCopyWith<$Res> {
  factory _$ReportRequestCopyWith(_ReportRequest value, $Res Function(_ReportRequest) _then) = __$ReportRequestCopyWithImpl;
@override @useResult
$Res call({
 String teamSlug, String targetType, String targetId, String reason, String? message
});




}
/// @nodoc
class __$ReportRequestCopyWithImpl<$Res>
    implements _$ReportRequestCopyWith<$Res> {
  __$ReportRequestCopyWithImpl(this._self, this._then);

  final _ReportRequest _self;
  final $Res Function(_ReportRequest) _then;

/// Create a copy of ReportRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teamSlug = null,Object? targetType = null,Object? targetId = null,Object? reason = null,Object? message = freezed,}) {
  return _then(_ReportRequest(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,targetType: null == targetType ? _self.targetType : targetType // ignore: cast_nullable_to_non_nullable
as String,targetId: null == targetId ? _self.targetId : targetId // ignore: cast_nullable_to_non_nullable
as String,reason: null == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
