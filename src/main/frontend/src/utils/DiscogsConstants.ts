/**
 * DiscogsConstants.ts
 *
 * Centralized constants for Discogs API integration.
 * Contains filter options (types and formats) for use in filtering
 * Discogs API queries, particularly within the Discogs Query App.
 */

/**
 * Interface representing a filter option.
 *
 * @interface FilterOption
 * @property {string} value - The value used in filtering.
 * @property {string} text - The display text for the option.
 */
export interface FilterOption {
  value: string;
  text: string;
}

/**
 * An array of Discogs types for filtering items.
 *
 * @constant {FilterOption[]}
 */
export const discogsTypes: FilterOption[] = [
  { value: 'RELEASE', text: 'Release' },
  { value: 'MASTER', text: 'Master' },
  { value: '', text: 'Select a type' },
];

/**
 * An array of Discogs formats for filtering items.
 *
 * @constant {FilterOption[]}
 */
export const discogFormats: FilterOption[] = [
  { value: '', text: 'Any Format' },
  { value: 'vinyl', text: 'Vinyl' },
  { value: 'album', text: 'Album' },
  { value: 'cd', text: 'CD' },
  { value: 'lp', text: 'LP' },
  { value: 'compilation', text: 'Compilation' },
  { value: 'album vinyl', text: 'Album Vinyl' },
  { value: 'compilation vinyl', text: 'Compilation Vinyl' },
  { value: 'all vinyls', text: 'All Vinyl Options' },
];
