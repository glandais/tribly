// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'thumbnail_owner_report.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ThumbnailOwnerReport {

/// Entity ID
 String get id;/// Entity kind
 String get kind;/// Team slug
 String get teamSlug;/// Entity slug
 String get slug;/// Why it was selected
 List<ThumbnailRegenerationReason> get reasons;/// Its thumbnails before
 List<ThumbnailFile> get before;/// What happened
 String get outcome;/// Its thumbnails after, absent on a dry run
 List<ThumbnailFile>? get after;/// Error message when the regeneration itself failed
 String? get error;
/// Create a copy of ThumbnailOwnerReport
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ThumbnailOwnerReportCopyWith<ThumbnailOwnerReport> get copyWith => _$ThumbnailOwnerReportCopyWithImpl<ThumbnailOwnerReport>(this as ThumbnailOwnerReport, _$identity);

  /// Serializes this ThumbnailOwnerReport to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ThumbnailOwnerReport;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ThumbnailOwnerReport&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.kind, _this.kind) || other.kind == _this.kind)&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.slug, _this.slug) || other.slug == _this.slug)&&const DeepCollectionEquality().equals(other.reasons, _this.reasons)&&const DeepCollectionEquality().equals(other.before, _this.before)&&(identical(other.outcome, _this.outcome) || other.outcome == _this.outcome)&&const DeepCollectionEquality().equals(other.after, _this.after)&&(identical(other.error, _this.error) || other.error == _this.error));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ThumbnailOwnerReport;
  return Object.hash(runtimeType,_this.id,_this.kind,_this.teamSlug,_this.slug,const DeepCollectionEquality().hash(_this.reasons),const DeepCollectionEquality().hash(_this.before),_this.outcome,const DeepCollectionEquality().hash(_this.after),_this.error);
}

@override
String toString() {
  final _this = this as ThumbnailOwnerReport;
  return 'ThumbnailOwnerReport(id: ${_this.id}, kind: ${_this.kind}, teamSlug: ${_this.teamSlug}, slug: ${_this.slug}, reasons: ${_this.reasons}, before: ${_this.before}, outcome: ${_this.outcome}, after: ${_this.after}, error: ${_this.error})';
}


}

/// @nodoc
abstract mixin class $ThumbnailOwnerReportCopyWith<$Res>  {
  factory $ThumbnailOwnerReportCopyWith(ThumbnailOwnerReport value, $Res Function(ThumbnailOwnerReport) _then) = _$ThumbnailOwnerReportCopyWithImpl;
@useResult
$Res call({
 String id, String kind, String teamSlug, String slug, List<ThumbnailRegenerationReason> reasons, List<ThumbnailFile> before, String outcome, List<ThumbnailFile>? after, String? error
});




}
/// @nodoc
class _$ThumbnailOwnerReportCopyWithImpl<$Res>
    implements $ThumbnailOwnerReportCopyWith<$Res> {
  _$ThumbnailOwnerReportCopyWithImpl(this._self, this._then);

  final ThumbnailOwnerReport _self;
  final $Res Function(ThumbnailOwnerReport) _then;

/// Create a copy of ThumbnailOwnerReport
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? kind = null,Object? teamSlug = null,Object? slug = null,Object? reasons = null,Object? before = null,Object? outcome = null,Object? after = freezed,Object? error = freezed,}) {
  return _then(ThumbnailOwnerReport(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,kind: null == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,reasons: null == reasons ? _self.reasons : reasons // ignore: cast_nullable_to_non_nullable
as List<ThumbnailRegenerationReason>,before: null == before ? _self.before : before // ignore: cast_nullable_to_non_nullable
as List<ThumbnailFile>,outcome: null == outcome ? _self.outcome : outcome // ignore: cast_nullable_to_non_nullable
as String,after: freezed == after ? _self.after : after // ignore: cast_nullable_to_non_nullable
as List<ThumbnailFile>?,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [ThumbnailOwnerReport].
extension ThumbnailOwnerReportPatterns on ThumbnailOwnerReport {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ThumbnailOwnerReport value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ThumbnailOwnerReport() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ThumbnailOwnerReport value)  $default,){
final _that = this;
switch (_that) {
case _ThumbnailOwnerReport():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ThumbnailOwnerReport value)?  $default,){
final _that = this;
switch (_that) {
case _ThumbnailOwnerReport() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String kind,  String teamSlug,  String slug,  List<ThumbnailRegenerationReason> reasons,  List<ThumbnailFile> before,  String outcome,  List<ThumbnailFile>? after,  String? error)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ThumbnailOwnerReport() when $default != null:
return $default(_that.id,_that.kind,_that.teamSlug,_that.slug,_that.reasons,_that.before,_that.outcome,_that.after,_that.error);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String kind,  String teamSlug,  String slug,  List<ThumbnailRegenerationReason> reasons,  List<ThumbnailFile> before,  String outcome,  List<ThumbnailFile>? after,  String? error)  $default,) {final _that = this;
switch (_that) {
case _ThumbnailOwnerReport():
return $default(_that.id,_that.kind,_that.teamSlug,_that.slug,_that.reasons,_that.before,_that.outcome,_that.after,_that.error);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String kind,  String teamSlug,  String slug,  List<ThumbnailRegenerationReason> reasons,  List<ThumbnailFile> before,  String outcome,  List<ThumbnailFile>? after,  String? error)?  $default,) {final _that = this;
switch (_that) {
case _ThumbnailOwnerReport() when $default != null:
return $default(_that.id,_that.kind,_that.teamSlug,_that.slug,_that.reasons,_that.before,_that.outcome,_that.after,_that.error);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ThumbnailOwnerReport implements ThumbnailOwnerReport {
  const _ThumbnailOwnerReport({required this.id, required this.kind, required this.teamSlug, required this.slug, required  List<ThumbnailRegenerationReason> reasons, required  List<ThumbnailFile> before, required this.outcome,  List<ThumbnailFile>? after, this.error}): _reasons = reasons,_before = before,_after = after;
  factory _ThumbnailOwnerReport.fromJson(Map<String, dynamic> json) => _$ThumbnailOwnerReportFromJson(json);

/// Entity ID
@override final  String id;
/// Entity kind
@override final  String kind;
/// Team slug
@override final  String teamSlug;
/// Entity slug
@override final  String slug;
/// Why it was selected
 final  List<ThumbnailRegenerationReason> _reasons;
/// Why it was selected
@override List<ThumbnailRegenerationReason> get reasons {
  if (_reasons is EqualUnmodifiableListView) return _reasons;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_reasons);
}

/// Its thumbnails before
 final  List<ThumbnailFile> _before;
/// Its thumbnails before
@override List<ThumbnailFile> get before {
  if (_before is EqualUnmodifiableListView) return _before;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_before);
}

/// What happened
@override final  String outcome;
/// Its thumbnails after, absent on a dry run
 final  List<ThumbnailFile>? _after;
/// Its thumbnails after, absent on a dry run
@override List<ThumbnailFile>? get after {
  final value = _after;
  if (value == null) return null;
  if (_after is EqualUnmodifiableListView) return _after;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

/// Error message when the regeneration itself failed
@override final  String? error;

/// Create a copy of ThumbnailOwnerReport
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ThumbnailOwnerReportCopyWith<_ThumbnailOwnerReport> get copyWith => __$ThumbnailOwnerReportCopyWithImpl<_ThumbnailOwnerReport>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ThumbnailOwnerReportToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ThumbnailOwnerReport&&(identical(other.id, id) || other.id == id)&&(identical(other.kind, kind) || other.kind == kind)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.slug, slug) || other.slug == slug)&&const DeepCollectionEquality().equals(other.reasons, _reasons)&&const DeepCollectionEquality().equals(other.before, _before)&&(identical(other.outcome, outcome) || other.outcome == outcome)&&const DeepCollectionEquality().equals(other.after, _after)&&(identical(other.error, error) || other.error == error));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,id,kind,teamSlug,slug,const DeepCollectionEquality().hash(_reasons),const DeepCollectionEquality().hash(_before),outcome,const DeepCollectionEquality().hash(_after),error);
}

