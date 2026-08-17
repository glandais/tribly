// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_invitation_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamInvitationListResponse {

/// Invitations
 List<TeamInvitationDto> get invitations;/// Total number of invitations
 int get total;/// Current page (0-indexed)
 int get page;/// Page size
 int get size;
/// Create a copy of TeamInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamInvitationListResponseCopyWith<TeamInvitationListResponse> get copyWith => _$TeamInvitationListResponseCopyWithImpl<TeamInvitationListResponse>(this as TeamInvitationListResponse, _$identity);

  /// Serializes this TeamInvitationListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamInvitationListResponse&&const DeepCollectionEquality().equals(other.invitations, invitations)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(invitations),total,page,size);

@override
String toString() {
  return 'TeamInvitationListResponse(invitations: $invitations, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $TeamInvitationListResponseCopyWith<$Res>  {
  factory $TeamInvitationListResponseCopyWith(TeamInvitationListResponse value, $Res Function(TeamInvitationListResponse) _then) = _$TeamInvitationListResponseCopyWithImpl;
@useResult
$Res call({
 List<TeamInvitationDto> invitations, int total, int page, int size
});




}
/// @nodoc
class _$TeamInvitationListResponseCopyWithImpl<$Res>
    implements $TeamInvitationListResponseCopyWith<$Res> {
  _$TeamInvitationListResponseCopyWithImpl(this._self, this._then);

  final TeamInvitationListResponse _self;
  final $Res Function(TeamInvitationListResponse) _then;

/// Create a copy of TeamInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? invitations = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(TeamInvitationListResponse(
invitations: null == invitations ? _self.invitations : invitations // ignore: cast_nullable_to_non_nullable
as List<TeamInvitationDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamInvitationListResponse].
extension TeamInvitationListResponsePatterns on TeamInvitationListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamInvitationListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamInvitationListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamInvitationListResponse value)  $default,){
final _that = this;
switch (_that) {
case _TeamInvitationListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamInvitationListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _TeamInvitationListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<TeamInvitationDto> invitations,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamInvitationListResponse() when $default != null:
return $default(_that.invitations,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<TeamInvitationDto> invitations,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _TeamInvitationListResponse():
return $default(_that.invitations,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<TeamInvitationDto> invitations,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _TeamInvitationListResponse() when $default != null:
return $default(_that.invitations,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamInvitationListResponse implements TeamInvitationListResponse {
  const _TeamInvitationListResponse({required  List<TeamInvitationDto> invitations, required this.total, required this.page, required this.size}): _invitations = invitations;
  factory _TeamInvitationListResponse.fromJson(Map<String, dynamic> json) => _$TeamInvitationListResponseFromJson(json);

/// Invitations
 final  List<TeamInvitationDto> _invitations;
/// Invitations
@override List<TeamInvitationDto> get invitations {
  if (_invitations is EqualUnmodifiableListView) return _invitations;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_invitations);
}

/// Total number of invitations
@override final  int total;
/// Current page (0-indexed)
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of TeamInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamInvitationListResponseCopyWith<_TeamInvitationListResponse> get copyWith => __$TeamInvitationListResponseCopyWithImpl<_TeamInvitationListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamInvitationListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamInvitationListResponse&&const DeepCollectionEquality().equals(other._invitations, _invitations)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_invitations),total,page,size);

@override
String toString() {
  return 'TeamInvitationListResponse(invitations: $invitations, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$TeamInvitationListResponseCopyWith<$Res> implements $TeamInvitationListResponseCopyWith<$Res> {
  factory _$TeamInvitationListResponseCopyWith(_TeamInvitationListResponse value, $Res Function(_TeamInvitationListResponse) _then) = __$TeamInvitationListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<TeamInvitationDto> invitations, int total, int page, int size
});




}
/// @nodoc
class __$TeamInvitationListResponseCopyWithImpl<$Res>
    implements _$TeamInvitationListResponseCopyWith<$Res> {
  __$TeamInvitationListResponseCopyWithImpl(this._self, this._then);

  final _TeamInvitationListResponse _self;
  final $Res Function(_TeamInvitationListResponse) _then;

/// Create a copy of TeamInvitationListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? invitations = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_TeamInvitationListResponse(
invitations: null == invitations ? _self._invitations : invitations // ignore: cast_nullable_to_non_nullable
as List<TeamInvitationDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
