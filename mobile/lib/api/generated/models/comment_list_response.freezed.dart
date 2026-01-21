// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'comment_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CommentListResponse {

/// List of comments (top-level only, with nested replies)
 List<CommentDto> get items;/// Total count including replies
 int get total;
/// Create a copy of CommentListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CommentListResponseCopyWith<CommentListResponse> get copyWith => _$CommentListResponseCopyWithImpl<CommentListResponse>(this as CommentListResponse, _$identity);

  /// Serializes this CommentListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CommentListResponse&&const DeepCollectionEquality().equals(other.items, items)&&(identical(other.total, total) || other.total == total));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(items),total);

@override
String toString() {
  return 'CommentListResponse(items: $items, total: $total)';
}


}

/// @nodoc
abstract mixin class $CommentListResponseCopyWith<$Res>  {
  factory $CommentListResponseCopyWith(CommentListResponse value, $Res Function(CommentListResponse) _then) = _$CommentListResponseCopyWithImpl;
@useResult
$Res call({
 List<CommentDto> items, int total
});




}
/// @nodoc
class _$CommentListResponseCopyWithImpl<$Res>
    implements $CommentListResponseCopyWith<$Res> {
  _$CommentListResponseCopyWithImpl(this._self, this._then);

  final CommentListResponse _self;
  final $Res Function(CommentListResponse) _then;

/// Create a copy of CommentListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? items = null,Object? total = null,}) {
  return _then(_self.copyWith(
items: null == items ? _self.items : items // ignore: cast_nullable_to_non_nullable
as List<CommentDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [CommentListResponse].
extension CommentListResponsePatterns on CommentListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CommentListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CommentListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CommentListResponse value)  $default,){
final _that = this;
switch (_that) {
case _CommentListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CommentListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _CommentListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<CommentDto> items,  int total)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CommentListResponse() when $default != null:
return $default(_that.items,_that.total);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<CommentDto> items,  int total)  $default,) {final _that = this;
switch (_that) {
case _CommentListResponse():
return $default(_that.items,_that.total);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<CommentDto> items,  int total)?  $default,) {final _that = this;
switch (_that) {
case _CommentListResponse() when $default != null:
return $default(_that.items,_that.total);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CommentListResponse implements CommentListResponse {
  const _CommentListResponse({required final  List<CommentDto> items, required this.total}): _items = items;
  factory _CommentListResponse.fromJson(Map<String, dynamic> json) => _$CommentListResponseFromJson(json);

/// List of comments (top-level only, with nested replies)
 final  List<CommentDto> _items;
/// List of comments (top-level only, with nested replies)
@override List<CommentDto> get items {
  if (_items is EqualUnmodifiableListView) return _items;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_items);
}

/// Total count including replies
@override final  int total;

/// Create a copy of CommentListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CommentListResponseCopyWith<_CommentListResponse> get copyWith => __$CommentListResponseCopyWithImpl<_CommentListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CommentListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _CommentListResponse&&const DeepCollectionEquality().equals(other._items, _items)&&(identical(other.total, total) || other.total == total));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_items),total);

@override
String toString() {
  return 'CommentListResponse(items: $items, total: $total)';
}


}

/// @nodoc
abstract mixin class _$CommentListResponseCopyWith<$Res> implements $CommentListResponseCopyWith<$Res> {
  factory _$CommentListResponseCopyWith(_CommentListResponse value, $Res Function(_CommentListResponse) _then) = __$CommentListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<CommentDto> items, int total
});




}
/// @nodoc
class __$CommentListResponseCopyWithImpl<$Res>
    implements _$CommentListResponseCopyWith<$Res> {
  __$CommentListResponseCopyWithImpl(this._self, this._then);

  final _CommentListResponse _self;
  final $Res Function(_CommentListResponse) _then;

/// Create a copy of CommentListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? items = null,Object? total = null,}) {
  return _then(_CommentListResponse(
items: null == items ? _self._items : items // ignore: cast_nullable_to_non_nullable
as List<CommentDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
