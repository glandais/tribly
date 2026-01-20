import { useGetConfig } from '@/api/endpoints/configuration/configuration'

export function useAppName(): string {
  const { data: config } = useGetConfig()
  return config?.appName ?? ''
}
