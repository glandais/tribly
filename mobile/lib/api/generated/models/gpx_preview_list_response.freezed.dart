// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'gpx_preview_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$GpxPreviewListResponse {

/// List of GPX previews
 List<GpxPreviewSummaryDto> get previews;/// Total number of previews
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of GpxPreviewListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$GpxPreviewListResponseCopyWith<GpxPreviewListResponse> get copyWith => _$GpxPreviewListResponseCopyWithImpl<GpxPreviewListResponse>(this as GpxPreviewListResponse, _$identity);

  /// Serializes this GpxPreviewListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is GpxPreviewListResponse&&const DeepCollectionEquality().equals(other.previews, previews)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(previews),total,page,size);

@override
String toString() {
  return 'GpxPreviewListResponse(previews: $previews, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $GpxPreviewListResponseCopyWith<$Res>  {
  factory $GpxPreviewListResponseCopyWith(GpxPreviewListResponse value, $Res Function(GpxPreviewListResponse) _then) = _$GpxPreviewListResponseCopyWithImpl;
@useResult
$Res call({
 List<GpxPreviewSummaryDto> previews, int total, int page, int size
});




}
/// @nodoc
class _$GpxPreviewListResponseCopyWithImpl<$Res>
    implements $GpxPreviewListResponseCopyWith<$Res> {
  _$GpxPreviewListResponseCopyWithImpl(this._self, this._then);

  final GpxPreviewListResponse _self;
  final $Res Function(GpxPreviewListResponse) _then;

/// Create a copy of GpxPreviewListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? previews = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_self.copyWith(
previews: null == previews ? _self.previews : previews // ignore: cast_nullable_to_non_nullable
as List<GpxPreviewSummaryDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [GpxPreviewListResponse].
extension GpxPreviewListResponsePatterns on GpxPreviewListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _GpxPreviewListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _GpxPreviewListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _GpxPreviewListResponse value)  $default,){
final _that = this;
switch (_that) {
case _GpxPreviewListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _GpxPreviewListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _GpxPreviewListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<GpxPreviewSummaryDto> previews,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _GpxPreviewListResponse() when $default != null:
return $default(_that.previews,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<GpxPreviewSummaryDto> previews,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _GpxPreviewListResponse():
return $default(_that.previews,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<GpxPreviewSummaryDto> previews,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _GpxPreviewListResponse() when $default != null:
return $default(_that.previews,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _GpxPreviewListResponse implements GpxPreviewListResponse {
  const _GpxPreviewListResponse({required final  List<GpxPreviewSummaryDto> previews, required this.total, required this.page, required this.size}): _previews = previews;
  factory _GpxPreviewListResponse.fromJson(Map<String, dynamic> json) => _$GpxPreviewListResponseFromJson(json);

/// List of GPX previews
 final  List<GpxPreviewSummaryDto> _previews;
/// List of GPX previews
@override List<GpxPreviewSummaryDto> get previews {
  if (_previews is EqualUnmodifiableListView) return _previews;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_previews);
}

/// Total number of previews
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of GpxPreviewListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$GpxPreviewListResponseCopyWith<_GpxPreviewListResponse> get copyWith => __$GpxPreviewListResponseCopyWithImpl<_GpxPreviewListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$GpxPreviewListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _GpxPreviewListResponse&&const DeepCollectionEquality().equals(other._previews, _previews)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_previews),total,page,size);

@override
String toString() {
  return 'GpxPreviewListResponse(previews: $previews, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$GpxPreviewListResponseCopyWith<$Res> implements $GpxPreviewListResponseCopyWith<$Res> {
  factory _$GpxPreviewListResponseCopyWith(_GpxPreviewListResponse value, $Res Function(_GpxPreviewListResponse) _then) = __$GpxPreviewListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<GpxPreviewSummaryDto> previews, int total, int page, int size
});




}
/// @nodoc
class __$GpxPreviewListResponseCopyWithImpl<$Res>
    implements _$GpxPreviewListResponseCopyWith<$Res> {
  __$GpxPreviewListResponseCopyWithImpl(this._self, this._then);

  final _GpxPreviewListResponse _self;
  final $Res Function(_GpxPreviewListResponse) _then;

/// Create a copy of GpxPreviewListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? previews = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_GpxPreviewListResponse(
previews: null == previews ? _self._previews : previews // ignore: cast_nullable_to_non_nullable
as List<GpxPreviewSummaryDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
