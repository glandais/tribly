// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_timezone_change_preview_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamTimezoneChangePreviewDto {

/// Team's current zone
 String get from;/// Zone asked for
 String get to;/// How many upcoming place-less events keep their wall time
 int get upcomingCount;/// How many past place-less events keep their instant, relabelled
 int get pastCount;/// The first upcoming ones, soonest first, at most 10
 List<TeamTimezoneChangeItemDto> get upcoming;
/// Create a copy of TeamTimezoneChangePreviewDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamTimezoneChangePreviewDtoCopyWith<TeamTimezoneChangePreviewDto> get copyWith => _$TeamTimezoneChangePreviewDtoCopyWithImpl<TeamTimezoneChangePreviewDto>(this as TeamTimezoneChangePreviewDto, _$identity);

  /// Serializes this TeamTimezoneChangePreviewDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamTimezoneChangePreviewDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamTimezoneChangePreviewDto&&(identical(other.from, _this.from) || other.from == _this.from)&&(identical(other.to, _this.to) || other.to == _this.to)&&(identical(other.upcomingCount, _this.upcomingCount) || other.upcomingCount == _this.upcomingCount)&&(identical(other.pastCount, _this.pastCount) || other.pastCount == _this.pastCount)&&const DeepCollectionEquality().equals(other.upcoming, _this.upcoming));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamTimezoneChangePreviewDto;
  return Object.hash(runtimeType,_this.from,_this.to,_this.upcomingCount,_this.pastCount,const DeepCollectionEquality().hash(_this.upcoming));
}

@override
String toString() {
  final _this = this as TeamTimezoneChangePreviewDto;
  return 'TeamTimezoneChangePreviewDto(from: ${_this.from}, to: ${_this.to}, upcomingCount: ${_this.upcomingCount}, pastCount: ${_this.pastCount}, upcoming: ${_this.upcoming})';
}


}

/// @nodoc
abstract mixin class $TeamTimezoneChangePreviewDtoCopyWith<$Res>  {
  factory $TeamTimezoneChangePreviewDtoCopyWith(TeamTimezoneChangePreviewDto value, $Res Function(TeamTimezoneChangePreviewDto) _then) = _$TeamTimezoneChangePreviewDtoCopyWithImpl;
@useResult
$Res call({
 String from, String to, int upcomingCount, int pastCount, List<TeamTimezoneChangeItemDto> upcoming
});




}
/// @nodoc
class _$TeamTimezoneChangePreviewDtoCopyWithImpl<$Res>
    implements $TeamTimezoneChangePreviewDtoCopyWith<$Res> {
  _$TeamTimezoneChangePreviewDtoCopyWithImpl(this._self, this._then);

  final TeamTimezoneChangePreviewDto _self;
  final $Res Function(TeamTimezoneChangePreviewDto) _then;

/// Create a copy of TeamTimezoneChangePreviewDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? from = null,Object? to = null,Object? upcomingCount = null,Object? pastCount = null,Object? upcoming = null,}) {
  return _then(TeamTimezoneChangePreviewDto(
from: null == from ? _self.from : from // ignore: cast_nullable_to_non_nullable
as String,to: null == to ? _self.to : to // ignore: cast_nullable_to_non_nullable
as String,upcomingCount: null == upcomingCount ? _self.upcomingCount : upcomingCount // ignore: cast_nullable_to_non_nullable
as int,pastCount: null == pastCount ? _self.pastCount : pastCount // ignore: cast_nullable_to_non_nullable
as int,upcoming: null == upcoming ? _self.upcoming : upcoming // ignore: cast_nullable_to_non_nullable
as List<TeamTimezoneChangeItemDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamTimezoneChangePreviewDto].
extension TeamTimezoneChangePreviewDtoPatterns on TeamTimezoneChangePreviewDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamTimezoneChangePreviewDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamTimezoneChangePreviewDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamTimezoneChangePreviewDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamTimezoneChangePreviewDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamTimezoneChangePreviewDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamTimezoneChangePreviewDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String from,  String to,  int upcomingCount,  int pastCount,  List<TeamTimezoneChangeItemDto> upcoming)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamTimezoneChangePreviewDto() when $default != null:
return $default(_that.from,_that.to,_that.upcomingCount,_that.pastCount,_that.upcoming);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String from,  String to,  int upcomingCount,  int pastCount,  List<TeamTimezoneChangeItemDto> upcoming)  $default,) {final _that = this;
switch (_that) {
case _TeamTimezoneChangePreviewDto():
return $default(_that.from,_that.to,_that.upcomingCount,_that.pastCount,_that.upcoming);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String from,  String to,  int upcomingCount,  int pastCount,  List<TeamTimezoneChangeItemDto> upcoming)?  $default,) {final _that = this;
switch (_that) {
case _TeamTimezoneChangePreviewDto() when $default != null:
return $default(_that.from,_that.to,_that.upcomingCount,_that.pastCount,_that.upcoming);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamTimezoneChangePreviewDto implements TeamTimezoneChangePreviewDto {
  const _TeamTimezoneChangePreviewDto({required this.from, required this.to, required this.upcomingCount, required this.pastCount, required  List<TeamTimezoneChangeItemDto> upcoming}): _upcoming = upcoming;
  factory _TeamTimezoneChangePreviewDto.fromJson(Map<String, dynamic> json) => _$TeamTimezoneChangePreviewDtoFromJson(json);

/// Team's current zone
@override final  String from;
/// Zone asked for
@override final  String to;
/// How many upcoming place-less events keep their wall time
@override final  int upcomingCount;
/// How many past place-less events keep their instant, relabelled
@override final  int pastCount;
/// The first upcoming ones, soonest first, at most 10
 final  List<TeamTimezoneChangeItemDto> _upcoming;
/// The first upcoming ones, soonest first, at most 10
@override List<TeamTimezoneChangeItemDto> get upcoming {
  if (_upcoming is EqualUnmodifiableListView) return _upcoming;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_upcoming);
}


/// Create a copy of TeamTimezoneChangePreviewDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamTimezoneChangePreviewDtoCopyWith<_TeamTimezoneChangePreviewDto> get copyWith => __$TeamTimezoneChangePreviewDtoCopyWithImpl<_TeamTimezoneChangePreviewDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamTimezoneChangePreviewDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamTimezoneChangePreviewDto&&(identical(other.from, from) || other.from == from)&&(identical(other.to, to) || other.to == to)&&(identical(other.upcomingCount, upcomingCount) || other.upcomingCount == upcomingCount)&&(identical(other.pastCount, pastCount) || other.pastCount == pastCount)&&const DeepCollectionEquality().equals(other.upcoming, _upcoming));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,from,to,upcomingCount,pastCount,const DeepCollectionEquality().hash(_upcoming));
}

@override
String toString() {
    return 'TeamTimezoneChangePreviewDto(from: $from, to: $to, upcomingCount: $upcomingCount, pastCount: $pastCount, upcoming: $upcoming)';
}


}

