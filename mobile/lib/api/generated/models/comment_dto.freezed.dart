// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'comment_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CommentDto {

/// Comment ID (TSID)
 String get id;/// Comment content
 String get content;/// Comment author
 PublicUserDto get author;/// Creation timestamp
 String get createdAt;/// Replies to this comment
 List<CommentDto> get replies;/// Parent comment ID (for replies)
 String? get parentId;
/// Create a copy of CommentDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CommentDtoCopyWith<CommentDto> get copyWith => _$CommentDtoCopyWithImpl<CommentDto>(this as CommentDto, _$identity);

  /// Serializes this CommentDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CommentDto&&(identical(other.id, id) || other.id == id)&&(identical(other.content, content) || other.content == content)&&(identical(other.author, author) || other.author == author)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&const DeepCollectionEquality().equals(other.replies, replies)&&(identical(other.parentId, parentId) || other.parentId == parentId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,content,author,createdAt,const DeepCollectionEquality().hash(replies),parentId);

@override
String toString() {
  return 'CommentDto(id: $id, content: $content, author: $author, createdAt: $createdAt, replies: $replies, parentId: $parentId)';
}


}

/// @nodoc
abstract mixin class $CommentDtoCopyWith<$Res>  {
  factory $CommentDtoCopyWith(CommentDto value, $Res Function(CommentDto) _then) = _$CommentDtoCopyWithImpl;
@useResult
$Res call({
 String id, String content, PublicUserDto author, String createdAt, List<CommentDto> replies, String? parentId
});


$PublicUserDtoCopyWith<$Res> get author;

}
/// @nodoc
class _$CommentDtoCopyWithImpl<$Res>
    implements $CommentDtoCopyWith<$Res> {
  _$CommentDtoCopyWithImpl(this._self, this._then);

  final CommentDto _self;
  final $Res Function(CommentDto) _then;

/// Create a copy of CommentDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? content = null,Object? author = null,Object? createdAt = null,Object? replies = null,Object? parentId = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,content: null == content ? _self.content : content // ignore: cast_nullable_to_non_nullable
as String,author: null == author ? _self.author : author // ignore: cast_nullable_to_non_nullable
as PublicUserDto,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,replies: null == replies ? _self.replies : replies // ignore: cast_nullable_to_non_nullable
as List<CommentDto>,parentId: freezed == parentId ? _self.parentId : parentId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of CommentDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get author {
  
  return $PublicUserDtoCopyWith<$Res>(_self.author, (value) {
    return _then(_self.copyWith(author: value));
  });
}
}


/// Adds pattern-matching-related methods to [CommentDto].
extension CommentDtoPatterns on CommentDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CommentDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CommentDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CommentDto value)  $default,){
final _that = this;
switch (_that) {
case _CommentDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CommentDto value)?  $default,){
final _that = this;
switch (_that) {
case _CommentDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String content,  PublicUserDto author,  String createdAt,  List<CommentDto> replies,  String? parentId)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CommentDto() when $default != null:
return $default(_that.id,_that.content,_that.author,_that.createdAt,_that.replies,_that.parentId);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String content,  PublicUserDto author,  String createdAt,  List<CommentDto> replies,  String? parentId)  $default,) {final _that = this;
switch (_that) {
case _CommentDto():
return $default(_that.id,_that.content,_that.author,_that.createdAt,_that.replies,_that.parentId);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String content,  PublicUserDto author,  String createdAt,  List<CommentDto> replies,  String? parentId)?  $default,) {final _that = this;
switch (_that) {
case _CommentDto() when $default != null:
return $default(_that.id,_that.content,_that.author,_that.createdAt,_that.replies,_that.parentId);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CommentDto implements CommentDto {
  const _CommentDto({required this.id, required this.content, required this.author, required this.createdAt, required final  List<CommentDto> replies, this.parentId}): _replies = replies;
  factory _CommentDto.fromJson(Map<String, dynamic> json) => _$CommentDtoFromJson(json);

/// Comment ID (TSID)
@override final  String id;
/// Comment content
@override final  String content;
/// Comment author
@override final  PublicUserDto author;
/// Creation timestamp
@override final  String createdAt;
/// Replies to this comment
 final  List<CommentDto> _replies;
/// Replies to this comment
@override List<CommentDto> get replies {
  if (_replies is EqualUnmodifiableListView) return _replies;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_replies);
}

/// Parent comment ID (for replies)
@override final  String? parentId;

/// Create a copy of CommentDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CommentDtoCopyWith<_CommentDto> get copyWith => __$CommentDtoCopyWithImpl<_CommentDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CommentDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _CommentDto&&(identical(other.id, id) || other.id == id)&&(identical(other.content, content) || other.content == content)&&(identical(other.author, author) || other.author == author)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&const DeepCollectionEquality().equals(other._replies, _replies)&&(identical(other.parentId, parentId) || other.parentId == parentId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,content,author,createdAt,const DeepCollectionEquality().hash(_replies),parentId);

@override
String toString() {
  return 'CommentDto(id: $id, content: $content, author: $author, createdAt: $createdAt, replies: $replies, parentId: $parentId)';
}


}

/// @nodoc
abstract mixin class _$CommentDtoCopyWith<$Res> implements $CommentDtoCopyWith<$Res> {
  factory _$CommentDtoCopyWith(_CommentDto value, $Res Function(_CommentDto) _then) = __$CommentDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String content, PublicUserDto author, String createdAt, List<CommentDto> replies, String? parentId
});


@override $PublicUserDtoCopyWith<$Res> get author;

}
/// @nodoc
class __$CommentDtoCopyWithImpl<$Res>
    implements _$CommentDtoCopyWith<$Res> {
  __$CommentDtoCopyWithImpl(this._self, this._then);

  final _CommentDto _self;
  final $Res Function(_CommentDto) _then;

/// Create a copy of CommentDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? content = null,Object? author = null,Object? createdAt = null,Object? replies = null,Object? parentId = freezed,}) {
  return _then(_CommentDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,content: null == content ? _self.content : content // ignore: cast_nullable_to_non_nullable
as String,author: null == author ? _self.author : author // ignore: cast_nullable_to_non_nullable
as PublicUserDto,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,replies: null == replies ? _self._replies : replies // ignore: cast_nullable_to_non_nullable
as List<CommentDto>,parentId: freezed == parentId ? _self.parentId : parentId // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of CommentDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get author {
  
  return $PublicUserDtoCopyWith<$Res>(_self.author, (value) {
    return _then(_self.copyWith(author: value));
  });
}
}

// dart format on
