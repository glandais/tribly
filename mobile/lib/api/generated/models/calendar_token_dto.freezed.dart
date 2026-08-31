// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'calendar_token_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CalendarTokenDto {

/// ICS feed token (include in URL)
 String get token;/// Global ICS feed URL
 String get globalFeedUrl;/// Team-specific feed URL template
 String get teamFeedUrlTemplate;
/// Create a copy of CalendarTokenDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CalendarTokenDtoCopyWith<CalendarTokenDto> get copyWith => _$CalendarTokenDtoCopyWithImpl<CalendarTokenDto>(this as CalendarTokenDto, _$identity);

  /// Serializes this CalendarTokenDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as CalendarTokenDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CalendarTokenDto&&(identical(other.token, _this.token) || other.token == _this.token)&&(identical(other.globalFeedUrl, _this.globalFeedUrl) || other.globalFeedUrl == _this.globalFeedUrl)&&(identical(other.teamFeedUrlTemplate, _this.teamFeedUrlTemplate) || other.teamFeedUrlTemplate == _this.teamFeedUrlTemplate));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as CalendarTokenDto;
  return Object.hash(runtimeType,_this.token,_this.globalFeedUrl,_this.teamFeedUrlTemplate);
}

@override
String toString() {
  final _this = this as CalendarTokenDto;
  return 'CalendarTokenDto(token: ${_this.token}, globalFeedUrl: ${_this.globalFeedUrl}, teamFeedUrlTemplate: ${_this.teamFeedUrlTemplate})';
}


}

/// @nodoc
abstract mixin class $CalendarTokenDtoCopyWith<$Res>  {
  factory $CalendarTokenDtoCopyWith(CalendarTokenDto value, $Res Function(CalendarTokenDto) _then) = _$CalendarTokenDtoCopyWithImpl;
@useResult
$Res call({
 String token, String globalFeedUrl, String teamFeedUrlTemplate
});




}
/// @nodoc
class _$CalendarTokenDtoCopyWithImpl<$Res>
    implements $CalendarTokenDtoCopyWith<$Res> {
  _$CalendarTokenDtoCopyWithImpl(this._self, this._then);

  final CalendarTokenDto _self;
  final $Res Function(CalendarTokenDto) _then;

/// Create a copy of CalendarTokenDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? token = null,Object? globalFeedUrl = null,Object? teamFeedUrlTemplate = null,}) {
  return _then(CalendarTokenDto(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,globalFeedUrl: null == globalFeedUrl ? _self.globalFeedUrl : globalFeedUrl // ignore: cast_nullable_to_non_nullable
as String,teamFeedUrlTemplate: null == teamFeedUrlTemplate ? _self.teamFeedUrlTemplate : teamFeedUrlTemplate // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [CalendarTokenDto].
extension CalendarTokenDtoPatterns on CalendarTokenDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CalendarTokenDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CalendarTokenDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CalendarTokenDto value)  $default,){
final _that = this;
switch (_that) {
case _CalendarTokenDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CalendarTokenDto value)?  $default,){
final _that = this;
switch (_that) {
case _CalendarTokenDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String token,  String globalFeedUrl,  String teamFeedUrlTemplate)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CalendarTokenDto() when $default != null:
return $default(_that.token,_that.globalFeedUrl,_that.teamFeedUrlTemplate);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String token,  String globalFeedUrl,  String teamFeedUrlTemplate)  $default,) {final _that = this;
switch (_that) {
case _CalendarTokenDto():
return $default(_that.token,_that.globalFeedUrl,_that.teamFeedUrlTemplate);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String token,  String globalFeedUrl,  String teamFeedUrlTemplate)?  $default,) {final _that = this;
switch (_that) {
case _CalendarTokenDto() when $default != null:
return $default(_that.token,_that.globalFeedUrl,_that.teamFeedUrlTemplate);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CalendarTokenDto implements CalendarTokenDto {
  const _CalendarTokenDto({required this.token, required this.globalFeedUrl, required this.teamFeedUrlTemplate});
  factory _CalendarTokenDto.fromJson(Map<String, dynamic> json) => _$CalendarTokenDtoFromJson(json);

/// ICS feed token (include in URL)
@override final  String token;
/// Global ICS feed URL
@override final  String globalFeedUrl;
/// Team-specific feed URL template
@override final  String teamFeedUrlTemplate;

/// Create a copy of CalendarTokenDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CalendarTokenDtoCopyWith<_CalendarTokenDto> get copyWith => __$CalendarTokenDtoCopyWithImpl<_CalendarTokenDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CalendarTokenDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _CalendarTokenDto&&(identical(other.token, token) || other.token == token)&&(identical(other.globalFeedUrl, globalFeedUrl) || other.globalFeedUrl == globalFeedUrl)&&(identical(other.teamFeedUrlTemplate, teamFeedUrlTemplate) || other.teamFeedUrlTemplate == teamFeedUrlTemplate));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,token,globalFeedUrl,teamFeedUrlTemplate);
}

@override
String toString() {
    return 'CalendarTokenDto(token: $token, globalFeedUrl: $globalFeedUrl, teamFeedUrlTemplate: $teamFeedUrlTemplate)';
}


}

/// @nodoc
abstract mixin class _$CalendarTokenDtoCopyWith<$Res> implements $CalendarTokenDtoCopyWith<$Res> {
  factory _$CalendarTokenDtoCopyWith(_CalendarTokenDto value, $Res Function(_CalendarTokenDto) _then) = __$CalendarTokenDtoCopyWithImpl;
@override @useResult
$Res call({
 String token, String globalFeedUrl, String teamFeedUrlTemplate
});




}
/// @nodoc
class __$CalendarTokenDtoCopyWithImpl<$Res>
    implements _$CalendarTokenDtoCopyWith<$Res> {
  __$CalendarTokenDtoCopyWithImpl(this._self, this._then);

  final _CalendarTokenDto _self;
  final $Res Function(_CalendarTokenDto) _then;

/// Create a copy of CalendarTokenDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? token = null,Object? globalFeedUrl = null,Object? teamFeedUrlTemplate = null,}) {
  return _then(_CalendarTokenDto(
token: null == token ? _self.token : token // ignore: cast_nullable_to_non_nullable
as String,globalFeedUrl: null == globalFeedUrl ? _self.globalFeedUrl : globalFeedUrl // ignore: cast_nullable_to_non_nullable
as String,teamFeedUrlTemplate: null == teamFeedUrlTemplate ? _self.teamFeedUrlTemplate : teamFeedUrlTemplate // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
