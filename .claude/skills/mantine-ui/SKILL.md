---
name: mantine-ui
description: Use when modifying files in frontend/src/components/ or frontend/src/pages/, creating React components, building forms, modals, or UI features. This project uses Mantine exclusively for all UI components.
---

# Mantine UI Reference (Tribly Patterns)

## Overview

Mantine v8 component library with TypeScript, dark mode, and form integration. All components require `MantineProvider` wrapper.

**Docs:** https://mantine.dev/llms.txt

## Theme Configuration

```tsx
import { MantineProvider, createTheme, virtualColor } from '@mantine/core';
import '@mantine/core/styles.css';

const theme = createTheme({
  primaryColor: 'primary',
  fontFamily: 'Inter, system-ui, sans-serif',
  defaultRadius: 'md',
  autoContrast: true,
  luminanceThreshold: 0.3,
  colors: {
    primary: virtualColor({ name: 'primary', light: 'indigo', dark: 'indigo' }),
    success: virtualColor({ name: 'success', light: 'green', dark: 'green' }),
    warning: virtualColor({ name: 'warning', light: 'yellow', dark: 'yellow' }),
    danger: virtualColor({ name: 'danger', light: 'red', dark: 'red' }),
  },
  headings: {
    sizes: {
      h1: { fontSize: 'clamp(1.5rem, 5vw, 2.125rem)', lineHeight: '1.2' },
      h2: { fontSize: 'clamp(1.25rem, 4vw, 1.625rem)', lineHeight: '1.3' },
    },
  },
  components: {
    Button: { styles: { root: { minHeight: 'var(--button-min-height, 44px)' } } },
    ActionIcon: { defaultProps: { size: 'lg' } },
  },
});

<MantineProvider theme={theme} defaultColorScheme="auto">
  <Notifications position="top-right" />
  {children}
</MantineProvider>
```

## Core Components

### Layout

| Component | Purpose | Key Props |
|-----------|---------|-----------|
| `Stack` | Vertical flex | `gap`, `align`, `justify` |
| `Group` | Horizontal flex | `gap`, `wrap`, `justify` |
| `Paper` | Card container | `shadow`, `withBorder`, `p`, `radius` |
| `Box` | Generic wrapper | All style props |
| `SimpleGrid` | Responsive grid | `cols={{ base: 1, sm: 2, lg: 3 }}` |
| `Center` | Center content | `mih` for min-height |
| `Collapse` | Collapsible content | `in={isOpen}` |

```tsx
// Common page layout
<Stack>
  <Group justify="space-between">
    <Title order={2}>{title}</Title>
    <Button leftSection={<IconPlus />}>{t('actions.add')}</Button>
  </Group>
  <Paper withBorder p="md">
    <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="md">
      {items.map(item => <ItemCard key={item.id} {...item} />)}
    </SimpleGrid>
  </Paper>
</Stack>
```

### Form with Zod Validation

```tsx
import { useForm } from '@mantine/form';
import { zodFormValidator } from '@/lib/formUtils'; // Project wrapper

const form = useForm<MyFormData>({
  validate: zodFormValidator<MyFormData>(mySchema),
  initialValues,
  validateInputOnChange: true,
});

<form onSubmit={form.onSubmit(handleSubmit)}>
  <Stack>
    <TextInput label={t('field.name')} {...form.getInputProps('name')} />
    <Select
      label={t('field.type')}
      data={typeOptions}
      {...form.getInputProps('type')}
    />
    <Group justify="flex-end" pt="md">
      <Button variant="default" onClick={onCancel}>{t('actions.cancel')}</Button>
      <Button type="submit" loading={isPending}>{t('actions.save')}</Button>
    </Group>
  </Stack>
</form>
```

**Array fields:**
```tsx
form.insertListItem('groups', { name: '' });
form.removeListItem('groups', index);
form.reorderListItem('groups', { from: index, to: newIndex });
form.setFieldValue('groups.0.name', 'New Name');
```

