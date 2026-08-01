// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'beta_signup_list_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BetaSignupListResponse {

/// List of sign-ups
 List<BetaSignupDto> get signups;/// Total number of sign-ups
 int get total;/// Current page number
 int get page;/// Page size
 int get size;
/// Create a copy of BetaSignupListResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BetaSignupListResponseCopyWith<BetaSignupListResponse> get copyWith => _$BetaSignupListResponseCopyWithImpl<BetaSignupListResponse>(this as BetaSignupListResponse, _$identity);

  /// Serializes this BetaSignupListResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BetaSignupListResponse&&const DeepCollectionEquality().equals(other.signups, signups)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(signups),total,page,size);

@override
String toString() {
  return 'BetaSignupListResponse(signups: $signups, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class $BetaSignupListResponseCopyWith<$Res>  {
  factory $BetaSignupListResponseCopyWith(BetaSignupListResponse value, $Res Function(BetaSignupListResponse) _then) = _$BetaSignupListResponseCopyWithImpl;
@useResult
$Res call({
 List<BetaSignupDto> signups, int total, int page, int size
});




}
/// @nodoc
class _$BetaSignupListResponseCopyWithImpl<$Res>
    implements $BetaSignupListResponseCopyWith<$Res> {
  _$BetaSignupListResponseCopyWithImpl(this._self, this._then);

  final BetaSignupListResponse _self;
  final $Res Function(BetaSignupListResponse) _then;

/// Create a copy of BetaSignupListResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? signups = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_self.copyWith(
signups: null == signups ? _self.signups : signups // ignore: cast_nullable_to_non_nullable
as List<BetaSignupDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [BetaSignupListResponse].
extension BetaSignupListResponsePatterns on BetaSignupListResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BetaSignupListResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BetaSignupListResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BetaSignupListResponse value)  $default,){
final _that = this;
switch (_that) {
case _BetaSignupListResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BetaSignupListResponse value)?  $default,){
final _that = this;
switch (_that) {
case _BetaSignupListResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<BetaSignupDto> signups,  int total,  int page,  int size)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BetaSignupListResponse() when $default != null:
return $default(_that.signups,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<BetaSignupDto> signups,  int total,  int page,  int size)  $default,) {final _that = this;
switch (_that) {
case _BetaSignupListResponse():
return $default(_that.signups,_that.total,_that.page,_that.size);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<BetaSignupDto> signups,  int total,  int page,  int size)?  $default,) {final _that = this;
switch (_that) {
case _BetaSignupListResponse() when $default != null:
return $default(_that.signups,_that.total,_that.page,_that.size);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BetaSignupListResponse implements BetaSignupListResponse {
  const _BetaSignupListResponse({required final  List<BetaSignupDto> signups, required this.total, required this.page, required this.size}): _signups = signups;
  factory _BetaSignupListResponse.fromJson(Map<String, dynamic> json) => _$BetaSignupListResponseFromJson(json);

/// List of sign-ups
 final  List<BetaSignupDto> _signups;
/// List of sign-ups
@override List<BetaSignupDto> get signups {
  if (_signups is EqualUnmodifiableListView) return _signups;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_signups);
}

/// Total number of sign-ups
@override final  int total;
/// Current page number
@override final  int page;
/// Page size
@override final  int size;

/// Create a copy of BetaSignupListResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BetaSignupListResponseCopyWith<_BetaSignupListResponse> get copyWith => __$BetaSignupListResponseCopyWithImpl<_BetaSignupListResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BetaSignupListResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _BetaSignupListResponse&&const DeepCollectionEquality().equals(other._signups, _signups)&&(identical(other.total, total) || other.total == total)&&(identical(other.page, page) || other.page == page)&&(identical(other.size, size) || other.size == size));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_signups),total,page,size);

@override
String toString() {
  return 'BetaSignupListResponse(signups: $signups, total: $total, page: $page, size: $size)';
}


}

/// @nodoc
abstract mixin class _$BetaSignupListResponseCopyWith<$Res> implements $BetaSignupListResponseCopyWith<$Res> {
  factory _$BetaSignupListResponseCopyWith(_BetaSignupListResponse value, $Res Function(_BetaSignupListResponse) _then) = __$BetaSignupListResponseCopyWithImpl;
@override @useResult
$Res call({
 List<BetaSignupDto> signups, int total, int page, int size
});




}
/// @nodoc
class __$BetaSignupListResponseCopyWithImpl<$Res>
    implements _$BetaSignupListResponseCopyWith<$Res> {
  __$BetaSignupListResponseCopyWithImpl(this._self, this._then);

  final _BetaSignupListResponse _self;
  final $Res Function(_BetaSignupListResponse) _then;

/// Create a copy of BetaSignupListResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? signups = null,Object? total = null,Object? page = null,Object? size = null,}) {
  return _then(_BetaSignupListResponse(
signups: null == signups ? _self._signups : signups // ignore: cast_nullable_to_non_nullable
as List<BetaSignupDto>,total: null == total ? _self.total : total // ignore: cast_nullable_to_non_nullable
as int,page: null == page ? _self.page : page // ignore: cast_nullable_to_non_nullable
as int,size: null == size ? _self.size : size // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
