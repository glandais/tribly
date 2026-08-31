// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ad_dto_location_geometry.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdDtoLocationGeometry {

 AdDtoLocationGeometryTypeType get type;/// Coordinates [longitude, latitude]
 List<double> get coordinates;
/// Create a copy of AdDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdDtoLocationGeometryCopyWith<AdDtoLocationGeometry> get copyWith => _$AdDtoLocationGeometryCopyWithImpl<AdDtoLocationGeometry>(this as AdDtoLocationGeometry, _$identity);

  /// Serializes this AdDtoLocationGeometry to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as AdDtoLocationGeometry;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdDtoLocationGeometry&&(identical(other.type, _this.type) || other.type == _this.type)&&const DeepCollectionEquality().equals(other.coordinates, _this.coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as AdDtoLocationGeometry;
  return Object.hash(runtimeType,_this.type,const DeepCollectionEquality().hash(_this.coordinates));
}

@override
String toString() {
  final _this = this as AdDtoLocationGeometry;
  return 'AdDtoLocationGeometry(type: ${_this.type}, coordinates: ${_this.coordinates})';
}


}

/// @nodoc
abstract mixin class $AdDtoLocationGeometryCopyWith<$Res>  {
  factory $AdDtoLocationGeometryCopyWith(AdDtoLocationGeometry value, $Res Function(AdDtoLocationGeometry) _then) = _$AdDtoLocationGeometryCopyWithImpl;
@useResult
$Res call({
 AdDtoLocationGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class _$AdDtoLocationGeometryCopyWithImpl<$Res>
    implements $AdDtoLocationGeometryCopyWith<$Res> {
  _$AdDtoLocationGeometryCopyWithImpl(this._self, this._then);

  final AdDtoLocationGeometry _self;
  final $Res Function(AdDtoLocationGeometry) _then;

/// Create a copy of AdDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(AdDtoLocationGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as AdDtoLocationGeometryTypeType,coordinates: null == coordinates ? _self.coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}

}


/// Adds pattern-matching-related methods to [AdDtoLocationGeometry].
extension AdDtoLocationGeometryPatterns on AdDtoLocationGeometry {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdDtoLocationGeometry value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdDtoLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdDtoLocationGeometry value)  $default,){
final _that = this;
switch (_that) {
case _AdDtoLocationGeometry():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdDtoLocationGeometry value)?  $default,){
final _that = this;
switch (_that) {
case _AdDtoLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( AdDtoLocationGeometryTypeType type,  List<double> coordinates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdDtoLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( AdDtoLocationGeometryTypeType type,  List<double> coordinates)  $default,) {final _that = this;
switch (_that) {
case _AdDtoLocationGeometry():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( AdDtoLocationGeometryTypeType type,  List<double> coordinates)?  $default,) {final _that = this;
switch (_that) {
case _AdDtoLocationGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdDtoLocationGeometry implements AdDtoLocationGeometry {
  const _AdDtoLocationGeometry({required this.type, required  List<double> coordinates}): _coordinates = coordinates;
  factory _AdDtoLocationGeometry.fromJson(Map<String, dynamic> json) => _$AdDtoLocationGeometryFromJson(json);

@override final  AdDtoLocationGeometryTypeType type;
/// Coordinates [longitude, latitude]
 final  List<double> _coordinates;
/// Coordinates [longitude, latitude]
@override List<double> get coordinates {
  if (_coordinates is EqualUnmodifiableListView) return _coordinates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_coordinates);
}


/// Create a copy of AdDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdDtoLocationGeometryCopyWith<_AdDtoLocationGeometry> get copyWith => __$AdDtoLocationGeometryCopyWithImpl<_AdDtoLocationGeometry>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdDtoLocationGeometryToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdDtoLocationGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.coordinates, _coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_coordinates));
}

@override
String toString() {
    return 'AdDtoLocationGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class _$AdDtoLocationGeometryCopyWith<$Res> implements $AdDtoLocationGeometryCopyWith<$Res> {
  factory _$AdDtoLocationGeometryCopyWith(_AdDtoLocationGeometry value, $Res Function(_AdDtoLocationGeometry) _then) = __$AdDtoLocationGeometryCopyWithImpl;
@override @useResult
$Res call({
 AdDtoLocationGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class __$AdDtoLocationGeometryCopyWithImpl<$Res>
    implements _$AdDtoLocationGeometryCopyWith<$Res> {
  __$AdDtoLocationGeometryCopyWithImpl(this._self, this._then);

  final _AdDtoLocationGeometry _self;
  final $Res Function(_AdDtoLocationGeometry) _then;

/// Create a copy of AdDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_AdDtoLocationGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as AdDtoLocationGeometryTypeType,coordinates: null == coordinates ? _self._coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}


}

// dart format on
