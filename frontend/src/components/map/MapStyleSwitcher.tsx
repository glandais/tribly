import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { IconMap, IconX } from '@tabler/icons-react'
import {
  Box,
  Paper,
  Group,
  Text,
  Stack,
  ActionIcon,
  UnstyledButton,
  Switch,
  Divider,
} from '@mantine/core'
import { groupStyles, isKnownGroup, type MapStyle, type MapStyleId } from './mapStyles'
import { MapControlButton, MapControlGroup } from './MapControl'

export interface MapStyleSwitcherProps {
  /** The served basemaps, in served order. Empty while the config is in flight. */
  styles: readonly MapStyle[]
  currentStyleId: MapStyleId | undefined
  onStyleChange: (styleId: MapStyleId) => void
  terrain3d?: boolean
  onTerrain3DChange?: (enabled: boolean) => void
  hillshade?: boolean
  onHillshadeChange?: (enabled: boolean) => void
}

export function MapStyleSwitcher({
  styles,
  currentStyleId,
  onStyleChange,
  terrain3d = false,
  onTerrain3DChange,
  hillshade = false,
  onHillshadeChange,
}: MapStyleSwitcherProps) {
  const { t } = useTranslation()
  const [isExpanded, setIsExpanded] = useState(false)

  const toggleExpanded = useCallback(() => {
    setIsExpanded((prev) => !prev)
  }, [])

  const handleStyleSelect = useCallback(
    (styleId: MapStyleId) => {
      onStyleChange(styleId)
      setIsExpanded(false)
    },
    [onStyleChange]
  )

  return (
    <>
      <MapControlGroup>
        <MapControlButton
          label={t('map.styles.switch')}
          icon={<IconMap size={20} />}
          onClick={toggleExpanded}
          active={isExpanded}
        />
      </MapControlGroup>

      {isExpanded && (
        <Box
          pos="absolute"
          top={10}
          // Clear of the control column (10px margin + a 29px button), so the panel opens beside
          // its own button instead of over it.
          left={48}
          style={{
            // Above the elevation-chart overlay (zIndex 1000 in RouteMapView), which would
            // otherwise occlude the panel on the embedded route map.
            zIndex: 1001,
            // Cap to the map height and scroll internally, otherwise a tall panel is clipped by
            // the map container's overflow:hidden on short (embedded) maps.
            maxHeight: 'calc(100% - 20px)',
            display: 'flex',
            flexDirection: 'column',
          }}
        >
          <Paper
            shadow="lg"
            p="sm"
            radius="md"
            maw={160}
            style={{ minHeight: 0, display: 'flex', flexDirection: 'column' }}
          >
            <Group
              justify="space-between"
              align="center"
              wrap="nowrap"
              mb="sm"
              pb="sm"
              style={{ borderBottom: '1px solid var(--mantine-color-default-border)' }}
            >
              <Text size="sm" fw={500} c="dimmed">
                {t('map.styles.title')}
              </Text>
              <ActionIcon
                variant="subtle"
                color="gray"
                size="sm"
                onClick={toggleExpanded}
                aria-label={t('actions.cancelAction')}
              >
                <IconX size={16} />
              </ActionIcon>
            </Group>
            {/* Only the list scrolls; the fixed header keeps the close button on one line */}
            <Box style={{ overflowY: 'auto', minHeight: 0 }}>
              <Stack gap="sm">
                {/* Sections follow the served order — the server classifies its basemaps, the client
                  doesn't re-sort. A group this build has no label for is titled with its raw value
                  rather than with its untranslated key, or silently merged into the one above. */}
                {groupStyles(styles).map((section) => (
                  <Stack key={section.group} gap={4}>
                    <Text size="xs" fw={600} c="dimmed" tt="uppercase">
                      {isKnownGroup(section.group)
                        ? t(`map.styles.group.${section.group}`)
                        : section.group}
                    </Text>
                    {section.styles.map((style) => {
                      const isSelected = style.id === currentStyleId
                      return (
                        <UnstyledButton
                          key={style.id}
                          onClick={() => handleStyleSelect(style.id)}
                          px="sm"
                          py="xs"
                          style={{
                            borderRadius: 'var(--mantine-radius-sm)',
                            backgroundColor: isSelected
                              ? 'var(--mantine-primary-color-light)'
                              : undefined,
                            transition: 'background-color 150ms',
                          }}
                        >
                          <Text
                            size="sm"
                            c={isSelected ? 'var(--mantine-primary-color-filled)' : undefined}
                            fw={isSelected ? 500 : 400}
                          >
                            {style.label}
                          </Text>
                        </UnstyledButton>
                      )
                    })}
                  </Stack>
                ))}
              </Stack>
              {(onTerrain3DChange || onHillshadeChange) && (
                <>
                  <Divider my="xs" />
                  <Stack gap={6}>
                    {onTerrain3DChange && (
                      <Switch
                        size="sm"
                        checked={terrain3d}
                        onChange={(e) => onTerrain3DChange(e.currentTarget.checked)}
                        label={t('map.terrain3d.label')}
                      />
                    )}
                    {onHillshadeChange && (
                      <Switch
                        size="sm"
                        checked={hillshade}
                        onChange={(e) => onHillshadeChange(e.currentTarget.checked)}
                        label={t('map.hillshade.label')}
                      />
                    )}
                  </Stack>
                </>
              )}
            </Box>
          </Paper>
        </Box>
      )}
    </>
  )
}
