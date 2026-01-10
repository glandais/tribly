import { Link } from 'react-router-dom'
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

  return (
    <Box mb="lg">
      <nav aria-label={t('aria.breadcrumb')}>
        {/* Back link - shown on mobile always, on desktop only when showBackLink is true */}
        {previousItem && previousItem.path && (
          <Anchor
            component={Link}
            to={previousItem.path}
            c="dimmed"
            size="sm"
            mb={showBackLink ? 'xs' : undefined}
            display={showBackLink ? 'inline-flex' : { base: 'inline-flex', sm: 'none' }}
            style={{ alignItems: 'center' }}
          >
            <IconChevronLeft size={16} style={{ marginRight: 4 }} />
            {previousItem.label}
          </Anchor>
        )}

        {/* Desktop: Full breadcrumb path (hidden when showBackLink is true to avoid redundancy) */}
        {!showBackLink && (
          <Breadcrumbs visibleFrom="sm">
            {items.map((item, index) => {
              const isLast = index === items.length - 1
              return (
                <Fragment key={`bc-${index}`}>
                  <Group gap={4}>
                    {item.path && !isLast ? (
                      <Anchor component={Link} to={item.path} size="sm">
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
                              <Menu.Item key={bIndex} component={Link} to={subItem.path}>
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
            })}
          </Breadcrumbs>
        )}
      </nav>
    </Box>
  )
}
