import type { ReactNode } from 'react'
import { Grid, Group, List, Stack, Text, ThemeIcon, Title, type MantineColor } from '@mantine/core'
import { IconCheck, type TablerIcon } from '@tabler/icons-react'

export interface FeatureSectionProps {
  /** Anchor of the section, the target of its jump chip. */
  id: string
  color: MantineColor
  icon: TablerIcon
  eyebrow: string
  title: string
  lead: string
  points: string[]
  visual: ReactNode
  /** Put the illustration on the left from `md` up, alternating down the page. */
  reverse?: boolean
}

/** One feature of the page: eyebrow, title, lead, bullet points, and its illustration beside it. */
export function FeatureSection({
  id,
  color,
  icon: Icon,
  eyebrow,
  title,
  lead,
  points,
  visual,
  reverse = false,
}: FeatureSectionProps) {
  const titleId = `${id}-title`
  return (
    <section id={id} aria-labelledby={titleId} className="features-section">
      <Grid gap={{ base: 'xl', md: 48 }} align="center">
        <Grid.Col span={{ base: 12, md: 6 }} order={{ base: 1, md: reverse ? 2 : 1 }}>
          <Stack gap="md">
            <Group gap="sm">
              <ThemeIcon variant="light" color={color} size={36} radius="md">
                <Icon size={20} />
              </ThemeIcon>
              <Text
                size="sm"
                fw={700}
                tt="uppercase"
                style={{ letterSpacing: 0.4, color: `var(--mantine-color-${color}-light-color)` }}
              >
                {eyebrow}
              </Text>
            </Group>
            <Title order={2} id={titleId}>
              {title}
            </Title>
            <Text size="lg" c="dimmed">
              {lead}
            </Text>
            <List
              spacing="xs"
              icon={
                <ThemeIcon variant="light" color={color} size={22} radius="xl">
                  <IconCheck size={14} />
                </ThemeIcon>
              }
            >
              {points.map((point) => (
                <List.Item key={point}>{point}</List.Item>
              ))}
            </List>
          </Stack>
        </Grid.Col>
        <Grid.Col span={{ base: 12, md: 6 }} order={{ base: 2, md: reverse ? 1 : 2 }}>
          {visual}
        </Grid.Col>
      </Grid>
    </section>
  )
}
