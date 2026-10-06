import { describe, it, expect, vi, afterEach } from 'vitest'
import { render, screen, fireEvent, cleanup } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'
import fr from '@/locales/fr/common.json'
import en from '@/locales/en/common.json'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key, i18n: { language: 'fr' } }),
}))

import { ListViewSwitch, type ListView } from './ListViewSwitch'

function renderSwitch<V extends ListView>(views: readonly V[], value: V, onChange = vi.fn()) {
  render(
    <MantineProvider>
      <ListViewSwitch views={views} value={value} onChange={onChange} />
    </MantineProvider>
  )
  return onChange
}

describe('ListViewSwitch', () => {
  afterEach(cleanup)

  it('is a radio group labelled « Affichage », one radio per view, in the given order', () => {
    renderSwitch(['card', 'row', 'map'], 'row')
    const group = screen.getByRole('radiogroup', { name: 'listView.label' })
    expect(group).toBeInTheDocument()
    expect(screen.getAllByRole('radio').map((radio) => radio.getAttribute('value'))).toEqual([
      'card',
      'row',
      'map',
    ])
  })

  it('names each icon-only view for assistive tech, and checks the current one', () => {
    renderSwitch(['card', 'row', 'calendar'], 'calendar')
    expect(screen.getByRole('radio', { name: 'listView.card' })).not.toBeChecked()
    expect(screen.getByRole('radio', { name: 'listView.row' })).not.toBeChecked()
    expect(screen.getByRole('radio', { name: 'listView.calendar' })).toBeChecked()
    expect(screen.queryByRole('radio', { name: 'listView.map' })).toBeNull()
  })

  it('reports the picked view', () => {
    const onChange = renderSwitch(['card', 'row', 'map'], 'card')
    fireEvent.click(screen.getByRole('radio', { name: 'listView.map' }))
    expect(onChange).toHaveBeenCalledWith('map')
  })

  it('has its labels in both languages', () => {
    for (const key of ['label', 'card', 'row', 'map', 'calendar']) {
      expect(fr).toHaveProperty([`listView.${key}`])
      expect(en).toHaveProperty([`listView.${key}`])
    }
    expect(fr['listView.label']).toBe('Affichage')
    expect(fr['listView.row']).toBe('Lignes')
  })
})
