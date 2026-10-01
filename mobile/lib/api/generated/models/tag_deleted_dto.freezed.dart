// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'tag_deleted_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TagDeletedDto {

/// Contents the tag was detached from, counted like usageCount (trashed contents lose it too, uncounted)
 int get detachedCount;
/// Create a copy of TagDeletedDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TagDeletedDtoCopyWith<TagDeletedDto> get copyWith => _$TagDeletedDtoCopyWithImpl<TagDeletedDto>(this as TagDeletedDto, _$identity);

  /// Serializes this TagDeletedDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TagDeletedDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TagDeletedDto&&(identical(other.detachedCount, _this.detachedCount) || other.detachedCount == _this.detachedCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TagDeletedDto;
  return Object.hash(runtimeType,_this.detachedCount);
}

@override
String toString() {
  final _this = this as TagDeletedDto;
  return 'TagDeletedDto(detachedCount: ${_this.detachedCount})';
}


}

/// @nodoc
abstract mixin class $TagDeletedDtoCopyWith<$Res>  {
  factory $TagDeletedDtoCopyWith(TagDeletedDto value, $Res Function(TagDeletedDto) _then) = _$TagDeletedDtoCopyWithImpl;
@useResult
$Res call({
 int detachedCount
});




}
/// @nodoc
class _$TagDeletedDtoCopyWithImpl<$Res>
    implements $TagDeletedDtoCopyWith<$Res> {
  _$TagDeletedDtoCopyWithImpl(this._self, this._then);

  final TagDeletedDto _self;
  final $Res Function(TagDeletedDto) _then;

/// Create a copy of TagDeletedDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? detachedCount = null,}) {
  return _then(TagDeletedDto(
detachedCount: null == detachedCount ? _self.detachedCount : detachedCount // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [TagDeletedDto].
extension TagDeletedDtoPatterns on TagDeletedDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TagDeletedDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TagDeletedDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TagDeletedDto value)  $default,){
final _that = this;
switch (_that) {
case _TagDeletedDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TagDeletedDto value)?  $default,){
final _that = this;
switch (_that) {
case _TagDeletedDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int detachedCount)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TagDeletedDto() when $default != null:
return $default(_that.detachedCount);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int detachedCount)  $default,) {final _that = this;
switch (_that) {
case _TagDeletedDto():
return $default(_that.detachedCount);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int detachedCount)?  $default,) {final _that = this;
switch (_that) {
case _TagDeletedDto() when $default != null:
return $default(_that.detachedCount);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TagDeletedDto implements TagDeletedDto {
  const _TagDeletedDto({required this.detachedCount});
  factory _TagDeletedDto.fromJson(Map<String, dynamic> json) => _$TagDeletedDtoFromJson(json);

/// Contents the tag was detached from, counted like usageCount (trashed contents lose it too, uncounted)
@override final  int detachedCount;

/// Create a copy of TagDeletedDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TagDeletedDtoCopyWith<_TagDeletedDto> get copyWith => __$TagDeletedDtoCopyWithImpl<_TagDeletedDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TagDeletedDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TagDeletedDto&&(identical(other.detachedCount, detachedCount) || other.detachedCount == detachedCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,detachedCount);
}

@override
String toString() {
    return 'TagDeletedDto(detachedCount: $detachedCount)';
}


}

/// @nodoc
abstract mixin class _$TagDeletedDtoCopyWith<$Res> implements $TagDeletedDtoCopyWith<$Res> {
  factory _$TagDeletedDtoCopyWith(_TagDeletedDto value, $Res Function(_TagDeletedDto) _then) = __$TagDeletedDtoCopyWithImpl;
@override @useResult
$Res call({
 int detachedCount
});




}
/// @nodoc
class __$TagDeletedDtoCopyWithImpl<$Res>
    implements _$TagDeletedDtoCopyWith<$Res> {
  __$TagDeletedDtoCopyWithImpl(this._self, this._then);

  final _TagDeletedDto _self;
  final $Res Function(_TagDeletedDto) _then;

/// Create a copy of TagDeletedDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? detachedCount = null,}) {
  return _then(_TagDeletedDto(
detachedCount: null == detachedCount ? _self.detachedCount : detachedCount // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
