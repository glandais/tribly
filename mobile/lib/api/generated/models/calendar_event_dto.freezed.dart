// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'calendar_event_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CalendarEventDto {

/// Event ID (TSID)
 String get id;/// Event title
 String get title;/// Event start date/time
 String get start;/// Is all-day event
 bool get allDay;/// Event type
 String get type;/// Team slug
 String get teamSlug;/// Team name
 String get teamName;/// Entity slug (ride or stage)
 String get entitySlug;/// Event end date/time
 String? get end;/// Parent trip slug (for stages only)
 String? get tripSlug;
/// Create a copy of CalendarEventDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CalendarEventDtoCopyWith<CalendarEventDto> get copyWith => _$CalendarEventDtoCopyWithImpl<CalendarEventDto>(this as CalendarEventDto, _$identity);

  /// Serializes this CalendarEventDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CalendarEventDto&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.start, start) || other.start == start)&&(identical(other.allDay, allDay) || other.allDay == allDay)&&(identical(other.type, type) || other.type == type)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.teamName, teamName) || other.teamName == teamName)&&(identical(other.entitySlug, entitySlug) || other.entitySlug == entitySlug)&&(identical(other.end, end) || other.end == end)&&(identical(other.tripSlug, tripSlug) || other.tripSlug == tripSlug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,title,start,allDay,type,teamSlug,teamName,entitySlug,end,tripSlug);

@override
String toString() {
  return 'CalendarEventDto(id: $id, title: $title, start: $start, allDay: $allDay, type: $type, teamSlug: $teamSlug, teamName: $teamName, entitySlug: $entitySlug, end: $end, tripSlug: $tripSlug)';
}


}

