// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'route_usages_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$RouteUsagesResponse {

/// Usages of the route, most recent first
 List<RouteUsageDto> get usages;
/// Create a copy of RouteUsagesResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$RouteUsagesResponseCopyWith<RouteUsagesResponse> get copyWith => _$RouteUsagesResponseCopyWithImpl<RouteUsagesResponse>(this as RouteUsagesResponse, _$identity);

  /// Serializes this RouteUsagesResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as RouteUsagesResponse;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is RouteUsagesResponse&&const DeepCollectionEquality().equals(other.usages, _this.usages));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as RouteUsagesResponse;
  return Object.hash(runtimeType,const DeepCollectionEquality().hash(_this.usages));
}

@override
String toString() {
  final _this = this as RouteUsagesResponse;
  return 'RouteUsagesResponse(usages: ${_this.usages})';
}


}

/// @nodoc
abstract mixin class $RouteUsagesResponseCopyWith<$Res>  {
  factory $RouteUsagesResponseCopyWith(RouteUsagesResponse value, $Res Function(RouteUsagesResponse) _then) = _$RouteUsagesResponseCopyWithImpl;
@useResult
$Res call({
 List<RouteUsageDto> usages
});




}
/// @nodoc
class _$RouteUsagesResponseCopyWithImpl<$Res>
    implements $RouteUsagesResponseCopyWith<$Res> {
  _$RouteUsagesResponseCopyWithImpl(this._self, this._then);

  final RouteUsagesResponse _self;
  final $Res Function(RouteUsagesResponse) _then;

/// Create a copy of RouteUsagesResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? usages = null,}) {
  return _then(RouteUsagesResponse(
usages: null == usages ? _self.usages : usages // ignore: cast_nullable_to_non_nullable
as List<RouteUsageDto>,
  ));
}

}


/// Adds pattern-matching-related methods to [RouteUsagesResponse].
extension RouteUsagesResponsePatterns on RouteUsagesResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _RouteUsagesResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _RouteUsagesResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _RouteUsagesResponse value)  $default,){
final _that = this;
switch (_that) {
case _RouteUsagesResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _RouteUsagesResponse value)?  $default,){
final _that = this;
switch (_that) {
case _RouteUsagesResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<RouteUsageDto> usages)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _RouteUsagesResponse() when $default != null:
return $default(_that.usages);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<RouteUsageDto> usages)  $default,) {final _that = this;
switch (_that) {
case _RouteUsagesResponse():
return $default(_that.usages);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<RouteUsageDto> usages)?  $default,) {final _that = this;
switch (_that) {
case _RouteUsagesResponse() when $default != null:
return $default(_that.usages);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _RouteUsagesResponse implements RouteUsagesResponse {
  const _RouteUsagesResponse({required  List<RouteUsageDto> usages}): _usages = usages;
  factory _RouteUsagesResponse.fromJson(Map<String, dynamic> json) => _$RouteUsagesResponseFromJson(json);

/// Usages of the route, most recent first
 final  List<RouteUsageDto> _usages;
/// Usages of the route, most recent first
@override List<RouteUsageDto> get usages {
  if (_usages is EqualUnmodifiableListView) return _usages;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_usages);
}


/// Create a copy of RouteUsagesResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$RouteUsagesResponseCopyWith<_RouteUsagesResponse> get copyWith => __$RouteUsagesResponseCopyWithImpl<_RouteUsagesResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$RouteUsagesResponseToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _RouteUsagesResponse&&const DeepCollectionEquality().equals(other.usages, _usages));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,const DeepCollectionEquality().hash(_usages));
}

@override
String toString() {
    return 'RouteUsagesResponse(usages: $usages)';
}


}

/// @nodoc
abstract mixin class _$RouteUsagesResponseCopyWith<$Res> implements $RouteUsagesResponseCopyWith<$Res> {
  factory _$RouteUsagesResponseCopyWith(_RouteUsagesResponse value, $Res Function(_RouteUsagesResponse) _then) = __$RouteUsagesResponseCopyWithImpl;
@override @useResult
$Res call({
 List<RouteUsageDto> usages
});




}
/// @nodoc
class __$RouteUsagesResponseCopyWithImpl<$Res>
    implements _$RouteUsagesResponseCopyWith<$Res> {
  __$RouteUsagesResponseCopyWithImpl(this._self, this._then);

  final _RouteUsagesResponse _self;
  final $Res Function(_RouteUsagesResponse) _then;

/// Create a copy of RouteUsagesResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? usages = null,}) {
  return _then(_RouteUsagesResponse(
usages: null == usages ? _self._usages : usages // ignore: cast_nullable_to_non_nullable
as List<RouteUsageDto>,
  ));
}


}

// dart format on
