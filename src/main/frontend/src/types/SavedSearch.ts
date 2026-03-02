/**
 * @file SavedSearch.ts
 * @description TypeScript type definitions for Discogs saved searches feature
 */

import type { ComponentType } from 'react';
import type { Query } from '@/types/DiscogsTypes';

/**
 * Discogs query parameters
 */
export interface DiscogsQueryParams extends Query {
  artist?: string;
  barcode?: string;
  album?: string;
  track?: string;
  format?: string;
  types?: string;
  genre?: string;
  year?: string;
  country?: string;
  label?: string;
  style?: string;
  decade?: string;
}

/**
 * Saved search configuration
 */
export interface SavedSearch {
  id: string;
  name: string;
  description?: string;
  query: DiscogsQueryParams;
  type: 'query-only' | 'query-with-results';
  results?: unknown[];
  resultCount?: number;
  savedAt?: Date;
  createdAt: Date;
  lastUsedAt?: Date;
  tags?: string[];
  color?: string;
}

export type SavedSearchInput = Omit<SavedSearch, 'id' | 'createdAt'>;

export type SavedSearchUpdate = Partial<
  Omit<SavedSearch, 'id' | 'createdAt' | 'query'>
> & {
  query?: DiscogsQueryParams;
};

export interface PresetSearch {
  id: string;
  title: string;
  description: string;
  icon: ComponentType<{ className?: string }>;
  query: DiscogsQueryParams;
  gradient: string;
  category?: 'genre' | 'format' | 'era' | 'special';
}

export interface SavedSearchesState {
  searches: SavedSearch[];
  activeSearchId?: string;
  isOpen: boolean;
  loading: boolean;
  error: string | null;
}

export interface SavedSearchesActions {
  saveSearch: (search: SavedSearchInput) => Promise<SavedSearch>;
  loadSearch: (id: string) => Promise<SavedSearch | null>;
  updateSearch: (id: string, updates: SavedSearchUpdate) => Promise<SavedSearch | null>;
  deleteSearch: (id: string) => Promise<void>;
  getAllSearches: () => Promise<SavedSearch[]>;
  exportSearches: () => Promise<string>;
  importSearches: (json: string) => Promise<void>;
  clearAll: () => Promise<void>;
}

export interface SavedSearchFilter {
  tag?: string;
  searchTerm?: string;
  sortBy?: 'name' | 'createdAt' | 'lastUsedAt' | 'alphabetical';
  sortDirection?: 'asc' | 'desc';
}

export interface SavedSearchesStats {
  total: number;
  recentlyUsed: number;
  mostUsed?: SavedSearch;
  allTags: string[];
  storageSize: number;
}
