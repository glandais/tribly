import { MediaDto } from '@/api/dto'

export const defaultMedia = (): MediaDto => {
  return {
    markdown: '',
    assets: {
      images: [],
      attachments: [],
    },
  }
}
