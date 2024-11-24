// src/types.ts

/**
 * Type definition for a single query object.
 */
export interface Query {
  id?: number;
  artist?: string;
  barcode?: string;
  album?: string;
  track?: string;
  format?: string;
  country?: string;
  types?: string;
}

/**
 * Unified type for a single entry in the results.
 */
export interface Entry {
  id?: string;
  title?: string;
  format?: string[];
  country?: string;
  year?: string;
  uri?: string;
  numberForSale?: number;
  lowestPrice?: number | null;
}

/**
 * Type definition for the query results.
 */
export interface QueryResult {
  results: Record<string, Entry[]>;
}

/**
 * Props for the Results component.
 */
export interface ResultsProps {
  response: QueryResult[];
}
