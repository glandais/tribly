// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'error_details.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;
ErrorDetails _$ErrorDetailsFromJson(
  Map<String, dynamic> json
) {
        switch (json['type']) {
                  case 'VALIDATION':
          return ErrorDetailsValidation.fromJson(
            json
          );
                case 'NOT_FOUND':
          return ErrorDetailsNotFound.fromJson(
            json
          );
        
          default:
            throw CheckedFromJsonException(
  json,
  'type',
  'ErrorDetails',
  'Invalid union type "${json['type']}"!'
);
        }
      
}

/// @nodoc
mixin _$ErrorDetails {



  /// Serializes this ErrorDetails to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ErrorDetails);
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => runtimeType.hashCode;

@override
String toString() {
  return 'ErrorDetails()';
}


}

/// @nodoc
class $ErrorDetailsCopyWith<$Res>  {
$ErrorDetailsCopyWith(ErrorDetails _, $Res Function(ErrorDetails) __);
}


/// Adds pattern-matching-related methods to [ErrorDetails].
extension ErrorDetailsPatterns on ErrorDetails {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>({TResult Function( ErrorDetailsValidation value)?  validation,TResult Function( ErrorDetailsNotFound value)?  notFound,required TResult orElse(),}){
final _that = this;
switch (_that) {
case ErrorDetailsValidation() when validation != null:
return validation(_that);case ErrorDetailsNotFound() when notFound != null:
return notFound(_that);case _:
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

@optionalTypeArgs TResult map<TResult extends Object?>({required TResult Function( ErrorDetailsValidation value)  validation,required TResult Function( ErrorDetailsNotFound value)  notFound,}){
final _that = this;
switch (_that) {
case ErrorDetailsValidation():
return validation(_that);case ErrorDetailsNotFound():
return notFound(_that);}
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>({TResult? Function( ErrorDetailsValidation value)?  validation,TResult? Function( ErrorDetailsNotFound value)?  notFound,}){
final _that = this;
switch (_that) {
case ErrorDetailsValidation() when validation != null:
return validation(_that);case ErrorDetailsNotFound() when notFound != null:
return notFound(_that);case _:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>({TResult Function( List<FieldError> fieldErrors)?  validation,TResult Function( String entityType,  String searchedBy,  String id)?  notFound,required TResult orElse(),}) {final _that = this;
switch (_that) {
case ErrorDetailsValidation() when validation != null:
return validation(_that.fieldErrors);case ErrorDetailsNotFound() when notFound != null:
return notFound(_that.entityType,_that.searchedBy,_that.id);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>({required TResult Function( List<FieldError> fieldErrors)  validation,required TResult Function( String entityType,  String searchedBy,  String id)  notFound,}) {final _that = this;
switch (_that) {
case ErrorDetailsValidation():
return validation(_that.fieldErrors);case ErrorDetailsNotFound():
return notFound(_that.entityType,_that.searchedBy,_that.id);}
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>({TResult? Function( List<FieldError> fieldErrors)?  validation,TResult? Function( String entityType,  String searchedBy,  String id)?  notFound,}) {final _that = this;
switch (_that) {
case ErrorDetailsValidation() when validation != null:
return validation(_that.fieldErrors);case ErrorDetailsNotFound() when notFound != null:
return notFound(_that.entityType,_that.searchedBy,_that.id);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class ErrorDetailsValidation implements ErrorDetails {
  const ErrorDetailsValidation({required final  List<FieldError> fieldErrors, final  String? $type}): _fieldErrors = fieldErrors,$type = $type ?? 'VALIDATION';
  factory ErrorDetailsValidation.fromJson(Map<String, dynamic> json) => _$ErrorDetailsValidationFromJson(json);

/// Field Errors
 final  List<FieldError> _fieldErrors;
/// Field Errors
 List<FieldError> get fieldErrors {
  if (_fieldErrors is EqualUnmodifiableListView) return _fieldErrors;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_fieldErrors);
}


@JsonKey(name: 'type')
final String $type;


/// Create a copy of ErrorDetails
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ErrorDetailsValidationCopyWith<ErrorDetailsValidation> get copyWith => _$ErrorDetailsValidationCopyWithImpl<ErrorDetailsValidation>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ErrorDetailsValidationToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ErrorDetailsValidation&&const DeepCollectionEquality().equals(other._fieldErrors, _fieldErrors));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,const DeepCollectionEquality().hash(_fieldErrors));

@override
String toString() {
  return 'ErrorDetails.validation(fieldErrors: $fieldErrors)';
}


}

/// @nodoc
abstract mixin class $ErrorDetailsValidationCopyWith<$Res> implements $ErrorDetailsCopyWith<$Res> {
  factory $ErrorDetailsValidationCopyWith(ErrorDetailsValidation value, $Res Function(ErrorDetailsValidation) _then) = _$ErrorDetailsValidationCopyWithImpl;
@useResult
$Res call({
 List<FieldError> fieldErrors
});




}
/// @nodoc
class _$ErrorDetailsValidationCopyWithImpl<$Res>
    implements $ErrorDetailsValidationCopyWith<$Res> {
  _$ErrorDetailsValidationCopyWithImpl(this._self, this._then);

  final ErrorDetailsValidation _self;
  final $Res Function(ErrorDetailsValidation) _then;

/// Create a copy of ErrorDetails
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') $Res call({Object? fieldErrors = null,}) {
  return _then(ErrorDetailsValidation(
fieldErrors: null == fieldErrors ? _self._fieldErrors : fieldErrors // ignore: cast_nullable_to_non_nullable
as List<FieldError>,
  ));
}


}

/// @nodoc
@JsonSerializable()

class ErrorDetailsNotFound implements ErrorDetails {
  const ErrorDetailsNotFound({required this.entityType, required this.searchedBy, required this.id, final  String? $type}): $type = $type ?? 'NOT_FOUND';
  factory ErrorDetailsNotFound.fromJson(Map<String, dynamic> json) => _$ErrorDetailsNotFoundFromJson(json);

/// Entity type
 final  String entityType;
/// Search type
 final  String searchedBy;
/// id/slug
 final  String id;

@JsonKey(name: 'type')
final String $type;


/// Create a copy of ErrorDetails
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ErrorDetailsNotFoundCopyWith<ErrorDetailsNotFound> get copyWith => _$ErrorDetailsNotFoundCopyWithImpl<ErrorDetailsNotFound>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ErrorDetailsNotFoundToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ErrorDetailsNotFound&&(identical(other.entityType, entityType) || other.entityType == entityType)&&(identical(other.searchedBy, searchedBy) || other.searchedBy == searchedBy)&&(identical(other.id, id) || other.id == id));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,entityType,searchedBy,id);

@override
String toString() {
  return 'ErrorDetails.notFound(entityType: $entityType, searchedBy: $searchedBy, id: $id)';
}


}

/// @nodoc
abstract mixin class $ErrorDetailsNotFoundCopyWith<$Res> implements $ErrorDetailsCopyWith<$Res> {
  factory $ErrorDetailsNotFoundCopyWith(ErrorDetailsNotFound value, $Res Function(ErrorDetailsNotFound) _then) = _$ErrorDetailsNotFoundCopyWithImpl;
@useResult
$Res call({
 String entityType, String searchedBy, String id
});




}
/// @nodoc
class _$ErrorDetailsNotFoundCopyWithImpl<$Res>
    implements $ErrorDetailsNotFoundCopyWith<$Res> {
  _$ErrorDetailsNotFoundCopyWithImpl(this._self, this._then);

  final ErrorDetailsNotFound _self;
  final $Res Function(ErrorDetailsNotFound) _then;

/// Create a copy of ErrorDetails
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') $Res call({Object? entityType = null,Object? searchedBy = null,Object? id = null,}) {
  return _then(ErrorDetailsNotFound(
entityType: null == entityType ? _self.entityType : entityType // ignore: cast_nullable_to_non_nullable
as String,searchedBy: null == searchedBy ? _self.searchedBy : searchedBy // ignore: cast_nullable_to_non_nullable
as String,id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
