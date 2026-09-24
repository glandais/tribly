// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'moderation_item_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ModerationItemDto {

/// Type of the reported target
 String get targetType;/// ID (TSID) of the reported target
 String get targetId;/// Slug of the team the reports were filed in
 String get teamSlug;/// Name of that team
 String get teamName;/// Who the moderation is about: the author of the content, or the member
 PublicUserDto get targetUser;/// How many reports this target gathered
 int get reportCount;/// The distinct reasons given
 List<ReportReason> get reasons;/// The non-empty free texts of the reports
 List<String> get messages;/// When the first report was filed
 String get firstReportedAt;/// When the last report was filed
 String get lastReportedAt;/// Whether the content is currently hidden from members, having gathered enough reports
 bool get hidden;/// OPEN while waiting; REMOVED or DISMISSED once decided (the latest decision)
 String get status;/// Name of the publication — of the commented one for a comment. Null for a member, or when the content is gone.
 String? get contentName;/// Type of the content to open: the publication itself, or the one a comment is on (POST, RIDE, TRIP, ROUTE or AD). Null for a member, or when the content is gone.
 String? get contentType;/// Slug of the content to open, with contentType
 String? get contentSlug;/// The reported text as it was when first reported (comment text, name and start of the description, or member name)
 String? get excerpt;/// Who reported. Only in the platform queue: always null in a team's queue, where reporters stay anonymous.
 List<PublicUserDto>? get reporters;
/// Create a copy of ModerationItemDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ModerationItemDtoCopyWith<ModerationItemDto> get copyWith => _$ModerationItemDtoCopyWithImpl<ModerationItemDto>(this as ModerationItemDto, _$identity);

  /// Serializes this ModerationItemDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ModerationItemDto;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ModerationItemDto&&(identical(other.targetType, _this.targetType) || other.targetType == _this.targetType)&&(identical(other.targetId, _this.targetId) || other.targetId == _this.targetId)&&(identical(other.teamSlug, _this.teamSlug) || other.teamSlug == _this.teamSlug)&&(identical(other.teamName, _this.teamName) || other.teamName == _this.teamName)&&(identical(other.targetUser, _this.targetUser) || other.targetUser == _this.targetUser)&&(identical(other.reportCount, _this.reportCount) || other.reportCount == _this.reportCount)&&const DeepCollectionEquality().equals(other.reasons, _this.reasons)&&const DeepCollectionEquality().equals(other.messages, _this.messages)&&(identical(other.firstReportedAt, _this.firstReportedAt) || other.firstReportedAt == _this.firstReportedAt)&&(identical(other.lastReportedAt, _this.lastReportedAt) || other.lastReportedAt == _this.lastReportedAt)&&(identical(other.hidden, _this.hidden) || other.hidden == _this.hidden)&&(identical(other.status, _this.status) || other.status == _this.status)&&(identical(other.contentName, _this.contentName) || other.contentName == _this.contentName)&&(identical(other.contentType, _this.contentType) || other.contentType == _this.contentType)&&(identical(other.contentSlug, _this.contentSlug) || other.contentSlug == _this.contentSlug)&&(identical(other.excerpt, _this.excerpt) || other.excerpt == _this.excerpt)&&const DeepCollectionEquality().equals(other.reporters, _this.reporters));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ModerationItemDto;
  return Object.hash(runtimeType,_this.targetType,_this.targetId,_this.teamSlug,_this.teamName,_this.targetUser,_this.reportCount,const DeepCollectionEquality().hash(_this.reasons),const DeepCollectionEquality().hash(_this.messages),_this.firstReportedAt,_this.lastReportedAt,_this.hidden,_this.status,_this.contentName,_this.contentType,_this.contentSlug,_this.excerpt,const DeepCollectionEquality().hash(_this.reporters));
}

