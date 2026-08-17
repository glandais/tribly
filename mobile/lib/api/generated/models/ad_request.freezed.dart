// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ad_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdRequest {

/// Ad name
 String get name;/// Ad description
 MediaDto get media;/// Ad status
 String get status;/// Ad type
 String get adType;/// Price (optional, null for 'contact for price')
 num? get price;/// Rental period (required for RENTAL type)
 String? get rentalPeriod;/// Location description
 String? get locationDescription;/// Location coordinates [longitude, latitude]
 AdRequestLocationGeometry? get locationGeometry;
/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdRequestCopyWith<AdRequest> get copyWith => _$AdRequestCopyWithImpl<AdRequest>(this as AdRequest, _$identity);

  /// Serializes this AdRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.status, status) || other.status == status)&&(identical(other.adType, adType) || other.adType == adType)&&(identical(other.price, price) || other.price == price)&&(identical(other.rentalPeriod, rentalPeriod) || other.rentalPeriod == rentalPeriod)&&(identical(other.locationDescription, locationDescription) || other.locationDescription == locationDescription)&&(identical(other.locationGeometry, locationGeometry) || other.locationGeometry == locationGeometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,status,adType,price,rentalPeriod,locationDescription,locationGeometry);

@override
String toString() {
  return 'AdRequest(name: $name, media: $media, status: $status, adType: $adType, price: $price, rentalPeriod: $rentalPeriod, locationDescription: $locationDescription, locationGeometry: $locationGeometry)';
}


}

/// @nodoc
abstract mixin class $AdRequestCopyWith<$Res>  {
  factory $AdRequestCopyWith(AdRequest value, $Res Function(AdRequest) _then) = _$AdRequestCopyWithImpl;
@useResult
$Res call({
 String name, MediaDto media, String status, String adType, num? price, String? rentalPeriod, String? locationDescription, AdRequestLocationGeometry? locationGeometry
});


$MediaDtoCopyWith<$Res> get media;$AdRequestLocationGeometryCopyWith<$Res>? get locationGeometry;

}
/// @nodoc
class _$AdRequestCopyWithImpl<$Res>
    implements $AdRequestCopyWith<$Res> {
  _$AdRequestCopyWithImpl(this._self, this._then);

  final AdRequest _self;
  final $Res Function(AdRequest) _then;

/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? name = null,Object? media = null,Object? status = null,Object? adType = null,Object? price = freezed,Object? rentalPeriod = freezed,Object? locationDescription = freezed,Object? locationGeometry = freezed,}) {
  return _then(AdRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,adType: null == adType ? _self.adType : adType // ignore: cast_nullable_to_non_nullable
as String,price: freezed == price ? _self.price : price // ignore: cast_nullable_to_non_nullable
as num?,rentalPeriod: freezed == rentalPeriod ? _self.rentalPeriod : rentalPeriod // ignore: cast_nullable_to_non_nullable
as String?,locationDescription: freezed == locationDescription ? _self.locationDescription : locationDescription // ignore: cast_nullable_to_non_nullable
as String?,locationGeometry: freezed == locationGeometry ? _self.locationGeometry : locationGeometry // ignore: cast_nullable_to_non_nullable
as AdRequestLocationGeometry?,
  ));
}
/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AdRequestLocationGeometryCopyWith<$Res>? get locationGeometry {
    if (_self.locationGeometry == null) {
    return null;
  }

  return $AdRequestLocationGeometryCopyWith<$Res>(_self.locationGeometry!, (value) {
    return _then(_self.copyWith(locationGeometry: value));
  });
}
}