/// @nodoc
abstract mixin class _$TeamTimezoneChangePreviewDtoCopyWith<$Res> implements $TeamTimezoneChangePreviewDtoCopyWith<$Res> {
  factory _$TeamTimezoneChangePreviewDtoCopyWith(_TeamTimezoneChangePreviewDto value, $Res Function(_TeamTimezoneChangePreviewDto) _then) = __$TeamTimezoneChangePreviewDtoCopyWithImpl;
@override @useResult
$Res call({
 String from, String to, int upcomingCount, int pastCount, List<TeamTimezoneChangeItemDto> upcoming
});




}
/// @nodoc
class __$TeamTimezoneChangePreviewDtoCopyWithImpl<$Res>
    implements _$TeamTimezoneChangePreviewDtoCopyWith<$Res> {
  __$TeamTimezoneChangePreviewDtoCopyWithImpl(this._self, this._then);

  final _TeamTimezoneChangePreviewDto _self;
  final $Res Function(_TeamTimezoneChangePreviewDto) _then;

/// Create a copy of TeamTimezoneChangePreviewDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? from = null,Object? to = null,Object? upcomingCount = null,Object? pastCount = null,Object? upcoming = null,}) {
  return _then(_TeamTimezoneChangePreviewDto(
from: null == from ? _self.from : from // ignore: cast_nullable_to_non_nullable
as String,to: null == to ? _self.to : to // ignore: cast_nullable_to_non_nullable
as String,upcomingCount: null == upcomingCount ? _self.upcomingCount : upcomingCount // ignore: cast_nullable_to_non_nullable
as int,pastCount: null == pastCount ? _self.pastCount : pastCount // ignore: cast_nullable_to_non_nullable
as int,upcoming: null == upcoming ? _self._upcoming : upcoming // ignore: cast_nullable_to_non_nullable
as List<TeamTimezoneChangeItemDto>,
  ));
}


}

// dart format on