@override
String toString() {
  final _this = this as ModerationItemDto;
  return 'ModerationItemDto(targetType: ${_this.targetType}, targetId: ${_this.targetId}, teamSlug: ${_this.teamSlug}, teamName: ${_this.teamName}, targetUser: ${_this.targetUser}, reportCount: ${_this.reportCount}, reasons: ${_this.reasons}, messages: ${_this.messages}, firstReportedAt: ${_this.firstReportedAt}, lastReportedAt: ${_this.lastReportedAt}, hidden: ${_this.hidden}, status: ${_this.status}, contentName: ${_this.contentName}, contentType: ${_this.contentType}, contentSlug: ${_this.contentSlug}, excerpt: ${_this.excerpt}, reporters: ${_this.reporters})';
}


}

/// @nodoc
abstract mixin class $ModerationItemDtoCopyWith<$Res>  {
  factory $ModerationItemDtoCopyWith(ModerationItemDto value, $Res Function(ModerationItemDto) _then) = _$ModerationItemDtoCopyWithImpl;
@useResult
$Res call({
 String targetType, String targetId, String teamSlug, String teamName, PublicUserDto targetUser, int reportCount, List<ReportReason> reasons, List<String> messages, String firstReportedAt, String lastReportedAt, bool hidden, String status, String? contentName, String? contentType, String? contentSlug, String? excerpt, List<PublicUserDto>? reporters
});


$PublicUserDtoCopyWith<$Res> get targetUser;

}
/// @nodoc
class _$ModerationItemDtoCopyWithImpl<$Res>
    implements $ModerationItemDtoCopyWith<$Res> {
  _$ModerationItemDtoCopyWithImpl(this._self, this._then);

  final ModerationItemDto _self;
  final $Res Function(ModerationItemDto) _then;

/// Create a copy of ModerationItemDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? targetType = null,Object? targetId = null,Object? teamSlug = null,Object? teamName = null,Object? targetUser = null,Object? reportCount = null,Object? reasons = null,Object? messages = null,Object? firstReportedAt = null,Object? lastReportedAt = null,Object? hidden = null,Object? status = null,Object? contentName = freezed,Object? contentType = freezed,Object? contentSlug = freezed,Object? excerpt = freezed,Object? reporters = freezed,}) {
  return _then(ModerationItemDto(
targetType: null == targetType ? _self.targetType : targetType // ignore: cast_nullable_to_non_nullable
as String,targetId: null == targetId ? _self.targetId : targetId // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,targetUser: null == targetUser ? _self.targetUser : targetUser // ignore: cast_nullable_to_non_nullable
as PublicUserDto,reportCount: null == reportCount ? _self.reportCount : reportCount // ignore: cast_nullable_to_non_nullable
as int,reasons: null == reasons ? _self.reasons : reasons // ignore: cast_nullable_to_non_nullable
as List<ReportReason>,messages: null == messages ? _self.messages : messages // ignore: cast_nullable_to_non_nullable
as List<String>,firstReportedAt: null == firstReportedAt ? _self.firstReportedAt : firstReportedAt // ignore: cast_nullable_to_non_nullable
as String,lastReportedAt: null == lastReportedAt ? _self.lastReportedAt : lastReportedAt // ignore: cast_nullable_to_non_nullable
as String,hidden: null == hidden ? _self.hidden : hidden // ignore: cast_nullable_to_non_nullable
as bool,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,contentName: freezed == contentName ? _self.contentName : contentName // ignore: cast_nullable_to_non_nullable
as String?,contentType: freezed == contentType ? _self.contentType : contentType // ignore: cast_nullable_to_non_nullable
as String?,contentSlug: freezed == contentSlug ? _self.contentSlug : contentSlug // ignore: cast_nullable_to_non_nullable
as String?,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,reporters: freezed == reporters ? _self.reporters : reporters // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>?,
  ));
}
/// Create a copy of ModerationItemDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get targetUser {
  
  return $PublicUserDtoCopyWith<$Res>(_self.targetUser, (value) {
    return _then(_self.copyWith(targetUser: value));
  });
}
}


