// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'sitemap_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$SitemapDto {

/// Indexable pages
 List<SitemapEntryDto> get entries;
/// Create a copy of SitemapDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$SitemapDtoCopyWith<SitemapDto> get copyWith => _$SitemapDtoCopyWithImpl<SitemapDto>(this as SitemapDto, _$identity);

  /// Serializes this SitemapDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as SitemapDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is SitemapDto&&const DeepCollectionEquality().equals(other.entries, _this.entries));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as SitemapDto;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.entries));
}

@override
String toString() {
  final _this = this as SitemapDto;
  return 'SitemapDto(entries: ${_this.entries})';
}


}

/// @nodoc
abstract mixin class $SitemapDtoCopyWith<$Res>  {
  factory $SitemapDtoCopyWith(SitemapDto value, $Res Function(SitemapDto) _then) = _$SitemapDtoCopyWithImpl;
@useResult
$Res call({
 List<SitemapEntryDto> entries
});




}
/// @nodoc
class _$SitemapDtoCopyWithImpl<$Res>
    implements $SitemapDtoCopyWith<$Res> {
  _$SitemapDtoCopyWithImpl(this._self, this._then);

  final SitemapDto _self;
  final $Res Function(SitemapDto) _then;

/// Create a copy of SitemapDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? entries = null,}) {
  return _then(SitemapDto(
entries: null == entries ? _self.entries : entries // ignore: cast_nullable_to_non_nullable
as List<SitemapEntryDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [SitemapDto].
extension SitemapDtoPatterns on SitemapDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _SitemapDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _SitemapDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _SitemapDto value)  $default,){
final _that = this;
switch (_that) {
case _SitemapDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _SitemapDto value)?  $default,){
final _that = this;
switch (_that) {
case _SitemapDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<SitemapEntryDto> entries)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _SitemapDto() when $default != null:
return $default(_that.entries);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<SitemapEntryDto> entries)  $default,) {final _that = this;
switch (_that) {
case _SitemapDto():
return $default(_that.entries);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<SitemapEntryDto> entries)?  $default,) {final _that = this;
switch (_that) {
case _SitemapDto() when $default != null:
return $default(_that.entries);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _SitemapDto implements SitemapDto {
  const _SitemapDto({required  List<SitemapEntryDto> entries}): _entries = entries;
  factory _SitemapDto.fromJson(Map<String, dynamic> json) => _$SitemapDtoFromJson(json);

/// Indexable pages
 final  List<SitemapEntryDto> _entries;
/// Indexable pages
@override List<SitemapEntryDto> get entries {
  if (_entries is EqualUnmodifiableListView) return _entries;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_entries);
}


/// Create a copy of SitemapDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$SitemapDtoCopyWith<_SitemapDto> get copyWith => __$SitemapDtoCopyWithImpl<_SitemapDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$SitemapDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _SitemapDto&&const DeepCollectionEquality().equals(other.entries, _entries));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_entries));
}

@override
String toString() {
    return 'SitemapDto(entries: $entries)';
}


}

/// @nodoc
abstract mixin class _$SitemapDtoCopyWith<$Res> implements $SitemapDtoCopyWith<$Res> {
  factory _$SitemapDtoCopyWith(_SitemapDto value, $Res Function(_SitemapDto) _then) = __$SitemapDtoCopyWithImpl;
@override @useResult
$Res call({
 List<SitemapEntryDto> entries
});




}
/// @nodoc
class __$SitemapDtoCopyWithImpl<$Res>
    implements _$SitemapDtoCopyWith<$Res> {
  __$SitemapDtoCopyWithImpl(this._self, this._then);

  final _SitemapDto _self;
  final $Res Function(_SitemapDto) _then;

/// Create a copy of SitemapDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? entries = null,}) {
  return _then(_SitemapDto(
entries: null == entries ? _self._entries : entries // ignore: cast_nullable_to_non_nullable
as List<SitemapEntryDto>,
  ));
}


}

// dart format on
