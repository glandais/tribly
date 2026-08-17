// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'place_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PlaceListResponse {

/// List of places
 List<PlaceDetailDto> get places;/// Total number of places
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of PlaceListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PlaceListResponseCopyWith<PlaceListResponse> get copyWith => _$PlaceListResponseCopyWithImpl<PlaceListResponse>(this as PlaceListResponse, _$identity);

  /// Serializes this PlaceListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PlaceListResponse&&const DeepCollectionEquality().equals(other.places, places)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(places),total,page,size);

@override
String toString() {
  return 'PlaceListResponse(places: $places, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $PlaceListResponseCopyWith<$Res>  {
  factory $PlaceListResponseCopyWith(PlaceListResponse value, $Res Function(PlaceListResponse) _then) = _$PlaceListResponseCopyWithImpl;
@useResult
$Res call({
 List<PlaceDetailDto> places, int total, int page, int size
});




}
/// @nodoc
class _$PlaceListResponseCopyWithImpl<$Res>
    implements $PlaceListResponseCopyWith<$Res> {
  _$PlaceListResponseCopyWithImpl(this._self, this._then);

  final PlaceListResponse _self;
  final $Res Function(PlaceListResponse) _then;

/// Create a copy of PlaceListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? places = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(PlaceListResponse(
places: null == places ? _self.places : places // ignore: cast_nullable_to_non_nullable
as List<PlaceDetailDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [PlaceListResponse].
extension PlaceListResponsePatterns on PlaceListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PlaceListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PlaceListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PlaceListResponse value)  $default,){
final _that = this;
switch (_that) {
case _PlaceListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PlaceListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _PlaceListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<PlaceDetailDto> places,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PlaceListResponse() when $default != null:
return $default(_that.places,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<PlaceDetailDto> places,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _PlaceListResponse():
return $default(_that.places,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<PlaceDetailDto> places,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _PlaceListResponse() when $default != null:
return $default(_that.places,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PlaceListResponse implements PlaceListResponse {
  const _PlaceListResponse({required  List<PlaceDetailDto> places, required this.total, required this.page, required this.size}): _places = places;
  factory _PlaceListResponse.fromJson(Map<String, dynamic> json) => _$PlaceListResponseFromJson(json);

/// List of places
 final  List<PlaceDetailDto> _places;
/// List of places
@override List<PlaceDetailDto> get places {
  if (_places is EqualUnmodifiableListView) return _places;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_places);
}

/// Total number of places
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of PlaceListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PlaceListResponseCopyWith<_PlaceListResponse> get copyWith => __$PlaceListResponseCopyWithImpl<_PlaceListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PlaceListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PlaceListResponse&&const DeepCollectionEquality().equals(other._places, _places)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_places),total,page,size);

@override
String toString() {
  return 'PlaceListResponse(places: $places, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$PlaceListResponseCopyWith<$Res> implements $PlaceListResponseCopyWith<$Res> {
  factory _$PlaceListResponseCopyWith(_PlaceListResponse value, $Res Function(_PlaceListResponse) _then) = __$PlaceListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<PlaceDetailDto> places, int total, int page, int size
});




}
/// @nodoc
class __$PlaceListResponseCopyWithImpl<$Res>
    implements _$PlaceListResponseCopyWith<$Res> {
  __$PlaceListResponseCopyWithImpl(this._self, this._then);

  final _PlaceListResponse _self;
  final $Res Function(_PlaceListResponse) _then;

/// Create a copy of PlaceListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? places = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_PlaceListResponse(
places: null == places ? _self._places : places // ignore: cast_nullable_to_non_nullable
as List<PlaceDetailDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
