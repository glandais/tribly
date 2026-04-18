export type AssetTypeRequest = (typeof AssetTypeRequest)[keyof typeof AssetTypeRequest]

export const AssetTypeRequest = {
  LOGO: 'LOGO',
  IMAGE: 'IMAGE',
  ATTACHMENT: 'ATTACHMENT',
} as const
