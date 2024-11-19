// src/modules/QueryFields.tsx

import React, { useState, useEffect } from 'react';
import { Query, QueryFieldsProps } from './queryFields/QueryFieldsTypes';
import { renderQueryFields } from './queryFields/QueryFieldsRenderer';

/**
 * QueryFields component allows users to input multiple queries for searching Discogs.
 *
 * @param {QueryFieldsProps} props - The props for the component.
 * @returns {JSX.Element} The rendered QueryFields component.
 */
const QueryFields: React.FC<QueryFieldsProps> = ({ onQueriesChange }) => {
  const [queries, setQueries] = useState<Query[]>([
    { artist: '', barcode: '', album: '', track: '', format: '', types: '' },
  ]);

  const [queryCounter, setQueryCounter] = useState<number>(1);

  useEffect(() => {
    onQueriesChange(queries);
  }, [queries, onQueriesChange]);

  const handleInputChange = (
    index: number,
    field: keyof Query,
    value: string
  ) => {
    const updatedQueries = queries.map((query, i) =>
      i === index ? { ...query, [field]: value } : query
    );
    setQueries(updatedQueries);
  };

  const addQuery = () => {
    const newQuery: Query = {
      artist: '',
      barcode: '',
      album: '',
      track: '',
      format: '',
      types: '',
    };
    setQueries([...queries, newQuery]);
    setQueryCounter(queryCounter + 1);
  };

  const removeQuery = (index: number) => {
    const updatedQueries = queries.filter((_, i) => i !== index);
    setQueries(updatedQueries);
  };

  return (
    <form>
      {queries.map((query, index) =>
        renderQueryFields(
          query,
          index,
          handleInputChange,
          removeQuery,
          queries.length
        )
      )}
      <button type="button" onClick={addQuery}>
        Add Another Query
      </button>
    </form>
  );
};

export default QueryFields;
