import type { GpxPreviewUpdateRequest } from './gpxPreviewUpdateRequest.ts'

export type UpdatePreviewBody = {
  preview?: GpxPreviewUpdateRequest
  gpxFile?: Blob
}
