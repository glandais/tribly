// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_team_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminTeamListResponse {

/// List of teams
 List<AdminTeamDto> get teams;/// Total number of teams
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of AdminTeamListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminTeamListResponseCopyWith<AdminTeamListResponse> get copyWith => _$AdminTeamListResponseCopyWithImpl<AdminTeamListResponse>(this as AdminTeamListResponse, _$identity);

  /// Serializes this AdminTeamListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminTeamListResponse&&const DeepCollectionEquality().equals(other.teams, teams)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(teams),total,page,size);

@override
String toString() {
  return 'AdminTeamListResponse(teams: $teams, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $AdminTeamListResponseCopyWith<$Res>  {
  factory $AdminTeamListResponseCopyWith(AdminTeamListResponse value, $Res Function(AdminTeamListResponse) _then) = _$AdminTeamListResponseCopyWithImpl;
@useResult
$Res call({
 List<AdminTeamDto> teams, int total, int page, int size
});




}
/// @nodoc
class _$AdminTeamListResponseCopyWithImpl<$Res>
    implements $AdminTeamListResponseCopyWith<$Res> {
  _$AdminTeamListResponseCopyWithImpl(this._self, this._then);

  final AdminTeamListResponse _self;
  final $Res Function(AdminTeamListResponse) _then;

/// Create a copy of AdminTeamListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teams = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(AdminTeamListResponse(
teams: null == teams ? _self.teams : teams // ignore: cast_nullable_to_non_nullable
as List<AdminTeamDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminTeamListResponse].
extension AdminTeamListResponsePatterns on AdminTeamListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminTeamListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminTeamListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminTeamListResponse value)  $default,){
final _that = this;
switch (_that) {
case _AdminTeamListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminTeamListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _AdminTeamListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<AdminTeamDto> teams,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminTeamListResponse() when $default != null:
return $default(_that.teams,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<AdminTeamDto> teams,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _AdminTeamListResponse():
return $default(_that.teams,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<AdminTeamDto> teams,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _AdminTeamListResponse() when $default != null:
return $default(_that.teams,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminTeamListResponse implements AdminTeamListResponse {
  const _AdminTeamListResponse({required  List<AdminTeamDto> teams, required this.total, required this.page, required this.size}): _teams = teams;
  factory _AdminTeamListResponse.fromJson(Map<String, dynamic> json) => _$AdminTeamListResponseFromJson(json);

/// List of teams
 final  List<AdminTeamDto> _teams;
/// List of teams
@override List<AdminTeamDto> get teams {
  if (_teams is EqualUnmodifiableListView) return _teams;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_teams);
}

/// Total number of teams
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of AdminTeamListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminTeamListResponseCopyWith<_AdminTeamListResponse> get copyWith => __$AdminTeamListResponseCopyWithImpl<_AdminTeamListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminTeamListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminTeamListResponse&&const DeepCollectionEquality().equals(other._teams, _teams)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_teams),total,page,size);

@override
String toString() {
  return 'AdminTeamListResponse(teams: $teams, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$AdminTeamListResponseCopyWith<$Res> implements $AdminTeamListResponseCopyWith<$Res> {
  factory _$AdminTeamListResponseCopyWith(_AdminTeamListResponse value, $Res Function(_AdminTeamListResponse) _then) = __$AdminTeamListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<AdminTeamDto> teams, int total, int page, int size
});




}
/// @nodoc
class __$AdminTeamListResponseCopyWithImpl<$Res>
    implements _$AdminTeamListResponseCopyWith<$Res> {
  __$AdminTeamListResponseCopyWithImpl(this._self, this._then);

  final _AdminTeamListResponse _self;
  final $Res Function(_AdminTeamListResponse) _then;

/// Create a copy of AdminTeamListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teams = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_AdminTeamListResponse(
teams: null == teams ? _self._teams : teams // ignore: cast_nullable_to_non_nullable
as List<AdminTeamDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
