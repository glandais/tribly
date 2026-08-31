// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ad_request_location_geometry.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdRequestLocationGeometry {

 AdRequestLocationGeometryTypeType get type;/// Coordinates [longitude, latitude]
 List<double> get coordinates;
/// Create a copy of AdRequestLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdRequestLocationGeometryCopyWith<AdRequestLocationGeometry> get copyWith => _$AdRequestLocationGeometryCopyWithImpl<AdRequestLocationGeometry>(this as AdRequestLocationGeometry, _$identity);

  /// Serializes this AdRequestLocationGeometry to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as AdRequestLocationGeometry;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdRequestLocationGeometry&&(identical(other.type, _this.type) || other.type == _this.type)&&const DeepCollectionEquality().equals(other.coordinates, _this.coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as AdRequestLocationGeometry;
  return Object.hash(runtimeType,_this.type,const DeepCollectionEquality().hash(_this.coordinates));
}

@override
String toString() {
  final _this = this as AdRequestLocationGeometry;
  return 'AdRequestLocationGeometry(type: ${_this.type}, coordinates: ${_this.coordinates})';
}


}

/// @nodoc
abstract mixin class $AdRequestLocationGeometryCopyWith<$Res>  {
  factory $AdRequestLocationGeometryCopyWith(AdRequestLocationGeometry value, $Res Function(AdRequestLocationGeometry) _then) = _$AdRequestLocationGeometryCopyWithImpl;
@useResult
$Res call({
 AdRequestLocationGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class _$AdRequestLocationGeometryCopyWithImpl<$Res>
    implements $AdRequestLocationGeometryCopyWith<$Res> {
  _$AdRequestLocationGeometryCopyWithImpl(this._self, this._then);

  final AdRequestLocationGeometry _self;
  final $Res Function(AdRequestLocationGeometry) _then;

/// Create a copy of AdRequestLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(AdRequestLocationGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as AdRequestLocationGeometryTypeType,coordinates: null == coordinates ? _self.coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}

}


/// Adds pattern-matching-related methods to [AdRequestLocationGeometry].
extension AdRequestLocationGeometryPatterns on AdRequestLocationGeometry {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdRequestLocationGeometry value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdRequestLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdRequestLocationGeometry value)  $default,){
final _that = this;
switch (_that) {
case _AdRequestLocationGeometry():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdRequestLocationGeometry value)?  $default,){
final _that = this;
switch (_that) {
case _AdRequestLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( AdRequestLocationGeometryTypeType type,  List<double> coordinates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdRequestLocationGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( AdRequestLocationGeometryTypeType type,  List<double> coordinates)  $default,) {final _that = this;
switch (_that) {
case _AdRequestLocationGeometry():
return $default(_that.type,_that.coordinates);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( AdRequestLocationGeometryTypeType type,  List<double> coordinates)?  $default,) {final _that = this;
switch (_that) {
case _AdRequestLocationGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdRequestLocationGeometry implements AdRequestLocationGeometry {
  const _AdRequestLocationGeometry({required this.type, required  List<double> coordinates}): _coordinates = coordinates;
  factory _AdRequestLocationGeometry.fromJson(Map<String, dynamic> json) => _$AdRequestLocationGeometryFromJson(json);

@override final  AdRequestLocationGeometryTypeType type;
/// Coordinates [longitude, latitude]
 final  List<double> _coordinates;
/// Coordinates [longitude, latitude]
@override List<double> get coordinates {
  if (_coordinates is EqualUnmodifiableListView) return _coordinates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_coordinates);
}


/// Create a copy of AdRequestLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdRequestLocationGeometryCopyWith<_AdRequestLocationGeometry> get copyWith => __$AdRequestLocationGeometryCopyWithImpl<_AdRequestLocationGeometry>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdRequestLocationGeometryToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdRequestLocationGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.coordinates, _coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_coordinates));
}

@override
String toString() {
    return 'AdRequestLocationGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class _$AdRequestLocationGeometryCopyWith<$Res> implements $AdRequestLocationGeometryCopyWith<$Res> {
  factory _$AdRequestLocationGeometryCopyWith(_AdRequestLocationGeometry value, $Res Function(_AdRequestLocationGeometry) _then) = __$AdRequestLocationGeometryCopyWithImpl;
@override @useResult
$Res call({
 AdRequestLocationGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class __$AdRequestLocationGeometryCopyWithImpl<$Res>
    implements _$AdRequestLocationGeometryCopyWith<$Res> {
  __$AdRequestLocationGeometryCopyWithImpl(this._self, this._then);

  final _AdRequestLocationGeometry _self;
  final $Res Function(_AdRequestLocationGeometry) _then;

/// Create a copy of AdRequestLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_AdRequestLocationGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as AdRequestLocationGeometryTypeType,coordinates: null == coordinates ? _self._coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}


}

// dart format on
