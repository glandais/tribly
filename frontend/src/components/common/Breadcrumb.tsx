import { PrefetchLink } from '@/components/common/PrefetchLink'
import { useTranslation } from 'react-i18next'
import { Breadcrumbs, Anchor, Text, Group, Menu, Box, ActionIcon } from '@mantine/core'
import { IconChevronLeft, IconDots } from '@tabler/icons-react'
import { Fragment } from 'react/jsx-runtime'

export interface BreadcrumbSubItemType {
  label: string
  path?: string
}

export interface BreadcrumbItemType {
  label: string
  path?: string
  subItems?: BreadcrumbSubItemType[]
}

interface BreadcrumbProps {
  items: BreadcrumbItemType[]
  showBackLink?: boolean
}

export function Breadcrumb({ items, showBackLink = false }: BreadcrumbProps) {
  const { t } = useTranslation()

  if (items.length === 0) {
    return null
  }

  // For back link: show link to previous item
  const previousItem = items.length > 1 ? items[items.length - 2] : null

  // Below `sm` the trail used to collapse to a single back link, which cost the mobile user
  // every sibling tab of the level — the very silo this port is meant to break. Keep the last
  // two levels instead, with their overflow menu.
  const compactItems = items.slice(-2)

  const renderItem = (item: BreadcrumbItemType, isLast: boolean, key: string) => (
    <Fragment key={key}>
      <Group gap={4}>
        {item.path && !isLast ? (
          <Anchor component={PrefetchLink} to={item.path} size="sm">
            {item.label}
          </Anchor>
        ) : (
          <Text size="sm" c="dimmed">
            {item.label}
          </Text>
        )}

        {item.subItems && item.subItems.length > 0 && (
          <Menu shadow="md" width={200}>
            <Menu.Target>
              <ActionIcon variant="subtle" size="xs" aria-label={t('aria.more')}>
                <IconDots size={14} />
              </ActionIcon>
            </Menu.Target>
            <Menu.Dropdown>
              {item.subItems.map((subItem, bIndex) =>
                subItem.path ? (
                  <Menu.Item key={bIndex} component={PrefetchLink} to={subItem.path}>
                    {subItem.label}
                  </Menu.Item>
                ) : (
                  <Menu.Item key={bIndex} disabled>
                    {subItem.label}
                  </Menu.Item>
                )
              )}
            </Menu.Dropdown>
          </Menu>
        )}
      </Group>
    </Fragment>
  )

  return (
    <Box mb="lg">
      <nav aria-label={t('aria.breadcrumb')}>
        {/* Explicit back link — replaces the trail entirely when a page asks for it */}
        {previousItem && previousItem.path && (
          <Anchor
            component={PrefetchLink}
            to={previousItem.path}
            c="dimmed"
            size="sm"
            mb="xs"
            display={showBackLink ? 'inline-flex' : 'none'}
            style={{ alignItems: 'center' }}
          >
            <IconChevronLeft size={16} style={{ marginRight: 4 }} />
            {previousItem.label}
          </Anchor>
        )}

        {/* Full path from `sm`; last two levels below it. */}
        {!showBackLink && (
          <>
            <Breadcrumbs visibleFrom="sm">
              {items.map((item, index) =>
                renderItem(item, index === items.length - 1, `bc-${index}`)
              )}
            </Breadcrumbs>
            <Breadcrumbs hiddenFrom="sm">
              {compactItems.map((item, index) =>
                renderItem(item, index === compactItems.length - 1, `bc-compact-${index}`)
              )}
            </Breadcrumbs>
          </>
        )}
      </nav>
    </Box>
  )
}
