// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'map_style_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MapStyleDto {

/// Stable style identifier, e.g. 'colorful'
 String get id;/// Human-readable label for the style switcher
 String get label;/// URL of the MapLibre style document to load in light mode (or at all times when darkVariant is null)
 String get url;/// URL of the style document to load instead of 'url' when the client renders in dark mode. Null when the style has no dark counterpart — the client then keeps using 'url'.
 String? get darkVariant;
/// Create a copy of MapStyleDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MapStyleDtoCopyWith<MapStyleDto> get copyWith => _$MapStyleDtoCopyWithImpl<MapStyleDto>(this as MapStyleDto, _$identity);

  /// Serializes this MapStyleDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MapStyleDto&&(identical(other.id, id) || other.id == id)&&(identical(other.label, label) || other.label == label)&&(identical(other.url, url) || other.url == url)&&(identical(other.darkVariant, darkVariant) || other.darkVariant == darkVariant));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,label,url,darkVariant);

@override
String toString() {
  return 'MapStyleDto(id: $id, label: $label, url: $url, darkVariant: $darkVariant)';
}


}

/// @nodoc
abstract mixin class $MapStyleDtoCopyWith<$Res>  {
  factory $MapStyleDtoCopyWith(MapStyleDto value, $Res Function(MapStyleDto) _then) = _$MapStyleDtoCopyWithImpl;
@useResult
$Res call({
 String id, String label, String url, String? darkVariant
});




}
/// @nodoc
class _$MapStyleDtoCopyWithImpl<$Res>
    implements $MapStyleDtoCopyWith<$Res> {
  _$MapStyleDtoCopyWithImpl(this._self, this._then);

  final MapStyleDto _self;
  final $Res Function(MapStyleDto) _then;

/// Create a copy of MapStyleDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? label = null,Object? url = null,Object? darkVariant = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,label: null == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String,url: null == url ? _self.url : url // ignore: cast_nullable_to_non_nullable
as String,darkVariant: freezed == darkVariant ? _self.darkVariant : darkVariant // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [MapStyleDto].
extension MapStyleDtoPatterns on MapStyleDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MapStyleDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MapStyleDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MapStyleDto value)  $default,){
final _that = this;
switch (_that) {
case _MapStyleDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MapStyleDto value)?  $default,){
final _that = this;
switch (_that) {
case _MapStyleDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String label,  String url,  String? darkVariant)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MapStyleDto() when $default != null:
return $default(_that.id,_that.label,_that.url,_that.darkVariant);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String label,  String url,  String? darkVariant)  $default,) {final _that = this;
switch (_that) {
case _MapStyleDto():
return $default(_that.id,_that.label,_that.url,_that.darkVariant);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String label,  String url,  String? darkVariant)?  $default,) {final _that = this;
switch (_that) {
case _MapStyleDto() when $default != null:
return $default(_that.id,_that.label,_that.url,_that.darkVariant);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MapStyleDto implements MapStyleDto {
  const _MapStyleDto({required this.id, required this.label, required this.url, this.darkVariant});
  factory _MapStyleDto.fromJson(Map<String, dynamic> json) => _$MapStyleDtoFromJson(json);

/// Stable style identifier, e.g. 'colorful'
@override final  String id;
/// Human-readable label for the style switcher
@override final  String label;
/// URL of the MapLibre style document to load in light mode (or at all times when darkVariant is null)
@override final  String url;
/// URL of the style document to load instead of 'url' when the client renders in dark mode. Null when the style has no dark counterpart — the client then keeps using 'url'.
@override final  String? darkVariant;

/// Create a copy of MapStyleDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MapStyleDtoCopyWith<_MapStyleDto> get copyWith => __$MapStyleDtoCopyWithImpl<_MapStyleDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MapStyleDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _MapStyleDto&&(identical(other.id, id) || other.id == id)&&(identical(other.label, label) || other.label == label)&&(identical(other.url, url) || other.url == url)&&(identical(other.darkVariant, darkVariant) || other.darkVariant == darkVariant));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,label,url,darkVariant);

@override
String toString() {
  return 'MapStyleDto(id: $id, label: $label, url: $url, darkVariant: $darkVariant)';
}


}

/// @nodoc
abstract mixin class _$MapStyleDtoCopyWith<$Res> implements $MapStyleDtoCopyWith<$Res> {
  factory _$MapStyleDtoCopyWith(_MapStyleDto value, $Res Function(_MapStyleDto) _then) = __$MapStyleDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String label, String url, String? darkVariant
});




}
/// @nodoc
class __$MapStyleDtoCopyWithImpl<$Res>
    implements _$MapStyleDtoCopyWith<$Res> {
  __$MapStyleDtoCopyWithImpl(this._self, this._then);

  final _MapStyleDto _self;
  final $Res Function(_MapStyleDto) _then;

/// Create a copy of MapStyleDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? label = null,Object? url = null,Object? darkVariant = freezed,}) {
  return _then(_MapStyleDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,label: null == label ? _self.label : label // ignore: cast_nullable_to_non_nullable
as String,url: null == url ? _self.url : url // ignore: cast_nullable_to_non_nullable
as String,darkVariant: freezed == darkVariant ? _self.darkVariant : darkVariant // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
