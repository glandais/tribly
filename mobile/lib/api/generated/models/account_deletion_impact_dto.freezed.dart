// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'account_deletion_impact_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AccountDeletionImpactDto {

/// Whether the deletion is refused: the user is the only admin of at least one team that has other members (SOLE_TEAM_ADMIN), or of a team migrated from biketeam (SOLE_MIGRATED_TEAM_ADMIN)
 bool get blocked;/// Teams the user is the only admin of while other members remain; they must name another admin, or delete the team, before deleting their account. Excludes the teams listed in migratedTeams
 List<TeamPublicationDto> get blockingTeams;/// Teams the user administers and is the only member of; they are deleted with the account. Excludes the teams listed in migratedTeams
 List<TeamPublicationDto> get deletedTeams;/// Teams the user is the only admin of, with or without other members, that were migrated from biketeam: their old biketeam addresses redirect to them. Each one refuses the deletion until another admin is named (or, for a platform admin, the switch-over is cancelled on biketeam and the team deleted)
 List<TeamPublicationDto> get migratedTeams;
/// Create a copy of AccountDeletionImpactDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AccountDeletionImpactDtoCopyWith<AccountDeletionImpactDto> get copyWith => _$AccountDeletionImpactDtoCopyWithImpl<AccountDeletionImpactDto>(this as AccountDeletionImpactDto, _$identity);

  /// Serializes this AccountDeletionImpactDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as AccountDeletionImpactDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AccountDeletionImpactDto&&(identical(other.blocked, _this.blocked) || other.blocked == _this.blocked)&&const DeepCollectionEquality().equals(other.blockingTeams, _this.blockingTeams)&&const DeepCollectionEquality().equals(other.deletedTeams, _this.deletedTeams)&&const DeepCollectionEquality().equals(other.migratedTeams, _this.migratedTeams));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as AccountDeletionImpactDto;
  return Object.hash(runtimeType,_this.blocked,const DeepCollectionEquality().hash(_this.blockingTeams),const DeepCollectionEquality().hash(_this.deletedTeams),const DeepCollectionEquality().hash(_this.migratedTeams));
}

@override
String toString() {
  final _this = this as AccountDeletionImpactDto;
  return 'AccountDeletionImpactDto(blocked: ${_this.blocked}, blockingTeams: ${_this.blockingTeams}, deletedTeams: ${_this.deletedTeams}, migratedTeams: ${_this.migratedTeams})';
}


}

