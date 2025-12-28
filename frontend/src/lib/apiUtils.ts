import { MediaDto } from '@/api'

export const defaultMedia = (): MediaDto => {
  return {
    markdown: '',
    assets: {
      images: [],
      videos: [],
      attachments: [],
    },
  }
}
