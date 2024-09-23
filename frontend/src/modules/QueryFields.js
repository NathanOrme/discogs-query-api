// src/modules/QueryFields.js

import React, { useState } from 'react';
import { discogsTypes, discogFormats, discogCountries } from './DiscogsData';

const QueryFields = () => {
  const [queries, setQueries] = useState([{ id: 1 }]); // Initial state with one query
  const [queryCounter, setQueryCounter] = useState(1);

  const addQuery = () => {
    setQueries([...queries, { id: queryCounter + 1 }]);
    setQueryCounter(queryCounter + 1);
  };

  const removeQuery = (id) => {
    setQueries(queries.filter(query => query.id !== id));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    for (const query of queries) {
      const artistInput = e.target[`artist-${query.id}`].value.trim();
      const barcodeInput = e.target[`barcode-${query.id}`].value.trim();
      if (!artistInput && !barcodeInput) {
        alert("Please provide either an artist name or a barcode.");
        return;
      }
    }
    // Submit logic here (e.g., API call)
  };

  return (
    <form onSubmit={handleSubmit}>
      {queries.map(query => (
        <div className="query" key={query.id}>
          <div className="query-header">
            <span>Query {query.id}</span>
            <button type="button" className="delete-button" onClick={() => removeQuery(query.id)}>
              {/* SVG for delete button */}
            </button>
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
      <button type="submit">Submit</button>
    </form>
  );
};

export default QueryFields;
