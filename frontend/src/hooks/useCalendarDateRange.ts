import { useState, useCallback } from 'react'

interface DateRange {
  from: string
  to: string
}

interface UseCalendarDateRangeReturn {
  dateRange: DateRange
  handleDateRangeChange: (start: Date, end: Date) => void
}

function getInitialDateRange(): DateRange {
  const now = new Date()
  const from = new Date(now)
  from.setMonth(from.getMonth() - 1)
  const to = new Date(now)
  to.setMonth(to.getMonth() + 6)
  return {
    from: from.toISOString(),
    to: to.toISOString(),
  }
}

export function useCalendarDateRange(): UseCalendarDateRangeReturn {
  const [dateRange, setDateRange] = useState<DateRange>(getInitialDateRange)

  const handleDateRangeChange = useCallback((start: Date, end: Date) => {
    setDateRange({
      from: start.toISOString(),
      to: end.toISOString(),
    })
  }, [])

  return { dateRange, handleDateRangeChange }
}
