/**
 * @module DiscogUtils
 * @description
 * Contains utility types and functions for handling Discogs query results,
 * especially focusing on album data. This module provides functionality for
 * processing Discogs API responses, computing statistics, and exporting data.
 */

/**
 * Represents a single Discogs marketplace entry with its details.
 * @interface EntryType
 * @property {number|string} [id] - The unique identifier for the entry
 * @property {string} [title] - The title of the release
 * @property {string[]} [format] - Array of formats (e.g., ["Vinyl", "LP", "Album"])
 * @property {string} [country] - The country of release
 * @property {number|string} [year] - The release year
 * @property {string} [uri] - The Discogs URI for this entry
 * @property {number} [numberForSale] - Number of copies available for sale
 * @property {number|null} [lowestPrice] - The lowest available price, or null if not available
 */
export interface EntryType {
  id?: number | string;
  title?: string;
  format?: string[];
  country?: string;
  year?: number | string;
  uri?: string;
  numberForSale?: number;
  lowestPrice?: number | null;
}

/**
 * Represents the result of a Discogs query, grouping entries by their title.
 * @interface QueryResult
 * @property {Object.<string, EntryType[]>} results - An object where keys are album titles
 * and values are arrays of matching EntryType objects
 */
export interface QueryResult {
  results: { [title: string]: EntryType[] };
}

// External cache for URL shortening to avoid self-reference
const urlCache: Record<string, string> = {};

// Export cache clearing function for testing
export const clearUrlCache = (): void => {
  Object.keys(urlCache).forEach((key) => delete urlCache[key]);
};

/**
 * Shortens a URL using the TinyURL API with basic in-memory caching.
 *
 * @function shortenUrl
 * @async
 * @param {string} longUrl - The URL to be shortened
 * @returns {Promise<string>} A promise that resolves to the shortened URL
 * @throws {Error} If the URL cannot be shortened
 * @example
 * const shortUrl = await shortenUrl('https://example.com/very/long/url');
 * logger.log(shortUrl); // Outputs: 'https://tinyurl.com/abc123'
 */
export async function shortenUrl(longUrl: string): Promise<string> {
  const cacheKey = longUrl;
  // Check cache first
  if (urlCache[cacheKey]) {
    return urlCache[cacheKey];
  }

  const apiUrl = `https://tinyurl.com/api-create.php?url=${encodeURIComponent(longUrl)}`;
  const response = await fetch(apiUrl);

  if (!response.ok) {
    throw new Error('Failed to shorten URL');
  }

  const shortUrl = await response.text();

  // Store in cache
  urlCache[cacheKey] = shortUrl;

  return shortUrl;
}

/**
 * Finds and returns the cheapest entry from a collection of query results.
 *
 * @function computeCheapestItem
 * @param {Object.<string, EntryType[]>} results - An object where keys are album titles
 * and values are arrays of EntryType objects
 * @returns {EntryType | null} The entry with the lowest price, or null if no entries with prices exist
 * @example
 * const results = {
 *   'Album1': [
 *     { title: 'Album1', lowestPrice: 10.99 },
 *     { title: 'Album1', lowestPrice: 12.99 }
 *   ],
 *   'Album2': [
 *     { title: 'Album2', lowestPrice: 8.99 }
 *   ]
 * };
 * const cheapest = computeCheapestItem(results);
 * logger.log(cheapest); // Outputs: { title: 'Album2', lowestPrice: 8.99 }
 */
export function computeCheapestItem(results: {
  [title: string]: EntryType[];
}): EntryType | null {
  let cheapest: EntryType | null = null;

  for (const entries of Object.values(results)) {
    for (const entry of entries) {
      if (
        entry.lowestPrice != null &&
        (cheapest == null ||
          entry.lowestPrice < (cheapest.lowestPrice ?? Infinity))
      ) {
        cheapest = entry;
      }
    }
  }

  return cheapest;
}

/**
 * Exports data as a JSON file and triggers a download in the browser.
 *
 * @function exportToJson
 * @param {unknown} data - The data to be exported as JSON
 * @param {string} filename - The base name for the downloaded file (without extension)
 * @returns {void}
 * @example
 * const data = { key: 'value' };
 * exportToJson(data, 'my-data'); // Downloads 'my-data.json'
 */
export default function exportToJson(
  data: unknown,
  filename: string,
): void {
  const fileData = JSON.stringify(data, null, 2);
  const blob = new Blob([fileData], { type: 'application/json' });
  const url = URL.createObjectURL(blob);

  const link = document.createElement('a');
  link.download = `${filename}.json`;
  link.href = url;

  // Trigger the download
  document.body.appendChild(link);
  link.click();

  // Clean up
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
