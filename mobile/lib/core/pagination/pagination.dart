/// Cursorless infinite-scroll pagination shared by the app's lists.
///
/// - [PagedListState] holds the four states a paginated list can be in.
/// - [PagedListNotifier] drives them (one request in flight, stale-response
///   guard, footer-scoped errors).
/// - [PagedListFooter] renders the end of the list.
library;

export 'paged_list_footer.dart';
export 'paged_list_notifier.dart';
export 'paged_list_state.dart';
