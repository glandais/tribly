import { DateTimePicker, type DateTimePickerProps } from '@mantine/dates'
import { formatInTimeZone, fromZonedTime } from 'date-fns-tz'
import { useEffectiveTimezone } from '@/utils/dateFormat'

const WALL_TIME_FORMAT = 'yyyy-MM-dd HH:mm:ss'

export interface InstantDateTimePickerProps extends Omit<
  DateTimePickerProps,
  'value' | 'onChange'
> {
  /** An ISO instant, as the API carries it. */
  value: string | null | undefined
  /** The picked wall time as an ISO instant, or `undefined` once cleared. */
  onChange: (value: string | undefined) => void
}

/**
 * A `DateTimePicker` over an instant, shown in the visitor's effective timezone.
 *
 * Handing Mantine a `Date` lets it format in whatever zone the process runs in — UTC on the SSR
 * server, the browser's zone on hydration — so every edit form with a date failed to hydrate
 * (React #418). Mantine's string value is a wall time with no zone; this component chooses the zone
 * itself, through `useEffectiveTimezone`, which reads UTC on the hydration render and the real zone
 * right after, and converts back with the same zone. It also makes the form honour the user's own
 * timezone preference, the one every displayed date already uses.
 */
export function InstantDateTimePicker({ value, onChange, ...props }: InstantDateTimePickerProps) {
  const { timezone } = useEffectiveTimezone()
  return (
    <DateTimePicker
      {...props}
      value={value ? formatInTimeZone(value, timezone, WALL_TIME_FORMAT) : null}
      onChange={(wallTime) =>
        onChange(wallTime ? fromZonedTime(wallTime, timezone).toISOString() : undefined)
      }
    />
  )
}
