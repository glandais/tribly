// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ad_edit_dto_location_geometry.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdEditDtoLocationGeometry {

 AdEditDtoLocationGeometryTypeType get type;/// Coordinates [longitude, latitude]
 List<double> get coordinates;
/// Create a copy of AdEditDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdEditDtoLocationGeometryCopyWith<AdEditDtoLocationGeometry> get copyWith => _$AdEditDtoLocationGeometryCopyWithImpl<AdEditDtoLocationGeometry>(this as AdEditDtoLocationGeometry, _$identity);

  /// Serializes this AdEditDtoLocationGeometry to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as AdEditDtoLocationGeometry;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdEditDtoLocationGeometry&&(identical(other.type, _this.type) || other.type == _this.type)&&const DeepCollectionEquality().equals(other.coordinates, _this.coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as AdEditDtoLocationGeometry;
  return Object.hash(runtimeType,_this.type,const DeepCollectionEquality().hash(_this.coordinates));
}

@override
String toString() {
  final _this = this as AdEditDtoLocationGeometry;
  return 'AdEditDtoLocationGeometry(type: ${_this.type}, coordinates: ${_this.coordinates})';
}


}

/// @nodoc
abstract mixin class $AdEditDtoLocationGeometryCopyWith<$Res>  {
  factory $AdEditDtoLocationGeometryCopyWith(AdEditDtoLocationGeometry value, $Res Function(AdEditDtoLocationGeometry) _then) = _$AdEditDtoLocationGeometryCopyWithImpl;
@useResult
$Res call({
 AdEditDtoLocationGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class _$AdEditDtoLocationGeometryCopyWithImpl<$Res>
    implements $AdEditDtoLocationGeometryCopyWith<$Res> {
  _$AdEditDtoLocationGeometryCopyWithImpl(this._self, this._then);

  final AdEditDtoLocationGeometry _self;
  final $Res Function(AdEditDtoLocationGeometry) _then;

/// Create a copy of AdEditDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(AdEditDtoLocationGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as AdEditDtoLocationGeometryTypeType,coordinates: null == coordinates ? _self.coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}

}


/// Adds pattern-matching-related methods to [AdEditDtoLocationGeometry].
extension AdEditDtoLocationGeometryPatterns on AdEditDtoLocationGeometry {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdEditDtoLocationGeometry value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdEditDtoLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdEditDtoLocationGeometry value)  $default,){
final _that = this;
switch (_that) {
case _AdEditDtoLocationGeometry():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdEditDtoLocationGeometry value)?  $default,){
final _that = this;
switch (_that) {
case _AdEditDtoLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( AdEditDtoLocationGeometryTypeType type,  List<double> coordinates)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdEditDtoLocationGeometry() when $default != null:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( AdEditDtoLocationGeometryTypeType type,  List<double> coordinates)  $default,) {final _that = this;
switch (_that) {
case _AdEditDtoLocationGeometry():
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( AdEditDtoLocationGeometryTypeType type,  List<double> coordinates)?  $default,) {final _that = this;
switch (_that) {
case _AdEditDtoLocationGeometry() when $default != null:
return $default(_that.type,_that.coordinates);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdEditDtoLocationGeometry implements AdEditDtoLocationGeometry {
  const _AdEditDtoLocationGeometry({required this.type, required  List<double> coordinates}): _coordinates = coordinates;
  factory _AdEditDtoLocationGeometry.fromJson(Map<String, dynamic> json) => _$AdEditDtoLocationGeometryFromJson(json);

@override final  AdEditDtoLocationGeometryTypeType type;
/// Coordinates [longitude, latitude]
 final  List<double> _coordinates;
/// Coordinates [longitude, latitude]
@override List<double> get coordinates {
  if (_coordinates is EqualUnmodifiableListView) return _coordinates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_coordinates);
}


/// Create a copy of AdEditDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdEditDtoLocationGeometryCopyWith<_AdEditDtoLocationGeometry> get copyWith => __$AdEditDtoLocationGeometryCopyWithImpl<_AdEditDtoLocationGeometry>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdEditDtoLocationGeometryToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdEditDtoLocationGeometry&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.coordinates, _coordinates));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_coordinates));
}

@override
String toString() {
    return 'AdEditDtoLocationGeometry(type: $type, coordinates: $coordinates)';
}


}

/// @nodoc
abstract mixin class _$AdEditDtoLocationGeometryCopyWith<$Res> implements $AdEditDtoLocationGeometryCopyWith<$Res> {
  factory _$AdEditDtoLocationGeometryCopyWith(_AdEditDtoLocationGeometry value, $Res Function(_AdEditDtoLocationGeometry) _then) = __$AdEditDtoLocationGeometryCopyWithImpl;
@override @useResult
$Res call({
 AdEditDtoLocationGeometryTypeType type, List<double> coordinates
});




}
/// @nodoc
class __$AdEditDtoLocationGeometryCopyWithImpl<$Res>
    implements _$AdEditDtoLocationGeometryCopyWith<$Res> {
  __$AdEditDtoLocationGeometryCopyWithImpl(this._self, this._then);

  final _AdEditDtoLocationGeometry _self;
  final $Res Function(_AdEditDtoLocationGeometry) _then;

/// Create a copy of AdEditDtoLocationGeometry
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? coordinates = null,}) {
  return _then(_AdEditDtoLocationGeometry(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as AdEditDtoLocationGeometryTypeType,coordinates: null == coordinates ? _self._coordinates : coordinates // ignore: cast_nullable_to_non_nullable
as List<double>,
  ));
}


}

// dart format on
