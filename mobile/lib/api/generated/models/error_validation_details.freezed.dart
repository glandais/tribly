// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'error_validation_details.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ErrorValidationDetails {

/// Type
 String get type;/// Field Errors
 List<FieldError> get fieldErrors;
/// Create a copy of ErrorValidationDetails
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ErrorValidationDetailsCopyWith<ErrorValidationDetails> get copyWith => _$ErrorValidationDetailsCopyWithImpl<ErrorValidationDetails>(this as ErrorValidationDetails, _$identity);

  /// Serializes this ErrorValidationDetails to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ErrorValidationDetails&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other.fieldErrors, fieldErrors));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(fieldErrors));

@override
String toString() {
  return 'ErrorValidationDetails(type: $type, fieldErrors: $fieldErrors)';
}


}

/// @nodoc
abstract mixin class $ErrorValidationDetailsCopyWith<$Res>  {
  factory $ErrorValidationDetailsCopyWith(ErrorValidationDetails value, $Res Function(ErrorValidationDetails) _then) = _$ErrorValidationDetailsCopyWithImpl;
@useResult
$Res call({
 String type, List<FieldError> fieldErrors
});




}
/// @nodoc
class _$ErrorValidationDetailsCopyWithImpl<$Res>
    implements $ErrorValidationDetailsCopyWith<$Res> {
  _$ErrorValidationDetailsCopyWithImpl(this._self, this._then);

  final ErrorValidationDetails _self;
  final $Res Function(ErrorValidationDetails) _then;

/// Create a copy of ErrorValidationDetails
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? type = null,Object? fieldErrors = null,}) {
  return _then(ErrorValidationDetails(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,fieldErrors: null == fieldErrors ? _self.fieldErrors : fieldErrors // ignore: cast_nullable_to_non_nullable
as List<FieldError>,
  ));
}

}


/// Adds pattern-matching-related methods to [ErrorValidationDetails].
extension ErrorValidationDetailsPatterns on ErrorValidationDetails {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ErrorValidationDetails value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ErrorValidationDetails() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ErrorValidationDetails value)  $default,){
final _that = this;
switch (_that) {
case _ErrorValidationDetails():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ErrorValidationDetails value)?  $default,){
final _that = this;
switch (_that) {
case _ErrorValidationDetails() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String type,  List<FieldError> fieldErrors)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ErrorValidationDetails() when $default != null:
return $default(_that.type,_that.fieldErrors);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String type,  List<FieldError> fieldErrors)  $default,) {final _that = this;
switch (_that) {
case _ErrorValidationDetails():
return $default(_that.type,_that.fieldErrors);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String type,  List<FieldError> fieldErrors)?  $default,) {final _that = this;
switch (_that) {
case _ErrorValidationDetails() when $default != null:
return $default(_that.type,_that.fieldErrors);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ErrorValidationDetails implements ErrorValidationDetails {
  const _ErrorValidationDetails({required this.type, required  List<FieldError> fieldErrors}): _fieldErrors = fieldErrors;
  factory _ErrorValidationDetails.fromJson(Map<String, dynamic> json) => _$ErrorValidationDetailsFromJson(json);

/// Type
@override final  String type;
/// Field Errors
 final  List<FieldError> _fieldErrors;
/// Field Errors
@override List<FieldError> get fieldErrors {
  if (_fieldErrors is EqualUnmodifiableListView) return _fieldErrors;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_fieldErrors);
}


/// Create a copy of ErrorValidationDetails
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ErrorValidationDetailsCopyWith<_ErrorValidationDetails> get copyWith => __$ErrorValidationDetailsCopyWithImpl<_ErrorValidationDetails>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ErrorValidationDetailsToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ErrorValidationDetails&&(identical(other.type, type) || other.type == type)&&const DeepCollectionEquality().equals(other._fieldErrors, _fieldErrors));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,type,const DeepCollectionEquality().hash(_fieldErrors));

@override
String toString() {
  return 'ErrorValidationDetails(type: $type, fieldErrors: $fieldErrors)';
}


}

/// @nodoc
abstract mixin class _$ErrorValidationDetailsCopyWith<$Res> implements $ErrorValidationDetailsCopyWith<$Res> {
  factory _$ErrorValidationDetailsCopyWith(_ErrorValidationDetails value, $Res Function(_ErrorValidationDetails) _then) = __$ErrorValidationDetailsCopyWithImpl;
@override @useResult
$Res call({
 String type, List<FieldError> fieldErrors
});




}
/// @nodoc
class __$ErrorValidationDetailsCopyWithImpl<$Res>
    implements _$ErrorValidationDetailsCopyWith<$Res> {
  __$ErrorValidationDetailsCopyWithImpl(this._self, this._then);

  final _ErrorValidationDetails _self;
  final $Res Function(_ErrorValidationDetails) _then;

/// Create a copy of ErrorValidationDetails
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? type = null,Object? fieldErrors = null,}) {
  return _then(_ErrorValidationDetails(
type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,fieldErrors: null == fieldErrors ? _self._fieldErrors : fieldErrors // ignore: cast_nullable_to_non_nullable
as List<FieldError>,
  ));
}


}

// dart format on
