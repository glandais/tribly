/**
 * Swaps an item at the given index with an adjacent item in the specified direction.
 * Returns a new array with the items swapped, or the original array if swap is not possible.
 *
 * @param items - The array to modify
 * @param index - The index of the item to move
 * @param direction - 'up' moves toward index 0, 'down' moves toward end
 * @returns A new array with items swapped, or the original array if at boundary
 */
export function moveItem<T>(items: T[], index: number, direction: 'up' | 'down'): T[] {
  const newIndex = direction === 'up' ? index - 1 : index + 1

  if (newIndex < 0 || newIndex >= items.length) {
    return items
  }

  const newItems = [...items]
  const temp = newItems[index]
  newItems[index] = newItems[newIndex]
  newItems[newIndex] = temp

  return newItems
}
