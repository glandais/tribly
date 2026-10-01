// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'tag_update_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TagUpdateRequest {

/// New label, trimmed, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
 String? get label;/// New colour family
 String? get color;
/// Create a copy of TagUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TagUpdateRequestCopyWith<TagUpdateRequest> get copyWith => _$TagUpdateRequestCopyWithImpl<TagUpdateRequest>(this as TagUpdateRequest, _$identity);

  /// Serializes this TagUpdateRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as TagUpdateRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TagUpdateRequest&&(identical(other.label, _this.label) || other.label == _this.label)&&(identical(other.color, _this.color) || other.color == _this.color));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as TagUpdateRequest;
  return Object.hash(runtimeType,_this.label,_this.color);
}

@override
String toString() {
  final _this = this as TagUpdateRequest;
  return 'TagUpdateRequest(label: ${_this.label}, color: ${_this.color})';
}


}

/// @nodoc
abstract mixin class $TagUpdateRequestCopyWith<$Res>  {
  factory $TagUpdateRequestCopyWith(TagUpdateRequest value, $Res Function(TagUpdateRequest) _then) = _$TagUpdateRequestCopyWithImpl;
@useResult
$Res call({
 String? label, String? color
});




}
/// @nodoc
class _$TagUpdateRequestCopyWithImpl<$Res>
    implements $TagUpdateRequestCopyWith<$Res> {
  _$TagUpdateRequestCopyWithImpl(this._self, this._then);

  final TagUpdateRequest _self;
  final $Res Function(TagUpdateRequest) _then;

/// Create a copy of TagUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? label = freezed,Object? color = freezed,}) {
  return _then(TagUpdateRequest(
label: freezed == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String?,color: freezed == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [TagUpdateRequest].
extension TagUpdateRequestPatterns on TagUpdateRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TagUpdateRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TagUpdateRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TagUpdateRequest value)  $default,){
final _that = this;
switch (_that) {
case _TagUpdateRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TagUpdateRequest value)?  $default,){
final _that = this;
switch (_that) {
case _TagUpdateRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String? label,  String? color)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TagUpdateRequest() when $default != null:
return $default(_that.label,_that.color);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String? label,  String? color)  $default,) {final _that = this;
switch (_that) {
case _TagUpdateRequest():
return $default(_that.label,_that.color);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String? label,  String? color)?  $default,) {final _that = this;
switch (_that) {
case _TagUpdateRequest() when $default != null:
return $default(_that.label,_that.color);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TagUpdateRequest implements TagUpdateRequest {
  const _TagUpdateRequest({this.label, this.color});
  factory _TagUpdateRequest.fromJson(Map<String, dynamic> json) => _$TagUpdateRequestFromJson(json);

/// New label, trimmed, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
@override final  String? label;
/// New colour family
@override final  String? color;

/// Create a copy of TagUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TagUpdateRequestCopyWith<_TagUpdateRequest> get copyWith => __$TagUpdateRequestCopyWithImpl<_TagUpdateRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TagUpdateRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _TagUpdateRequest&&(identical(other.label, label) || other.label == label)&&(identical(other.color, color) || other.color == color));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,label,color);
}

@override
String toString() {
    return 'TagUpdateRequest(label: $label, color: $color)';
}


}

/// @nodoc
abstract mixin class _$TagUpdateRequestCopyWith<$Res> implements $TagUpdateRequestCopyWith<$Res> {
  factory _$TagUpdateRequestCopyWith(_TagUpdateRequest value, $Res Function(_TagUpdateRequest) _then) = __$TagUpdateRequestCopyWithImpl;
@override @useResult
$Res call({
 String? label, String? color
});




}
/// @nodoc
class __$TagUpdateRequestCopyWithImpl<$Res>
    implements _$TagUpdateRequestCopyWith<$Res> {
  __$TagUpdateRequestCopyWithImpl(this._self, this._then);

  final _TagUpdateRequest _self;
  final $Res Function(_TagUpdateRequest) _then;

/// Create a copy of TagUpdateRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? label = freezed,Object? color = freezed,}) {
  return _then(_TagUpdateRequest(
label: freezed == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String?,color: freezed == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
