// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'reorder_pages_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ReorderPagesRequest {

/// Ordered list of page IDs
 List<String> get pageIds;
/// Create a copy of ReorderPagesRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ReorderPagesRequestCopyWith<ReorderPagesRequest> get copyWith => _$ReorderPagesRequestCopyWithImpl<ReorderPagesRequest>(this as ReorderPagesRequest, _$identity);

  /// Serializes this ReorderPagesRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ReorderPagesRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ReorderPagesRequest&&const DeepCollectionEquality().equals(other.pageIds, _this.pageIds));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ReorderPagesRequest;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.pageIds));
}

@override
String toString() {
  final _this = this as ReorderPagesRequest;
  return 'ReorderPagesRequest(pageIds: ${_this.pageIds})';
}


}

/// @nodoc
abstract mixin class $ReorderPagesRequestCopyWith<$Res>  {
  factory $ReorderPagesRequestCopyWith(ReorderPagesRequest value, $Res Function(ReorderPagesRequest) _then) = _$ReorderPagesRequestCopyWithImpl;
@useResult
$Res call({
 List<String> pageIds
});




}
/// @nodoc
class _$ReorderPagesRequestCopyWithImpl<$Res>
    implements $ReorderPagesRequestCopyWith<$Res> {
  _$ReorderPagesRequestCopyWithImpl(this._self, this._then);

  final ReorderPagesRequest _self;
  final $Res Function(ReorderPagesRequest) _then;

/// Create a copy of ReorderPagesRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? pageIds = null,}) {
  return _then(ReorderPagesRequest(
pageIds: null == pageIds ? _self.pageIds : pageIds // ignore: cast_nullable_to_non_nullable
as List<String>,
  ));
}

}


/// Adds pattern-matching-related methods to [ReorderPagesRequest].
extension ReorderPagesRequestPatterns on ReorderPagesRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ReorderPagesRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ReorderPagesRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ReorderPagesRequest value)  $default,){
final _that = this;
switch (_that) {
case _ReorderPagesRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ReorderPagesRequest value)?  $default,){
final _that = this;
switch (_that) {
case _ReorderPagesRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<String> pageIds)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ReorderPagesRequest() when $default != null:
return $default(_that.pageIds);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<String> pageIds)  $default,) {final _that = this;
switch (_that) {
case _ReorderPagesRequest():
return $default(_that.pageIds);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<String> pageIds)?  $default,) {final _that = this;
switch (_that) {
case _ReorderPagesRequest() when $default != null:
return $default(_that.pageIds);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ReorderPagesRequest implements ReorderPagesRequest {
  const _ReorderPagesRequest({required  List<String> pageIds}): _pageIds = pageIds;
  factory _ReorderPagesRequest.fromJson(Map<String, dynamic> json) => _$ReorderPagesRequestFromJson(json);

/// Ordered list of page IDs
 final  List<String> _pageIds;
/// Ordered list of page IDs
@override List<String> get pageIds {
  if (_pageIds is EqualUnmodifiableListView) return _pageIds;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_pageIds);
}


/// Create a copy of ReorderPagesRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ReorderPagesRequestCopyWith<_ReorderPagesRequest> get copyWith => __$ReorderPagesRequestCopyWithImpl<_ReorderPagesRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ReorderPagesRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ReorderPagesRequest&&const DeepCollectionEquality().equals(other.pageIds, _pageIds));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_pageIds));
}

@override
String toString() {
    return 'ReorderPagesRequest(pageIds: $pageIds)';
}


}

/// @nodoc
abstract mixin class _$ReorderPagesRequestCopyWith<$Res> implements $ReorderPagesRequestCopyWith<$Res> {
  factory _$ReorderPagesRequestCopyWith(_ReorderPagesRequest value, $Res Function(_ReorderPagesRequest) _then) = __$ReorderPagesRequestCopyWithImpl;
@override @useResult
$Res call({
 List<String> pageIds
});




}
/// @nodoc
class __$ReorderPagesRequestCopyWithImpl<$Res>
    implements _$ReorderPagesRequestCopyWith<$Res> {
  __$ReorderPagesRequestCopyWithImpl(this._self, this._then);

  final _ReorderPagesRequest _self;
  final $Res Function(_ReorderPagesRequest) _then;

/// Create a copy of ReorderPagesRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? pageIds = null,}) {
  return _then(_ReorderPagesRequest(
pageIds: null == pageIds ? _self._pageIds : pageIds // ignore: cast_nullable_to_non_nullable
as List<String>,
  ));
}


}

// dart format on
