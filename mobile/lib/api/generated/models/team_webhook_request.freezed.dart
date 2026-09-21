// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_webhook_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamWebhookRequest {

/// Language the messages are written in
 String get language;/// Whether announcements are posted
 bool get enabled;/// The https URL to post to. Omit it to keep the one already set — the API never returns it in full. Required when the team has no webhook yet.
 String? get url;
/// Create a copy of TeamWebhookRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamWebhookRequestCopyWith<TeamWebhookRequest> get copyWith => _$TeamWebhookRequestCopyWithImpl<TeamWebhookRequest>(this as TeamWebhookRequest, _$identity);

  /// Serializes this TeamWebhookRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TeamWebhookRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamWebhookRequest&&(identical(other.language, _this.language) || other.language == _this.language)&&(identical(other.enabled, _this.enabled) || other.enabled == _this.enabled)&&(identical(other.url, _this.url) || other.url == _this.url));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TeamWebhookRequest;
  return Object.hash(runtimeType,_this.language,_this.enabled,_this.url);
}

@override
String toString() {
  final _this = this as TeamWebhookRequest;
  return 'TeamWebhookRequest(language: ${_this.language}, enabled: ${_this.enabled}, url: ${_this.url})';
}


}

/// @nodoc
abstract mixin class $TeamWebhookRequestCopyWith<$Res>  {
  factory $TeamWebhookRequestCopyWith(TeamWebhookRequest value, $Res Function(TeamWebhookRequest) _then) = _$TeamWebhookRequestCopyWithImpl;
@useResult
$Res call({
 String language, bool enabled, String? url
});




}
/// @nodoc
class _$TeamWebhookRequestCopyWithImpl<$Res>
    implements $TeamWebhookRequestCopyWith<$Res> {
  _$TeamWebhookRequestCopyWithImpl(this._self, this._then);

  final TeamWebhookRequest _self;
  final $Res Function(TeamWebhookRequest) _then;

/// Create a copy of TeamWebhookRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? language = null,Object? enabled = null,Object? url = freezed,}) {
  return _then(TeamWebhookRequest(
language: null == language ? _self.language : language // ignore: cast_nullable_to_non_nullable
as String,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,url: freezed == url ? _self.url : url // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [TeamWebhookRequest].
extension TeamWebhookRequestPatterns on TeamWebhookRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamWebhookRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamWebhookRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamWebhookRequest value)  $default,){
final _that = this;
switch (_that) {
case _TeamWebhookRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamWebhookRequest value)?  $default,){
final _that = this;
switch (_that) {
case _TeamWebhookRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String language,  bool enabled,  String? url)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamWebhookRequest() when $default != null:
return $default(_that.language,_that.enabled,_that.url);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String language,  bool enabled,  String? url)  $default,) {final _that = this;
switch (_that) {
case _TeamWebhookRequest():
return $default(_that.language,_that.enabled,_that.url);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String language,  bool enabled,  String? url)?  $default,) {final _that = this;
switch (_that) {
case _TeamWebhookRequest() when $default != null:
return $default(_that.language,_that.enabled,_that.url);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamWebhookRequest implements TeamWebhookRequest {
  const _TeamWebhookRequest({required this.language, required this.enabled, this.url});
  factory _TeamWebhookRequest.fromJson(Map<String, dynamic> json) => _$TeamWebhookRequestFromJson(json);

/// Language the messages are written in
@override final  String language;
/// Whether announcements are posted
@override final  bool enabled;
/// The https URL to post to. Omit it to keep the one already set — the API never returns it in full. Required when the team has no webhook yet.
@override final  String? url;

/// Create a copy of TeamWebhookRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamWebhookRequestCopyWith<_TeamWebhookRequest> get copyWith => __$TeamWebhookRequestCopyWithImpl<_TeamWebhookRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamWebhookRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamWebhookRequest&&(identical(other.language, language) || other.language == language)&&(identical(other.enabled, enabled) || other.enabled == enabled)&&(identical(other.url, url) || other.url == url));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,language,enabled,url);
}

@override
String toString() {
    return 'TeamWebhookRequest(language: $language, enabled: $enabled, url: $url)';
}


}

/// @nodoc
abstract mixin class _$TeamWebhookRequestCopyWith<$Res> implements $TeamWebhookRequestCopyWith<$Res> {
  factory _$TeamWebhookRequestCopyWith(_TeamWebhookRequest value, $Res Function(_TeamWebhookRequest) _then) = __$TeamWebhookRequestCopyWithImpl;
@override @useResult
$Res call({
 String language, bool enabled, String? url
});




}
/// @nodoc
class __$TeamWebhookRequestCopyWithImpl<$Res>
    implements _$TeamWebhookRequestCopyWith<$Res> {
  __$TeamWebhookRequestCopyWithImpl(this._self, this._then);

  final _TeamWebhookRequest _self;
  final $Res Function(_TeamWebhookRequest) _then;

/// Create a copy of TeamWebhookRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? language = null,Object? enabled = null,Object? url = freezed,}) {
  return _then(_TeamWebhookRequest(
language: null == language ? _self.language : language // ignore: cast_nullable_to_non_nullable
as String,enabled: null == enabled ? _self.enabled : enabled // ignore: cast_nullable_to_non_nullable
as bool,url: freezed == url ? _self.url : url // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
