// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'slug_change_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$SlugChangeRequest {

/// New slug (lowercase letters, numbers, and hyphens only)
 String get slug;
/// Create a copy of SlugChangeRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$SlugChangeRequestCopyWith<SlugChangeRequest> get copyWith => _$SlugChangeRequestCopyWithImpl<SlugChangeRequest>(this as SlugChangeRequest, _$identity);

  /// Serializes this SlugChangeRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is SlugChangeRequest&&(identical(other.slug, slug) || other.slug == slug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,slug);

@override
String toString() {
  return 'SlugChangeRequest(slug: $slug)';
}


}

/// @nodoc
abstract mixin class $SlugChangeRequestCopyWith<$Res>  {
  factory $SlugChangeRequestCopyWith(SlugChangeRequest value, $Res Function(SlugChangeRequest) _then) = _$SlugChangeRequestCopyWithImpl;
@useResult
$Res call({
 String slug
});




}
/// @nodoc
class _$SlugChangeRequestCopyWithImpl<$Res>
    implements $SlugChangeRequestCopyWith<$Res> {
  _$SlugChangeRequestCopyWithImpl(this._self, this._then);

  final SlugChangeRequest _self;
  final $Res Function(SlugChangeRequest) _then;

/// Create a copy of SlugChangeRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? slug = null,}) {
  return _then(SlugChangeRequest(
slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [SlugChangeRequest].
extension SlugChangeRequestPatterns on SlugChangeRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _SlugChangeRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _SlugChangeRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _SlugChangeRequest value)  $default,){
final _that = this;
switch (_that) {
case _SlugChangeRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _SlugChangeRequest value)?  $default,){
final _that = this;
switch (_that) {
case _SlugChangeRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String slug)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _SlugChangeRequest() when $default != null:
return $default(_that.slug);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String slug)  $default,) {final _that = this;
switch (_that) {
case _SlugChangeRequest():
return $default(_that.slug);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String slug)?  $default,) {final _that = this;
switch (_that) {
case _SlugChangeRequest() when $default != null:
return $default(_that.slug);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _SlugChangeRequest implements SlugChangeRequest {
  const _SlugChangeRequest({required this.slug});
  factory _SlugChangeRequest.fromJson(Map<String, dynamic> json) => _$SlugChangeRequestFromJson(json);

/// New slug (lowercase letters, numbers, and hyphens only)
@override final  String slug;

/// Create a copy of SlugChangeRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$SlugChangeRequestCopyWith<_SlugChangeRequest> get copyWith => __$SlugChangeRequestCopyWithImpl<_SlugChangeRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$SlugChangeRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _SlugChangeRequest&&(identical(other.slug, slug) || other.slug == slug));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,slug);

@override
String toString() {
  return 'SlugChangeRequest(slug: $slug)';
}


}

/// @nodoc
abstract mixin class _$SlugChangeRequestCopyWith<$Res> implements $SlugChangeRequestCopyWith<$Res> {
  factory _$SlugChangeRequestCopyWith(_SlugChangeRequest value, $Res Function(_SlugChangeRequest) _then) = __$SlugChangeRequestCopyWithImpl;
@override @useResult
$Res call({
 String slug
});




}
/// @nodoc
class __$SlugChangeRequestCopyWithImpl<$Res>
    implements _$SlugChangeRequestCopyWith<$Res> {
  __$SlugChangeRequestCopyWithImpl(this._self, this._then);

  final _SlugChangeRequest _self;
  final $Res Function(_SlugChangeRequest) _then;

/// Create a copy of SlugChangeRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? slug = null,}) {
  return _then(_SlugChangeRequest(
slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
