// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'user_preferences_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$UserPreferencesRequest {

/// Preferred unit system. Omit or send null to leave it unchanged.
 String? get unitSystem;/// Preferred colour scheme. Omit or send null to leave it unchanged.
 String? get theme;/// Preferred language as a BCP-47 tag ('fr', 'en', 'fr-CA'). Omit or send null to leave it unchanged. Not validated against the set of translations the app ships: a client asking for a language nobody has translated yet falls back on its own, which is better than a 400 the day a translation lands.
 String? get language;/// Preferred IANA timezone (e.g. 'Europe/Paris'). Omit or send null to leave it unchanged. Validated against the JDK's own timezone database, not a regex.
 String? get timezone;/// Whether team members may reach you through the classified-ad relay. Omit or send null to leave it unchanged. Setting it to false stops the relay from delivering to you; your ads stay visible, they simply stop being answerable.
 bool? get contactableByMembers;
/// Create a copy of UserPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$UserPreferencesRequestCopyWith<UserPreferencesRequest> get copyWith => _$UserPreferencesRequestCopyWithImpl<UserPreferencesRequest>(this as UserPreferencesRequest, _$identity);

  /// Serializes this UserPreferencesRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is UserPreferencesRequest&&(identical(other.unitSystem, unitSystem) || other.unitSystem == unitSystem)&&(identical(other.theme, theme) || other.theme == theme)&&(identical(other.language, language) || other.language == language)&&(identical(other.timezone, timezone) || other.timezone == timezone)&&(identical(other.contactableByMembers, contactableByMembers) || other.contactableByMembers == contactableByMembers));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,unitSystem,theme,language,timezone,contactableByMembers);

@override
String toString() {
  return 'UserPreferencesRequest(unitSystem: $unitSystem, theme: $theme, language: $language, timezone: $timezone, contactableByMembers: $contactableByMembers)';
}


}

