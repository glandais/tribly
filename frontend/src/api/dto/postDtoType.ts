export type PostDtoType = (typeof PostDtoType)[keyof typeof PostDtoType]

export const PostDtoType = {
  POST: 'POST',
} as const
