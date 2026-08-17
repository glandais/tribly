// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'social_backfill_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$SocialBackfillResponse {

/// Number of identity rows created
 int get identitiesCreated;
/// Create a copy of SocialBackfillResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$SocialBackfillResponseCopyWith<SocialBackfillResponse> get copyWith => _$SocialBackfillResponseCopyWithImpl<SocialBackfillResponse>(this as SocialBackfillResponse, _$identity);

  /// Serializes this SocialBackfillResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is SocialBackfillResponse&&(identical(other.identitiesCreated, identitiesCreated) || other.identitiesCreated == identitiesCreated));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,identitiesCreated);

@override
String toString() {
  return 'SocialBackfillResponse(identitiesCreated: $identitiesCreated)';
}


}

/// @nodoc
abstract mixin class $SocialBackfillResponseCopyWith<$Res>  {
  factory $SocialBackfillResponseCopyWith(SocialBackfillResponse value, $Res Function(SocialBackfillResponse) _then) = _$SocialBackfillResponseCopyWithImpl;
@useResult
$Res call({
 int identitiesCreated
});




}
/// @nodoc
class _$SocialBackfillResponseCopyWithImpl<$Res>
    implements $SocialBackfillResponseCopyWith<$Res> {
  _$SocialBackfillResponseCopyWithImpl(this._self, this._then);

  final SocialBackfillResponse _self;
  final $Res Function(SocialBackfillResponse) _then;

/// Create a copy of SocialBackfillResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? identitiesCreated = null,}) {
  return _then(SocialBackfillResponse(
identitiesCreated: null == identitiesCreated ? _self.identitiesCreated : identitiesCreated // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [SocialBackfillResponse].
extension SocialBackfillResponsePatterns on SocialBackfillResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _SocialBackfillResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _SocialBackfillResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _SocialBackfillResponse value)  $default,){
final _that = this;
switch (_that) {
case _SocialBackfillResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _SocialBackfillResponse value)?  $default,){
final _that = this;
switch (_that) {
case _SocialBackfillResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int identitiesCreated)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _SocialBackfillResponse() when $default != null:
return $default(_that.identitiesCreated);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int identitiesCreated)  $default,) {final _that = this;
switch (_that) {
case _SocialBackfillResponse():
return $default(_that.identitiesCreated);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int identitiesCreated)?  $default,) {final _that = this;
switch (_that) {
case _SocialBackfillResponse() when $default != null:
return $default(_that.identitiesCreated);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _SocialBackfillResponse implements SocialBackfillResponse {
  const _SocialBackfillResponse({required this.identitiesCreated});
  factory _SocialBackfillResponse.fromJson(Map<String, dynamic> json) => _$SocialBackfillResponseFromJson(json);

/// Number of identity rows created
@override final  int identitiesCreated;

/// Create a copy of SocialBackfillResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$SocialBackfillResponseCopyWith<_SocialBackfillResponse> get copyWith => __$SocialBackfillResponseCopyWithImpl<_SocialBackfillResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$SocialBackfillResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _SocialBackfillResponse&&(identical(other.identitiesCreated, identitiesCreated) || other.identitiesCreated == identitiesCreated));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,identitiesCreated);

@override
String toString() {
  return 'SocialBackfillResponse(identitiesCreated: $identitiesCreated)';
}


}

/// @nodoc
abstract mixin class _$SocialBackfillResponseCopyWith<$Res> implements $SocialBackfillResponseCopyWith<$Res> {
  factory _$SocialBackfillResponseCopyWith(_SocialBackfillResponse value, $Res Function(_SocialBackfillResponse) _then) = __$SocialBackfillResponseCopyWithImpl;
@override @useResult
$Res call({
 int identitiesCreated
});




}
/// @nodoc
class __$SocialBackfillResponseCopyWithImpl<$Res>
    implements _$SocialBackfillResponseCopyWith<$Res> {
  __$SocialBackfillResponseCopyWithImpl(this._self, this._then);

  final _SocialBackfillResponse _self;
  final $Res Function(_SocialBackfillResponse) _then;

/// Create a copy of SocialBackfillResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? identitiesCreated = null,}) {
  return _then(_SocialBackfillResponse(
identitiesCreated: null == identitiesCreated ? _self.identitiesCreated : identitiesCreated // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
