// src/components/QueryFields.tsx

import type { QueryFieldsProps } from '@/utils/DiscogsTypes';
import { Plus } from 'lucide-react';
import { useCallback, useEffect, useRef, useState } from 'react';

import type { Query } from '@/types/DiscogsTypes';
import type { FC } from 'react';

import QueryItem from './QueryItem';

const QueryFields: FC<QueryFieldsProps> = ({ onQueriesChange }) => {
  const nextIdRef = useRef<number>(1);

  const [queries, setQueries] = useState<Query[]>(() => [
    {
      id: 0,
      artist: '',
      barcode: '',
      album: '',
      track: '',
      format: '',
      types: '',
    },
  ]);

  useEffect(() => {
    onQueriesChange(queries);
  }, [queries, onQueriesChange]);

  const addQuery = useCallback(() => {
    setQueries((prev) => [
      ...prev,
      {
        id: nextIdRef.current++,
        artist: '',
        barcode: '',
        album: '',
        track: '',
        format: '',
        types: '',
      },
    ]);
  }, []);

  const removeQuery = useCallback((index: number) => {
    setQueries((prev) => prev.filter((_, i) => i !== index));
  }, []);

  const handleInputChange = useCallback(
    (index: number, field: keyof Query, value: string) => {
      setQueries((prev) =>
        prev.map((q, i) => (i === index ? { ...q, [field]: value } : q)),
      );
    },
    [],
  );

  return (
    <section aria-label="Search Queries" className="space-y-3">
      {queries.map((query, index) => (
        <QueryItem
          key={query.id}
          query={query}
          index={index}
          onInputChange={handleInputChange}
          removeQuery={removeQuery}
        />
      ))}

      <button
        onClick={addQuery}
        className="w-full flex items-center justify-center gap-2 h-8 rounded-lg text-xs font-medium border transition-all duration-150"
        style={{
          borderColor: 'rgba(230,81,0,0.3)',
          color: 'rgba(230,81,0,0.7)',
          background: 'rgba(230,81,0,0.05)',
        }}
        onMouseEnter={(e) => {
          (e.currentTarget as HTMLElement).style.borderColor = 'rgba(230,81,0,0.6)';
          (e.currentTarget as HTMLElement).style.color = '#e65100';
          (e.currentTarget as HTMLElement).style.background = 'rgba(230,81,0,0.1)';
        }}
        onMouseLeave={(e) => {
          (e.currentTarget as HTMLElement).style.borderColor = 'rgba(230,81,0,0.3)';
          (e.currentTarget as HTMLElement).style.color = 'rgba(230,81,0,0.7)';
          (e.currentTarget as HTMLElement).style.background = 'rgba(230,81,0,0.05)';
        }}
        aria-label="Add another search query"
      >
        <Plus className="w-3.5 h-3.5" />
        Add Query
      </button>
    </section>
  );
};

export default QueryFields;
