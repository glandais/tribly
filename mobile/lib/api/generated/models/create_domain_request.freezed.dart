// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'create_domain_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CreateDomainRequest {

/// Domain hostname
 String get domain;/// Domain display name
 String get name;/// Base URL for the domain
 String get baseUrl;/// Whether domain is single-team mode
 bool? get singleTeam;/// Whether the interactive planner is open in the GPX tools
 bool? get enableGpxPlanner;/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
 String? get androidFingerprints;
/// Create a copy of CreateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CreateDomainRequestCopyWith<CreateDomainRequest> get copyWith => _$CreateDomainRequestCopyWithImpl<CreateDomainRequest>(this as CreateDomainRequest, _$identity);

  /// Serializes this CreateDomainRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as CreateDomainRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CreateDomainRequest&&(identical(other.domain, _this.domain) || other.domain == _this.domain)&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.baseUrl, _this.baseUrl) || other.baseUrl == _this.baseUrl)&&(identical(other.singleTeam, _this.singleTeam) || other.singleTeam == _this.singleTeam)&&(identical(other.enableGpxPlanner, _this.enableGpxPlanner) || other.enableGpxPlanner == _this.enableGpxPlanner)&&(identical(other.androidFingerprints, _this.androidFingerprints) || other.androidFingerprints == _this.androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as CreateDomainRequest;
  return Object.hash(runtimeType,_this.domain,_this.name,_this.baseUrl,_this.singleTeam,_this.enableGpxPlanner,_this.androidFingerprints);
}

@override
String toString() {
  final _this = this as CreateDomainRequest;
  return 'CreateDomainRequest(domain: ${_this.domain}, name: ${_this.name}, baseUrl: ${_this.baseUrl}, singleTeam: ${_this.singleTeam}, enableGpxPlanner: ${_this.enableGpxPlanner}, androidFingerprints: ${_this.androidFingerprints})';
}


}

/// @nodoc
abstract mixin class $CreateDomainRequestCopyWith<$Res>  {
  factory $CreateDomainRequestCopyWith(CreateDomainRequest value, $Res Function(CreateDomainRequest) _then) = _$CreateDomainRequestCopyWithImpl;
@useResult
$Res call({
 String domain, String name, String baseUrl, bool? singleTeam, bool? enableGpxPlanner, String? androidFingerprints
});




}
/// @nodoc
class _$CreateDomainRequestCopyWithImpl<$Res>
    implements $CreateDomainRequestCopyWith<$Res> {
  _$CreateDomainRequestCopyWithImpl(this._self, this._then);

  final CreateDomainRequest _self;
  final $Res Function(CreateDomainRequest) _then;

/// Create a copy of CreateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? domain = null,Object? name = null,Object? baseUrl = null,Object? singleTeam = freezed,Object? enableGpxPlanner = freezed,Object? androidFingerprints = freezed,}) {
  return _then(CreateDomainRequest(
domain: null == domain ? _self.domain : domain // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,singleTeam: freezed == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool?,enableGpxPlanner: freezed == enableGpxPlanner ? _self.enableGpxPlanner : enableGpxPlanner // ignore: cast_nullable_to_non_nullable
as bool?,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [CreateDomainRequest].
extension CreateDomainRequestPatterns on CreateDomainRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CreateDomainRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CreateDomainRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CreateDomainRequest value)  $default,){
final _that = this;
switch (_that) {
case _CreateDomainRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CreateDomainRequest value)?  $default,){
final _that = this;
switch (_that) {
case _CreateDomainRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String domain,  String name,  String baseUrl,  bool? singleTeam,  bool? enableGpxPlanner,  String? androidFingerprints)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CreateDomainRequest() when $default != null:
return $default(_that.domain,_that.name,_that.baseUrl,_that.singleTeam,_that.enableGpxPlanner,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String domain,  String name,  String baseUrl,  bool? singleTeam,  bool? enableGpxPlanner,  String? androidFingerprints)  $default,) {final _that = this;
switch (_that) {
case _CreateDomainRequest():
return $default(_that.domain,_that.name,_that.baseUrl,_that.singleTeam,_that.enableGpxPlanner,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String domain,  String name,  String baseUrl,  bool? singleTeam,  bool? enableGpxPlanner,  String? androidFingerprints)?  $default,) {final _that = this;
switch (_that) {
case _CreateDomainRequest() when $default != null:
return $default(_that.domain,_that.name,_that.baseUrl,_that.singleTeam,_that.enableGpxPlanner,_that.androidFingerprints);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CreateDomainRequest implements CreateDomainRequest {
  const _CreateDomainRequest({required this.domain, required this.name, required this.baseUrl, this.singleTeam, this.enableGpxPlanner, this.androidFingerprints});
  factory _CreateDomainRequest.fromJson(Map<String, dynamic> json) => _$CreateDomainRequestFromJson(json);

/// Domain hostname
@override final  String domain;
/// Domain display name
@override final  String name;
/// Base URL for the domain
@override final  String baseUrl;
/// Whether domain is single-team mode
@override final  bool? singleTeam;
/// Whether the interactive planner is open in the GPX tools
@override final  bool? enableGpxPlanner;
/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
@override final  String? androidFingerprints;

/// Create a copy of CreateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CreateDomainRequestCopyWith<_CreateDomainRequest> get copyWith => __$CreateDomainRequestCopyWithImpl<_CreateDomainRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CreateDomainRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _CreateDomainRequest&&(identical(other.domain, domain) || other.domain == domain)&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.singleTeam, singleTeam) || other.singleTeam == singleTeam)&&(identical(other.enableGpxPlanner, enableGpxPlanner) || other.enableGpxPlanner == enableGpxPlanner)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,domain,name,baseUrl,singleTeam,enableGpxPlanner,androidFingerprints);
}

@override
String toString() {
    return 'CreateDomainRequest(domain: $domain, name: $name, baseUrl: $baseUrl, singleTeam: $singleTeam, enableGpxPlanner: $enableGpxPlanner, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class _$CreateDomainRequestCopyWith<$Res> implements $CreateDomainRequestCopyWith<$Res> {
  factory _$CreateDomainRequestCopyWith(_CreateDomainRequest value, $Res Function(_CreateDomainRequest) _then) = __$CreateDomainRequestCopyWithImpl;
@override @useResult
$Res call({
 String domain, String name, String baseUrl, bool? singleTeam, bool? enableGpxPlanner, String? androidFingerprints
});




}
/// @nodoc
class __$CreateDomainRequestCopyWithImpl<$Res>
    implements _$CreateDomainRequestCopyWith<$Res> {
  __$CreateDomainRequestCopyWithImpl(this._self, this._then);

  final _CreateDomainRequest _self;
  final $Res Function(_CreateDomainRequest) _then;

/// Create a copy of CreateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? domain = null,Object? name = null,Object? baseUrl = null,Object? singleTeam = freezed,Object? enableGpxPlanner = freezed,Object? androidFingerprints = freezed,}) {
  return _then(_CreateDomainRequest(
domain: null == domain ? _self.domain : domain // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,singleTeam: freezed == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool?,enableGpxPlanner: freezed == enableGpxPlanner ? _self.enableGpxPlanner : enableGpxPlanner // ignore: cast_nullable_to_non_nullable
as bool?,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