/// Adds pattern-matching-related methods to [ModerationItemDto].
extension ModerationItemDtoPatterns on ModerationItemDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ModerationItemDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ModerationItemDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ModerationItemDto value)  $default,){
final _that = this;
switch (_that) {
case _ModerationItemDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ModerationItemDto value)?  $default,){
final _that = this;
switch (_that) {
case _ModerationItemDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String targetType,  String targetId,  String teamSlug,  String teamName,  PublicUserDto targetUser,  int reportCount,  List<ReportReason> reasons,  List<String> messages,  String firstReportedAt,  String lastReportedAt,  bool hidden,  String status,  String? contentName,  String? contentType,  String? contentSlug,  String? excerpt,  List<PublicUserDto>? reporters)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ModerationItemDto() when $default != null:
return $default(_that.targetType,_that.targetId,_that.teamSlug,_that.teamName,_that.targetUser,_that.reportCount,_that.reasons,_that.messages,_that.firstReportedAt,_that.lastReportedAt,_that.hidden,_that.status,_that.contentName,_that.contentType,_that.contentSlug,_that.excerpt,_that.reporters);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String targetType,  String targetId,  String teamSlug,  String teamName,  PublicUserDto targetUser,  int reportCount,  List<ReportReason> reasons,  List<String> messages,  String firstReportedAt,  String lastReportedAt,  bool hidden,  String status,  String? contentName,  String? contentType,  String? contentSlug,  String? excerpt,  List<PublicUserDto>? reporters)  $default,) {final _that = this;
switch (_that) {
case _ModerationItemDto():
return $default(_that.targetType,_that.targetId,_that.teamSlug,_that.teamName,_that.targetUser,_that.reportCount,_that.reasons,_that.messages,_that.firstReportedAt,_that.lastReportedAt,_that.hidden,_that.status,_that.contentName,_that.contentType,_that.contentSlug,_that.excerpt,_that.reporters);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String targetType,  String targetId,  String teamSlug,  String teamName,  PublicUserDto targetUser,  int reportCount,  List<ReportReason> reasons,  List<String> messages,  String firstReportedAt,  String lastReportedAt,  bool hidden,  String status,  String? contentName,  String? contentType,  String? contentSlug,  String? excerpt,  List<PublicUserDto>? reporters)?  $default,) {final _that = this;
switch (_that) {
case _ModerationItemDto() when $default != null:
return $default(_that.targetType,_that.targetId,_that.teamSlug,_that.teamName,_that.targetUser,_that.reportCount,_that.reasons,_that.messages,_that.firstReportedAt,_that.lastReportedAt,_that.hidden,_that.status,_that.contentName,_that.contentType,_that.contentSlug,_that.excerpt,_that.reporters);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ModerationItemDto implements ModerationItemDto {
  const _ModerationItemDto({required this.targetType, required this.targetId, required this.teamSlug, required this.teamName, required this.targetUser, required this.reportCount, required  List<ReportReason> reasons, required  List<String> messages, required this.firstReportedAt, required this.lastReportedAt, required this.hidden, required this.status, this.contentName, this.contentType, this.contentSlug, this.excerpt,  List<PublicUserDto>? reporters}): _reasons = reasons,_messages = messages,_reporters = reporters;
  factory _ModerationItemDto.fromJson(Map<String, dynamic> json) => _$ModerationItemDtoFromJson(json);

/// Type of the reported target
@override final  String targetType;
/// ID (TSID) of the reported target
@override final  String targetId;
/// Slug of the team the reports were filed in
@override final  String teamSlug;
/// Name of that team
@override final  String teamName;
/// Who the moderation is about: the author of the content, or the member
@override final  PublicUserDto targetUser;
/// How many reports this target gathered
@override final  int reportCount;
/// The distinct reasons given
 final  List<ReportReason> _reasons;
/// The distinct reasons given
@override List<ReportReason> get reasons {
  if (_reasons is EqualUnmodifiableListView) return _reasons;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_reasons);
}

/// The non-empty free texts of the reports
 final  List<String> _messages;
/// The non-empty free texts of the reports
@override List<String> get messages {
  if (_messages is EqualUnmodifiableListView) return _messages;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_messages);
}

/// When the first report was filed
@override final  String firstReportedAt;
/// When the last report was filed
@override final  String lastReportedAt;
/// Whether the content is currently hidden from members, having gathered enough reports
@override final  bool hidden;
/// OPEN while waiting; REMOVED or DISMISSED once decided (the latest decision)
@override final  String status;
/// Name of the publication — of the commented one for a comment. Null for a member, or when the content is gone.
@override final  String? contentName;
/// Type of the content to open: the publication itself, or the one a comment is on (POST, RIDE, TRIP, ROUTE or AD). Null for a member, or when the content is gone.
@override final  String? contentType;
/// Slug of the content to open, with contentType
@override final  String? contentSlug;
/// The reported text as it was when first reported (comment text, name and start of the description, or member name)
@override final  String? excerpt;
/// Who reported. Only in the platform queue: always null in a team's queue, where reporters stay anonymous.
 final  List<PublicUserDto>? _reporters;
/// Who reported. Only in the platform queue: always null in a team's queue, where reporters stay anonymous.
@override List<PublicUserDto>? get reporters {
  final value = _reporters;
  if (value == null) return null;
  if (_reporters is EqualUnmodifiableListView) return _reporters;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}


/// Create a copy of ModerationItemDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ModerationItemDtoCopyWith<_ModerationItemDto> get copyWith => __$ModerationItemDtoCopyWithImpl<_ModerationItemDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ModerationItemDtoToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ModerationItemDto&&(identical(other.targetType, targetType) || other.targetType == targetType)&&(identical(other.targetId, targetId) || other.targetId == targetId)&&(identical(other.teamSlug, teamSlug) || other.teamSlug == teamSlug)&&(identical(other.teamName, teamName) || other.teamName == teamName)&&(identical(other.targetUser, targetUser) || other.targetUser == targetUser)&&(identical(other.reportCount, reportCount) || other.reportCount == reportCount)&&const DeepCollectionEquality().equals(other.reasons, _reasons)&&const DeepCollectionEquality().equals(other.messages, _messages)&&(identical(other.firstReportedAt, firstReportedAt) || other.firstReportedAt == firstReportedAt)&&(identical(other.lastReportedAt, lastReportedAt) || other.lastReportedAt == lastReportedAt)&&(identical(other.hidden, hidden) || other.hidden == hidden)&&(identical(other.status, status) || other.status == status)&&(identical(other.contentName, contentName) || other.contentName == contentName)&&(identical(other.contentType, contentType) || other.contentType == contentType)&&(identical(other.contentSlug, contentSlug) || other.contentSlug == contentSlug)&&(identical(other.excerpt, excerpt) || other.excerpt == excerpt)&&const DeepCollectionEquality().equals(other.reporters, _reporters));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,targetType,targetId,teamSlug,teamName,targetUser,reportCount,const DeepCollectionEquality().hash(_reasons),const DeepCollectionEquality().hash(_messages),firstReportedAt,lastReportedAt,hidden,status,contentName,contentType,contentSlug,excerpt,const DeepCollectionEquality().hash(_reporters));
}

