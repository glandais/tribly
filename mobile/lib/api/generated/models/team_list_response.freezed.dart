// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamListResponse {

/// List of teams
 List<TeamDetailDto> get teams;/// Total number of teams
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of TeamListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamListResponseCopyWith<TeamListResponse> get copyWith => _$TeamListResponseCopyWithImpl<TeamListResponse>(this as TeamListResponse, _$identity);

  /// Serializes this TeamListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamListResponse&&const DeepCollectionEquality().equals(other.teams, teams)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(teams),total,page,size);

@override
String toString() {
  return 'TeamListResponse(teams: $teams, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $TeamListResponseCopyWith<$Res>  {
  factory $TeamListResponseCopyWith(TeamListResponse value, $Res Function(TeamListResponse) _then) = _$TeamListResponseCopyWithImpl;
@useResult
$Res call({
 List<TeamDetailDto> teams, int total, int page, int size
});




}
/// @nodoc
class _$TeamListResponseCopyWithImpl<$Res>
    implements $TeamListResponseCopyWith<$Res> {
  _$TeamListResponseCopyWithImpl(this._self, this._then);

  final TeamListResponse _self;
  final $Res Function(TeamListResponse) _then;

/// Create a copy of TeamListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teams = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(TeamListResponse(
teams: null == teams ? _self.teams : teams // ignore: cast_nullable_to_non_nullable
as List<TeamDetailDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamListResponse].
extension TeamListResponsePatterns on TeamListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamListResponse value)  $default,){
final _that = this;
switch (_that) {
case _TeamListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _TeamListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<TeamDetailDto> teams,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamListResponse() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<TeamDetailDto> teams,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _TeamListResponse():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<TeamDetailDto> teams,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _TeamListResponse() when $default != null:
return $default(_that.teams,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamListResponse implements TeamListResponse {
  const _TeamListResponse({required  List<TeamDetailDto> teams, required this.total, required this.page, required this.size}): _teams = teams;
  factory _TeamListResponse.fromJson(Map<String, dynamic> json) => _$TeamListResponseFromJson(json);

/// List of teams
 final  List<TeamDetailDto> _teams;
/// List of teams
@override List<TeamDetailDto> get teams {
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

/// Create a copy of TeamListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamListResponseCopyWith<_TeamListResponse> get copyWith => __$TeamListResponseCopyWithImpl<_TeamListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamListResponse&&const DeepCollectionEquality().equals(other._teams, _teams)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_teams),total,page,size);

@override
String toString() {
  return 'TeamListResponse(teams: $teams, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$TeamListResponseCopyWith<$Res> implements $TeamListResponseCopyWith<$Res> {
  factory _$TeamListResponseCopyWith(_TeamListResponse value, $Res Function(_TeamListResponse) _then) = __$TeamListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<TeamDetailDto> teams, int total, int page, int size
});




}
/// @nodoc
class __$TeamListResponseCopyWithImpl<$Res>
    implements _$TeamListResponseCopyWith<$Res> {
  __$TeamListResponseCopyWithImpl(this._self, this._then);

  final _TeamListResponse _self;
  final $Res Function(_TeamListResponse) _then;

/// Create a copy of TeamListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teams = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_TeamListResponse(
teams: null == teams ? _self._teams : teams // ignore: cast_nullable_to_non_nullable
as List<TeamDetailDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
