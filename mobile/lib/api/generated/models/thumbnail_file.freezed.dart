// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'thumbnail_file.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ThumbnailFile {

/// Asset type
 String get type;/// Stored size in bytes, -1 when the file is missing
 int get bytes;
/// Create a copy of ThumbnailFile
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ThumbnailFileCopyWith<ThumbnailFile> get copyWith => _$ThumbnailFileCopyWithImpl<ThumbnailFile>(this as ThumbnailFile, _$identity);

  /// Serializes this ThumbnailFile to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ThumbnailFile;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ThumbnailFile&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.bytes, _this.bytes) || other.bytes == _this.bytes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ThumbnailFile;
  return Object.hash(runtimeType,_this.type,_this.bytes);
}

@override
String toString() {
  final _this = this as ThumbnailFile;
  return 'ThumbnailFile(type: ${_this.type}, bytes: ${_this.bytes})';
}


}

/// @nodoc
abstract mixin class $ThumbnailFileCopyWith<$Res>  {
  factory $ThumbnailFileCopyWith(ThumbnailFile value, $Res Function(ThumbnailFile) _then) = _$ThumbnailFileCopyWithImpl;
@useResult
$Res call({
 String type, int bytes
});




}
/// @nodoc
class _$ThumbnailFileCopyWithImpl<$Res>
    implements $ThumbnailFileCopyWith<$Res> {
  _$ThumbnailFileCopyWithImpl(this._self, this._then);

  final ThumbnailFile _self;
  final $Res Function(ThumbnailFile) _then;

/// Create a copy of ThumbnailFile
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? bytes = null,}) {
  return _then(ThumbnailFile(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,bytes: null == bytes ? _self.bytes : bytes // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [ThumbnailFile].
extension ThumbnailFilePatterns on ThumbnailFile {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ThumbnailFile value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ThumbnailFile() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ThumbnailFile value)  $default,){
final _that = this;
switch (_that) {
case _ThumbnailFile():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ThumbnailFile value)?  $default,){
final _that = this;
switch (_that) {
case _ThumbnailFile() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  int bytes)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ThumbnailFile() when $default != null:
return $default(_that.type,_that.bytes);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  int bytes)  $default,) {final _that = this;
switch (_that) {
case _ThumbnailFile():
return $default(_that.type,_that.bytes);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  int bytes)?  $default,) {final _that = this;
switch (_that) {
case _ThumbnailFile() when $default != null:
return $default(_that.type,_that.bytes);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ThumbnailFile implements ThumbnailFile {
  const _ThumbnailFile({required this.type, required this.bytes});
  factory _ThumbnailFile.fromJson(Map<String, dynamic> json) => _$ThumbnailFileFromJson(json);

/// Asset type
@override final  String type;
/// Stored size in bytes, -1 when the file is missing
@override final  int bytes;

/// Create a copy of ThumbnailFile
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ThumbnailFileCopyWith<_ThumbnailFile> get copyWith => __$ThumbnailFileCopyWithImpl<_ThumbnailFile>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ThumbnailFileToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ThumbnailFile&&(identical(other.type, type) || other.type == type)&&(identical(other.bytes, bytes) || other.bytes == bytes));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,bytes);
}

@override
String toString() {
    return 'ThumbnailFile(type: $type, bytes: $bytes)';
}


}

/// @nodoc
abstract mixin class _$ThumbnailFileCopyWith<$Res> implements $ThumbnailFileCopyWith<$Res> {
  factory _$ThumbnailFileCopyWith(_ThumbnailFile value, $Res Function(_ThumbnailFile) _then) = __$ThumbnailFileCopyWithImpl;
@override @useResult
$Res call({
 String type, int bytes
});




}
/// @nodoc
class __$ThumbnailFileCopyWithImpl<$Res>
    implements _$ThumbnailFileCopyWith<$Res> {
  __$ThumbnailFileCopyWithImpl(this._self, this._then);

  final _ThumbnailFile _self;
  final $Res Function(_ThumbnailFile) _then;

/// Create a copy of ThumbnailFile
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? bytes = null,}) {
  return _then(_ThumbnailFile(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,bytes: null == bytes ? _self.bytes : bytes // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
