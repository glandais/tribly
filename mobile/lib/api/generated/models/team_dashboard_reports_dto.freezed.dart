// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_dashboard_reports_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamDashboardReportsDto {

/// Reported targets waiting for a decision — the number of items of the open queue
 int get openCount;/// Reason of the most recent open report, null when none
 String? get latestReason;/// What the most recent open report is about, null when none
 String? get latestTargetType;/// The reported text as it was when the most recent open report was filed, null when none
 String? get latestExcerpt;/// When the most recent open report was filed, null when none
 String? get latestReportedAt;
/// Create a copy of TeamDashboardReportsDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamDashboardReportsDtoCopyWith<TeamDashboardReportsDto> get copyWith => _$TeamDashboardReportsDtoCopyWithImpl<TeamDashboardReportsDto>(this as TeamDashboardReportsDto, _$identity);

  /// Serializes this TeamDashboardReportsDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamDashboardReportsDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamDashboardReportsDto&&(identical(other.openCount, _this.openCount) || other.openCount == _this.openCount)&&(identical(other.latestReason, _this.latestReason) || other.latestReason == _this.latestReason)&&(identical(other.latestTargetType, _this.latestTargetType) || other.latestTargetType == _this.latestTargetType)&&(identical(other.latestExcerpt, _this.latestExcerpt) || other.latestExcerpt == _this.latestExcerpt)&&(identical(other.latestReportedAt, _this.latestReportedAt) || other.latestReportedAt == _this.latestReportedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamDashboardReportsDto;
  return Object.hash(runtimeType,_this.openCount,_this.latestReason,_this.latestTargetType,_this.latestExcerpt,_this.latestReportedAt);
}

@override
String toString() {
  final _this = this as TeamDashboardReportsDto;
  return 'TeamDashboardReportsDto(openCount: ${_this.openCount}, latestReason: ${_this.latestReason}, latestTargetType: ${_this.latestTargetType}, latestExcerpt: ${_this.latestExcerpt}, latestReportedAt: ${_this.latestReportedAt})';
}


}

