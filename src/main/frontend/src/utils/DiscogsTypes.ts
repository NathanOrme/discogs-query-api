import type { EntryType, QueryResult } from '@/utils/DiscogUtils';
import type { Query } from '@/types/DiscogsTypes';
import type { Dispatch, SetStateAction } from 'react';

export interface ErrorSnackbarProps {
  error: string | null;
  setError: Dispatch<SetStateAction<string | null>>;
}

export interface MainContentProps {
  loading: boolean;
  response: QueryResult[];
}

export interface ResultsProps {
  response: QueryResult[];
}

export interface QueryResultsProps {
  queryResult: QueryResult;
  index: number;
}

export interface EntryProps {
  entry: EntryType;
}

export interface CheapestItemProps {
  item: EntryType;
}

export interface SidebarProps {
  queriesExpanded: boolean;
  setQueriesExpanded: Dispatch<SetStateAction<boolean>>;
  username: string;
  setUsername: Dispatch<SetStateAction<string>>;
  queries: Query[];
  handleQueriesChange: (newQueries: Query[]) => void;
  handleExportResults: () => void;
  handleReset: () => void;
  response: QueryResult[];
  loading: boolean;
  setResponse: Dispatch<SetStateAction<QueryResult[]>>;
  setLoading: Dispatch<SetStateAction<boolean>>;
  setError: Dispatch<SetStateAction<string | null>>;
}

export interface UsernameFieldProps {
  username: string;
  setUsername: Dispatch<SetStateAction<string>>;
}

export interface QueryFieldsProps {
  onQueriesChange: (queries: Query[]) => void;
}

export interface QueryItemProps {
  query: Query;
  index: number;
  onInputChange: (index: number, field: keyof Query, value: string) => void;
  removeQuery: (index: number) => void;
}

export interface SearchButtonProps {
  queries: Query[];
  username?: string;
  setResponse: (response: QueryResult[]) => void;
  loading: boolean;
  onLoadingChange: (loading: boolean) => void;
  onError: (error: string) => void;
}

export interface ExportButtonProps {
  handleExportResults: () => void;
}

export interface ResetButtonProps {
  handleReset: () => void;
}
