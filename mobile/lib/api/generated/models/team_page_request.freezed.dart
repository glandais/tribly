// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'team_page_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TeamPageRequest {

/// Page title
 String get title;/// Page content
 MediaDto get media;/// Visibility level
 String get visibility;
/// Create a copy of TeamPageRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TeamPageRequestCopyWith<TeamPageRequest> get copyWith => _$TeamPageRequestCopyWithImpl<TeamPageRequest>(this as TeamPageRequest, _$identity);

  /// Serializes this TeamPageRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TeamPageRequest&&(identical(other.title, title) || other.title == title)&&(identical(other.media, media) || other.media == media)&&(identical(other.visibility, visibility) || other.visibility == visibility));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,title,media,visibility);

@override
String toString() {
  return 'TeamPageRequest(title: $title, media: $media, visibility: $visibility)';
}


}

/// @nodoc
abstract mixin class $TeamPageRequestCopyWith<$Res>  {
  factory $TeamPageRequestCopyWith(TeamPageRequest value, $Res Function(TeamPageRequest) _then) = _$TeamPageRequestCopyWithImpl;
@useResult
$Res call({
 String title, MediaDto media, String visibility
});


$MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class _$TeamPageRequestCopyWithImpl<$Res>
    implements $TeamPageRequestCopyWith<$Res> {
  _$TeamPageRequestCopyWithImpl(this._self, this._then);

  final TeamPageRequest _self;
  final $Res Function(TeamPageRequest) _then;

/// Create a copy of TeamPageRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? title = null,Object? media = null,Object? visibility = null,}) {
  return _then(TeamPageRequest(
title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,
  ));
}
/// Create a copy of TeamPageRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}
}


/// Adds pattern-matching-related methods to [TeamPageRequest].
extension TeamPageRequestPatterns on TeamPageRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TeamPageRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TeamPageRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TeamPageRequest value)  $default,){
final _that = this;
switch (_that) {
case _TeamPageRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TeamPageRequest value)?  $default,){
final _that = this;
switch (_that) {
case _TeamPageRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String title,  MediaDto media,  String visibility)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TeamPageRequest() when $default != null:
return $default(_that.title,_that.media,_that.visibility);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String title,  MediaDto media,  String visibility)  $default,) {final _that = this;
switch (_that) {
case _TeamPageRequest():
return $default(_that.title,_that.media,_that.visibility);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String title,  MediaDto media,  String visibility)?  $default,) {final _that = this;
switch (_that) {
case _TeamPageRequest() when $default != null:
return $default(_that.title,_that.media,_that.visibility);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TeamPageRequest implements TeamPageRequest {
  const _TeamPageRequest({required this.title, required this.media, required this.visibility});
  factory _TeamPageRequest.fromJson(Map<String, dynamic> json) => _$TeamPageRequestFromJson(json);

/// Page title
@override final  String title;
/// Page content
@override final  MediaDto media;
/// Visibility level
@override final  String visibility;

/// Create a copy of TeamPageRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TeamPageRequestCopyWith<_TeamPageRequest> get copyWith => __$TeamPageRequestCopyWithImpl<_TeamPageRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TeamPageRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TeamPageRequest&&(identical(other.title, title) || other.title == title)&&(identical(other.media, media) || other.media == media)&&(identical(other.visibility, visibility) || other.visibility == visibility));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,title,media,visibility);

@override
String toString() {
  return 'TeamPageRequest(title: $title, media: $media, visibility: $visibility)';
}


}

/// @nodoc
abstract mixin class _$TeamPageRequestCopyWith<$Res> implements $TeamPageRequestCopyWith<$Res> {
  factory _$TeamPageRequestCopyWith(_TeamPageRequest value, $Res Function(_TeamPageRequest) _then) = __$TeamPageRequestCopyWithImpl;
@override @useResult
$Res call({
 String title, MediaDto media, String visibility
});


@override $MediaDtoCopyWith<$Res> get media;

}
/// @nodoc
class __$TeamPageRequestCopyWithImpl<$Res>
    implements _$TeamPageRequestCopyWith<$Res> {
  __$TeamPageRequestCopyWithImpl(this._self, this._then);

  final _TeamPageRequest _self;
  final $Res Function(_TeamPageRequest) _then;

/// Create a copy of TeamPageRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? title = null,Object? media = null,Object? visibility = null,}) {
  return _then(_TeamPageRequest(
title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

/// Create a copy of TeamPageRequest
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
