// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'create_domain_alias_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CreateDomainAliasRequest {

/// Alias hostname
 String get hostname;/// Slug of the team to pin (must belong to the domain)
 String get teamSlug;/// Alias display name
 String get name;/// Base URL for the alias
 String get baseUrl;/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
 String? get androidFingerprints;
/// Create a copy of CreateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CreateDomainAliasRequestCopyWith<CreateDomainAliasRequest> get copyWith => _$CreateDomainAliasRequestCopyWithImpl<CreateDomainAliasRequest>(this as CreateDomainAliasRequest, _$identity);

  /// Serializes this CreateDomainAliasRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CreateDomainAliasRequest&&(identical(other.hostname, hostname) || other.hostname == hostname)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,hostname,teamSlug,name,baseUrl,androidFingerprints);

@override
String toString() {
  return 'CreateDomainAliasRequest(hostname: $hostname, teamSlug: $teamSlug, name: $name, baseUrl: $baseUrl, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class $CreateDomainAliasRequestCopyWith<$Res>  {
  factory $CreateDomainAliasRequestCopyWith(CreateDomainAliasRequest value, $Res Function(CreateDomainAliasRequest) _then) = _$CreateDomainAliasRequestCopyWithImpl;
@useResult
$Res call({
 String hostname, String teamSlug, String name, String baseUrl, String? androidFingerprints
});




}
/// @nodoc
class _$CreateDomainAliasRequestCopyWithImpl<$Res>
    implements $CreateDomainAliasRequestCopyWith<$Res> {
  _$CreateDomainAliasRequestCopyWithImpl(this._self, this._then);

  final CreateDomainAliasRequest _self;
  final $Res Function(CreateDomainAliasRequest) _then;

/// Create a copy of CreateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? hostname = null,Object? teamSlug = null,Object? name = null,Object? baseUrl = null,Object? androidFingerprints = freezed,}) {
  return _then(_self.copyWith(
hostname: null == hostname ? _self.hostname : hostname // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [CreateDomainAliasRequest].
extension CreateDomainAliasRequestPatterns on CreateDomainAliasRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CreateDomainAliasRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CreateDomainAliasRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CreateDomainAliasRequest value)  $default,){
final _that = this;
switch (_that) {
case _CreateDomainAliasRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CreateDomainAliasRequest value)?  $default,){
final _that = this;
switch (_that) {
case _CreateDomainAliasRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String hostname,  String teamSlug,  String name,  String baseUrl,  String? androidFingerprints)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CreateDomainAliasRequest() when $default != null:
return $default(_that.hostname,_that.teamSlug,_that.name,_that.baseUrl,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String hostname,  String teamSlug,  String name,  String baseUrl,  String? androidFingerprints)  $default,) {final _that = this;
switch (_that) {
case _CreateDomainAliasRequest():
return $default(_that.hostname,_that.teamSlug,_that.name,_that.baseUrl,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String hostname,  String teamSlug,  String name,  String baseUrl,  String? androidFingerprints)?  $default,) {final _that = this;
switch (_that) {
case _CreateDomainAliasRequest() when $default != null:
return $default(_that.hostname,_that.teamSlug,_that.name,_that.baseUrl,_that.androidFingerprints);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CreateDomainAliasRequest implements CreateDomainAliasRequest {
  const _CreateDomainAliasRequest({required this.hostname, required this.teamSlug, required this.name, required this.baseUrl, this.androidFingerprints});
  factory _CreateDomainAliasRequest.fromJson(Map<String, dynamic> json) => _$CreateDomainAliasRequestFromJson(json);

/// Alias hostname
@override final  String hostname;
/// Slug of the team to pin (must belong to the domain)
@override final  String teamSlug;
/// Alias display name
@override final  String name;
/// Base URL for the alias
@override final  String baseUrl;
/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
@override final  String? androidFingerprints;

/// Create a copy of CreateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CreateDomainAliasRequestCopyWith<_CreateDomainAliasRequest> get copyWith => __$CreateDomainAliasRequestCopyWithImpl<_CreateDomainAliasRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CreateDomainAliasRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _CreateDomainAliasRequest&&(identical(other.hostname, hostname) || other.hostname == hostname)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,hostname,teamSlug,name,baseUrl,androidFingerprints);

@override
String toString() {
  return 'CreateDomainAliasRequest(hostname: $hostname, teamSlug: $teamSlug, name: $name, baseUrl: $baseUrl, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class _$CreateDomainAliasRequestCopyWith<$Res> implements $CreateDomainAliasRequestCopyWith<$Res> {
  factory _$CreateDomainAliasRequestCopyWith(_CreateDomainAliasRequest value, $Res Function(_CreateDomainAliasRequest) _then) = __$CreateDomainAliasRequestCopyWithImpl;
@override @useResult
$Res call({
 String hostname, String teamSlug, String name, String baseUrl, String? androidFingerprints
});




}
/// @nodoc
class __$CreateDomainAliasRequestCopyWithImpl<$Res>
    implements _$CreateDomainAliasRequestCopyWith<$Res> {
  __$CreateDomainAliasRequestCopyWithImpl(this._self, this._then);

  final _CreateDomainAliasRequest _self;
  final $Res Function(_CreateDomainAliasRequest) _then;

/// Create a copy of CreateDomainAliasRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? hostname = null,Object? teamSlug = null,Object? name = null,Object? baseUrl = null,Object? androidFingerprints = freezed,}) {
  return _then(_CreateDomainAliasRequest(
hostname: null == hostname ? _self.hostname : hostname // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
