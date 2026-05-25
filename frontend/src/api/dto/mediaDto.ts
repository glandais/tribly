import type { AssetsDto } from './assetsDto.ts'

export interface MediaDto {
  /** Markdown */
  markdown: string
  /** Assets */
  assets: AssetsDto
}
