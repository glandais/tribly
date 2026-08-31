// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'member_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MemberListResponse {

/// List of members
 List<MemberDto> get members;/// Total number of members
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of MemberListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MemberListResponseCopyWith<MemberListResponse> get copyWith => _$MemberListResponseCopyWithImpl<MemberListResponse>(this as MemberListResponse, _$identity);

  /// Serializes this MemberListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as MemberListResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MemberListResponse&&const DeepCollectionEquality().equals(other.members, _this.members)&&(identical(other.total, _this.total) || other.total == _this.total)&&(identical(other.page, _this.page) || other.page == _this.page)&&(identical(other.size, _this.size) || other.size == _this.size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as MemberListResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.members),_this.total,_this.page,_this.size);
}

@override
String toString() {
  final _this = this as MemberListResponse;
  return 'MemberListResponse(members: ${_this.members}, total: ${_this.total}, page: ${_this.page}, size: ${_this.size})';
}


}

/// @nodoc
abstract mixin class $MemberListResponseCopyWith<$Res>  {
  factory $MemberListResponseCopyWith(MemberListResponse value, $Res Function(MemberListResponse) _then) = _$MemberListResponseCopyWithImpl;
@useResult
$Res call({
 List<MemberDto> members, int total, int page, int size
});




}
/// @nodoc
class _$MemberListResponseCopyWithImpl<$Res>
    implements $MemberListResponseCopyWith<$Res> {
  _$MemberListResponseCopyWithImpl(this._self, this._then);

  final MemberListResponse _self;
  final $Res Function(MemberListResponse) _then;

/// Create a copy of MemberListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? members = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(MemberListResponse(
members: null == members ? _self.members : members // ignore: cast_nullable_to_non_nullable
as List<MemberDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [MemberListResponse].
extension MemberListResponsePatterns on MemberListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MemberListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MemberListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MemberListResponse value)  $default,){
final _that = this;
switch (_that) {
case _MemberListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MemberListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _MemberListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<MemberDto> members,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MemberListResponse() when $default != null:
return $default(_that.members,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<MemberDto> members,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _MemberListResponse():
return $default(_that.members,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<MemberDto> members,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _MemberListResponse() when $default != null:
return $default(_that.members,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MemberListResponse implements MemberListResponse {
  const _MemberListResponse({required  List<MemberDto> members, required this.total, required this.page, required this.size}): _members = members;
  factory _MemberListResponse.fromJson(Map<String, dynamic> json) => _$MemberListResponseFromJson(json);

/// List of members
 final  List<MemberDto> _members;
/// List of members
@override List<MemberDto> get members {
  if (_members is EqualUnmodifiableListView) return _members;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_members);
}

/// Total number of members
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of MemberListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MemberListResponseCopyWith<_MemberListResponse> get copyWith => __$MemberListResponseCopyWithImpl<_MemberListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MemberListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _MemberListResponse&&const DeepCollectionEquality().equals(other.members, _members)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_members),total,page,size);
}

@override
String toString() {
    return 'MemberListResponse(members: $members, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$MemberListResponseCopyWith<$Res> implements $MemberListResponseCopyWith<$Res> {
  factory _$MemberListResponseCopyWith(_MemberListResponse value, $Res Function(_MemberListResponse) _then) = __$MemberListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<MemberDto> members, int total, int page, int size
});




}
/// @nodoc
class __$MemberListResponseCopyWithImpl<$Res>
    implements _$MemberListResponseCopyWith<$Res> {
  __$MemberListResponseCopyWithImpl(this._self, this._then);

  final _MemberListResponse _self;
  final $Res Function(_MemberListResponse) _then;

/// Create a copy of MemberListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? members = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_MemberListResponse(
members: null == members ? _self._members : members // ignore: cast_nullable_to_non_nullable
as List<MemberDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