/// @nodoc
abstract mixin class $AccountDeletionImpactDtoCopyWith<$Res>  {
  factory $AccountDeletionImpactDtoCopyWith(AccountDeletionImpactDto value, $Res Function(AccountDeletionImpactDto) _then) = _$AccountDeletionImpactDtoCopyWithImpl;
@useResult
$Res call({
 bool blocked, List<TeamPublicationDto> blockingTeams, List<TeamPublicationDto> deletedTeams, List<TeamPublicationDto> migratedTeams
});




}
/// @nodoc
class _$AccountDeletionImpactDtoCopyWithImpl<$Res>
    implements $AccountDeletionImpactDtoCopyWith<$Res> {
  _$AccountDeletionImpactDtoCopyWithImpl(this._self, this._then);

  final AccountDeletionImpactDto _self;
  final $Res Function(AccountDeletionImpactDto) _then;

/// Create a copy of AccountDeletionImpactDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? blocked = null,Object? blockingTeams = null,Object? deletedTeams = null,Object? migratedTeams = null,}) {
  return _then(AccountDeletionImpactDto(
blocked: null == blocked ? _self.blocked : blocked // ignore: cast_nullable_to_non_nullable
as bool,blockingTeams: null == blockingTeams ? _self.blockingTeams : blockingTeams // ignore: cast_nullable_to_non_nullable
as List<TeamPublicationDto>,deletedTeams: null == deletedTeams ? _self.deletedTeams : deletedTeams // ignore: cast_nullable_to_non_nullable
as List<TeamPublicationDto>,migratedTeams: null == migratedTeams ? _self.migratedTeams : migratedTeams // ignore: cast_nullable_to_non_nullable
as List<TeamPublicationDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [AccountDeletionImpactDto].
extension AccountDeletionImpactDtoPatterns on AccountDeletionImpactDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AccountDeletionImpactDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AccountDeletionImpactDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AccountDeletionImpactDto value)  $default,){
final _that = this;
switch (_that) {
case _AccountDeletionImpactDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AccountDeletionImpactDto value)?  $default,){
final _that = this;
switch (_that) {
case _AccountDeletionImpactDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( bool blocked,  List<TeamPublicationDto> blockingTeams,  List<TeamPublicationDto> deletedTeams,  List<TeamPublicationDto> migratedTeams)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AccountDeletionImpactDto() when $default != null:
return $default(_that.blocked,_that.blockingTeams,_that.deletedTeams,_that.migratedTeams);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( bool blocked,  List<TeamPublicationDto> blockingTeams,  List<TeamPublicationDto> deletedTeams,  List<TeamPublicationDto> migratedTeams)  $default,) {final _that = this;
switch (_that) {
case _AccountDeletionImpactDto():
return $default(_that.blocked,_that.blockingTeams,_that.deletedTeams,_that.migratedTeams);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( bool blocked,  List<TeamPublicationDto> blockingTeams,  List<TeamPublicationDto> deletedTeams,  List<TeamPublicationDto> migratedTeams)?  $default,) {final _that = this;
switch (_that) {
case _AccountDeletionImpactDto() when $default != null:
return $default(_that.blocked,_that.blockingTeams,_that.deletedTeams,_that.migratedTeams);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AccountDeletionImpactDto implements AccountDeletionImpactDto {
  const _AccountDeletionImpactDto({required this.blocked, required  List<TeamPublicationDto> blockingTeams, required  List<TeamPublicationDto> deletedTeams, required  List<TeamPublicationDto> migratedTeams}): _blockingTeams = blockingTeams,_deletedTeams = deletedTeams,_migratedTeams = migratedTeams;
  factory _AccountDeletionImpactDto.fromJson(Map<String, dynamic> json) => _$AccountDeletionImpactDtoFromJson(json);

/// Whether the deletion is refused: the user is the only admin of at least one team that has other members (SOLE_TEAM_ADMIN), or of a team migrated from biketeam (SOLE_MIGRATED_TEAM_ADMIN)
@override final  bool blocked;
/// Teams the user is the only admin of while other members remain; they must name another admin, or delete the team, before deleting their account. Excludes the teams listed in migratedTeams
 final  List<TeamPublicationDto> _blockingTeams;
/// Teams the user is the only admin of while other members remain; they must name another admin, or delete the team, before deleting their account. Excludes the teams listed in migratedTeams
@override List<TeamPublicationDto> get blockingTeams {
  if (_blockingTeams is EqualUnmodifiableListView) return _blockingTeams;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_blockingTeams);
}

/// Teams the user administers and is the only member of; they are deleted with the account. Excludes the teams listed in migratedTeams
 final  List<TeamPublicationDto> _deletedTeams;
/// Teams the user administers and is the only member of; they are deleted with the account. Excludes the teams listed in migratedTeams
@override List<TeamPublicationDto> get deletedTeams {
  if (_deletedTeams is EqualUnmodifiableListView) return _deletedTeams;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_deletedTeams);
}

/// Teams the user is the only admin of, with or without other members, that were migrated from biketeam: their old biketeam addresses redirect to them. Each one refuses the deletion until another admin is named (or, for a platform admin, the switch-over is cancelled on biketeam and the team deleted)
 final  List<TeamPublicationDto> _migratedTeams;
/// Teams the user is the only admin of, with or without other members, that were migrated from biketeam: their old biketeam addresses redirect to them. Each one refuses the deletion until another admin is named (or, for a platform admin, the switch-over is cancelled on biketeam and the team deleted)
@override List<TeamPublicationDto> get migratedTeams {
  if (_migratedTeams is EqualUnmodifiableListView) return _migratedTeams;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_migratedTeams);
}


/// Create a copy of AccountDeletionImpactDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AccountDeletionImpactDtoCopyWith<_AccountDeletionImpactDto> get copyWith => __$AccountDeletionImpactDtoCopyWithImpl<_AccountDeletionImpactDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AccountDeletionImpactDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _AccountDeletionImpactDto&&(identical(other.blocked, blocked) || other.blocked == blocked)&&const DeepCollectionEquality().equals(other.blockingTeams, _blockingTeams)&&const DeepCollectionEquality().equals(other.deletedTeams, _deletedTeams)&&const DeepCollectionEquality().equals(other.migratedTeams, _migratedTeams));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,blocked,const DeepCollectionEquality().hash(_blockingTeams),const DeepCollectionEquality().hash(_deletedTeams),const DeepCollectionEquality().hash(_migratedTeams));
}

@override
String toString() {
    return 'AccountDeletionImpactDto(blocked: $blocked, blockingTeams: $blockingTeams, deletedTeams: $deletedTeams, migratedTeams: $migratedTeams)';
}


}

/// @nodoc
abstract mixin class _$AccountDeletionImpactDtoCopyWith<$Res> implements $AccountDeletionImpactDtoCopyWith<$Res> {
  factory _$AccountDeletionImpactDtoCopyWith(_AccountDeletionImpactDto value, $Res Function(_AccountDeletionImpactDto) _then) = __$AccountDeletionImpactDtoCopyWithImpl;
@override @useResult
$Res call({
 bool blocked, List<TeamPublicationDto> blockingTeams, List<TeamPublicationDto> deletedTeams, List<TeamPublicationDto> migratedTeams
});




}
/// @nodoc
class __$AccountDeletionImpactDtoCopyWithImpl<$Res>
    implements _$AccountDeletionImpactDtoCopyWith<$Res> {
  __$AccountDeletionImpactDtoCopyWithImpl(this._self, this._then);

  final _AccountDeletionImpactDto _self;
  final $Res Function(_AccountDeletionImpactDto) _then;

/// Create a copy of AccountDeletionImpactDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? blocked = null,Object? blockingTeams = null,Object? deletedTeams = null,Object? migratedTeams = null,}) {
  return _then(_AccountDeletionImpactDto(
blocked: null == blocked ? _self.blocked : blocked // ignore: cast_nullable_to_non_nullable
as bool,blockingTeams: null == blockingTeams ? _self._blockingTeams : blockingTeams // ignore: cast_nullable_to_non_nullable
as List<TeamPublicationDto>,deletedTeams: null == deletedTeams ? _self._deletedTeams : deletedTeams // ignore: cast_nullable_to_non_nullable
as List<TeamPublicationDto>,migratedTeams: null == migratedTeams ? _self._migratedTeams : migratedTeams // ignore: cast_nullable_to_non_nullable
as List<TeamPublicationDto>,
  ));
}


}

// dart format on
