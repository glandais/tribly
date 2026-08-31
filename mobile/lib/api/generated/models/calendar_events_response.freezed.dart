// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'calendar_events_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CalendarEventsResponse {

/// List of calendar events
 List<CalendarEventDto> get events;
/// Create a copy of CalendarEventsResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CalendarEventsResponseCopyWith<CalendarEventsResponse> get copyWith => _$CalendarEventsResponseCopyWithImpl<CalendarEventsResponse>(this as CalendarEventsResponse, _$identity);

  /// Serializes this CalendarEventsResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as CalendarEventsResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CalendarEventsResponse&&const DeepCollectionEquality().equals(other.events, _this.events));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as CalendarEventsResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.events));
}

@override
String toString() {
  final _this = this as CalendarEventsResponse;
  return 'CalendarEventsResponse(events: ${_this.events})';
}


}

/// @nodoc
abstract mixin class $CalendarEventsResponseCopyWith<$Res>  {
  factory $CalendarEventsResponseCopyWith(CalendarEventsResponse value, $Res Function(CalendarEventsResponse) _then) = _$CalendarEventsResponseCopyWithImpl;
@useResult
$Res call({
 List<CalendarEventDto> events
});




}
/// @nodoc
class _$CalendarEventsResponseCopyWithImpl<$Res>
    implements $CalendarEventsResponseCopyWith<$Res> {
  _$CalendarEventsResponseCopyWithImpl(this._self, this._then);

  final CalendarEventsResponse _self;
  final $Res Function(CalendarEventsResponse) _then;

/// Create a copy of CalendarEventsResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? events = null,}) {
  return _then(CalendarEventsResponse(
events: null == events ? _self.events : events // ignore: cast_nullable_to_non_nullable
as List<CalendarEventDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [CalendarEventsResponse].
extension CalendarEventsResponsePatterns on CalendarEventsResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CalendarEventsResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CalendarEventsResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CalendarEventsResponse value)  $default,){
final _that = this;
switch (_that) {
case _CalendarEventsResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CalendarEventsResponse value)?  $default,){
final _that = this;
switch (_that) {
case _CalendarEventsResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<CalendarEventDto> events)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CalendarEventsResponse() when $default != null:
return $default(_that.events);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<CalendarEventDto> events)  $default,) {final _that = this;
switch (_that) {
case _CalendarEventsResponse():
return $default(_that.events);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<CalendarEventDto> events)?  $default,) {final _that = this;
switch (_that) {
case _CalendarEventsResponse() when $default != null:
return $default(_that.events);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CalendarEventsResponse implements CalendarEventsResponse {
  const _CalendarEventsResponse({required  List<CalendarEventDto> events}): _events = events;
  factory _CalendarEventsResponse.fromJson(Map<String, dynamic> json) => _$CalendarEventsResponseFromJson(json);

/// List of calendar events
 final  List<CalendarEventDto> _events;
/// List of calendar events
@override List<CalendarEventDto> get events {
  if (_events is EqualUnmodifiableListView) return _events;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_events);
}


/// Create a copy of CalendarEventsResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CalendarEventsResponseCopyWith<_CalendarEventsResponse> get copyWith => __$CalendarEventsResponseCopyWithImpl<_CalendarEventsResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CalendarEventsResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _CalendarEventsResponse&&const DeepCollectionEquality().equals(other.events, _events));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_events));
}

@override
String toString() {
    return 'CalendarEventsResponse(events: $events)';
}


}

/// @nodoc
abstract mixin class _$CalendarEventsResponseCopyWith<$Res> implements $CalendarEventsResponseCopyWith<$Res> {
  factory _$CalendarEventsResponseCopyWith(_CalendarEventsResponse value, $Res Function(_CalendarEventsResponse) _then) = __$CalendarEventsResponseCopyWithImpl;
@override @useResult
$Res call({
 List<CalendarEventDto> events
});




}
/// @nodoc
class __$CalendarEventsResponseCopyWithImpl<$Res>
    implements _$CalendarEventsResponseCopyWith<$Res> {
  __$CalendarEventsResponseCopyWithImpl(this._self, this._then);

  final _CalendarEventsResponse _self;
  final $Res Function(_CalendarEventsResponse) _then;

/// Create a copy of CalendarEventsResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? events = null,}) {
  return _then(_CalendarEventsResponse(
events: null == events ? _self._events : events // ignore: cast_nullable_to_non_nullable
as List<CalendarEventDto>,
  ));
}


}

// dart format on
