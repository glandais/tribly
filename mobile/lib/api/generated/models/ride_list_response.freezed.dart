// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ride_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RideListResponse {

/// List of rides
 List<RideDto> get rides;/// Total number of rides
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of RideListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RideListResponseCopyWith<RideListResponse> get copyWith => _$RideListResponseCopyWithImpl<RideListResponse>(this as RideListResponse, _$identity);

  /// Serializes this RideListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as RideListResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RideListResponse&&const DeepCollectionEquality().equals(other.rides, _this.rides)&&(identical(other.total, _this.total) || other.total == _this.total)&&(identical(other.page, _this.page) || other.page == _this.page)&&(identical(other.size, _this.size) || other.size == _this.size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as RideListResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.rides),_this.total,_this.page,_this.size);
}

@override
String toString() {
  final _this = this as RideListResponse;
  return 'RideListResponse(rides: ${_this.rides}, total: ${_this.total}, page: ${_this.page}, size: ${_this.size})';
}


}

/// @nodoc
abstract mixin class $RideListResponseCopyWith<$Res>  {
  factory $RideListResponseCopyWith(RideListResponse value, $Res Function(RideListResponse) _then) = _$RideListResponseCopyWithImpl;
@useResult
$Res call({
 List<RideDto> rides, int total, int page, int size
});




}
/// @nodoc
class _$RideListResponseCopyWithImpl<$Res>
    implements $RideListResponseCopyWith<$Res> {
  _$RideListResponseCopyWithImpl(this._self, this._then);

  final RideListResponse _self;
  final $Res Function(RideListResponse) _then;

/// Create a copy of RideListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? rides = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(RideListResponse(
rides: null == rides ? _self.rides : rides // ignore: cast_nullable_to_non_nullable
as List<RideDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [RideListResponse].
extension RideListResponsePatterns on RideListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RideListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RideListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RideListResponse value)  $default,){
final _that = this;
switch (_that) {
case _RideListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RideListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _RideListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<RideDto> rides,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RideListResponse() when $default != null:
return $default(_that.rides,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<RideDto> rides,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _RideListResponse():
return $default(_that.rides,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<RideDto> rides,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _RideListResponse() when $default != null:
return $default(_that.rides,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RideListResponse implements RideListResponse {
  const _RideListResponse({required  List<RideDto> rides, required this.total, required this.page, required this.size}): _rides = rides;
  factory _RideListResponse.fromJson(Map<String, dynamic> json) => _$RideListResponseFromJson(json);

/// List of rides
 final  List<RideDto> _rides;
/// List of rides
@override List<RideDto> get rides {
  if (_rides is EqualUnmodifiableListView) return _rides;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_rides);
}

/// Total number of rides
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of RideListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RideListResponseCopyWith<_RideListResponse> get copyWith => __$RideListResponseCopyWithImpl<_RideListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RideListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _RideListResponse&&const DeepCollectionEquality().equals(other.rides, _rides)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_rides),total,page,size);
}

@override
String toString() {
    return 'RideListResponse(rides: $rides, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$RideListResponseCopyWith<$Res> implements $RideListResponseCopyWith<$Res> {
  factory _$RideListResponseCopyWith(_RideListResponse value, $Res Function(_RideListResponse) _then) = __$RideListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<RideDto> rides, int total, int page, int size
});




}
/// @nodoc
class __$RideListResponseCopyWithImpl<$Res>
    implements _$RideListResponseCopyWith<$Res> {
  __$RideListResponseCopyWithImpl(this._self, this._then);

  final _RideListResponse _self;
  final $Res Function(_RideListResponse) _then;

/// Create a copy of RideListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? rides = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_RideListResponse(
rides: null == rides ? _self._rides : rides // ignore: cast_nullable_to_non_nullable
as List<RideDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
