import type { AssetsDto } from './assetsDto.ts'

export interface MediaDto {
  /**
   * Markdown
   * @maxLength 100000
   */
  markdown: string
  /** Assets */
  assets: AssetsDto
}