@override
String toString() {
    return 'ThumbnailOwnerReport(id: $id, kind: $kind, teamSlug: $teamSlug, slug: $slug, reasons: $reasons, before: $before, outcome: $outcome, after: $after, error: $error)';
}


}

/// @nodoc
abstract mixin class _$ThumbnailOwnerReportCopyWith<$Res> implements $ThumbnailOwnerReportCopyWith<$Res> {
  factory _$ThumbnailOwnerReportCopyWith(_ThumbnailOwnerReport value, $Res Function(_ThumbnailOwnerReport) _then) = __$ThumbnailOwnerReportCopyWithImpl;
@override @useResult
$Res call({
 String id, String kind, String teamSlug, String slug, List<ThumbnailRegenerationReason> reasons, List<ThumbnailFile> before, String outcome, List<ThumbnailFile>? after, String? error
});




}
/// @nodoc
class __$ThumbnailOwnerReportCopyWithImpl<$Res>
    implements _$ThumbnailOwnerReportCopyWith<$Res> {
  __$ThumbnailOwnerReportCopyWithImpl(this._self, this._then);

  final _ThumbnailOwnerReport _self;
  final $Res Function(_ThumbnailOwnerReport) _then;

/// Create a copy of ThumbnailOwnerReport
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? kind = null,Object? teamSlug = null,Object? slug = null,Object? reasons = null,Object? before = null,Object? outcome = null,Object? after = freezed,Object? error = freezed,}) {
  return _then(_ThumbnailOwnerReport(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,kind: null == kind ? _self.kind : kind // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,slug: null == slug ? _self.slug : slug // ignore: cast_nullable_to_non_nullable
as String,reasons: null == reasons ? _self._reasons : reasons // ignore: cast_nullable_to_non_nullable
as List<ThumbnailRegenerationReason>,before: null == before ? _self._before : before // ignore: cast_nullable_to_non_nullable
as List<ThumbnailFile>,outcome: null == outcome ? _self.outcome : outcome // ignore: cast_nullable_to_non_nullable
as String,after: freezed == after ? _self._after : after // ignore: cast_nullable_to_non_nullable
as List<ThumbnailFile>?,error: freezed == error ? _self.error : error // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
