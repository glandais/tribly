// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'profile_participation_summary_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ProfileParticipationSummaryDto {

/// Outings starting from now on
 int get upcomingCount;/// Outings that started before now
 int get pastCount;/// The next outing, in the compact list view (no markdown body): empty when nothing is coming up, never more than one row
 List<PublicationDto> get next;
/// Create a copy of ProfileParticipationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ProfileParticipationSummaryDtoCopyWith<ProfileParticipationSummaryDto> get copyWith => _$ProfileParticipationSummaryDtoCopyWithImpl<ProfileParticipationSummaryDto>(this as ProfileParticipationSummaryDto, _$identity);

  /// Serializes this ProfileParticipationSummaryDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ProfileParticipationSummaryDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ProfileParticipationSummaryDto&&(identical(other.upcomingCount, _this.upcomingCount) || other.upcomingCount == _this.upcomingCount)&&(identical(other.pastCount, _this.pastCount) || other.pastCount == _this.pastCount)&&const DeepCollectionEquality().equals(other.next, _this.next));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ProfileParticipationSummaryDto;
  return Object.hash(runtimeType,_this.upcomingCount,_this.pastCount,const DeepCollectionEquality().hash(_this.next));
}

@override
String toString() {
  final _this = this as ProfileParticipationSummaryDto;
  return 'ProfileParticipationSummaryDto(upcomingCount: ${_this.upcomingCount}, pastCount: ${_this.pastCount}, next: ${_this.next})';
}


}

/// @nodoc
abstract mixin class $ProfileParticipationSummaryDtoCopyWith<$Res>  {
  factory $ProfileParticipationSummaryDtoCopyWith(ProfileParticipationSummaryDto value, $Res Function(ProfileParticipationSummaryDto) _then) = _$ProfileParticipationSummaryDtoCopyWithImpl;
@useResult
$Res call({
 int upcomingCount, int pastCount, List<PublicationDto> next
});




}
/// @nodoc
class _$ProfileParticipationSummaryDtoCopyWithImpl<$Res>
    implements $ProfileParticipationSummaryDtoCopyWith<$Res> {
  _$ProfileParticipationSummaryDtoCopyWithImpl(this._self, this._then);

  final ProfileParticipationSummaryDto _self;
  final $Res Function(ProfileParticipationSummaryDto) _then;

/// Create a copy of ProfileParticipationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? upcomingCount = null,Object? pastCount = null,Object? next = null,}) {
  return _then(ProfileParticipationSummaryDto(
upcomingCount: null == upcomingCount ? _self.upcomingCount : upcomingCount // ignore: cast_nullable_to_non_nullable
as int,pastCount: null == pastCount ? _self.pastCount : pastCount // ignore: cast_nullable_to_non_nullable
as int,next: null == next ? _self.next : next // ignore: cast_nullable_to_non_nullable
as List<PublicationDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [ProfileParticipationSummaryDto].
extension ProfileParticipationSummaryDtoPatterns on ProfileParticipationSummaryDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ProfileParticipationSummaryDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ProfileParticipationSummaryDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ProfileParticipationSummaryDto value)  $default,){
final _that = this;
switch (_that) {
case _ProfileParticipationSummaryDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ProfileParticipationSummaryDto value)?  $default,){
final _that = this;
switch (_that) {
case _ProfileParticipationSummaryDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int upcomingCount,  int pastCount,  List<PublicationDto> next)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ProfileParticipationSummaryDto() when $default != null:
return $default(_that.upcomingCount,_that.pastCount,_that.next);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int upcomingCount,  int pastCount,  List<PublicationDto> next)  $default,) {final _that = this;
switch (_that) {
case _ProfileParticipationSummaryDto():
return $default(_that.upcomingCount,_that.pastCount,_that.next);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int upcomingCount,  int pastCount,  List<PublicationDto> next)?  $default,) {final _that = this;
switch (_that) {
case _ProfileParticipationSummaryDto() when $default != null:
return $default(_that.upcomingCount,_that.pastCount,_that.next);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ProfileParticipationSummaryDto implements ProfileParticipationSummaryDto {
  const _ProfileParticipationSummaryDto({required this.upcomingCount, required this.pastCount, required  List<PublicationDto> next}): _next = next;
  factory _ProfileParticipationSummaryDto.fromJson(Map<String, dynamic> json) => _$ProfileParticipationSummaryDtoFromJson(json);

/// Outings starting from now on
@override final  int upcomingCount;
/// Outings that started before now
@override final  int pastCount;
/// The next outing, in the compact list view (no markdown body): empty when nothing is coming up, never more than one row
 final  List<PublicationDto> _next;
/// The next outing, in the compact list view (no markdown body): empty when nothing is coming up, never more than one row
@override List<PublicationDto> get next {
  if (_next is EqualUnmodifiableListView) return _next;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_next);
}


/// Create a copy of ProfileParticipationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ProfileParticipationSummaryDtoCopyWith<_ProfileParticipationSummaryDto> get copyWith => __$ProfileParticipationSummaryDtoCopyWithImpl<_ProfileParticipationSummaryDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ProfileParticipationSummaryDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ProfileParticipationSummaryDto&&(identical(other.upcomingCount, upcomingCount) || other.upcomingCount == upcomingCount)&&(identical(other.pastCount, pastCount) || other.pastCount == pastCount)&&const DeepCollectionEquality().equals(other.next, _next));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,upcomingCount,pastCount,const DeepCollectionEquality().hash(_next));
}

@override
String toString() {
    return 'ProfileParticipationSummaryDto(upcomingCount: $upcomingCount, pastCount: $pastCount, next: $next)';
}


}

/// @nodoc
abstract mixin class _$ProfileParticipationSummaryDtoCopyWith<$Res> implements $ProfileParticipationSummaryDtoCopyWith<$Res> {
  factory _$ProfileParticipationSummaryDtoCopyWith(_ProfileParticipationSummaryDto value, $Res Function(_ProfileParticipationSummaryDto) _then) = __$ProfileParticipationSummaryDtoCopyWithImpl;
@override @useResult
$Res call({
 int upcomingCount, int pastCount, List<PublicationDto> next
});




}
/// @nodoc
class __$ProfileParticipationSummaryDtoCopyWithImpl<$Res>
    implements _$ProfileParticipationSummaryDtoCopyWith<$Res> {
  __$ProfileParticipationSummaryDtoCopyWithImpl(this._self, this._then);

  final _ProfileParticipationSummaryDto _self;
  final $Res Function(_ProfileParticipationSummaryDto) _then;

/// Create a copy of ProfileParticipationSummaryDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? upcomingCount = null,Object? pastCount = null,Object? next = null,}) {
  return _then(_ProfileParticipationSummaryDto(
upcomingCount: null == upcomingCount ? _self.upcomingCount : upcomingCount // ignore: cast_nullable_to_non_nullable
as int,pastCount: null == pastCount ? _self.pastCount : pastCount // ignore: cast_nullable_to_non_nullable
as int,next: null == next ? _self._next : next // ignore: cast_nullable_to_non_nullable
as List<PublicationDto>,
  ));
}


}

// dart format on
