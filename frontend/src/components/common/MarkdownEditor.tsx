import React, { useCallback, useEffect, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { LexicalComposer } from '@lexical/react/LexicalComposer'
import { RichTextPlugin } from '@lexical/react/LexicalRichTextPlugin'
import { ContentEditable } from '@lexical/react/LexicalContentEditable'
import { HistoryPlugin } from '@lexical/react/LexicalHistoryPlugin'
import { OnChangePlugin } from '@lexical/react/LexicalOnChangePlugin'
import { MarkdownShortcutPlugin } from '@lexical/react/LexicalMarkdownShortcutPlugin'
import { ListPlugin } from '@lexical/react/LexicalListPlugin'
import { LinkPlugin } from '@lexical/react/LexicalLinkPlugin'
import { useLexicalComposerContext } from '@lexical/react/LexicalComposerContext'
import { HeadingNode, QuoteNode } from '@lexical/rich-text'
import { ListItemNode, ListNode } from '@lexical/list'
import { LinkNode, AutoLinkNode } from '@lexical/link'
import { CodeNode } from '@lexical/code'
import {
  $getSelection,
  $isRangeSelection,
  EditorState,
  FORMAT_TEXT_COMMAND,
  UNDO_COMMAND,
  REDO_COMMAND,
} from 'lexical'
import { $setBlocksType } from '@lexical/selection'
import { $createHeadingNode } from '@lexical/rich-text'
import { INSERT_ORDERED_LIST_COMMAND, INSERT_UNORDERED_LIST_COMMAND } from '@lexical/list'
import { TOGGLE_LINK_COMMAND } from '@lexical/link'
import {
  TRANSFORMERS,
  $convertFromMarkdownString,
  $convertToMarkdownString,
} from '@lexical/markdown'
import {
  BoldIcon,
  ItalicIcon,
  ListBulletIcon,
  NumberedListIcon,
  LinkIcon,
  CodeBracketIcon,
  ArrowUturnLeftIcon,
  ArrowUturnRightIcon,
} from '@heroicons/react/24/outline'

// Toolbar Component
function ToolbarPlugin() {
  const { t } = useTranslation('common')
  const [editor] = useLexicalComposerContext()

  const formatBold = useCallback(() => {
    editor.dispatchCommand(FORMAT_TEXT_COMMAND, 'bold')
  }, [editor])

  const formatItalic = useCallback(() => {
    editor.dispatchCommand(FORMAT_TEXT_COMMAND, 'italic')
  }, [editor])

  const formatCode = useCallback(() => {
    editor.dispatchCommand(FORMAT_TEXT_COMMAND, 'code')
  }, [editor])

  const formatHeading = useCallback(
    (headingSize: 'h1' | 'h2' | 'h3') => {
      editor.update(() => {
        const selection = $getSelection()
        if ($isRangeSelection(selection)) {
          $setBlocksType(selection, () => $createHeadingNode(headingSize))
        }
      })
    },
    [editor]
  )

  const formatBulletList = useCallback(() => {
    editor.dispatchCommand(INSERT_UNORDERED_LIST_COMMAND, undefined)
  }, [editor])

  const formatNumberedList = useCallback(() => {
    editor.dispatchCommand(INSERT_ORDERED_LIST_COMMAND, undefined)
  }, [editor])

  const insertLink = useCallback(() => {
    const url = prompt(t('editor.linkPrompt'))
    if (url) {
      editor.dispatchCommand(TOGGLE_LINK_COMMAND, url)
    }
  }, [editor, t])

  const undo = useCallback(() => {
    editor.dispatchCommand(UNDO_COMMAND, undefined)
  }, [editor])

  const redo = useCallback(() => {
    editor.dispatchCommand(REDO_COMMAND, undefined)
  }, [editor])

  const toolbarButtonClass = (isActive = false) =>
    `group relative p-2.5 rounded-lg transition-all duration-200 ${
      isActive
        ? 'bg-cyan-50 text-cyan-700 shadow-sm'
        : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
    }`

  return (
    <div className="flex items-center gap-1 p-2 border-b border-gray-200 bg-gray-50/50 rounded-t-xl">
      {/* History */}
      <div className="flex items-center gap-1 pr-3 border-r border-gray-300">
        <button
          onClick={undo}
          className={toolbarButtonClass()}
          aria-label="Undo"
          title="Undo"
          type="button"
        >
          <ArrowUturnLeftIcon className="w-5 h-5" />
        </button>
        <button
          onClick={redo}
          className={toolbarButtonClass()}
          aria-label="Redo"
          title="Redo"
          type="button"
        >
          <ArrowUturnRightIcon className="w-5 h-5" />
        </button>
      </div>

      {/* Text Formatting */}
      <div className="flex items-center gap-1 pr-3 border-r border-gray-300">
        <button
          onClick={formatBold}
          className={toolbarButtonClass()}
          aria-label={t('editor.bold')}
          title={t('editor.bold')}
          type="button"
        >
          <BoldIcon className="w-5 h-5" />
        </button>
        <button
          onClick={formatItalic}
          className={toolbarButtonClass()}
          aria-label={t('editor.italic')}
          title={t('editor.italic')}
          type="button"
        >
          <ItalicIcon className="w-5 h-5" />
        </button>
        <button
          onClick={formatCode}
          className={toolbarButtonClass()}
          aria-label={t('editor.code')}
          title={t('editor.code')}
          type="button"
        >
          <CodeBracketIcon className="w-5 h-5" />
        </button>
      </div>

      {/* Headings */}
      <div className="flex items-center gap-1 pr-3 border-r border-gray-300">
        <button
          onClick={() => formatHeading('h1')}
          className={toolbarButtonClass()}
          aria-label={t('editor.heading1')}
          title={t('editor.heading1')}
          type="button"
        >
          <span className="text-sm font-bold">H1</span>
        </button>
        <button
          onClick={() => formatHeading('h2')}
          className={toolbarButtonClass()}
          aria-label={t('editor.heading2')}
          title={t('editor.heading2')}
          type="button"
        >
          <span className="text-sm font-bold">H2</span>
        </button>
        <button
          onClick={() => formatHeading('h3')}
          className={toolbarButtonClass()}
          aria-label={t('editor.heading3')}
          title={t('editor.heading3')}
          type="button"
        >
          <span className="text-sm font-bold">H3</span>
        </button>
      </div>

      {/* Lists */}
      <div className="flex items-center gap-1 pr-3 border-r border-gray-300">
        <button
          onClick={formatBulletList}
          className={toolbarButtonClass()}
          aria-label={t('editor.bulletList')}
          title={t('editor.bulletList')}
          type="button"
        >
          <ListBulletIcon className="w-5 h-5" />
        </button>
        <button
          onClick={formatNumberedList}
          className={toolbarButtonClass()}
          aria-label={t('editor.numberedList')}
          title={t('editor.numberedList')}
          type="button"
        >
          <NumberedListIcon className="w-5 h-5" />
        </button>
      </div>

      {/* Insert */}
      <div className="flex items-center gap-1">
        <button
          onClick={insertLink}
          className={toolbarButtonClass()}
          aria-label={t('editor.link')}
          title={t('editor.link')}
          type="button"
        >
          <LinkIcon className="w-5 h-5" />
        </button>
      </div>

      {/* Helper text */}
      <div className="ml-auto text-xs text-gray-500 italic pr-2">
        {t('editor.markdownSupported')}
      </div>
    </div>
  )
}

// Custom error boundary component
function EditorErrorBoundary({ children }: { children: React.ReactNode }) {
  return <>{children}</>
}

// Plugin to handle initial markdown value
function InitialValuePlugin({ value }: { value: string }) {
  const [editor] = useLexicalComposerContext()
  const hasInitializedRef = useRef(false)

  useEffect(() => {
    // Only initialize ONCE when we first receive a non-empty value
    // After that, ignore prop changes (user is editing)
    if (value && !hasInitializedRef.current) {
      editor.update(() => {
        // Convert markdown string to Lexical nodes
        // This automatically clears the root and sets the new content
        $convertFromMarkdownString(value, TRANSFORMERS)
      })
      hasInitializedRef.current = true
    }
  }, [editor, value])

  return null
}

export interface MarkdownEditorProps {
  initialValue?: string
  onChange?: (value: string) => void
  placeholder?: string
  className?: string
  minHeight?: string
  maxHeight?: string
  disabled?: boolean
  ariaLabel?: string
}

export function MarkdownEditor({
  initialValue = '',
  onChange,
  placeholder,
  className = '',
  minHeight = '200px',
  maxHeight = '500px',
  disabled = false,
  ariaLabel,
}: MarkdownEditorProps) {
  const { t } = useTranslation('common')

  const initialConfig = {
    namespace: 'MarkdownEditor',
    theme: {
      paragraph: 'mb-2 text-gray-900 leading-relaxed',
      heading: {
        h1: 'text-3xl font-bold mb-4 mt-6 text-gray-900 tracking-tight',
        h2: 'text-2xl font-bold mb-3 mt-5 text-gray-900 tracking-tight',
        h3: 'text-xl font-bold mb-2 mt-4 text-gray-900 tracking-tight',
      },
      list: {
        ul: 'list-disc list-inside mb-2 text-gray-900',
        ol: 'list-decimal list-inside mb-2 text-gray-900',
        listitem: 'mb-1',
        nested: {
          listitem: 'list-none',
        },
      },
      link: 'text-cyan-600 hover:text-cyan-700 underline cursor-pointer transition-colors',
      text: {
        bold: 'font-bold',
        italic: 'italic',
        code: 'bg-gray-100 text-pink-600 px-1.5 py-0.5 rounded font-mono text-sm',
      },
      quote: 'border-l-4 border-cyan-500 pl-4 italic text-gray-700 my-4',
      code: 'bg-gray-900 text-gray-100 p-4 rounded-lg font-mono text-sm my-4 overflow-x-auto block',
    },
    nodes: [HeadingNode, QuoteNode, ListNode, ListItemNode, LinkNode, AutoLinkNode, CodeNode],
    onError: (error: Error) => {
      console.error('Lexical error:', error)
    },
    editable: !disabled,
  }

  const handleChange = useCallback(
    (editorState: EditorState) => {
      if (onChange) {
        editorState.read(() => {
          // Convert Lexical nodes to markdown string
          const markdown = $convertToMarkdownString(TRANSFORMERS)
          onChange(markdown)
        })
      }
    },
    [onChange]
  )

  return (
    <div
      className={`markdown-editor-container border border-gray-300 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-300 bg-white ${className}`}
    >
      <LexicalComposer initialConfig={initialConfig}>
        <ToolbarPlugin />
        <div className="relative">
          <RichTextPlugin
            contentEditable={
              <ContentEditable
                className="editor-input px-6 py-5 focus:outline-hidden text-base leading-relaxed"
                style={{ minHeight, maxHeight, overflowY: 'auto' }}
                aria-label={ariaLabel || t('editor.ariaLabel')}
              />
            }
            placeholder={
              <div
                className="editor-placeholder absolute top-5 left-6 text-gray-400 pointer-events-none select-none text-base"
                aria-hidden="true"
              >
                {placeholder || t('editor.placeholder')}
              </div>
            }
            ErrorBoundary={EditorErrorBoundary}
          />
          <HistoryPlugin />
          <OnChangePlugin onChange={handleChange} />
          <MarkdownShortcutPlugin transformers={TRANSFORMERS} />
          <ListPlugin />
          <LinkPlugin />
          <InitialValuePlugin value={initialValue} />
        </div>
      </LexicalComposer>

      {/* Keyboard shortcuts hint */}
      <div className="px-4 py-2 bg-gray-50 border-t border-gray-200 text-xs text-gray-500 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <span>
            <kbd className="px-1.5 py-0.5 bg-white border border-gray-300 rounded text-xs font-mono">
              **
            </kbd>{' '}
            {t('editor.shortcuts.bold')}
          </span>
          <span>
            <kbd className="px-1.5 py-0.5 bg-white border border-gray-300 rounded text-xs font-mono">
              *
            </kbd>{' '}
            {t('editor.shortcuts.italic')}
          </span>
          <span>
            <kbd className="px-1.5 py-0.5 bg-white border border-gray-300 rounded text-xs font-mono">
              #
            </kbd>{' '}
            {t('editor.shortcuts.heading')}
          </span>
        </div>
        <div className="text-gray-400">{t('editor.shortcuts.hint')}</div>
      </div>
    </div>
  )
}
