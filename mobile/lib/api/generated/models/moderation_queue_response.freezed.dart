// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'moderation_queue_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ModerationQueueResponse {

/// One item per reported target
 List<ModerationItemDto> get items;/// How many items
 int get total;
/// Create a copy of ModerationQueueResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ModerationQueueResponseCopyWith<ModerationQueueResponse> get copyWith => _$ModerationQueueResponseCopyWithImpl<ModerationQueueResponse>(this as ModerationQueueResponse, _$identity);

  /// Serializes this ModerationQueueResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ModerationQueueResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ModerationQueueResponse&&const DeepCollectionEquality().equals(other.items, _this.items)&&(identical(other.total, _this.total) || other.total == _this.total));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ModerationQueueResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.items),_this.total);
}

@override
String toString() {
  final _this = this as ModerationQueueResponse;
  return 'ModerationQueueResponse(items: ${_this.items}, total: ${_this.total})';
}


}

/// @nodoc
abstract mixin class $ModerationQueueResponseCopyWith<$Res>  {
  factory $ModerationQueueResponseCopyWith(ModerationQueueResponse value, $Res Function(ModerationQueueResponse) _then) = _$ModerationQueueResponseCopyWithImpl;
@useResult
$Res call({
 List<ModerationItemDto> items, int total
});




}
/// @nodoc
class _$ModerationQueueResponseCopyWithImpl<$Res>
    implements $ModerationQueueResponseCopyWith<$Res> {
  _$ModerationQueueResponseCopyWithImpl(this._self, this._then);

  final ModerationQueueResponse _self;
  final $Res Function(ModerationQueueResponse) _then;

/// Create a copy of ModerationQueueResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? items = null,Object? total = null,}) {
  return _then(ModerationQueueResponse(
items: null == items ? _self.items : items // ignore: cast_nullable_to_non_nullable
as List<ModerationItemDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [ModerationQueueResponse].
extension ModerationQueueResponsePatterns on ModerationQueueResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ModerationQueueResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ModerationQueueResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ModerationQueueResponse value)  $default,){
final _that = this;
switch (_that) {
case _ModerationQueueResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ModerationQueueResponse value)?  $default,){
final _that = this;
switch (_that) {
case _ModerationQueueResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<ModerationItemDto> items,  int total)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ModerationQueueResponse() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<ModerationItemDto> items,  int total)  $default,) {final _that = this;
switch (_that) {
case _ModerationQueueResponse():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<ModerationItemDto> items,  int total)?  $default,) {final _that = this;
switch (_that) {
case _ModerationQueueResponse() when $default != null:
return $default(_that.items,_that.total);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ModerationQueueResponse implements ModerationQueueResponse {
  const _ModerationQueueResponse({required  List<ModerationItemDto> items, required this.total}): _items = items;
  factory _ModerationQueueResponse.fromJson(Map<String, dynamic> json) => _$ModerationQueueResponseFromJson(json);

/// One item per reported target
 final  List<ModerationItemDto> _items;
/// One item per reported target
@override List<ModerationItemDto> get items {
  if (_items is EqualUnmodifiableListView) return _items;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_items);
}

/// How many items
@override final  int total;

/// Create a copy of ModerationQueueResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ModerationQueueResponseCopyWith<_ModerationQueueResponse> get copyWith => __$ModerationQueueResponseCopyWithImpl<_ModerationQueueResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ModerationQueueResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ModerationQueueResponse&&const DeepCollectionEquality().equals(other.items, _items)&&(identical(other.total, total) || other.total == total));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_items),total);
}

@override
String toString() {
    return 'ModerationQueueResponse(items: $items, total: $total)';
}


}

/// @nodoc
abstract mixin class _$ModerationQueueResponseCopyWith<$Res> implements $ModerationQueueResponseCopyWith<$Res> {
  factory _$ModerationQueueResponseCopyWith(_ModerationQueueResponse value, $Res Function(_ModerationQueueResponse) _then) = __$ModerationQueueResponseCopyWithImpl;
@override @useResult
$Res call({
 List<ModerationItemDto> items, int total
});




}
/// @nodoc
class __$ModerationQueueResponseCopyWithImpl<$Res>
    implements _$ModerationQueueResponseCopyWith<$Res> {
  __$ModerationQueueResponseCopyWithImpl(this._self, this._then);

  final _ModerationQueueResponse _self;
  final $Res Function(_ModerationQueueResponse) _then;

/// Create a copy of ModerationQueueResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? items = null,Object? total = null,}) {
  return _then(_ModerationQueueResponse(
items: null == items ? _self._items : items // ignore: cast_nullable_to_non_nullable
as List<ModerationItemDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
