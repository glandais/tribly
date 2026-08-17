// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'admin_stats_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdminStatsDto {

/// Total number of domains
 int get totalDomains;/// Total number of teams
 int get totalTeams;/// Total number of users
 int get totalUsers;
/// Create a copy of AdminStatsDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdminStatsDtoCopyWith<AdminStatsDto> get copyWith => _$AdminStatsDtoCopyWithImpl<AdminStatsDto>(this as AdminStatsDto, _$identity);

  /// Serializes this AdminStatsDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdminStatsDto&&(identical(other.totalDomains, totalDomains) || other.totalDomains == totalDomains)&&(identical(other.totalTeams, totalTeams) || other.totalTeams == totalTeams)&&(identical(other.totalUsers, totalUsers) || other.totalUsers == totalUsers));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,totalDomains,totalTeams,totalUsers);

@override
String toString() {
  return 'AdminStatsDto(totalDomains: $totalDomains, totalTeams: $totalTeams, totalUsers: $totalUsers)';
}


}

/// @nodoc
abstract mixin class $AdminStatsDtoCopyWith<$Res>  {
  factory $AdminStatsDtoCopyWith(AdminStatsDto value, $Res Function(AdminStatsDto) _then) = _$AdminStatsDtoCopyWithImpl;
@useResult
$Res call({
 int totalDomains, int totalTeams, int totalUsers
});




}
/// @nodoc
class _$AdminStatsDtoCopyWithImpl<$Res>
    implements $AdminStatsDtoCopyWith<$Res> {
  _$AdminStatsDtoCopyWithImpl(this._self, this._then);

  final AdminStatsDto _self;
  final $Res Function(AdminStatsDto) _then;

/// Create a copy of AdminStatsDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? totalDomains = null,Object? totalTeams = null,Object? totalUsers = null,}) {
  return _then(AdminStatsDto(
totalDomains: null == totalDomains ? _self.totalDomains : totalDomains // ignore: cast_nullable_to_non_nullable
as int,totalTeams: null == totalTeams ? _self.totalTeams : totalTeams // ignore: cast_nullable_to_non_nullable
as int,totalUsers: null == totalUsers ? _self.totalUsers : totalUsers // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [AdminStatsDto].
extension AdminStatsDtoPatterns on AdminStatsDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdminStatsDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdminStatsDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdminStatsDto value)  $default,){
final _that = this;
switch (_that) {
case _AdminStatsDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdminStatsDto value)?  $default,){
final _that = this;
switch (_that) {
case _AdminStatsDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int totalDomains,  int totalTeams,  int totalUsers)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdminStatsDto() when $default != null:
return $default(_that.totalDomains,_that.totalTeams,_that.totalUsers);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int totalDomains,  int totalTeams,  int totalUsers)  $default,) {final _that = this;
switch (_that) {
case _AdminStatsDto():
return $default(_that.totalDomains,_that.totalTeams,_that.totalUsers);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int totalDomains,  int totalTeams,  int totalUsers)?  $default,) {final _that = this;
switch (_that) {
case _AdminStatsDto() when $default != null:
return $default(_that.totalDomains,_that.totalTeams,_that.totalUsers);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdminStatsDto implements AdminStatsDto {
  const _AdminStatsDto({required this.totalDomains, required this.totalTeams, required this.totalUsers});
  factory _AdminStatsDto.fromJson(Map<String, dynamic> json) => _$AdminStatsDtoFromJson(json);

/// Total number of domains
@override final  int totalDomains;
/// Total number of teams
@override final  int totalTeams;
/// Total number of users
@override final  int totalUsers;

/// Create a copy of AdminStatsDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdminStatsDtoCopyWith<_AdminStatsDto> get copyWith => __$AdminStatsDtoCopyWithImpl<_AdminStatsDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdminStatsDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdminStatsDto&&(identical(other.totalDomains, totalDomains) || other.totalDomains == totalDomains)&&(identical(other.totalTeams, totalTeams) || other.totalTeams == totalTeams)&&(identical(other.totalUsers, totalUsers) || other.totalUsers == totalUsers));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,totalDomains,totalTeams,totalUsers);

@override
String toString() {
  return 'AdminStatsDto(totalDomains: $totalDomains, totalTeams: $totalTeams, totalUsers: $totalUsers)';
}


}

/// @nodoc
abstract mixin class _$AdminStatsDtoCopyWith<$Res> implements $AdminStatsDtoCopyWith<$Res> {
  factory _$AdminStatsDtoCopyWith(_AdminStatsDto value, $Res Function(_AdminStatsDto) _then) = __$AdminStatsDtoCopyWithImpl;
@override @useResult
$Res call({
 int totalDomains, int totalTeams, int totalUsers
});




}
/// @nodoc
class __$AdminStatsDtoCopyWithImpl<$Res>
    implements _$AdminStatsDtoCopyWith<$Res> {
  __$AdminStatsDtoCopyWithImpl(this._self, this._then);

  final _AdminStatsDto _self;
  final $Res Function(_AdminStatsDto) _then;

/// Create a copy of AdminStatsDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? totalDomains = null,Object? totalTeams = null,Object? totalUsers = null,}) {
  return _then(_AdminStatsDto(
totalDomains: null == totalDomains ? _self.totalDomains : totalDomains // ignore: cast_nullable_to_non_nullable
as int,totalTeams: null == totalTeams ? _self.totalTeams : totalTeams // ignore: cast_nullable_to_non_nullable
as int,totalUsers: null == totalUsers ? _self.totalUsers : totalUsers // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
