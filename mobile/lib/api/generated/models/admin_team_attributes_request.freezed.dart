// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_team_attributes_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminTeamAttributesRequest {

/// Whether team admins can change visibility
 bool get visibilityEditable;/// Whether any domain user can join this public team
 bool get joinable;/// Whether team admins can add members
 bool get addMemberAllowed;
/// Create a copy of AdminTeamAttributesRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminTeamAttributesRequestCopyWith<AdminTeamAttributesRequest> get copyWith => _$AdminTeamAttributesRequestCopyWithImpl<AdminTeamAttributesRequest>(this as AdminTeamAttributesRequest, _$identity);

  /// Serializes this AdminTeamAttributesRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminTeamAttributesRequest&&(identical(other.visibilityEditable, visibilityEditable) || other.visibilityEditable == visibilityEditable)&&(identical(other.joinable, joinable) || other.joinable == joinable)&&(identical(other.addMemberAllowed, addMemberAllowed) || other.addMemberAllowed == addMemberAllowed));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,visibilityEditable,joinable,addMemberAllowed);

@override
String toString() {
  return 'AdminTeamAttributesRequest(visibilityEditable: $visibilityEditable, joinable: $joinable, addMemberAllowed: $addMemberAllowed)';
}


}

/// @nodoc
abstract mixin class $AdminTeamAttributesRequestCopyWith<$Res>  {
  factory $AdminTeamAttributesRequestCopyWith(AdminTeamAttributesRequest value, $Res Function(AdminTeamAttributesRequest) _then) = _$AdminTeamAttributesRequestCopyWithImpl;
@useResult
$Res call({
 bool visibilityEditable, bool joinable, bool addMemberAllowed
});




}
/// @nodoc
class _$AdminTeamAttributesRequestCopyWithImpl<$Res>
    implements $AdminTeamAttributesRequestCopyWith<$Res> {
  _$AdminTeamAttributesRequestCopyWithImpl(this._self, this._then);

  final AdminTeamAttributesRequest _self;
  final $Res Function(AdminTeamAttributesRequest) _then;

/// Create a copy of AdminTeamAttributesRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? visibilityEditable = null,Object? joinable = null,Object? addMemberAllowed = null,}) {
  return _then(_self.copyWith(
visibilityEditable: null == visibilityEditable ? _self.visibilityEditable : visibilityEditable // ignore: cast_nullable_to_non_nullable
as bool,joinable: null == joinable ? _self.joinable : joinable // ignore: cast_nullable_to_non_nullable
as bool,addMemberAllowed: null == addMemberAllowed ? _self.addMemberAllowed : addMemberAllowed // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminTeamAttributesRequest].
extension AdminTeamAttributesRequestPatterns on AdminTeamAttributesRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminTeamAttributesRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminTeamAttributesRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminTeamAttributesRequest value)  $default,){
final _that = this;
switch (_that) {
case _AdminTeamAttributesRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminTeamAttributesRequest value)?  $default,){
final _that = this;
switch (_that) {
case _AdminTeamAttributesRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( bool visibilityEditable,  bool joinable,  bool addMemberAllowed)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminTeamAttributesRequest() when $default != null:
return $default(_that.visibilityEditable,_that.joinable,_that.addMemberAllowed);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( bool visibilityEditable,  bool joinable,  bool addMemberAllowed)  $default,) {final _that = this;
switch (_that) {
case _AdminTeamAttributesRequest():
return $default(_that.visibilityEditable,_that.joinable,_that.addMemberAllowed);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( bool visibilityEditable,  bool joinable,  bool addMemberAllowed)?  $default,) {final _that = this;
switch (_that) {
case _AdminTeamAttributesRequest() when $default != null:
return $default(_that.visibilityEditable,_that.joinable,_that.addMemberAllowed);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminTeamAttributesRequest implements AdminTeamAttributesRequest {
  const _AdminTeamAttributesRequest({required this.visibilityEditable, required this.joinable, required this.addMemberAllowed});
  factory _AdminTeamAttributesRequest.fromJson(Map<String, dynamic> json) => _$AdminTeamAttributesRequestFromJson(json);

/// Whether team admins can change visibility
@override final  bool visibilityEditable;
/// Whether any domain user can join this public team
@override final  bool joinable;
/// Whether team admins can add members
@override final  bool addMemberAllowed;

/// Create a copy of AdminTeamAttributesRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminTeamAttributesRequestCopyWith<_AdminTeamAttributesRequest> get copyWith => __$AdminTeamAttributesRequestCopyWithImpl<_AdminTeamAttributesRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminTeamAttributesRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminTeamAttributesRequest&&(identical(other.visibilityEditable, visibilityEditable) || other.visibilityEditable == visibilityEditable)&&(identical(other.joinable, joinable) || other.joinable == joinable)&&(identical(other.addMemberAllowed, addMemberAllowed) || other.addMemberAllowed == addMemberAllowed));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,visibilityEditable,joinable,addMemberAllowed);

@override
String toString() {
  return 'AdminTeamAttributesRequest(visibilityEditable: $visibilityEditable, joinable: $joinable, addMemberAllowed: $addMemberAllowed)';
}


}

/// @nodoc
abstract mixin class _$AdminTeamAttributesRequestCopyWith<$Res> implements $AdminTeamAttributesRequestCopyWith<$Res> {
  factory _$AdminTeamAttributesRequestCopyWith(_AdminTeamAttributesRequest value, $Res Function(_AdminTeamAttributesRequest) _then) = __$AdminTeamAttributesRequestCopyWithImpl;
@override @useResult
$Res call({
 bool visibilityEditable, bool joinable, bool addMemberAllowed
});




}
/// @nodoc
class __$AdminTeamAttributesRequestCopyWithImpl<$Res>
    implements _$AdminTeamAttributesRequestCopyWith<$Res> {
  __$AdminTeamAttributesRequestCopyWithImpl(this._self, this._then);

  final _AdminTeamAttributesRequest _self;
  final $Res Function(_AdminTeamAttributesRequest) _then;

/// Create a copy of AdminTeamAttributesRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? visibilityEditable = null,Object? joinable = null,Object? addMemberAllowed = null,}) {
  return _then(_AdminTeamAttributesRequest(
visibilityEditable: null == visibilityEditable ? _self.visibilityEditable : visibilityEditable // ignore: cast_nullable_to_non_nullable
as bool,joinable: null == joinable ? _self.joinable : joinable // ignore: cast_nullable_to_non_nullable
as bool,addMemberAllowed: null == addMemberAllowed ? _self.addMemberAllowed : addMemberAllowed // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
