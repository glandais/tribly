import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/utils/formatters.dart';
import '../../data/calendar_repository.dart';

final calendarEventsProvider = FutureProvider.family<List<CalendarEventDto>,
    ({DateTime start, DateTime end, String? teamSlug})>((ref, params) async {
  final repository = ref.watch(calendarRepositoryProvider);
  if (params.teamSlug != null) {
    return repository.getTeamCalendarEvents(
      params.teamSlug!,
      start: params.start,
      end: params.end,
    );
  }
  return repository.getMyCalendarEvents(start: params.start, end: params.end);
});

class CalendarPage extends ConsumerStatefulWidget {
  final String? teamSlug;

  const CalendarPage({super.key, this.teamSlug});

  @override
  ConsumerState<CalendarPage> createState() => _CalendarPageState();
}

class _CalendarPageState extends ConsumerState<CalendarPage> {
  DateTime _selectedMonth = DateTime.now();
  bool _initialMonthResolved = false;

  DateTime get _monthStart =>
      DateTime(_selectedMonth.year, _selectedMonth.month, 1);
  DateTime get _monthEnd =>
      DateTime(_selectedMonth.year, _selectedMonth.month + 1, 0, 23, 59, 59);

  @override
  void initState() {
    super.initState();
    _findFirstMonthWithEvents();
  }

  Future<void> _findFirstMonthWithEvents() async {
    final now = DateTime.now();
    final repository = ref.read(calendarRepositoryProvider);

    for (int i = 0; i < 6; i++) {
      final month = DateTime(now.year, now.month + i, 1);
      final start = i == 0 ? now : month;
      final end = DateTime(month.year, month.month + 1, 0, 23, 59, 59);

      try {
        final List<CalendarEventDto> events;
        if (widget.teamSlug != null) {
          events = await repository.getTeamCalendarEvents(
            widget.teamSlug!,
            start: start,
            end: end,
          );
        } else {
          events = await repository.getMyCalendarEvents(
            start: start,
            end: end,
          );
        }
        if (events.isNotEmpty) {
          if (mounted && !_initialMonthResolved) {
            setState(() {
              _selectedMonth = month;
              _initialMonthResolved = true;
            });
          }
          return;
        }
      } catch (_) {
        break;
      }
    }
    if (mounted) {
      setState(() => _initialMonthResolved = true);
    }
  }

  @override
  Widget build(BuildContext context) {
    final params = (start: _monthStart, end: _monthEnd, teamSlug: widget.teamSlug);
    final eventsAsync = ref.watch(calendarEventsProvider(params));

    return Scaffold(
      appBar: AppBar(
        title: Text('calendar.title'.tr()),
      ),
      body: Column(
        children: [
          // Month selector
          ContentWidthConstraint(
            child: _MonthSelector(
              selectedMonth: _selectedMonth,
              onPreviousMonth: () {
                setState(() {
                  _selectedMonth =
                      DateTime(_selectedMonth.year, _selectedMonth.month - 1, 1);
                });
              },
              onNextMonth: () {
                setState(() {
                  _selectedMonth =
                      DateTime(_selectedMonth.year, _selectedMonth.month + 1, 1);
                });
              },
            ),
          ),

          // Events list
          Expanded(
            child: eventsAsync.when(
              data: (events) {
                if (events.isEmpty) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        AnimatedEmptyState(
                          child: Icon(
                            Icons.event_busy,
                            size: 64,
                            color: Theme.of(context).colorScheme.outline,
                          ),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'calendar.noEvents'.tr(),
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                      ],
                    ),
                  );
                }

                // Group events by date
                final groupedEvents = <DateTime, List<CalendarEventDto>>{};
                for (final event in events) {
                  final startDate = DateTime.parse(event.start);
                  final date = DateTime(
                    startDate.year,
                    startDate.month,
                    startDate.day,
                  );
                  groupedEvents.putIfAbsent(date, () => []).add(event);
                }

                final sortedDates = groupedEvents.keys.toList()..sort();

                return RefreshIndicator(
                  onRefresh: () => ref.refresh(calendarEventsProvider(params).future),
                  child: StaggeredListView(
                    padding: const EdgeInsets.all(16),
                    itemCount: sortedDates.length,
                    itemBuilder: (context, index) {
                      final date = sortedDates[index];
                      final dayEvents = groupedEvents[date]!;
                      return ContentWidthConstraint(
                        child: _DaySection(date: date, events: dayEvents),
                      );
                    },
                  ),
                );
              },
              loading: () => Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Day header placeholder
                    Row(
                      children: [
                        ShimmerPlaceholder(
                          width: 48,
                          height: 48,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        const SizedBox(width: 12),
                        ShimmerPlaceholder.text(width: 100, height: 16),
                      ],
                    ),
                    const SizedBox(height: 16),
                    // Event card placeholders
                    ...List.generate(
                      3,
                      (index) => const Padding(
                        padding: EdgeInsets.only(left: 60, bottom: 8),
                        child: ShimmerEventCard(),
                      ),
                    ),
                  ],
                ),
              ),
              error: (error, stack) => Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.error_outline, size: 48, color: Theme.of(context).colorScheme.error),
                    const SizedBox(height: 16),
                    Text(getErrorMessage(error)),
                    const SizedBox(height: 16),
                    FilledButton(
                      onPressed: () =>
                          ref.invalidate(calendarEventsProvider(params)),
                      child: Text('common.retry'.tr()),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _MonthSelector extends StatelessWidget {
  final DateTime selectedMonth;
  final VoidCallback onPreviousMonth;
  final VoidCallback onNextMonth;

  const _MonthSelector({
    required this.selectedMonth,
    required this.onPreviousMonth,
    required this.onNextMonth,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          IconButton(
            onPressed: onPreviousMonth,
            icon: const Icon(Icons.chevron_left),
          ),
          Text(
            AppFormatters.formatMonthYear(selectedMonth),
            style: Theme.of(context).textTheme.titleLarge,
          ),
          IconButton(
            onPressed: onNextMonth,
            icon: const Icon(Icons.chevron_right),
          ),
        ],
      ),
    );
  }
}

