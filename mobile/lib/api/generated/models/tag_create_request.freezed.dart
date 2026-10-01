// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'tag_create_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TagCreateRequest {

/// Kind of content the tag applies to
 String get type;/// Label, trimmed; unique in the team and kind whatever the case, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
 String get label;/// Colour family
 String get color;
/// Create a copy of TagCreateRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TagCreateRequestCopyWith<TagCreateRequest> get copyWith => _$TagCreateRequestCopyWithImpl<TagCreateRequest>(this as TagCreateRequest, _$identity);

  /// Serializes this TagCreateRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TagCreateRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TagCreateRequest&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.label, _this.label) || other.label == _this.label)&&(identical(other.color, _this.color) || other.color == _this.color));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TagCreateRequest;
  return Object.hash(runtimeType,_this.type,_this.label,_this.color);
}

@override
String toString() {
  final _this = this as TagCreateRequest;
  return 'TagCreateRequest(type: ${_this.type}, label: ${_this.label}, color: ${_this.color})';
}


}

/// @nodoc
abstract mixin class $TagCreateRequestCopyWith<$Res>  {
  factory $TagCreateRequestCopyWith(TagCreateRequest value, $Res Function(TagCreateRequest) _then) = _$TagCreateRequestCopyWithImpl;
@useResult
$Res call({
 String type, String label, String color
});




}
/// @nodoc
class _$TagCreateRequestCopyWithImpl<$Res>
    implements $TagCreateRequestCopyWith<$Res> {
  _$TagCreateRequestCopyWithImpl(this._self, this._then);

  final TagCreateRequest _self;
  final $Res Function(TagCreateRequest) _then;

/// Create a copy of TagCreateRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? label = null,Object? color = null,}) {
  return _then(TagCreateRequest(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,label: null == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String,color: null == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [TagCreateRequest].
extension TagCreateRequestPatterns on TagCreateRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TagCreateRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TagCreateRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TagCreateRequest value)  $default,){
final _that = this;
switch (_that) {
case _TagCreateRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TagCreateRequest value)?  $default,){
final _that = this;
switch (_that) {
case _TagCreateRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String label,  String color)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TagCreateRequest() when $default != null:
return $default(_that.type,_that.label,_that.color);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String label,  String color)  $default,) {final _that = this;
switch (_that) {
case _TagCreateRequest():
return $default(_that.type,_that.label,_that.color);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String label,  String color)?  $default,) {final _that = this;
switch (_that) {
case _TagCreateRequest() when $default != null:
return $default(_that.type,_that.label,_that.color);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TagCreateRequest implements TagCreateRequest {
  const _TagCreateRequest({required this.type, required this.label, required this.color});
  factory _TagCreateRequest.fromJson(Map<String, dynamic> json) => _$TagCreateRequestFromJson(json);

/// Kind of content the tag applies to
@override final  String type;
/// Label, trimmed; unique in the team and kind whatever the case, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
@override final  String label;
/// Colour family
@override final  String color;

/// Create a copy of TagCreateRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TagCreateRequestCopyWith<_TagCreateRequest> get copyWith => __$TagCreateRequestCopyWithImpl<_TagCreateRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TagCreateRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TagCreateRequest&&(identical(other.type, type) || other.type == type)&&(identical(other.label, label) || other.label == label)&&(identical(other.color, color) || other.color == color));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,label,color);
}

@override
String toString() {
    return 'TagCreateRequest(type: $type, label: $label, color: $color)';
}


}

/// @nodoc
abstract mixin class _$TagCreateRequestCopyWith<$Res> implements $TagCreateRequestCopyWith<$Res> {
  factory _$TagCreateRequestCopyWith(_TagCreateRequest value, $Res Function(_TagCreateRequest) _then) = __$TagCreateRequestCopyWithImpl;
@override @useResult
$Res call({
 String type, String label, String color
});




}
/// @nodoc
class __$TagCreateRequestCopyWithImpl<$Res>
    implements _$TagCreateRequestCopyWith<$Res> {
  __$TagCreateRequestCopyWithImpl(this._self, this._then);

  final _TagCreateRequest _self;
  final $Res Function(_TagCreateRequest) _then;

/// Create a copy of TagCreateRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? label = null,Object? color = null,}) {
  return _then(_TagCreateRequest(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,label: null == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String,color: null == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
