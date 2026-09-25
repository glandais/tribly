// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'thumbnail_regeneration_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ThumbnailRegenerationResponse {

/// Whether this was a dry run (nothing redrawn)
 bool get dryRun;/// Number of entities matching the criteria, before the limit
 int get matched;/// Entities processed, at most the limit
 List<ThumbnailOwnerReport> get entities;
/// Create a copy of ThumbnailRegenerationResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ThumbnailRegenerationResponseCopyWith<ThumbnailRegenerationResponse> get copyWith => _$ThumbnailRegenerationResponseCopyWithImpl<ThumbnailRegenerationResponse>(this as ThumbnailRegenerationResponse, _$identity);

  /// Serializes this ThumbnailRegenerationResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ThumbnailRegenerationResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ThumbnailRegenerationResponse&&(identical(other.dryRun, _this.dryRun) || other.dryRun == _this.dryRun)&&(identical(other.matched, _this.matched) || other.matched == _this.matched)&&const DeepCollectionEquality().equals(other.entities, _this.entities));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ThumbnailRegenerationResponse;
  return Object.hash(runtimeType,_this.dryRun,_this.matched,const DeepCollectionEquality().hash(_this.entities));
}

@override
String toString() {
  final _this = this as ThumbnailRegenerationResponse;
  return 'ThumbnailRegenerationResponse(dryRun: ${_this.dryRun}, matched: ${_this.matched}, entities: ${_this.entities})';
}


}

/// @nodoc
abstract mixin class $ThumbnailRegenerationResponseCopyWith<$Res>  {
  factory $ThumbnailRegenerationResponseCopyWith(ThumbnailRegenerationResponse value, $Res Function(ThumbnailRegenerationResponse) _then) = _$ThumbnailRegenerationResponseCopyWithImpl;
@useResult
$Res call({
 bool dryRun, int matched, List<ThumbnailOwnerReport> entities
});




}
/// @nodoc
class _$ThumbnailRegenerationResponseCopyWithImpl<$Res>
    implements $ThumbnailRegenerationResponseCopyWith<$Res> {
  _$ThumbnailRegenerationResponseCopyWithImpl(this._self, this._then);

  final ThumbnailRegenerationResponse _self;
  final $Res Function(ThumbnailRegenerationResponse) _then;

/// Create a copy of ThumbnailRegenerationResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? dryRun = null,Object? matched = null,Object? entities = null,}) {
  return _then(ThumbnailRegenerationResponse(
dryRun: null == dryRun ? _self.dryRun : dryRun // ignore: cast_nullable_to_non_nullable
as bool,matched: null == matched ? _self.matched : matched // ignore: cast_nullable_to_non_nullable
as int,entities: null == entities ? _self.entities : entities // ignore: cast_nullable_to_non_nullable
as List<ThumbnailOwnerReport>,
  ));
}

}


/// Adds pattern-matching-related methods to [ThumbnailRegenerationResponse].
extension ThumbnailRegenerationResponsePatterns on ThumbnailRegenerationResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ThumbnailRegenerationResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ThumbnailRegenerationResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ThumbnailRegenerationResponse value)  $default,){
final _that = this;
switch (_that) {
case _ThumbnailRegenerationResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ThumbnailRegenerationResponse value)?  $default,){
final _that = this;
switch (_that) {
case _ThumbnailRegenerationResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( bool dryRun,  int matched,  List<ThumbnailOwnerReport> entities)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ThumbnailRegenerationResponse() when $default != null:
return $default(_that.dryRun,_that.matched,_that.entities);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( bool dryRun,  int matched,  List<ThumbnailOwnerReport> entities)  $default,) {final _that = this;
switch (_that) {
case _ThumbnailRegenerationResponse():
return $default(_that.dryRun,_that.matched,_that.entities);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( bool dryRun,  int matched,  List<ThumbnailOwnerReport> entities)?  $default,) {final _that = this;
switch (_that) {
case _ThumbnailRegenerationResponse() when $default != null:
return $default(_that.dryRun,_that.matched,_that.entities);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ThumbnailRegenerationResponse implements ThumbnailRegenerationResponse {
  const _ThumbnailRegenerationResponse({required this.dryRun, required this.matched, required  List<ThumbnailOwnerReport> entities}): _entities = entities;
  factory _ThumbnailRegenerationResponse.fromJson(Map<String, dynamic> json) => _$ThumbnailRegenerationResponseFromJson(json);

/// Whether this was a dry run (nothing redrawn)
@override final  bool dryRun;
/// Number of entities matching the criteria, before the limit
@override final  int matched;
/// Entities processed, at most the limit
 final  List<ThumbnailOwnerReport> _entities;
/// Entities processed, at most the limit
@override List<ThumbnailOwnerReport> get entities {
  if (_entities is EqualUnmodifiableListView) return _entities;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_entities);
}


/// Create a copy of ThumbnailRegenerationResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ThumbnailRegenerationResponseCopyWith<_ThumbnailRegenerationResponse> get copyWith => __$ThumbnailRegenerationResponseCopyWithImpl<_ThumbnailRegenerationResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ThumbnailRegenerationResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ThumbnailRegenerationResponse&&(identical(other.dryRun, dryRun) || other.dryRun == dryRun)&&(identical(other.matched, matched) || other.matched == matched)&&const DeepCollectionEquality().equals(other.entities, _entities));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,dryRun,matched,const DeepCollectionEquality().hash(_entities));
}

@override
String toString() {
    return 'ThumbnailRegenerationResponse(dryRun: $dryRun, matched: $matched, entities: $entities)';
}


}

/// @nodoc
abstract mixin class _$ThumbnailRegenerationResponseCopyWith<$Res> implements $ThumbnailRegenerationResponseCopyWith<$Res> {
  factory _$ThumbnailRegenerationResponseCopyWith(_ThumbnailRegenerationResponse value, $Res Function(_ThumbnailRegenerationResponse) _then) = __$ThumbnailRegenerationResponseCopyWithImpl;
@override @useResult
$Res call({
 bool dryRun, int matched, List<ThumbnailOwnerReport> entities
});




}
/// @nodoc
class __$ThumbnailRegenerationResponseCopyWithImpl<$Res>
    implements _$ThumbnailRegenerationResponseCopyWith<$Res> {
  __$ThumbnailRegenerationResponseCopyWithImpl(this._self, this._then);

  final _ThumbnailRegenerationResponse _self;
  final $Res Function(_ThumbnailRegenerationResponse) _then;

/// Create a copy of ThumbnailRegenerationResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? dryRun = null,Object? matched = null,Object? entities = null,}) {
  return _then(_ThumbnailRegenerationResponse(
dryRun: null == dryRun ? _self.dryRun : dryRun // ignore: cast_nullable_to_non_nullable
as bool,matched: null == matched ? _self.matched : matched // ignore: cast_nullable_to_non_nullable
as int,entities: null == entities ? _self._entities : entities // ignore: cast_nullable_to_non_nullable
as List<ThumbnailOwnerReport>,
  ));
}


}

// dart format on