### Inputs

| Component | Purpose | Key Props |
|-----------|---------|-----------|
| `TextInput` | Text field | `label`, `error`, `leftSection`, `rightSection` |
| `Select` | Dropdown (restricted) | `data`, `searchable`, `clearable` |
| `NumberInput` | Numeric | `min`, `max`, `step`, `decimalScale` |
| `Textarea` | Multi-line | `autosize`, `minRows`, `maxRows` |
| `DateTimePicker` | Date/time | `@mantine/dates` package |
| `Radio.Group` | Radio buttons | For status/visibility selection |

```tsx
// Controlled input with clear button
<TextInput
  value={search}
  onChange={(e) => setSearch(e.currentTarget.value)}
  leftSection={<IconSearch size={16} />}
  rightSection={search && <CloseButton onClick={() => setSearch('')} />}
/>
```

### Buttons

| Component | Purpose | Key Props |
|-----------|---------|-----------|
| `Button` | Primary action | `variant`, `color`, `loading`, `leftSection` |
| `ActionIcon` | Icon-only | `variant`, `aria-label` (required!) |

**Variants:** `filled`, `light`, `outline`, `subtle`, `default`, `transparent`

```tsx
<Button variant="default" onClick={onCancel}>{t('actions.cancel')}</Button>
<Button color="danger" loading={isDeleting}>{t('actions.delete')}</Button>
<ActionIcon variant="subtle" color="red" aria-label="Delete">
  <IconTrash size={16} />
</ActionIcon>
```

### Modal & Dialog

```tsx
import { useDisclosure } from '@mantine/hooks';

const [opened, { open, close }] = useDisclosure(false);

<Modal opened={opened} onClose={close} title={title} centered size="lg">
  <Stack>
    <Text c="dimmed">{message}</Text>
    <Group justify="flex-end" mt="md">
      <Button variant="default" onClick={close}>{t('actions.cancel')}</Button>
      <Button color="primary" onClick={handleConfirm}>{t('actions.confirm')}</Button>
    </Group>
  </Stack>
</Modal>
```

**Modal sizes:** `xs`, `sm`, `md`, `lg`, `xl`, `4xl`

**ConfirmDialog pattern** (project component):
```tsx
<ConfirmDialog
  isOpen={isOpen}
  onClose={close}
  onConfirm={handleDelete}
  title={t('confirm.delete.title')}
  message={t('confirm.delete.message')}
  variant="danger"
  isLoading={isPending}
/>
```

### Display

| Component | Purpose | Key Props |
|-----------|---------|-----------|
| `Text` | Body text | `size`, `c` (color), `fw` (weight), `lh` (line-height) |
| `Title` | Headings | `order` (1-6), `mb`, `mt` |
| `Badge` | Status labels | `color`, `variant` |
| `Alert` | Messages | `icon`, `title`, `color` |
| `Skeleton` | Loading placeholder | `height`, `width`, `circle` |
| `Progress` | Progress bar | `value`, `color`, `size` |

```tsx
// Color conventions
<Text c="dimmed">Subtle text</Text>
<Text c="red">Error text</Text>
<Text fw={500}>Semi-bold label</Text>

// Role/status badges
const roleBadgeColors = { ADMIN: 'grape', ORGANIZER: 'blue', MEMBER: 'gray' };
<Badge color={roleBadgeColors[role]}>{t(`roles.${role}`)}</Badge>
```

## CSS Variables

Use Mantine CSS vars for dark mode support:

```tsx
<Box bg="var(--mantine-color-body)">
<Paper style={{ borderColor: 'var(--mantine-color-default-border)' }}>
<Text c="var(--mantine-color-dimmed)">
<Box style={{ boxShadow: 'var(--mantine-shadow-sm)' }}>
```

Common vars: `--mantine-color-body`, `--mantine-color-default`, `--mantine-color-default-border`, `--mantine-color-dimmed`, `--mantine-color-anchor`, `--mantine-radius-md`, `--mantine-shadow-sm`