@override
String toString() {
    return 'ModerationItemDto(targetType: $targetType, targetId: $targetId, teamSlug: $teamSlug, teamName: $teamName, targetUser: $targetUser, reportCount: $reportCount, reasons: $reasons, messages: $messages, firstReportedAt: $firstReportedAt, lastReportedAt: $lastReportedAt, hidden: $hidden, status: $status, contentName: $contentName, contentType: $contentType, contentSlug: $contentSlug, excerpt: $excerpt, reporters: $reporters)';
}


}

/// @nodoc
abstract mixin class _$ModerationItemDtoCopyWith<$Res> implements $ModerationItemDtoCopyWith<$Res> {
  factory _$ModerationItemDtoCopyWith(_ModerationItemDto value, $Res Function(_ModerationItemDto) _then) = __$ModerationItemDtoCopyWithImpl;
@override @useResult
$Res call({
 String targetType, String targetId, String teamSlug, String teamName, PublicUserDto targetUser, int reportCount, List<ReportReason> reasons, List<String> messages, String firstReportedAt, String lastReportedAt, bool hidden, String status, String? contentName, String? contentType, String? contentSlug, String? excerpt, List<PublicUserDto>? reporters
});


@override $PublicUserDtoCopyWith<$Res> get targetUser;

}
/// @nodoc
class __$ModerationItemDtoCopyWithImpl<$Res>
    implements _$ModerationItemDtoCopyWith<$Res> {
  __$ModerationItemDtoCopyWithImpl(this._self, this._then);

  final _ModerationItemDto _self;
  final $Res Function(_ModerationItemDto) _then;

/// Create a copy of ModerationItemDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? targetType = null,Object? targetId = null,Object? teamSlug = null,Object? teamName = null,Object? targetUser = null,Object? reportCount = null,Object? reasons = null,Object? messages = null,Object? firstReportedAt = null,Object? lastReportedAt = null,Object? hidden = null,Object? status = null,Object? contentName = freezed,Object? contentType = freezed,Object? contentSlug = freezed,Object? excerpt = freezed,Object? reporters = freezed,}) {
  return _then(_ModerationItemDto(
targetType: null == targetType ? _self.targetType : targetType // ignore: cast_nullable_to_non_nullable
as String,targetId: null == targetId ? _self.targetId : targetId // ignore: cast_nullable_to_non_nullable
as String,teamSlug: null == teamSlug ? _self.teamSlug : teamSlug // ignore: cast_nullable_to_non_nullable
as String,teamName: null == teamName ? _self.teamName : teamName // ignore: cast_nullable_to_non_nullable
as String,targetUser: null == targetUser ? _self.targetUser : targetUser // ignore: cast_nullable_to_non_nullable
as PublicUserDto,reportCount: null == reportCount ? _self.reportCount : reportCount // ignore: cast_nullable_to_non_nullable
as int,reasons: null == reasons ? _self._reasons : reasons // ignore: cast_nullable_to_non_nullable
as List<ReportReason>,messages: null == messages ? _self._messages : messages // ignore: cast_nullable_to_non_nullable
as List<String>,firstReportedAt: null == firstReportedAt ? _self.firstReportedAt : firstReportedAt // ignore: cast_nullable_to_non_nullable
as String,lastReportedAt: null == lastReportedAt ? _self.lastReportedAt : lastReportedAt // ignore: cast_nullable_to_non_nullable
as String,hidden: null == hidden ? _self.hidden : hidden // ignore: cast_nullable_to_non_nullable
as bool,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,contentName: freezed == contentName ? _self.contentName : contentName // ignore: cast_nullable_to_non_nullable
as String?,contentType: freezed == contentType ? _self.contentType : contentType // ignore: cast_nullable_to_non_nullable
as String?,contentSlug: freezed == contentSlug ? _self.contentSlug : contentSlug // ignore: cast_nullable_to_non_nullable
as String?,excerpt: freezed == excerpt ? _self.excerpt : excerpt // ignore: cast_nullable_to_non_nullable
as String?,reporters: freezed == reporters ? _self._reporters : reporters // ignore: cast_nullable_to_non_nullable
as List<PublicUserDto>?,
  ));
}

/// Create a copy of ModerationItemDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PublicUserDtoCopyWith<$Res> get targetUser {
  
  return $PublicUserDtoCopyWith<$Res>(_self.targetUser, (value) {
    return _then(_self.copyWith(targetUser: value));
  });
}
}

// dart format on
