// Context + hook pattern: exporting provider component with its hook is intentional
/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useMemo } from 'react'
import type { AssetDto } from '@/api/dto'

interface AssetImagesContextValue {
  images: AssetDto[]
}

const AssetImagesContext = createContext<AssetImagesContextValue>({ images: [] })

export function AssetImagesProvider({
  images,
  children,
}: {
  images: AssetDto[]
  children: React.ReactNode
}) {
  const value = useMemo(() => ({ images }), [images])
  return <AssetImagesContext.Provider value={value}>{children}</AssetImagesContext.Provider>
}

export function useAssetImages(): AssetDto[] {
  return useContext(AssetImagesContext).images
}