## Responsive Patterns

```tsx
// Responsive grid
<SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="md">

// Responsive props
<Box p={{ base: 'xs', sm: 'md', lg: 'xl' }} display={{ base: 'none', md: 'block' }}>

// useResponsive hook (project)
const { sizeCompact, isMobile } = useResponsive();
<Button size={sizeCompact}>{text}</Button>

// Responsive typography (in theme)
fontSize: 'clamp(1.5rem, 5vw, 2.125rem)'
```

**Breakpoints:** `xs` (36em), `sm` (48em), `md` (62em), `lg` (75em), `xl` (88em)

## i18n Integration

```tsx
const { t } = useTranslation();

// Direct keys
<Button>{t('actions.save')}</Button>
<Text>{t('pagination.page', { current: 1, total: 10 })}</Text>

// Templated keys with type safety
t(`status.${status satisfies 'DRAFT' | 'PUBLISHED'}`)
t(`roles.${role satisfies 'ADMIN' | 'ORGANIZER' | 'MEMBER'}`)
```

## Rich Text (Tiptap)

```tsx
import { RichTextEditor } from '@mantine/tiptap';

<RichTextEditor editor={editor}>
  <RichTextEditor.Toolbar sticky stickyOffset={0}>
    <RichTextEditor.ControlsGroup>
      <RichTextEditor.Bold />
      <RichTextEditor.Italic />
    </RichTextEditor.ControlsGroup>
  </RichTextEditor.Toolbar>
  <RichTextEditor.Content />
</RichTextEditor>
```

## Icons

**Always use @tabler/icons-react, never SVG:**

```tsx
import { IconPlus, IconTrash, IconSettings } from '@tabler/icons-react';

<Button leftSection={<IconPlus size={16} />}>{t('actions.add')}</Button>
<ActionIcon aria-label="Delete"><IconTrash size={16} /></ActionIcon>
```

## Common Mistakes

### 1. Missing MantineProvider
All components need `MantineProvider` at root.

### 2. ActionIcon without aria-label
```tsx
// ❌ BAD
<ActionIcon><IconTrash /></ActionIcon>
// ✅ GOOD
<ActionIcon aria-label="Delete"><IconTrash /></ActionIcon>
```

### 3. Nested interactive elements
```tsx
// ❌ BAD - button inside button
<Button><ActionIcon /></Button>
// ✅ GOOD - use Menu or separate
<Group><Button /><ActionIcon /></Group>
```

### 4. Select vs Autocomplete
- `Select`: Restricts to provided options
- `Autocomplete`: Allows free-form input with suggestions

### 5. Controlled/Uncontrolled mixing
```tsx
// ❌ BAD
<TextInput defaultValue="x" value={val} onChange={...} />
// ✅ GOOD - pick one
<TextInput value={val} onChange={...} />
```

### 6. Hard-coded text
Always use i18n:
```tsx
// ❌ BAD
<Button>Save</Button>
// ✅ GOOD
<Button>{t('actions.save')}</Button>
```

### 7. Hard-coded routes
Use paths from config:
```tsx
// ❌ BAD
<Link to={`/teams/${slug}/rides`}>
// ✅ GOOD
<Link to={paths.teamRides(slug)}>
```

## Quick Reference

```tsx
import {
  MantineProvider, createTheme,
  Button, ActionIcon, TextInput, Select, NumberInput,
  Modal, Menu, Tooltip, Collapse,
  Stack, Group, Paper, Box, SimpleGrid, Center,
  Text, Title, Badge, Alert, Skeleton, Progress,
  Table, Tabs,
} from '@mantine/core';
import { useDisclosure, useMediaQuery } from '@mantine/hooks';
import { useForm } from '@mantine/form';
import { DateTimePicker } from '@mantine/dates';
import { Notifications } from '@mantine/notifications';
import '@mantine/core/styles.css';
```
