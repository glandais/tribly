import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, cleanup } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'
import type { TagDto } from '@/api/dto'

// Keys and their arguments, not wording.
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string, options?: { hidden?: number; count?: number }) =>
      options?.hidden !== undefined
        ? `${key}:${options.hidden}`
        : options?.count !== undefined
          ? `${key}:${options.count}`
          : key,
  }),
}))

import { TagList } from './TagList'
import { tagFamily } from './tagFamily'

const tag = (id: string, label: string, color: TagDto['color'] = 'GREEN'): TagDto => ({
  id,
  label,
  color,
})

const five = [
  tag('a', 'Col'),
  tag('b', 'Gravel'),
  tag('c', 'Nuit'),
  tag('d', 'Plat'),
  tag('e', 'Rapide'),
]

function renderList(props: Parameters<typeof TagList>[0]) {
  return render(
    <MantineProvider>
      <TagList {...props} />
    </MantineProvider>
  )
}

describe('TagList', () => {
  afterEach(cleanup)

  it('renders nothing for a content without tags', () => {
    renderList({ tags: [] })
    expect(screen.queryByLabelText('tags.listLabel')).toBeNull()
    cleanup()
    renderList({ tags: undefined })
    expect(screen.queryByLabelText('tags.listLabel')).toBeNull()
  })

  it('shows every tag on a detail page', () => {
    renderList({ tags: five })
    for (const t of five) expect(screen.getByText(t.label)).toBeTruthy()
    expect(screen.queryByText(/tags.moreShort/)).toBeNull()
  })

  it('truncates on a card and counts the rest', () => {
    renderList({ tags: five, max: 3 })
    expect(screen.getByText('Col')).toBeTruthy()
    expect(screen.getByText('Nuit')).toBeTruthy()
    expect(screen.queryByText('Plat')).toBeNull()
    expect(screen.getByText('tags.moreShort:2')).toBeTruthy()
  })

  it('never spends a « +1 » chip to hide a single tag', () => {
    renderList({ tags: five.slice(0, 4), max: 3 })
    expect(screen.getByText('Plat')).toBeTruthy()
    expect(screen.queryByText(/tags.moreShort/)).toBeNull()
  })
})

describe('tagFamily', () => {
  it('lower-cases the API colour into a charter family', () => {
    expect(tagFamily('GRAPE')).toBe('grape')
    expect(tagFamily('GRAY')).toBe('gray')
  })

  it('renders a colour this client does not know as gray', () => {
    expect(tagFamily('MAGENTA')).toBe('gray')
  })
})
