// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'participant_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ParticipantListResponse {

/// Participants of this page, in registration order (earliest first)
 List<PublicUserDto> get participants;/// Number of participants matching the search, over every page — the M of « N of M »
 int get total;/// Current page number (0-based)
 int get page;/// Page size actually applied
 int get size;
/// Create a copy of ParticipantListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ParticipantListResponseCopyWith<ParticipantListResponse> get copyWith => _$ParticipantListResponseCopyWithImpl<ParticipantListResponse>(this as ParticipantListResponse, _$identity);

  /// Serializes this ParticipantListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ParticipantListResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ParticipantListResponse&&const DeepCollectionEquality().equals(other.participants, _this.participants)&&(identical(other.total, _this.total) || other.total == _this.total)&&(identical(other.page, _this.page) || other.page == _this.page)&&(identical(other.size, _this.size) || other.size == _this.size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ParticipantListResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.participants),_this.total,_this.page,_this.size);
}

@override
String toString() {
  final _this = this as ParticipantListResponse;
  return 'ParticipantListResponse(participants: ${_this.participants}, total: ${_this.total}, page: ${_this.page}, size: ${_this.size})';
}


}

/// @nodoc
abstract mixin class $ParticipantListResponseCopyWith<$Res>  {
  factory $ParticipantListResponseCopyWith(ParticipantListResponse value, $Res Function(ParticipantListResponse) _then) = _$ParticipantListResponseCopyWithImpl;
@useResult
$Res call({
 List<PublicUserDto> participants, int total, int page, int size
});




}
/// @nodoc
class _$ParticipantListResponseCopyWithImpl<$Res>
    implements $ParticipantListResponseCopyWith<$Res> {
  _$ParticipantListResponseCopyWithImpl(this._self, this._then);

  final ParticipantListResponse _self;
  final $Res Function(ParticipantListResponse) _then;

/// Create a copy of ParticipantListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? participants = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(ParticipantListResponse(
participants: null == participants ? _self.participants : participants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [ParticipantListResponse].
extension ParticipantListResponsePatterns on ParticipantListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ParticipantListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ParticipantListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ParticipantListResponse value)  $default,){
final _that = this;
switch (_that) {
case _ParticipantListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ParticipantListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _ParticipantListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<PublicUserDto> participants,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ParticipantListResponse() when $default != null:
return $default(_that.participants,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<PublicUserDto> participants,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _ParticipantListResponse():
return $default(_that.participants,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<PublicUserDto> participants,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _ParticipantListResponse() when $default != null:
return $default(_that.participants,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ParticipantListResponse implements ParticipantListResponse {
  const _ParticipantListResponse({required  List<PublicUserDto> participants, required this.total, required this.page, required this.size}): _participants = participants;
  factory _ParticipantListResponse.fromJson(Map<String, dynamic> json) => _$ParticipantListResponseFromJson(json);

/// Participants of this page, in registration order (earliest first)
 final  List<PublicUserDto> _participants;
/// Participants of this page, in registration order (earliest first)
@override List<PublicUserDto> get participants {
  if (_participants is EqualUnmodifiableListView) return _participants;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_participants);
}

/// Number of participants matching the search, over every page — the M of « N of M »
@override final  int total;
/// Current page number (0-based)
@override final  int page;
/// Page size actually applied
@override final  int size;

/// Create a copy of ParticipantListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ParticipantListResponseCopyWith<_ParticipantListResponse> get copyWith => __$ParticipantListResponseCopyWithImpl<_ParticipantListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ParticipantListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ParticipantListResponse&&const DeepCollectionEquality().equals(other.participants, _participants)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_participants),total,page,size);
}

@override
String toString() {
    return 'ParticipantListResponse(participants: $participants, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$ParticipantListResponseCopyWith<$Res> implements $ParticipantListResponseCopyWith<$Res> {
  factory _$ParticipantListResponseCopyWith(_ParticipantListResponse value, $Res Function(_ParticipantListResponse) _then) = __$ParticipantListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<PublicUserDto> participants, int total, int page, int size
});




}
/// @nodoc
class __$ParticipantListResponseCopyWithImpl<$Res>
    implements _$ParticipantListResponseCopyWith<$Res> {
  __$ParticipantListResponseCopyWithImpl(this._self, this._then);

  final _ParticipantListResponse _self;
  final $Res Function(_ParticipantListResponse) _then;

/// Create a copy of ParticipantListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? participants = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_ParticipantListResponse(
participants: null == participants ? _self._participants : participants // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
