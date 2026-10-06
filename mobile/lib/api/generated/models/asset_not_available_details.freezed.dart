// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'asset_not_available_details.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AssetNotAvailableDetails {

/// Type
 String get type;/// Id of the asset, as the request cited it
 String get assetId;
/// Create a copy of AssetNotAvailableDetails
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AssetNotAvailableDetailsCopyWith<AssetNotAvailableDetails> get copyWith => _$AssetNotAvailableDetailsCopyWithImpl<AssetNotAvailableDetails>(this as AssetNotAvailableDetails, _$identity);

  /// Serializes this AssetNotAvailableDetails to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as AssetNotAvailableDetails;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AssetNotAvailableDetails&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.assetId, _this.assetId) || other.assetId == _this.assetId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as AssetNotAvailableDetails;
  return Object.hash(runtimeType,_this.type,_this.assetId);
}

@override
String toString() {
  final _this = this as AssetNotAvailableDetails;
  return 'AssetNotAvailableDetails(type: ${_this.type}, assetId: ${_this.assetId})';
}


}

/// @nodoc
abstract mixin class $AssetNotAvailableDetailsCopyWith<$Res>  {
  factory $AssetNotAvailableDetailsCopyWith(AssetNotAvailableDetails value, $Res Function(AssetNotAvailableDetails) _then) = _$AssetNotAvailableDetailsCopyWithImpl;
@useResult
$Res call({
 String type, String assetId
});




}
/// @nodoc
class _$AssetNotAvailableDetailsCopyWithImpl<$Res>
    implements $AssetNotAvailableDetailsCopyWith<$Res> {
  _$AssetNotAvailableDetailsCopyWithImpl(this._self, this._then);

  final AssetNotAvailableDetails _self;
  final $Res Function(AssetNotAvailableDetails) _then;

/// Create a copy of AssetNotAvailableDetails
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? assetId = null,}) {
  return _then(AssetNotAvailableDetails(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,assetId: null == assetId ? _self.assetId : assetId // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [AssetNotAvailableDetails].
extension AssetNotAvailableDetailsPatterns on AssetNotAvailableDetails {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AssetNotAvailableDetails value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AssetNotAvailableDetails() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AssetNotAvailableDetails value)  $default,){
final _that = this;
switch (_that) {
case _AssetNotAvailableDetails():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AssetNotAvailableDetails value)?  $default,){
final _that = this;
switch (_that) {
case _AssetNotAvailableDetails() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  String assetId)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AssetNotAvailableDetails() when $default != null:
return $default(_that.type,_that.assetId);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  String assetId)  $default,) {final _that = this;
switch (_that) {
case _AssetNotAvailableDetails():
return $default(_that.type,_that.assetId);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  String assetId)?  $default,) {final _that = this;
switch (_that) {
case _AssetNotAvailableDetails() when $default != null:
return $default(_that.type,_that.assetId);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AssetNotAvailableDetails implements AssetNotAvailableDetails {
  const _AssetNotAvailableDetails({required this.type, required this.assetId});
  factory _AssetNotAvailableDetails.fromJson(Map<String, dynamic> json) => _$AssetNotAvailableDetailsFromJson(json);

/// Type
@override final  String type;
/// Id of the asset, as the request cited it
@override final  String assetId;

/// Create a copy of AssetNotAvailableDetails
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AssetNotAvailableDetailsCopyWith<_AssetNotAvailableDetails> get copyWith => __$AssetNotAvailableDetailsCopyWithImpl<_AssetNotAvailableDetails>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AssetNotAvailableDetailsToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _AssetNotAvailableDetails&&(identical(other.type, type) || other.type == type)&&(identical(other.assetId, assetId) || other.assetId == assetId));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,type,assetId);
}

@override
String toString() {
    return 'AssetNotAvailableDetails(type: $type, assetId: $assetId)';
}


}

/// @nodoc
abstract mixin class _$AssetNotAvailableDetailsCopyWith<$Res> implements $AssetNotAvailableDetailsCopyWith<$Res> {
  factory _$AssetNotAvailableDetailsCopyWith(_AssetNotAvailableDetails value, $Res Function(_AssetNotAvailableDetails) _then) = __$AssetNotAvailableDetailsCopyWithImpl;
@override @useResult
$Res call({
 String type, String assetId
});




}
/// @nodoc
class __$AssetNotAvailableDetailsCopyWithImpl<$Res>
    implements _$AssetNotAvailableDetailsCopyWith<$Res> {
  __$AssetNotAvailableDetailsCopyWithImpl(this._self, this._then);

  final _AssetNotAvailableDetails _self;
  final $Res Function(_AssetNotAvailableDetails) _then;

/// Create a copy of AssetNotAvailableDetails
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? assetId = null,}) {
  return _then(_AssetNotAvailableDetails(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,assetId: null == assetId ? _self.assetId : assetId // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
