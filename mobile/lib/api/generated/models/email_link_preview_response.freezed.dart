// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'email_link_preview_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$EmailLinkPreviewResponse {

/// The address the link verifies
 String get email;/// What following the link does
 String get kind;
/// Create a copy of EmailLinkPreviewResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$EmailLinkPreviewResponseCopyWith<EmailLinkPreviewResponse> get copyWith => _$EmailLinkPreviewResponseCopyWithImpl<EmailLinkPreviewResponse>(this as EmailLinkPreviewResponse, _$identity);

  /// Serializes this EmailLinkPreviewResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as EmailLinkPreviewResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is EmailLinkPreviewResponse&&(identical(other.email, _this.email) || other.email == _this.email)&&(identical(other.kind, _this.kind) || other.kind == _this.kind));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as EmailLinkPreviewResponse;
  return Object.hash(runtimeType,_this.email,_this.kind);
}

@override
String toString() {
  final _this = this as EmailLinkPreviewResponse;
  return 'EmailLinkPreviewResponse(email: ${_this.email}, kind: ${_this.kind})';
}


}

/// @nodoc
abstract mixin class $EmailLinkPreviewResponseCopyWith<$Res>  {
  factory $EmailLinkPreviewResponseCopyWith(EmailLinkPreviewResponse value, $Res Function(EmailLinkPreviewResponse) _then) = _$EmailLinkPreviewResponseCopyWithImpl;
@useResult
$Res call({
 String email, String kind
});




}
/// @nodoc
class _$EmailLinkPreviewResponseCopyWithImpl<$Res>
    implements $EmailLinkPreviewResponseCopyWith<$Res> {
  _$EmailLinkPreviewResponseCopyWithImpl(this._self, this._then);

  final EmailLinkPreviewResponse _self;
  final $Res Function(EmailLinkPreviewResponse) _then;

/// Create a copy of EmailLinkPreviewResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? email = null,Object? kind = null,}) {
  return _then(EmailLinkPreviewResponse(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,kind: null == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [EmailLinkPreviewResponse].
extension EmailLinkPreviewResponsePatterns on EmailLinkPreviewResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _EmailLinkPreviewResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _EmailLinkPreviewResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _EmailLinkPreviewResponse value)  $default,){
final _that = this;
switch (_that) {
case _EmailLinkPreviewResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _EmailLinkPreviewResponse value)?  $default,){
final _that = this;
switch (_that) {
case _EmailLinkPreviewResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String email,  String kind)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _EmailLinkPreviewResponse() when $default != null:
return $default(_that.email,_that.kind);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String email,  String kind)  $default,) {final _that = this;
switch (_that) {
case _EmailLinkPreviewResponse():
return $default(_that.email,_that.kind);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String email,  String kind)?  $default,) {final _that = this;
switch (_that) {
case _EmailLinkPreviewResponse() when $default != null:
return $default(_that.email,_that.kind);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _EmailLinkPreviewResponse implements EmailLinkPreviewResponse {
  const _EmailLinkPreviewResponse({required this.email, required this.kind});
  factory _EmailLinkPreviewResponse.fromJson(Map<String, dynamic> json) => _$EmailLinkPreviewResponseFromJson(json);

/// The address the link verifies
@override final  String email;
/// What following the link does
@override final  String kind;

/// Create a copy of EmailLinkPreviewResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$EmailLinkPreviewResponseCopyWith<_EmailLinkPreviewResponse> get copyWith => __$EmailLinkPreviewResponseCopyWithImpl<_EmailLinkPreviewResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$EmailLinkPreviewResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _EmailLinkPreviewResponse&&(identical(other.email, email) || other.email == email)&&(identical(other.kind, kind) || other.kind == kind));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,email,kind);
}

@override
String toString() {
    return 'EmailLinkPreviewResponse(email: $email, kind: $kind)';
}


}

/// @nodoc
abstract mixin class _$EmailLinkPreviewResponseCopyWith<$Res> implements $EmailLinkPreviewResponseCopyWith<$Res> {
  factory _$EmailLinkPreviewResponseCopyWith(_EmailLinkPreviewResponse value, $Res Function(_EmailLinkPreviewResponse) _then) = __$EmailLinkPreviewResponseCopyWithImpl;
@override @useResult
$Res call({
 String email, String kind
});




}
/// @nodoc
class __$EmailLinkPreviewResponseCopyWithImpl<$Res>
    implements _$EmailLinkPreviewResponseCopyWith<$Res> {
  __$EmailLinkPreviewResponseCopyWithImpl(this._self, this._then);

  final _EmailLinkPreviewResponse _self;
  final $Res Function(_EmailLinkPreviewResponse) _then;

/// Create a copy of EmailLinkPreviewResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? email = null,Object? kind = null,}) {
  return _then(_EmailLinkPreviewResponse(
email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,kind: null == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
