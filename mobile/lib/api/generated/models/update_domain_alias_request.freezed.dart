// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'update_domain_alias_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$UpdateDomainAliasRequest {

/// Slug of the team to pin (must belong to the domain)
 String get teamSlug;/// Alias display name
 String get name;/// Base URL for the alias
 String get baseUrl;/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
 String? get androidFingerprints;
/// Create a copy of UpdateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$UpdateDomainAliasRequestCopyWith<UpdateDomainAliasRequest> get copyWith => _$UpdateDomainAliasRequestCopyWithImpl<UpdateDomainAliasRequest>(this as UpdateDomainAliasRequest, _$identity);

  /// Serializes this UpdateDomainAliasRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is UpdateDomainAliasRequest&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,teamSlug,name,baseUrl,androidFingerprints);

@override
String toString() {
  return 'UpdateDomainAliasRequest(teamSlug: $teamSlug, name: $name, baseUrl: $baseUrl, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class $UpdateDomainAliasRequestCopyWith<$Res>  {
  factory $UpdateDomainAliasRequestCopyWith(UpdateDomainAliasRequest value, $Res Function(UpdateDomainAliasRequest) _then) = _$UpdateDomainAliasRequestCopyWithImpl;
@useResult
$Res call({
 String teamSlug, String name, String baseUrl, String? androidFingerprints
});




}
/// @nodoc
class _$UpdateDomainAliasRequestCopyWithImpl<$Res>
    implements $UpdateDomainAliasRequestCopyWith<$Res> {
  _$UpdateDomainAliasRequestCopyWithImpl(this._self, this._then);

  final UpdateDomainAliasRequest _self;
  final $Res Function(UpdateDomainAliasRequest) _then;

/// Create a copy of UpdateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? teamSlug = null,Object? name = null,Object? baseUrl = null,Object? androidFingerprints = freezed,}) {
  return _then(_self.copyWith(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [UpdateDomainAliasRequest].
extension UpdateDomainAliasRequestPatterns on UpdateDomainAliasRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _UpdateDomainAliasRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _UpdateDomainAliasRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _UpdateDomainAliasRequest value)  $default,){
final _that = this;
switch (_that) {
case _UpdateDomainAliasRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _UpdateDomainAliasRequest value)?  $default,){
final _that = this;
switch (_that) {
case _UpdateDomainAliasRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String teamSlug,  String name,  String baseUrl,  String? androidFingerprints)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _UpdateDomainAliasRequest() when $default != null:
return $default(_that.teamSlug,_that.name,_that.baseUrl,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String teamSlug,  String name,  String baseUrl,  String? androidFingerprints)  $default,) {final _that = this;
switch (_that) {
case _UpdateDomainAliasRequest():
return $default(_that.teamSlug,_that.name,_that.baseUrl,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String teamSlug,  String name,  String baseUrl,  String? androidFingerprints)?  $default,) {final _that = this;
switch (_that) {
case _UpdateDomainAliasRequest() when $default != null:
return $default(_that.teamSlug,_that.name,_that.baseUrl,_that.androidFingerprints);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _UpdateDomainAliasRequest implements UpdateDomainAliasRequest {
  const _UpdateDomainAliasRequest({required this.teamSlug, required this.name, required this.baseUrl, this.androidFingerprints});
  factory _UpdateDomainAliasRequest.fromJson(Map<String, dynamic> json) => _$UpdateDomainAliasRequestFromJson(json);

/// Slug of the team to pin (must belong to the domain)
@override final  String teamSlug;
/// Alias display name
@override final  String name;
/// Base URL for the alias
@override final  String baseUrl;
/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
@override final  String? androidFingerprints;

/// Create a copy of UpdateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$UpdateDomainAliasRequestCopyWith<_UpdateDomainAliasRequest> get copyWith => __$UpdateDomainAliasRequestCopyWithImpl<_UpdateDomainAliasRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$UpdateDomainAliasRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _UpdateDomainAliasRequest&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,teamSlug,name,baseUrl,androidFingerprints);

@override
String toString() {
  return 'UpdateDomainAliasRequest(teamSlug: $teamSlug, name: $name, baseUrl: $baseUrl, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class _$UpdateDomainAliasRequestCopyWith<$Res> implements $UpdateDomainAliasRequestCopyWith<$Res> {
  factory _$UpdateDomainAliasRequestCopyWith(_UpdateDomainAliasRequest value, $Res Function(_UpdateDomainAliasRequest) _then) = __$UpdateDomainAliasRequestCopyWithImpl;
@override @useResult
$Res call({
 String teamSlug, String name, String baseUrl, String? androidFingerprints
});




}
/// @nodoc
class __$UpdateDomainAliasRequestCopyWithImpl<$Res>
    implements _$UpdateDomainAliasRequestCopyWith<$Res> {
  __$UpdateDomainAliasRequestCopyWithImpl(this._self, this._then);

  final _UpdateDomainAliasRequest _self;
  final $Res Function(_UpdateDomainAliasRequest) _then;

/// Create a copy of UpdateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? teamSlug = null,Object? name = null,Object? baseUrl = null,Object? androidFingerprints = freezed,}) {
  return _then(_UpdateDomainAliasRequest(
teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
