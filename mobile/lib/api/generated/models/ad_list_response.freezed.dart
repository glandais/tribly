// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ad_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdListResponse {

/// List of ads
 List<AdDto> get ads;/// Total number of ads
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of AdListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdListResponseCopyWith<AdListResponse> get copyWith => _$AdListResponseCopyWithImpl<AdListResponse>(this as AdListResponse, _$identity);

  /// Serializes this AdListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdListResponse&&const DeepCollectionEquality().equals(other.ads, ads)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(ads),total,page,size);

@override
String toString() {
  return 'AdListResponse(ads: $ads, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $AdListResponseCopyWith<$Res>  {
  factory $AdListResponseCopyWith(AdListResponse value, $Res Function(AdListResponse) _then) = _$AdListResponseCopyWithImpl;
@useResult
$Res call({
 List<AdDto> ads, int total, int page, int size
});




}
/// @nodoc
class _$AdListResponseCopyWithImpl<$Res>
    implements $AdListResponseCopyWith<$Res> {
  _$AdListResponseCopyWithImpl(this._self, this._then);

  final AdListResponse _self;
  final $Res Function(AdListResponse) _then;

/// Create a copy of AdListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? ads = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(AdListResponse(
ads: null == ads ? _self.ads : ads // ignore: cast_nullable_to_non_nullable
as List<AdDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [AdListResponse].
extension AdListResponsePatterns on AdListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdListResponse value)  $default,){
final _that = this;
switch (_that) {
case _AdListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _AdListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<AdDto> ads,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdListResponse() when $default != null:
return $default(_that.ads,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<AdDto> ads,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _AdListResponse():
return $default(_that.ads,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<AdDto> ads,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _AdListResponse() when $default != null:
return $default(_that.ads,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdListResponse implements AdListResponse {
  const _AdListResponse({required  List<AdDto> ads, required this.total, required this.page, required this.size}): _ads = ads;
  factory _AdListResponse.fromJson(Map<String, dynamic> json) => _$AdListResponseFromJson(json);

/// List of ads
 final  List<AdDto> _ads;
/// List of ads
@override List<AdDto> get ads {
  if (_ads is EqualUnmodifiableListView) return _ads;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_ads);
}

/// Total number of ads
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of AdListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdListResponseCopyWith<_AdListResponse> get copyWith => __$AdListResponseCopyWithImpl<_AdListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdListResponse&&const DeepCollectionEquality().equals(other._ads, _ads)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_ads),total,page,size);

@override
String toString() {
  return 'AdListResponse(ads: $ads, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$AdListResponseCopyWith<$Res> implements $AdListResponseCopyWith<$Res> {
  factory _$AdListResponseCopyWith(_AdListResponse value, $Res Function(_AdListResponse) _then) = __$AdListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<AdDto> ads, int total, int page, int size
});




}
/// @nodoc
class __$AdListResponseCopyWithImpl<$Res>
    implements _$AdListResponseCopyWith<$Res> {
  __$AdListResponseCopyWithImpl(this._self, this._then);

  final _AdListResponse _self;
  final $Res Function(_AdListResponse) _then;

/// Create a copy of AdListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? ads = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_AdListResponse(
ads: null == ads ? _self._ads : ads // ignore: cast_nullable_to_non_nullable
as List<AdDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
