// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_timezone_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamTimezoneDto {

/// IANA zone of the point; the team's own when no point is given or the point lies outside every zone
 String get timezone;
/// Create a copy of TeamTimezoneDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamTimezoneDtoCopyWith<TeamTimezoneDto> get copyWith => _$TeamTimezoneDtoCopyWithImpl<TeamTimezoneDto>(this as TeamTimezoneDto, _$identity);

  /// Serializes this TeamTimezoneDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamTimezoneDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamTimezoneDto&&(identical(other.timezone, _this.timezone) || other.timezone == _this.timezone));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamTimezoneDto;
  return Object.hash(runtimeType,_this.timezone);
}

@override
String toString() {
  final _this = this as TeamTimezoneDto;
  return 'TeamTimezoneDto(timezone: ${_this.timezone})';
}


}

/// @nodoc
abstract mixin class $TeamTimezoneDtoCopyWith<$Res>  {
  factory $TeamTimezoneDtoCopyWith(TeamTimezoneDto value, $Res Function(TeamTimezoneDto) _then) = _$TeamTimezoneDtoCopyWithImpl;
@useResult
$Res call({
 String timezone
});




}
/// @nodoc
class _$TeamTimezoneDtoCopyWithImpl<$Res>
    implements $TeamTimezoneDtoCopyWith<$Res> {
  _$TeamTimezoneDtoCopyWithImpl(this._self, this._then);

  final TeamTimezoneDto _self;
  final $Res Function(TeamTimezoneDto) _then;

/// Create a copy of TeamTimezoneDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? timezone = null,}) {
  return _then(TeamTimezoneDto(
timezone: null == timezone ? _self.timezone : timezone // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamTimezoneDto].
extension TeamTimezoneDtoPatterns on TeamTimezoneDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamTimezoneDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamTimezoneDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamTimezoneDto value)  $default,){
final _that = this;
switch (_that) {
case _TeamTimezoneDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamTimezoneDto value)?  $default,){
final _that = this;
switch (_that) {
case _TeamTimezoneDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String timezone)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamTimezoneDto() when $default != null:
return $default(_that.timezone);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String timezone)  $default,) {final _that = this;
switch (_that) {
case _TeamTimezoneDto():
return $default(_that.timezone);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String timezone)?  $default,) {final _that = this;
switch (_that) {
case _TeamTimezoneDto() when $default != null:
return $default(_that.timezone);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamTimezoneDto implements TeamTimezoneDto {
  const _TeamTimezoneDto({required this.timezone});
  factory _TeamTimezoneDto.fromJson(Map<String, dynamic> json) => _$TeamTimezoneDtoFromJson(json);

/// IANA zone of the point; the team's own when no point is given or the point lies outside every zone
@override final  String timezone;

/// Create a copy of TeamTimezoneDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamTimezoneDtoCopyWith<_TeamTimezoneDto> get copyWith => __$TeamTimezoneDtoCopyWithImpl<_TeamTimezoneDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamTimezoneDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamTimezoneDto&&(identical(other.timezone, timezone) || other.timezone == timezone));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,timezone);
}

@override
String toString() {
    return 'TeamTimezoneDto(timezone: $timezone)';
}


}

/// @nodoc
abstract mixin class _$TeamTimezoneDtoCopyWith<$Res> implements $TeamTimezoneDtoCopyWith<$Res> {
  factory _$TeamTimezoneDtoCopyWith(_TeamTimezoneDto value, $Res Function(_TeamTimezoneDto) _then) = __$TeamTimezoneDtoCopyWithImpl;
@override @useResult
$Res call({
 String timezone
});




}
/// @nodoc
class __$TeamTimezoneDtoCopyWithImpl<$Res>
    implements _$TeamTimezoneDtoCopyWith<$Res> {
  __$TeamTimezoneDtoCopyWithImpl(this._self, this._then);

  final _TeamTimezoneDto _self;
  final $Res Function(_TeamTimezoneDto) _then;

/// Create a copy of TeamTimezoneDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? timezone = null,}) {
  return _then(_TeamTimezoneDto(
timezone: null == timezone ? _self.timezone : timezone // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
