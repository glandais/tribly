// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'ad_edit_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AdEditDto {

/// Team
 TeamPublicationDto get team;/// Ad ID (TSID)
 String get id;/// Ad URL slug
 String get slug;/// Ad name
 String get name;/// Ad media
 MediaDto get media;/// Ad status
 String get status;/// Visibility level
 String get visibility;/// Ad type
 String get adType;/// Creation timestamp
 String get createdAt;/// Creation timestamp
 String get updatedAt;/// Creator ID (TSID)
 String get createdById;/// Price
 num? get price;/// Rental period
 String? get rentalPeriod;/// Location coordinates [longitude, latitude]
 AdEditDtoLocationGeometry? get locationGeometry;/// Location description
 String? get locationDescription;
/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AdEditDtoCopyWith<AdEditDto> get copyWith => _$AdEditDtoCopyWithImpl<AdEditDto>(this as AdEditDto, _$identity);

  /// Serializes this AdEditDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AdEditDto&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.adType, adType) || other.adType == adType)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt)&&(identical(other.createdById, createdById) || other.createdById == createdById)&&(identical(other.price, price) || other.price == price)&&(identical(other.rentalPeriod, rentalPeriod) || other.rentalPeriod == rentalPeriod)&&(identical(other.locationGeometry, locationGeometry) || other.locationGeometry == locationGeometry)&&(identical(other.locationDescription, locationDescription) || other.locationDescription == locationDescription));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,team,id,slug,name,media,status,visibility,adType,createdAt,updatedAt,createdById,price,rentalPeriod,locationGeometry,locationDescription);

@override
String toString() {
  return 'AdEditDto(team: $team, id: $id, slug: $slug, name: $name, media: $media, status: $status, visibility: $visibility, adType: $adType, createdAt: $createdAt, updatedAt: $updatedAt, createdById: $createdById, price: $price, rentalPeriod: $rentalPeriod, locationGeometry: $locationGeometry, locationDescription: $locationDescription)';
}


}

