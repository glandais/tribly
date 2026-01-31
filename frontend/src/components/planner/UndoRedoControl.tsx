import { useEffect, useRef } from 'react'
import { useControl } from 'react-map-gl/maplibre'
import type { IControl, Map as MaplibreMap } from 'maplibre-gl'

interface UndoRedoControlProps {
  position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
  canUndo: boolean
  canRedo: boolean
  onUndo: () => void
  onRedo: () => void
}

// SVG paths from tabler-icons (arrow-back-up / arrow-forward-up)
const UNDO_SVG = `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 14l-4 -4l4 -4"/><path d="M5 10h11a4 4 0 1 1 0 8h-1"/></svg>`
const REDO_SVG = `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M15 14l4 -4l-4 -4"/><path d="M19 10h-11a4 4 0 1 0 0 8h1"/></svg>`

class UndoRedoControlImpl implements IControl {
  _container: HTMLDivElement | null = null
  _undoBtn: HTMLButtonElement | null = null
  _redoBtn: HTMLButtonElement | null = null
  _onUndo: () => void
  _onRedo: () => void

  constructor(onUndo: () => void, onRedo: () => void) {
    this._onUndo = onUndo
    this._onRedo = onRedo
  }

  onAdd(_map: MaplibreMap): HTMLElement {
    this._container = document.createElement('div')
    this._container.className = 'maplibregl-ctrl maplibregl-ctrl-group'

    this._undoBtn = this._createButton(UNDO_SVG, 'Undo (Ctrl+Z)', () => this._onUndo())
    this._redoBtn = this._createButton(REDO_SVG, 'Redo (Ctrl+Shift+Z)', () => this._onRedo())

    this._container.appendChild(this._undoBtn)
    this._container.appendChild(this._redoBtn)
    return this._container
  }

  onRemove(): void {
    this._container?.remove()
    this._container = null
  }

  update(canUndo: boolean, canRedo: boolean) {
    if (this._undoBtn) {
      this._undoBtn.disabled = !canUndo
      this._undoBtn.style.opacity = canUndo ? '1' : '0.4'
      this._undoBtn.style.cursor = canUndo ? 'pointer' : 'default'
    }
    if (this._redoBtn) {
      this._redoBtn.disabled = !canRedo
      this._redoBtn.style.opacity = canRedo ? '1' : '0.4'
      this._redoBtn.style.cursor = canRedo ? 'pointer' : 'default'
    }
  }

  private _createButton(svg: string, title: string, onClick: () => void): HTMLButtonElement {
    const btn = document.createElement('button')
    btn.type = 'button'
    btn.title = title
    btn.innerHTML = svg
    btn.style.display = 'flex'
    btn.style.alignItems = 'center'
    btn.style.justifyContent = 'center'
    btn.addEventListener('click', onClick)
    return btn
  }
}

export function UndoRedoControl({
  position = 'top-left',
  canUndo,
  canRedo,
  onUndo,
  onRedo,
}: UndoRedoControlProps) {
  const onUndoRef = useRef(onUndo)
  const onRedoRef = useRef(onRedo)
  useEffect(() => {
    onUndoRef.current = onUndo
    onRedoRef.current = onRedo
  }, [onUndo, onRedo])

  const ctrl = useControl<UndoRedoControlImpl>(
    () =>
      new UndoRedoControlImpl(
        () => onUndoRef.current(),
        () => onRedoRef.current()
      ),
    { position }
  )

  useEffect(() => {
    ctrl.update(canUndo, canRedo)
  }, [ctrl, canUndo, canRedo])

  return null
}
