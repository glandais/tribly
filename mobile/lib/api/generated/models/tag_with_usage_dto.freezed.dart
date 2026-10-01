// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'tag_with_usage_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TagWithUsageDto {

/// Tag ID (TSID)
 String get id;/// Label, at most 32 characters
 String get label;/// Colour family
 String get color;/// Kind of content the tag applies to
 String get type;/// Contents carrying the tag: rides, posts, trips, routes and ads out of the trash, plus ride templates — what a deletion would detach it from
 int get usageCount;
/// Create a copy of TagWithUsageDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TagWithUsageDtoCopyWith<TagWithUsageDto> get copyWith => _$TagWithUsageDtoCopyWithImpl<TagWithUsageDto>(this as TagWithUsageDto, _$identity);

  /// Serializes this TagWithUsageDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TagWithUsageDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TagWithUsageDto&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.label, _this.label) || other.label == _this.label)&&(identical(other.color, _this.color) || other.color == _this.color)&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.usageCount, _this.usageCount) || other.usageCount == _this.usageCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TagWithUsageDto;
  return Object.hash(runtimeType,_this.id,_this.label,_this.color,_this.type,_this.usageCount);
}

@override
String toString() {
  final _this = this as TagWithUsageDto;
  return 'TagWithUsageDto(id: ${_this.id}, label: ${_this.label}, color: ${_this.color}, type: ${_this.type}, usageCount: ${_this.usageCount})';
}


}

/// @nodoc
abstract mixin class $TagWithUsageDtoCopyWith<$Res>  {
  factory $TagWithUsageDtoCopyWith(TagWithUsageDto value, $Res Function(TagWithUsageDto) _then) = _$TagWithUsageDtoCopyWithImpl;
@useResult
$Res call({
 String id, String label, String color, String type, int usageCount
});




}
/// @nodoc
class _$TagWithUsageDtoCopyWithImpl<$Res>
    implements $TagWithUsageDtoCopyWith<$Res> {
  _$TagWithUsageDtoCopyWithImpl(this._self, this._then);

  final TagWithUsageDto _self;
  final $Res Function(TagWithUsageDto) _then;

/// Create a copy of TagWithUsageDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? label = null,Object? color = null,Object? type = null,Object? usageCount = null,}) {
  return _then(TagWithUsageDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,label: null == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String,color: null == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,usageCount: null == usageCount ? _self.usageCount : usageCount // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [TagWithUsageDto].
extension TagWithUsageDtoPatterns on TagWithUsageDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TagWithUsageDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TagWithUsageDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TagWithUsageDto value)  $default,){
final _that = this;
switch (_that) {
case _TagWithUsageDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TagWithUsageDto value)?  $default,){
final _that = this;
switch (_that) {
case _TagWithUsageDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String label,  String color,  String type,  int usageCount)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TagWithUsageDto() when $default != null:
return $default(_that.id,_that.label,_that.color,_that.type,_that.usageCount);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String label,  String color,  String type,  int usageCount)  $default,) {final _that = this;
switch (_that) {
case _TagWithUsageDto():
return $default(_that.id,_that.label,_that.color,_that.type,_that.usageCount);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String label,  String color,  String type,  int usageCount)?  $default,) {final _that = this;
switch (_that) {
case _TagWithUsageDto() when $default != null:
return $default(_that.id,_that.label,_that.color,_that.type,_that.usageCount);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TagWithUsageDto implements TagWithUsageDto {
  const _TagWithUsageDto({required this.id, required this.label, required this.color, required this.type, required this.usageCount});
  factory _TagWithUsageDto.fromJson(Map<String, dynamic> json) => _$TagWithUsageDtoFromJson(json);

/// Tag ID (TSID)
@override final  String id;
/// Label, at most 32 characters
@override final  String label;
/// Colour family
@override final  String color;
/// Kind of content the tag applies to
@override final  String type;
/// Contents carrying the tag: rides, posts, trips, routes and ads out of the trash, plus ride templates — what a deletion would detach it from
@override final  int usageCount;

/// Create a copy of TagWithUsageDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TagWithUsageDtoCopyWith<_TagWithUsageDto> get copyWith => __$TagWithUsageDtoCopyWithImpl<_TagWithUsageDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TagWithUsageDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TagWithUsageDto&&(identical(other.id, id) || other.id == id)&&(identical(other.label, label) || other.label == label)&&(identical(other.color, color) || other.color == color)&&(identical(other.type, type) || other.type == type)&&(identical(other.usageCount, usageCount) || other.usageCount == usageCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,id,label,color,type,usageCount);
}

@override
String toString() {
    return 'TagWithUsageDto(id: $id, label: $label, color: $color, type: $type, usageCount: $usageCount)';
}


}

/// @nodoc
abstract mixin class _$TagWithUsageDtoCopyWith<$Res> implements $TagWithUsageDtoCopyWith<$Res> {
  factory _$TagWithUsageDtoCopyWith(_TagWithUsageDto value, $Res Function(_TagWithUsageDto) _then) = __$TagWithUsageDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String label, String color, String type, int usageCount
});




}
/// @nodoc
class __$TagWithUsageDtoCopyWithImpl<$Res>
    implements _$TagWithUsageDtoCopyWith<$Res> {
  __$TagWithUsageDtoCopyWithImpl(this._self, this._then);

  final _TagWithUsageDto _self;
  final $Res Function(_TagWithUsageDto) _then;

/// Create a copy of TagWithUsageDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? label = null,Object? color = null,Object? type = null,Object? usageCount = null,}) {
  return _then(_TagWithUsageDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,label: null == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String,color: null == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,usageCount: null == usageCount ? _self.usageCount : usageCount // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