/// @nodoc
abstract mixin class $AdEditDtoCopyWith<$Res>  {
  factory $AdEditDtoCopyWith(AdEditDto value, $Res Function(AdEditDto) _then) = _$AdEditDtoCopyWithImpl;
@useResult
$Res call({
 TeamPublicationDto team, String id, String slug, String name, MediaDto media, String status, String visibility, String adType, String createdAt, String updatedAt, String createdById, num? price, String? rentalPeriod, AdEditDtoLocationGeometry? locationGeometry, String? locationDescription
});


$TeamPublicationDtoCopyWith<$Res> get team;$MediaDtoCopyWith<$Res> get media;$AdEditDtoLocationGeometryCopyWith<$Res>? get locationGeometry;

}
/// @nodoc
class _$AdEditDtoCopyWithImpl<$Res>
    implements $AdEditDtoCopyWith<$Res> {
  _$AdEditDtoCopyWithImpl(this._self, this._then);

  final AdEditDto _self;
  final $Res Function(AdEditDto) _then;

/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? status = null,Object? visibility = null,Object? adType = null,Object? createdAt = null,Object? updatedAt = null,Object? createdById = null,Object? price = freezed,Object? rentalPeriod = freezed,Object? locationGeometry = freezed,Object? locationDescription = freezed,}) {
  return _then(_self.copyWith(
team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,adType: null == adType ? _self.adType : adType // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,createdById: null == createdById ? _self.createdById : createdById // ignore: cast_nullable_to_non_nullable
as String,price: freezed == price ? _self.price : price // ignore: cast_nullable_to_non_nullable
as num?,rentalPeriod: freezed == rentalPeriod ? _self.rentalPeriod : rentalPeriod // ignore: cast_nullable_to_non_nullable
as String?,locationGeometry: freezed == locationGeometry ? _self.locationGeometry : locationGeometry // ignore: cast_nullable_to_non_nullable
as AdEditDtoLocationGeometry?,locationDescription: freezed == locationDescription ? _self.locationDescription : locationDescription // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}
/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AdEditDtoLocationGeometryCopyWith<$Res>? get locationGeometry {
    if (_self.locationGeometry == null) {
    return null;
  }

  return $AdEditDtoLocationGeometryCopyWith<$Res>(_self.locationGeometry!, (value) {
    return _then(_self.copyWith(locationGeometry: value));
  });
}
}


/// Adds pattern-matching-related methods to [AdEditDto].
extension AdEditDtoPatterns on AdEditDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AdEditDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AdEditDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AdEditDto value)  $default,){
final _that = this;
switch (_that) {
case _AdEditDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AdEditDto value)?  $default,){
final _that = this;
switch (_that) {
case _AdEditDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String status,  String visibility,  String adType,  String createdAt,  String updatedAt,  String createdById,  num? price,  String? rentalPeriod,  AdEditDtoLocationGeometry? locationGeometry,  String? locationDescription)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AdEditDto() when $default != null:
return $default(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.status,_that.visibility,_that.adType,_that.createdAt,_that.updatedAt,_that.createdById,_that.price,_that.rentalPeriod,_that.locationGeometry,_that.locationDescription);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String status,  String visibility,  String adType,  String createdAt,  String updatedAt,  String createdById,  num? price,  String? rentalPeriod,  AdEditDtoLocationGeometry? locationGeometry,  String? locationDescription)  $default,) {final _that = this;
switch (_that) {
case _AdEditDto():
return $default(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.status,_that.visibility,_that.adType,_that.createdAt,_that.updatedAt,_that.createdById,_that.price,_that.rentalPeriod,_that.locationGeometry,_that.locationDescription);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( TeamPublicationDto team,  String id,  String slug,  String name,  MediaDto media,  String status,  String visibility,  String adType,  String createdAt,  String updatedAt,  String createdById,  num? price,  String? rentalPeriod,  AdEditDtoLocationGeometry? locationGeometry,  String? locationDescription)?  $default,) {final _that = this;
switch (_that) {
case _AdEditDto() when $default != null:
return $default(_that.team,_that.id,_that.slug,_that.name,_that.media,_that.status,_that.visibility,_that.adType,_that.createdAt,_that.updatedAt,_that.createdById,_that.price,_that.rentalPeriod,_that.locationGeometry,_that.locationDescription);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AdEditDto implements AdEditDto {
  const _AdEditDto({required this.team, required this.id, required this.slug, required this.name, required this.media, required this.status, required this.visibility, required this.adType, required this.createdAt, required this.updatedAt, required this.createdById, this.price, this.rentalPeriod, this.locationGeometry, this.locationDescription});
  factory _AdEditDto.fromJson(Map<String, dynamic> json) => _$AdEditDtoFromJson(json);

/// Team
@override final  TeamPublicationDto team;
/// Ad ID (TSID)
@override final  String id;
/// Ad URL slug
@override final  String slug;
/// Ad name
@override final  String name;
/// Ad media
@override final  MediaDto media;
/// Ad status
@override final  String status;
/// Visibility level
@override final  String visibility;
/// Ad type
@override final  String adType;
/// Creation timestamp
@override final  String createdAt;
/// Creation timestamp
@override final  String updatedAt;
/// Creator ID (TSID)
@override final  String createdById;
/// Price
@override final  num? price;
/// Rental period
@override final  String? rentalPeriod;
/// Location coordinates [longitude, latitude]
@override final  AdEditDtoLocationGeometry? locationGeometry;
/// Location description
@override final  String? locationDescription;

/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AdEditDtoCopyWith<_AdEditDto> get copyWith => __$AdEditDtoCopyWithImpl<_AdEditDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AdEditDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AdEditDto&&(identical(other.team, team) || other.team == team)&&(identical(other.id, id) || other.id == id)&&(identical(other.slug, slug) || other.slug == slug)&&(identical(other.name, name) || other.name == name)&&(identical(other.media, media) || other.media == media)&&(identical(other.status, status) || other.status == status)&&(identical(other.visibility, visibility) || other.visibility == visibility)&&(identical(other.adType, adType) || other.adType == adType)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt)&&(identical(other.createdById, createdById) || other.createdById == createdById)&&(identical(other.price, price) || other.price == price)&&(identical(other.rentalPeriod, rentalPeriod) || other.rentalPeriod == rentalPeriod)&&(identical(other.locationGeometry, locationGeometry) || other.locationGeometry == locationGeometry)&&(identical(other.locationDescription, locationDescription) || other.locationDescription == locationDescription));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,team,id,slug,name,media,status,visibility,adType,createdAt,updatedAt,createdById,price,rentalPeriod,locationGeometry,locationDescription);

@override
String toString() {
  return 'AdEditDto(team: $team, id: $id, slug: $slug, name: $name, media: $media, status: $status, visibility: $visibility, adType: $adType, createdAt: $createdAt, updatedAt: $updatedAt, createdById: $createdById, price: $price, rentalPeriod: $rentalPeriod, locationGeometry: $locationGeometry, locationDescription: $locationDescription)';
}


}

