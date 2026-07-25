import 'dart:async';

import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pagination/pagination.dart';

/// A notifier whose pages are handed to it by the test.
class _FakeNotifier extends PagedListNotifier<String> {
  _FakeNotifier(this.responder, {super.pageSize = 3});

  final Future<PageResult<String>> Function(int page) responder;
  final List<int> requestedPages = [];

  @override
  Future<PageResult<String>> fetchPage(int page) {
    requestedPages.add(page);
    return responder(page);
  }

  @override
  Object? itemKey(String item) => item;
}

PageResult<String> _page(List<String> items, int total) =>
    PageResult(items: items, total: total);

void main() {
  group('PagedListNotifier', () {
    test('loads the first page on creation', () async {
      final notifier = _FakeNotifier((page) async => _page(['a', 'b', 'c'], 6));
      await pumpEventQueue();

      expect(notifier.requestedPages, [0]);
      expect(notifier.state.items, ['a', 'b', 'c']);
      expect(notifier.state.total, 6);
      expect(notifier.state.isLoadingInitial, isFalse);
      expect(notifier.state.hasMore, isTrue);
    });

    test('appends the next page and stops at the total', () async {
      final notifier = _FakeNotifier(
        (page) async =>
            page == 0 ? _page(['a', 'b', 'c'], 5) : _page(['d', 'e'], 5),
      );
      await pumpEventQueue();

      await notifier.loadNextPage();

      expect(notifier.requestedPages, [0, 1]);
      expect(notifier.state.items, ['a', 'b', 'c', 'd', 'e']);
      expect(notifier.state.hasMore, isFalse);
      expect(notifier.state.isLoadingNext, isFalse);
    });

    test('drops items already loaded, so an offset shift cannot duplicate '
        'them', () async {
      final notifier = _FakeNotifier(
        (page) async =>
            page == 0 ? _page(['a', 'b', 'c'], 9) : _page(['c', 'd'], 9),
      );
      await pumpEventQueue();

      await notifier.loadNextPage();

      expect(notifier.state.items, ['a', 'b', 'c', 'd']);
    });

    test('an empty page ends the list even when the total disagrees', () async {
      final notifier = _FakeNotifier(
        (page) async => page == 0 ? _page(['a'], 50) : _page([], 50),
      );
      await pumpEventQueue();

      await notifier.loadNextPage();

      expect(notifier.state.hasMore, isFalse);
      expect(notifier.state.items, ['a']);
    });

    test('a next-page failure keeps the loaded items and only fills '
        'nextError', () async {
      final notifier = _FakeNotifier((page) async {
        if (page == 0) return _page(['a', 'b', 'c'], 9);
        throw StateError('offline');
      });
      await pumpEventQueue();

      await notifier.loadNextPage();

      expect(notifier.state.items, ['a', 'b', 'c']);
      expect(notifier.state.nextError, isA<StateError>());
      expect(notifier.state.initialError, isNull);
    });

    test('scrolling does not retry a failed page; the retry does', () async {
      var failNext = true;
      final notifier = _FakeNotifier((page) async {
        if (page == 0) return _page(['a', 'b', 'c'], 9);
        if (failNext) throw StateError('offline');
        return _page(['d', 'e', 'f'], 9);
      });
      await pumpEventQueue();
      await notifier.loadNextPage();
      expect(notifier.requestedPages, [0, 1]);

      // Further scrolling must not hammer the failing endpoint.
      notifier.onItemBuilt(2);
      await notifier.loadNextPage();
      await pumpEventQueue();
      expect(notifier.requestedPages, [0, 1]);

      failNext = false;
      await notifier.retryNextPage();

      expect(notifier.requestedPages, [0, 1, 1]);
      expect(notifier.state.items, ['a', 'b', 'c', 'd', 'e', 'f']);
      expect(notifier.state.nextError, isNull);
    });

    test('a first-page failure surfaces as initialError', () async {
      final notifier = _FakeNotifier((page) async => throw StateError('boom'));
      await pumpEventQueue();

      expect(notifier.state.initialError, isA<StateError>());
      expect(notifier.state.items, isEmpty);
      expect(notifier.state.isLoadingInitial, isFalse);
    });

    test('a failed refresh keeps the list on screen', () async {
      var fail = false;
      final notifier = _FakeNotifier((page) async {
        if (fail) throw StateError('offline');
        return _page(['a', 'b', 'c'], 9);
      });
      await pumpEventQueue();

      fail = true;
      await notifier.refresh();

      expect(notifier.state.items, ['a', 'b', 'c']);
      expect(notifier.state.initialError, isNull);
      expect(notifier.state.nextError, isA<StateError>());
    });

    test('only one request is in flight at a time', () async {
      final completers = <int, Completer<PageResult<String>>>{};
      final notifier = _FakeNotifier((page) {
        return (completers[page] ??= Completer<PageResult<String>>()).future;
      });
      await pumpEventQueue();

      // The first page has not landed yet: asking for the next one is a no-op.
      unawaited(notifier.loadNextPage());
      await pumpEventQueue();
      expect(notifier.requestedPages, [0]);

      completers[0]!.complete(_page(['a', 'b', 'c'], 9));
      await pumpEventQueue();

      unawaited(notifier.loadNextPage());
      unawaited(notifier.loadNextPage());
      await pumpEventQueue();
      expect(notifier.requestedPages, [0, 1]);
    });

    test('a response from before a refresh is discarded', () async {
      final completers = <int, Completer<PageResult<String>>>{};
      var attempt = 0;
      final notifier = _FakeNotifier((page) {
        return (completers[attempt++] ??= Completer<PageResult<String>>())
            .future;
      });
      await pumpEventQueue();

      // Refresh before the initial load answers.
      unawaited(notifier.refresh());
      await pumpEventQueue();

      completers[1]!.complete(_page(['fresh'], 1));
      await pumpEventQueue();
      completers[0]!.complete(_page(['stale'], 1));
      await pumpEventQueue();

      expect(notifier.state.items, ['fresh']);
    });

    test('prefetch fires within three items of the end, not before', () async {
      final notifier = _FakeNotifier(
        (page) async => _page(['a', 'b', 'c', 'd', 'e', 'f'], 20),
        pageSize: 6,
      );
      await pumpEventQueue();

      notifier.onItemBuilt(1);
      await pumpEventQueue();
      expect(notifier.requestedPages, [0]);

      notifier.onItemBuilt(3);
      await pumpEventQueue();
      expect(notifier.requestedPages, [0, 1]);
    });
  });
}
