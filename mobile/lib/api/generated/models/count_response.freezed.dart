// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'count_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$CountResponse {

/// Total number of matching items
 int get total;
/// Create a copy of CountResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$CountResponseCopyWith<CountResponse> get copyWith => _$CountResponseCopyWithImpl<CountResponse>(this as CountResponse, _$identity);

  /// Serializes this CountResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as CountResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is CountResponse&&(identical(other.total, _this.total) || other.total == _this.total));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as CountResponse;
  return Object.hash(runtimeType,_this.total);
}

@override
String toString() {
  final _this = this as CountResponse;
  return 'CountResponse(total: ${_this.total})';
}


}

/// @nodoc
abstract mixin class $CountResponseCopyWith<$Res>  {
  factory $CountResponseCopyWith(CountResponse value, $Res Function(CountResponse) _then) = _$CountResponseCopyWithImpl;
@useResult
$Res call({
 int total
});




}
/// @nodoc
class _$CountResponseCopyWithImpl<$Res>
    implements $CountResponseCopyWith<$Res> {
  _$CountResponseCopyWithImpl(this._self, this._then);

  final CountResponse _self;
  final $Res Function(CountResponse) _then;

/// Create a copy of CountResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? total = null,}) {
  return _then(CountResponse(
total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [CountResponse].
extension CountResponsePatterns on CountResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _CountResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _CountResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _CountResponse value)  $default,){
final _that = this;
switch (_that) {
case _CountResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _CountResponse value)?  $default,){
final _that = this;
switch (_that) {
case _CountResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int total)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _CountResponse() when $default != null:
return $default(_that.total);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int total)  $default,) {final _that = this;
switch (_that) {
case _CountResponse():
return $default(_that.total);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int total)?  $default,) {final _that = this;
switch (_that) {
case _CountResponse() when $default != null:
return $default(_that.total);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _CountResponse implements CountResponse {
  const _CountResponse({required this.total});
  factory _CountResponse.fromJson(Map<String, dynamic> json) => _$CountResponseFromJson(json);

/// Total number of matching items
@override final  int total;

/// Create a copy of CountResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$CountResponseCopyWith<_CountResponse> get copyWith => __$CountResponseCopyWithImpl<_CountResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$CountResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _CountResponse&&(identical(other.total, total) || other.total == total));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,total);
}

@override
String toString() {
    return 'CountResponse(total: $total)';
}


}

/// @nodoc
abstract mixin class _$CountResponseCopyWith<$Res> implements $CountResponseCopyWith<$Res> {
  factory _$CountResponseCopyWith(_CountResponse value, $Res Function(_CountResponse) _then) = __$CountResponseCopyWithImpl;
@override @useResult
$Res call({
 int total
});




}
/// @nodoc
class __$CountResponseCopyWithImpl<$Res>
    implements _$CountResponseCopyWith<$Res> {
  __$CountResponseCopyWithImpl(this._self, this._then);

  final _CountResponse _self;
  final $Res Function(_CountResponse) _then;

/// Create a copy of CountResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? total = null,}) {
  return _then(_CountResponse(
total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