/// @nodoc
abstract mixin class _$AdEditDtoCopyWith<$Res> implements $AdEditDtoCopyWith<$Res> {
  factory _$AdEditDtoCopyWith(_AdEditDto value, $Res Function(_AdEditDto) _then) = __$AdEditDtoCopyWithImpl;
@override @useResult
$Res call({
 TeamPublicationDto team, String id, String slug, String name, MediaDto media, String status, String visibility, String adType, String createdAt, String updatedAt, String createdById, num? price, String? rentalPeriod, AdEditDtoLocationGeometry? locationGeometry, String? locationDescription
});


@override $TeamPublicationDtoCopyWith<$Res> get team;@override $MediaDtoCopyWith<$Res> get media;@override $AdEditDtoLocationGeometryCopyWith<$Res>? get locationGeometry;

}
/// @nodoc
class __$AdEditDtoCopyWithImpl<$Res>
    implements _$AdEditDtoCopyWith<$Res> {
  __$AdEditDtoCopyWithImpl(this._self, this._then);

  final _AdEditDto _self;
  final $Res Function(_AdEditDto) _then;

/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? team = null,Object? id = null,Object? slug = null,Object? name = null,Object? media = null,Object? status = null,Object? visibility = null,Object? adType = null,Object? createdAt = null,Object? updatedAt = null,Object? createdById = null,Object? price = freezed,Object? rentalPeriod = freezed,Object? locationGeometry = freezed,Object? locationDescription = freezed,}) {
  return _then(_AdEditDto(
team: null == team ? _self.team : team // ignore: cast_nullable_to_non_nullable
as TeamPublicationDto,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,media: null == media ? _self.media : media // ignore: cast_nullable_to_non_nullable
as MediaDto,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,visibility: null == visibility ? _self.visibility : visibility // ignore: cast_nullable_to_non_nullable
as String,adType: null == adType ? _self.adType : adType // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,createdById: null == createdById ? _self.createdById : createdById // ignore: cast_nullable_to_non_nullable
as String,price: freezed == price ? _self.price : price // ignore: cast_nullable_to_non_nullable
as num?,rentalPeriod: freezed == rentalPeriod ? _self.rentalPeriod : rentalPeriod // ignore: cast_nullable_to_non_nullable
as String?,locationGeometry: freezed == locationGeometry ? _self.locationGeometry : locationGeometry // ignore: cast_nullable_to_non_nullable
as AdEditDtoLocationGeometry?,locationDescription: freezed == locationDescription ? _self.locationDescription : locationDescription // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$TeamPublicationDtoCopyWith<$Res> get team {
  
  return $TeamPublicationDtoCopyWith<$Res>(_self.team, (value) {
    return _then(_self.copyWith(team: value));
  });
}/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$MediaDtoCopyWith<$Res> get media {
  
  return $MediaDtoCopyWith<$Res>(_self.media, (value) {
    return _then(_self.copyWith(media: value));
  });
}/// Create a copy of AdEditDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AdEditDtoLocationGeometryCopyWith<$Res>? get locationGeometry {
    if (_self.locationGeometry == null) {
    return null;
  }

  return $AdEditDtoLocationGeometryCopyWith<$Res>(_self.locationGeometry!, (value) {
    return _then(_self.copyWith(locationGeometry: value));
  });
}
}

// dart format on
