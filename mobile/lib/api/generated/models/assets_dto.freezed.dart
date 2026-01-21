// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'assets_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AssetsDto {

/// Images
 List<AssetDto> get images;/// Attachments
 List<AssetDto> get attachments;/// Logo
 AssetDto? get logo;/// Original GPX
 AssetDto? get originalGpx;/// GPX
 AssetDto? get gpx;/// FIT
 AssetDto? get fit;/// Thumbnail
 AssetDto? get thumbnail;
/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AssetsDtoCopyWith<AssetsDto> get copyWith => _$AssetsDtoCopyWithImpl<AssetsDto>(this as AssetsDto, _$identity);

  /// Serializes this AssetsDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AssetsDto&&const DeepCollectionEquality().equals(other.images, images)&&const DeepCollectionEquality().equals(other.attachments, attachments)&&(identical(other.logo, logo) || other.logo == logo)&&(identical(other.originalGpx, originalGpx) || other.originalGpx == originalGpx)&&(identical(other.gpx, gpx) || other.gpx == gpx)&&(identical(other.fit, fit) || other.fit == fit)&&(identical(other.thumbnail, thumbnail) || other.thumbnail == thumbnail));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(images),const DeepCollectionEquality().hash(attachments),logo,originalGpx,gpx,fit,thumbnail);

@override
String toString() {
  return 'AssetsDto(images: $images, attachments: $attachments, logo: $logo, originalGpx: $originalGpx, gpx: $gpx, fit: $fit, thumbnail: $thumbnail)';
}


}

