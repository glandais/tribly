export type AdSortBy = (typeof AdSortBy)[keyof typeof AdSortBy]

export const AdSortBy = {
  DATE_TIME: 'DATE_TIME',
  PRICE: 'PRICE',
  NAME: 'NAME',
} as const
