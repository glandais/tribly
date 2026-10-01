export type TagColor = (typeof TagColor)[keyof typeof TagColor]

export const TagColor = {
  INDIGO: 'INDIGO',
  BLUE: 'BLUE',
  GREEN: 'GREEN',
  RED: 'RED',
  YELLOW: 'YELLOW',
  ORANGE: 'ORANGE',
  GRAPE: 'GRAPE',
  TEAL: 'TEAL',
  GRAY: 'GRAY',
} as const