/// @nodoc
abstract mixin class $AssetsDtoCopyWith<$Res>  {
  factory $AssetsDtoCopyWith(AssetsDto value, $Res Function(AssetsDto) _then) = _$AssetsDtoCopyWithImpl;
@useResult
$Res call({
 List<AssetDto> images, List<AssetDto> attachments, AssetDto? logo, AssetDto? originalGpx, AssetDto? gpx, AssetDto? fit, AssetDto? thumbnail
});


$AssetDtoCopyWith<$Res>? get logo;$AssetDtoCopyWith<$Res>? get originalGpx;$AssetDtoCopyWith<$Res>? get gpx;$AssetDtoCopyWith<$Res>? get fit;$AssetDtoCopyWith<$Res>? get thumbnail;

}
/// @nodoc
class _$AssetsDtoCopyWithImpl<$Res>
    implements $AssetsDtoCopyWith<$Res> {
  _$AssetsDtoCopyWithImpl(this._self, this._then);

  final AssetsDto _self;
  final $Res Function(AssetsDto) _then;

/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? images = null,Object? attachments = null,Object? logo = freezed,Object? originalGpx = freezed,Object? gpx = freezed,Object? fit = freezed,Object? thumbnail = freezed,}) {
  return _then(_self.copyWith(
images: null == images ? _self.images : images // ignore: cast_nullable_to_non_nullable
as List<AssetDto>,attachments: null == attachments ? _self.attachments : attachments // ignore: cast_nullable_to_non_nullable
as List<AssetDto>,logo: freezed == logo ? _self.logo : logo // ignore: cast_nullable_to_non_nullable
as AssetDto?,originalGpx: freezed == originalGpx ? _self.originalGpx : originalGpx // ignore: cast_nullable_to_non_nullable
as AssetDto?,gpx: freezed == gpx ? _self.gpx : gpx // ignore: cast_nullable_to_non_nullable
as AssetDto?,fit: freezed == fit ? _self.fit : fit // ignore: cast_nullable_to_non_nullable
as AssetDto?,thumbnail: freezed == thumbnail ? _self.thumbnail : thumbnail // ignore: cast_nullable_to_non_nullable
as AssetDto?,
  ));
}
/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get logo {
    if (_self.logo == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.logo!, (value) {
    return _then(_self.copyWith(logo: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get originalGpx {
    if (_self.originalGpx == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.originalGpx!, (value) {
    return _then(_self.copyWith(originalGpx: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get gpx {
    if (_self.gpx == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.gpx!, (value) {
    return _then(_self.copyWith(gpx: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get fit {
    if (_self.fit == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.fit!, (value) {
    return _then(_self.copyWith(fit: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get thumbnail {
    if (_self.thumbnail == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.thumbnail!, (value) {
    return _then(_self.copyWith(thumbnail: value));
  });
}
}


/// Adds pattern-matching-related methods to [AssetsDto].
extension AssetsDtoPatterns on AssetsDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AssetsDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AssetsDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AssetsDto value)  $default,){
final _that = this;
switch (_that) {
case _AssetsDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AssetsDto value)?  $default,){
final _that = this;
switch (_that) {
case _AssetsDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( List<AssetDto> images,  List<AssetDto> attachments,  AssetDto? logo,  AssetDto? originalGpx,  AssetDto? gpx,  AssetDto? fit,  AssetDto? thumbnail)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AssetsDto() when $default != null:
return $default(_that.images,_that.attachments,_that.logo,_that.originalGpx,_that.gpx,_that.fit,_that.thumbnail);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( List<AssetDto> images,  List<AssetDto> attachments,  AssetDto? logo,  AssetDto? originalGpx,  AssetDto? gpx,  AssetDto? fit,  AssetDto? thumbnail)  $default,) {final _that = this;
switch (_that) {
case _AssetsDto():
return $default(_that.images,_that.attachments,_that.logo,_that.originalGpx,_that.gpx,_that.fit,_that.thumbnail);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( List<AssetDto> images,  List<AssetDto> attachments,  AssetDto? logo,  AssetDto? originalGpx,  AssetDto? gpx,  AssetDto? fit,  AssetDto? thumbnail)?  $default,) {final _that = this;
switch (_that) {
case _AssetsDto() when $default != null:
return $default(_that.images,_that.attachments,_that.logo,_that.originalGpx,_that.gpx,_that.fit,_that.thumbnail);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AssetsDto implements AssetsDto {
  const _AssetsDto({required final  List<AssetDto> images, required final  List<AssetDto> attachments, this.logo, this.originalGpx, this.gpx, this.fit, this.thumbnail}): _images = images,_attachments = attachments;
  factory _AssetsDto.fromJson(Map<String, dynamic> json) => _$AssetsDtoFromJson(json);

/// Images
 final  List<AssetDto> _images;
/// Images
@override List<AssetDto> get images {
  if (_images is EqualUnmodifiableListView) return _images;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_images);
}

/// Attachments
 final  List<AssetDto> _attachments;
/// Attachments
@override List<AssetDto> get attachments {
  if (_attachments is EqualUnmodifiableListView) return _attachments;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_attachments);
}

/// Logo
@override final  AssetDto? logo;
/// Original GPX
@override final  AssetDto? originalGpx;
/// GPX
@override final  AssetDto? gpx;
/// FIT
@override final  AssetDto? fit;
/// Thumbnail
@override final  AssetDto? thumbnail;

/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AssetsDtoCopyWith<_AssetsDto> get copyWith => __$AssetsDtoCopyWithImpl<_AssetsDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AssetsDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AssetsDto&&const DeepCollectionEquality().equals(other._images, _images)&&const DeepCollectionEquality().equals(other._attachments, _attachments)&&(identical(other.logo, logo) || other.logo == logo)&&(identical(other.originalGpx, originalGpx) || other.originalGpx == originalGpx)&&(identical(other.gpx, gpx) || other.gpx == gpx)&&(identical(other.fit, fit) || other.fit == fit)&&(identical(other.thumbnail, thumbnail) || other.thumbnail == thumbnail));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_images),const DeepCollectionEquality().hash(_attachments),logo,originalGpx,gpx,fit,thumbnail);

@override
String toString() {
  return 'AssetsDto(images: $images, attachments: $attachments, logo: $logo, originalGpx: $originalGpx, gpx: $gpx, fit: $fit, thumbnail: $thumbnail)';
}


}

/// @nodoc
abstract mixin class _$AssetsDtoCopyWith<$Res> implements $AssetsDtoCopyWith<$Res> {
  factory _$AssetsDtoCopyWith(_AssetsDto value, $Res Function(_AssetsDto) _then) = __$AssetsDtoCopyWithImpl;
@override @useResult
$Res call({
 List<AssetDto> images, List<AssetDto> attachments, AssetDto? logo, AssetDto? originalGpx, AssetDto? gpx, AssetDto? fit, AssetDto? thumbnail
});


@override $AssetDtoCopyWith<$Res>? get logo;@override $AssetDtoCopyWith<$Res>? get originalGpx;@override $AssetDtoCopyWith<$Res>? get gpx;@override $AssetDtoCopyWith<$Res>? get fit;@override $AssetDtoCopyWith<$Res>? get thumbnail;

}
/// @nodoc
class __$AssetsDtoCopyWithImpl<$Res>
    implements _$AssetsDtoCopyWith<$Res> {
  __$AssetsDtoCopyWithImpl(this._self, this._then);

  final _AssetsDto _self;
  final $Res Function(_AssetsDto) _then;

/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? images = null,Object? attachments = null,Object? logo = freezed,Object? originalGpx = freezed,Object? gpx = freezed,Object? fit = freezed,Object? thumbnail = freezed,}) {
  return _then(_AssetsDto(
images: null == images ? _self._images : images // ignore: cast_nullable_to_non_nullable
as List<AssetDto>,attachments: null == attachments ? _self._attachments : attachments // ignore: cast_nullable_to_non_nullable
as List<AssetDto>,logo: freezed == logo ? _self.logo : logo // ignore: cast_nullable_to_non_nullable
as AssetDto?,originalGpx: freezed == originalGpx ? _self.originalGpx : originalGpx // ignore: cast_nullable_to_non_nullable
as AssetDto?,gpx: freezed == gpx ? _self.gpx : gpx // ignore: cast_nullable_to_non_nullable
as AssetDto?,fit: freezed == fit ? _self.fit : fit // ignore: cast_nullable_to_non_nullable
as AssetDto?,thumbnail: freezed == thumbnail ? _self.thumbnail : thumbnail // ignore: cast_nullable_to_non_nullable
as AssetDto?,
  ));
}

/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get logo {
    if (_self.logo == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.logo!, (value) {
    return _then(_self.copyWith(logo: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get originalGpx {
    if (_self.originalGpx == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.originalGpx!, (value) {
    return _then(_self.copyWith(originalGpx: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get gpx {
    if (_self.gpx == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.gpx!, (value) {
    return _then(_self.copyWith(gpx: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get fit {
    if (_self.fit == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.fit!, (value) {
    return _then(_self.copyWith(fit: value));
  });
}/// Create a copy of AssetsDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$AssetDtoCopyWith<$Res>? get thumbnail {
    if (_self.thumbnail == null) {
    return null;
  }

  return $AssetDtoCopyWith<$Res>(_self.thumbnail!, (value) {
    return _then(_self.copyWith(thumbnail: value));
  });
}
}

// dart format on
