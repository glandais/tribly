// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'trip_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$TripListResponse {

/// List of trips
 List<TripDto> get trips;/// Total number of trips
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of TripListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$TripListResponseCopyWith<TripListResponse> get copyWith => _$TripListResponseCopyWithImpl<TripListResponse>(this as TripListResponse, _$identity);

  /// Serializes this TripListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is TripListResponse&&const DeepCollectionEquality().equals(other.trips, trips)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(trips),total,page,size);

@override
String toString() {
  return 'TripListResponse(trips: $trips, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $TripListResponseCopyWith<$Res>  {
  factory $TripListResponseCopyWith(TripListResponse value, $Res Function(TripListResponse) _then) = _$TripListResponseCopyWithImpl;
@useResult
$Res call({
 List<TripDto> trips, int total, int page, int size
});




}
/// @nodoc
class _$TripListResponseCopyWithImpl<$Res>
    implements $TripListResponseCopyWith<$Res> {
  _$TripListResponseCopyWithImpl(this._self, this._then);

  final TripListResponse _self;
  final $Res Function(TripListResponse) _then;

/// Create a copy of TripListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? trips = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_self.copyWith(
trips: null == trips ? _self.trips : trips // ignore: cast_nullable_to_non_nullable
as List<TripDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [TripListResponse].
extension TripListResponsePatterns on TripListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _TripListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _TripListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _TripListResponse value)  $default,){
final _that = this;
switch (_that) {
case _TripListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _TripListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _TripListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<TripDto> trips,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _TripListResponse() when $default != null:
return $default(_that.trips,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<TripDto> trips,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _TripListResponse():
return $default(_that.trips,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<TripDto> trips,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _TripListResponse() when $default != null:
return $default(_that.trips,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _TripListResponse implements TripListResponse {
  const _TripListResponse({required final  List<TripDto> trips, required this.total, required this.page, required this.size}): _trips = trips;
  factory _TripListResponse.fromJson(Map<String, dynamic> json) => _$TripListResponseFromJson(json);

/// List of trips
 final  List<TripDto> _trips;
/// List of trips
@override List<TripDto> get trips {
  if (_trips is EqualUnmodifiableListView) return _trips;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_trips);
}

/// Total number of trips
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of TripListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$TripListResponseCopyWith<_TripListResponse> get copyWith => __$TripListResponseCopyWithImpl<_TripListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$TripListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _TripListResponse&&const DeepCollectionEquality().equals(other._trips, _trips)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_trips),total,page,size);

@override
String toString() {
  return 'TripListResponse(trips: $trips, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$TripListResponseCopyWith<$Res> implements $TripListResponseCopyWith<$Res> {
  factory _$TripListResponseCopyWith(_TripListResponse value, $Res Function(_TripListResponse) _then) = __$TripListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<TripDto> trips, int total, int page, int size
});




}
/// @nodoc
class __$TripListResponseCopyWithImpl<$Res>
    implements _$TripListResponseCopyWith<$Res> {
  __$TripListResponseCopyWithImpl(this._self, this._then);

  final _TripListResponse _self;
  final $Res Function(_TripListResponse) _then;

/// Create a copy of TripListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? trips = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_TripListResponse(
trips: null == trips ? _self._trips : trips // ignore: cast_nullable_to_non_nullable
as List<TripDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
