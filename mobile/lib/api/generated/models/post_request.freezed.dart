// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'post_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PostRequest {

/// Post name
 String get name;/// Post description
 MediaDto get media;/// Post date/time
 String get dateTime;/// Post status
 String get status;/// Visibility level
 String get visibility;/// Publication timestamp (for scheduled publishing)
 String? get publishAt;
/// Create a copy of PostRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PostRequestCopyWith<PostRequest> get copyWith => _$PostRequestCopyWithImpl<PostRequest>(this as PostRequest, _$identity);

  /// Serializes this PostRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as PostRequest;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PostRequest&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.media, _this.media) || other.media == _this.media)&&(identical(other.dateTime, _this.dateTime) || other.dateTime == _this.dateTime)&&(identical(other.status, _this.status) || other.status == _this.status)&&(identical(other.visibility, _this.visibility) || other.visibility == _this.visibility)&&(identical(other.publishAt, _this.publishAt) || other.publishAt == _this.publishAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as PostRequest;
  return Object.hash(runtimeType,_this.name,_this.media,_this.dateTime,_this.status,_this.visibility,_this.publishAt);
}

@override
String toString() {
  final _this = this as PostRequest;
  return 'PostRequest(name: ${_this.name}, media: ${_this.media}, dateTime: ${_this.dateTime}, status: ${_this.status}, visibility: ${_this.visibility}, publishAt: ${_this.publishAt})';
}


}

/// @nodoc
abstract mixin class $PostRequestCopyWith<$Res>  {
  factory $PostRequestCopyWith(PostRequest value, $Res Function(PostRequest) _then) = _$PostRequestCopyWithImpl;
@useResult
$Res call({
 String name, MediaDto media, String dateTime, String status, String visibility, String? publishAt
});


$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$PostRequestCopyWithImpl<$Res>
    implements $PostRequestCopyWith<$Res> {
  _$PostRequestCopyWithImpl(this._self, this._then);

  final PostRequest _self;
  final $Res Function(PostRequest) _then;

/// Create a copy of PostRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? publishAt = freezed,}) {
  return _then(PostRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of PostRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [PostRequest].
extension PostRequestPatterns on PostRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PostRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PostRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PostRequest value)  $default,){
final _that = this;
switch (_that) {
case _PostRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PostRequest value)?  $default,){
final _that = this;
switch (_that) {
case _PostRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  String? publishAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PostRequest() when $default != null:
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.publishAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  String? publishAt)  $default,) {final _that = this;
switch (_that) {
case _PostRequest():
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.publishAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  MediaDto media,  String dateTime,  String status,  String visibility,  String? publishAt)?  $default,) {final _that = this;
switch (_that) {
case _PostRequest() when $default != null:
return $default(_that.name,_that.media,_that.dateTime,_that.status,_that.visibility,_that.publishAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PostRequest implements PostRequest {
  const _PostRequest({required this.name, required this.media, required this.dateTime, required this.status, required this.visibility, this.publishAt});
  factory _PostRequest.fromJson(Map<String, dynamic> json) => _$PostRequestFromJson(json);

/// Post name
@override final  String name;
/// Post description
@override final  MediaDto media;
/// Post date/time
@override final  String dateTime;
/// Post status
@override final  String status;
/// Visibility level
@override final  String visibility;
/// Publication timestamp (for scheduled publishing)
@override final  String? publishAt;

/// Create a copy of PostRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PostRequestCopyWith<_PostRequest> get copyWith => __$PostRequestCopyWithImpl<_PostRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PostRequestToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _PostRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.dateTime, dateTime) || other.dateTime == dateTime)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.publishAt, publishAt) || other.publishAt == publishAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,name,media,dateTime,status,visibility,publishAt);
}

@override
String toString() {
    return 'PostRequest(name: $name, media: $media, dateTime: $dateTime, status: $status, visibility: $visibility, publishAt: $publishAt)';
}


}

/// @nodoc
abstract mixin class _$PostRequestCopyWith<$Res> implements $PostRequestCopyWith<$Res> {
  factory _$PostRequestCopyWith(_PostRequest value, $Res Function(_PostRequest) _then) = __$PostRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, MediaDto media, String dateTime, String status, String visibility, String? publishAt
});


@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class __$PostRequestCopyWithImpl<$Res>
    implements _$PostRequestCopyWith<$Res> {
  __$PostRequestCopyWithImpl(this._self, this._then);

  final _PostRequest _self;
  final $Res Function(_PostRequest) _then;

/// Create a copy of PostRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? media = null,Object? dateTime = null,Object? status = null,Object? visibility = null,Object? publishAt = freezed,}) {
  return _then(_PostRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,dateTime: null == dateTime ? _self.dateTime : dateTime // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,publishAt: freezed == publishAt ? _self.publishAt : publishAt // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of PostRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}

// dart format on