class _DaySection extends StatelessWidget {
  final DateTime date;
  final List<CalendarEventDto> events;

  const _DaySection({required this.date, required this.events});

  @override
  Widget build(BuildContext context) {
    final isToday = _isToday(date);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 8),
          child: Row(
            children: [
              Container(
                width: 48,
                height: 48,
                decoration: BoxDecoration(
                  color: isToday
                      ? Theme.of(context).colorScheme.primary
                      : Theme.of(context).colorScheme.surfaceContainerHighest,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      AppFormatters.dayAbbrev(date.weekday),
                      style: TextStyle(
                        fontSize: 10,
                        color: isToday
                            ? Theme.of(context).colorScheme.onPrimary
                            : Theme.of(context).colorScheme.onSurfaceVariant,
                      ),
                    ),
                    Text(
                      '${date.day}',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: isToday
                            ? Theme.of(context).colorScheme.onPrimary
                            : Theme.of(context).colorScheme.onSurface,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 12),
              Text(
                isToday ? AppFormatters.today : AppFormatters.formatDayMonth(date),
                style: Theme.of(context).textTheme.titleSmall,
              ),
            ],
          ),
        ),
        ...events.map((event) => _EventCard(key: ValueKey(event.entitySlug), event: event)),
        const SizedBox(height: 8),
      ],
    );
  }

  bool _isToday(DateTime date) {
    final now = DateTime.now();
    return date.year == now.year &&
        date.month == now.month &&
        date.day == now.day;
  }
}

class _EventCard extends StatelessWidget {
  final CalendarEventDto event;

  const _EventCard({super.key, required this.event});

  DateTime get _startDate => DateTime.parse(event.start);

  @override
  Widget build(BuildContext context) {
    final isRide = event.type == 'RIDE';

    return Padding(
      padding: const EdgeInsets.only(left: 60, bottom: 8),
      child: AnimatedCard(
        onTap: isRide
            ? () => context.push(Paths.ride(event.teamSlug, event.entitySlug))
            : null,
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              Container(
                width: 4,
                height: 40,
                decoration: BoxDecoration(
                  color: isRide
                      ? Theme.of(context).colorScheme.primary
                      : Theme.of(context).colorScheme.tertiary,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
              const SizedBox(width: 12),
              Icon(
                isRide ? Icons.directions_bike : Icons.event,
                color: isRide
                    ? Theme.of(context).colorScheme.primary
                    : Theme.of(context).colorScheme.tertiary,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      event.title,
                      style: Theme.of(context).textTheme.titleSmall?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      '${event.teamName} • ${AppFormatters.formatTime(_startDate)}',
                      style: Theme.of(context).textTheme.bodySmall,
                    ),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right),
            ],
          ),
        ),
      ),
    );
  }
}
