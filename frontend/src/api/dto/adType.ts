export type AdType = (typeof AdType)[keyof typeof AdType]

export const AdType = {
  SALE: 'SALE',
  RENTAL: 'RENTAL',
  WANTED: 'WANTED',
} as const
