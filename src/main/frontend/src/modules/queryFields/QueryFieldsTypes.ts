export interface Query {
  artist: string;
  barcode: string;
  album: string;
  track: string;
  format: string;
  types: string;
}

export interface QueryFieldsProps {
  onQueriesChange: (queries: Query[]) => void;
}