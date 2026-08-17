// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'post_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PostListResponse {

/// List of posts
 List<PostDto> get posts;/// Total number of posts
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of PostListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PostListResponseCopyWith<PostListResponse> get copyWith => _$PostListResponseCopyWithImpl<PostListResponse>(this as PostListResponse, _$identity);

  /// Serializes this PostListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PostListResponse&&const DeepCollectionEquality().equals(other.posts, posts)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(posts),total,page,size);

@override
String toString() {
  return 'PostListResponse(posts: $posts, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $PostListResponseCopyWith<$Res>  {
  factory $PostListResponseCopyWith(PostListResponse value, $Res Function(PostListResponse) _then) = _$PostListResponseCopyWithImpl;
@useResult
$Res call({
 List<PostDto> posts, int total, int page, int size
});




}
/// @nodoc
class _$PostListResponseCopyWithImpl<$Res>
    implements $PostListResponseCopyWith<$Res> {
  _$PostListResponseCopyWithImpl(this._self, this._then);

  final PostListResponse _self;
  final $Res Function(PostListResponse) _then;

/// Create a copy of PostListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? posts = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(PostListResponse(
posts: null == posts ? _self.posts : posts // ignore: cast_nullable_to_non_nullable
as List<PostDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [PostListResponse].
extension PostListResponsePatterns on PostListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PostListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PostListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PostListResponse value)  $default,){
final _that = this;
switch (_that) {
case _PostListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PostListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _PostListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<PostDto> posts,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PostListResponse() when $default != null:
return $default(_that.posts,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<PostDto> posts,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _PostListResponse():
return $default(_that.posts,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<PostDto> posts,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _PostListResponse() when $default != null:
return $default(_that.posts,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PostListResponse implements PostListResponse {
  const _PostListResponse({required  List<PostDto> posts, required this.total, required this.page, required this.size}): _posts = posts;
  factory _PostListResponse.fromJson(Map<String, dynamic> json) => _$PostListResponseFromJson(json);

/// List of posts
 final  List<PostDto> _posts;
/// List of posts
@override List<PostDto> get posts {
  if (_posts is EqualUnmodifiableListView) return _posts;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_posts);
}

/// Total number of posts
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of PostListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PostListResponseCopyWith<_PostListResponse> get copyWith => __$PostListResponseCopyWithImpl<_PostListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PostListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PostListResponse&&const DeepCollectionEquality().equals(other._posts, _posts)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_posts),total,page,size);

@override
String toString() {
  return 'PostListResponse(posts: $posts, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$PostListResponseCopyWith<$Res> implements $PostListResponseCopyWith<$Res> {
  factory _$PostListResponseCopyWith(_PostListResponse value, $Res Function(_PostListResponse) _then) = __$PostListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<PostDto> posts, int total, int page, int size
});




}
/// @nodoc
class __$PostListResponseCopyWithImpl<$Res>
    implements _$PostListResponseCopyWith<$Res> {
  __$PostListResponseCopyWithImpl(this._self, this._then);

  final _PostListResponse _self;
  final $Res Function(_PostListResponse) _then;

/// Create a copy of PostListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? posts = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_PostListResponse(
posts: null == posts ? _self._posts : posts // ignore: cast_nullable_to_non_nullable
as List<PostDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
