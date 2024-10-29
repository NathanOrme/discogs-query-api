// src/modules/discogsData.tsx

/**
 * Interface for filter options with value and text properties.
 */
interface FilterOption {
  value: string;
  text: string;
}

/**
 * An array of Discogs types for filtering items.
 * @type {FilterOption[]}
 */
const discogsTypes: FilterOption[] = [
  { value: "RELEASE", text: "Release" },
  { value: "MASTER", text: "Master" },
  { value: "", text: "Select a type" },
];

/**
 * An array of Discogs formats for filtering items.
 * @type {FilterOption[]}
 */
const discogFormats: FilterOption[] = [
  { value: "", text: "Any Format" },
  { value: "vinyl", text: "Vinyl" },
  { value: "album", text: "Album" },
  { value: "cd", text: "CD" },
  { value: "lp", text: "LP" },
  { value: "compilation", text: "Compilation" },
  { value: "album vinyl", text: "Album Vinyl" },
  { value: "compilation vinyl", text: "Compilation Vinyl" },
  { value: "all vinyls", text: "All Vinyl Options" },
];

export { discogsTypes, discogFormats };
