import { createContext, useContext, useMemo, type ReactNode } from 'react'
import type { AssetDto } from '../../../api/api'
import { createImageMap, resolveAssetUrl } from '../../../lib/assetMarkdown'

type ImageResolver = (assetId: string) => string | undefined

interface ImageContextValue {
  resolver: ImageResolver
  images: AssetDto[]
}

const ImageResolverContext = createContext<ImageContextValue>({
  resolver: () => undefined,
  images: [],
})

interface ImageResolverProviderProps {
  images: AssetDto[]
  children: ReactNode
}

export function ImageResolverProvider({ images, children }: ImageResolverProviderProps) {
  const value = useMemo(() => {
    const imageMap = createImageMap(images)
    return {
      resolver: (assetId: string) => resolveAssetUrl(assetId, imageMap),
      images,
    }
  }, [images])

  return <ImageResolverContext.Provider value={value}>{children}</ImageResolverContext.Provider>
}

export function useImageResolver(): ImageResolver {
  return useContext(ImageResolverContext).resolver
}

export function useImages(): AssetDto[] {
  return useContext(ImageResolverContext).images
}