/// Adds pattern-matching-related methods to [AdRequest].
extension AdRequestPatterns on AdRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdRequest value)  $default,){
final _that = this;
switch (_that) {
case _AdRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdRequest value)?  $default,){
final _that = this;
switch (_that) {
case _AdRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String status,  String adType,  num? price,  String? rentalPeriod,  String? locationDescription,  AdRequestLocationGeometry? locationGeometry)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdRequest() when $default != null:
return $default(_that.name,_that.media,_that.status,_that.adType,_that.price,_that.rentalPeriod,_that.locationDescription,_that.locationGeometry);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String name,  MediaDto media,  String status,  String adType,  num? price,  String? rentalPeriod,  String? locationDescription,  AdRequestLocationGeometry? locationGeometry)  $default,) {final _that = this;
switch (_that) {
case _AdRequest():
return $default(_that.name,_that.media,_that.status,_that.adType,_that.price,_that.rentalPeriod,_that.locationDescription,_that.locationGeometry);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String name,  MediaDto media,  String status,  String adType,  num? price,  String? rentalPeriod,  String? locationDescription,  AdRequestLocationGeometry? locationGeometry)?  $default,) {final _that = this;
switch (_that) {
case _AdRequest() when $default != null:
return $default(_that.name,_that.media,_that.status,_that.adType,_that.price,_that.rentalPeriod,_that.locationDescription,_that.locationGeometry);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdRequest implements AdRequest {
  const _AdRequest({required this.name, required this.media, required this.status, required this.adType, this.price, this.rentalPeriod, this.locationDescription, this.locationGeometry});
  factory _AdRequest.fromJson(Map<String, dynamic> json) => _$AdRequestFromJson(json);

/// Ad name
@override final  String name;
/// Ad description
@override final  MediaDto media;
/// Ad status
@override final  String status;
/// Ad type
@override final  String adType;
/// Price (optional, null for 'contact for price')
@override final  num? price;
/// Rental period (required for RENTAL type)
@override final  String? rentalPeriod;
/// Location description
@override final  String? locationDescription;
/// Location coordinates [longitude, latitude]
@override final  AdRequestLocationGeometry? locationGeometry;

/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdRequestCopyWith<_AdRequest> get copyWith => __$AdRequestCopyWithImpl<_AdRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdRequest&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.status, status) || other.status == status)&&(identical(other.adType, adType) || other.adType == adType)&&(identical(other.price, price) || other.price == price)&&(identical(other.rentalPeriod, rentalPeriod) || other.rentalPeriod == rentalPeriod)&&(identical(other.locationDescription, locationDescription) || other.locationDescription == locationDescription)&&(identical(other.locationGeometry, locationGeometry) || other.locationGeometry == locationGeometry));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,name,media,status,adType,price,rentalPeriod,locationDescription,locationGeometry);

@override
String toString() {
  return 'AdRequest(name: $name, media: $media, status: $status, adType: $adType, price: $price, rentalPeriod: $rentalPeriod, locationDescription: $locationDescription, locationGeometry: $locationGeometry)';
}


}

/// @nodoc
abstract mixin class _$AdRequestCopyWith<$Res> implements $AdRequestCopyWith<$Res> {
  factory _$AdRequestCopyWith(_AdRequest value, $Res Function(_AdRequest) _then) = __$AdRequestCopyWithImpl;
@override @useResult
$Res call({
 String name, MediaDto media, String status, String adType, num? price, String? rentalPeriod, String? locationDescription, AdRequestLocationGeometry? locationGeometry
});


@override $MediaDtoCopyWith<$Res> get media;@override $AdRequestLocationGeometryCopyWith<$Res>? get locationGeometry;

}
/// @nodoc
class __$AdRequestCopyWithImpl<$Res>
    implements _$AdRequestCopyWith<$Res> {
  __$AdRequestCopyWithImpl(this._self, this._then);

  final _AdRequest _self;
  final $Res Function(_AdRequest) _then;

/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? name = null,Object? media = null,Object? status = null,Object? adType = null,Object? price = freezed,Object? rentalPeriod = freezed,Object? locationDescription = freezed,Object? locationGeometry = freezed,}) {
  return _then(_AdRequest(
name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,adType: null == adType ? _self.adType : adType // ignore: cast_nullable_to_non_nullable
as String,price: freezed == price ? _self.price : price // ignore: cast_nullable_to_non_nullable
as num?,rentalPeriod: freezed == rentalPeriod ? _self.rentalPeriod : rentalPeriod // ignore: cast_nullable_to_non_nullable
as String?,locationDescription: freezed == locationDescription ? _self.locationDescription : locationDescription // ignore: cast_nullable_to_non_nullable
as String?,locationGeometry: freezed == locationGeometry ? _self.locationGeometry : locationGeometry // ignore: cast_nullable_to_non_nullable
as AdRequestLocationGeometry?,
  ));
}

/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of AdRequest
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AdRequestLocationGeometryCopyWith<$Res>? get locationGeometry {
    if (_self.locationGeometry == null) {
    return null;
  }

  return $AdRequestLocationGeometryCopyWith<$Res>(_self.locationGeometry!, (value) {
    return _then(_self.copyWith(locationGeometry: value));
  });
}
}

// dart format on
