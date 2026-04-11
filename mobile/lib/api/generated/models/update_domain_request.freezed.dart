// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'update_domain_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$UpdateDomainRequest {

/// Domain display name
 String get name;/// Base URL for the domain
 String get baseUrl;/// Whether domain is single-team mode
 bool? get singleTeam;/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
 String? get androidFingerprints;
/// Create a copy of UpdateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$UpdateDomainRequestCopyWith<UpdateDomainRequest> get copyWith => _$UpdateDomainRequestCopyWithImpl<UpdateDomainRequest>(this as UpdateDomainRequest, _$identity);

  /// Serializes this UpdateDomainRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is UpdateDomainRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.singleTeam, singleTeam) || other.singleTeam == singleTeam)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,baseUrl,singleTeam,androidFingerprints);

@override
String toString() {
  return 'UpdateDomainRequest(name: $name, baseUrl: $baseUrl, singleTeam: $singleTeam, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class $UpdateDomainRequestCopyWith<$Res>  {
  factory $UpdateDomainRequestCopyWith(UpdateDomainRequest value, $Res Function(UpdateDomainRequest) _then) = _$UpdateDomainRequestCopyWithImpl;
@useResult
$Res call({
 String name, String baseUrl, bool? singleTeam, String? androidFingerprints
});




}
/// @nodoc
class _$UpdateDomainRequestCopyWithImpl<$Res>
    implements $UpdateDomainRequestCopyWith<$Res> {
  _$UpdateDomainRequestCopyWithImpl(this._self, this._then);

  final UpdateDomainRequest _self;
  final $Res Function(UpdateDomainRequest) _then;

/// Create a copy of UpdateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? baseUrl = null,Object? singleTeam = freezed,Object? androidFingerprints = freezed,}) {
  return _then(_self.copyWith(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,singleTeam: freezed == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool?,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [UpdateDomainRequest].
extension UpdateDomainRequestPatterns on UpdateDomainRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _UpdateDomainRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _UpdateDomainRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _UpdateDomainRequest value)  $default,){
final _that = this;
switch (_that) {
case _UpdateDomainRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _UpdateDomainRequest value)?  $default,){
final _that = this;
switch (_that) {
case _UpdateDomainRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  String baseUrl,  bool? singleTeam,  String? androidFingerprints)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _UpdateDomainRequest() when $default != null:
return $default(_that.name,_that.baseUrl,_that.singleTeam,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  String baseUrl,  bool? singleTeam,  String? androidFingerprints)  $default,) {final _that = this;
switch (_that) {
case _UpdateDomainRequest():
return $default(_that.name,_that.baseUrl,_that.singleTeam,_that.androidFingerprints);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  String baseUrl,  bool? singleTeam,  String? androidFingerprints)?  $default,) {final _that = this;
switch (_that) {
case _UpdateDomainRequest() when $default != null:
return $default(_that.name,_that.baseUrl,_that.singleTeam,_that.androidFingerprints);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _UpdateDomainRequest implements UpdateDomainRequest {
  const _UpdateDomainRequest({required this.name, required this.baseUrl, this.singleTeam, this.androidFingerprints});
  factory _UpdateDomainRequest.fromJson(Map<String, dynamic> json) => _$UpdateDomainRequestFromJson(json);

/// Domain display name
@override final  String name;
/// Base URL for the domain
@override final  String baseUrl;
/// Whether domain is single-team mode
@override final  bool? singleTeam;
/// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
@override final  String? androidFingerprints;

/// Create a copy of UpdateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$UpdateDomainRequestCopyWith<_UpdateDomainRequest> get copyWith => __$UpdateDomainRequestCopyWithImpl<_UpdateDomainRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$UpdateDomainRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _UpdateDomainRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl)&&(identical(other.singleTeam, singleTeam) || other.singleTeam == singleTeam)&&(identical(other.androidFingerprints, androidFingerprints) || other.androidFingerprints == androidFingerprints));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,baseUrl,singleTeam,androidFingerprints);

@override
String toString() {
  return 'UpdateDomainRequest(name: $name, baseUrl: $baseUrl, singleTeam: $singleTeam, androidFingerprints: $androidFingerprints)';
}


}

/// @nodoc
abstract mixin class _$UpdateDomainRequestCopyWith<$Res> implements $UpdateDomainRequestCopyWith<$Res> {
  factory _$UpdateDomainRequestCopyWith(_UpdateDomainRequest value, $Res Function(_UpdateDomainRequest) _then) = __$UpdateDomainRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, String baseUrl, bool? singleTeam, String? androidFingerprints
});




}
/// @nodoc
class __$UpdateDomainRequestCopyWithImpl<$Res>
    implements _$UpdateDomainRequestCopyWith<$Res> {
  __$UpdateDomainRequestCopyWithImpl(this._self, this._then);

  final _UpdateDomainRequest _self;
  final $Res Function(_UpdateDomainRequest) _then;

/// Create a copy of UpdateDomainRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? baseUrl = null,Object? singleTeam = freezed,Object? androidFingerprints = freezed,}) {
  return _then(_UpdateDomainRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,baseUrl: null == baseUrl ? _self.baseUrl : baseUrl // ignore: cast_nullable_to_non_nullable
as String,singleTeam: freezed == singleTeam ? _self.singleTeam : singleTeam // ignore: cast_nullable_to_non_nullable
as bool?,androidFingerprints: freezed == androidFingerprints ? _self.androidFingerprints : androidFingerprints // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