/// @nodoc
abstract mixin class $TeamDashboardReportsDtoCopyWith<$Res>  {
  factory $TeamDashboardReportsDtoCopyWith(TeamDashboardReportsDto value, $Res Function(TeamDashboardReportsDto) _then) = _$TeamDashboardReportsDtoCopyWithImpl;
@useResult
$Res call({
 int openCount, String? latestReason, String? latestTargetType, String? latestExcerpt, String? latestReportedAt
});




}
/// @nodoc
class _$TeamDashboardReportsDtoCopyWithImpl<$Res>
    implements $TeamDashboardReportsDtoCopyWith<$Res> {
  _$TeamDashboardReportsDtoCopyWithImpl(this._self, this._then);

  final TeamDashboardReportsDto _self;
  final $Res Function(TeamDashboardReportsDto) _then;

/// Create a copy of TeamDashboardReportsDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? openCount = null,Object? latestReason = freezed,Object? latestTargetType = freezed,Object? latestExcerpt = freezed,Object? latestReportedAt = freezed,}) {
  return _then(TeamDashboardReportsDto(
openCount: null == openCount ? _self.openCount : openCount // ignore: cast_nullable_to_non_nullable
as int,latestReason: freezed == latestReason ? _self.latestReason : latestReason // ignore: cast_nullable_to_non_nullable
as String?,latestTargetType: freezed == latestTargetType ? _self.latestTargetType : latestTargetType // ignore: cast_nullable_to_non_nullable
as String?,latestExcerpt: freezed == latestExcerpt ? _self.latestExcerpt : latestExcerpt // ignore: cast_nullable_to_non_nullable
as String?,latestReportedAt: freezed == latestReportedAt ? _self.latestReportedAt : latestReportedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamDashboardReportsDto].
extension TeamDashboardReportsDtoPatterns on TeamDashboardReportsDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamDashboardReportsDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamDashboardReportsDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamDashboardReportsDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamDashboardReportsDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamDashboardReportsDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamDashboardReportsDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int openCount,  String? latestReason,  String? latestTargetType,  String? latestExcerpt,  String? latestReportedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamDashboardReportsDto() when $default != null:
return $default(_that.openCount,_that.latestReason,_that.latestTargetType,_that.latestExcerpt,_that.latestReportedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int openCount,  String? latestReason,  String? latestTargetType,  String? latestExcerpt,  String? latestReportedAt)  $default,) {final _that = this;
switch (_that) {
case _TeamDashboardReportsDto():
return $default(_that.openCount,_that.latestReason,_that.latestTargetType,_that.latestExcerpt,_that.latestReportedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int openCount,  String? latestReason,  String? latestTargetType,  String? latestExcerpt,  String? latestReportedAt)?  $default,) {final _that = this;
switch (_that) {
case _TeamDashboardReportsDto() when $default != null:
return $default(_that.openCount,_that.latestReason,_that.latestTargetType,_that.latestExcerpt,_that.latestReportedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamDashboardReportsDto implements TeamDashboardReportsDto {
  const _TeamDashboardReportsDto({required this.openCount, this.latestReason, this.latestTargetType, this.latestExcerpt, this.latestReportedAt});
  factory _TeamDashboardReportsDto.fromJson(Map<String, dynamic> json) => _$TeamDashboardReportsDtoFromJson(json);

/// Reported targets waiting for a decision — the number of items of the open queue
@override final  int openCount;
/// Reason of the most recent open report, null when none
@override final  String? latestReason;
/// What the most recent open report is about, null when none
@override final  String? latestTargetType;
/// The reported text as it was when the most recent open report was filed, null when none
@override final  String? latestExcerpt;
/// When the most recent open report was filed, null when none
@override final  String? latestReportedAt;

/// Create a copy of TeamDashboardReportsDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamDashboardReportsDtoCopyWith<_TeamDashboardReportsDto> get copyWith => __$TeamDashboardReportsDtoCopyWithImpl<_TeamDashboardReportsDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamDashboardReportsDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamDashboardReportsDto&&(identical(other.openCount, openCount) || other.openCount == openCount)&&(identical(other.latestReason, latestReason) || other.latestReason == latestReason)&&(identical(other.latestTargetType, latestTargetType) || other.latestTargetType == latestTargetType)&&(identical(other.latestExcerpt, latestExcerpt) || other.latestExcerpt == latestExcerpt)&&(identical(other.latestReportedAt, latestReportedAt) || other.latestReportedAt == latestReportedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,openCount,latestReason,latestTargetType,latestExcerpt,latestReportedAt);
}

@override
String toString() {
    return 'TeamDashboardReportsDto(openCount: $openCount, latestReason: $latestReason, latestTargetType: $latestTargetType, latestExcerpt: $latestExcerpt, latestReportedAt: $latestReportedAt)';
}


}

/// @nodoc
abstract mixin class _$TeamDashboardReportsDtoCopyWith<$Res> implements $TeamDashboardReportsDtoCopyWith<$Res> {
  factory _$TeamDashboardReportsDtoCopyWith(_TeamDashboardReportsDto value, $Res Function(_TeamDashboardReportsDto) _then) = __$TeamDashboardReportsDtoCopyWithImpl;
@override @useResult
$Res call({
 int openCount, String? latestReason, String? latestTargetType, String? latestExcerpt, String? latestReportedAt
});




}
/// @nodoc
class __$TeamDashboardReportsDtoCopyWithImpl<$Res>
    implements _$TeamDashboardReportsDtoCopyWith<$Res> {
  __$TeamDashboardReportsDtoCopyWithImpl(this._self, this._then);

  final _TeamDashboardReportsDto _self;
  final $Res Function(_TeamDashboardReportsDto) _then;

/// Create a copy of TeamDashboardReportsDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? openCount = null,Object? latestReason = freezed,Object? latestTargetType = freezed,Object? latestExcerpt = freezed,Object? latestReportedAt = freezed,}) {
  return _then(_TeamDashboardReportsDto(
openCount: null == openCount ? _self.openCount : openCount // ignore: cast_nullable_to_non_nullable
as int,latestReason: freezed == latestReason ? _self.latestReason : latestReason // ignore: cast_nullable_to_non_nullable
as String?,latestTargetType: freezed == latestTargetType ? _self.latestTargetType : latestTargetType // ignore: cast_nullable_to_non_nullable
as String?,latestExcerpt: freezed == latestExcerpt ? _self.latestExcerpt : latestExcerpt // ignore: cast_nullable_to_non_nullable
as String?,latestReportedAt: freezed == latestReportedAt ? _self.latestReportedAt : latestReportedAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
