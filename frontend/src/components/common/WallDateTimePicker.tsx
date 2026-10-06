import { DateTimePicker, type DateTimePickerProps } from '@mantine/dates'
import { useZoneMention } from '@/hooks/useZoneMention'
import { normalizeWallTime, type WallTime } from '@/utils/wallTime'

export interface WallDateTimePickerProps extends Omit<DateTimePickerProps, 'value' | 'onChange'> {
  /** A wall time without offset, `yyyy-MM-ddTHH:mm:ss`, as the form holds and sends it. */
  value: WallTime | null | undefined
  /** The picked wall time, or `undefined` once cleared. */
  onChange: (value: WallTime | undefined) => void
  /** The IANA zone the event's times are read in: only for the mention, never a conversion. */
  zone: string
}

/**
 * A `DateTimePicker` over a wall time (docs/LEDGER_*.md API-60, plan §6): the value is the clock as
 * it reads where the event happens, and nothing is converted — the backend reads it in the zone it
 * resolves for the entity. Being zone-free, the string renders the same on the SSR server and in the
 * browser. The zone only labels the field, « heure de Tokyo », when it is not the reader's.
 */
export function WallDateTimePicker({
  value,
  onChange,
  zone,
  description,
  ...props
}: WallDateTimePickerProps) {
  const mention = useZoneMention(zone, value)
  return (
    <DateTimePicker
      {...props}
      description={
        mention && description ? (
          <>
            {description} · {mention}
          </>
        ) : (
          (mention ?? description)
        )
      }
      value={value ?? null}
      // Mantine hands back `YYYY-MM-DD HH:mm:ss`; the API wants the ISO `T`.
      onChange={(wall) => onChange(wall ? normalizeWallTime(wall) : undefined)}
    />
  )
}