/// @nodoc
abstract mixin class $UserPreferencesRequestCopyWith<$Res>  {
  factory $UserPreferencesRequestCopyWith(UserPreferencesRequest value, $Res Function(UserPreferencesRequest) _then) = _$UserPreferencesRequestCopyWithImpl;
@useResult
$Res call({
 String? unitSystem, String? theme, String? language, String? timezone, bool? contactableByMembers
});




}
/// @nodoc
class _$UserPreferencesRequestCopyWithImpl<$Res>
    implements $UserPreferencesRequestCopyWith<$Res> {
  _$UserPreferencesRequestCopyWithImpl(this._self, this._then);

  final UserPreferencesRequest _self;
  final $Res Function(UserPreferencesRequest) _then;

/// Create a copy of UserPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? unitSystem = freezed,Object? theme = freezed,Object? language = freezed,Object? timezone = freezed,Object? contactableByMembers = freezed,}) {
  return _then(UserPreferencesRequest(
unitSystem: freezed == unitSystem ? _self.unitSystem : unitSystem // ignore: cast_nullable_to_non_nullable
as String?,theme: freezed == theme ? _self.theme : theme // ignore: cast_nullable_to_non_nullable
as String?,language: freezed == language ? _self.language : language // ignore: cast_nullable_to_non_nullable
as String?,timezone: freezed == timezone ? _self.timezone : timezone // ignore: cast_nullable_to_non_nullable
as String?,contactableByMembers: freezed == contactableByMembers ? _self.contactableByMembers : contactableByMembers // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}

}


/// Adds pattern-matching-related methods to [UserPreferencesRequest].
extension UserPreferencesRequestPatterns on UserPreferencesRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _UserPreferencesRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _UserPreferencesRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _UserPreferencesRequest value)  $default,){
final _that = this;
switch (_that) {
case _UserPreferencesRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _UserPreferencesRequest value)?  $default,){
final _that = this;
switch (_that) {
case _UserPreferencesRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String? unitSystem,  String? theme,  String? language,  String? timezone,  bool? contactableByMembers)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _UserPreferencesRequest() when $default != null:
return $default(_that.unitSystem,_that.theme,_that.language,_that.timezone,_that.contactableByMembers);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String? unitSystem,  String? theme,  String? language,  String? timezone,  bool? contactableByMembers)  $default,) {final _that = this;
switch (_that) {
case _UserPreferencesRequest():
return $default(_that.unitSystem,_that.theme,_that.language,_that.timezone,_that.contactableByMembers);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String? unitSystem,  String? theme,  String? language,  String? timezone,  bool? contactableByMembers)?  $default,) {final _that = this;
switch (_that) {
case _UserPreferencesRequest() when $default != null:
return $default(_that.unitSystem,_that.theme,_that.language,_that.timezone,_that.contactableByMembers);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _UserPreferencesRequest implements UserPreferencesRequest {
  const _UserPreferencesRequest({this.unitSystem, this.theme, this.language, this.timezone, this.contactableByMembers});
  factory _UserPreferencesRequest.fromJson(Map<String, dynamic> json) => _$UserPreferencesRequestFromJson(json);

/// Preferred unit system. Omit or send null to leave it unchanged.
@override final  String? unitSystem;
/// Preferred colour scheme. Omit or send null to leave it unchanged.
@override final  String? theme;
/// Preferred language as a BCP-47 tag ('fr', 'en', 'fr-CA'). Omit or send null to leave it unchanged. Not validated against the set of translations the app ships: a client asking for a language nobody has translated yet falls back on its own, which is better than a 400 the day a translation lands.
@override final  String? language;
/// Preferred IANA timezone (e.g. 'Europe/Paris'). Omit or send null to leave it unchanged. Validated against the JDK's own timezone database, not a regex.
@override final  String? timezone;
/// Whether team members may reach you through the classified-ad relay. Omit or send null to leave it unchanged. Setting it to false stops the relay from delivering to you; your ads stay visible, they simply stop being answerable.
@override final  bool? contactableByMembers;

/// Create a copy of UserPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$UserPreferencesRequestCopyWith<_UserPreferencesRequest> get copyWith => __$UserPreferencesRequestCopyWithImpl<_UserPreferencesRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$UserPreferencesRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _UserPreferencesRequest&&(identical(other.unitSystem, unitSystem) || other.unitSystem == unitSystem)&&(identical(other.theme, theme) || other.theme == theme)&&(identical(other.language, language) || other.language == language)&&(identical(other.timezone, timezone) || other.timezone == timezone)&&(identical(other.contactableByMembers, contactableByMembers) || other.contactableByMembers == contactableByMembers));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,unitSystem,theme,language,timezone,contactableByMembers);

@override
String toString() {
  return 'UserPreferencesRequest(unitSystem: $unitSystem, theme: $theme, language: $language, timezone: $timezone, contactableByMembers: $contactableByMembers)';
}


}

/// @nodoc
abstract mixin class _$UserPreferencesRequestCopyWith<$Res> implements $UserPreferencesRequestCopyWith<$Res> {
  factory _$UserPreferencesRequestCopyWith(_UserPreferencesRequest value, $Res Function(_UserPreferencesRequest) _then) = __$UserPreferencesRequestCopyWithImpl;
@override @useResult
$Res call({
 String? unitSystem, String? theme, String? language, String? timezone, bool? contactableByMembers
});




}
/// @nodoc
class __$UserPreferencesRequestCopyWithImpl<$Res>
    implements _$UserPreferencesRequestCopyWith<$Res> {
  __$UserPreferencesRequestCopyWithImpl(this._self, this._then);

  final _UserPreferencesRequest _self;
  final $Res Function(_UserPreferencesRequest) _then;

/// Create a copy of UserPreferencesRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? unitSystem = freezed,Object? theme = freezed,Object? language = freezed,Object? timezone = freezed,Object? contactableByMembers = freezed,}) {
  return _then(_UserPreferencesRequest(
unitSystem: freezed == unitSystem ? _self.unitSystem : unitSystem // ignore: cast_nullable_to_non_nullable
as String?,theme: freezed == theme ? _self.theme : theme // ignore: cast_nullable_to_non_nullable
as String?,language: freezed == language ? _self.language : language // ignore: cast_nullable_to_non_nullable
as String?,timezone: freezed == timezone ? _self.timezone : timezone // ignore: cast_nullable_to_non_nullable
as String?,contactableByMembers: freezed == contactableByMembers ? _self.contactableByMembers : contactableByMembers // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}


}

// dart format on
