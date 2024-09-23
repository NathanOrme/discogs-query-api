// src/modules/QueryFields.js

import React, { useState, useEffect } from 'react';
import { discogsTypes, discogFormats, discogCountries } from './DiscogsData';

const QueryFields = ({ onQueriesChange }) => {
  const [queries, setQueries] = useState([{ id: 1 }]); // Initial state with one query
  const [queryCounter, setQueryCounter] = useState(1);

  useEffect(() => {
    onQueriesChange(queries);
  }, [queries, onQueriesChange]);

  const addQuery = () => {
    const newQuery = { id: queryCounter + 1 };
    setQueries([...queries, newQuery]);
    setQueryCounter(queryCounter + 1);
    console.log("Added new query:", newQuery);
  };

  const removeQuery = (id) => {
    const updatedQueries = queries.filter(query => query.id !== id);
    setQueries(updatedQueries);
    console.log("Removed query with id:", id, "Remaining queries:", updatedQueries);
  };

  return (
    <form>
      {queries.map(query => (
        <div className="query" key={query.id}>
          <div className="query-header">
            <span>Query {query.id}</span>
            {queries.length > 1 && (
              <button type="button" className="delete-button" onClick={() => removeQuery(query.id)}>
                Remove
              </button>
            )}
          </div>
          <div className="query-content">
            <label htmlFor={`artist-${query.id}`}>Artist:</label>
            <input type="text" className="artist" name={`artist-${query.id}`} />

            <label htmlFor={`barcode-${query.id}`}>Barcode:</label>
            <input type="text" className="barcode" name={`barcode-${query.id}`} />

            <label htmlFor={`album-${query.id}`}>Album (optional):</label>
            <input type="text" className="album" name={`album-${query.id}`} />

            <label htmlFor={`track-${query.id}`}>Track (optional):</label>
            <input type="text" className="track" name={`track-${query.id}`} />

            <label htmlFor={`format-${query.id}`}>Format (optional):</label>
            <select className="format" name={`format-${query.id}`}>
              {discogFormats.map(format => (
                <option key={format.value} value={format.value}>{format.text}</option>
              ))}
            </select>

            <label htmlFor={`country-${query.id}`}>Country (optional):</label>
            <select className="country" name={`country-${query.id}`}>
              {discogCountries.map(country => (
                <option key={country.value} value={country.value}>{country.text}</option>
              ))}
            </select>

            <label htmlFor={`types-${query.id}`}>Types (optional):</label>
            <select className="types" name={`types-${query.id}`}>
              {discogsTypes.map(type => (
                <option key={type.value} value={type.value}>{type.text}</option>
              ))}
            </select>
          </div>
        </div>
      ))}
      <button type="button" onClick={addQuery}>Add Query</button>
    </form>
  );
};

export default QueryFields;
