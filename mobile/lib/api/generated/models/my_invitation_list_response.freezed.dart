// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'my_invitation_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MyInvitationListResponse {

/// Invitations, newest first
 List<MyInvitationDto> get invitations;
/// Create a copy of MyInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MyInvitationListResponseCopyWith<MyInvitationListResponse> get copyWith => _$MyInvitationListResponseCopyWithImpl<MyInvitationListResponse>(this as MyInvitationListResponse, _$identity);

  /// Serializes this MyInvitationListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MyInvitationListResponse&&const DeepCollectionEquality().equals(other.invitations, invitations));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(invitations));

@override
String toString() {
  return 'MyInvitationListResponse(invitations: $invitations)';
}


}

/// @nodoc
abstract mixin class $MyInvitationListResponseCopyWith<$Res>  {
  factory $MyInvitationListResponseCopyWith(MyInvitationListResponse value, $Res Function(MyInvitationListResponse) _then) = _$MyInvitationListResponseCopyWithImpl;
@useResult
$Res call({
 List<MyInvitationDto> invitations
});




}
/// @nodoc
class _$MyInvitationListResponseCopyWithImpl<$Res>
    implements $MyInvitationListResponseCopyWith<$Res> {
  _$MyInvitationListResponseCopyWithImpl(this._self, this._then);

  final MyInvitationListResponse _self;
  final $Res Function(MyInvitationListResponse) _then;

/// Create a copy of MyInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? invitations = null,}) {
  return _then(_self.copyWith(
invitations: null == invitations ? _self.invitations : invitations // ignore: cast_nullable_to_non_nullable
as List<MyInvitationDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [MyInvitationListResponse].
extension MyInvitationListResponsePatterns on MyInvitationListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MyInvitationListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MyInvitationListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MyInvitationListResponse value)  $default,){
final _that = this;
switch (_that) {
case _MyInvitationListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MyInvitationListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _MyInvitationListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<MyInvitationDto> invitations)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MyInvitationListResponse() when $default != null:
return $default(_that.invitations);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<MyInvitationDto> invitations)  $default,) {final _that = this;
switch (_that) {
case _MyInvitationListResponse():
return $default(_that.invitations);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<MyInvitationDto> invitations)?  $default,) {final _that = this;
switch (_that) {
case _MyInvitationListResponse() when $default != null:
return $default(_that.invitations);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MyInvitationListResponse implements MyInvitationListResponse {
  const _MyInvitationListResponse({required final  List<MyInvitationDto> invitations}): _invitations = invitations;
  factory _MyInvitationListResponse.fromJson(Map<String, dynamic> json) => _$MyInvitationListResponseFromJson(json);

/// Invitations, newest first
 final  List<MyInvitationDto> _invitations;
/// Invitations, newest first
@override List<MyInvitationDto> get invitations {
  if (_invitations is EqualUnmodifiableListView) return _invitations;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_invitations);
}


/// Create a copy of MyInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MyInvitationListResponseCopyWith<_MyInvitationListResponse> get copyWith => __$MyInvitationListResponseCopyWithImpl<_MyInvitationListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MyInvitationListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MyInvitationListResponse&&const DeepCollectionEquality().equals(other._invitations, _invitations));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_invitations));

@override
String toString() {
  return 'MyInvitationListResponse(invitations: $invitations)';
}


}

/// @nodoc
abstract mixin class _$MyInvitationListResponseCopyWith<$Res> implements $MyInvitationListResponseCopyWith<$Res> {
  factory _$MyInvitationListResponseCopyWith(_MyInvitationListResponse value, $Res Function(_MyInvitationListResponse) _then) = __$MyInvitationListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<MyInvitationDto> invitations
});




}
/// @nodoc
class __$MyInvitationListResponseCopyWithImpl<$Res>
    implements _$MyInvitationListResponseCopyWith<$Res> {
  __$MyInvitationListResponseCopyWithImpl(this._self, this._then);

  final _MyInvitationListResponse _self;
  final $Res Function(_MyInvitationListResponse) _then;

/// Create a copy of MyInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? invitations = null,}) {
  return _then(_MyInvitationListResponse(
invitations: null == invitations ? _self._invitations : invitations // ignore: cast_nullable_to_non_nullable
as List<MyInvitationDto>,
  ));
}


}

// dart format on