/// @nodoc
abstract mixin class $CalendarEventDtoCopyWith<$Res>  {
  factory $CalendarEventDtoCopyWith(CalendarEventDto value, $Res Function(CalendarEventDto) _then) = _$CalendarEventDtoCopyWithImpl;
@useResult
$Res call({
 String id, String title, String start, bool allDay, String type, String teamSlug, String teamName, String entitySlug, String? end, String? tripSlug
});




}
/// @nodoc
class _$CalendarEventDtoCopyWithImpl<$Res>
    implements $CalendarEventDtoCopyWith<$Res> {
  _$CalendarEventDtoCopyWithImpl(this._self, this._then);

  final CalendarEventDto _self;
  final $Res Function(CalendarEventDto) _then;

/// Create a copy of CalendarEventDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? title = null,Object? start = null,Object? allDay = null,Object? type = null,Object? teamSlug = null,Object? teamName = null,Object? entitySlug = null,Object? end = freezed,Object? tripSlug = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,start: null == start ? _self.start : start // ignore: cast_nullable_to_non_nullable
as String,allDay: null == allDay ? _self.allDay : allDay // ignore: cast_nullable_to_non_nullable
as bool,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,entitySlug: null == entitySlug ? _self.entitySlug : entitySlug // ignore: cast_nullable_to_non_nullable
as String,end: freezed == end ? _self.end : end // ignore: cast_nullable_to_non_nullable
as String?,tripSlug: freezed == tripSlug ? _self.tripSlug : tripSlug // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [CalendarEventDto].
extension CalendarEventDtoPatterns on CalendarEventDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CalendarEventDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CalendarEventDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CalendarEventDto value)  $default,){
final _that = this;
switch (_that) {
case _CalendarEventDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CalendarEventDto value)?  $default,){
final _that = this;
switch (_that) {
case _CalendarEventDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String title,  String start,  bool allDay,  String type,  String teamSlug,  String teamName,  String entitySlug,  String? end,  String? tripSlug)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CalendarEventDto() when $default != null:
return $default(_that.id,_that.title,_that.start,_that.allDay,_that.type,_that.teamSlug,_that.teamName,_that.entitySlug,_that.end,_that.tripSlug);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String title,  String start,  bool allDay,  String type,  String teamSlug,  String teamName,  String entitySlug,  String? end,  String? tripSlug)  $default,) {final _that = this;
switch (_that) {
case _CalendarEventDto():
return $default(_that.id,_that.title,_that.start,_that.allDay,_that.type,_that.teamSlug,_that.teamName,_that.entitySlug,_that.end,_that.tripSlug);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String title,  String start,  bool allDay,  String type,  String teamSlug,  String teamName,  String entitySlug,  String? end,  String? tripSlug)?  $default,) {final _that = this;
switch (_that) {
case _CalendarEventDto() when $default != null:
return $default(_that.id,_that.title,_that.start,_that.allDay,_that.type,_that.teamSlug,_that.teamName,_that.entitySlug,_that.end,_that.tripSlug);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CalendarEventDto implements CalendarEventDto {
  const _CalendarEventDto({required this.id, required this.title, required this.start, required this.allDay, required this.type, required this.teamSlug, required this.teamName, required this.entitySlug, this.end, this.tripSlug});
  factory _CalendarEventDto.fromJson(Map<String, dynamic> json) => _$CalendarEventDtoFromJson(json);

/// Event ID (TSID)
@override final  String id;
/// Event title
@override final  String title;
/// Event start date/time
@override final  String start;
/// Is all-day event
@override final  bool allDay;
/// Event type
@override final  String type;
/// Team slug
@override final  String teamSlug;
/// Team name
@override final  String teamName;
/// Entity slug (ride or stage)
@override final  String entitySlug;
/// Event end date/time
@override final  String? end;
/// Parent trip slug (for stages only)
@override final  String? tripSlug;

/// Create a copy of CalendarEventDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CalendarEventDtoCopyWith<_CalendarEventDto> get copyWith => __$CalendarEventDtoCopyWithImpl<_CalendarEventDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CalendarEventDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _CalendarEventDto&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.start, start) || other.start == start)&&(identical(other.allDay, allDay) || other.allDay == allDay)&&(identical(other.type, type) || other.type == type)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.teamName, teamName) || other.teamName == teamName)&&(identical(other.entitySlug, entitySlug) || other.entitySlug == entitySlug)&&(identical(other.end, end) || other.end == end)&&(identical(other.tripSlug, tripSlug) || other.tripSlug == tripSlug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,title,start,allDay,type,teamSlug,teamName,entitySlug,end,tripSlug);

@override
String toString() {
  return 'CalendarEventDto(id: $id, title: $title, start: $start, allDay: $allDay, type: $type, teamSlug: $teamSlug, teamName: $teamName, entitySlug: $entitySlug, end: $end, tripSlug: $tripSlug)';
}


}

/// @nodoc
abstract mixin class _$CalendarEventDtoCopyWith<$Res> implements $CalendarEventDtoCopyWith<$Res> {
  factory _$CalendarEventDtoCopyWith(_CalendarEventDto value, $Res Function(_CalendarEventDto) _then) = __$CalendarEventDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String title, String start, bool allDay, String type, String teamSlug, String teamName, String entitySlug, String? end, String? tripSlug
});




}
/// @nodoc
class __$CalendarEventDtoCopyWithImpl<$Res>
    implements _$CalendarEventDtoCopyWith<$Res> {
  __$CalendarEventDtoCopyWithImpl(this._self, this._then);

  final _CalendarEventDto _self;
  final $Res Function(_CalendarEventDto) _then;

/// Create a copy of CalendarEventDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? title = null,Object? start = null,Object? allDay = null,Object? type = null,Object? teamSlug = null,Object? teamName = null,Object? entitySlug = null,Object? end = freezed,Object? tripSlug = freezed,}) {
  return _then(_CalendarEventDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,start: null == start ? _self.start : start // ignore: cast_nullable_to_non_nullable
as String,allDay: null == allDay ? _self.allDay : allDay // ignore: cast_nullable_to_non_nullable
as bool,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,entitySlug: null == entitySlug ? _self.entitySlug : entitySlug // ignore: cast_nullable_to_non_nullable
as String,end: freezed == end ? _self.end : end // ignore: cast_nullable_to_non_nullable
as String?,tripSlug: freezed == tripSlug ? _self.tripSlug : tripSlug // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
